package com.seikosantana.githubusers

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

class MainApplication : Application() {
    lateinit var queue: RequestQueue
    lateinit var context: Context

    private val dataStore: DataStore<Preferences> by preferencesDataStore("settings")
    private val SETTINGS_THEME_OVERRIDE = booleanPreferencesKey("override_theme_setting")
    private val SETTINGS_THEME_DARK = booleanPreferencesKey("dark_theme_setting")

    fun getOverrideThemeSettings(): Flow<Boolean> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                Log.d("DataStoreRepository", exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
            .map { prefs ->
                prefs[SETTINGS_THEME_OVERRIDE] ?: false
            }
    }

    suspend fun setOverrideThemeSettings(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[SETTINGS_THEME_OVERRIDE] = value
        }
    }

    fun getDarkThemeSettings(): Flow<Boolean> {
        return dataStore.data.catch { exception ->
            Log.d("DataStoreRepository", exception.message.toString())
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
            .map { prefs ->
                prefs[SETTINGS_THEME_DARK] ?: false
            }
    }

    suspend fun setDarkThemeSettings(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[SETTINGS_THEME_DARK] = value
        }
    }

    override fun onCreate() {
        super.onCreate()
        queue = Volley.newRequestQueue(this)
        UserHelper.apply {
            requestQueue = queue
            context = this@MainApplication
        }
        monitorTheme()
    }

    var override = false
    var dark = false

    fun notifyThemeChanges() = GlobalScope.launch(Dispatchers.Main) {
        Log.d("Theme", "Monitor requested")
        if (!override) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        else {
            if (dark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    fun monitorTheme() {
        GlobalScope.launch {
            getOverrideThemeSettings().collect {
                override = it
                notifyThemeChanges()
            }
        }
        GlobalScope.launch {
            getDarkThemeSettings().collect {
                dark = it
                notifyThemeChanges()
            }
        }
    }

    fun resetOverrideTheme() = GlobalScope.launch {
        var override = false
        var dark = false
        setOverrideThemeSettings(override)
        setDarkThemeSettings(dark)
    }

}
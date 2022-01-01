package com.seikosantana.githubusers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    lateinit var switchOverride: SwitchMaterial
    lateinit var switchDarkTheme: SwitchMaterial
    lateinit var lblOverride: TextView
    lateinit var lblDarkTheme: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.title = "Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        findViews()
        initEvents()
    }

    private fun findViews() {
        switchDarkTheme = findViewById(R.id.switchDarkTheme)
        switchOverride = findViewById(R.id.switchOverride)
        lblOverride = findViewById(R.id.lblOverride)
        lblDarkTheme = findViewById(R.id.lblDarkTheme)
    }

    private fun setOverrideDarkThemeState(value: Boolean) {
        if (value) {
            lblDarkTheme.visibility = View.VISIBLE
            switchDarkTheme.visibility = View.VISIBLE
        }
        else {
            lblDarkTheme.visibility = View.INVISIBLE
            switchDarkTheme.visibility = View.INVISIBLE
        }
    }

    private fun initEvents() {
        val mainApplication: MainApplication = this.application as MainApplication
        switchOverride.setOnCheckedChangeListener { _, value ->
            run {
                setOverrideDarkThemeState(value)
                GlobalScope.launch(Dispatchers.IO) {
                    mainApplication.setOverrideThemeSettings(value)
                }
            }
        }
        switchDarkTheme.setOnCheckedChangeListener { _, value ->
            run {
                GlobalScope.launch(Dispatchers.IO) {
                    mainApplication.setDarkThemeSettings(value)
                }
            }
        }
        switchOverride.isChecked = mainApplication.override
        switchDarkTheme.isChecked = mainApplication.dark
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
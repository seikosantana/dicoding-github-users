package com.seikosantana.githubusers

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.seikosantana.githubusers.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivityModel : ViewModel() {
    var users: ArrayList<UserMain> = arrayListOf()
    private val baseUrl = "https://api.github.com"

    fun searchUsers(phrase: String, searchFinishedCallback: (ArrayList<UserMain>) -> Unit, failureCallback: () -> Unit) {
        UserHelper.getUsers(phrase, {
            searchFinishedCallback(it)
        }, failureCallback)
    }

}
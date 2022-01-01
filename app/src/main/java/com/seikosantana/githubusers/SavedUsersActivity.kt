package com.seikosantana.githubusers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.asFlow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import database.SavedUser
import database.SavedUsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavedUsersActivity : AppCompatActivity() {

    private lateinit var mSavedUsersRepository: SavedUsersRepository
    private lateinit var savedUsersList: List<SavedUser>
    private lateinit var rvSavedUser: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var noFavText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_users)
        mSavedUsersRepository = SavedUsersRepository(application)

        rvSavedUser = findViewById(R.id.rvSavedUsers)
        rvSavedUser.layoutManager = LinearLayoutManager(this)
        rvSavedUser.adapter = UserListAdapter(arrayListOf())

        progressBar = findViewById(R.id.savedUsersLoadingIndicator)
        noFavText = findViewById(R.id.savedUsersNoFav)

        supportActionBar?.title = "Favourite Users"

        observeData()
    }

    fun observeData() {
        GlobalScope.launch {
            mSavedUsersRepository.getAllSavedUsers().asFlow()
                .collect {
                    savedUsersList = it
                    notifySavedUserChanged()
                }
        }
    }

    fun notifySavedUserChanged() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                noFavText.visibility = View.INVISIBLE
                if (savedUsersList.isNotEmpty()) {
                    progressBar.visibility = View.VISIBLE
                    UserHelper.getSavedUserList(savedUsersList, {
                        val usersResult = it
                        var adapter = UserListAdapter(usersResult)
                        progressBar.visibility = View.INVISIBLE
                        rvSavedUser.visibility = View.VISIBLE
                        rvSavedUser.adapter = adapter
                    }, {
                        Toast.makeText(applicationContext, "Unable to load saved Github Users", Toast.LENGTH_SHORT).show()
                    })
                }
                else {
                    noFavText.visibility = View.VISIBLE
                    rvSavedUser.visibility = View.INVISIBLE
                }
            }
        }
    }


}
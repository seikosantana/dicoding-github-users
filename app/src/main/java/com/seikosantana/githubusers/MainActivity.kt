package com.seikosantana.githubusers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.seikosantana.githubusers.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var activityMainBinding: ActivityMainBinding
    val viewModel: MainActivityModel by viewModels()
    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        queue = Volley.newRequestQueue(this)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        val searchView = findViewById<SearchView>(R.id.searchViewSearchUser)
        searchView.isIconified = false
        searchView.isIconifiedByDefault = false
        searchView.queryHint = "Search user"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                toggleLoading(true)
                if (query != null && query.trim() != "") {
                    viewModel.searchUsers(
                        query, {
                            activityMainBinding.rvUserList.swapAdapter(UserListAdapter(it), true)
                            toggleLoading(false)
                        },
                        {
                            Toast.makeText(
                                this@MainActivity,
                                "An error occurred",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            toggleLoading(false)
                        }
                    )
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })

        val usersRecyclerView = findViewById<RecyclerView>(R.id.rvUserList)

        usersRecyclerView.layoutManager = LinearLayoutManager(this)
        usersRecyclerView.adapter = UserListAdapter(arrayListOf())
    }

    fun toggleLoading(isLoading: Boolean) {
        activityMainBinding.rvUserList.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        activityMainBinding.progressSearch.visibility =
            if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        else if (item.itemId == R.id.itemSettings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        else if (item.itemId == R.id.itemFavourites) {
            val intent = Intent(this, SavedUsersActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

}
package com.seikosantana.githubusers

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mikhaellopez.circularimageview.CircularImageView
import database.SavedUser
import database.SavedUsersRepository

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvCompany: TextView
    private lateinit var tvLocation: TextView
    private lateinit var imgLocationIcon: ImageView
    private lateinit var imgAvatar: CircularImageView
    private lateinit var btnShare: ImageButton
    private lateinit var progressUserDetail: ProgressBar

    private lateinit var clContent: ConstraintLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var btnToggleFavourite: ImageButton
    private lateinit var userDetail: UserDetail
    private var isFavourite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)
        supportActionBar?.title = "User Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        findViews()
        fillData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getUri(url: String): Uri {
        return Uri.parse(url)
    }

    private fun findViews() {
        tvUsername = findViewById(R.id.tvUserDetailUsername)
        tvName = findViewById(R.id.tvUserDetailName)
        tvCompany = findViewById(R.id.tvUserDetailCompany)
        tvLocation = findViewById(R.id.tvLocation)
        imgLocationIcon = findViewById(R.id.iconLocation)
        imgAvatar = findViewById(R.id.imgUserDetailAvatar)
        btnShare = findViewById(R.id.tvUserDetailShare)
        progressUserDetail = findViewById(R.id.progressUserDetail)
//        svUserDetailContent = findViewById(R.id.svUserDetailContent)
        clContent = findViewById(R.id.clContent)
        viewPager = findViewById(R.id.vpDetailsPager)
        tabLayout = findViewById(R.id.tabLayout)
        btnToggleFavourite = findViewById(R.id.btnUserDetailToggleFavourite)
    }

    private fun triggerFavouriteIconChange() {
        if (isFavourite) {
            btnToggleFavourite.setImageResource(R.drawable.ic_fav_on)
        }
        else {
            btnToggleFavourite.setImageResource(R.drawable.ic_fav_off)
        }
    }

    private fun fillData() {
        progressUserDetail.visibility = View.VISIBLE
//        svUserDetailContent.visibility = View.INVISIBLE
        clContent.visibility = View.INVISIBLE
        val username = intent.getStringExtra("username")!!
        UserHelper.getUserDetails(username, {
            userDetail = it
            val savedUserRepository = SavedUsersRepository(application)
            savedUserRepository.findIfUserIsInFavourite(userDetail.username) { isMarkedFavourite: Boolean ->
                run {
                    isFavourite = isMarkedFavourite
                    triggerFavouriteIconChange()
                }
            }
            tvLocation.text = userDetail.location
            imgLocationIcon.visibility = if (userDetail.location == "") View.INVISIBLE else View.VISIBLE
            it.apply {
                tvName.text = name
                tvUsername.text = username
                tvCompany.text = company
                Glide.with(applicationContext)
                    .asBitmap()
                    .load(it.avatarUrl)
                    .into(imgAvatar)

                val uri: Uri = getUri(it.avatarUrl)
                btnShare.setOnClickListener {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            getString(
                                R.string.share_placeholder,
                                name,
                                username,
                                company,
                                userDetail.url
                            )
                        )
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = "image/png"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Share this Github User")
                    startActivity(shareIntent)
                }
                btnToggleFavourite.setOnClickListener {
                    if (isFavourite) {
                        savedUserRepository.delete(SavedUser(userDetail.username, userDetail.avatarUrl))
                    }
                    else {
                        savedUserRepository.insert(SavedUser(userDetail.username, userDetail.avatarUrl))
                    }
                    isFavourite = !isFavourite
                    Toast.makeText(applicationContext, "${userDetail.username} has been ${if (isFavourite) "added to" else "removed from"} favourites", Toast.LENGTH_SHORT).show()
                    triggerFavouriteIconChange()
                }
                progressUserDetail.visibility = View.INVISIBLE
//                svUserDetailContent.visibility = View.VISIBLE
                clContent.visibility = View.VISIBLE
                viewPager.adapter = SectionsPagerAdapter(
                    this@UserDetailsActivity,
                    it.repositories,
                    it.followers,
                    it.following
                )
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    when (position) {
                        0 -> tab.text = "Repositories (${repositories.size})"
                        1 -> tab.text = "Followers (${followers.size})"
                        2 -> tab.text = "Following (${following.size})"
                    }
                }.attach()
            }
        },
            {
                Toast.makeText(this, "Unable to get user details.", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

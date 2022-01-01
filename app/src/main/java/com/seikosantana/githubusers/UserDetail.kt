package com.seikosantana.githubusers

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDetail(
    var username: String,
    var avatarUrl: String,
    var name: String,
    var company: String,
    var location: String,
    var repositories: ArrayList<Repository>,
    var followers: ArrayList<UserMain>,
    var following: ArrayList<UserMain>,
    var url: String
) : Parcelable
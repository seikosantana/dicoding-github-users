package com.seikosantana.githubusers

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserMain(var username: String,
                    var avatarUrl: String,
                    var repositoryCount: Int,
                    var followerCount: Int) : Parcelable
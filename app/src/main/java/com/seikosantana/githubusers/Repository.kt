package com.seikosantana.githubusers

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Repository(var name: String, var url: String) : Parcelable

package com.dicoding.githubuserapplication.dataclasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Users(
    var id: String = "",
    var avatarUrl: String = "",
    var htmlUrl: String = "",
    var followingUrl: String = "",
    var login: String = "",
    var followersUrl: String = "",
    var url: String = "",
) : Parcelable

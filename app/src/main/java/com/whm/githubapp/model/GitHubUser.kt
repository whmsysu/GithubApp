package com.whm.githubapp.model

import com.google.gson.annotations.SerializedName

data class GitHubUser(
    val login: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    val name: String? = null,
    val bio: String? = null,
    @SerializedName("public_repos") val publicRepos: Int? = null,
    val followers: Int? = null,
    val following: Int? = null
)
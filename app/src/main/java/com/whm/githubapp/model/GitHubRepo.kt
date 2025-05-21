package com.whm.githubapp.model

import com.google.gson.annotations.SerializedName

data class GitHubRepo(
    @SerializedName("name") val name: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("stargazers_count") val stars: Int,
    @SerializedName("owner") val owner: Owner,
    @SerializedName("language") val language: String?,
    @SerializedName("forks_count") val forks: Int?,
    @SerializedName("open_issues_count") val issues: Int?,
    @SerializedName("updated_at") val updatedAt: String?,
    @Transient var isStarred: Boolean = false
)

data class Owner(
    val login: String
)
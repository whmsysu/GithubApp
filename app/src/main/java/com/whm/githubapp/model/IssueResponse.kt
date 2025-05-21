package com.whm.githubapp.model

import com.google.gson.annotations.SerializedName

data class IssueResponse(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    @SerializedName("html_url") val url: String
)
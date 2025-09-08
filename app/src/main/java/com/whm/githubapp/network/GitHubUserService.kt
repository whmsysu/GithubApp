package com.whm.githubapp.network

import com.whm.githubapp.model.GitHubUser
import com.whm.githubapp.model.GitHubRepo
import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface GitHubUserService {

    @GET("user")
    suspend fun getUserInfo(): GitHubUser

    @GET("user/repos")
    suspend fun getUserRepos(): List<GitHubRepo>

    @PUT("user/starred/{owner}/{repo}")
    suspend fun starRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): retrofit2.Response<Unit>

    @DELETE("user/starred/{owner}/{repo}")
    suspend fun unstarRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): retrofit2.Response<Unit>

    @GET("user/starred/{owner}/{repo}")
    suspend fun checkIfStarred(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): retrofit2.Response<Void>

    companion object {}
}
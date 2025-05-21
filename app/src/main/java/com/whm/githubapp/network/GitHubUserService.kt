package com.whm.githubapp.network

import com.whm.githubapp.model.GitHubUser
import com.whm.githubapp.model.GitHubRepo
import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface GitHubUserService {

    @GET("user")
    suspend fun getUserInfo(@Header("Authorization") token: String): GitHubUser

    @GET("user/repos")
    suspend fun getUserRepos(@Header("Authorization") token: String): List<GitHubRepo>

    @PUT("user/starred/{owner}/{repo}")
    suspend fun starRepo(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): retrofit2.Response<Unit>

    @DELETE("user/starred/{owner}/{repo}")
    suspend fun unstarRepo(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): retrofit2.Response<Unit>

    @GET("user/starred/{owner}/{repo}")
    suspend fun checkIfStarred(
        @Header("Authorization") auth: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): retrofit2.Response<Void>

    companion object {
        fun create(): GitHubUserService {
            return Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GitHubUserService::class.java)
        }
    }
}
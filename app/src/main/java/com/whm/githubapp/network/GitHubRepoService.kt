package com.whm.githubapp.network

import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.model.GitHubRepoResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Path
import retrofit2.http.Body
import com.whm.githubapp.model.IssueResponse
import com.whm.githubapp.model.CreateIssueRequest

interface GitHubRepoService {

    @POST("repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body issue: CreateIssueRequest
    ): IssueResponse


    @GET("search/repositories")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null
    ): GitHubRepoResponse

    @GET("search/repositories")
    suspend fun getTrendingRepos(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null
    ): GitHubRepoResponse

    @GET("repos/{owner}/{repo}")
    suspend fun getRepoDetailAuth(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): GitHubRepo

    companion object {}
}
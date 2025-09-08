package com.whm.githubapp.repository

import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.model.GitHubUser
import com.whm.githubapp.network.GitHubUserService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val service: GitHubUserService
) {
    suspend fun getUserInfo(): GitHubUser = service.getUserInfo()

    suspend fun getUserRepos(): List<GitHubRepo> = service.getUserRepos()

    suspend fun checkIfStarred(owner: String, repo: String): Response<Void> = service.checkIfStarred(owner, repo)

    suspend fun starRepo(owner: String, repo: String): Response<Unit> = service.starRepo(owner, repo)

    suspend fun unstarRepo(owner: String, repo: String): Response<Unit> = service.unstarRepo(owner, repo)
}



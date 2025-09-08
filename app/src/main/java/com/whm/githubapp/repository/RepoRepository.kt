package com.whm.githubapp.repository

import com.whm.githubapp.model.CreateIssueRequest
import com.whm.githubapp.model.GitHubRepo
import com.whm.githubapp.model.GitHubRepoResponse
import com.whm.githubapp.model.IssueResponse
import com.whm.githubapp.network.GitHubRepoService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepoRepository @Inject constructor(
    private val service: GitHubRepoService
) {
    suspend fun createIssue(owner: String, repo: String, title: String, body: String): IssueResponse {
        return service.createIssue(owner = owner, repo = repo, issue = CreateIssueRequest(title, body))
    }

    suspend fun searchRepos(query: String, page: Int? = null, perPage: Int? = null): GitHubRepoResponse {
        val q = buildString { append(query) }
        return service.searchRepos(q, page = page, perPage = perPage)
    }

    suspend fun getTrendingRepos(query: String, page: Int? = null, perPage: Int? = null): GitHubRepoResponse {
        return service.getTrendingRepos(query, page = page, perPage = perPage)
    }

    suspend fun getRepoDetail(owner: String, repo: String): GitHubRepo {
        return service.getRepoDetailAuth(owner, repo)
    }
}



package com.example.data.repository

import com.example.data.model.github.CommitSummary
import com.example.data.model.github.ContributorSummary
import com.example.data.model.github.GitHubRepoDto
import com.example.data.model.github.GitHubRepoStats
import com.example.data.model.github.GitHubUserProfile
import com.example.data.service.GitHubDataService
import com.example.data.service.GitHubDataServiceImpl

/**
 * Repository layer managing GitHub API interactions for repository statistics,
 * commit history, and user profile data by delegating to [GitHubDataService].
 */
class GitHubRepository(
    private val dataService: GitHubDataService = GitHubDataServiceImpl()
) {

    /**
     * Fetches comprehensive repository statistics including star count, forks, top contributors,
     * and recent commit history.
     */
    suspend fun fetchRepoStats(
        owner: String,
        repo: String,
        personalAccessToken: String? = null
    ): Result<GitHubRepoStats> {
        return dataService.fetchRepositoryStats(owner, repo, personalAccessToken)
    }

    /**
     * Fetches single repository raw metadata.
     */
    suspend fun fetchRepository(
        owner: String,
        repo: String,
        personalAccessToken: String? = null
    ): Result<GitHubRepoDto> {
        return dataService.fetchRepository(owner, repo, personalAccessToken)
    }

    /**
     * Fetches public user profile information.
     */
    suspend fun fetchUserProfile(
        username: String,
        personalAccessToken: String? = null
    ): Result<GitHubUserProfile> {
        return dataService.fetchUserProfile(username, personalAccessToken)
    }

    /**
     * Fetches authenticated user profile using token.
     */
    suspend fun fetchAuthenticatedUser(
        personalAccessToken: String
    ): Result<GitHubUserProfile> {
        return dataService.fetchAuthenticatedUser(personalAccessToken)
    }

    /**
     * Fetches repositories for a specific user.
     */
    suspend fun fetchUserRepositories(
        username: String,
        sort: String = "updated",
        perPage: Int = 30,
        personalAccessToken: String? = null
    ): Result<List<GitHubRepoDto>> {
        return dataService.fetchUserRepositories(username, sort, perPage, personalAccessToken)
    }

    /**
     * Fetches commit history for a repository.
     */
    suspend fun fetchCommitHistory(
        owner: String,
        repo: String,
        perPage: Int = 20,
        branchOrSha: String? = null,
        personalAccessToken: String? = null
    ): Result<List<CommitSummary>> {
        return dataService.fetchCommitHistory(owner, repo, perPage, branchOrSha, personalAccessToken)
    }

    /**
     * Fetches top contributors for a repository.
     */
    suspend fun fetchContributors(
        owner: String,
        repo: String,
        perPage: Int = 10,
        personalAccessToken: String? = null
    ): Result<List<ContributorSummary>> {
        return dataService.fetchContributors(owner, repo, perPage, personalAccessToken)
    }
}

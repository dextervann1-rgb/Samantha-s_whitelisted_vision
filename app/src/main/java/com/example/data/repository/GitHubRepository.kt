package com.example.data.repository

import com.example.data.model.github.CommitSummary
import com.example.data.model.github.ContributorSummary
import com.example.data.model.github.GitHubCommitDto
import com.example.data.model.github.GitHubRepoStats
import com.example.data.remote.github.GitHubApiClient
import com.example.data.remote.github.GitHubApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository layer managing GitHub API interactions for repository statistics and commit history.
 */
class GitHubRepository(
    private val apiService: GitHubApiService = GitHubApiClient.apiService
) {

    /**
     * Fetches comprehensive repository statistics including star count, forks, top contributors,
     * and recent commit history.
     *
     * @param owner The organization or user owning the repository (e.g. "ethereum", "MetaMask")
     * @param repo The repository name (e.g. "solidity", "metamask-extension")
     * @param personalAccessToken Optional GitHub PAT for higher rate limits
     */
    suspend fun fetchRepoStats(
        owner: String,
        repo: String,
        personalAccessToken: String? = null
    ): Result<GitHubRepoStats> = withContext(Dispatchers.IO) {
        try {
            val authHeader = personalAccessToken?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }

            // 1. Fetch Repository Metadata
            val repoResponse = apiService.getRepository(owner, repo, authHeader)
            if (!repoResponse.isSuccessful || repoResponse.body() == null) {
                val errorMsg = repoResponse.errorBody()?.string() ?: "Failed with HTTP ${repoResponse.code()}"
                return@withContext Result.failure(Exception("Repository fetch error ($owner/$repo): $errorMsg"))
            }
            val repoDto = repoResponse.body()!!

            // 2. Fetch Recent Commits (first page, 15 items)
            val commitsResponse = apiService.getCommitHistory(owner, repo, perPage = 15, page = 1, authHeader = authHeader)
            val commits = if (commitsResponse.isSuccessful) {
                commitsResponse.body().orEmpty().map { it.toCommitSummary() }
            } else {
                emptyList()
            }

            // 3. Fetch Contributors
            val contribResponse = apiService.getContributors(owner, repo, perPage = 8, authHeader = authHeader)
            val contributors = if (contribResponse.isSuccessful) {
                contribResponse.body().orEmpty().map {
                    ContributorSummary(
                        username = it.login,
                        contributions = it.contributions,
                        avatarUrl = it.avatarUrl
                    )
                }
            } else {
                emptyList()
            }

            val stats = GitHubRepoStats(
                fullName = repoDto.fullName,
                name = repoDto.name,
                description = repoDto.description ?: "No description provided",
                stars = repoDto.stargazersCount,
                forks = repoDto.forksCount,
                watchers = repoDto.watchersCount,
                openIssues = repoDto.openIssuesCount,
                language = repoDto.language ?: "Solidity / TypeScript",
                defaultBranch = repoDto.defaultBranch ?: "main",
                htmlUrl = repoDto.htmlUrl,
                ownerLogin = repoDto.owner?.login ?: owner,
                ownerAvatarUrl = repoDto.owner?.avatarUrl,
                recentCommits = commits,
                contributors = contributors
            )

            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches only commit history for a repository.
     */
    suspend fun fetchCommitHistory(
        owner: String,
        repo: String,
        perPage: Int = 20,
        personalAccessToken: String? = null
    ): Result<List<CommitSummary>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = personalAccessToken?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
            val response = apiService.getCommitHistory(owner, repo, perPage = perPage, authHeader = authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toCommitSummary() })
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun GitHubCommitDto.toCommitSummary(): CommitSummary {
        val firstLineMessage = this.commit.message.lines().firstOrNull()?.take(80) ?: "Commit update"
        return CommitSummary(
            sha = this.sha,
            shortSha = this.sha.take(7),
            message = firstLineMessage,
            authorName = this.author?.login ?: this.commit.author?.name ?: "git-author",
            authorAvatarUrl = this.author?.avatarUrl,
            date = this.commit.author?.date?.take(10) ?: "recent",
            url = this.htmlUrl ?: "https://github.com"
        )
    }
}

package com.example.data.service

import com.example.data.model.github.*
import com.example.data.remote.github.GitHubApiClient
import com.example.data.remote.github.GitHubApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Service interface for communicating with GitHub REST API via Retrofit.
 * Provides typed operations for fetching repository statistics, commit logs,
 * contributors, and user profiles according to project architecture.
 */
interface GitHubDataService {
    suspend fun fetchRepository(
        owner: String,
        repo: String,
        token: String? = null
    ): Result<GitHubRepoDto>

    suspend fun fetchRepositoryStats(
        owner: String,
        repo: String,
        token: String? = null
    ): Result<GitHubRepoStats>

    suspend fun fetchUserProfile(
        username: String,
        token: String? = null
    ): Result<GitHubUserProfile>

    suspend fun fetchAuthenticatedUser(
        token: String
    ): Result<GitHubUserProfile>

    suspend fun fetchUserRepositories(
        username: String,
        sort: String = "updated",
        perPage: Int = 30,
        token: String? = null
    ): Result<List<GitHubRepoDto>>

    suspend fun fetchCommitHistory(
        owner: String,
        repo: String,
        perPage: Int = 20,
        branchOrSha: String? = null,
        token: String? = null
    ): Result<List<CommitSummary>>

    suspend fun fetchContributors(
        owner: String,
        repo: String,
        perPage: Int = 10,
        token: String? = null
    ): Result<List<ContributorSummary>>
}

/**
 * Production implementation of [GitHubDataService] utilizing Retrofit and Coroutines.
 */
class GitHubDataServiceImpl(
    private val apiService: GitHubApiService = GitHubApiClient.apiService
) : GitHubDataService {

    override suspend fun fetchRepository(
        owner: String,
        repo: String,
        token: String?
    ): Result<GitHubRepoDto> = withContext(Dispatchers.IO) {
        try {
            val authHeader = token?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
            val response = apiService.getRepository(owner, repo, authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "HTTP ${response.code()}"
                Result.failure(Exception("Failed to fetch repository ($owner/$repo): $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchRepositoryStats(
        owner: String,
        repo: String,
        token: String?
    ): Result<GitHubRepoStats> = withContext(Dispatchers.IO) {
        try {
            val authHeader = token?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }

            // 1. Fetch Repository Metadata
            val repoResponse = apiService.getRepository(owner, repo, authHeader)
            if (!repoResponse.isSuccessful || repoResponse.body() == null) {
                val errorMsg = repoResponse.errorBody()?.string() ?: "HTTP ${repoResponse.code()}"
                return@withContext Result.failure(Exception("Repository fetch error ($owner/$repo): $errorMsg"))
            }
            val repoDto = repoResponse.body()!!

            // 2. Fetch Recent Commits
            val commitsResponse = apiService.getCommitHistory(owner, repo, perPage = 15, page = 1, authHeader = authHeader)
            val commits = if (commitsResponse.isSuccessful) {
                commitsResponse.body().orEmpty().map { it.toCommitSummary() }
            } else {
                emptyList()
            }

            // 3. Fetch Top Contributors
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

    override suspend fun fetchUserProfile(
        username: String,
        token: String?
    ): Result<GitHubUserProfile> = withContext(Dispatchers.IO) {
        try {
            val authHeader = token?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
            val response = apiService.getUserProfile(username, authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                val errorMsg = response.errorBody()?.string() ?: "HTTP ${response.code()}"
                Result.failure(Exception("Failed to fetch profile for user '$username': $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchAuthenticatedUser(
        token: String
    ): Result<GitHubUserProfile> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer $token"
            val response = apiService.getAuthenticatedUser(authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                val errorMsg = response.errorBody()?.string() ?: "HTTP ${response.code()}"
                Result.failure(Exception("Failed to fetch authenticated profile: $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchUserRepositories(
        username: String,
        sort: String,
        perPage: Int,
        token: String?
    ): Result<List<GitHubRepoDto>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = token?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
            val response = apiService.getUserRepositories(username, sort, perPage, authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "HTTP ${response.code()}"
                Result.failure(Exception("Failed to fetch repositories for '$username': $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchCommitHistory(
        owner: String,
        repo: String,
        perPage: Int,
        branchOrSha: String?,
        token: String?
    ): Result<List<CommitSummary>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = token?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
            val response = apiService.getCommitHistory(owner, repo, perPage = perPage, branchOrSha = branchOrSha, authHeader = authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toCommitSummary() })
            } else {
                val errorMsg = response.errorBody()?.string() ?: "HTTP ${response.code()}"
                Result.failure(Exception("Failed to fetch commit history ($owner/$repo): $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchContributors(
        owner: String,
        repo: String,
        perPage: Int,
        token: String?
    ): Result<List<ContributorSummary>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = token?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
            val response = apiService.getContributors(owner, repo, perPage = perPage, authHeader = authHeader)
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!.map {
                    ContributorSummary(
                        username = it.login,
                        contributions = it.contributions,
                        avatarUrl = it.avatarUrl
                    )
                }
                Result.success(list)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "HTTP ${response.code()}"
                Result.failure(Exception("Failed to fetch contributors ($owner/$repo): $errorMsg"))
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

    private fun GitHubUserProfileDto.toDomain(): GitHubUserProfile {
        return GitHubUserProfile(
            id = this.id,
            login = this.login,
            name = this.name ?: this.login,
            avatarUrl = this.avatarUrl,
            htmlUrl = this.htmlUrl,
            company = this.company,
            blog = this.blog,
            location = this.location,
            email = this.email,
            bio = this.bio ?: "No bio provided",
            twitterUsername = this.twitterUsername,
            publicRepos = this.publicRepos,
            publicGists = this.publicGists,
            followers = this.followers,
            following = this.following,
            memberSince = this.createdAt?.take(10) ?: "N/A"
        )
    }
}

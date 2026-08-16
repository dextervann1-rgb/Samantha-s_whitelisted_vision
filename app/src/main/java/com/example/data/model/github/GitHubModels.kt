package com.example.data.model.github

import com.squareup.moshi.Json

data class GitHubRepoDto(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "description") val description: String?,
    @Json(name = "html_url") val htmlUrl: String,
    @Json(name = "stargazers_count") val stargazersCount: Int = 0,
    @Json(name = "forks_count") val forksCount: Int = 0,
    @Json(name = "watchers_count") val watchersCount: Int = 0,
    @Json(name = "open_issues_count") val openIssuesCount: Int = 0,
    @Json(name = "default_branch") val defaultBranch: String? = "main",
    @Json(name = "language") val language: String? = null,
    @Json(name = "owner") val owner: GitHubOwnerDto? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null,
    @Json(name = "pushed_at") val pushedAt: String? = null,
    @Json(name = "size") val sizeInKb: Int = 0
)

data class GitHubOwnerDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "login") val login: String,
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "html_url") val htmlUrl: String? = null
)

data class GitHubCommitDto(
    @Json(name = "sha") val sha: String,
    @Json(name = "html_url") val htmlUrl: String? = null,
    @Json(name = "commit") val commit: GitHubCommitDetailDto,
    @Json(name = "author") val author: GitHubUserDto? = null,
    @Json(name = "committer") val committer: GitHubUserDto? = null
)

data class GitHubCommitDetailDto(
    @Json(name = "message") val message: String,
    @Json(name = "author") val author: GitHubGitUserDto? = null,
    @Json(name = "committer") val committer: GitHubGitUserDto? = null,
    @Json(name = "comment_count") val commentCount: Int = 0
)

data class GitHubGitUserDto(
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String? = null,
    @Json(name = "date") val date: String
)

data class GitHubUserDto(
    @Json(name = "login") val login: String,
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "html_url") val htmlUrl: String? = null
)

data class GitHubContributorDto(
    @Json(name = "login") val login: String,
    @Json(name = "contributions") val contributions: Int = 0,
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "html_url") val htmlUrl: String? = null
)

/**
 * Domain-level model aggregating repository statistics and recent commit history
 */
data class GitHubRepoStats(
    val fullName: String,
    val name: String,
    val description: String,
    val stars: Int,
    val forks: Int,
    val watchers: Int,
    val openIssues: Int,
    val language: String,
    val defaultBranch: String,
    val htmlUrl: String,
    val ownerLogin: String,
    val ownerAvatarUrl: String?,
    val recentCommits: List<CommitSummary>,
    val contributors: List<ContributorSummary>
)

data class CommitSummary(
    val sha: String,
    val shortSha: String,
    val message: String,
    val authorName: String,
    val authorAvatarUrl: String?,
    val date: String,
    val url: String
)

data class ContributorSummary(
    val username: String,
    val contributions: Int,
    val avatarUrl: String?
)

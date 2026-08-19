package com.example.data.remote.github

import com.example.data.model.github.GitHubCommitDto
import com.example.data.model.github.GitHubContributorDto
import com.example.data.model.github.GitHubRepoDto
import com.example.data.model.github.GitHubUserProfileDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit Service interface for fetching repository statistics, commit history,
 * and user profiles from the GitHub REST API.
 */
interface GitHubApiService {

    /**
     * Fetch detailed repository metadata and statistics.
     * Example: GET https://api.github.com/repos/ethereum/solidity
     */
    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") authHeader: String? = null
    ): Response<GitHubRepoDto>

    /**
     * Fetch commit history for a specific repository.
     * Example: GET https://api.github.com/repos/ethereum/solidity/commits?per_page=30&page=1
     */
    @GET("repos/{owner}/{repo}/commits")
    suspend fun getCommitHistory(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1,
        @Query("sha") branchOrSha: String? = null,
        @Header("Authorization") authHeader: String? = null
    ): Response<List<GitHubCommitDto>>

    /**
     * Fetch top contributors and their contribution counts.
     * Example: GET https://api.github.com/repos/ethereum/solidity/contributors
     */
    @GET("repos/{owner}/{repo}/contributors")
    suspend fun getContributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("per_page") perPage: Int = 10,
        @Header("Authorization") authHeader: String? = null
    ): Response<List<GitHubContributorDto>>

    /**
     * Fetch public user profile by username.
     * Example: GET https://api.github.com/users/dextervann1
     */
    @GET("users/{username}")
    suspend fun getUserProfile(
        @Path("username") username: String,
        @Header("Authorization") authHeader: String? = null
    ): Response<GitHubUserProfileDto>

    /**
     * Fetch authenticated user profile using token.
     * Example: GET https://api.github.com/user
     */
    @GET("user")
    suspend fun getAuthenticatedUser(
        @Header("Authorization") authHeader: String
    ): Response<GitHubUserProfileDto>

    /**
     * Fetch repositories for a specific user or organization.
     * Example: GET https://api.github.com/users/dextervann1/repos
     */
    @GET("users/{username}/repos")
    suspend fun getUserRepositories(
        @Path("username") username: String,
        @Query("sort") sort: String = "updated",
        @Query("per_page") perPage: Int = 30,
        @Header("Authorization") authHeader: String? = null
    ): Response<List<GitHubRepoDto>>
}

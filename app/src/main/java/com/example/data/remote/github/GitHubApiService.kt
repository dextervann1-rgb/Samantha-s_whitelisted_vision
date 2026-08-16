package com.example.data.remote.github

import com.example.data.model.github.GitHubCommitDto
import com.example.data.model.github.GitHubContributorDto
import com.example.data.model.github.GitHubRepoDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit Service interface for fetching repository statistics and commit history from the GitHub REST API.
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

package com.example.data.local

import androidx.room.*
import com.example.data.model.FusedCapsule
import com.example.data.model.RealioAsset
import com.example.data.model.Web3Repo
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Query("SELECT * FROM web3_repos ORDER BY stars DESC")
    fun getAllRepos(): Flow<List<Web3Repo>>

    @Query("SELECT * FROM web3_repos WHERE id = :id")
    suspend fun getRepoById(id: String): Web3Repo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepo(repo: Web3Repo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<Web3Repo>)

    @Delete
    suspend fun deleteRepo(repo: Web3Repo)
}

@Dao
interface AssetDao {
    @Query("SELECT * FROM realio_assets ORDER BY title ASC")
    fun getAllAssets(): Flow<List<RealioAsset>>

    @Query("SELECT * FROM realio_assets WHERE id = :id")
    suspend fun getAssetById(id: String): RealioAsset?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: RealioAsset)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(assets: List<RealioAsset>)

    @Delete
    suspend fun deleteAsset(asset: RealioAsset)
}

@Dao
interface CapsuleDao {
    @Query("SELECT * FROM fused_capsules ORDER BY createdAtMillis DESC")
    fun getAllCapsules(): Flow<List<FusedCapsule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapsule(capsule: FusedCapsule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(capsules: List<FusedCapsule>)

    @Update
    suspend fun updateCapsule(capsule: FusedCapsule)

    @Delete
    suspend fun deleteCapsule(capsule: FusedCapsule)
}

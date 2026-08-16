package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.FusedCapsule
import com.example.data.model.RealioAsset
import com.example.data.model.Web3Repo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Web3Repo::class, RealioAsset::class, FusedCapsule::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun assetDao(): AssetDao
    abstract fun capsuleDao(): CapsuleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "muse_fusion_database.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)
                            database.repoDao().insertAll(SampleData.initialRepos)
                            database.assetDao().insertAll(SampleData.initialAssets)
                            database.capsuleDao().insertAll(SampleData.initialCapsules)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

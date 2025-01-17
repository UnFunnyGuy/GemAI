package com.sarath.gem.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sarath.gem.data.local.dao.ConversationDao
import com.sarath.gem.data.local.dao.MessageDao
import com.sarath.gem.data.local.dao.PromptDao
import com.sarath.gem.data.local.model.ConversationEntity
import com.sarath.gem.data.local.model.DefaultPrompts
import com.sarath.gem.data.local.model.MessageEntity
import com.sarath.gem.data.local.model.PromptEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ConversationEntity::class, MessageEntity::class, PromptEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao

    abstract fun messageDao(): MessageDao

    abstract fun promptDao(): PromptDao

    companion object {

        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE
                ?: synchronized(this) { INSTANCE ?: buildDatabase(context = context).also { INSTANCE = it } }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "gemai.db")
                .fallbackToDestructiveMigration()
                .addCallback(
                    object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch { INSTANCE?.promptDao()?.insertAll(DefaultPrompts) }
                        }
                    }
                )
                .build()
        }
    }
}

package com.sarath.gem.core.base

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Upsert

/**
 * Base interface for Data Access Objects (DAOs). Provides common CRUD operations for entities. All DAOs in the
 * application should extend this interface to get common CRUD operations.
 *
 * @param T The entity type this DAO will manage.
 */
@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insert(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insertAll(entities: List<T>): List<Long>

    @Upsert suspend fun upsert(entity: T): Long

    @Upsert suspend fun upsertAll(entities: List<T>)

    @Update suspend fun update(entity: T)

    @Update suspend fun updateAll(entities: List<T>)

    @Delete suspend fun delete(entity: T)

    @Delete suspend fun deleteAll(entities: List<T>)
}

package com.cll.database.dao

import androidx.room.*
import com.cll.database.entities.EntityPersonsImages
import kotlinx.coroutines.flow.Flow


@Dao
interface PersonsImageDao {

    @Query("SELECT * FROM images WHERE image_type = :type ORDER BY uId DESC LIMIT 1")
    fun getImageByTpe(type: String): Flow<EntityPersonsImages?>

    @Query("SELECT * FROM images")
    suspend fun getAllImages(): List<EntityPersonsImages>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(entityPersonsImages: EntityPersonsImages)

    @Query("DELETE FROM images")
    suspend fun deleteImages()

    @Query("DELETE FROM images WHERE uId = :id")
    suspend fun deleteImageById(id: Long)

}
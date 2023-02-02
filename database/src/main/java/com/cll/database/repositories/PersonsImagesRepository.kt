package com.cll.database.repositories

import com.cll.database.entities.EntityPersonsImages
import kotlinx.coroutines.flow.Flow


interface PersonsImagesRepository {

    fun getImageByTpe(type: String): Flow<com.cll.database.entities.EntityPersonsImages?>

    suspend fun getAllImages(): List<com.cll.database.entities.EntityPersonsImages>

    suspend fun insertImage(entityPersonsImages: com.cll.database.entities.EntityPersonsImages)

    suspend fun deleteImages()

    suspend fun deleteImageById(id: Long)


}
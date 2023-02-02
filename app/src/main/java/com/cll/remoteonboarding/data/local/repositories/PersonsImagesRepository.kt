package com.cll.remoteonboarding.data.local.repositories

import com.cll.remoteonboarding.data.local.entities.EntityPersonsImages
import kotlinx.coroutines.flow.Flow


interface PersonsImagesRepository {

    fun getImageByTpe(type: String): Flow<EntityPersonsImages?>

    suspend fun getAllImages(): List<EntityPersonsImages>

    suspend fun insertImage(entityPersonsImages: EntityPersonsImages)

    suspend fun deleteImages()

    suspend fun deleteImageById(id: Long)


}
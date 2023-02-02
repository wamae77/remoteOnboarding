package com.cll.database.repositories

import com.cll.database.dao.PersonsImageDao
import com.cll.database.entities.EntityPersonsImages
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonsImagesRepositoryImpl @Inject constructor(
    private val personsImageDao: com.cll.database.dao.PersonsImageDao
) : PersonsImagesRepository {
    override  fun getImageByTpe(type: String): Flow<com.cll.database.entities.EntityPersonsImages?> {
        return personsImageDao.getImageByTpe(type)
    }

    override suspend fun getAllImages(): List<com.cll.database.entities.EntityPersonsImages> {
        return personsImageDao.getAllImages()
    }

    override suspend fun insertImage(entityPersonsImages: com.cll.database.entities.EntityPersonsImages) {
        personsImageDao.insertImage(entityPersonsImages)
    }

    override suspend fun deleteImages() {
        personsImageDao.deleteImages()
    }

    override suspend fun deleteImageById(id: Long) {
        personsImageDao.deleteImageById(id)
    }

}
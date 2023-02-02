package com.cll.remoteonboarding.data.local.repositories

import com.cll.remoteonboarding.data.local.dao.PersonsImageDao
import com.cll.remoteonboarding.data.local.entities.EntityPersonsImages
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonsImagesRepositoryImpl @Inject constructor(
    private val personsImageDao: PersonsImageDao
) : PersonsImagesRepository {
    override  fun getImageByTpe(type: String): Flow<EntityPersonsImages?> {
        return personsImageDao.getImageByTpe(type)
    }

    override suspend fun getAllImages(): List<EntityPersonsImages> {
        return personsImageDao.getAllImages()
    }

    override suspend fun insertImage(entityPersonsImages: EntityPersonsImages) {
        personsImageDao.insertImage(entityPersonsImages)
    }

    override suspend fun deleteImages() {
        personsImageDao.deleteImages()
    }

    override suspend fun deleteImageById(id: Long) {
        personsImageDao.deleteImageById(id)
    }

}
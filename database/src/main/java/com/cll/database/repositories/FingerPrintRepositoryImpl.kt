package com.cll.database.repositories

import com.cll.database.dao.FingerPrintDao
import com.cll.database.entities.EntityFingerPrint
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FingerPrintRepositoryImpl @Inject constructor(
    private val fingerPrintDao: com.cll.database.dao.FingerPrintDao
) : FingerPrintRepository {

    override val getPrints: Flow<List<com.cll.database.entities.EntityFingerPrint>>
        get() = fingerPrintDao.getPrints()

    override suspend fun insertFingerPrint(entityFingerPrint: com.cll.database.entities.EntityFingerPrint) {
        fingerPrintDao.insertFingerPrint(entityFingerPrint)
    }

    override suspend fun insertAllFingerPrint(fingerPrints: List<com.cll.database.entities.EntityFingerPrint>) {
        fingerPrintDao.insertAllFingerPrint(fingerPrints)
    }

    override suspend fun loadFingerPrintByPersonId(id: Int): List<com.cll.database.entities.EntityFingerPrint> {
        return fingerPrintDao.loadFingerPrintByPersonId(id)
    }

    override suspend fun deletePrintById(id: Int) {
        fingerPrintDao.deletePrintById(id)
    }

    override suspend fun deleteByNistPosition(nist_position: Int) {
        fingerPrintDao.deleteByNistPosition(nist_position)
    }
}
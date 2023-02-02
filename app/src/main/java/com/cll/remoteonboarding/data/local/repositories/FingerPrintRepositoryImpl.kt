package com.cll.remoteonboarding.data.local.repositories

import com.cll.remoteonboarding.data.local.dao.FingerPrintDao
import com.cll.remoteonboarding.data.local.entities.EntityFingerPrint
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FingerPrintRepositoryImpl @Inject constructor(
    private val fingerPrintDao: FingerPrintDao
) : FingerPrintRepository {

    override val getPrints: Flow<List<EntityFingerPrint>>
        get() = fingerPrintDao.getPrints()

    override suspend fun insertFingerPrint(entityFingerPrint: EntityFingerPrint) {
        fingerPrintDao.insertFingerPrint(entityFingerPrint)
    }

    override suspend fun insertAllFingerPrint(fingerPrints: List<EntityFingerPrint>) {
        fingerPrintDao.insertAllFingerPrint(fingerPrints)
    }

    override suspend fun loadFingerPrintByPersonId(id: Int): List<EntityFingerPrint> {
        return fingerPrintDao.loadFingerPrintByPersonId(id)
    }

    override suspend fun deletePrintById(id: Int) {
        fingerPrintDao.deletePrintById(id)
    }

    override suspend fun deleteByNistPosition(nist_position: Int) {
        fingerPrintDao.deleteByNistPosition(nist_position)
    }
}
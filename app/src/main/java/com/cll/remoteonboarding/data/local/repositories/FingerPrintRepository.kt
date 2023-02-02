package com.cll.remoteonboarding.data.local.repositories

import com.cll.remoteonboarding.data.local.entities.EntityFingerPrint
import kotlinx.coroutines.flow.Flow

interface FingerPrintRepository {

    val getPrints: Flow<List<EntityFingerPrint>>

    suspend fun insertFingerPrint(entityFingerPrint: EntityFingerPrint)

    suspend fun insertAllFingerPrint(fingerPrints: List<EntityFingerPrint>)

    suspend fun loadFingerPrintByPersonId(id: Int): List<EntityFingerPrint>

    suspend fun deletePrintById(id: Int)

    suspend fun deleteByNistPosition(nist_position:Int)
}
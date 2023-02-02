package com.cll.database.repositories

import com.cll.database.entities.EntityFingerPrint
import kotlinx.coroutines.flow.Flow

interface FingerPrintRepository {

    val getPrints: Flow<List<com.cll.database.entities.EntityFingerPrint>>

    suspend fun insertFingerPrint(entityFingerPrint: com.cll.database.entities.EntityFingerPrint)

    suspend fun insertAllFingerPrint(fingerPrints: List<com.cll.database.entities.EntityFingerPrint>)

    suspend fun loadFingerPrintByPersonId(id: Int): List<com.cll.database.entities.EntityFingerPrint>

    suspend fun deletePrintById(id: Int)

    suspend fun deleteByNistPosition(nist_position:Int)
}
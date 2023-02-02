package com.cll.database.dao

import androidx.room.*
import com.cll.database.entities.EntityFingerPrint
import kotlinx.coroutines.flow.Flow


@Dao
interface FingerPrintDao {


    @Query("SELECT * FROM finger_print")
    fun getPrints(): Flow<List<EntityFingerPrint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFingerPrint(entityFingerPrint: EntityFingerPrint)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFingerPrint(fingerPrints: List<EntityFingerPrint>)

    @Query("SELECT * FROM finger_print WHERE user_id =:id")
    suspend fun loadFingerPrintByPersonId(id: Int): List<EntityFingerPrint>

    @Query("DELETE FROM finger_print WHERE id = :id ")
    suspend fun deletePrintById(id: Int)

    @Query("DELETE FROM finger_print WHERE nist_position =:nist_position")
    suspend fun deleteByNistPosition(nist_position:Int)
}
package com.cll.remoteonboarding.data.local.dao

import androidx.room.*
import com.cll.remoteonboarding.data.local.entities.EntityPerson

@Dao
interface PersonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: EntityPerson)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPersons(person: List<EntityPerson>)

    @Delete
    suspend fun deletePerson(person: EntityPerson)

    @Update
    suspend fun updatePerson(person: EntityPerson)

    @Query("SELECT * FROM person WHERE userId = :id")
    suspend fun loadPersonById(id: Int):EntityPerson?
}
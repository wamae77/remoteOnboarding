package com.cll.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cll.database.dao.FingerPrintDao
import com.cll.database.dao.PersonDao
import com.cll.database.dao.PersonsImageDao
import com.cll.database.entities.EntityFingerPrint
import com.cll.database.entities.EntityPerson
import com.cll.database.entities.EntityPersonsImages

@Database(
    entities = [EntityPerson::class, EntityFingerPrint::class, EntityPersonsImages::class],
    exportSchema = true,
    version = 1,

    )
abstract class ApplicationDataBase : RoomDatabase() {

    abstract fun FingerPrintDao(): com.cll.database.dao.FingerPrintDao

    abstract fun PersonDao(): com.cll.database.dao.PersonDao

    abstract fun PersonsImageDao(): com.cll.database.dao.PersonsImageDao

    companion object{
        const val NAME ="onBoarding_database"
    }
}
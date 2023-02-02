package com.cll.remoteonboarding.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cll.remoteonboarding.data.local.dao.FingerPrintDao
import com.cll.remoteonboarding.data.local.dao.PersonDao
import com.cll.remoteonboarding.data.local.dao.PersonsImageDao
import com.cll.remoteonboarding.data.local.entities.EntityFingerPrint
import com.cll.remoteonboarding.data.local.entities.EntityPerson
import com.cll.remoteonboarding.data.local.entities.EntityPersonsImages

@Database(
    entities = [EntityPerson::class, EntityFingerPrint::class,EntityPersonsImages::class],
    exportSchema = true,
    version = 1,

    )
abstract class ApplicationDataBase : RoomDatabase() {

    abstract fun FingerPrintDao(): FingerPrintDao

    abstract fun PersonDao(): PersonDao

    abstract fun PersonsImageDao():PersonsImageDao

    companion object{
        const val NAME ="onBoarding_database"
    }
}
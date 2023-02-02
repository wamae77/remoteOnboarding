package com.cll.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
class EntityPersonsImages(
    @PrimaryKey(autoGenerate = true) val uId: Long,
    @ColumnInfo(name = "image_path") val path: String,
    @ColumnInfo(name = "image_type") val imageType: String,
    @ColumnInfo(name = "user_id") val personsId: Long
){
    enum class IMAGE_TYPES{
        SELFIE,
        BACK_FRONT_ID,
        FRONT_ID,
        SIGNATURE
    }
}
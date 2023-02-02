package com.cll.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "finger_print")
data class EntityFingerPrint(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "image_path") val imagePath: String,
    @ColumnInfo(name = "nist_position") val nistPosCode: Int,
    @ColumnInfo(name = "quality") val quality: Int
)
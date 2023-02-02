package com.cll.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "person")
data class EntityPerson(
    @PrimaryKey(autoGenerate = true) val userId: Int,
    @ColumnInfo(name = "full_name") val name: String,
    @ColumnInfo(name = "serial_number") val serialNumber: String,
    @ColumnInfo(name = "nid_number") val identityNUmber: String,
    @ColumnInfo(name = "date_of_birth") val dateOfBirth: String,
    @ColumnInfo(name = "sex") val sex: String,
    @ColumnInfo(name = "district_of_birth") val districtOfBirth: String,
    @ColumnInfo(name = "place_of_issue") val placeOfIssue: String,
    @ColumnInfo(name = "date_of_issue") val dateOfIssue: String,
    @ColumnInfo(name = "face_image") val faceImage: String
)
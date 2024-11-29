package com.example.calculationofscaffolding.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "walls")
data class WallsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val wallsJson: String // Сериализованный JSON
)

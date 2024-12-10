package com.example.calculationofscaffolding.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "elements")
data class Element(
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,  // ID элемента
    @ColumnInfo(name = "name") val name: String,  // Название
    @ColumnInfo(name = "price") var price: Int,  // Цена
    @ColumnInfo(name = "weight") val weight: Double// вес
) {
    @Ignore
    var quantity: Int = 0 // Локальное поле для хранения количества
}

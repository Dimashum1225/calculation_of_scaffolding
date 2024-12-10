package com.example.calculationofscaffolding.DB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.calculationofscaffolding.models.Element
import kotlinx.coroutines.flow.Flow

@Dao
interface ElementDao {

    @Insert
    suspend fun insertElement(element: Element)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(elemts: List<Element>)
    @Update
    suspend fun updateElement(element: Element)

    @Delete
    suspend fun deleteElement(element: Element)

    @Query("SELECT * FROM elements")
    suspend fun getAllElements(): List<Element>

    @Query("SELECT * FROM elements WHERE id = :id")
    suspend fun getElementById(id: Int): Element?
}

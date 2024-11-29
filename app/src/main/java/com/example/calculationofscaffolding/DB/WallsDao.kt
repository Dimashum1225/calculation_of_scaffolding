package com.example.calculationofscaffolding.DB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.calculationofscaffolding.models.WallsEntity

@Dao
interface WallsDao {
    // Метод для вставки новой записи (перед этим удаляем старые)
    @Insert
    suspend fun insert(wallsEntity: WallsEntity)

    // Метод для удаления всех записей
    @Query("DELETE FROM walls")
    suspend fun deleteAll()

    // Метод для получения записи по ID
    @Query("SELECT * FROM walls LIMIT 1")
    suspend fun getLatestWalls(): WallsEntity?
}


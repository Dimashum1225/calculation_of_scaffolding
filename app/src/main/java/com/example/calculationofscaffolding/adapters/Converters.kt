package com.example.calculationofscaffolding.adapters
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.calculationofscaffolding.models.Wall

class Converters {

    @TypeConverter
    fun fromWallList(walls: List<Wall>?): String? {
        val gson = Gson()
        val type = object : TypeToken<List<Wall>>() {}.type
        return gson.toJson(walls, type)
    }

    @TypeConverter
    fun toWallList(wallsString: String?): List<Wall>? {
        val gson = Gson()
        val type = object : TypeToken<List<Wall>>() {}.type
        return gson.fromJson(wallsString, type)
    }
}

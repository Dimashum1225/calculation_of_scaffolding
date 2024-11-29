package com.example.calculationofscaffolding.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.calculationofscaffolding.models.Element
import com.example.calculationofscaffolding.models.WallsEntity

@Database(entities = [Element::class,WallsEntity::class], version = 1)
abstract class ElementsDatabase : RoomDatabase() {

    abstract fun elementDao(): ElementDao
    abstract fun wallsDao(): WallsDao
    companion object {
        @Volatile
        private var INSTANCE: ElementsDatabase? = null

        fun getDatabase(context: Context): ElementsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ElementsDatabase::class.java,
                    "elementsDb"
                )
                    .addCallback(DatabaseCallback(context)) // Callback для предзаполнения данных
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Callback для предзаполнения базы данных
        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Используем корутину для выполнения операции в фоне
                ioThread {
                    getDatabase(context).elementDao().insertAll(prepopulateData())
                }
            }
        }

        // Метод для предзаполнения базы данных
        private fun prepopulateData(): List<Element> {
            return listOf(
                Element(0, "рама проходная", 1800,8.0),
                Element(1, "рама с лестницей", 2100,9.0),
                Element(2, "диагональ", 790,2.5),
                Element(3, "горизонталь", 420,0.8),
                Element(4, "ригеля", 1550,0.8),
                Element(5, "крепления к стене", 560,6.0),
                Element(6, "хомут поворотный", 620,0.7),
                Element(7, "домкрат", 2600,1.0),
                Element(8, "настил", 650,4.0),
                Element(9, "пятки", 250,0.5),
            )
        }

        // Выполнение задачи в отдельном потоке
        fun ioThread(f: () -> Unit) =
            Thread { f() }.start()
    }
}



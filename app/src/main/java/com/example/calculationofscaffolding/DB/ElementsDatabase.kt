package com.example.calculationofscaffolding.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.calculationofscaffolding.models.Element
import com.example.calculationofscaffolding.models.WallsEntity

@Database(entities = [Element::class,WallsEntity::class], version = 2)
abstract class ElementsDatabase : RoomDatabase() {

    abstract fun elementDao(): ElementDao
    abstract fun wallsDao(): WallsDao
    companion object {
        @Volatile
        private var INSTANCE: ElementsDatabase? = null
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Создаём временную таблицу с той же структурой
                database.execSQL("""
            CREATE TABLE elements_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                price INTEGER NOT NULL,
                weight REAL NOT NULL
            )
        """)

                // Заполняем новую таблицу данными в нужном порядке
                database.execSQL("""
            INSERT INTO elements_new (id, name, price, weight) VALUES 
                (1, 'рама с лестницей', 2100, 9.0),
                (2, 'диагональ', 790, 2.5),
                (3, 'рама проходная', 1800, 8.0),
                (4, 'горизонталь', 420, 0.8),
                (5, 'ригеля', 1550, 0.8),
                (6, 'крепления к стене', 560, 6.0),
                (7, 'хомут поворотный', 620, 0.7),
                (8, 'домкрат', 2600, 1.0),
                (9, 'настил', 650, 4.0),
                (10, 'пятки', 250, 0.5)
        """)

                // Удаляем старую таблицу
                database.execSQL("DROP TABLE elements")

                // Переименовываем новую таблицу
                database.execSQL("ALTER TABLE elements_new RENAME TO elements")
            }
        }

        fun getDatabase(context: Context): ElementsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ElementsDatabase::class.java,
                    "elementsDb"
                )
                    .addCallback(DatabaseCallback(context)) // Callback для предзаполнения данных
                    .addMigrations(MIGRATION_1_2)
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
                Element(1, "рама проходная", 1800,8.0),
                Element(2, "рама с лестницей", 2100,9.0),
                Element(3, "диагональ", 790,2.5),
                Element(4, "горизонталь", 420,0.8),
                Element(5, "ригеля", 1550,0.8),
                Element(6, "крепления к стене", 560,6.0),
                Element(7, "хомут поворотный", 620,0.7),
                Element(8, "домкрат", 2600,1.0),
                Element(9, "настил", 650,4.0),
                Element(10, "пятки", 250,0.5),
            )
        }

        // Выполнение задачи в отдельном потоке
        fun ioThread(f: () -> Unit) =
            Thread { f() }.start()
    }
}



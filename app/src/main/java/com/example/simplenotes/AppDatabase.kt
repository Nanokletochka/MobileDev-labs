package com.example.simplenotes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Класс базы данных приложения, использующий Room Persistence Library
 * Отвечает за создание и управление базой данных SQLite
 */
@Database(
    entities = [Note::class],  // Сущности, хранимые в базе данных
    version = 1,               // Версия схемы базы данных
    exportSchema = false       // Отключаем экспорт схемы
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Предоставляет доступ к DAO для работы с заметками
     * Возвращает экземпляр NoteDao для выполнения операций с базой данных
     */
    abstract fun noteDao(): NoteDao

    /**
     * Гарантируем наличие только одного экземпляра базы данных
     */
    companion object {

        // @Volatile гарантирует видимость изменений переменной между потоками
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Получение экземпляра базы данных (реализация паттерна Singleton)
         * context - контекст приложения для создания базы данных
         * Возвращает единственный экземпляр AppDatabase
         */
        fun getInstance(context: Context): AppDatabase {
            // Возвращаем существующий экземпляр или создаём новый
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,     // Класс базы данных
                    "notes_database"             // Имя файла базы данных
                ).build()

                // Сохраняем созданный экземпляр в статической переменной
                INSTANCE = instance

                // Возвращаем созданный экземпляр
                instance
            }
        }
    }
}
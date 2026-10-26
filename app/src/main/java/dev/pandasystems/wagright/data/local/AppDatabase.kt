package dev.pandasystems.wagright.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DogEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
	abstract fun dogDao(): DogDao
}

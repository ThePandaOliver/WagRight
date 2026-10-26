package dev.pandasystems.wagright.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DogDao {
	@Query("SELECT * FROM liked_dogs ORDER BY likedAt DESC")
	fun getAllLikedDogs(): Flow<List<DogEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(dog: DogEntity)

	@Delete
	suspend fun delete(dog: DogEntity)
}
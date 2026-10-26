package dev.pandasystems.wagright.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.pandasystems.wagright.data.model.DogImage

@Entity(tableName = "liked_dogs")
data class DogEntity(
	@PrimaryKey val imageUrl: String,
	val breed: String,
	val likedAt: Long
)

fun DogEntity.toDomainModel() = DogImage(
	imageUrl = imageUrl,
	breed = breed,
	likedAt = likedAt
)

fun DogImage.toEntity() = DogEntity(
	imageUrl = imageUrl,
	breed = breed,
	likedAt = likedAt
)
package dev.pandasystems.wagright.data.repository

import dev.pandasystems.wagright.data.local.DogDao
import dev.pandasystems.wagright.data.local.toDomainModel
import dev.pandasystems.wagright.data.local.toEntity
import dev.pandasystems.wagright.data.model.DogImage
import dev.pandasystems.wagright.data.remote.DogApiService
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class DogRepository @Inject constructor(
	private val api: DogApiService,
	private val dao: DogDao
) {
	val likedDogs: Flow<List<DogImage>> = dao.getAllLikedDogs()
		.map { entities -> entities.map { it.toDomainModel() } }
		.flowOn(Dispatchers.IO)

	suspend fun fetchRandomDog(): Result<DogImage> = withContext(Dispatchers.IO) {
		runCatching {
			val response = api.getRandomDogImage()
			DogImage(imageUrl = response.message)
		}
	}

	suspend fun fetchRandomDogPair(): Result<Pair<DogImage, DogImage>> = withContext(Dispatchers.IO) {
		runCatching {
			coroutineScope {
				val firstDeferred = async { api.getRandomDogImage() }
				val secondDeferred = async { api.getRandomDogImage() }
				val (first, second) = awaitAll(firstDeferred, secondDeferred)
				DogImage(imageUrl = first.message) to DogImage(imageUrl = second.message)
			}
		}
	}

	suspend fun likeDog(dog: DogImage) = withContext(Dispatchers.IO) {
		dao.insert(dog.toEntity())
	}

	suspend fun removeLikedDog(dog: DogImage) = withContext(Dispatchers.IO) {
		dao.delete(dog.toEntity())
	}
}

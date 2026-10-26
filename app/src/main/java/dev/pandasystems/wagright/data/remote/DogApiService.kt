package dev.pandasystems.wagright.data.remote

import retrofit2.http.GET

interface DogApiService {
	@GET("breeds/image/random")
	suspend fun getRandomDogImage(): DogApiResponse

	companion object {
		const val BASE_URL = "https://dog.ceo/api/"
	}
}
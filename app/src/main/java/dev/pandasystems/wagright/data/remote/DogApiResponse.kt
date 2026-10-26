package dev.pandasystems.wagright.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class DogApiResponse(
	val message: String,
	val status: String
)
package dev.pandasystems.wagright.ui.swipe

import dev.pandasystems.wagright.data.model.DogImage

sealed interface SwipeUiState {
	data object Loading : SwipeUiState
	data class Success(
		val currentDog: DogImage,
		val nextDog: DogImage? = null
	) : SwipeUiState
	data class Error(val message: String) : SwipeUiState
}

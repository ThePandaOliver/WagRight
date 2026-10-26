package dev.pandasystems.wagright.ui.swipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandasystems.wagright.data.model.DogImage
import dev.pandasystems.wagright.data.repository.DogRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SwipeViewModel @Inject constructor(
	private val repository: DogRepository
) : ViewModel() {

	private val _uiState = MutableStateFlow<SwipeUiState>(SwipeUiState.Loading)
	val uiState: StateFlow<SwipeUiState> = _uiState.asStateFlow()

	init {
		loadInitialDog()
	}

	fun loadInitialDog() {
		viewModelScope.launch {
			_uiState.value = SwipeUiState.Loading
			repository.fetchRandomDogPair()
				.onSuccess { (current, next) ->
					_uiState.value = SwipeUiState.Success(
						currentDog = current,
						nextDog = next
					)
				}
				.onFailure { error ->
					_uiState.value = SwipeUiState.Error(
						message = error.localizedMessage ?: "Failed to load dog image"
					)
				}
		}
	}

	fun onSwipeRight(dog: DogImage) {
		viewModelScope.launch {
			launch { repository.likeDog(dog) }
			advanceToNextDog()
		}
	}

	fun onSwipeLeft() {
		viewModelScope.launch {
			advanceToNextDog()
		}
	}

	private suspend fun advanceToNextDog() {
		val currentState = _uiState.value
		if (currentState is SwipeUiState.Success && currentState.nextDog != null) {
			val promotedDog = currentState.nextDog
			_uiState.value = SwipeUiState.Success(currentDog = promotedDog, nextDog = null)

			repository.fetchRandomDog()
				.onSuccess { newNextDog ->
					val latestState = _uiState.value
					if (latestState is SwipeUiState.Success && latestState.currentDog == promotedDog) {
						_uiState.value = latestState.copy(nextDog = newNextDog)
					}
				}
		} else {
			loadInitialDog()
		}
	}
}

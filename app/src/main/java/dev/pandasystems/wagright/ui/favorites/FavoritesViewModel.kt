package dev.pandasystems.wagright.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandasystems.wagright.data.model.DogImage
import dev.pandasystems.wagright.data.repository.DogRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class FavoritesViewModel @Inject constructor(
	private val repository: DogRepository
) : ViewModel() {

	val likedDogs: StateFlow<List<DogImage>> = repository.likedDogs
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = emptyList()
		)

	fun removeDog(dog: DogImage) {
		viewModelScope.launch {
			repository.removeLikedDog(dog)
		}
	}
}

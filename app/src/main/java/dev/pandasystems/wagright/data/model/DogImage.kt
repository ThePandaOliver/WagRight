package dev.pandasystems.wagright.data.model

data class DogImage(
	val imageUrl: String,
	val breed: String = extractBreedFromUrl(imageUrl),
	val likedAt: Long = System.currentTimeMillis()
) {
	companion object {
		fun extractBreedFromUrl(url: String): String {
			return url.substringAfter("/breeds/", "")
				.substringBefore("/")
				.split("-")
				.joinToString(" ") { part ->
					part.replaceFirstChar { it.uppercase() }
				}
				.ifEmpty { "Unknown Breed" }
		}
	}
}
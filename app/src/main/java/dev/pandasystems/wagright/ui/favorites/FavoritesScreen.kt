package dev.pandasystems.wagright.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import dev.pandasystems.wagright.data.model.DogImage
import dev.pandasystems.wagright.ui.theme.WagRightTheme

@Composable
fun FavoritesRoute(
	viewModel: FavoritesViewModel = hiltViewModel(),
	modifier: Modifier = Modifier
) {
	val likedDogs by viewModel.likedDogs.collectAsStateWithLifecycle()
	FavoritesScreen(
		likedDogs = likedDogs,
		onRemoveDog = viewModel::removeDog,
		modifier = modifier
	)
}

@Composable
fun FavoritesScreen(
	likedDogs: List<DogImage>,
	onRemoveDog: (DogImage) -> Unit,
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(horizontal = 16.dp)
	) {
		Text(
			text = "Liked Dogs (${likedDogs.size})",
			style = MaterialTheme.typography.headlineMedium,
			fontWeight = FontWeight.ExtraBold,
			color = MaterialTheme.colorScheme.primary,
			modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
		)

		if (likedDogs.isEmpty()) {
			Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					Icon(
						imageVector = Icons.Default.FavoriteBorder,
						contentDescription = null,
						modifier = Modifier.size(64.dp),
						tint = MaterialTheme.colorScheme.outline
					)
					Spacer(modifier = Modifier.height(12.dp))
					Text(
						text = "No liked dogs yet",
						style = MaterialTheme.typography.titleMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
					Text(
						text = "Swipe right on dogs you love!",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.outline
					)
				}
			}
		} else {
			LazyVerticalGrid(
				columns = GridCells.Adaptive(minSize = 150.dp),
				contentPadding = PaddingValues(bottom = 24.dp),
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				verticalArrangement = Arrangement.spacedBy(12.dp),
				modifier = Modifier.fillMaxSize()
			) {
				items(items = likedDogs, key = { it.imageUrl }) { dog ->
					LikedDogItem(
						dog = dog,
						onRemove = { onRemoveDog(dog) }
					)
				}
			}
		}
	}
}

@Composable
private fun LikedDogItem(
	dog: DogImage,
	onRemove: () -> Unit,
	modifier: Modifier = Modifier
) {
	Card(
		modifier = modifier
			.fillMaxWidth()
			.aspectRatio(0.8f),
		shape = RoundedCornerShape(16.dp),
		elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
	) {
		Box(modifier = Modifier.fillMaxSize()) {
			AsyncImage(
				model = dog.imageUrl,
				contentDescription = dog.breed,
				contentScale = ContentScale.Crop,
				modifier = Modifier.fillMaxSize()
			)

			IconButton(
				onClick = onRemove,
				modifier = Modifier
					.align(Alignment.TopEnd)
					.padding(8.dp)
					.size(36.dp),
				colors = IconButtonDefaults.iconButtonColors(
					containerColor = Color.Black.copy(alpha = 0.5f),
					contentColor = Color.White
				)
			) {
				Icon(
					imageVector = Icons.Default.Delete,
					contentDescription = "Remove from favorites",
					modifier = Modifier.size(20.dp)
				)
			}

			Box(
				modifier = Modifier
					.fillMaxWidth()
					.align(Alignment.BottomCenter)
					.background(
						Brush.verticalGradient(
							colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
						)
					)
					.padding(12.dp)
			) {
				Text(
					text = dog.breed,
					style = MaterialTheme.typography.bodyMedium,
					fontWeight = FontWeight.Bold,
					color = Color.White,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
	WagRightTheme {
		FavoritesScreen(
			likedDogs = listOf(
				DogImage(
					imageUrl = "https://images.dog.ceo/breeds/husky/n02110185_10047.jpg",
					breed = "Husky"
				),
				DogImage(
					imageUrl = "https://images.dog.ceo/breeds/cattledog-australian/img_3668.jpg",
					breed = "Cattle Dog"
				)
			),
			onRemoveDog = {}
		)
	}
}

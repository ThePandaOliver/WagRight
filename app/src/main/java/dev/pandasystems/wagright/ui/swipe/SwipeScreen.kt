package dev.pandasystems.wagright.ui.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import dev.pandasystems.wagright.data.model.DogImage
import dev.pandasystems.wagright.ui.theme.WagRightTheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SwipeRoute(
	viewModel: SwipeViewModel = hiltViewModel(),
	modifier: Modifier = Modifier
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	SwipeScreen(
		uiState = uiState,
		onSwipeRight = viewModel::onSwipeRight,
		onSwipeLeft = viewModel::onSwipeLeft,
		onRetry = viewModel::loadInitialDog,
		modifier = modifier
	)
}

@Composable
fun SwipeScreen(
	uiState: SwipeUiState,
	onSwipeRight: (DogImage) -> Unit,
	onSwipeLeft: () -> Unit,
	onRetry: () -> Unit,
	modifier: Modifier = Modifier
) {
	Box(
		modifier = modifier.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		when (uiState) {
			is SwipeUiState.Loading -> {
				Column(
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center
				) {
					CircularProgressIndicator()
					Spacer(modifier = Modifier.height(16.dp))
					Text(
						text = "Fetching cute dogs...",
						style = MaterialTheme.typography.bodyLarge
					)
				}
			}

			is SwipeUiState.Error -> {
				Column(
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center,
					modifier = Modifier.padding(24.dp)
				) {
					Text(
						text = "Could not load dog image.",
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Bold,
						textAlign = TextAlign.Center
					)
					Spacer(modifier = Modifier.height(8.dp))
					Text(
						text = uiState.message,
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						textAlign = TextAlign.Center
					)
					Spacer(modifier = Modifier.height(16.dp))
					Button(onClick = onRetry) {
						Icon(
							imageVector = Icons.Default.Refresh,
							contentDescription = null,
							modifier = Modifier.padding(end = 8.dp)
						)
						Text("Try Again")
					}
				}
			}

			is SwipeUiState.Success -> {
				val scope = rememberCoroutineScope()
				val offsetX = remember(uiState.currentDog.imageUrl) { Animatable(0f) }
				val offsetY = remember(uiState.currentDog.imageUrl) { Animatable(0f) }
				val swipeThreshold = 300f
				val backgroundSwipeProgress = (abs(offsetX.value) / (swipeThreshold * 3)).coerceIn(0f, 1f)
				val foregroundSwipeProgress = (abs(offsetX.value) / (swipeThreshold)).coerceIn(0f, 1f)

				Box(
					modifier = Modifier.fillMaxSize(),
					contentAlignment = Alignment.Center
				) {
					// Background Card
					uiState.nextDog?.let { nextDog ->
						val scale = lerp(
							start = 0.7f,
							stop = 1f,
							fraction = backgroundSwipeProgress
						)
						val backgroundTargetCornerRadius = lerp(
							start = 48.dp,
							stop = 0.dp,
							fraction = backgroundSwipeProgress
						)
						DogCard(
							dog = nextDog,
							targetCornerRadius = backgroundTargetCornerRadius,
							modifier = Modifier
								.fillMaxSize()
								.scale(scale)
						)
					}

					// Foreground swipeable card
					key(uiState.currentDog.imageUrl) {
						val foregroundTargetCornerRadius = lerp(
							start = 0.dp,
							stop = 48.dp,
							fraction = foregroundSwipeProgress
						)
						DogCard(
							dog = uiState.currentDog,
							targetCornerRadius = foregroundTargetCornerRadius,
							modifier = Modifier
								.fillMaxSize()
								.offset {
									IntOffset(
										offsetX.value.roundToInt(),
										offsetY.value.roundToInt()
									)
								}
								.graphicsLayer {
									rotationZ = (offsetX.value / 25f).coerceIn(-20f, 20f)
								}
								.pointerInput(uiState.currentDog.imageUrl) {
									detectDragGestures(
										onDragEnd = {
											scope.launch {
												when {
													offsetX.value > swipeThreshold -> {
														offsetX.animateTo(
															targetValue = 1600f,
															animationSpec = tween(300)
														)
														onSwipeRight(uiState.currentDog)
													}

													offsetX.value < -swipeThreshold -> {
														offsetX.animateTo(
															targetValue = -1600f,
															animationSpec = tween(300)
														)
														onSwipeLeft()
													}

													else -> {
														launch { offsetX.animateTo(0f, tween(200)) }
														launch { offsetY.animateTo(0f, tween(200)) }
													}
												}
											}
										},
										onDragCancel = {
											scope.launch {
												launch { offsetX.animateTo(0f, tween(200)) }
												launch { offsetY.animateTo(0f, tween(200)) }
											}
										},
										onDrag = { change, dragAmount ->
											change.consume()
											scope.launch {
												offsetX.snapTo(offsetX.value + dragAmount.x)
												offsetY.snapTo(offsetY.value + dragAmount.y)
											}
										}
									)
								}
						)
					}

					// Overlay Pass / Like Buttons
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.align(Alignment.BottomCenter)
							.padding(bottom = 24.dp),
						horizontalArrangement = Arrangement.SpaceEvenly,
						verticalAlignment = Alignment.CenterVertically
					) {
						FilledIconButton(
							onClick = {
								scope.launch {
									offsetX.animateTo(-1600f, tween(300))
									onSwipeLeft()
								}
							},
							modifier = Modifier.size(64.dp),
							shape = CircleShape,
							colors = IconButtonDefaults.filledIconButtonColors(
								containerColor = Color(0x88FFFFFF),
								contentColor = Color(0xAA000000)
							)
						) {
							Icon(
								imageVector = Icons.Default.Close,
								contentDescription = "Pass",
								modifier = Modifier.size(32.dp)
							)
						}

						FilledIconButton(
							onClick = {
								scope.launch {
									offsetX.animateTo(1600f, tween(300))
									onSwipeRight(uiState.currentDog)
								}
							},
							modifier = Modifier.size(64.dp),
							shape = CircleShape,
							colors = IconButtonDefaults.filledIconButtonColors(
								containerColor = Color(0x88FFFFFF),
								contentColor = Color(0xAA000000)
							)
						) {
							Icon(
								imageVector = Icons.Default.Favorite,
								contentDescription = "Like",
								modifier = Modifier.size(32.dp)
							)
						}
					}
				}
			}
		}
	}
}

@Composable
private fun DogCard(
	dog: DogImage,
	targetCornerRadius: Dp,
	modifier: Modifier = Modifier
) {
	val cornerRadius by animateDpAsState(
		targetValue = targetCornerRadius,
		animationSpec = spring(
			dampingRatio = Spring.DampingRatioNoBouncy,
			stiffness = Spring.StiffnessHigh
		),
		label = "cornerRadius"
	)

	Card(
		modifier = modifier,
		shape = RoundedCornerShape(cornerRadius),
		elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
	) {
		Box(modifier = Modifier.fillMaxSize()) {
			AsyncImage(
				model = dog.imageUrl,
				contentDescription = dog.breed,
				contentScale = ContentScale.Crop,
				modifier = Modifier.fillMaxSize()
			)

			// Text & Gradient
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.align(Alignment.BottomCenter)
					.background(
						Brush.verticalGradient(
							colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
						)
					)
					.padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 104.dp)
			) {
				Column {
					Text(
						text = dog.breed,
						style = MaterialTheme.typography.headlineSmall,
						fontWeight = FontWeight.Bold,
						color = Color.White
					)
					Text(
						text = "Swipe right to like, left to pass",
						style = MaterialTheme.typography.bodyMedium,
						color = Color.White.copy(alpha = 0.8f)
					)
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun SwipeScreenPreview() {
	WagRightTheme {
		SwipeScreen(
			uiState = SwipeUiState.Success(
				currentDog = DogImage(
					imageUrl = "https://images.dog.ceo/breeds/retriever-golden/n02099601_3004.jpg",
					breed = "Retriever Golden"
				)
			),
			onSwipeRight = {},
			onSwipeLeft = {},
			onRetry = {}
		)
	}
}

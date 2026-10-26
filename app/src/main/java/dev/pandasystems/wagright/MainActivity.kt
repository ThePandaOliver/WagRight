package dev.pandasystems.wagright

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dagger.hilt.android.AndroidEntryPoint
import dev.pandasystems.wagright.ui.favorites.FavoritesRoute
import dev.pandasystems.wagright.ui.swipe.SwipeRoute
import dev.pandasystems.wagright.ui.theme.WagRightTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			WagRightTheme {
				WagRightApp()
			}
		}
	}
}

@Composable
fun WagRightApp() {
	var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.SWIPE) }

	NavigationSuiteScaffold(
		navigationSuiteItems = {
			AppDestinations.entries.forEach {
				item(
					icon = {
						Icon(
							imageVector = it.icon,
							contentDescription = it.label
						)
					},
					label = { Text(it.label) },
					selected = it == currentDestination,
					onClick = { currentDestination = it }
				)
			}
		}
	) {
		Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
			when (currentDestination) {
				AppDestinations.SWIPE -> SwipeRoute(
					modifier = Modifier.padding(innerPadding)
				)
				AppDestinations.FAVORITES -> FavoritesRoute(
					modifier = Modifier.padding(innerPadding)
				)
			}
		}
	}
}

enum class AppDestinations(
	val label: String,
	val icon: ImageVector,
) {
	SWIPE("Swipe", Icons.Default.Pets),
	FAVORITES("Favorites", Icons.Default.Favorite),
}

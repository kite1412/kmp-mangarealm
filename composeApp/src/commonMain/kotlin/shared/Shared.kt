package shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.irgaly.kottage.platform.KottageContext

@Composable
expect fun adjustStatusBarColor(color: Color)

expect val kottageContext: KottageContext

expect val databaseDir: String

@Composable
expect fun applyEdgeToEdge()

@Composable
expect fun adjustNavBarColor()

expect val currentTimeMillis: Long
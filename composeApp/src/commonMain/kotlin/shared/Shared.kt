package shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.irgaly.kottage.platform.KottageContext

@Composable
expect fun adjustStatusBar(color: Color)

expect val kottageContext: KottageContext

expect val databaseDir: String
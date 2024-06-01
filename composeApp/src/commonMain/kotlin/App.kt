
import androidx.compose.runtime.Composable
import api.mangadex.service.MangaDex
import api.mangadex.service.MangaDexImpl
import api.mangadex.service.TokenHandler
import api.mangadex.service.TokenHandlerImpl
import io.github.irgaly.kottage.Kottage
import io.github.irgaly.kottage.KottageEnvironment
import io.github.irgaly.kottage.get
import io.github.irgaly.kottage.platform.KottageContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.ui.tooling.preview.Preview
import shared.kottageContext
import theme.AppTheme
import theme.gradient2
import util.KOTTAGE_TOKEN
import view.SplashScreen
import view.LoginScreen
import view.MainScreen
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import io.github.irgaly.kottage.getOrNull
import kotlinx.coroutines.launch
import shared.databaseDir
import shared.adjustStatusBarColor
import shared.applyEdgeToEdge
import shared.adjustNavBarColor
import kotlinx.coroutines.delay
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import viewmodel.MainViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.MutableState
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.MangaAttributes

data class ScreenSize(
    val height: Dp,
    val width: Dp
)

lateinit var screenSize: ScreenSize

object Libs {
    val mangaDex: MangaDex = MangaDexImpl()
    private val kottage: Kottage = Kottage(
        name = "kottage",
        directoryPath = databaseDir,
        environment = KottageEnvironment(
            context = kottageContext
        ),
        scope = CoroutineScope(Dispatchers.Default)
    )
    val kottageStorage = kottage.storage("default")
    val kottageCache = kottage.cache("default")
}

private suspend fun isLoggedIn(): Boolean {
    Libs.kottageStorage.getOrNull<String>(KOTTAGE_TOKEN)?.let {
        return true
    }
    return false
}

@Composable
@Preview
fun App() {
    val isLoggedIn = remember {
        mutableStateOf(false)
    }
    val isShowingSplash = remember {
        mutableStateOf(true)
    }
    LaunchedEffect(true) {
        isLoggedIn.value = isLoggedIn()
        delay(util.SPLASH_TIME.toLong())
        isShowingSplash.value = false
    }
    AppTheme {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            screenSize = ScreenSize(height = this.maxHeight, width = this.maxWidth)
            if (!isLoggedIn.value) {
                LoginScreen(onSuccess = {
                    isLoggedIn.value = true
                })
            } else {
                if (!isShowingSplash.value) {
                    applyEdgeToEdge()
                    adjustNavBarColor()
                }
                view.MainScreen()
            }
            if (isShowingSplash.value) {
                SplashScreen()
            }
        }
    }
}
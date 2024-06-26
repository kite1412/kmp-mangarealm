
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import api.mangadex.service.MangaDex
import api.mangadex.service.MangaDexImpl
import cafe.adriel.voyager.navigator.Navigator
import io.github.irgaly.kottage.Kottage
import io.github.irgaly.kottage.KottageEnvironment
import io.github.irgaly.kottage.getOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import shared.databaseDir
import shared.kottageContext
import theme.AppTheme
import view.LoginScreen
import view.MainScreen
import view.SplashScreen
import viewmodel.MainViewModel

data class ScreenSize(
    val height: Dp,
    val width: Dp
)

lateinit var screenSize: ScreenSize
val LocalMainViewModel = compositionLocalOf { MainViewModel() }

object Libs {
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
    val mangaDex: MangaDex = MangaDexImpl()
}


private suspend fun isLoggedIn(): Boolean {
    Libs.kottageStorage.getOrNull<String>(util.KottageConst.TOKEN)?.let {
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
    val mainViewModel = remember { MainViewModel() }
    // init for utilities that are not bound by whether is logged in or not
    LaunchedEffect(true) {
        Initializer()(postTagSetup = {
            mainViewModel.fetchMangaByTags(it)
        })
    }
    mainViewModel.initMangaTagsPainter()
    // check for login info
    LaunchedEffect(true) {
        isLoggedIn.value = isLoggedIn()
        delay(util.SPLASH_TIME.toLong())
        isShowingSplash.value = false
    }
    // perform actions after logged in
    LaunchedEffect(isLoggedIn.value) {
        if (isLoggedIn.value) {
            mainViewModel.updateUsername()
        }
    }
    AppTheme {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            screenSize = ScreenSize(height = this.maxHeight, width = this.maxWidth)
            if (!isShowingSplash.value) {
                mainViewModel.disableEdgeToEdge()
            }
            if (!isLoggedIn.value) {
                LoginScreen(onSuccess = {
                    isLoggedIn.value = true
                })
            } else {
                CompositionLocalProvider(LocalMainViewModel provides mainViewModel) {
                    Navigator(MainScreen())
                }
            }
            if (isShowingSplash.value) {
                SplashScreen()
            }
        }
    }
}

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import api.jikan.service.Jikan
import api.jikan.service.JikanImpl
import api.mangadex.service.MangaDex
import api.mangadex.service.MangaDexImpl
import cafe.adriel.voyager.navigator.Navigator
import io.github.irgaly.kottage.Kottage
import io.github.irgaly.kottage.KottageEnvironment
import io.github.irgaly.kottage.getOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.ChapterList
import model.Manga
import model.ScreenSize
import model.WidthClass
import model.emptyManga
import org.jetbrains.compose.ui.tooling.preview.Preview
import shared.adjustStatusBarColor
import shared.databaseDir
import shared.kottageContext
import theme.AppTheme
import theme.darkBeige
import view.LoginScreen
import view.MainScreen
import view.SplashScreen
import view_model.SharedViewModel
import view_model.main.MainViewModel

val LocalScreenSize = compositionLocalOf { ScreenSize(0.dp, 0.dp) }
val LocalMainViewModel = compositionLocalOf { MainViewModel(SharedViewModel()) }
val LocalSharedViewModel = compositionLocalOf { SharedViewModel() }
val LocalWidthClass: ProvidableCompositionLocal<WidthClass> = compositionLocalOf { WidthClass.Compact }

object Libs {
    val kottageStorage = Kottage(
        name = "kottage",
        directoryPath = databaseDir,
        environment = KottageEnvironment(
            context = kottageContext
        ),
        scope = CoroutineScope(Dispatchers.Default)
    ).storage("default")
    val cache = Cache()
    val mangaDex: MangaDex = MangaDexImpl()
    val jikan: Jikan = JikanImpl()
}

object SharedObject {
    var detailManga: Manga = emptyManga()
    var chapterList: ChapterList = ChapterList()
    var popNotifierCount = 2
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
    var isLoggedIn by rememberSaveable {
        mutableStateOf(false)
    }
    var isShowingSplash by rememberSaveable {
        mutableStateOf(true)
    }
    var executeOnce by rememberSaveable { mutableStateOf(false) }
    var loginCompleted by rememberSaveable { mutableStateOf(false) }
    val sharedViewModel = viewModel { SharedViewModel() }
    val mainViewModel = viewModel { MainViewModel(sharedViewModel) }
    // check for login info
    LaunchedEffect(true) {
        if (!executeOnce) {
            isLoggedIn = isLoggedIn()
            launch {
                Initializer()(postTagSetup = mainViewModel.homeState::setTags)
            }
            delay(util.SPLASH_TIME.toLong())
            isShowingSplash = false
            executeOnce = true
            mainViewModel.undoEdgeToEdge = true
        }
    }
    // perform actions after logged in
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn && !loginCompleted) {
            mainViewModel.homeState.updateUsername()
            loginCompleted = true
        }
    }
    CompositionLocalProvider(LocalSharedViewModel provides sharedViewModel) {
        AppTheme {
            adjustStatusBarColor(darkBeige)
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val screenSize = ScreenSize(height = this.maxHeight, width = this.maxWidth)
                CompositionLocalProvider(LocalScreenSize provides screenSize) {
                    CompositionLocalProvider(LocalWidthClass provides WidthClass(screenSize)) {
                        if (!isLoggedIn) {
                            LoginScreen(onSuccess = {
                                isLoggedIn = true
                            })
                        } else {
                            CompositionLocalProvider(LocalMainViewModel provides mainViewModel) {
                                Navigator(MainScreen())
                            }
                        }
                        if (isShowingSplash) {
                            SplashScreen()
                        } else {
                            adjustStatusBarColor(MaterialTheme.colors.background)
                        }
                    }
                }
            }
        }
    }
}
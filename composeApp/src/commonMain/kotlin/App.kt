
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
import util.KOTTAGE_TOKEN
import view.SplashScreen
import view.LoginScreen
import view.MainScreen
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import io.github.irgaly.kottage.getOrNull
import kotlinx.coroutines.launch
import shared.databaseDir
import shared.adjustStatusBar
import kotlinx.coroutines.delay
import androidx.compose.material.MaterialTheme

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
    val scope = rememberCoroutineScope()
    LaunchedEffect(true) {
        isLoggedIn.value = isLoggedIn()
        delay(2000)
        isShowingSplash.value = false
    }
    AppTheme {
        if (isShowingSplash.value) {
            SplashScreen()
        } else {
            adjustStatusBar(MaterialTheme.colors.onBackground)
            if (!isLoggedIn.value) {
                LoginScreen(onSuccess = {
                    isLoggedIn.value = true
                })
            } else {
                MainScreen()
            }
        }
    }
}
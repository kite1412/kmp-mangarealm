
import androidx.compose.runtime.Composable
import api.mangadex.service.MangaDex
import api.mangadex.service.MangaDexImpl
import api.mangadex.service.TokenHandler
import api.mangadex.service.TokenHandlerImpl
import io.github.irgaly.kottage.Kottage
import io.github.irgaly.kottage.KottageEnvironment
import io.github.irgaly.kottage.platform.KottageContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.ui.tooling.preview.Preview
import shared.kottageContext
import theme.AppTheme
import view.LoginScreen

object Libs {
    val mangaDex: MangaDex = MangaDexImpl()
    val kottage: Kottage = Kottage(
        name = "kottage",
        directoryPath = "/kottage",
        environment = KottageEnvironment(
            context = kottageContext
        ),
        scope = CoroutineScope(Dispatchers.Default)
    )
    val kottageStorage = kottage.storage("default")
    val kottageCache = kottage.cache("default")
}

@Composable
@Preview
fun App() {
    AppTheme {
        LoginScreen(onSuccess = {})
    }
}
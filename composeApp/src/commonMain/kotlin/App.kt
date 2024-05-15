
import androidx.compose.runtime.Composable
import api.mangadex.service.MangaDex
import api.mangadex.service.MangaDexImpl
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.AppTheme
import view.LoginScreen

object Libs {
    val mangaDex: MangaDex = MangaDexImpl()
}

@Composable
@Preview
fun App() {
    AppTheme {
        LoginScreen(onSuccess = {})
    }
}
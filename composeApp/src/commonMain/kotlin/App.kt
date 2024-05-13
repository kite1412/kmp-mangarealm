
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.AppTheme
import view.LoginScreen

@Composable
@Preview
fun App() {
    AppTheme {
        Scaffold {
            LoginScreen()
        }
    }
}
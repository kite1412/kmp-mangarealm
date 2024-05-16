import android.app.Application
import io.github.irgaly.kottage.platform.contextOf

open class MApp : Application() {

    companion object : MApp() {
        val kContext = contextOf(this)
    }
}
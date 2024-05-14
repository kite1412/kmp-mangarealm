package viewmodel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import kotlin.reflect.KClass

class LoginViewModel : ViewModel() {
    object Factory: ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
            return LoginViewModel() as T
        }

        fun create(): LoginViewModel {
            return create(modelClass = LoginViewModel::class, extras = CreationExtras.Empty)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    val pagerState: PagerState
        @Composable get() = rememberPagerState { 2 }
}
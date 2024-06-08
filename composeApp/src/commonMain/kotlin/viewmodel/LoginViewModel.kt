package viewmodel

import Libs
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import api.mangadex.model.request.TokenRequest
import api.mangadex.model.response.Token
import api.mangadex.service.TokenHandler
import io.github.irgaly.kottage.put
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import util.KottageConst
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

    private val mangaDex = Libs.mangaDex
    private val storage = Libs.kottageStorage

    @OptIn(ExperimentalFoundationApi::class)
    val pagerState: PagerState
        @Composable get() = rememberPagerState { 2 }

    private var _username = mutableStateOf("")
    val username = _username

    private var _password = mutableStateOf("")
    val password = _password

    private var _clientId = mutableStateOf("")
    val clientId = _clientId

    private var _clientSecret = mutableStateOf("")
    val clientSecret = _clientSecret

    private var _loading = mutableStateOf(false)
    val loading = _loading

    private var _triggerMessage = mutableStateOf(false)
    val triggerMessage = _triggerMessage

    private var _enableTap = mutableStateOf(false)
    val enableTap = _enableTap

    private var _success = mutableStateOf(false)
    val success = _success

    private fun syncEnableTap() {
        _enableTap.value = username.value.isNotEmpty() &&
            password.value.isNotEmpty() &&
            clientId.value.isNotEmpty() &&
            clientSecret.value.isNotEmpty()
    }

    fun setUsername(new: String) {
        _username.value = new
        syncEnableTap()
    }

    fun setPassword(new: String) {
        _password.value = new
        syncEnableTap()
    }

    fun setClientId(new: String) {
        _clientId.value = new
        syncEnableTap()
    }

    fun setClientSecret(new: String) {
        _clientSecret.value = new
        syncEnableTap()
    }

    private suspend fun store(token: Token) {
        val expiresIn = TokenHandler.expiration(token)
        val refreshExpiresIn = TokenHandler.expiration(token.refreshToken)
        storage.put(
            KottageConst.EXPIRES_IN,
            expiresIn
        )
        storage.put<String>(
            KottageConst.TOKEN,
            token.accessToken
        )
        storage.put<Int>(
            KottageConst.REFRESH_EXPIRES_IN,
            refreshExpiresIn
        )
        storage.put<String>(
            KottageConst.REFRESH_TOKEN,
            token.refreshToken
        )
        storage.put<String>(
            KottageConst.CLIENT_ID,
            _clientId.value
        )
        storage.put<String>(
            KottageConst.CLIENT_SECRET,
            _clientSecret.value
        )
    }

    fun onTap(onSuccess: (Token) -> Unit) {
        _loading.value = true
        _enableTap.value = false
        viewModelScope.launch {
            val token = mangaDex.login(
                TokenRequest(
                    username = username.value,
                    password = password.value,
                    clientId = clientId.value,
                    clientSecret = clientSecret.value
                )
            )
            _loading.value = false
            if (token != null) {
                _success.value = true
                _triggerMessage.value = true
                store(token)
                delay(3000)
                _triggerMessage.value = false
                onSuccess(token)
            } else {
                _enableTap.value = true
                _triggerMessage.value = true
                delay(2000)
                _triggerMessage.value = false
            }
        }
    }
}
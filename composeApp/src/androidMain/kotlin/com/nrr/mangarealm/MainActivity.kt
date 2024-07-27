package com.nrr.mangarealm

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class MainActivity : ComponentActivity() {
    companion object {
        lateinit var c: MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Napier.base(DebugAntilog())
        c = this
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { false }
        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
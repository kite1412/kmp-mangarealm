package view

import LocalSharedViewModel
import SharedObject
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import util.APP_BAR_HEIGHT
import util.appGray
import util.popNoticeDuration
import util.swipeToPop
import view_model.SettingsScreenModel

class SettingsScreen : Screen {
    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val sharedViewModel = LocalSharedViewModel.current
        val sm = rememberScreenModel { SettingsScreenModel(sharedViewModel) }
        val nav = LocalNavigator.currentOrThrow
        LifecycleEffectOnce {
            if (sm.enableSwipeToPop) {
                val count = SharedObject.popNotifierCount--
                if (count > 0) sm.screenModelScope.launch {
                    sm.showPopNotice = true
                    delay(popNoticeDuration)
                    sm.showPopNotice = false
                }
            }
        }
        Scaffold(
            topBar = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(APP_BAR_HEIGHT)
                        .background(MaterialTheme.colors.background)
                        .padding(16.dp)
                ) {
                    Text(
                        "Settings",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            modifier = Modifier.swipeToPop(nav)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                Settings(sm)
                PopNotice(
                    show = sm.showPopNotice,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        }
    }

    @Composable
    private fun Settings(
        sm: SettingsScreenModel,
        modifier: Modifier = Modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsCategory("Theme") {
                ToggleSetting(
                    checked = sm.isDarkMode,
                    settings = "Dark Mode",
                    onCheckedChange = { sm.toggleDarkMode(it) }
                )
            }
            SettingsCategory("Navigation") {
                ToggleSetting(
                    checked = sm.enableSwipeToPop,
                    settings = "Swipe to pop",
                    onCheckedChange = { sm.toggleSwipeToPop(it) }
                )
                SliderSetting(
                    value = sm.swipeSensitivityFactor,
                    settings = "Swipe sensitivity",
                    onValueChange = sm::newSwipeSensitivityFactor,
                    steps = 3,
                    valueRange = 1f..5f,
                    enabled = sm.enableSwipeToPop,
                    onValueChangeFinished = sm::saveSwipeSensitivityFactor
                )
            }
        }
    }

    @Composable
    private fun Header(
        header: String,
        modifier: Modifier = Modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier
        ) {
            val color = appGray()
            Text(
                header,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color)
            )
        }
    }

    @Composable
    fun SettingsCategory(
        category: String,
        modifier: Modifier = Modifier,
        content: @Composable ColumnScope.() -> Unit
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            Header(category)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = content
            )
        }
    }

    @Composable
    private fun SettingName(
        settings: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.typography.body1.color,
        fontWeight: FontWeight = FontWeight.SemiBold,
        fontSize: TextUnit = 16.sp
    ) = Text(
        settings,
        modifier = modifier,
        fontWeight = fontWeight,
        fontSize = fontSize,
        color = color
    )

    @Composable
    private fun ToggleSetting(
        checked: Boolean,
        settings: String,
        modifier: Modifier = Modifier,
        onCheckedChange: (Boolean) -> Unit
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .fillMaxWidth()
                .clickable(indication = null, interactionSource = MutableInteractionSource()) { onCheckedChange(!checked) }
        ) {
            SettingName(settings)
            Toggle(
                checked = checked,
                onToggle = onCheckedChange,
            )
        }
    }

    @Composable
    private fun SliderSetting(
        value: Float,
        settings: String,
        onValueChange: (Float) -> Unit,
        steps: Int,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
        onValueChangeFinished: (() -> Unit)? = null
    ) {
        Column(modifier = modifier) {
            SettingName(
                settings = settings,
                color = MaterialTheme.typography.body1.color.copy(
                    alpha = if (enabled) 1f else 0.5f
                ),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                enabled = enabled,
                onValueChangeFinished = onValueChangeFinished
            )
        }
    }
}
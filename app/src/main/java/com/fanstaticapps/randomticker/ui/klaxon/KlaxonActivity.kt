package com.fanstaticapps.randomticker.ui.klaxon

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.compose.AppTheme
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.extensions.turnScreenOffAndKeyguardOn
import com.fanstaticapps.randomticker.extensions.turnScreenOnAndKeyguardOff
import com.fanstaticapps.randomticker.helper.IntentHelper
import com.fanstaticapps.randomticker.ui.BaseActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class KlaxonActivity : BaseActivity() {

    private val klaxonViewModel: KlaxonViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val bookmark = klaxonViewModel.currentBookmark.observeAsState().value
            if (bookmark != null) {
                val windowSizeClass = calculateWindowSizeClass(this)
                KlaxonView(
                    bookmark,
                    windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact && windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
                )
            }
        }
        klaxonViewModel.currentBookmark.observe(this) {
            if (it != null && it.autoRepeat) {
                lifecycleScope.launch {
                    delay(it.autoRepeatInterval)
                    openMainActivity(it)
                }
            }
        }
        turnScreenOnAndKeyguardOff()
    }

    @Composable
    private fun KlaxonView(bookmark: Bookmark, compactSize: Boolean) {
        AppTheme {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = bookmark.name, style = MaterialTheme.typography.headlineLarge
                )
                val size = if (compactSize) 80.dp else 128.dp
                Box(
                    modifier = Modifier.defaultMinSize(size, size),
                    contentAlignment = Alignment.Center
                ) {
                    Pulsating(size = size)
                    IconButton(onClick = {
                        klaxonViewModel.cancelTimer(this@KlaxonActivity)
                        openMainActivity(bookmark)
                    }) {
                        Icon(
                            modifier = Modifier.size(size),
                            imageVector = Icons.Filled.Close,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentDescription = stringResource(id = android.R.string.cancel)
                        )
                    }
                }
                Button(
                    onClick = { autoRepeatTicker(bookmark) },
                    modifier = Modifier.alpha(if (bookmark.autoRepeat) 0f else 1f)
                ) {
                    Text(
                        text = stringResource(id = R.string.action_repeat)
                    )
                }
            }
        }
    }

    @Composable
    fun Pulsating(size: Dp) {
        val infiniteTransition = rememberInfiniteTransition(label = "Pulsating")

        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 2.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ScalePulsating"
        )

        Box(modifier = Modifier.scale(scale)) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape,
                modifier = Modifier.size(size),
                content = {}
            )
        }
    }

    @Preview(device = Devices.PIXEL_4)
    @Composable
    private fun KlaxonPreview() {
        KlaxonView(
            Bookmark("Test"),
            true
        )
    }

    private fun autoRepeatTicker(bookmark: Bookmark) {
        klaxonViewModel.scheduleTicker(this)
        openMainActivity(bookmark)
    }

    private fun openMainActivity(bookmark: Bookmark) {
        startActivity(IntentHelper.getMainActivity(this, bookmark.id))
        noOpenOrCloseTransitions()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyguardOn()
    }


}
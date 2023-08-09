package com.fanstaticapps.randomticker.ui.klaxon

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
            AppTheme {
                val bookmark = klaxonViewModel.currentBookmark.observeAsState().value
                if (bookmark != null) {
                    val windowSizeClass = calculateWindowSizeClass(this)
                    KlaxonView(
                        bookmark
                    )
                }
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
    private fun KlaxonView(bookmark: Bookmark) {
        val colorList = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colorList))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(modifier = Modifier.weight(0.3f))
            Text(
                text = bookmark.name,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxHeight(0.15f)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                val buttonModifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                Button(
                    modifier = buttonModifier,
                    onClick = {
                        klaxonViewModel.cancelTimer(this@KlaxonActivity)
                        openMainActivity(bookmark)
                    },
                    shape = ButtonDefaults.outlinedShape
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stop,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = stringResource(id = R.string.action_stop)
                    )
                }
                if (!bookmark.autoRepeat) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { autoRepeatTicker(bookmark) },
                        modifier = buttonModifier,
                    ) {
                        Text(
                            text = stringResource(id = R.string.action_repeat)
                        )
                    }
                }
            }
        }
    }

    @Preview(uiMode = UI_MODE_NIGHT_NO)
    @Preview(uiMode = UI_MODE_NIGHT_YES)
    @Composable
    private fun KlaxonPreview() {
        AppTheme {
            KlaxonView(
                Bookmark("Test")
            )
        }
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
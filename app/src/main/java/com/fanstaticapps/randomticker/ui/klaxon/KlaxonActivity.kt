package com.fanstaticapps.randomticker.ui.klaxon

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.activity.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.databinding.ActivityKlaxonBinding
import com.fanstaticapps.randomticker.extensions.turnScreenOffAndKeyguardOn
import com.fanstaticapps.randomticker.extensions.turnScreenOnAndKeyguardOff
import com.fanstaticapps.randomticker.extensions.viewBinding
import com.fanstaticapps.randomticker.helper.IntentHelper
import com.fanstaticapps.randomticker.helper.TimerHelper
import com.fanstaticapps.randomticker.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class KlaxonActivity : BaseActivity() {

    @Inject
    lateinit var timerHelper: TimerHelper

    private val viewModel: KlaxonViewModel by viewModels()
    private val binding by viewBinding(ActivityKlaxonBinding::inflate)

    private val pulsatorAnimation = AnimatorSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setBackgroundDrawableResource(R.drawable.klaxon_background)
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        binding.prepareView()
        prepareAnimation()

        viewModel.currentBookmark.observe(this) {
            pulsatorAnimation.start()
            if (it.autoRepeat) {
                lifecycleScope.launch {
                    delay(2000)
                    autoRepeatTicker(it)
                }
            }
        }
        turnScreenOnAndKeyguardOff()
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyguardOn()
    }


    private fun prepareAnimation() {
        val imageXAnimator = ObjectAnimator.ofFloat(binding.ivPulsatorDismiss, "scaleX", 3f)
        val imageYAnimator = ObjectAnimator.ofFloat(binding.ivPulsatorDismiss, "scaleY", 3f)
        val imageAlphaAnimator = ObjectAnimator.ofFloat(binding.ivPulsatorDismiss, "alpha", 0.6f)
        val animators = listOf(imageXAnimator, imageYAnimator, imageAlphaAnimator)

        pulsatorAnimation.apply {
            playTogether(imageXAnimator, imageYAnimator, imageAlphaAnimator)
            duration = 1800
            interpolator = FastOutSlowInInterpolator()
        }
        animators.forEach { animator ->
            animator.repeatCount = ObjectAnimator.INFINITE
            animator.repeatMode = ObjectAnimator.REVERSE
        }
    }

    private fun ActivityKlaxonBinding.prepareView() {
        btnDismiss.setOnClickListener {
            viewModel.currentBookmark.value?.let { bookmark ->
                timerHelper.cancelTicker(bookmark.id)
                openMainActivity(bookmark)
            }
            pulsatorAnimation.end()
        }

        btnRepeat.setOnClickListener {
            viewModel.currentBookmark.observe(this@KlaxonActivity) {
                autoRepeatTicker(it)
            }
        }
    }

    private fun autoRepeatTicker(bookmark: Bookmark) {
        timerHelper.startTicker(bookmark)
        openMainActivity(bookmark)
    }

    private fun openMainActivity(bookmark: Bookmark) {
        startActivity(IntentHelper.getMainActivity(this, bookmark.id))
        noOpenOrCloseTransitions()
        finish()
    }


}
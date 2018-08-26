package com.fanstaticapps.sequentialtimer

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import com.fanstaticapps.common.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity(), MainView {

    @Inject
    lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAdd.setOnClickListener {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                presenter.addCondition()
            }
        }
    }

    override fun showConditionDialog(conditions: List<PossibleCondition>) {
    }

    fun passCondition(condition: Condition) {
        presenter.createCondition(condition)
    }

}

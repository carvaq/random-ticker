package com.fanstaticapps.sequentialtimer

import com.fanstaticapps.sequentialtimer.PossibleCondition.*

class MainPresenter(private val view: MainView) {

    private val conditions = mutableListOf<Condition>()

    fun addCondition() {
        view.showConditionDialog(RANDOM_INTERVAL, EXACT_INTERVAL, NUMBER_OF_TIMES)
    }

    fun selectedCondition(condition: Condition) {
        conditions.add(condition)

    }
}
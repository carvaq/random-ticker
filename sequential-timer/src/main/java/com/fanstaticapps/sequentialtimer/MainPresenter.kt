package com.fanstaticapps.sequentialtimer

import com.fanstaticapps.sequentialtimer.PossibleCondition.*

class MainPresenter(private val view: MainView) {

    private val conditionBuilder = mutableListOf<Condition>()


    fun addCondition() {
        view.showConditionDialog(listOf(RANDOM_INTERVAL, EXACT_INTERVAL, NUMBER_OF_TIMES))
    }

    fun selectedCondition(index: Int) {

        view.showConditionDialog()
    }

    fun createCondition(condition: Condition) {
        conditionBuilder.add(condition)
    }
}
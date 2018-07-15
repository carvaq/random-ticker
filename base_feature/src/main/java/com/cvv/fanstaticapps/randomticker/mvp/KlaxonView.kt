package com.cvv.fanstaticapps.randomticker.mvp

interface KlaxonView {
    fun render(viewState: KlaxonPresenter.ViewState)
}
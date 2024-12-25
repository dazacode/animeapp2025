package com.kawaidev.kawaime.ui.adapters.explore.helpers

data class ActionItem(
    val label: String,
    val iconResId: Int,
    val backgroundColorResId: Int,
    val tintColorResId: Int,
    val onClick: () -> Unit
)
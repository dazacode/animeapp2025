package com.kawaidev.kawaime.ui.fragments.details.helpers

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.fragments.details.DetailsFragment

object DetailsHelper {
    fun showImage(fragment: DetailsFragment) {
        fragment.image.visibility = View.VISIBLE
        val constraintSet = ConstraintSet()
        constraintSet.clone(fragment.requireView().findViewById<ConstraintLayout>(R.id.parentLayout))
        constraintSet.connect(
            R.id.recycler,
            ConstraintSet.START,
            R.id.imageContainer,
            ConstraintSet.END
        )
        constraintSet.applyTo(fragment.requireView().findViewById(R.id.parentLayout))
    }

    fun hideImage(fragment: DetailsFragment) {
        fragment.image.visibility = View.GONE
        val constraintSet = ConstraintSet()
        constraintSet.clone(fragment.requireView().findViewById<ConstraintLayout>(R.id.parentLayout))
        constraintSet.connect(
            R.id.recycler,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START
        )
        constraintSet.applyTo(fragment.requireView().findViewById(R.id.parentLayout))
    }
}
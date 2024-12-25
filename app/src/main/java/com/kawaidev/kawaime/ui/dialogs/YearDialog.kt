package com.kawaidev.kawaime.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.activity.MainActivity
import java.util.Calendar

class YearDialog : DialogFragment() {

    private var onResetListener: (() -> Unit)? = null
    private var onApplyListener: ((String, String) -> Unit)? = null

    private val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)
    private var initialStartYear: Int = currentYear
    private var initialEndYear: Int = currentYear

    fun setOnResetListener(listener: () -> Unit) {
        onResetListener = listener
    }

    fun setOnApplyListener(listener: (String, String) -> Unit) {
        onApplyListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_layout_date, null)

        val negativeButton = dialogView.findViewById<Button>(R.id.reset_button)
        val positiveButton = dialogView.findViewById<Button>(R.id.apply_button)

        val startDatePicker = dialogView.findViewById<NumberPicker>(R.id.startDate)
        val endDatePicker = dialogView.findViewById<NumberPicker>(R.id.endDate)

        startDatePicker.minValue = 1917
        startDatePicker.maxValue = currentYear
        startDatePicker.value = initialStartYear
        startDatePicker.wrapSelectorWheel = true

        endDatePicker.minValue = 1917
        endDatePicker.maxValue = currentYear
        endDatePicker.value = initialEndYear
        endDatePicker.wrapSelectorWheel = true

        startDatePicker.setOnValueChangedListener { _, _, newVal ->
            endDatePicker.minValue = newVal
        }

        endDatePicker.setOnValueChangedListener { _, _, newVal ->
            startDatePicker.maxValue = newVal
        }

        negativeButton.setOnClickListener {
            onResetListener?.invoke()
            dismiss()
        }

        positiveButton.setOnClickListener {
            val startYear = startDatePicker.value
            val endYear = endDatePicker.value

            if (startYear <= endYear) {
                onApplyListener?.invoke("$startYear", "$endYear")
                dismiss()
            } else {
                (requireActivity() as MainActivity).dialogs.onError("Start year must be less than or equal to end year")
            }
        }

        val builder = AlertDialog.Builder(context, R.style.CustomDialogStyle)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 48))

        return dialog
    }

    fun setInitialYears(startYear: Int, endYear: Int) {
        initialStartYear = startYear
        initialEndYear = endYear
    }

    companion object {
        fun newInstance(): YearDialog {
            return YearDialog()
        }
    }
}
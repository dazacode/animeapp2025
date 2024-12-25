package com.kawaidev.kawaime.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.kawaidev.kawaime.R

class LoadingDialog : DialogFragment() {

    private val handler = Handler(Looper.getMainLooper())
    private var isViewVisible = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialogStyle)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_layout_loading, null)

        val aBitLong: TextView = view.findViewById(R.id.aBitLong)
        aBitLong.visibility = View.GONE

        builder.setView(view)
        builder.setCancelable(false)

        val dialog = builder.create()

        dialog?.window?.setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 48))

        handler.postDelayed({
            if (isViewVisible) {
                aBitLong.visibility = View.VISIBLE
            }
        }, 5000)

        return dialog
    }

    override fun onStart() {
        super.onStart()
        isViewVisible = true
    }

    override fun onStop() {
        super.onStop()
        isViewVisible = false
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        fun newInstance(): LoadingDialog {
            return LoadingDialog()
        }
    }
}
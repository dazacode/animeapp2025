package com.kawaidev.kawaime.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.kawaidev.kawaime.R

class LoadingDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialogStyle)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_layout_loading, null)

        builder.setView(view)
        builder.setCancelable(false)

        val dialog = builder.create()

        dialog?.window?.setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 48))

        return dialog
    }

    companion object {
        fun newInstance(): LoadingDialog {
            return LoadingDialog()
        }
    }
}
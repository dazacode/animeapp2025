package com.kawaidev.kawaime.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.objects.HelpMessages

class HelpDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialogStyle)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_layout_help, null)

        val close: FrameLayout = view.findViewById(R.id.negative_button)

        val help: TextView = view.findViewById(R.id.helpDesc)

        val formattedText = """
            <b>${HelpMessages.ANIME_NOT_LOADED_TITLE}</b><br><br>
            ${HelpMessages.CHECK_INTERNET_CONNECTION}
            ${HelpMessages.RESTART_APP}
            ${HelpMessages.CLEAR_CACHE}
            ${HelpMessages.SERVER_ISSUES}
            ${HelpMessages.UPDATE_APP}
            ${HelpMessages.CONTACT_SUPPORT}
            ${HelpMessages.FINAL_MESSAGE}
            """

        val styledText = Html.fromHtml(formattedText, Html.FROM_HTML_MODE_LEGACY)
        help.text = styledText


        builder.setView(view)
        builder.setCancelable(true)

        val dialog = builder.create()

        dialog?.window?.setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 48))

        close.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    companion object {
        fun newInstance(): HelpDialog {
            return HelpDialog()
        }
    }
}
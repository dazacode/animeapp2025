package com.kawaidev.kawaime.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.kawaidev.kawaime.R

class QualityDialog : DialogFragment() {

    private var onQualitySelected: ((String) -> Unit)? = null

    private var selectedQuality: String? = null
    private var qualities: Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        qualities = resources.getStringArray(R.array.qualities) // Replace with your qualities array
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialogStyle)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_layout_quality, null)

        val listView: ListView = view.findViewById(R.id.list)
        val okButton: Button = view.findViewById(R.id.ok_button)
        val cancelButton: Button = view.findViewById(R.id.cancel_button)

        // Setup the adapter for radio buttons
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.list_item,
            qualities ?: emptyArray()
        )

        val defaultIndex = qualities?.indexOf(selectedQuality) ?: -1
        if (defaultIndex >= 0) {
            listView.setItemChecked(defaultIndex, true)
        }

        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        listView.setOnItemClickListener { _, _, position, _ ->
            selectedQuality = qualities?.get(position)
        }

        okButton.setOnClickListener {
            if (selectedQuality != null) {
                onQualitySelected?.invoke(selectedQuality ?: "1080p")
                dismiss()
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }

        builder.setView(view)
        return builder.create()
    }

    fun setOnQualitySelectedListener(listener: (String) -> Unit) {
        onQualitySelected = listener
    }

    companion object {
        fun newInstance(): QualityDialog {
            return QualityDialog()
        }
    }
}
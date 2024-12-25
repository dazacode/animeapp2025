package com.kawaidev.kawaime.ui.adapters.filter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.api_utils.SearchParams
import com.kawaidev.kawaime.ui.adapters.filter.helpers.FilterEnum
import com.kawaidev.kawaime.ui.adapters.filter.helpers.FilterItem
import com.kawaidev.kawaime.ui.adapters.filter.helpers.FilterType
import com.kawaidev.kawaime.ui.fragments.filter.FilterFragment

class FilterAdapter(
    private val fragment: FilterFragment,
    private val filters: MutableList<FilterItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class FilterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textInputLayout: TextInputLayout = view.findViewById(R.id.textInputLayout)
        val autoCompleteTextView: MaterialAutoCompleteTextView = textInputLayout.editText as MaterialAutoCompleteTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_filter, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val filter = filters[position]

        if (holder is FilterViewHolder) {
            holder.textInputLayout.hint = filter.filterEnum.label

            if (filter.type == FilterType.DEFAULT) {
                val adapter = ArrayAdapter(holder.itemView.context, R.layout.simple_dropdown_item, filter.filterEnum.getList())
                holder.autoCompleteTextView.setAdapter(adapter)
            } else {
                holder.textInputLayout.isEnabled = false
                holder.textInputLayout.setEndIconDrawable(R.drawable.arrow_drop_down)

                val clickableOverlay = holder.itemView.findViewById<View>(R.id.clickableOverlay)
                clickableOverlay.visibility = View.VISIBLE

                clickableOverlay.setOnClickListener {
                    filter.onClick(holder.autoCompleteTextView)
                }
            }

            holder.autoCompleteTextView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                    filter.selectedValue = charSequence.toString()
                    fragment.saveFilters(filters)
                }

                override fun afterTextChanged(editable: Editable?) {}
            })

            holder.autoCompleteTextView.setText(if (filter.selectedValue != "All") filter.selectedValue else filter.defaultValue, false)

            if (filter.desc.isNotEmpty()) {
                holder.itemView.findViewById<TextView>(R.id.helperText).apply {
                    text = filter.desc
                    visibility = View.VISIBLE
                }
            } else holder.itemView.findViewById<TextView>(R.id.helperText).visibility = View.GONE
        }
    }

    override fun getItemCount() = filters.size

    fun getParams(): SearchParams {
        val filterMap = mutableMapOf<String, String?>()

        for (filterItem in filters) {
            when (filterItem.filterEnum) {
                FilterEnum.TYPE -> if (filterItem.selectedValue != "All") filterMap["type"] = filterItem.selectedValue
                    .lowercase()
                    .replace(" ", "-")
                FilterEnum.STATUS -> if (filterItem.selectedValue != "All") filterMap["status"] = filterItem.selectedValue
                    .lowercase()
                    .replace(" ", "-")
                FilterEnum.RATED -> if (filterItem.selectedValue != "All") filterMap["rated"] = filterItem.selectedValue
                    .lowercase()
                    .replace(" ", "-")
                FilterEnum.SCORE -> if (filterItem.selectedValue != "All") filterMap["score"] = filterItem.selectedValue
                    .lowercase()
                    .replace(" ", "-")
                FilterEnum.SEASON -> if (filterItem.selectedValue != "All") filterMap["season"] = filterItem.selectedValue
                    .lowercase()
                    .replace(" ", "-")
                FilterEnum.LANGUAGE -> if (filterItem.selectedValue != "All") filterMap["language"] = filterItem.selectedValue
                    .lowercase()
                    .replace(" ", "-")
                FilterEnum.DATE -> {
                    val dateRange = filterItem.selectedValue?.takeIf { it != "All" }?.split(" - ")
                    dateRange?.getOrNull(0)?.let { filterMap["start_date"] = "$it-1-1" }
                    dateRange?.getOrNull(1)?.let { filterMap["end_date"] = "$it-12-31" }
                }
                FilterEnum.SORT -> if (filterItem.selectedValue != "All") filterMap["sort"] = filterItem.selectedValue
                    .lowercase()
                    .replace(" ", "-")
                FilterEnum.GENRES -> {
                    filterItem.selectedValue
                        .takeIf { it != "All" }
                        ?.lowercase()
                        ?.replace(" ", "")
                        ?.let { filterMap["genres"] = it }
                }
            }
        }

        return SearchParams(
            type = filterMap["type"],
            status = filterMap["status"],
            rated = filterMap["rated"],
            score = filterMap["score"],
            season = filterMap["season"],
            language = filterMap["language"],
            start_date = filterMap["start_date"],
            end_date = filterMap["end_date"],
            sort = filterMap["sort"],
            genres = filterMap["genres"]
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reset() {
        filters.forEach { filterItem ->
            filterItem.selectedValue = "All"
        }

        notifyDataSetChanged()
    }

    fun getFilters(): List<FilterItem> {
        return filters
    }
}
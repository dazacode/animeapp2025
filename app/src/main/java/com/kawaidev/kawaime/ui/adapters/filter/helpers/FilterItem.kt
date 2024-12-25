package com.kawaidev.kawaime.ui.adapters.filter.helpers

import com.google.android.material.textfield.MaterialAutoCompleteTextView
import android.os.Parcel
import android.os.Parcelable

data class FilterItem(
    val filterEnum: FilterEnum,
    val type: FilterType,
    var desc: String = "",
    var selectedValue: String = "All",
    var defaultValue: String = "All",
    val pairedWith: FilterEnum? = null,
    val onClick: (view: MaterialAutoCompleteTextView) -> Unit = {}
) : Parcelable {
    constructor(parcel: Parcel) : this(
        filterEnum = FilterEnum.valueOf(parcel.readString()!!),
        type = FilterType.valueOf(parcel.readString()!!),
        desc = parcel.readString()!!,
        selectedValue = parcel.readString()!!,
        defaultValue = parcel.readString()!!,
        pairedWith = parcel.readString()?.let { FilterEnum.valueOf(it) }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(filterEnum.name)
        parcel.writeString(type.name)
        parcel.writeString(desc)
        parcel.writeString(selectedValue)
        parcel.writeString(defaultValue)
        parcel.writeString(pairedWith?.name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FilterItem> {
        override fun createFromParcel(parcel: Parcel): FilterItem {
            return FilterItem(parcel)
        }

        override fun newArray(size: Int): Array<FilterItem?> {
            return arrayOfNulls(size)
        }
    }
}

enum class FilterType {
    DEFAULT,
    CUSTOM
}
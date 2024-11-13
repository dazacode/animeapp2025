package com.kawaidev.kawaime.ui.dialogs

import com.kawaidev.kawaime.ui.activity.MainActivity

class Dialogs(private val activity: MainActivity) {
    private var loadingDialog: LoadingDialog? = null

    fun onLoading(loading: Boolean) {
        val fragmentManager = activity.supportFragmentManager
        when (loading) {
            true -> {
                if (loadingDialog == null) {
                    loadingDialog = LoadingDialog.newInstance()
                    loadingDialog?.show(fragmentManager, "LoadingDialog")
                }
            }
            false -> {
                loadingDialog?.dismiss()
                loadingDialog = null
            }
        }
    }
}

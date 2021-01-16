package com.app.kgs_user.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import com.app.kgs_user.R

class LoadingDialog(internal var context: Context) {

    private var dialog: Dialog = Dialog(this.context)

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.progress_layout)
        val window = dialog.window
        window!!.setGravity(Gravity.CENTER)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setCancelable(false)
    }

    fun showDialog() {
        dialog.show()
    }

    fun hideDialog() {
        dialog.dismiss()
    }
}

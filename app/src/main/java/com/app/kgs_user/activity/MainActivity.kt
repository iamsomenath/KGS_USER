package com.app.kgs_user.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.app.kgs_user.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun showBackPress() {
        MaterialDialog.Builder(this)
            .title(resources.getString(R.string.dialogTitle_exit))
            .content(resources.getString(R.string.dialogMessage_exit))
            .positiveText(resources.getString(R.string.dialogPositiveButtonText_logout))
            .positiveColor(ContextCompat.getColor(this, R.color.button_and_vespac_red_color))
            .negativeText(resources.getString(R.string.dialogPositiveButtonText_cancel))
            .negativeColor(ContextCompat.getColor(this, R.color.colorTranslucentButton))
            .onPositive { dialog, which ->
                finishAffinity()
            }
            .cancelable(false)
            .onNegative { dialog, which -> dialog.dismiss() }.show()
    }

    @SuppressLint("MissingPermission")
    override fun onBackPressed() {
        showBackPress()
    }
}

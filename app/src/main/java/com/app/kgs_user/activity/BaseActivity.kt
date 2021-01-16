package com.app.kgs_user.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText


abstract class BaseActivity : AppCompatActivity() {

    private val loadingType: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    abstract fun initFields()

    abstract fun setupOnClick()

    fun setupUI(view: View, activity: AppCompatActivity) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is TextInputEditText) {
            view.setOnTouchListener { v, event ->
                hideSoftKeyboard(activity)
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView, activity)
            }
        }
    }

    companion object {

        fun hideSoftKeyboard(activity: Activity) {
            val focusedView = activity.currentFocus
            if (focusedView != null) {
                val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(focusedView.windowToken, 0)
            }
        }
    }
}

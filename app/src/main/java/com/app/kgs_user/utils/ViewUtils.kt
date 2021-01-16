package com.app.kgs_user.utils

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun ProgressBar.hide() {
    visibility = View.GONE
}

fun View.snackbar(message: String) {
    //Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    val sbView = snackbar.view
    sbView.setBackgroundColor(Color.parseColor("#fb4629"))
    snackbar.show()
}

fun ImageView.loadUrl(url: String) {
    Picasso.get().load(url).into(this)
}

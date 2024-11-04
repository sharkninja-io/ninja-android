package com.sharkninja.ninja.connected.kitchen.ext

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.enums.MenuItem

fun TextView.updateIconLabelsDefault(context: Context, selection: MenuItem) {
    this.typeface = ResourcesCompat.getFont(context, selection.textStyle.fontStyle)
    this.setTextColor(ContextCompat.getColor(context, selection.textStyle.colorDefault))
}

fun TextView.updateIconLabelsDark(context: Context, selection: MenuItem) {
    this.typeface = ResourcesCompat.getFont(context, selection.textStyle.fontStyle)
    this.setTextColor(ContextCompat.getColor(context, selection.textStyle.colorDark))
}

fun TextView.updateIconLabelFontAndColor(context: Context, color: Int) {
    this.typeface = ResourcesCompat.getFont(context, R.font.gotham_book_normal)
    this.setTextColor(ContextCompat.getColor(context, color))
}

fun ImageView.setIconTintColor(context: Context, color: Int) {
    this.setColorFilter(ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.SRC_IN)
}

fun CardView.setBGColor(context: Context, color: Int) {
    this.setCardBackgroundColor(ContextCompat.getColor(context, color))
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.enable() {
    this.isEnabled = true
}

fun View.disable() {
    this.isEnabled = false
}

fun View.hideSoftInput() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}
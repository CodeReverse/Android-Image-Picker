package com.dennis.imagepicker.model

import android.view.View


interface OnItemClickListener {
    fun onItemClick(view: View, position: Int)
}
package com.example.pennydrop4

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("isHidden")
fun bindIsHidden(view: View, isInvisible: Boolean) {
    view.visibility = if (isInvisible) View.INVISIBLE else View.VISIBLE
}

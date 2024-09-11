package com.example.eventapp.extension

import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.eventapp.R
import com.example.eventapp.service.dataclass.Image

fun List<Image>.getImageByRatio(ratio: ImageEnum): String {
    val filteredImage = this.filter { it.ratio == ratio.value }.maxByOrNull { it.width }
    return filteredImage?.url ?: ""
}

fun ImageView.loadImage(image: String) {
    Glide.with(this.context).load(image).placeholder(R.drawable.app_logo).into(this)
}

fun Fragment.openFragment(fragment: Fragment, containerId: Int) {
    parentFragmentManager.beginTransaction()
        .replace(containerId, fragment)
        .addToBackStack(null)
        .commit()
}

fun AppCompatActivity.openFragment(fragment: Fragment, containerId: Int) {
    supportFragmentManager.beginTransaction()
        .replace(containerId, fragment)
        .addToBackStack(null)
        .commit()
}
package com.example.eventapp.extension

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
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
    Glide.with(this.context).load(image).placeholder(R.drawable.image).into(this)
}

fun ImageView.setFavoriteIcon(isFav: Boolean) {
    this.setImageResource(if (isFav) R.drawable.heart_color else R.drawable.heart)
}

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun AppCompatActivity.openFragment(fragment: Fragment, containerId: Int) {
    supportFragmentManager.beginTransaction()
        .replace(containerId, fragment)
        .addToBackStack(null)
        .commit()
}


fun View.visibilityGone() {
    visibility = View.GONE
}

fun View.visibilityVisible() {
    visibility = View.VISIBLE
}
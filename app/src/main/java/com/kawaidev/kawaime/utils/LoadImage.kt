package com.kawaidev.kawaime.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import jp.wasabeef.glide.transformations.BlurTransformation

class LoadImage {
    @SuppressLint("CheckResult")
    fun loadImage(
        context: Context,
        imageUrl: String?,
        imageView: ImageView,
        animateOnLoad: Boolean = true,
        animationDuration: Int = 250,
        originalSize: Boolean = false,
        blur: Int = 0
    ) {
        imageUrl?.let {
            val glideRequest = Glide.with(context)
                .load(it)
                .diskCacheStrategy(DiskCacheStrategy.ALL)

            if (animateOnLoad) {
                glideRequest.transition(DrawableTransitionOptions.withCrossFade(animationDuration))
            }

            if (originalSize) {
                glideRequest.apply(RequestOptions().override(Target.SIZE_ORIGINAL))
            }

            if (blur != 0) {
                glideRequest.apply(RequestOptions.bitmapTransform(BlurTransformation(blur)))
            }

            glideRequest.into(imageView)
        }
    }
}
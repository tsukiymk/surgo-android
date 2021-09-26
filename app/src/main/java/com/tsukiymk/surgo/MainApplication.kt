package com.tsukiymk.surgo

import android.app.Application
import app.surgo.common.compose.utils.FormatTextInterceptor
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.annotation.ExperimentalCoilApi
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application(), ImageLoaderFactory {
    @OptIn(ExperimentalCoilApi::class)
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .componentRegistry {
                add(FormatTextInterceptor)
            }
            .build()
    }
}

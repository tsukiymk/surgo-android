package com.tsukiymk.surgo

import android.app.Application
import android.os.Build
import android.os.StrictMode
import app.surgo.common.compose.utils.FormatTextInterceptor
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.annotation.ExperimentalCoilApi
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application(), ImageLoaderFactory {
    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build()
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        detectUnsafeIntentLaunch()
                    }
                }
                .penaltyLog()
                .build()
        )
    }

    override fun onCreate() {
        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }

        super.onCreate()
    }

    @OptIn(ExperimentalCoilApi::class)
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .componentRegistry {
                add(FormatTextInterceptor)
            }
            .build()
    }
}

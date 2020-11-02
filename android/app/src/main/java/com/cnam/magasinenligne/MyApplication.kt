package com.cnam.magasinenligne

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Point
import android.os.StrictMode
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.multidex.MultiDexApplication
import com.cnam.magasinenligne.api.models.Client
import com.cnam.magasinenligne.api.models.Vendeur

class MyApplication : MultiDexApplication() {

    companion object {
        lateinit var editor: SharedPreferences.Editor
        lateinit var shared: SharedPreferences
        var errorTimeLeft = 0L
        var screenWidth: Int = 0
        var screenHeight: Int = 0
        lateinit var clientProfile: Client
        lateinit var merchantProfile: Vendeur
        fun isClient() = this::clientProfile.isInitialized
        fun isMerchant() = this::merchantProfile.isInitialized
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        shared = applicationContext.getSharedPreferences("default", Context.MODE_PRIVATE)
        editor = shared.edit()
    }

    private fun getScreenDimensions() {
        val wm = applicationContext
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay

        val point = Point()
        display.getSize(point)
        screenWidth = point.x
        screenHeight = point.y
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)

    }
}
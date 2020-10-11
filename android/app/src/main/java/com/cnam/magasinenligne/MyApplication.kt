package com.cnam.magasinenligne

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.StrictMode
import androidx.multidex.MultiDexApplication

class MyApplication : MultiDexApplication() {

    companion object {
        lateinit var editor: SharedPreferences.Editor
        lateinit var shared: SharedPreferences
        var errorTimeLeft = 0L
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        shared = applicationContext.getSharedPreferences("default", Context.MODE_PRIVATE)
        editor = shared.edit()
    }
}
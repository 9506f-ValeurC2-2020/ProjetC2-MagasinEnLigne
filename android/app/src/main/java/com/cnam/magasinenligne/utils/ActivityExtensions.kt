package com.cnam.magasinenligne.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.fragments.landing.AccountFragment
import com.cnam.magasinenligne.fragments.landing.HomeFragment
import com.cnam.magasinenligne.fragments.landing.ShopFragment
import com.yalantis.ucrop.UCrop

fun FragmentManager.makeTransaction(
    fragment: Fragment,
    tag: String,
    animationEnter: Int? = null,
    animationExit: Int? = null
) {

    val transaction = if (animationExit != null && animationEnter != null) this.beginTransaction()
        .setCustomAnimations(animationEnter, 0, 0, animationExit) else this.beginTransaction()

    transaction.replace(
        R.id.fl_container,
        fragment,
        tag
    )
        .addToBackStack(null)
        .commit()
}

fun FragmentManager.addTransaction(
    fragment: Fragment,
    tag: String,
    animationEnter: Int? = null,
    animationExit: Int? = null
) {

    val transaction = if (animationExit != null && animationEnter != null) this.beginTransaction()
        .setCustomAnimations(animationEnter, 0, 0, animationExit) else this.beginTransaction()
    transaction.add(
        R.id.fl_container,
        fragment,
        tag
    )
        .addToBackStack(null)
        .commit()
}

fun verifyPermissions(activity: Activity, request: Int, permissions: Array<String>): Boolean {
    var granted = true
    for (element in permissions) {
        val permission = ActivityCompat.checkSelfPermission(activity, element)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            granted = false
        }
    }
    if (!granted) ActivityCompat.requestPermissions(activity, permissions, request)
    return granted
}

fun checkPermissions(activity: Activity, request: Int, permissions: Array<String>): Boolean {
    var granted = true
    for (element in permissions) {
        val permission = ActivityCompat.checkSelfPermission(activity, element)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            granted = false
        }
    }
    return granted
}

fun Context.toast(message: String): Toast {
    val myToast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
    myToast.show()
    return myToast
}

fun Activity.startCameraCapture(uri: Uri) {
    val hasCamera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    if (!hasCamera) {
        toast("No camera to record")
    } else {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            it.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            it.putExtra("return-data", true)
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivityForResult(intent, 4000)
    }
}

fun Activity.createDialog(title: String, message: String): AlertDialog.Builder {
    return AlertDialog.Builder(this).also {
        it.setTitle(title)
        it.setMessage(message)
        it.setCancelable(true)
    }
}

fun Activity.hideKeyboard() {
    val manager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
}

fun Activity.showKeyboard(v: View) {
    val manager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
}

fun Activity.startCrop(sourceUri: Uri, destinationUri: Uri) {
    val options = UCrop.Options()
    options.setHideBottomControls(true)
    options.withMaxResultSize(640, 640)
    UCrop.of(sourceUri, destinationUri)
        .withAspectRatio(1f, 1f)
        .withOptions(options)
        .start(this)
}

fun FragmentManager.getActiveFragmentTag(): String {
    val fragment = this.findFragmentById(R.id.fl_container)
    return if (fragment != null) {
        when (fragment) {
            is HomeFragment -> "home"
            is ShopFragment -> "shop"
            is AccountFragment -> "account"
            else -> ""
        }
    } else ""
}

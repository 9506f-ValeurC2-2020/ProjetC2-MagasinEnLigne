package com.cnam.magasinenligne.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.fade(vararg args: EditText, fadeIn: Boolean = false) {
    val objectAnimator = if (!fadeIn) ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
    else ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
    objectAnimator.duration = 500
    objectAnimator.interpolator = DecelerateInterpolator()

    objectAnimator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            if (fadeIn) {
                this@fade.show()
                this@fade.alpha = 1f
            } else {
                this@fade.hide()
                this@fade.alpha = 1f
            }

            if (args.isNotEmpty()) {
                for (e in args) {
                    e.isEnabled = true
                }
            }
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
            if (fadeIn) this@fade.show()
            if (args.isNotEmpty()) {
                for (e in args) {
                    e.isEnabled = false
                    Log.d("edit", e.toString())
                }
            }
        }

    })
    objectAnimator.start()
}

fun View.vanish() {
    this.visibility = View.INVISIBLE
}

fun ViewGroup.show() {
    this.visibility = View.VISIBLE
}

fun View.isVisible(): Boolean = this.visibility == View.VISIBLE

fun ViewGroup.hide() {
    this.visibility = View.GONE
}

fun ViewGroup.lockView(isLock: Boolean) {
    if (isLock) {
        this.show()
    } else {
        this.hide()
    }
}

fun TextView.setTimer(sec: Int, progressBar: ProgressBar): CountDownTimer {
    val finish = "Tap to record"
    return object : CountDownTimer((sec * 1000).toLong(), 1000) {
        @SuppressLint("ObjectAnimatorBinding")
        override fun onTick(millisUntilFinished: Long) {

            val initialProgress = progressBar.progress
            val progress = initialProgress + 1

            progressBar.progress = progress

            val second = (millisUntilFinished / 1000).toInt() % 60
            val minutes = (millisUntilFinished / (1000 * 60) % 60).toInt()

            val time = String.format("%02d:%02d", minutes, second)

            text = time


        }

        override fun onFinish() {
            text = finish
            progressBar.progress = 0
        }
    }
}

fun TextView.countDown(timeInMin: Int, textView: TextView? = null): CountDownTimer {
    val finish = "Resend Code"
    textView?.hide()
    return object : CountDownTimer((timeInMin * 60 * 1000).toLong(), 1000) {
        @SuppressLint("ObjectAnimatorBinding")
        override fun onTick(millisUntilFinished: Long) {


            val second = (millisUntilFinished / 1000).toInt() % 60
            val time = String.format("%02d", second)

            text = time


        }

        override fun onFinish() {
            text = finish
            textView?.show()
        }
    }
}

fun EditText.goOnClick(target: View) {
    this.setOnEditorActionListener { _, actionId, event ->
        if (event != null && (event.action == actionId)) {
            target.performClick()
        }
        false
    }
}


fun View.showSnack(message: String) {
    val color: Int = Color.WHITE
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setTextColor(color).show()
}

fun View.disableView() {
    alpha = 0.5f
    isEnabled = false
}

fun View.enableView() {
    alpha = 1f
    isEnabled = true
}


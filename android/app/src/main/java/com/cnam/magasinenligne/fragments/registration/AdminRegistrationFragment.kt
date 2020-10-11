package com.cnam.magasinenligne.fragments.registration

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.cnam.magasinenligne.MyApplication
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.activities.RegistrationActivity
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.utils.createDialog
import com.cnam.magasinenligne.utils.hide
import com.cnam.magasinenligne.utils.logDebug
import com.cnam.magasinenligne.utils.show
import kotlinx.android.synthetic.main.fragment_admin_registration.*

class AdminRegistrationFragment : BaseFragment() {
    private var errorCount = 0
    private val errorCountDownTimer by lazy { countDownError(30000) }
    private lateinit var globalErrorCountDownTimer: CountDownTimer

    /**
     * OnBackPressedCallback
     */
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity!!.finish()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_registration, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        listeners()
        logDebug("MyApplication error count left is => ${MyApplication.errorTimeLeft}")
        if (MyApplication.errorTimeLeft != 0L) {
            pv_secret.isEnabled = false
            globalErrorCountDownTimer = countDownError(MyApplication.errorTimeLeft)
            globalErrorCountDownTimer.start()
            tv_error_counter.show()
        }
    }

    private fun listeners() {
        pv_secret.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 5) {
                    val code = s.toString()
                    if (code == getString(R.string.admin_code)) { // this is an admin
                        (activity!! as RegistrationActivity).login("admin", "")
                    } else {
                        errorCount++
                        if (errorCount == 5) {
                            errorCountDownTimer.start()
                            pv_secret.isEnabled = false
                            tv_error_counter.show()
                            tv_error_counter.text = getString(R.string.try_again, 30)
                            handlePinError(R.string.try_again_in_30)
                        } else {
                            handlePinError(R.string.incorrect_pin)
                        }

                    }

                }
            }
        })
    }

    private fun handlePinError(string: Int) {
        activity!!.createDialog(
            getString(R.string.error),
            getString(string)
        )
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                pv_secret.setText("")
            }
            .create()
            .show()
    }

    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        TODO("Not yet implemented")
    }

    private fun countDownError(timeInMilli: Long): CountDownTimer {
        return object : CountDownTimer(timeInMilli, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (tv_error_counter == null) return
                val timeLeftInMillis = timeInMilli - (timeInMilli - millisUntilFinished)
                val timeLeft = timeLeftInMillis / 1000
                MyApplication.errorTimeLeft = timeLeftInMillis
                tv_error_counter.text = getString(R.string.try_again, timeLeft.toInt())
            }

            override fun onFinish() {
                if (tv_error_counter == null) return
                errorCount = 0
                pv_secret.isEnabled = true
                tv_error_counter.hide()
                MyApplication.errorTimeLeft = 0L
            }

        }
    }

    override fun onStop() {
        super.onStop()
        logDebug("onStop MyApplication error count left is => ${MyApplication.errorTimeLeft}")
        errorCountDownTimer.cancel()
        if (this::globalErrorCountDownTimer.isInitialized)
            globalErrorCountDownTimer.cancel()
    }
}
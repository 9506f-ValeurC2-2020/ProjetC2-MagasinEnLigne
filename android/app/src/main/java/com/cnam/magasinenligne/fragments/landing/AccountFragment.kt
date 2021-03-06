package com.cnam.magasinenligne.fragments.landing

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cnam.magasinenligne.MyApplication
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.activities.LandingActivity
import com.cnam.magasinenligne.activities.RegistrationActivity
import com.cnam.magasinenligne.api.ApiCallback
import com.cnam.magasinenligne.api.AppRetrofitClient
import com.cnam.magasinenligne.api.ID
import com.cnam.magasinenligne.api.RetrofitResponseListener
import com.cnam.magasinenligne.api.models.Client
import com.cnam.magasinenligne.api.models.SingleClientResponse
import com.cnam.magasinenligne.api.models.SingleVendeurResponse
import com.cnam.magasinenligne.api.models.Vendeur
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.fragments.profile.ChangeEmailFragment
import com.cnam.magasinenligne.fragments.profile.ChangePasswordFragment
import com.cnam.magasinenligne.fragments.profile.ChangePhoneFragment
import com.cnam.magasinenligne.isUserLoggedIn
import com.cnam.magasinenligne.utils.*
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_account.*
import java.io.File

class AccountFragment : BaseFragment(), RetrofitResponseListener {
    private lateinit var myActivity: LandingActivity
    private var changePasswordClicked = false
    private var changePhoneClicked = false
    private var changeEmailClicked = false
    private var camClicked = false
    var capturedImageUri: Uri? = null
    private val camRequest = 7000
    private val camPermissions = arrayOf(Manifest.permission.CAMERA)
    private val selectPictureRequest = 3000
    private val cameraCaptureRequest = 4000

    /**
     * OnBackPressedCallback
     */
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                myActivity.finish()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myActivity = activity!! as LandingActivity
        return inflater.inflate(R.layout.fragment_account, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        loadView()
        initListeners()
        myActivity.addOnBackStackListener(this)
    }

    private fun isPicExist(): Boolean {
        return if (MyApplication.isClient()) {
            !MyApplication.clientProfile.image.isNullOrEmpty()
        } else {
            !MyApplication.merchantProfile.image.isNullOrEmpty()
        }
    }


    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        logDebug("onBackStackChanged ${myActivity.supportFragmentManager.backStackEntryCount}")
        if (!myActivity.accountPaused) {
            val fragment = myActivity.supportFragmentManager.findFragmentById(R.id.fl_container)
            if (fragment != null && fragment is AccountFragment)
                resetClicks()
        }
    }

    private fun loadView() {
        if (MyApplication.isClient()) {
            if (!MyApplication.clientProfile.image.isNullOrEmpty())
                iv_profile_pic.setImageBitmap(createBitmap(MyApplication.clientProfile.image!!))
            tv_name.text = MyApplication.clientProfile.fullName
        } else {
            if (!MyApplication.merchantProfile.image.isNullOrEmpty())
                iv_profile_pic.setImageBitmap(createBitmap(MyApplication.merchantProfile.image!!))
            tv_name.text = MyApplication.merchantProfile.fullName
        }
    }

    private fun initListeners() {
        tv_edit_password.setOnClickListener {
            if (!changePasswordClicked) {
                myActivity.accountPaused = true
                changePasswordClicked = true
                myActivity.supportFragmentManager.addTransaction(
                    ChangePasswordFragment(),
                    ChangePasswordFragment::class.java.simpleName,
                    R.anim.enter_from_right,
                    R.anim.exit_to_right
                )
            }
        }
        tv_edit_phone.setOnClickListener {
            if (!changePhoneClicked) {
                myActivity.accountPaused = true
                changePhoneClicked = true
                myActivity.supportFragmentManager.addTransaction(
                    ChangePhoneFragment(),
                    ChangePhoneFragment::class.java.simpleName,
                    R.anim.enter_from_right,
                    R.anim.exit_to_right
                )
            }
        }
        tv_edit_email.setOnClickListener {
            if (!changeEmailClicked) {
                myActivity.accountPaused = true
                changeEmailClicked = true
                myActivity.supportFragmentManager.addTransaction(
                    ChangeEmailFragment(),
                    ChangeEmailFragment::class.java.simpleName,
                    R.anim.enter_from_right,
                    R.anim.exit_to_right
                )
            }
        }
        tv_logout.setOnClickListener {
            putPreference(isUserLoggedIn, false)
            startActivity(Intent(myActivity, RegistrationActivity::class.java))
        }
        iv_profile_pic.setOnClickListener {
            if (!camClicked) {
                camClicked = true
                val options1 = arrayOf(
                    getString(R.string.edit),
                    getString(R.string.remove)
                )

                if (!isPicExist()) {
                    showOptions()
                } else {
                    val builder = AlertDialog.Builder(myActivity)
                    builder
                        .setTitle(resources.getString(R.string.update))
                        .setCancelable(true)
                        .setItems(options1) { dialog, which ->

                            when (options1[which]) {
                                getString(R.string.edit) -> {
                                    dialog.cancel()
                                    showOptions()
                                }
                                getString(R.string.remove) -> {
                                    updatePhoto(null)
                                }
                            }
                        }
                        .setOnDismissListener {
                            logDebug("Cancel")
                            camClicked = false
                        }


                    val alert = builder.create()
                    alert.show()
                }
            }
        }
    }


    private fun updatePhoto(uri: Uri?) {
        myActivity.startLoading()
        myActivity.lockView(true)
        logDebug("URI $uri")
        if (uri == null) {
            iv_profile_pic.setImageResource(R.drawable.iv_contact)
        } else {
            Picasso.get().load(uri).into(iv_profile_pic)
        }
        val part = if (uri != null) setImageFile(uri) else null
        if (MyApplication.isClient()) {
            logDebug("PART is " + part.toString())
            val fields = hashMapOf(
                ID to createTextRequestBody(MyApplication.clientProfile.id)
            )
            val apiCallback = ApiCallback<SingleClientResponse>("from_client_update", this)
            AppRetrofitClient.buildService(1).updateClientPhoto(fields, part).enqueue(apiCallback)
        } else {
            val fields = hashMapOf(
                ID to createTextRequestBody(MyApplication.merchantProfile.id)
            )
            val apiCallback = ApiCallback<SingleVendeurResponse>("from_merchant_update", this)
            AppRetrofitClient.buildService(2).updateMerchantPhoto(fields, part).enqueue(apiCallback)
        }
    }

    private fun captureImage() {
        val imageUri = Uri.fromFile(getImagePath(myActivity))
        capturedImageUri = imageUri
        myActivity.startCameraCapture(imageUri)
    }

    private fun getImagePath(context: Context): File? {
        val dir: File? = context.getExternalFilesDir(null)
        val time = getCurrentTimeUsingDate().replace(" ", "_").replace(":", "-")

        val res = ((if (dir == null) "" else dir.absolutePath + "/")
                + "image" + time + ".jpg")
        return if (res != "") File(res) else null
    }

    private fun showOptions() {
        val options =
            arrayOf(getString(R.string.gallery), getString(R.string.camera))
        val builder = AlertDialog.Builder(myActivity)
        builder
            .setTitle(getString(R.string.choose_image_from))
            .setCancelable(true)
            .setItems(options) { _, which ->

                when (options[which]) {
                    getString(R.string.gallery) -> {
                        camClicked = true
                        chooseImage()
                    }
                    getString(R.string.camera) -> {
                        val cameraGranted = verifyPermissions(
                            myActivity,
                            camRequest,
                            camPermissions
                        )
                        if (cameraGranted) {
                            captureImage()
                        }
                    }
                }
            }
            .setOnDismissListener {
                camClicked = false
            }


        val alert = builder.create()
        alert.show()
    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), selectPictureRequest)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        logDebug(requestCode.toString())
        when (requestCode) {
            camRequest -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImage()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            selectPictureRequest -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        val imageUri = data.data
                        try {
                            capturedImageUri = imageUri

                            val selectedBitmap = getBitmap(requireContext(), imageUri!!)

                            /*We can access getExternalFileDir() without asking any storage permission.*/
                            val selectedImgFile = File(
                                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                getNowTimeStamp() + "_selectedImg.jpg"
                            )

                            convertBitmapToFile(selectedImgFile, selectedBitmap!!)
                            /*We have to again create a new file where we will save the cropped image. */
                            val croppedImgFile = File(
                                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                getNowTimeStamp() + "_croppedImg.jpg"
                            )

                            myActivity.startCrop(
                                Uri.fromFile(selectedImgFile),
                                Uri.fromFile(croppedImgFile)
                            )

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        logDebug("Image uri: $imageUri")
                    }

                }
            }
            cameraCaptureRequest -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        /*We cannot access the image directly so we again create a new File at different location and use it for further processing.*/

                        val capturedBitmap = getBitmap(requireContext(), capturedImageUri!!)

                        /*We are storing the above bitmap at different location where we can access it.*/
                        val capturedImgFile = File(
                            myActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            getNowTimeStamp() + "_capturedImg.jpg"
                        )
                        convertBitmapToFile(capturedImgFile, capturedBitmap!!)
                        /*We have to again create a new file where we will save the processed image.*/
                        val croppedImgFile = File(
                            myActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            getNowTimeStamp() + "_croppedImg.jpg"
                        )
                        myActivity.startCrop(
                            Uri.fromFile(capturedImgFile),
                            Uri.fromFile(croppedImgFile)
                        )

                    }
                }
            }
            UCrop.REQUEST_CROP -> {
                logDebug("Entering crop")
                when (resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        if (data != null) {
                            try {
                                /*After the cropping is done we will get the cropped image Uri here.
                                We can use this Uri and create file and use it for other purpose like saving to cloud etc.*/
                                val croppedImageUri = UCrop.getOutput(data)
                                updatePhoto(croppedImageUri)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    }
                    UCrop.RESULT_ERROR -> {
                        if (data != null) {
                            val cropError = UCrop.getError(data)
                            logDebug("crop error is ${cropError?.message}")

                        }
                    }
                }

            }

        }
        resetClicks()
        logDebug("Result is $resultCode  from $requestCode")
    }

    private fun resetClicks() {
        camClicked = false
        changePasswordClicked = false
        changePhoneClicked = false
        changeEmailClicked = false
        myActivity.showNavigation()
    }

    override fun onSuccess(result: Any, from: String) {
        myActivity.lockView(false)
        myActivity.stopLoading()
        resetClicks()
        when (from) {
            "from_client_update" -> {
                MyApplication.clientProfile = result as Client
            }
            "from_merchant_update" -> {
                MyApplication.merchantProfile = result as Vendeur
            }
        }
        tv_logout.showSnack(getString(R.string.picture_updated))
    }

    override fun onFailure(error: String) {
        resetClicks()
        myActivity.lockView(false)
        myActivity.stopLoading()
        tv_logout.showSnack(error)
    }
}
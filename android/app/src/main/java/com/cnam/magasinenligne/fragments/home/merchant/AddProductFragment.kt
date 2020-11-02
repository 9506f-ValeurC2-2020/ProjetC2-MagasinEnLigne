package com.cnam.magasinenligne.fragments.home.merchant

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
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.cnam.magasinenligne.MyApplication
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.activities.LandingActivity
import com.cnam.magasinenligne.api.*
import com.cnam.magasinenligne.api.models.SingleProductResponse
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.utils.*
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_add_product.*
import java.io.File

class AddProductFragment : BaseFragment(), RetrofitResponseListener {
    private lateinit var myActivity: LandingActivity
    private var camClicked = false
    private var addClicked = false
    private var hasChanged = false
    var capturedImageUri: Uri? = null
    private val camRequest = 7000
    private val camPermissions = arrayOf(Manifest.permission.CAMERA)
    private val selectPictureRequest = 3000
    private val cameraCaptureRequest = 4000
    private var selectedCategory = "clothing"
    private var selectedAgeCategory = "0-3"

    /**
     * OnBackPressedCallback
     */
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity!!.supportFragmentManager.popBackStack()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myActivity = activity!! as LandingActivity
        return inflater.inflate(R.layout.fragment_add_product, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        initListeners()
    }

    private fun initListeners() {
        /**
         * radio change listeners
         */
        rb_clothing.setOnClickListener {
            selectedCategory = "clothing"
            adjustSelection(1, shouldShow = true)
        }
        rb_makeup.setOnClickListener {
            selectedCategory = "makeup"
            adjustSelection(1)
        }
        rb_electronics.setOnClickListener {
            selectedCategory = "electronics"
            adjustSelection(2)
        }
        rb_appliances.setOnClickListener {
            selectedCategory = "home appliances"
            adjustSelection(2)
        }
        cb_on_sale.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                et_sale_price.show()
            } else {
                et_sale_price.setText("")
                et_sale_price.hide()
            }
        }
        val listener1 = View.OnClickListener { adjustSelection(3) }
        val listener2 = View.OnClickListener { adjustSelection(4) }
        for (rb in rg_ages_1.children) {
            selectedAgeCategory = (rb as RadioButton).text.toString()
            rb.setOnClickListener(listener1)
        }
        for (rb in rg_ages_2.children) {
            selectedAgeCategory = (rb as RadioButton).text.toString()
            rb.setOnClickListener(listener2)
        }

        /**
         * click listeners
         */
        iv_product.setOnClickListener {
            if (!camClicked) {
                camClicked = true
                val options1 = arrayOf(
                    getString(R.string.edit),
                    getString(R.string.remove)
                )

                if (!hasChanged) {
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
                                    removePic()
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
        bt_add.setOnClickListener {
            if (!addClicked) {
                addClicked = true
                val name = et_name.text.toString()
                val price = et_price.text.toString()
                val isOnSale = cb_on_sale.isChecked
                val salePrice = if (isOnSale) et_sale_price.text.toString() else "0"
                if (name.isEmpty()) {
                    showError(getString(R.string.name_required_error))
                    return@setOnClickListener
                }
                if (price.isEmpty()) {
                    showError(getString(R.string.price_required_error))
                    return@setOnClickListener
                }
                if (price.toInt() == 0) {
                    showError(getString(R.string.null_price_error))
                    return@setOnClickListener
                }
                if (isOnSale) {
                    if (salePrice.isEmpty()) {
                        showError(getString(R.string.sale_price_required_error))
                        return@setOnClickListener
                    }
                    if (salePrice.toInt() == 0) {
                        showError(getString(R.string.null_price_error))
                        return@setOnClickListener
                    }
                    if (salePrice.toInt() > price.toInt()) {
                        showError(getString(R.string.sale_price_bigger_than_initial_error))
                        return@setOnClickListener
                    }
                }
                myActivity.lockView(true)
                myActivity.startLoading()
                val part = if (capturedImageUri != null) setImageFile(capturedImageUri!!) else null
                val fields = hashMapOf(
                    NAME to createTextRequestBody(name),
                    CATEGORY to createTextRequestBody(selectedCategory),
                    PRICE to createTextRequestBody(price),
                    PROVIDED_BY to createTextRequestBody(MyApplication.merchantProfile.id)
                )
                if (selectedCategory == "clothing") {
                    val sex = if (rb_female.isChecked) 1 else 0
                    fields[SEX] = createTextRequestBody("$sex")
                    fields[AGE_CATEGORY] = createTextRequestBody(selectedAgeCategory)
                }
                if (isOnSale) {
                    fields[ON_SALE] = createTextRequestBody("$isOnSale")
                    fields[SALE_PRICE] = createTextRequestBody(salePrice)
                }
                val apiCallback = ApiCallback<SingleProductResponse>("from_product_save", this)
                AppRetrofitClient.buildService(3).saveProduct(fields, part).enqueue(apiCallback)
                logDebug(fields.toString())

            }
        }
    }

    private fun showError(message: String) {
        myActivity.createDialog(getString(R.string.error), message)
            .setCancelable(true)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .setOnDismissListener { addClicked = false }
            .show()
    }

    private fun removePic() {
        iv_product.setImageResource(R.drawable.ic_camera)
        capturedImageUri = null
        hasChanged = false
    }

    private fun updatePhoto(uri: Uri?) {
        Picasso.get().load(uri).into(iv_product)
        capturedImageUri = uri
        hasChanged = true
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


    private fun adjustSelection(groupId: Int, shouldShow: Boolean = false) {
        when (groupId) {
            1 -> {
                if (shouldShow) group_clothing.show() else group_clothing.hide()
                rg_categories_2.clearCheck()
            }
            2 -> {
                rg_categories_1.clearCheck()
                group_clothing.hide()
            }
            3 -> rg_ages_2.clearCheck()
            else -> rg_ages_1.clearCheck()
        }

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

    override fun onBackStackChanged() {
        logDebug("onBackStackChanged")
    }


    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    private fun resetClicks() {
        camClicked = false
    }

    override fun onSuccess(result: Any, from: String) {
        myActivity.stopLoading()
        myActivity.lockView(false)
        bt_add.showSnack("Product added successfully")
        clearViewsData()
    }

    override fun onFailure(error: String) {
        myActivity.stopLoading()
        myActivity.lockView(false)
        bt_add.showSnack(error)
    }

    private fun clearViewsData() {
        et_name.setText("")
        et_sale_price.setText("")
        et_price.setText("")
        cb_on_sale.isChecked = false
        rb_0_3.performClick()
        rb_female.performClick()
        rb_clothing.performClick()
        addClicked = false
        capturedImageUri = null
        removePic()
    }

}
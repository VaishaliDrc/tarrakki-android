package com.tarrakki.module.myprofile

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.annotation.NonNull
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import com.tarrakki.*
import com.tarrakki.api.AES
import com.tarrakki.api.ApiClient
import com.tarrakki.api.model.Country
import com.tarrakki.api.model.UserProfileResponse
import com.tarrakki.api.model.parseArray
import com.tarrakki.databinding.FragmentMyProfileBinding
import com.tarrakki.module.ekyc.SignatureActivity
import com.tarrakki.module.otp.OtpVerificationActivity
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_my_profile.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.events.Event
import org.supportcompact.ktx.*
import org.supportcompact.utilise.ImageChooserUtil
import java.io.File

const val PROFILE_EMAIL_DATA = "profile_email_data"
const val PROFILE_MOBILE_DATA = "profile_mobile_data"

class MyProfileFragment : CoreFragment<MyProfileVM, FragmentMyProfileBinding>() {

    var isEmailVerified: Boolean? = false
    var isMobileVerified: Boolean? = false

    var verifiedEmail: String? = ""
    var verifiedMobile: String? = ""

    var profileUri: Uri? = null
    var signatureUri: Uri? = null

    var isProfileClickable: Boolean? = false

    private val SAMPLE_CROPPED_IMAGE_NAME = "ProfileImage"
    private val SIGN_CROPPED_IMAGE_NAME = "SignCropImage"

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.my_profile)

    override fun getLayout(): Int {
        return R.layout.fragment_my_profile
    }

    override fun createViewModel(): Class<out MyProfileVM> {
        return MyProfileVM::class.java
    }

    override fun setVM(binding: FragmentMyProfileBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    val countryJSON = App.INSTANCE.resources.openRawResource(R.raw.country).bufferedReader().use { it.readText() }
    val countries = countryJSON.parseArray<ArrayList<Country>>()

    val profileObserver: android.arch.lifecycle.Observer<UserProfileResponse> = android.arch.lifecycle.Observer { response ->
        response?.let {
            //getBinding().root.visibility = View.VISIBLE
            getViewModel().signatureBtnVisibility.set(if (response.data.isAOFUploaded == true) View.GONE else View.VISIBLE)
            getViewModel().profileUrl.set(response.data.userProfileImage)
            getViewModel().PANName.set(response.data.kycDetail.panName)
            getViewModel().PANNumber.set(response.data.kycDetail.pan)
            getViewModel().fName.set(response.data.kycDetail.fullName)
            getViewModel().email.set(response.data.email)
            getViewModel().mobile.set(response.data.mobileNumber)
            getViewModel().guardian.set(response.data.guardianName)
            getViewModel().guardianPANNumber.set(response.data.guardianPan)
            getViewModel().nominiName.set(response.data.nomineeName)
            getViewModel().nominiRelationship.set(response.data.nomineeRelationship)
            getViewModel().address.set(response.data.address)
            getViewModel().city.set(response.data.city)
            getViewModel().pincode.set(response.data.pincode)
            getViewModel().state.set(response.data.state)
            getViewModel().country.set(response.data.country)
            if (!TextUtils.isEmpty(response.data.country)) {
                val country = countries?.find { it.code == response.data.country }
                country?.let { edtCountry?.setText(it.name) }
            } else {
                val item = countries?.first { it.name == "India" }
                edtCountry?.text = item?.name
                getViewModel().country.set(item?.code)
            }

            isEmailVerified = response.data.isEmailActivated
            isMobileVerified = response.data.isMobileVerified

            getViewModel().isEmailVerified.set(isEmailVerified)
            getViewModel().isMobileVerified.set(isMobileVerified)

            verifiedEmail = response.data.email
            verifiedMobile = response.data.mobileNumber
        }
    }

    override fun createReference() {
        //getBinding().root.visibility = View.GONE
        getViewModel().profileUrl.observe {
            it?.let {
                val requestOptions = RequestOptions()
                requestOptions.placeholder(R.drawable.ic_profile_default)
                requestOptions.error(R.drawable.ic_profile_default)
                Glide.with(ivProfile)
                        .setDefaultRequestOptions(requestOptions)
                        .load(ApiClient.IMAGE_BASE_URL.plus(it))
                        .into(ivProfile)
            }
        }
        edtCountry?.setOnClickListener {
            countries?.let {
                context?.showCustomListDialog(R.string.select_country, countries) { item: Country ->
                    edtCountry?.text = item.name
                    getViewModel().country.set(item.code)
                }
            }
        }

        getViewModel().isEmailVerified.observe {
            if (it) {
                edtEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_succeeded, 0)
            } else {
                edtEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }

        getViewModel().isMobileVerified.observe {
            if (it) {
                edtMobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_succeeded, 0)
            } else {
                edtMobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }

        getViewModel().email.observe {
            if (it.isNotEmpty()) {
                if (isEmailVerified == true) {
                    if (verifiedEmail == it) {
                        getViewModel().isEmailVerified.set(true)
                    } else {
                        getViewModel().isEmailVerified.set(false)
                    }
                } else {
                    getViewModel().isEmailVerified.set(false)
                }
            } else {
                getViewModel().isEmailVerified.set(false)
            }
        }

        getViewModel().mobile.observe {
            if (it.isNotEmpty()) {
                if (isMobileVerified == true) {
                    if (verifiedMobile == it) {
                        getViewModel().isMobileVerified.set(true)
                    } else {
                        getViewModel().isMobileVerified.set(false)
                    }
                } else {
                    getViewModel().isMobileVerified.set(false)
                }
            } else {
                getViewModel().isMobileVerified.set(false)
            }
        }

        btnEdit?.setOnClickListener {
            getViewModel().isEdit.get()?.let { isEdit ->
                if (isEdit) {
                    if (isValidate()) {
                        getViewModel().updateProfile().observe(this, android.arch.lifecycle.Observer {
                            context?.simpleAlert(context?.getString(R.string.alert_profile_success_update).toString()) {
                                Handler().postDelayed({
                                    getViewModel().isEdit.set(false)
                                    getViewModel().getUserProfile().observe(this, profileObserver)
                                }, 100)
                            }
                        })
                    } else {

                    }
                } else {
                    getViewModel().isEdit.set(true)
                    Handler().postDelayed({
                        edtFName?.setSelection(edtFName.length())
                        edtFName?.requestFocus()
                    }, 100)
                }
            }
        }

        ivEditProfilePick?.setOnClickListener {
            isProfileClickable = true
            context?.takePick(
                    onGallery = {
                        openGallery()
                    },
                    onCamera = {
                        openCamera()
                    }
            )
        }

        btnSignature?.setOnClickListener {
            isProfileClickable = false
            context?.signatureDialog(
                    btnDigitally = {
                        startActivity<SignatureActivity>()
                    },
                    btnPhysically = {
                        context?.takePick(
                                onGallery = {
                                    openGallery()
                                },
                                onCamera = {
                                    openCamera()
                                }
                        )
                    })
        }

        edtEmail.setOnClickListener {
            context?.updateEmailOrMobile(updateField = "${getViewModel().email.get()}") { email: String ->
                getViewModel().email.set(email)
                onVerifyEmail()
            }
            /*if (i == EditorInfo.IME_ACTION_DONE) {
                onVerifyEmail()
            }*/
        }

        edtMobile.setOnClickListener {
            context?.updateEmailOrMobile(isEmailUpdate = false, updateField = "${getViewModel().mobile.get()}") { mobile: String ->
                getViewModel().mobile.set(mobile)
                onVerifyMobile()
            }
            /*if (i == EditorInfo.IME_ACTION_DONE) {
                onVerifyMobile()
            }*/
        }

        App.INSTANCE.signatureFile.observe(this, Observer {
            it?.let { file ->
                startSignCrop(Uri.fromFile(file), true)
                App.INSTANCE.signatureFile.value = null
            }
        })

        getViewModel().getUserProfile().observe(this, profileObserver)
    }

    private fun validateMobile(): Boolean {
        return when {
            getViewModel().mobile.isEmpty() -> {
                context?.simpleAlert(context?.getString(R.string.pls_enter_mobile_number).toString()) {
                    Handler().postDelayed({
                        edtMobile?.requestFocus()
                    }, 100)
                }
                false
            }
            !getViewModel().mobile.isValidMobile() -> {
                context?.simpleAlert(context?.getString(R.string.pls_enter_valid_indian_mobile_number).toString()) {
                    Handler().postDelayed({
                        edtMobile?.requestFocus()
                    }, 100)
                }
                false
            }
            else -> true
        }
    }

    private fun validateEmail(): Boolean {
        return when {
            getViewModel().email.isEmpty() -> {
                context?.simpleAlert(context?.getString(R.string.pls_enter_email_address).toString()) {
                    Handler().postDelayed({
                        edtEmail?.requestFocus()
                    }, 100)
                }
                false
            }
            !getViewModel().email.isEmail() -> {
                context?.simpleAlert(context?.getString(R.string.pls_enter_valid_email_address).toString()) {
                    Handler().postDelayed({
                        edtEmail?.requestFocus()
                    }, 100)
                }
                false
            }
            else -> true
        }
    }

    private fun onVerifyEmail() {
        if (verifiedEmail != getViewModel().email.get()) {
            context?.confirmationDialog(title = getString(R.string.app_name),
                    msg = getString(R.string.alert_email_auth),
                    btnPositive = "Yes",
                    btnNegative = "No",
                    btnPositiveClick = {
                        val json = JsonObject()
                        json.addProperty("user_id", App.INSTANCE.getUserId())
                        json.addProperty("email", "$verifiedEmail".toLowerCase())
                        json.addProperty("type", "update_email")
                        e("Plain Data=>", json.toString())
                        val data = AES.encrypt(json.toString())
                        e("Encrypted Data=>", data)
                        getViewModel().getOTP(data).observe(this, android.arch.lifecycle.Observer {
                            it?.let { it1 ->
                                val intent = Intent(activity, OtpVerificationActivity::class.java)
                                intent.putExtra(PROFILE_EMAIL_DATA, json.toString())
                                startActivity(intent)
                                EventBus.getDefault().postSticky(it1)
                            }
                        })
                    },
                    btnNegativeClick = {
                        getViewModel().email.set(verifiedEmail)
                    })
        }
        /*if (validateEmail()) {

        }*/
    }

    private fun onVerifyMobile() {
        if (getViewModel().mobile.get() != verifiedMobile) {
            context?.confirmationDialog(title = getString(R.string.app_name),
                    msg = getString(R.string.alert_mobile_auth),
                    btnPositive = "Yes",
                    btnNegative = "No",
                    btnPositiveClick = {
                        val json = JsonObject()
                        json.addProperty("user_id", App.INSTANCE.getUserId())
                        json.addProperty("mobile", getViewModel().mobile.get())
                        json.addProperty("type", "update_mobile")
                        e("Plain Data=>", json.toString())
                        val data = AES.encrypt(json.toString())
                        e("Encrypted Data=>", data)
                        getViewModel().getOTP(data).observe(this, android.arch.lifecycle.Observer {
                            it?.let { it1 ->
                                val intent = Intent(activity, OtpVerificationActivity::class.java)
                                intent.putExtra(PROFILE_MOBILE_DATA, json.toString())
                                startActivity(intent)
                                EventBus.getDefault().postSticky(it1)
                            }
                        })
                    },
                    btnNegativeClick = {
                        getViewModel().mobile.set(verifiedMobile)
                    })
        }
        /*if (validateMobile()) {

        }*/
    }

    private fun openGallery() {
        val permissions = arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestPermissionsIfRequired(permissions, object : PermissionCallBack {
            override fun permissionGranted() {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                        .setType("image/*")
                        .addCategory(Intent.CATEGORY_OPENABLE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    val mimeTypes = arrayOf("image/jpeg", "image/png")
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                }
                startActivityForResult(Intent.createChooser(intent, "Select File"), getViewModel().IMAGE_RQ_CODE)
                /*val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(Intent.createChooser(pickPhoto, "Select File"), getViewModel().IMAGE_RQ_CODE)*/
            }

            override fun permissionDenied() {
                context?.confirmationDialog(
                        title = getString(R.string.permission),
                        msg = getString(R.string.write_external_storage_title),
                        btnPositive = getString(R.string.allow),
                        btnNegative = getString(R.string.dont_allow),
                        btnPositiveClick = {
                            openGallery()
                        }
                )
            }

            override fun onPermissionDisabled() {
                context?.confirmationDialog(
                        title = getString(R.string.permission),
                        msg = getString(R.string.write_external_storage_title),
                        btnPositive = getString(R.string.settings),
                        btnNegative = getString(R.string.cancel),
                        btnPositiveClick = {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", context?.packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                )
            }
        })
    }

    private fun openCamera() {
        val permissions = arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        requestPermissionsIfRequired(permissions, object : PermissionCallBack {
            override fun permissionGranted() {
                ImageChooserUtil.startCameraIntent(this@MyProfileFragment, getViewModel().cvPhotoName, getViewModel().ICAMERA_RQ_CODE)
            }

            override fun permissionDenied() {
                context?.confirmationDialog(
                        title = getString(R.string.permission),
                        msg = getString(R.string.write_external_storage_title),
                        btnPositive = getString(R.string.allow),
                        btnNegative = getString(R.string.dont_allow),
                        btnPositiveClick = {
                            openCamera()
                            //ImageChooserUtil.startCameraIntent(this@MyProfileFragment, getViewModel().cvPhotoName, getViewModel().ICAMERA_RQ_CODE)
                        }
                )
            }

            override fun onPermissionDisabled() {
                context?.confirmationDialog(
                        title = getString(R.string.permission),
                        msg = getString(R.string.write_external_storage_title),
                        btnPositive = getString(R.string.settings),
                        btnNegative = getString(R.string.cancel),
                        btnPositiveClick = {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", context?.packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                )
            }
        })
    }

    private fun startCrop(@NonNull uri: Uri) {
        var destinationFileName = SAMPLE_CROPPED_IMAGE_NAME
        destinationFileName += ".png"
        val uCrop = UCrop.of(uri, Uri.fromFile(File(context?.cacheDir, destinationFileName)))
        uCrop.withAspectRatio(1f, 1f)
        val options = context?.getUCropOptions()
        // context?.color(R.color.lighter_gray)?.let { options?.setRootViewBackgroundColor(it) }
        options?.let { uCrop.withOptions(it) }
        uCrop.start(context!!, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                getViewModel().ICAMERA_RQ_CODE -> {
                    val file = ImageChooserUtil.getCameraImageFile(getViewModel().cvPhotoName)
                    if (isProfileClickable == true) {
                        startCrop(Uri.fromFile(file))
                    } else {
                        startSignCrop(Uri.fromFile(file), true)
                    }
                }
                getViewModel().IMAGE_RQ_CODE -> {
                    val selectedUri = data?.data
                    if (selectedUri != null) {
                        if (isProfileClickable == true) {
                            startCrop(selectedUri)
                        } else {
                            startSignCrop(selectedUri, true)
                        }
                    }
                }
                UCrop.REQUEST_CROP -> {
                    if (data != null) {
                        if (isProfileClickable == true) {
                            profileUri = UCrop.getOutput(data)
                            profileUri?.let {
                                getViewModel().updateProfileImage(profileUri).observe(this, android.arch.lifecycle.Observer {
                                    context?.simpleAlert(getString(R.string.alert_profile_success)) {
                                        Handler().postDelayed({
                                            getViewModel().isEdit.set(false)
                                            getViewModel().getUserProfile().observe(this, profileObserver)
                                        }, 100)
                                    }
                                })

                                if (it.scheme == "file") {
                                    val myBitmap = BitmapFactory.decodeFile(it.path)
                                    ivProfile?.setImageBitmap(myBitmap)
                                } else {
                                    ivProfile?.setImageURI(it)
                                }
                            }
                        } else {
                            signatureUri = com.tarrakki.ucrop.UCrop.getOutput(data)
                            signatureUri?.let {
                                getViewModel().updateSignatureImage(signatureUri).observe(this, android.arch.lifecycle.Observer {
                                    context?.simpleAlert(getString(R.string.alert_signtuare_success)) {
                                        Handler().postDelayed({
                                            getViewModel().isEdit.set(false)
                                            getViewModel().getUserProfile().observe(this, profileObserver)
                                        }, 100)
                                    }
                                })
                                if (it.scheme == "file") {
                                    val myBitmap = BitmapFactory.decodeFile(it.path)
                                    imgSign?.setImageBitmap(myBitmap)
                                } else {
                                    imgSign?.setImageURI(it)
                                }
                                // imgSign.show()
                            }
                        }
                    }
                }
            }
        }
    }

    @Subscribe(sticky = true)
    fun onEventData(event: Event) {
        when (event) {
            Event.ISEMAILVERIFIED -> {
                verifiedEmail = getViewModel().email.get()
                getViewModel().isEmailVerified.set(false)
            }
            Event.ISMOBILEVERIFIED -> {
                verifiedMobile = getViewModel().mobile.get()
                getViewModel().isMobileVerified.set(true)
            }
            Event.ISEMAILVERIFIEDBACK -> {
                getViewModel().email.set(verifiedEmail)
            }
            Event.ISMOBILEVERIFIEDBACK -> {
                getViewModel().mobile.set(verifiedMobile)
            }
            else -> {

            }
        }
    }

    private fun isValidate(): Boolean {
        return when {
            getViewModel().fName.isEmpty() -> {
                context?.simpleAlert(getString(R.string.alert_enter_display_name)) {
                    Handler().postDelayed({
                        edtFName?.requestFocus()
                    }, 100)
                }
                false
            }
            getViewModel().email.isEmpty() -> {
                context?.simpleAlert(context?.getString(R.string.pls_enter_email_address).toString()) {
                    Handler().postDelayed({
                        edtEmail?.requestFocus()
                    }, 100)
                }
                false
            }
            !getViewModel().email.isEmail() -> {
                context?.simpleAlert(context?.getString(R.string.pls_enter_valid_email_address).toString()) {
                    Handler().postDelayed({
                        edtEmail?.requestFocus()
                    }, 100)
                }
                false
            }
            getViewModel().mobile.isEmpty() -> {
                context?.simpleAlert(context?.getString(R.string.pls_enter_mobile_number).toString()) {
                    Handler().postDelayed({
                        edtMobile?.requestFocus()
                    }, 100)
                }
                false
            }
            !getViewModel().mobile.isValidMobile() -> {
                context?.simpleAlert(context?.getString(R.string.pls_enter_valid_indian_mobile_number).toString()) {
                    Handler().postDelayed({
                        edtMobile?.requestFocus()
                    }, 100)
                }
                false
            }
            getViewModel().nominiName.isEmpty() -> {
                context?.simpleAlert(context?.getString(R.string.alert_req_nominee_name).toString()) {
                    Handler().postDelayed({
                        edtNominee?.requestFocus()
                    }, 100)
                }
                false
            }
            getViewModel().nominiRelationship.isEmpty() -> {
                context?.simpleAlert(context?.getString(R.string.alert_req_nominee_relationship).toString()) {
                    Handler().postDelayed({
                        edtNomineeRelationship?.requestFocus()
                    }, 100)
                }
                false
            }
            verifiedMobile != getViewModel().mobile.get() -> {
                context?.simpleAlert(getString(R.string.alert_verify_mobile))
                false
            }
            verifiedEmail != getViewModel().email.get() -> {
                context?.simpleAlert(getString(R.string.alert_verify_email))
                false
            }
            else -> true
        }
    }

    private fun startSignCrop(@NonNull uri: Uri, isPhysically: Boolean = true) {
        var destinationFileName = SIGN_CROPPED_IMAGE_NAME
        destinationFileName += ".png"
        val options = context?.getCustomUCropOptions()
        val uCrop = com.tarrakki.ucrop.UCrop.of(uri, Uri.fromFile(File(context?.cacheDir, destinationFileName)))
        options?.let {
            if (isPhysically) {
                it.setRootViewBackgroundColor(Color.WHITE)
            }
            it.setCompressionQuality(100)
            it.setCompressionFormat(Bitmap.CompressFormat.PNG)
            it.setShowCropGrid(false)
        }
        options?.let { uCrop.withOptions(it) }
        uCrop.withAspectRatio(16f, 9f)
        uCrop.withMaxResultSize(200, 120)
        uCrop.start(context!!, this)
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = MyProfileFragment().apply { arguments = basket }
    }

}

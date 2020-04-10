package com.tarrakki.module.bankaccount


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.NonNull
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.tarrakki.*
import com.tarrakki.api.model.BankDetail
import com.tarrakki.api.model.UserBanksResponse
import com.tarrakki.api.model.toDecrypt
import com.tarrakki.databinding.FragmentBankAccountsBinding
import com.tarrakki.module.account.AccountActivity
import com.tarrakki.module.account.AccountFragment
import com.tarrakki.module.birth_certificate.UploadDOBCertiFragment
import com.tarrakki.module.ekyc.EKYCRemainingDetailsFragment
import com.tarrakki.module.ekyc.IS_FROM_VIDEO_KYC
import com.tarrakki.module.ekyc.KYCData
import com.tarrakki.module.ekyc.SignatureActivity
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.home.HomeFragment
import com.tarrakki.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_bank_accounts.*
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.events.Event
import org.supportcompact.ktx.*
import org.supportcompact.utilise.ImageChooserUtil
import java.io.File


const val DOB_CERTIFICATE_REQ_CODE = 121

class BankAccountsFragment : CoreFragment<BankAccountsVM, FragmentBankAccountsBinding>() {

    private val SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage"

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_accounts)

    override fun getLayout(): Int {
        return R.layout.fragment_bank_accounts
    }

    override fun createViewModel(): Class<out BankAccountsVM> {
        return BankAccountsVM::class.java
    }

    override fun setVM(binding: FragmentBankAccountsBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isRegistration = arguments?.getBoolean(IS_FROM_COMLETE_REGISTRATION) ?: false
        if (isRegistration) {
            coreActivityVM?.title?.set(getString(
                    if (arguments?.getBoolean(IS_FROM_VIDEO_KYC, false) == true)
                        R.string.kyc_details
                    else
                        R.string.complete_registration
            ))
        }
    }

    lateinit var bankObserver: Observer<UserBanksResponse>

    override fun createReference() {
        val isRegistration = arguments?.getBoolean(IS_FROM_COMLETE_REGISTRATION) ?: false

        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let { isRefreshing ->
                mRefresh?.isRefreshing = false
                App.INSTANCE.isRefreshing.value = null
            }
        })

        bankObserver = Observer { r ->
            r?.let { userBanksResponse ->
                val banks = arrayListOf<WidgetsViewModel>()
                var count = 0
                val noBanks = userBanksResponse.data.bankDetails.isEmpty()
                if (userBanksResponse.data.bankDetails.isEmpty()) {
                    banks.add(NoBankAccount())
                } else {
                    banks.addAll(userBanksResponse.data.bankDetails)
                    count = userBanksResponse.data.bankDetails.size
                }
                if (isRegistration) {
                    banks.add(object : WidgetsViewModel {
                        override fun layoutId(): Int {
                            return R.layout.btn_add_next_bank_mandate
                        }
                    })
                } else if (count < 5) {
                    banks.add(SingleButton(R.string.add_new_bank_account))
                }

                banks.add(object : WidgetsViewModel {
                    override fun layoutId(): Int {
                        return R.layout.row_bank_account_note
                    }
                })

                rvBanks?.setUpMultiViewRecyclerAdapter(banks) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                    binder.setVariable(BR.widget, item)
                    binder.setVariable(BR.onAdd, View.OnClickListener {
                        if (count >= 5) {
                            context?.simpleAlert(getString(R.string.alert_limit_add_bank))
                            return@OnClickListener
                        }
                        startFragment(AddBankAccountFragment.newInstance(Bundle().apply { putSerializable(IS_FROM_BANK_ACCOUNT, true) }), R.id.frmContainer)
                    })
                    binder.setVariable(BR.onNext, View.OnClickListener {
                        if (noBanks) {
                            context?.simpleAlert(getString(R.string.alert_add_bank_complete_registration))
                            return@OnClickListener
                        }

                        if (arguments?.getBoolean(IS_FROM_VIDEO_KYC, false) == true) {
                            startFragment(EKYCRemainingDetailsFragment.newInstance(), R.id.frmContainer)
                            getViewModel().kycData.value?.let {
                                postSticky(it)
                            }
                            return@OnClickListener
                        }
                         if (getViewModel().kycData.value?.guardianName?.isNotEmpty() == true && getViewModel().kycData.value?.bobCirtificate?.isEmpty() == true) {
//                            val f = UploadDOBCertiFragment.newInstance()
//                            f.setTargetFragment(this, DOB_CERTIFICATE_REQ_CODE)
                            getViewModel().kycData.value?.bobCirtificate = ""
                            startFragment(UploadDOBCertiFragment.newInstance(), R.id.frmContainer)
                            return@OnClickListener
                        }
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
                    })
                    binder.setVariable(BR.setDefault, View.OnClickListener {
                        if (item is BankDetail && !item.isDefault) {
                            context?.let {
                                it.confirmationDialog(getString(R.string.alert_bank_default),
                                        btnPositiveClick = {
                                            getViewModel().setDefault("${item.id}").observe(this@BankAccountsFragment, Observer {
                                                coreActivityVM?.onNewBank?.value = true
                                            })
                                        }
                                )
                            }

                        }
                    })

                    binder.setVariable(BR.onUpdate, View.OnClickListener {
                        val bundle = Bundle()
                        r.data.bankDetail = item as BankDetail
                        bundle.putString("userBankData", Gson().toJson(r))
                        startFragment(AddBankAccountFragment.newInstance(bundle.apply { putSerializable(IS_FROM_BANK_ACCOUNT, true) }), R.id.frmContainer)
                    })

                    binder.executePendingBindings()
                }
            }
        }
        coreActivityVM?.onNewBank?.observe(this, Observer {
            getViewModel().getAllBanks().observe(this, bankObserver)
        })
        getViewModel().getAllBanks().observe(this, bankObserver)
        App.INSTANCE.signatureFile.observe(this, Observer {
            it?.let { file ->
                startCrop(Uri.fromFile(file), true)
                App.INSTANCE.signatureFile.value = null
                getViewModel().imageFrom = getViewModel().SIGNPAD_RQ_CODE
            }
        })

        mRefresh?.setOnRefreshListener {
            getViewModel().getAllBanks(true).observe(this, bankObserver)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (arguments?.getBoolean(IS_FROM_VIDEO_KYC, false) == true) {
                    context?.confirmationDialog(getString(R.string.are_you_sure_you_want_to_exit),
                            btnPositiveClick = {
                                getViewModel().kycData.value?.let { removeStickyEvent(it) }
                                if (activity is HomeActivity) {
                                    onBackExclusive(HomeFragment::class.java)
                                } else {
                                    onBackExclusive(AccountFragment::class.java)
                                }
                            }
                    )
                } else {
                    //requireActivity().onBackPressed()
                    onBack(1)
                }
            }
        })
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
                ImageChooserUtil.startCameraIntent(this@BankAccountsFragment, getViewModel().cvPhotoName, getViewModel().ICAMERA_RQ_CODE)
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

    private fun startCrop(@NonNull uri: Uri, isPhysically: Boolean = true) {
        var destinationFileName = SAMPLE_CROPPED_IMAGE_NAME
        destinationFileName += ".png"
        val options = context?.getCustomUCropOptions()
        val uCrop = UCrop.of(uri, Uri.fromFile(File(context?.cacheDir, destinationFileName)))
        options?.let {
            if (isPhysically) {
                it.setRootViewBackgroundColor(Color.WHITE)
            }
            it.setCompressionQuality(100)
            it.setCompressionFormat(Bitmap.CompressFormat.PNG)
            it.setShowCropGrid(false)
            //  it.setToolbarTitle("Edit Signature")
        }
        options?.let { uCrop.withOptions(it) }
        uCrop.withAspectRatio(16f, 9f)
        uCrop.withMaxResultSize(250, 150)
        uCrop.start(context!!, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                /*DOB_CERTIFICATE_REQ_CODE -> {
                    getViewModel().kycData.value?.bobCirtificate = data?.getStringExtra("img") ?: ""
                }*/
                getViewModel().ICAMERA_RQ_CODE -> {
                    val file = ImageChooserUtil.getCameraImageFile(getViewModel().cvPhotoName)
                    startCrop(Uri.fromFile(file))
                    getViewModel().imageFrom = getViewModel().ICAMERA_RQ_CODE
                }
                getViewModel().IMAGE_RQ_CODE -> {
                    val selectedUri = data?.data
                    if (selectedUri != null) {
                        val path = getPath(selectedUri)
                        path?.let { filePath ->
                            startCrop(Uri.fromFile(File(filePath)))
                            getViewModel().imageFrom = getViewModel().IMAGE_RQ_CODE
                        }
                    }
                }
                UCrop.REQUEST_CROP -> {
                    if (data != null) {
                        val imageUri = UCrop.getOutput(data)
                        imageUri?.let {
                            val path = getPath(it)
                            path?.let { filePath ->
                                getViewModel().kycData.value?.let { kycData ->
                                    getViewModel().completeRegistrations(File(filePath), kycData).observe(this, Observer { apiResponse ->
                                        apiResponse?.let {
                                            //{"data": {"ready_to_invest": false}}*#$*
                                            val json = JSONObject("${it.data?.toDecrypt()}")
                                            val isReadyToInvest = json.optJSONObject("data")?.optBoolean("ready_to_invest") == true
                                            context?.setReadyToInvest(isReadyToInvest)
                                            context?.simpleAlert(if (apiResponse.status?.code == 1) getString(if (isReadyToInvest) R.string.complete_registration_msg else R.string.account_verification_is_pending) else "${apiResponse.status?.message}") {
                                                removeStickyEvent(kycData)
                                                if (activity is HomeActivity) {
                                                    onBack(3)
                                                } else {
                                                    startActivity<AccountActivity>()
                                                }
                                            }
                                        }
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @Subscribe(sticky = true)
    fun onReceive(kycData: KYCData) {
        if (getViewModel().kycData.value == null) {
            getViewModel().kycData.value = kycData
        }
    }

    override fun onEvent(event: Event) {
        super.onEvent(event)
        if (event == Event.REFRESH_ACCOUNT) {
            getViewModel().getAllBanks().observe(this, bankObserver)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = BankAccountsFragment().apply { arguments = basket }
    }
}

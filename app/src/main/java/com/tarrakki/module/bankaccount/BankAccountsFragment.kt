package com.tarrakki.module.bankaccount


import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.ViewDataBinding
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.NonNull
import android.view.View
import com.tarrakki.*
import com.tarrakki.api.model.BankDetail
import com.tarrakki.api.model.UserBanksResponse
import com.tarrakki.databinding.FragmentBankAccountsBinding
import com.tarrakki.module.ekyc.KYCData
import com.tarrakki.module.ekyc.SignatureActivity
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_bank_accounts.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.*
import org.supportcompact.utilise.ImageChooserUtil
import java.io.File


class BankAccountsFragment : CoreFragment<BankAccountsVM, FragmentBankAccountsBinding>() {

    private val SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage"

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_accounts)

    var isFromBankMandate: Boolean? = false

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
        /*isFromBankMandate = arguments?.getBoolean(ISFROMBANKMANDATE)
        if (isFromBankMandate==true){
            coreActivityVM?.title?.set(getString(R.string.bank_mandate))
        }*/
        if (isRegistration) {
            coreActivityVM?.title?.set(getString(R.string.complete_registration))
        }
    }

    override fun createReference() {
        val isRegistration = arguments?.getBoolean(IS_FROM_COMLETE_REGISTRATION) ?: false
        val bankObserver = Observer<UserBanksResponse> { r ->
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
                } else {
                    banks.add(SingleButton(R.string.add_new_bank_account))
                }

                rvBanks?.setUpMultiViewRecyclerAdapter(banks) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                    binder.setVariable(BR.widget, item)
                    binder.setVariable(BR.onAdd, View.OnClickListener {
                        if (count >= 5) {
                            context?.simpleAlert("You can't add more then 5 banks.")
                            return@OnClickListener
                        }
                        startFragment(AddBankAccountFragment.newInstance(Bundle().apply { putSerializable(IS_FROM_BANK_ACCOUNT, true) }), R.id.frmContainer)
                    })
                    binder.setVariable(BR.onNext, View.OnClickListener {
                        if (noBanks) {
                            context?.simpleAlert("Please add bank to continue complete registration")
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
                        if (item is BankDetail) {
                            getViewModel().setDefault("${item.id}").observe(this@BankAccountsFragment, Observer {
                                coreActivityVM?.onNewBank?.value = true
                            })
                        }
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
                getViewModel().completeRegistrations(file).observe(this, Observer { apiResponse ->
                    apiResponse?.let {
                        context?.simpleAlert("${apiResponse.status?.message}") {
                            onBack(3)
                        }
                    }
                })
                App.INSTANCE.signatureFile.value = null
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

    private fun startCrop(@NonNull uri: Uri) {
        var destinationFileName = SAMPLE_CROPPED_IMAGE_NAME
        destinationFileName += ".jpg"
        val uCrop = UCrop.of(uri, Uri.fromFile(File(context?.cacheDir, destinationFileName)))
        uCrop.withAspectRatio(16f, 9f)
        uCrop.withMaxResultSize(50, 30)
        uCrop.start(context!!, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                getViewModel().ICAMERA_RQ_CODE -> {
                    val file = ImageChooserUtil.getCameraImageFile(getViewModel().cvPhotoName)
                    startCrop(Uri.fromFile(file))
                }
                getViewModel().IMAGE_RQ_CODE -> {
                    val selectedUri = data?.data
                    if (selectedUri != null) {
                        startCrop(selectedUri)
                    }
                }
                UCrop.REQUEST_CROP -> {
                    if (data != null) {
                        val imageUri = UCrop.getOutput(data)
                        imageUri?.let {
                            val path = getPath(it)
                            path?.let { App.INSTANCE.signatureFile.value = File(it) }
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
        removeStickyEvent(kycData)
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = BankAccountsFragment().apply { arguments = basket }
    }
}

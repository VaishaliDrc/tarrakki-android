package com.tarrakki.module.bankmandate


import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.KeyEvent
import com.tarrakki.IS_FROM_BANK_ACCOUNT
import com.tarrakki.R
import com.tarrakki.api.model.BankDetail
import com.tarrakki.api.model.UserBankMandateResponse
import com.tarrakki.databinding.FragmentBankMandateBinding
import com.tarrakki.databinding.RowBankMandateListItemBinding
import com.tarrakki.databinding.RowUserBankListMandateBinding
import com.tarrakki.getBankMandateStatus
import com.tarrakki.module.bankaccount.AddBankAccountFragment
import com.tarrakki.module.bankaccount.SingleButton
import com.tarrakki.module.recommended.ISFROMGOALRECOMMEDED
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_bank_mandate.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.ChoiceMode
import org.supportcompact.adapters.KSelectionAdapter
import org.supportcompact.adapters.setUpAdapter
import org.supportcompact.events.Event
import org.supportcompact.ktx.*
import org.supportcompact.utilise.ImageChooserUtil
import java.io.File

const val ISFROMBANKMANDATE = "isfrombankmandate"

class BankMandateFragment : CoreFragment<BankMandateVM, FragmentBankMandateBinding>() {

    private val SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage"

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_mandate)

    var mandateBankAdapter: KSelectionAdapter<UserBankMandateResponse.Data, RowBankMandateListItemBinding>? = null
    var userBankAdapter: KSelectionAdapter<BankDetail, RowUserBankListMandateBinding>? = null

    override fun getLayout(): Int {
        return R.layout.fragment_bank_mandate
    }

    override fun createViewModel(): Class<out BankMandateVM> {
        return BankMandateVM::class.java
    }

    override fun setVM(binding: FragmentBankMandateBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        /* rvBankMandate?.setUpMultiViewRecyclerAdapter(getViewModel().bankMandate) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
             binder.setVariable(BR.widget, item)
             binder.setVariable(BR.onNext, View.OnClickListener {
                 startFragment(AutoDebitFragment.newInstance(), R.id.frmContainer)
                 EventBus.getDefault().postSticky(getViewModel().bankMandate[0])
             })
             binder.setVariable(BR.onAdd, View.OnClickListener {
                 startFragment(AddBankAccountFragment.newInstance(Bundle().apply { putSerializable(IS_FROM_BANK_ACCOUNT, false) }), R.id.frmContainer)
             })
             binder.executePendingBindings()
         }*/

        btnNext?.setOnClickListener {
            if (getViewModel().isMandateBankList.get() != true) {
                if (userBankAdapter?.selectedItemViewCount != 0) {
                    startFragment(AutoDebitFragment.newInstance(), R.id.frmContainer)
                    postSticky(userBankAdapter?.getSelectedItems()?.get(0) as BankDetail)
                } else {
                    context?.simpleAlert("Please Select Bank.")
                }
            }
        }
        btnAdd?.setOnClickListener {
            startFragment(AddBankAccountFragment.newInstance(Bundle().apply { putSerializable(IS_FROM_BANK_ACCOUNT, false) }), R.id.frmContainer)
        }
    }

    private fun getBanksData() {
        getViewModel().getAllMandateBanks().observe(this, Observer {
            if (it?.data?.isNotEmpty() == true) {
                getViewModel().isNoBankAccount.set(false)
                setUserBankMandateAdapter(it.data)
                getViewModel().isNextVisible.set(false)
                getViewModel().isAddVisible.set(true)
            } else {
                getViewModel().getAllBanks().observe(this, Observer { it1 ->
                    getViewModel().isAddVisible.set(true)
                    if (it1?.data?.bankDetails?.isNotEmpty() == true) {
                        getViewModel().isNoBankAccount.set(false)
                        getViewModel().isNextVisible.set(true)
                        setUserBankAdapter(it1.data.bankDetails)
                    } else {
                        getViewModel().isNoBankAccount.set(true)
                        getViewModel().isNextVisible.set(false)
                    }
                })
            }
        })
    }

    private fun setUserBankMandateAdapter(bankDetails: List<UserBankMandateResponse.Data>) {
        getViewModel().isMandateBankList.set(true)
        mandateBankAdapter = setUpAdapter(bankDetails as MutableList<UserBankMandateResponse.Data>,
                ChoiceMode.SINGLE,
                R.layout.row_bank_mandate_list_item,
                { item, binder: RowBankMandateListItemBinding?, position, adapter ->
                    binder?.widget = item
                    binder?.executePendingBindings()
                    binder?.isSelected = adapter.isItemViewToggled(position)

                    binder?.tvPending?.text = item.status.getBankMandateStatus()
                    binder?.tvInfo?.text = if (item.mandateType=="I"){
                        getString(R.string.normally_take_)
                    }else{
                        getString(R.string.normally_take_nach)
                    }

                    binder?.btnUploadSanned?.setOnClickListener {
                        context?.takePick(
                                onGallery = {
                                    openGallery()
                                },
                                onCamera = {
                                    openCamera()
                                }
                        )
                    }

                }, { item, position, adapter ->

        })
        rvBankMandate?.adapter = mandateBankAdapter
        mandateBankAdapter?.toggleItemView(0)
        mandateBankAdapter?.notifyItemChanged(0)
    }

    private fun setUserBankAdapter(bankDetails: List<BankDetail>) {
        getViewModel().isMandateBankList.set(false)
        userBankAdapter = setUpAdapter(bankDetails as MutableList<BankDetail>,
                ChoiceMode.SINGLE,
                R.layout.row_user_bank_list_mandate,
                { item, binder: RowUserBankListMandateBinding?, position, adapter ->
                    binder?.widget = item
                    binder?.executePendingBindings()
                    binder?.isSelected = adapter.isItemViewToggled(position)

                }, { item, position, adapter ->

        }
        )
        rvBankMandate?.adapter = userBankAdapter
        /*userBankAdapter?.toggleItemView(0)
        userBankAdapter?.notifyItemChanged(0)*/
        val default_position = bankDetails.indexOfFirst { it.isDefault }
        if (default_position != -1) {
            userBankAdapter?.toggleItemView(default_position)
            userBankAdapter?.notifyItemChanged(default_position)
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            Event.BANK_MANDATE_SUBMITTED -> {
                getViewModel().bankMandate.forEach { item ->
                    if (item is BankMandate) {
                        item.isPending = true
                    } else {
                        getViewModel().bankMandate.remove(item)
                    }
                }
                getViewModel().bankMandate.add(SingleButton(R.string.add_new_bank_account))
                rvBankMandate?.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        getBanksData()
        super.onResume()
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
                ImageChooserUtil.startCameraIntent(this@BankMandateFragment,
                        getViewModel().cvPhotoName, getViewModel().ICAMERA_RQ_CODE)
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
        val options = UCrop.Options()
        options.setMaxBitmapSize(2097152)
        options.setToolbarColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
        options.setStatusBarColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
        options.setActiveWidgetColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        uCrop.withOptions(options)
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
                            val bundle = Bundle().apply {
                                putString("upload_url",imageUri.toString())
                                putBoolean(ISFROMBANKMANDATE,true)
                            }
                            /* if (it.scheme == "file") {
                                 val myBitmap = BitmapFactory.decodeFile(it.path)
                                 ivProfile?.setImageBitmap(myBitmap)
                             } else {
                                 ivProfile?.setImageURI(it)
                             }*/
                            startFragment(UploadBankMandateFormFragment.newInstance(bundle), R.id.frmContainer)
                        }
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = BankMandateFragment().apply { arguments = basket }
    }


}

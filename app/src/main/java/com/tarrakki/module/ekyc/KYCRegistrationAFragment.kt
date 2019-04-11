package com.tarrakki.module.ekyc


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentKycregistrationABinding
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_kycregistration_a.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.*
import java.text.DateFormatSymbols
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [KYCRegistrationAFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class KYCRegistrationAFragment : CoreFragment<KYCRegistrationAVM, FragmentKycregistrationABinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.complete_registration)

    override fun getLayout(): Int {
        return R.layout.fragment_kycregistration_a
    }

    override fun createViewModel(): Class<out KYCRegistrationAVM> {
        return KYCRegistrationAVM::class.java
    }

    override fun setVM(binding: FragmentKycregistrationABinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        /*getViewModel().ivEmailVerifiedVisibility.set(context?.isEmailVerified() == true)
        if (getViewModel().ivEmailVerifiedVisibility.get() == false) {
        }*/
        /*tvCertificate.text = "Are you born before  ${getDate(18).convertTo("dd MMM, yyyy")} ?"
        switchOnOff?.setOnCheckedChangeListener { buttonView, isChecked ->
            isAdult(isChecked)
        }*/
        getViewModel().kycData.observe(this, android.arch.lifecycle.Observer {
            it?.let { kycData ->
                getBinding().kycData = kycData
                edtPAN?.setText(kycData.pan)
                getBinding().executePendingBindings()
                //switchOnOff?.isChecked = kycData.guardianName.isEmpty()
                isAdult(kycData.guardianName.isEmpty())
            }
        })

        btnContinue?.setOnClickListener {
            startFragment(KYCRegistrationBFragment.newInstance(), R.id.frmContainer)
        }
        /* edtAddressType?.setOnClickListener {
             context?.showListDialog(R.string.select_address_type, R.array.address_type) { item ->
                 getViewModel().addressType.set(item)
             }
         }
         edtState?.setOnClickListener {
             context?.showListDialog(R.string.select_state, R.array.indian_states) { item ->
                 getViewModel().state.set(item)
             }
         }*/

        edtGuardianDOB?.setOnClickListener {
            val now: Calendar = Calendar.getInstance()
            val minDate = getCalendar(18)
            minDate.add(Calendar.DAY_OF_YEAR, 1)
            var Cdob: Calendar? = null
            var date: String? = getViewModel().kycData.value?.guardianDOB
            date?.toDate("dd/MM/yyyy")?.let { dob ->
                Cdob = dob.toCalendar()
            }
            val dPicker = SpinnerDatePickerDialogBuilder()
                    .context(context)
                    .callback { view, year, monthOfYear, dayOfMonth ->
                        date = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year)
                        getViewModel().kycData.value?.guardianDOB = date as String
                        edtGuardianDOB?.text = String.format("%02d %s, %d", dayOfMonth, DateFormatSymbols().months[monthOfYear].substring(0, 3), year)
                    }
                    .showTitle(true)
                    .minDate(minDate.get(Calendar.YEAR), minDate.get(Calendar.MONTH), minDate.get(Calendar.DAY_OF_MONTH))
                    .maxDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            if (Cdob != null) {
                Cdob?.let {
                    dPicker.defaultDate(it.get(Calendar.YEAR), it.get(Calendar.MONTH), it.get(Calendar.DAY_OF_MONTH))
                }
            } else {
                dPicker.defaultDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            }
            dPicker.build().show()
        }

        btnContinue?.setOnClickListener {
            getViewModel().kycData.value?.let { kycData ->
                if (isValid(kycData)) {
                    kycData.pageNo = 2
                    saveKYCData(kycData).observe(this, android.arch.lifecycle.Observer {
                        context?.setKYClVarified(true)
                        startFragment(KYCRegistrationBFragment.newInstance(), R.id.frmContainer)
                        post(kycData)
                    })
                }
            }
        }
    }

    private fun isAdult(isChecked: Boolean) {
        getViewModel().guardianVisibility.set(!isChecked)
        getViewModel().isEdit.set(isChecked)
        if (isChecked) {
            edtDOB?.text = getViewModel().kycData.value?.dob?.toDate("dd/MM/yyyy")?.convertTo("dd MMM, yyyy")
            getViewModel().kycData.value?.fullName = "" + getViewModel().kycData.value?.nameOfPANHolder
        } else {
            getViewModel().kycData.value?.fullName = ""
        }
        /*edtDOB?.alpha = if (isChecked) 0.8f else 1f
        edtDOB?.isEnabled = !isChecked
        if (isChecked) {
            edtDOB?.setBackgroundResource(R.drawable.shape_edt_disable_gray_round)
            edtDOB?.text = getViewModel().kycData.value?.dob?.toDate("dd/MM/yyyy")?.convertTo("dd MMM, yyyy")
            getViewModel().kycData.value?.guardianName = ""
            getViewModel().kycData.value?.guardianDOB = ""
            getViewModel().kycData.value?.fullName = "" + getViewModel().kycData.value?.nameOfPANHolder
            edtPAN?.setText(getViewModel().kycData.value?.pan)
        } else {
            edtDOB?.setBackgroundResource(R.drawable.shape_edt_gray_round)
            getViewModel().kycData.value?.guardianName = "" + getViewModel().kycData.value?.nameOfPANHolder
            edtPAN?.text?.clear()
            if (!getViewModel().kycData.value?.guardianDOB.isNullOrEmpty()) {
                edtDOB?.text = getViewModel().kycData.value?.guardianDOB?.toDate("dd/MM/yyyy")?.convertTo("dd MMM, yyyy")
            } else {
                edtDOB?.text = ""
                getViewModel().kycData.value?.fullName = ""
            }
        }*/
    }

    private fun isValid(kycData: KYCData): Boolean {
        return when {
            kycData.fullName.isEmpty() -> {
                context?.simpleAlert(getString(if (kycData.guardianName.isNotEmpty()) R.string.alert_req_monors_full_name else R.string.alert_req_full_name))
                false
            }
            kycData.pan.isEmpty() -> {
                context?.simpleAlert(getString(R.string.alert_req_pan_number))
                false
            }
            !kycData.pan.isPAN() -> {
                context?.simpleAlert(getString(R.string.alert_valid_pan_number))
                false
            }
            kycData.guardianName.isNotEmpty() && kycData.guardianDOB.isEmpty() -> {
                context?.simpleAlert(getString(R.string.alert_pls_select_minors_dob))
                false
            }
            /*getViewModel().email.isEmpty() -> {
                context?.simpleAlert("Please enter email id")
                false
            }
            !getViewModel().email.isEmail() -> {
                context?.simpleAlert("Please enter valid email id")
                false
            }
            getViewModel().mobile.isEmpty() -> {
                context?.simpleAlert("Please enter mobile number")
                false
            }
            getViewModel().mobile.get()?.length != 10 -> {
                context?.simpleAlert("Please enter valid mobile number")
                false
            }*/
            /*kycData.guardianName.isNotEmpty() && kycData.guardianName.isEmpty() -> {
                context?.simpleAlert(getString(R.string.alert_req_guardian_name)) {
                    edtGuardian?.requestFocus()
                }
                false
            }*/
            /*!getViewModel().isEdit.get()!! && getViewModel().guardianPANNumber.isEmpty() -> {
                context?.simpleAlert("Please enter guardian PAN number")
                false
            }
            !getViewModel().isEdit.get()!! && getViewModel().guardianPANNumber.isPAN() -> {
                context?.simpleAlert("Please enter valid guardian PAN number")
                false
            }*/
            /* getViewModel().addressType.isEmpty() -> {
                 context?.simpleAlert("Please select address type")
                 false
             }
             getViewModel().address.isEmpty() -> {
                 context?.simpleAlert("Please enter address")
                 false
             }
             getViewModel().city.isEmpty() -> {
                 context?.simpleAlert("Please enter city")
                 false
             }
             getViewModel().pincode.isEmpty() -> {
                 context?.simpleAlert("Please enter pin-code")
                 false
             }
             getViewModel().pincode.get()?.length != 6 -> {
                 context?.simpleAlert("Please enter valid pin-code")
                 false
             }
             getViewModel().state.isEmpty() -> {
                 context?.simpleAlert("Please select state")
                 false
             }
             getViewModel().country.isEmpty() -> {
                 context?.simpleAlert("Please enter country")
                 false
             }*/
            kycData.nomineeName.isEmpty() -> {
                context?.simpleAlert(getString(R.string.alert_req_nominee_name)) {
                    edtNominee?.requestFocus()
                }
                false
            }
            kycData.nomineeRelation.isEmpty() -> {
                context?.simpleAlert(getString(R.string.alert_req_nominee_relationship)) {
                    edtRelationship?.requestFocus()
                }
                false
            }
            else -> true
        }
    }

    @Subscribe(sticky = true)
    fun onReceive(kycData: KYCData) {
        if (getViewModel().kycData.value == null) {
            getViewModel().kycData.value = kycData
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment KYCRegistrationAFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = KYCRegistrationAFragment().apply { arguments = basket }
    }
}

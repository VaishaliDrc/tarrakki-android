package com.tarrakki.module.ekyc


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.tarrakki.R
import com.tarrakki.databinding.FragmentKycregistrationABinding
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_kycregistration_a.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.*
import java.text.DateFormatSymbols
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [KYCRegistrationAFragment.newInstance] factory method to
 * create an instance of this fragment.
 *s
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

        edtDOB?.setOnClickListener {
            val now: Calendar = Calendar.getInstance()
            var Cdob: Calendar? = null
            var date: String? = getViewModel().dob.get()
            date?.toDate("dd MMM, yyyy")?.let { dob ->
                Cdob = dob.toCalendar()
            }
            val dPicker = SpinnerDatePickerDialogBuilder()
                    .context(context)
                    .callback { view, year, monthOfYear, dayOfMonth ->
                        date = String.format("%02d %s, %d", dayOfMonth, DateFormatSymbols().months[monthOfYear].substring(0, 3), year)
                        getViewModel().dob.set(date)
                        val isAdult = isAdult(year, monthOfYear, dayOfMonth)
                        getViewModel().guardianVisibility.set(if (isAdult) View.GONE else View.VISIBLE)
                        getViewModel().isEdit.set(isAdult)
                    }
                    .showTitle(true)
                    .maxDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            if (Cdob != null) {
                Cdob?.let { it ->
                    dPicker.defaultDate(it.get(Calendar.YEAR), it.get(Calendar.MONTH), it.get(Calendar.DAY_OF_MONTH))
                }
            } else {
                dPicker.defaultDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            }
            dPicker.build().show()
        }

        btnContinue?.setOnClickListener {
            if (isValid()) {
                startFragment(KYCRegistrationBFragment.newInstance(), R.id.frmContainer)
            }
        }
    }

    private fun isAdult(year: Int, month: Int, day: Int): Boolean {
        //calculating age from dob
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob.set(year, month, day)
        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age >= 18
    }

    private fun isValid(): Boolean {
        return when {
            getViewModel().fName.isEmpty() -> {
                context?.simpleAlert("Please enter full name")
                false
            }
            getViewModel().PANNumber.isEmpty() -> {
                context?.simpleAlert("Please enter PAN number")
                false
            }
            getViewModel().isEdit.get()!! && !getViewModel().PANNumber.isPAN() -> {
                context?.simpleAlert("Please enter valid PAN number")
                false
            }
            getViewModel().dob.isEmpty() -> {
                context?.simpleAlert("Please select date of birth")
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
            !getViewModel().isEdit.get()!! && getViewModel().guardian.isEmpty() -> {
                context?.simpleAlert("Please enter guardian name")
                false
            }
            !getViewModel().isEdit.get()!! && getViewModel().guardianPANNumber.isEmpty() -> {
                context?.simpleAlert("Please enter guardian PAN number")
                false
            }
            !getViewModel().isEdit.get()!! && getViewModel().guardianPANNumber.isPAN() -> {
                context?.simpleAlert("Please enter valid guardian PAN number")
                false
            }
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
            getViewModel().nominiName.isEmpty() -> {
                context?.simpleAlert("Please enter nominee name")
                false
            }
            getViewModel().nominiRelationship.isEmpty() -> {
                context?.simpleAlert("Please enter nominee relationship")
                false
            }
            else -> true
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
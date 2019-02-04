package com.tarrakki.module.ekyc


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentKycregistrationABinding
import kotlinx.android.synthetic.main.fragment_kycregistration_a.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.*

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
        edtAddressType?.setOnClickListener {
            context?.showListDialog(R.string.select_address_type, R.array.address_type) { item ->
                getViewModel().address.set(item)
            }
        }
        edtState?.setOnClickListener {
            context?.showListDialog(R.string.select_state, R.array.indian_states) { item ->
                getViewModel().state.set(item)
            }
        }

        btnContinue?.setOnClickListener {
            isValid()
        }
    }

    fun isValid(): Boolean {
        return when {
            getViewModel().fName.isEmpty() -> {
                context?.simpleAlert("Please enter full name")
                false
            }
            getViewModel().PANNumber.isEmpty() -> {
                context?.simpleAlert("Please enter PAN number")
                false
            }
            !getViewModel().PANNumber.isPAN() -> {
                context?.simpleAlert("Please enter valid PAN number")
                false
            }
            getViewModel().dob.isEmpty() -> {
                context?.simpleAlert("Please select date of birth")
                false
            }
            getViewModel().email.isEmail() -> {
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
            getViewModel().mobile.isEmpty() -> {
                context?.simpleAlert("Please enter mobile number")
                false
            }
            getViewModel().addressType.isEmpty() -> {
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
            getViewModel().state.isEmpty() -> {
                context?.simpleAlert("Please select state")
                false
            }
            getViewModel().country.isEmpty() -> {
                context?.simpleAlert("Please enter country")
                false
            }
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

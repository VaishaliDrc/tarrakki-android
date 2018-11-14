package com.tarrakki.module.panverify


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentPanverifyBinding
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_panverify.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment
import java.text.DateFormatSymbols
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [PANVerifyFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class PANVerifyFragment : CoreFragment<PANVerifyVM, FragmentPanverifyBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.verify_your_pan)

    override fun getLayout(): Int {
        return R.layout.fragment_panverify
    }

    override fun createViewModel(): Class<out PANVerifyVM> {
        return PANVerifyVM::class.java
    }

    override fun setVM(binding: FragmentPanverifyBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

        tvDOB?.setOnClickListener {
            val now = Calendar.getInstance()
            SpinnerDatePickerDialogBuilder()
                    .context(context)
                    .callback { view, year, monthOfYear, dayOfMonth ->
                        getViewModel().dob.set(String.format("%02d %s %d", dayOfMonth, DateFormatSymbols().months[monthOfYear].substring(0, 3), year))
                    }
                    .showTitle(true)
                    .defaultDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                    .minDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                    .build()
                    .show()
        }
        btnVerify?.setOnClickListener {

            when {
                getViewModel().pan.get()?.length == 0 -> context?.simpleAlert("Please enter PAN number") {
                    edtPAN?.requestFocus()
                }
                getViewModel().dob.get()?.length == 0 -> context?.simpleAlert("Please enter date of birth")
                getViewModel().mobile.get()?.length == 0 -> context?.simpleAlert("Please enter mobile number") {
                    edtMobile?.requestFocus()
                }
                else -> {
                    startFragment(OtpPANVerifyFragment.newInstance(), R.id.frmContainer)
                }
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket as Bundle.
         * @return A new instance of fragment PANVerifyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = PANVerifyFragment().apply { arguments = basket }
    }
}

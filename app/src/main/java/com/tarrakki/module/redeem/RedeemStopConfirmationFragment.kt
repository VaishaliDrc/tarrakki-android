package com.tarrakki.module.redeem


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentRedeemStopConfirmationBinding
import org.supportcompact.CoreFragment

/**
 * A simple [Fragment] subclass.
 * Use the [RedeemStopConfirmationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RedeemStopConfirmationFragment : CoreFragment<RedeemConfirmVM, FragmentRedeemStopConfirmationBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = "Redeem"

    override fun getLayout(): Int {
        return R.layout.fragment_redeem_stop_confirmation
    }

    override fun createViewModel(): Class<out RedeemConfirmVM> {
        return RedeemConfirmVM::class.java
    }

    override fun setVM(binding: FragmentRedeemStopConfirmationBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment RedeemStopConfirmationFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle) = RedeemStopConfirmationFragment().apply { arguments = basket }
    }
}

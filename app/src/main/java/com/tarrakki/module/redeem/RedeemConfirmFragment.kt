package com.tarrakki.module.redeem


import android.os.Bundle
import com.tarrakki.R
import com.tarrakki.databinding.FragmentRedeemConfirmBinding
import kotlinx.android.synthetic.main.fragment_redeem_confirm.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.startFragment


class RedeemConfirmFragment : CoreFragment<RedeemConfirmVM, FragmentRedeemConfirmBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.redemption_confim)

    override fun getLayout(): Int {
        return R.layout.fragment_redeem_confirm
    }

    override fun createViewModel(): Class<out RedeemConfirmVM> {
        return RedeemConfirmVM::class.java
    }

    override fun setVM(binding: FragmentRedeemConfirmBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        btnProceed?.setOnClickListener {
            startFragment(RedemptionStatusFragment.newInstance(), R.id.frmContainer)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment RedeemConfirmFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = RedeemConfirmFragment().apply { arguments = basket }
    }
}

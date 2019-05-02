package com.tarrakki.module.debitcart


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.api.model.FolioData
import com.tarrakki.databinding.FragmentDebitCartInfoBinding
import kotlinx.android.synthetic.main.fragment_debit_cart_info.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.startFragment
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [DebitCartInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DebitCartInfoFragment : CoreFragment<DebitCartInfoVM, FragmentDebitCartInfoBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.apply_for_debit_cart)

    override fun getLayout(): Int {
        return R.layout.fragment_debit_cart_info
    }

    override fun createViewModel(): Class<out DebitCartInfoVM> {
        return DebitCartInfoVM::class.java
    }

    override fun setVM(binding: FragmentDebitCartInfoBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        btnPayNow?.setOnClickListener {
            startFragment(ApplyForDebitCartFragment.newInstance(), R.id.frmContainer)
            postSticky(getViewModel().folioData)
        }
    }

    @Subscribe(sticky = true)
    fun onReemFund(items: ArrayList<FolioData>) {
        if (getViewModel().folioData.isEmpty()) {
            getViewModel().folioData.addAll(items)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket as Bundle.
         * @return A new instance of fragment DebitCartInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = DebitCartInfoFragment().apply { arguments = basket }
    }
}

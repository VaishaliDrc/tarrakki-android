package com.tarrakki.module.bankmandate


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.MenuItem
import com.tarrakki.R
import com.tarrakki.api.model.BankDetail
import com.tarrakki.databinding.FragmentBankMandateSuccessBinding
import kotlinx.android.synthetic.main.fragment_bank_mandate_success.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment

/**
 * A simple [Fragment] subclass.
 * Use the [BankMandateSuccessFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class BankMandateSuccessFragment : CoreFragment<BankMandateSuccessVM, FragmentBankMandateSuccessBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_mandate)

    override fun getLayout(): Int {
        return R.layout.fragment_bank_mandate_success
    }

    override fun createViewModel(): Class<out BankMandateSuccessVM> {
        return BankMandateSuccessVM::class.java
    }

    override fun setVM(binding: FragmentBankMandateSuccessBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        getViewModel().isIMandate.set(arguments?.getBoolean(ISIPMANDATE))

        if (getViewModel().isIMandate.get() == true) {
            tvBankMandateType?.text = getString(R.string.sip_mandate)
            tvContent?.text = "Your bank mandate request has been submitted to your bank successfully. Your bank may take up to 7 days for the approval."
        }

        btnInvest?.setOnClickListener {
            back()
        }
    }

    private fun back() {
        if (getViewModel().isIMandate.get() == true) {
            onBack(4)
        } else {
            onBack(5)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: BankDetail) {
        getViewModel().bankMandate.set(data)
        removeStickyEvent(data)
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = BankMandateSuccessFragment().apply { arguments = basket }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                back()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

package com.tarrakki.module.bankmandate

import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import com.tarrakki.R
import com.tarrakki.api.model.BankDetail
import com.tarrakki.api.model.UserBankMandateResponse
import com.tarrakki.databinding.FragmentBankMandateSuccessBinding
import kotlinx.android.synthetic.main.fragment_bank_mandate_success.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import org.supportcompact.CoreFragment
import org.supportcompact.events.Event

class BankMandateSuccessFragment : CoreFragment<BankMandateSuccessVM, FragmentBankMandateSuccessBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_mandate)

    var isFromNew : Boolean ? = null

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
        setHasOptionsMenu(true)

        getViewModel().isIMandate.set(arguments?.getBoolean(ISIPMANDATE))

        if (getViewModel().isIMandate.get() == true) {
            tvBankMandateType?.text = getString(R.string.sip_mandate)
            tvContent?.text = "Your bank mandate request has been submitted to your bank successfully.\nYour bank may takes 7 days for the approval."
        }

        btnInvest?.setOnClickListener {
            onCheckStatus()
        }

        getBinding().root.isFocusableInTouchMode = true
        getBinding().root.requestFocus()
        getBinding().root.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                onCheckStatus()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    private fun onCheckStatus() {
        if (getViewModel().isIMandate.get() == true) {
            if (isFromNew == true){
                onBack(5)
            }else{
                onBack(4)
            }
        } else {
            if (arguments?.getBoolean(ISFROMDIRECTBANKMANDATE) == true) {
                onBack(3)
            } else {
                if (isFromNew == true){
                    onBack(6)
                }else{
                    onBack(5)
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: BankDetail) {
        getViewModel().bankMandate.set(data)
        getViewModel().bankLogo.set(data.bankLogo)
        getViewModel().bankName.set(data.branchBankIdBankName)
        getViewModel().accountNumber.set(data.accountNumber)
        getViewModel().branchName.set(data.branchBranchName)
        removeStickyEvent(data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: UserBankMandateResponse.Data.BankDetails) {
        //getViewModel().bankMandate.set(data)
        getViewModel().bankLogo.set(data.bankLogo)
        getViewModel().bankName.set(data.bankName)
        getViewModel().accountNumber.set(data.accountNumber)
        getViewModel().branchName.set(data.branchName)
        removeStickyEvent(data)
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = BankMandateSuccessFragment().apply { arguments = basket }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onCheckStatus()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Subscribe(sticky = true)
    override fun onEvent(event: Event) {
        super.onEvent(event)
        when (event) {
            Event.ISFROMNEWBANKMANDATE -> {
                isFromNew = true
            }
            Event.ISFROMBANKMANDATE -> {
                isFromNew = false
            }
        }
    }
}

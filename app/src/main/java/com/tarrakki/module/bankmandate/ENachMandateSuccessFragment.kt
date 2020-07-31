package com.tarrakki.module.bankmandate

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.tarrakki.R
import com.tarrakki.api.model.BankDetail
import com.tarrakki.api.model.UserBankMandateResponse
import com.tarrakki.databinding.FragmentBankMandateSuccessBinding
import com.tarrakki.databinding.FragmentENachMandateSuccessBinding
import kotlinx.android.synthetic.main.fragment_bank_mandate_success.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.events.Event

class ENachMandateSuccessFragment : CoreFragment<ENachSuccessMandateVM, FragmentENachMandateSuccessBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_mandate)

    var isFromNew: Boolean? = null

    override fun getLayout(): Int {
        return R.layout.fragment_e_nach_mandate_success
    }

    override fun createViewModel(): Class<out ENachSuccessMandateVM> {
        return ENachSuccessMandateVM::class.java
    }

    override fun setVM(binding: FragmentENachMandateSuccessBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        setHasOptionsMenu(true)

        getViewModel().isIMandate.set(arguments?.getBoolean(ISIPMANDATE))

        btnInvest?.setOnClickListener {
            onCheckStatus()
        }

        /*getBinding().root.isFocusableInTouchMode = true
        getBinding().root.requestFocus()
        getBinding().root.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                onCheckStatus()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }*/
        requireActivity().onBackPressedDispatcher.addCallback(this,object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                onCheckStatus()
            }
        })
    }

    private fun onCheckStatus() {
        onBackExclusive(BankMandateFragment::class.java)
        /*if (getViewModel().isIMandate.get() == true) {
            if (isFromNew == true) {
                onBack(5)
            } else {
                onBack(4)
            }
        } else {
            if (arguments?.getBoolean(ISFROMDIRECTBANKMANDATE) == true) {
                onBack(3)
            } else {
                if (isFromNew == true) {
                    onBack(6)
                } else {
                    onBack(5)
                }
            }
        }*/
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


    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = ENachMandateSuccessFragment().apply { arguments = basket }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onCheckStatus()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

  /*  @Subscribe(sticky = true)
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
    }*/
}

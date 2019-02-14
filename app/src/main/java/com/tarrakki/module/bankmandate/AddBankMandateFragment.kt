package com.tarrakki.module.bankmandate


import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tarrakki.IS_FROM_BANK_ACCOUNT
import com.tarrakki.R
import com.tarrakki.api.model.BankDetail
import com.tarrakki.api.model.UserBankMandateResponse
import com.tarrakki.databinding.FragmentBankMandateBinding
import com.tarrakki.databinding.RowBankMandateListItemBinding
import com.tarrakki.databinding.RowUserBankListMandateBinding
import com.tarrakki.getBankMandateStatus
import com.tarrakki.getUCropOptions
import com.tarrakki.module.bankaccount.AddBankAccountFragment
import com.tarrakki.module.bankaccount.SingleButton
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_bank_mandate.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.ChoiceMode
import org.supportcompact.adapters.KSelectionAdapter
import org.supportcompact.adapters.setUpAdapter
import org.supportcompact.events.Event
import org.supportcompact.ktx.*
import org.supportcompact.utilise.ImageChooserUtil
import java.io.File

class AddBankMandateFragment : CoreFragment<BankMandateVM, FragmentBankMandateBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_mandate)

    var userBankAdapter: KSelectionAdapter<BankDetail, RowUserBankListMandateBinding>? = null

    override fun getLayout(): Int {
        return R.layout.fragment_bank_mandate
    }

    override fun createViewModel(): Class<out BankMandateVM> {
        return BankMandateVM::class.java
    }

    override fun setVM(binding: FragmentBankMandateBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        btnAdd?.text = getString(R.string.add_bank_account)
        btnNext?.setOnClickListener {
                if (userBankAdapter?.selectedItemViewCount != 0) {
                    startFragment(AutoDebitFragment.newInstance(), R.id.frmContainer)
                    postSticky(userBankAdapter?.getSelectedItems()?.get(0) as BankDetail)
                    postSticky(Event.ISFROMNEWBANKMANDATE)
                } else {
                    context?.simpleAlert("Please Select Bank.")
                }
        }
        btnAdd?.setOnClickListener {
                startFragment(AddBankAccountFragment.newInstance(Bundle().apply {
                    putSerializable(IS_FROM_BANK_ACCOUNT, false)
                }), R.id.frmContainer)
        }
    }

    fun getUserBankAPI(){
        getViewModel().getAllBanks().observe(this, Observer { it1 ->
            getViewModel().isAddVisible.set(true)
            if (it1?.data?.bankDetails?.isNotEmpty() == true) {
                getViewModel().isNoBankAccount.set(false)
                getViewModel().isNextVisible.set(true)
                setUserBankAdapter(it1.data.bankDetails)
            } else {
                getViewModel().isNoBankAccount.set(true)
                getViewModel().isNextVisible.set(false)
            }
        })
    }

    private fun setUserBankAdapter(bankDetails: List<BankDetail>) {
        getViewModel().isMandateBankList.set(false)
        userBankAdapter = setUpAdapter(bankDetails as MutableList<BankDetail>,
                ChoiceMode.SINGLE,
                R.layout.row_user_bank_list_mandate,
                { item, binder: RowUserBankListMandateBinding?, position, adapter ->
                    binder?.widget = item
                    binder?.executePendingBindings()
                    binder?.isSelected = adapter.isItemViewToggled(position)

                }, { item, position, adapter ->

        }
        )
        rvBankMandate?.adapter = userBankAdapter
        val default_position = bankDetails.indexOfFirst { it.isDefault }
        if (default_position != -1) {
            userBankAdapter?.toggleItemView(default_position)
            userBankAdapter?.notifyItemChanged(default_position)
        }else{
            userBankAdapter?.toggleItemView(0)
            userBankAdapter?.notifyItemChanged(0)
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            Event.BANK_MANDATE_SUBMITTED -> {
                getViewModel().bankMandate.forEach { item ->
                    if (item is BankMandate) {
                        item.isPending = true
                    } else {
                        getViewModel().bankMandate.remove(item)
                    }
                }
                getViewModel().bankMandate.add(SingleButton(R.string.add_new_bank_account))
                rvBankMandate?.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        getUserBankAPI()
        super.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = AddBankMandateFragment().apply { arguments = basket }
    }
}
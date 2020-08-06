package com.tarrakki.module.bankmandate


import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.tarrakki.App
import com.tarrakki.BR
import com.tarrakki.IS_FROM_BANK_ACCOUNT
import com.tarrakki.R
import com.tarrakki.api.model.BankDetail
import com.tarrakki.api.model.UserBanksResponse
import com.tarrakki.databinding.FragmentBankMandateBinding
import com.tarrakki.databinding.RowUserBankListMandateBinding
import com.tarrakki.module.bankaccount.AddBankAccountFragment
import com.tarrakki.module.bankaccount.SingleButton
import com.tarrakki.module.paymentmode.BANKACCOUNTNUMBER
import com.tarrakki.module.paymentmode.ISFROMPAYMENTMODE
import kotlinx.android.synthetic.main.fragment_bank_mandate.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.ChoiceMode
import org.supportcompact.adapters.KSelectionAdapter
import org.supportcompact.adapters.setUpAdapter
import org.supportcompact.events.Event
import org.supportcompact.ktx.setENachLimit
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment

class AddBankMandateFragment : CoreFragment<BankMandateVM, FragmentBankMandateBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = if (arguments?.getBoolean(ISFROMPAYMENTMODE, false) != true) {
            getString(R.string.bank_mandate)
        } else {
            getString(R.string.bank_accounts)
        }

    var userBankAdapter: KSelectionAdapter<BankDetail, RowUserBankListMandateBinding>? = null
    var isFromPaymentMode: Boolean? = null
    var currentBank: String? = ""

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
        isFromPaymentMode = arguments?.getBoolean(ISFROMPAYMENTMODE, false)
        if (isFromPaymentMode == true) {
            currentBank = arguments?.getString(BANKACCOUNTNUMBER, "")
        }

        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let { isRefreshing ->
                mRefresh?.isRefreshing = false
                App.INSTANCE.isRefreshing.value = null
            }
        })

        btnAdd?.text = getString(R.string.add_bank_account)
        if (isFromPaymentMode == true) {
            btnSelectBankMandate?.text = getString(R.string.select_bank_use)
            getViewModel().isSelectBankVisible.set(true)
        }

        btnSelectBankMandate?.setOnClickListener {
            if (userBankAdapter?.selectedItemViewCount ?: 0 > 0) {
                onBack()
                postSticky(userBankAdapter?.getSelectedItems()?.get(0) as BankDetail)
            } else {
                context?.simpleAlert(getString(R.string.alert_req_bank))
            }
        }

        btnNext?.setOnClickListener {
            if (userBankAdapter?.selectedItemViewCount ?: 0 > 0) {
                startFragment(AutoDebitFragment.newInstance(), R.id.frmContainer)
                postSticky(userBankAdapter?.getSelectedItems()?.get(0) as BankDetail)
                postSticky(Event.ISFROMNEWBANKMANDATE)
            } else {
                context?.simpleAlert(getString(R.string.alert_req_bank))
            }
        }

        btnAdd?.setOnClickListener {
            startFragment(AddBankAccountFragment.newInstance(Bundle().apply {
                putSerializable(IS_FROM_BANK_ACCOUNT, false)
            }), R.id.frmContainer)
        }

        mRefresh?.setOnRefreshListener {
            getUserBankAPI(true)
        }
    }

    fun getUserBankAPI(isRefreshing: Boolean = false) {
        getViewModel().getAllBanks(isRefreshing).observe(this, Observer { it1 ->
            getViewModel().isAddVisible.set(true)
            context?.setENachLimit(it1?.data?.enachLimit.toString())
            if (it1?.data?.bankDetails?.isNotEmpty() == true) {
                getViewModel().isNoBankAccount.set(false)
                if (isFromPaymentMode == true) {
                    getViewModel().isNextVisible.set(false)
                } else {
                    getViewModel().isNextVisible.set(true)
                }
                setUserBankAdapter(it1)
            } else {
                getViewModel().isNoBankAccount.set(true)
                getViewModel().isNextVisible.set(false)
                getViewModel().isSelectBankVisible.set(false)
            }
        })
    }

    private fun setUserBankAdapter(banRes: UserBanksResponse) {
        getViewModel().isMandateBankList.set(false)
        userBankAdapter = setUpAdapter(banRes.data.bankDetails as MutableList<BankDetail>,
                ChoiceMode.SINGLE,
                R.layout.row_user_bank_list_mandate,
                { item, binder: RowUserBankListMandateBinding?, position, adapter ->
                    binder?.widget = item
                    binder?.executePendingBindings()
                    binder?.isSelected = adapter.isItemViewToggled(position)
                    binder?.setVariable(BR.onUpdate, View.OnClickListener {
                        val bundle = Bundle()
                        banRes.data.bankDetail = item
                        bundle.putString("userBankData", Gson().toJson(banRes))
                        startFragment(AddBankAccountFragment.newInstance(bundle.apply { putSerializable(IS_FROM_BANK_ACCOUNT, true) }), R.id.frmContainer)

                    })
                },
                { item, position, adapter ->
                    if (item.checkBoxVisibility == View.VISIBLE) {
                        adapter.toggleItemView(position)
                    }
                }, false
        )
        rvBankMandate?.adapter = userBankAdapter
        if (isFromPaymentMode == true) {
            val position = banRes.data.bankDetails.indexOfFirst { it.accountNumber == currentBank }
            if (position != -1) {
                userBankAdapter?.toggleItemView(position)
                userBankAdapter?.notifyItemChanged(position)
            }
        } else {
            val default_position = banRes.data.bankDetails.indexOfFirst { it.isValid }
            if (default_position != -1) {
                userBankAdapter?.toggleItemView(default_position)
                userBankAdapter?.notifyItemChanged(default_position)
            }/* else {
                userBankAdapter?.toggleItemView(0)
                userBankAdapter?.notifyItemChanged(0)
            }*/
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

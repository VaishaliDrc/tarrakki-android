package com.tarrakki.module.bankmandate


import android.arch.lifecycle.Observer
import android.os.Bundle
import com.tarrakki.IS_FROM_BANK_ACCOUNT
import com.tarrakki.R
import com.tarrakki.api.model.BankDetail
import com.tarrakki.api.model.UserBankMandateResponse
import com.tarrakki.databinding.FragmentBankMandateBinding
import com.tarrakki.databinding.RowBankMandateListItemBinding
import com.tarrakki.databinding.RowUserBankListMandateBinding
import com.tarrakki.getBankMandateStatus
import com.tarrakki.module.bankaccount.AddBankAccountFragment
import kotlinx.android.synthetic.main.fragment_bank_mandate.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.ChoiceMode
import org.supportcompact.adapters.KSelectionAdapter
import org.supportcompact.adapters.setUpAdapter
import org.supportcompact.events.Event
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment

const val ISFROMDIRECTBANKMANDATE = "isfromdirectbankmandate"
const val MANDATEID = "mandate_id"

class BankMandateFragment : CoreFragment<BankMandateVM, FragmentBankMandateBinding>() {

    private var mandateId = ""
    private var isMandate: Boolean? = null

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_mandate)

    var mandateBankAdapter: KSelectionAdapter<UserBankMandateResponse.Data, RowBankMandateListItemBinding>? = null
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
        btnNext?.setOnClickListener {
            if (getViewModel().isMandateBankList.get() != true) {
                if (userBankAdapter?.selectedItemViewCount != 0) {
                    startFragment(AutoDebitFragment.newInstance(), R.id.frmContainer)
                    postSticky(userBankAdapter?.getSelectedItems()?.get(0) as BankDetail)
                    postSticky(Event.ISFROMBANKMANDATE)
                } else {
                    context?.simpleAlert("Please Select Bank.")
                }
            }
        }
        btnAdd?.setOnClickListener {
            if (isMandate == false) {
                startFragment(AddBankAccountFragment.newInstance(Bundle().apply {
                    putSerializable(IS_FROM_BANK_ACCOUNT, false)
                }), R.id.frmContainer)
            } else {
                startFragment(AddBankMandateFragment.newInstance(), R.id.frmContainer)
            }
        }
    }

    private fun getBanksData() {
        getViewModel().getAllMandateBanks().observe(this, Observer {
            if (it?.data?.isNotEmpty() == true) {
                isMandate = true
                getViewModel().isNoBankAccount.set(false)
                getViewModel().isNextVisible.set(false)
                getViewModel().isAddVisible.set(true)
                setUserBankMandateAdapter(it.data)
            } else {
                isMandate = false
                getUserBankAPI()
            }
        })
    }

    fun getUserBankAPI() {
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

    private fun setUserBankMandateAdapter(bankDetails: List<UserBankMandateResponse.Data>) {
        getViewModel().isMandateBankList.set(true)
        mandateBankAdapter = setUpAdapter(bankDetails as MutableList<UserBankMandateResponse.Data>,
                ChoiceMode.SINGLE,
                R.layout.row_bank_mandate_list_item,
                { item, binder: RowBankMandateListItemBinding?, position, adapter ->
                    binder?.widget = item
                    binder?.executePendingBindings()
                    binder?.isSelected = adapter.isItemViewToggled(position)

                    binder?.tvPending?.setBackgroundResource(item.statuscolor)

                    binder?.btnUploadSanned?.setOnClickListener {
                        val bundle = Bundle().apply {
                            putBoolean(ISFROMDIRECTBANKMANDATE, true)
                            putString(MANDATEID, item.id.toString())
                        }
                        startFragment(BankMandateFormFragment.newInstance(bundle), R.id.frmContainer)
                        postSticky(item.bankDetails)
                    }
                }, { item, position, adapter ->

        })
        rvBankMandate?.adapter = mandateBankAdapter
        mandateBankAdapter?.toggleItemView(0)
        mandateBankAdapter?.notifyItemChanged(0)
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
        } else {
            userBankAdapter?.toggleItemView(0)
            userBankAdapter?.notifyItemChanged(0)
        }
    }

    override fun onResume() {
        getBanksData()
        super.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = BankMandateFragment().apply { arguments = basket }
    }
}

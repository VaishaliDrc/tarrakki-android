package com.tarrakki.module.bankmandate


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.IS_FROM_BANK_ACCOUNT
import com.tarrakki.R
import com.tarrakki.api.model.BankDetail
import com.tarrakki.api.model.UserBankMandateResponse
import com.tarrakki.databinding.FragmentBankMandateBinding
import com.tarrakki.databinding.RowBankMandateListItemBinding
import com.tarrakki.databinding.RowUserBankListMandateBinding
import com.tarrakki.module.bankaccount.AddBankAccountFragment
import com.tarrakki.module.bankaccount.SingleButton
import kotlinx.android.synthetic.main.fragment_bank_mandate.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.ChoiceMode
import org.supportcompact.adapters.KSelectionAdapter
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpAdapter
import org.supportcompact.events.Event
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [BankMandateFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class BankMandateFragment : CoreFragment<BankMandateVM, FragmentBankMandateBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_mandate)

    var mandateBankAdapter : KSelectionAdapter<UserBankMandateResponse.Data,RowBankMandateListItemBinding> ? = null
    var userBankAdapter : KSelectionAdapter<BankDetail,RowUserBankListMandateBinding> ? = null

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
        /* rvBankMandate?.setUpMultiViewRecyclerAdapter(getViewModel().bankMandate) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
             binder.setVariable(BR.widget, item)
             binder.setVariable(BR.onNext, View.OnClickListener {
                 startFragment(AutoDebitFragment.newInstance(), R.id.frmContainer)
                 EventBus.getDefault().postSticky(getViewModel().bankMandate[0])
             })
             binder.setVariable(BR.onAdd, View.OnClickListener {
                 startFragment(AddBankAccountFragment.newInstance(Bundle().apply { putSerializable(IS_FROM_BANK_ACCOUNT, false) }), R.id.frmContainer)
             })
             binder.executePendingBindings()
         }*/

        btnNext?.setOnClickListener {
            if (getViewModel().isMandateBankList.get()==true){

            }else{
                if (userBankAdapter?.selectedItemViewCount!=0){
                    startFragment(AutoDebitFragment.newInstance(), R.id.frmContainer)
                    postSticky(userBankAdapter?.getSelectedItems()?.get(0) as BankDetail)
                }else{
                    context?.simpleAlert("Please Select Bank.")
                }
            }
        }

        btnAdd?.setOnClickListener {
            startFragment(AddBankAccountFragment.newInstance(Bundle().apply { putSerializable(IS_FROM_BANK_ACCOUNT, false) }), R.id.frmContainer)
        }
    }

    private fun getBanksData(){
        getViewModel().getAllMandateBanks().observe(this, Observer {
            if (it?.data?.isNotEmpty()==true) {
                setUserBankMandateAdapter(it.data)
                getViewModel().isNextVisible.set(true)
            }else{
                getViewModel().getAllBanks().observe(this, Observer {it1->
                    if (!it1?.data?.bankDetails.isNullOrEmpty()) {
                        getViewModel().isNextVisible.set(true)
                        it1?.data?.bankDetails?.let { it2 -> setUserBankAdapter(it2) }
                    }else{
                        getViewModel().isNextVisible.set(false)
                    }
                })
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

                }, { item, position, adapter ->

        })
        rvBankMandate?.adapter = mandateBankAdapter
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
        /*val default_position = bankDetails.indexOfFirst { it.isDefault }
        if (default_position != -1) {
            adapter.toggleItemView(default_position)
            adapter.notifyItemChanged(default_position)
        }
*/
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
        getBanksData()
        super.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = BankMandateFragment().apply { arguments = basket }
    }
}

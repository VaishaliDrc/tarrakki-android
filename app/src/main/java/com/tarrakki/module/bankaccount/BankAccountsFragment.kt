package com.tarrakki.module.bankaccount


import android.arch.lifecycle.Observer
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.tarrakki.BR
import com.tarrakki.IS_FROM_BANK_ACCOUNT
import com.tarrakki.R
import com.tarrakki.api.model.BankDetail
import com.tarrakki.api.model.UserBanksResponse
import com.tarrakki.databinding.FragmentBankAccountsBinding
import kotlinx.android.synthetic.main.fragment_bank_accounts.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [BankAccountsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class BankAccountsFragment : CoreFragment<BankAccountsVM, FragmentBankAccountsBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_accounts)

    override fun getLayout(): Int {
        return R.layout.fragment_bank_accounts
    }

    override fun createViewModel(): Class<out BankAccountsVM> {
        return BankAccountsVM::class.java
    }

    override fun setVM(binding: FragmentBankAccountsBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        val bankObserver = Observer<UserBanksResponse> { r ->
            r?.let { userBanksResponse ->
                val banks = arrayListOf<WidgetsViewModel>()
                if (userBanksResponse.data.bankDetails.isEmpty()) {
                    banks.add(NoBankAccount())
                } else {
                    banks.addAll(userBanksResponse.data.bankDetails)
                }
                banks.add(SingleButton(R.string.add_new_bank_account))
                rvBanks?.setUpMultiViewRecyclerAdapter(banks) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                    binder.setVariable(BR.widget, item)
                    binder.setVariable(BR.onAdd, View.OnClickListener {
                        startFragment(AddBankAccountFragment.newInstance(Bundle().apply { putSerializable(IS_FROM_BANK_ACCOUNT, true) }), R.id.frmContainer)
                    })
                    binder.setVariable(BR.setDefault, View.OnClickListener {
                        if (item is BankDetail) {
                            getViewModel().setDefault("${item.id}").observe(this@BankAccountsFragment, Observer {
                                coreActivityVM?.onNewBank?.value = true
                            })
                        }
                    })
                    binder.executePendingBindings()
                }
            }
        }
        coreActivityVM?.onNewBank?.observe(this, Observer {
            getViewModel().getAllBanks().observe(this, bankObserver)
        })
        getViewModel().getAllBanks().observe(this, bankObserver)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment BankAccountsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = BankAccountsFragment().apply { arguments = basket }
    }
}

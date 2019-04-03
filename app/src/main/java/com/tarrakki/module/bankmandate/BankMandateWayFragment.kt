package com.tarrakki.module.bankmandate


import android.arch.lifecycle.Observer
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.View
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.model.BankDetail
import com.tarrakki.api.model.IMandateResponse
import com.tarrakki.api.model.UserMandateDownloadResponse
import com.tarrakki.api.model.parseTo
import com.tarrakki.databinding.FragmentBankMandateWayBinding
import kotlinx.android.synthetic.main.fragment_bank_mandate_way.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.isCompletedRegistration
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment

const val ISIPMANDATE = "isipmandate"
const val IMANDATEDATA = "imandatedata"

class BankMandateWayFragment : CoreFragment<BankMandateWayVM, FragmentBankMandateWayBinding>() {

    var amount = ""

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_mandate)

    override fun getLayout(): Int {
        return R.layout.fragment_bank_mandate_way
    }

    override fun createViewModel(): Class<out BankMandateWayVM> {
        return BankMandateWayVM::class.java
    }

    override fun setVM(binding: FragmentBankMandateWayBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        amount = arguments?.getString(AMOUNT).toString()

        var selectedAt = 0
        rvBankMandateWay?.setUpMultiViewRecyclerAdapter(getViewModel().bankMandateWays) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.widget, item)
            binder.setVariable(BR.onAdd, View.OnClickListener {
                if (context?.isCompletedRegistration() == true){
                    for (viewmodel in getViewModel().bankMandateWays) {
                        if (viewmodel is BankMandateWay) {
                            if (viewmodel.isSelected) {
                                val type = if (viewmodel.title == R.string.sip_mandate) {
                                    "I"
                                } else {
                                    "X"
                                }
                                getViewModel().addMandateBank(getViewModel().bankMandate.get()?.id, amount,
                                        type).observe(this, Observer {
                                    if (viewmodel.title == R.string.sip_mandate) {
                                        val data = it?.data?.parseTo<IMandateResponse>()
                                        val html = data?.data_html
                                        val bundle = Bundle().apply {
                                            putString(AMOUNT, amount)
                                            putBoolean(ISIPMANDATE, true)
                                            putString(IMANDATEDATA, html)
                                        }
                                        startFragment(BankMandateFormFragment.newInstance(bundle), R.id.frmContainer)
                                    } else {
                                        val data = it?.data?.parseTo<UserMandateDownloadResponse>()
                                        val bundle = Bundle().apply {
                                            putString(AMOUNT, amount)
                                            putBoolean(ISIPMANDATE, false)
                                        }
                                        startFragment(BankMandateFormFragment.newInstance(bundle), R.id.frmContainer)
                                        data?.let { it1 -> postSticky(it1) }
                                    }
                                })
                                break
                            }
                        }
                    }
                }else{
                    context?.simpleAlert("Please first complete your registration to place the bank mandate request.")
                }
            })
            binder.root.setOnClickListener {
                if (item is BankMandateWay) {
                    val data = getViewModel().bankMandateWays[selectedAt]
                    if (data is BankMandateWay) {
                        data.isSelected = false
                    }
                    item.isSelected = true
                    selectedAt = position
                }
            }
            binder.executePendingBindings()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: BankDetail) {
        getViewModel().bankMandate.set(data)
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = BankMandateWayFragment().apply { arguments = basket }
    }
}

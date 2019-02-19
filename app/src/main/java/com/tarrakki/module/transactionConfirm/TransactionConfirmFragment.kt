package com.tarrakki.module.transactionConfirm


import android.os.Bundle
import android.view.View
import com.tarrakki.R
import com.tarrakki.databinding.FragmentTransactionConfirmBinding
import com.tarrakki.databinding.RowTransactionConfirmBinding
import com.tarrakki.databinding.RowTranscationStatusBinding
import kotlinx.android.synthetic.main.fragment_transaction_confirm.*
import kotlinx.android.synthetic.main.row_transaction_confirm.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.ChoiceMode
import org.supportcompact.adapters.setUpAdapter

class TransactionConfirmFragment : CoreFragment<TransactionConfirmVM, FragmentTransactionConfirmBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.transaction_confirm)

    override fun getLayout(): Int {
        return R.layout.fragment_transaction_confirm
    }

    override fun createReference() {
        setOrderItemsAdapter(getViewModel().list)
    }

    override fun createViewModel(): Class<out TransactionConfirmVM> {
        return TransactionConfirmVM::class.java
    }

    override fun setVM(binding: FragmentTransactionConfirmBinding) {
        getBinding().vm = getViewModel()
        getBinding().executePendingBindings()
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = TransactionConfirmFragment().apply { arguments = basket }
    }

    private fun setOrderItemsAdapter(list : List<TransactionConfirmVM.TransactionConfirm>){
        val adapter = setUpAdapter(list as MutableList<TransactionConfirmVM.TransactionConfirm>,
                ChoiceMode.NONE,
                R.layout.row_transaction_confirm,
                { item, binder: RowTransactionConfirmBinding?, position, adapter ->
                    binder?.widget = item
                    binder?.executePendingBindings()

                    binder?.imgArrow?.setOnClickListener {
                        binder.expStatus.toggle()
                        binder.isExpanded = binder.expStatus.isExpanded
                    }

                    val statusAdapter = setUpAdapter(item.status,
                            ChoiceMode.NONE,
                            R.layout.row_transcation_status,
                            { item1, binder1: RowTranscationStatusBinding?, position1, adapter ->
                                binder1?.widget = item1
                                binder1?.executePendingBindings()
                                if (position1==item.status.size-1){
                                    binder1?.verticalDivider?.visibility = View.GONE
                                }else{
                                    binder1?.verticalDivider?.visibility = View.VISIBLE
                                }

                            }, { item, position, adapter ->

                    },false)
                    binder?.rvTransactionStatus?.adapter = statusAdapter


                }, { item, position, adapter ->

        },false)
        rvOrderItems?.adapter = adapter

    }

}

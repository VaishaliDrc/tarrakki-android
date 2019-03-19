package com.tarrakki.module.redeem


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.tarrakki.R
import com.tarrakki.databinding.FragmentRedemptionStatusBinding
import com.tarrakki.databinding.RowTransactionListStatusBinding
import com.tarrakki.module.transactionConfirm.TransactionConfirmVM
import kotlinx.android.synthetic.main.fragment_redemption_status.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView

/**
 * A simple [Fragment] subclass.
 * Use the [RedemptionStatusFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class RedemptionStatusFragment : CoreFragment<RedeemConfirmVM, FragmentRedemptionStatusBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.redeem_status)

    override fun getLayout(): Int {
        return R.layout.fragment_redemption_status
    }

    override fun createViewModel(): Class<out RedeemConfirmVM> {
        return RedeemConfirmVM::class.java
    }

    override fun setVM(binding: FragmentRedemptionStatusBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        val statuslist = arrayListOf<TransactionConfirmVM.TranscationStatuss>()
        statuslist.add(TransactionConfirmVM.TranscationStatuss("Withdrawal Sent to AMC", "12 Mar 2019, 01:34 PM", "completed"))
        statuslist.add(TransactionConfirmVM.TranscationStatuss("Withdrawal Confirmation", "", "In progress"))
        statuslist.add(TransactionConfirmVM.TranscationStatuss("Amount Credited", "", "Pending"))
        val adapter = rvTransactionStatus?.setUpRecyclerView(R.layout.row_transaction_list_status, statuslist)
        { item2: TransactionConfirmVM.TranscationStatuss, binder2: RowTransactionListStatusBinding, position2: Int ->
            binder2.widget = item2
            binder2.executePendingBindings()
            if (position2 == statuslist.size - 1) {
                binder2.verticalDivider.visibility = View.GONE
            } else {
                binder2.verticalDivider.visibility = View.VISIBLE
            }
        }
        rvTransactionStatus?.adapter = adapter
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment RedemptionStatusFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = RedemptionStatusFragment().apply { arguments = basket }
    }
}

package com.tarrakki.module.redeem


import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import com.tarrakki.R
import com.tarrakki.api.model.UserPortfolioResponse
import com.tarrakki.databinding.FragmentRedemptionStatusBinding
import com.tarrakki.databinding.RowTransactionListStatusBinding
import com.tarrakki.module.transactionConfirm.TransactionConfirmVM
import kotlinx.android.synthetic.main.fragment_redeem_confirm.*
import kotlinx.android.synthetic.main.fragment_redemption_status.*
import kotlinx.android.synthetic.main.fragment_redemption_status.tvName
import kotlinx.android.synthetic.main.fragment_redemption_status.tvNote
import kotlinx.android.synthetic.main.fragment_redemption_status.tvUnits
import org.greenrobot.eventbus.Subscribe
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
        setHasOptionsMenu(true)
        val statuslist = arrayListOf<TransactionConfirmVM.TranscationStatuss>()
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
        getViewModel().directRedeemFund.observe(this, Observer {
            it?.let { fund ->
                val isFailed = "Failed".equals(fund.redeemedStatus?.data?.withdrawalSent, true)
                tvRemark?.text = fund.redeemedStatus?.data?.remarks
                tvAmount?.setText(if (fund.isInstaRedeem) R.string.amount else R.string.units)
                tvName?.text = fund.fundName

                if((fund.isInstaRedeem)){
                    tvUnits?.text = fund.redeemUnits
                }else{
                    fund.redeemUnits?.let {
                        if(it.isEmpty()){
                            tvAmount?.setText(R.string.amount)
                            tvUnits?.text = fund.redeemAmount
                        }else{
                            tvAmount?.setText(R.string.units)
                            tvUnits?.text = fund.redeemUnits
                        }
                    }
                }
                getBinding().bank = fund.bank
                getBinding().isFailed = isFailed
                getBinding().executePendingBindings()
                statuslist.clear()
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Withdrawal Sent to AMC", "".plus(fund.redeemedStatus?.data?.dateTime
                        ?: ""), "${fund.redeemedStatus?.data?.withdrawalSent}"))
                if (!isFailed) {
                    statuslist.add(TransactionConfirmVM.TranscationStatuss("Withdrawal Confirmation", "", "${fund.redeemedStatus?.data?.withdrawalConfirm}"))
                    if (fund.isInstaRedeem) {
                        tvTypeL?.visibility = View.GONE
                        tvType?.visibility = View.GONE
                        tvNote?.setText(R.string.we_have_sent_you_withdrawal_request_tinsta_redemption)
                        statuslist.add(TransactionConfirmVM.TranscationStatuss("Amount Credited", "", "${fund.redeemedStatus?.data?.amountCreadited}"))
                    }
                }
                adapter?.notifyDataSetChanged()
            }
        })
        getViewModel().goalBasedRedeemFund.observe(this, Observer {
            it?.let { fund ->
                val isFailed = "Failed".equals(fund.redeemedStatus?.data?.withdrawalSent, true)
                tvRemark?.text = fund.redeemedStatus?.data?.remarks
                tvAmount?.setText(if (fund.isInstaRedeem) R.string.amount else R.string.units)
                tvName?.text = fund.fundName
                tvUnits?.text = fund.redeemUnits
                getBinding().bank = fund.bank
                getBinding().isFailed = isFailed
                getBinding().executePendingBindings()
                statuslist.clear()
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Withdrawal Sent to AMC", "".plus(fund.redeemedStatus?.data?.dateTime
                        ?: ""), "${fund.redeemedStatus?.data?.withdrawalSent}"))
                if (!isFailed) {
                    statuslist.add(TransactionConfirmVM.TranscationStatuss("Withdrawal Confirmation", "", "${fund.redeemedStatus?.data?.withdrawalConfirm}"))
                    if (fund.isInstaRedeem) {
                        tvTypeL?.visibility = View.GONE
                        tvType?.visibility = View.GONE
                        tvNote?.setText(R.string.we_have_sent_you_withdrawal_request_tinsta_redemption)
                        statuslist.add(TransactionConfirmVM.TranscationStatuss("Amount Credited", "", "${fund.redeemedStatus?.data?.amountCreadited}"))
                    }
                }
                adapter?.notifyDataSetChanged()
            }
        })
        getViewModel().tarrakkiZyaadaRedeemFund.observe(this, Observer {
            it?.let { fund ->
                val isFailed = "Failed".equals(fund.redeemedStatus?.data?.withdrawalSent, true)
                tvRemark?.text = fund.redeemedStatus?.data?.remarks
                tvAmount?.setText(if (fund.isInstaRedeem) R.string.amount else R.string.units)
                tvName?.text = fund.fundName
                tvUnits?.text = fund.redeemUnits
                getBinding().bank = fund.bank
                getBinding().isFailed = isFailed
                getBinding().executePendingBindings()
                statuslist.clear()
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Withdrawal Sent to AMC", "".plus(fund.redeemedStatus?.data?.dateTime
                        ?: ""), "${fund.redeemedStatus?.data?.withdrawalSent}"))
                if (!isFailed) {
                    statuslist.add(TransactionConfirmVM.TranscationStatuss("Withdrawal Confirmation", "", "${fund.redeemedStatus?.data?.withdrawalConfirm}"))
                    if (fund.isInstaRedeem) {
                        tvTypeL?.visibility = View.GONE
                        tvType?.visibility = View.GONE
                        tvNote?.setText(R.string.we_have_sent_you_withdrawal_request_tinsta_redemption)
                        statuslist.add(TransactionConfirmVM.TranscationStatuss("Amount Credited", "", "${fund.redeemedStatus?.data?.amountCreadited}"))
                    }
                }
                adapter?.notifyDataSetChanged()
            }
        })
        rvTransactionStatus?.adapter = adapter
        getBinding().root.isFocusableInTouchMode = true
        getBinding().root.requestFocus()
        getBinding().root.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                onBackPress()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPress()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onBackPress() {
        //onBack(if (getViewModel().goalBasedRedeemFund.value == null) 2 else 3)
        onBack(if (getViewModel().goalBasedRedeemFund.value == null) 3 else 4)
    }

    @Subscribe(sticky = true)
    fun onReemFund(item: UserPortfolioResponse.Data.DirectInvestment) {
        if (getViewModel().directRedeemFund.value == null) {
            getViewModel().directRedeemFund.value = item
        }
        removeStickyEvent(item)
    }

    @Subscribe(sticky = true)
    fun onReemFund(item: UserPortfolioResponse.Data.GoalBasedInvestment.Fund) {
        if (getViewModel().goalBasedRedeemFund.value == null) {
            getViewModel().goalBasedRedeemFund.value = item
        }
        removeStickyEvent(item)
    }

    @Subscribe(sticky = true)
    fun onReemFund(item: UserPortfolioResponse.Data.TarrakkiZyaadaInvestment) {
        if (getViewModel().tarrakkiZyaadaRedeemFund.value == null) {
            getViewModel().tarrakkiZyaadaRedeemFund.value = item
        }
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

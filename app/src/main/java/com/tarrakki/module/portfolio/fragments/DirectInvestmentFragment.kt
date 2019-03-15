package com.tarrakki.module.portfolio.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.gson.JsonObject
import com.tarrakki.*
import com.tarrakki.api.model.*
import com.tarrakki.databinding.FragmentDirectInvestmentBinding
import com.tarrakki.databinding.RowDirectInvestmentListItemBinding
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.portfolio.PortfolioVM
import kotlinx.android.synthetic.main.fragment_direct_investment.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.*
import org.supportcompact.utilise.EqualSpacingItemDecoration
import java.util.*
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import kotlinx.android.synthetic.main.row_table_layout_content.view.*
import android.widget.TableLayout
import android.view.ViewGroup.LayoutParams.FILL_PARENT
import android.view.ViewGroup.LayoutParams.FILL_PARENT
import android.widget.TableRow


class DirectInvestmentFragment : CoreFragment<PortfolioVM, FragmentDirectInvestmentBinding>() {

    var vm: PortfolioVM? = null

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.portfolio)

    override fun getLayout(): Int {
        return R.layout.fragment_direct_investment
    }

    override fun createViewModel(): Class<out PortfolioVM> {
        return PortfolioVM::class.java
    }

    override fun setVM(binding: FragmentDirectInvestmentBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        rvDInvests?.addItemDecoration(EqualSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.space_item)))

        parentFragment?.let {
            vm = ViewModelProviders.of(it).get(PortfolioVM::class.java)
        }

        vm?.isRefreshing?.observe(this, Observer {
            it?.let { isRefreshing ->
                mRefresh?.isRefreshing = false
            }
        })

        vm?.let { vm ->
            vm.portfolioData.observe(this, Observer {
                if (!it?.data?.directInvestment.isNullOrEmpty()) {
                    getViewModel().isDirectEmpty.set(false)
                    rvDInvests?.setUpRecyclerView(R.layout.row_direct_investment_list_item, it?.data?.directInvestment as ArrayList<UserPortfolioResponse.Data.DirectInvestment>) { item: UserPortfolioResponse.Data.DirectInvestment, binder: RowDirectInvestmentListItemBinding, position ->
                        binder.investment = item
                        binder.executePendingBindings()

                        for (folioList in item.folioList){
                            val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                            val tableRow = TableRow(context)
                            tableRow.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)

                            val columnFolioNo = inflater.inflate(R.layout.row_table_layout_content,null,false)
                            columnFolioNo.tvTableRowContent.text = folioList.folioNo
                            tableRow.addView(columnFolioNo)

                            val columnTotalInvestment = inflater.inflate(R.layout.row_table_layout_content,null,false)
                            columnTotalInvestment.tvTableRowContent.text = folioList.amount.toDouble().toCurrency()
                            tableRow.addView(columnTotalInvestment)

                            val columnCurrentValue = inflater.inflate(R.layout.row_table_layout_content,null,false)
                            columnCurrentValue.tvTableRowContent.text = folioList.currentValue.toCurrency()
                            tableRow.addView(columnCurrentValue)

                            val columnUnits = inflater.inflate(R.layout.row_table_layout_content,null,false)
                            columnUnits.tvTableRowContent.text = "20"
                            tableRow.addView(columnUnits)

                            val columnReturn = inflater.inflate(R.layout.row_table_layout_content,null,false)
                            columnReturn.tvTableRowContent.text = "2 %"
                            tableRow.addView(columnReturn)

                            binder.tlfolio.addView(tableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))
                        }

                        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                        val tableRow = TableRow(context)
                        tableRow.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)

                        val columnFolioNo = inflater.inflate(R.layout.row_table_layout_content,null,false)
                        columnFolioNo.tvTableRowContent.text = "Total"
                        context?.color(R.color.black)?.let { it1 -> columnFolioNo.tvTableRowContent.setTextColor(it1) }
                        tableRow.addView(columnFolioNo)

                        val columnTotalInvestment = inflater.inflate(R.layout.row_table_layout_content,null,false)
                        columnTotalInvestment.tvTableRowContent.text = 20000.00.toCurrency()
                        context?.color(R.color.black)?.let { it1 -> columnTotalInvestment.tvTableRowContent.setTextColor(it1) }
                        tableRow.addView(columnTotalInvestment)

                        val columnCurrentValue = inflater.inflate(R.layout.row_table_layout_content,null,false)
                        columnCurrentValue.tvTableRowContent.text = 200000.00.toCurrency()
                        context?.color(R.color.black)?.let { it1 -> columnCurrentValue.tvTableRowContent.setTextColor(it1) }
                        tableRow.addView(columnCurrentValue)

                        val columnUnits = inflater.inflate(R.layout.row_table_layout_content,null,false)
                        columnUnits.tvTableRowContent.text = "200"
                        context?.color(R.color.black)?.let { it1 -> columnUnits.tvTableRowContent.setTextColor(it1) }
                        tableRow.addView(columnUnits)

                        val columnReturn = inflater.inflate(R.layout.row_table_layout_content,null,false)
                        columnReturn.tvTableRowContent.text = "22 %"
                        context?.color(R.color.black)?.let { it1 -> columnReturn.tvTableRowContent.setTextColor(it1) }
                        tableRow.addView(columnReturn)

                        binder.tlfolio.addView(tableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))

                        binder.tvAddPortfolio.setOnClickListener {
                            val folios: MutableList<FolioData> = mutableListOf()
                            for (folio in item.folioList) {
                                folios.add(FolioData(folio.currentValue,folio.amount, folio.folioNo))
                            }
                            context?.addFundPortfolioDialog(folios, item.validminlumpsumAmount,
                                    item.validminSIPAmount) { portfolio, amountLumpsum, amountSIP ->
                                addToCartPortfolio(item.fundId, amountSIP.toString(),
                                        amountLumpsum.toString(), portfolio).observe(this,
                                        android.arch.lifecycle.Observer { response ->
                                            context?.simpleAlert(getString(R.string.cart_fund_added)) {
                                                startFragment(CartFragment.newInstance(), R.id.frmContainer)
                                            }
                                        })
                            }
                        }

                        binder.tvRedeem.setOnClickListener {
                            val folios: MutableList<FolioData> = mutableListOf()
                            for (folio in item.folioList) {
                                folios.add(FolioData(folio.currentValue,folio.amount, folio.folioNo))
                            }
                            context?.redeemFundPortfolioDialog(folios) { portfolioNo, totalAmount, allRedeem, amount ->
                                val json = JsonObject()
                                json.addProperty("user_id", App.INSTANCE.getUserId())
                                json.addProperty("fund_id", item.fundId)
                                json.addProperty("all_redeem", allRedeem)
                                json.addProperty("amount", totalAmount)
                                json.addProperty("folio_number", portfolioNo)
                                val data = json.toString().toEncrypt()
                                redeemPortfolio(data).observe(this, Observer {
                                    context?.simpleAlert("Your redemption of amount ${amount.toCurrencyBigInt().toCurrency()} is successful.") {
                                        vm.getUserPortfolio()
                                    }
                                })
                            }
                        }

                        binder.tvStopPortfolio.setOnClickListener {
                            val folios: MutableList<FolioData> = mutableListOf()
                            for (folio in item.folioList) {
                                val sipDetailsList: MutableList<SIPDetails> = mutableListOf()
                                for (sipDetail in folio.sipDetails) {
                                    sipDetailsList.add(SIPDetails(sipDetail.amount, sipDetail.startDate, sipDetail.transId))
                                }
                                folios.add(FolioData(folio.currentValue,folio.amount, folio.folioNo, sipDetailsList))
                            }

                            context?.stopFundPortfolioDialog(folios) { transactionId,folio,date ->
                                stopPortfolio(transactionId).observe(this, Observer {
                                    context?.simpleAlert("Your SIP having folio no. $folio and start date $date has been stopped successfully."){
                                        vm.getUserPortfolio()
                                    }
                                })
                            }
                        }
                    }
                } else {
                    getViewModel().isDirectEmpty.set(true)
                }
            })
        }

        mRefresh?.setOnRefreshListener {
            vm?.getUserPortfolio(true)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = DirectInvestmentFragment().apply { arguments = basket }
    }
}

package com.tarrakki.module.portfolio.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.google.gson.JsonObject
import com.tarrakki.*
import com.tarrakki.api.model.FolioData
import com.tarrakki.api.model.UserPortfolioResponse
import com.tarrakki.api.model.printRequest
import com.tarrakki.api.model.toEncrypt
import com.tarrakki.databinding.FragmentDirectInvestmentBinding
import com.tarrakki.databinding.RowDirectInvestmentListItemBinding
import com.tarrakki.module.portfolio.PortfolioVM
import kotlinx.android.synthetic.main.fragment_direct_investment.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.getUserId
import org.supportcompact.utilise.EqualSpacingItemDecoration
import java.util.*

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
        rvDInvests?.addItemDecoration(EqualSpacingItemDecoration(44))

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
                    rvDInvests?.setUpRecyclerView(R.layout.row_direct_investment_list_item, it?.data?.directInvestment as ArrayList<UserPortfolioResponse.Data.DirectInvestment>) { item: UserPortfolioResponse.Data.DirectInvestment, binder: RowDirectInvestmentListItemBinding, position ->
                        binder.investment = item
                        binder.executePendingBindings()

                        binder.tvAddPortfolio.setOnClickListener {
                            val portfolio = mutableListOf<String>()
                            portfolio.add("Portfolio1")
                            portfolio.add("Portfolio2")
                            portfolio.add("Portfolio3")
                            context?.addFundPortfolioDialog(portfolio) { selectedPortfolio, type, amountLumpsum, amountSIP ->

                            }
                        }

                        binder.tvRedeem.setOnClickListener {
                            val folios : MutableList<FolioData> = mutableListOf()
                            for(folio in item.folioList){
                                folios.add(FolioData(folio.amount,folio.folioNo))
                            }
                            context?.redeemFundPortfolioDialog(folios) {
                                portfolioNo, totalAmount, allRedeem ->
                                val json = JsonObject()
                                json.addProperty("user_id", App.INSTANCE.getUserId())
                                json.addProperty("fund_id", item.fundId)
                                json.addProperty("all_redeem", allRedeem)
                                json.addProperty("amount", totalAmount)
                                json.addProperty("folio_number", portfolioNo)
                               // json.addProperty("goal_id", sip)
                                json.toString().printRequest()
                                val data = json.toString().toEncrypt()
                                redeemPortfolio(data).observe(this, Observer {

                                })
                            }
                        }
                    }
                } else {

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

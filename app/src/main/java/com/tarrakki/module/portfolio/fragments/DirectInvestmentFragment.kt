package com.tarrakki.module.portfolio.fragments

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tarrakki.R
import com.tarrakki.addFundPortfolioDialog
import com.tarrakki.databinding.FragmentDirectInvestmentBinding
import com.tarrakki.databinding.RowDirectInvestmentListItemBinding
import com.tarrakki.module.portfolio.Investment
import com.tarrakki.module.portfolio.PortfolioVM
import com.tarrakki.redeemFundPortfolioDialog
import kotlinx.android.synthetic.main.fragment_direct_investment.*
import org.supportcompact.adapters.setUpRecyclerView

/**
 * A simple [Fragment] subclass.
 * Use the [DirectInvestmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DirectInvestmentFragment : Fragment() {

    var vm: PortfolioVM? = null
    var binder: FragmentDirectInvestmentBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binder == null) {
            binder = DataBindingUtil.inflate(inflater, R.layout.fragment_direct_investment, container, false)
            parentFragment?.let {
                vm = ViewModelProviders.of(it).get(PortfolioVM::class.java)
            }
        }
        return binder?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm?.let { vm ->
            rvDInvests?.setUpRecyclerView(R.layout.row_direct_investment_list_item, vm.directInvestment) { item: Investment, binder: RowDirectInvestmentListItemBinding, position ->
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
                        val portfolio = mutableListOf<String>()
                        portfolio.add("Portfolio1")
                        portfolio.add("Portfolio2")
                        portfolio.add("Portfolio3")
                        context?.redeemFundPortfolioDialog(portfolio) { selectedPortfolio,amount ->

                        }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = DirectInvestmentFragment().apply { arguments = basket }
    }
}

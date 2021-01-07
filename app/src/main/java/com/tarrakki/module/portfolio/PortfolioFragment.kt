package com.tarrakki.module.portfolio


import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.databinding.FragmentPortfolioBinding
import com.tarrakki.module.portfolio.fragments.LiquiLoanPortfolioFragment
import com.tarrakki.module.portfolio.fragments.DirectInvestmentFragment
import com.tarrakki.module.portfolio.fragments.GoalBasedInvestmentFragment
import com.tarrakki.module.portfolio.fragments.TarrakkiZyaadaPortfolioFragment
import kotlinx.android.synthetic.main.fragment_portfolio.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.Page
import org.supportcompact.adapters.setFragmentPagerAdapter


/**
 * A simple [Fragment] subclass.
 * Use the [PortfolioFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class PortfolioFragment : CoreFragment<PortfolioVM, FragmentPortfolioBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.portfolio)

    override fun getLayout(): Int {
        return R.layout.fragment_portfolio
    }

    override fun createViewModel(): Class<out PortfolioVM> {
        return PortfolioVM::class.java
    }

    override fun setVM(binding: FragmentPortfolioBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let { isRefreshing ->
                getViewModel().isRefreshing.value = false
                App.INSTANCE.isRefreshing.value = null
            }
        })

        getViewModel().getUserPortfolio()
        getViewModel().getLiquiloansPortfolioAPI()

        val pages = arrayListOf(
                Page(getString(R.string.goal_base_investment), GoalBasedInvestmentFragment.newInstance()),
                Page(getString(R.string.direct_investment), DirectInvestmentFragment.newInstance()),
                Page(getString(R.string.tarrakki_zyaada_investment), TarrakkiZyaadaPortfolioFragment.newInstance()),
                Page(getString(R.string.liquiloans), LiquiLoanPortfolioFragment.newInstance())
        )
        mPager?.isNestedScrollingEnabled = false
        mPager?.offscreenPageLimit = 1
        mPager?.setFragmentPagerAdapter(childFragmentManager, pages)
        mTab?.setupWithViewPager(mPager, true)
    }

    override fun onResume() {
        getViewModel().getUserPortfolio()
        getViewModel().getLiquiloansPortfolioAPI()
        super.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = PortfolioFragment().apply { arguments = basket }
    }
}
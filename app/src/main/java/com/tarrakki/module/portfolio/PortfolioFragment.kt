package com.tarrakki.module.portfolio


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.databinding.FragmentPortfolioBinding
import com.tarrakki.module.portfolio.fragments.DirectInvestmentFragment
import com.tarrakki.module.portfolio.fragments.GoalBasedInvestmentFragment
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

        val pages = arrayListOf(
                Page("Goal Based Investment", GoalBasedInvestmentFragment.newInstance()),
                Page("Direct Investment", DirectInvestmentFragment.newInstance())
        )
        mPager?.isNestedScrollingEnabled = false
        mPager?.setFragmentPagerAdapter(childFragmentManager, pages)
        mTab?.setupWithViewPager(mPager, true)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment PortfolioFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = PortfolioFragment().apply { arguments = basket }
    }
}

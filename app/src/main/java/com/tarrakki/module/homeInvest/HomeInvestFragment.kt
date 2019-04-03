package com.tarrakki.module.homeInvest


import android.os.Bundle
import com.tarrakki.R
import com.tarrakki.databinding.FragmentHomeInvestBinding
import com.tarrakki.module.exploreallinvestfunds.ExploreAllInvestFundsFragment
import kotlinx.android.synthetic.main.fragment_home_invest.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment

class HomeInvestFragment : CoreFragment<HomeInvestVM, FragmentHomeInvestBinding>() {

    override val isBackEnabled: Boolean
        get() = false
    override val title: String
        get() = getString(R.string.invest)

    override fun getLayout(): Int {
        return R.layout.fragment_home_invest
    }

    override fun createViewModel(): Class<out HomeInvestVM> {
        return HomeInvestVM::class.java
    }

    override fun setVM(binding: FragmentHomeInvestBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        setHasOptionsMenu(true)

        lyt_equality?.setOnClickListener {
            startFragment(ExploreAllInvestFundsFragment.newInstance(), R.id.frmContainer)
        }

        lyt_advisory?.setOnClickListener {
            context?.simpleAlert(getString(R.string.coming_soon))
        }

    }

    /*override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.home_menu, menu)
        val tvCartCount = menu?.findItem(R.id.itemHome)?.actionView?.findViewById<TextView>(R.id.tvCartCount)
        App.INSTANCE.cartCount.observe(this, Observer {
            it?.let {
                tvCartCount?.cartCount(it)
            }
        })
        menu?.findItem(R.id.itemHome)?.actionView?.setOnClickListener {
            startFragment(CartFragment.newInstance(), R.id.frmContainer)
        }
    }*/

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = HomeInvestFragment().apply { arguments = basket }

    }

}

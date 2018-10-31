package com.tarrakki.module.home


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.TextView
import com.tarrakki.App
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.databinding.FragmentHomeBinding
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.goal.GoalFragment
import com.tarrakki.module.portfolio.PortfolioFragment
import kotlinx.android.synthetic.main.fragment_home.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.startFragment


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class HomeFragment : CoreFragment<HomeVM, FragmentHomeBinding>() {

    override val isBackEnabled: Boolean
        get() = false
    override val title: String
        get() = getString(R.string.home)

    override fun getLayout(): Int {
        return R.layout.fragment_home
    }

    override fun createViewModel(): Class<out HomeVM> {
        return HomeVM::class.java
    }

    override fun setVM(binding: FragmentHomeBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        setHasOptionsMenu(true)
        tvWhyTarrakkii?.setOnClickListener { _ ->
            getViewModel().whayTarrakki.get()?.let {
                getViewModel().whayTarrakki.set(!it)
            }
        }
        tvViewPortfolio?.setOnClickListener {
            startFragment(PortfolioFragment.newInstance(), R.id.frmContainer)
        }
        rvHomeItem.isFocusable = false
        rvHomeItem.isNestedScrollingEnabled = false
        rvHomeItem.setUpMultiViewRecyclerAdapter(getViewModel().homeSections) { item, binder, position ->
            binder.setVariable(BR.section, item)
            binder.setVariable(BR.onViewAll, View.OnClickListener {
                when (position) {
                    1 -> {
                        startFragment(GoalFragment.newInstance(), R.id.frmContainer)
                    }
                }
            })
            binder.executePendingBindings()
        }
        cpPortfolio.setProgressWithAnimation(78f)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.home_menu, menu)
        val tvCartCount = menu?.findItem(R.id.itemHome)?.actionView?.findViewById<TextView>(R.id.tvCartCount)
        App.INSTANCE.cartCount.observe(this, Observer {
            tvCartCount?.text = it.toString()
        })
        menu?.findItem(R.id.itemHome)?.actionView?.setOnClickListener {
            startFragment(CartFragment.newInstance(), R.id.frmContainer)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket .
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = HomeFragment().apply { arguments = basket }
    }
}

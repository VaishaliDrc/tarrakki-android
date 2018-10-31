package com.tarrakki.module.portfolio.fragments

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tarrakki.R
import com.tarrakki.databinding.FragmentPortfolioBinding
import com.tarrakki.module.portfolio.PortfolioVM

/**
 * A simple [Fragment] subclass.
 * Use the [DirectInvestmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DirectInvestmentFragment : Fragment() {

    var vm: PortfolioVM? = null
    var binder: FragmentPortfolioBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
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
        vm?.let {
            
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle
         * @return A new instance of fragment DirectInvestmentFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = DirectInvestmentFragment().apply { arguments = basket }
    }
}

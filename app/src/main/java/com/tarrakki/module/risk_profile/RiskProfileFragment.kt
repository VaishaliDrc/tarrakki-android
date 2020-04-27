package com.tarrakki.module.risk_profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR

import com.tarrakki.R
import com.tarrakki.databinding.FragmentRiskProfileBinding
import kotlinx.android.synthetic.main.fragment_risk_profile.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [RiskProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RiskProfileFragment : CoreFragment<RiskProfileVM, FragmentRiskProfileBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.your_risk_profile)

    override fun getLayout(): Int {
        return R.layout.fragment_risk_profile
    }

    override fun createViewModel(): Class<out RiskProfileVM> {
        return RiskProfileVM::class.java
    }

    override fun setVM(binding: FragmentRiskProfileBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        rvRiskProfile?.setUpMultiViewRecyclerAdapter(getViewModel().data) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.widget, item)
            binder.executePendingBindings()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment RiskProfileFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = RiskProfileFragment().apply { arguments = basket }
    }

}

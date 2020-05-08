package com.tarrakki.module.risk_profile

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.RiskProfileResponse
import com.tarrakki.api.model.parseTo
import com.tarrakki.databinding.FragmentRiskProfileBinding
import com.tarrakki.databinding.RowSpeedometerRiskProfileBinding
import com.tarrakki.module.bankaccount.SingleButton
import com.tarrakki.speedometer.components.Section
import com.tarrakki.speedometer.components.indicators.ImageIndicator
import kotlinx.android.synthetic.main.fragment_risk_profile.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.*

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
        setHasOptionsMenu(true)
        rvRiskProfile?.setUpMultiViewRecyclerAdapter(getViewModel().data) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.widget, item)
            binder.setVariable(BR.onAdd, View.OnClickListener {
                startFragment(StartAssessmentFragment.newInstance(), R.id.frmContainer)
            })
            binder.executePendingBindings()
        }
        getViewModel().riskProfile.observe(this, Observer { o ->
            GlobalScope.launch {
                withContext(Dispatchers.Default) {
                    val res = o.data?.parseTo<RiskProfileResponse>()
                    val data = getViewModel().data
                    data.clear()
                    res?.data?.let { report ->
                        data.add(RiskProfile(
                                report.userName ?: "",
                                "as on ".plus(report.reportDate?.toDate("MM/dd/yyyy")?.convertTo()),
                                report.userProfilePhoto ?: ""
                        ))
                        var observation = ""
                        report.observations?.forEach {
                            observation += (it.observation ?: "").plus("\n\n")
                        }
                        data.add(RiskObservation(observation))
                        data.add(RiskSpeedometer(report.classification?.riskScore?.toFloatOrNull()
                                ?: 0f))
                        data.add(SingleButton(R.string.retake_risk_assessment))
                    }
                }
                withContext(Dispatchers.Main) {
                    rvRiskProfile?.adapter?.notifyDataSetChanged()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Subscribe(sticky = true)
    fun onReceive(apiRes: ApiResponse) {
        getViewModel().riskProfile.value = apiRes
        removeStickyEvent(apiRes)
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

package com.tarrakki.module.risk_profile

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.tarrakki.R
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.RiskProfileResponse
import com.tarrakki.api.model.parseTo
import com.tarrakki.databinding.FragmentRiskProfileBinding
import com.tarrakki.getRiskAssessmentQuestions
import com.tarrakki.module.account.AccountFragment
import com.tarrakki.module.bankaccount.SingleButton
import com.tarrakki.module.funddetails.FundDetailsFragment
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.invest.InvestActivity
import com.tarrakki.module.risk_assesment.AssessmentQFragment
import kotlinx.android.synthetic.main.fragment_risk_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.events.Event
import org.supportcompact.ktx.convertTo
import org.supportcompact.ktx.startFragment
import org.supportcompact.ktx.toDate

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
        var report: List<RiskProfileResponse.Data.Report>? = null
        rvRiskProfile?.setUpMultiViewRecyclerAdapter(getViewModel().data) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.widget, item)
            binder.setVariable(BR.onAdd, View.OnClickListener {
                getRiskAssessmentQuestions().observe(this, Observer { apiRes ->
                    apiRes?.let {
                        report?.forEach { q ->
                            val data = apiRes.data?.firstOrNull { it.questionId == q.questionId }
                            q.options?.sortedBy { it.optionId }?.forEachIndexed { indexMain, op ->
                                data?.option?.filter { it.optionId == op.optionId }?.forEach {
                                    it.isSelected = true
                                    op.extraData?.forEachIndexed { index, extraDataX ->
                                        when (index) {
                                            0 -> {
                                                if ("checkbox".equals(op.optionType, true) && TextUtils.isEmpty(data.totalValue)) {
                                                    data.totalValue = extraDataX.amount ?: ""
                                                } else {
                                                    it.goalAmount = extraDataX.amount?.optValue(indexMain) ?: ""
                                                }
                                            }
                                            else -> {
                                                it.targetYear = extraDataX.targetYear?.optValue(indexMain) ?: ""
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        startFragment(AssessmentQFragment.newInstance(), R.id.frmContainer)
                        apiRes.page = 1
                        apiRes.isReassessment = true
                        repostSticky(apiRes)
                    }
                })
            })
            binder.executePendingBindings()
        }
        getViewModel().riskProfile.observe(this, Observer { o ->
            GlobalScope.launch {
                withContext(Dispatchers.Default) {
                    val res = o.data?.parseTo<RiskProfileResponse>()
                    report = res?.data?.report
                    val data = getViewModel().data
                    data.clear()
                    res?.data?.let { report ->
                        data.add(RiskProfile(
                                report.userName ?: "",
                                "as on ".plus(report.reportDate/*?.toDate("MM/dd/yyyy")?.convertTo()*/),
                                report.userProfilePhoto ?: ""
                        ))
                        var observation = ""
                        report.observations?.forEach {
                            observation += (it.observation ?: "").plus("\n\n")
                        }
                        data.add(RiskObservation(observation))
                        data.add(RiskSpeedometer(report.classification?.riskProfile ?: ""))
                        data.add(SingleButton(R.string.retake_risk_assessment))
                    }
                }
                withContext(Dispatchers.Main) {
                    rvRiskProfile?.adapter?.notifyDataSetChanged()
                }
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (activity is InvestActivity || activity is HomeActivity) {
                    onBackExclusive(FundDetailsFragment::class.java)
                    postSticky(Event.REFRESH_FUN_DETAILS)
                } else {
                    onBackExclusive(AccountFragment::class.java)
                }
            }
        })
    }

    private fun String.optValue(valueAt: Int): String {
        val data = split(",")
        return data.getOrNull(valueAt) ?: ""
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

package com.tarrakki.module.risk_assessment_agree

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.lifecycle.Observer
import com.tarrakki.R
import com.tarrakki.api.model.RiskAssessmentQuestionsApiResponse
import com.tarrakki.databinding.FragmentAssessmentDeclarationBinding
import com.tarrakki.getReportOfRiskProfile
import com.tarrakki.module.risk_profile.RiskProfileFragment
import kotlinx.android.synthetic.main.fragment_assessment_declaration.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.startFragment

class AssessmentDeclartionFragment : CoreFragment<AssessmentDeclarationVM, FragmentAssessmentDeclarationBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.risk_assessment)

    override fun getLayout() = R.layout.fragment_assessment_declaration

    override fun createViewModel(): Class<out AssessmentDeclarationVM> {
        return AssessmentDeclarationVM::class.java
    }

    override fun setVM(binding: FragmentAssessmentDeclarationBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun createReference() {
        setHasOptionsMenu(true)
        btnAgree?.setOnClickListener {
            getViewModel().submitRiskAssessmentAws().observe(this, Observer {
                getReportOfRiskProfile().observe(this, Observer { apiRes ->
                    if (apiRes.status?.code == 1) {
                        startFragment(RiskProfileFragment.newInstance(), R.id.frmContainer)
                        postSticky(apiRes)
                    }
                })
            })
        }
    }

    @Subscribe(sticky = true)
    fun onReceive(data: RiskAssessmentQuestionsApiResponse) {
        if (getViewModel().questions.value == null) {
            getViewModel().questions.value = data
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = AssessmentDeclartionFragment().apply { arguments = basket }
    }
}
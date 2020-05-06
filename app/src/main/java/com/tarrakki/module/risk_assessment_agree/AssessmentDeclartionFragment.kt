package com.tarrakki.module.risk_assessment_agree

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import com.tarrakki.R
import com.tarrakki.databinding.FragmentAssessmentDeclarationBinding
import com.tarrakki.databinding.FragmentAssessmentQBinding
import org.supportcompact.CoreFragment

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


    }


    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = AssessmentDeclartionFragment().apply { arguments = basket }
    }
}
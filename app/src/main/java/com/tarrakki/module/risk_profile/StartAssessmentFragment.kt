package com.tarrakki.module.risk_profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentStartAssessmentBinding
import com.tarrakki.module.risk_assesment.AssessmentQFragment
import kotlinx.android.synthetic.main.fragment_start_assessment.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [StartAssessmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StartAssessmentFragment : CoreFragment<StartAssessmentVM, FragmentStartAssessmentBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.your_risk_profile)

    override fun getLayout() = R.layout.fragment_start_assessment

    override fun createViewModel(): Class<out StartAssessmentVM> {
        return StartAssessmentVM::class.java
    }

    override fun setVM(binding: FragmentStartAssessmentBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        btnStart?.setOnClickListener {
            startFragment(AssessmentQFragment.newInstance(), R.id.frmContainer)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket Bundle.
         * @return A new instance of fragment StartAssessmentFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = StartAssessmentFragment().apply { arguments = basket }
    }
}

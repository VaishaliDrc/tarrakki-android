package com.tarrakki.module.risk_assesment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.tarrakki.R
import com.tarrakki.api.model.RiskAssessmentQuestionsApiResponse
import com.tarrakki.databinding.FragmentAssessmentQBinding
import com.tarrakki.databinding.RowRiskAssessmentSliderItemStartBinding
import kotlinx.android.synthetic.main.fragment_assessment_q.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [AssessmentQFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AssessmentQFragment : CoreFragment<AssessmentQVM, FragmentAssessmentQBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.risk_assessment)

    override fun getLayout() = R.layout.fragment_assessment_q

    override fun createViewModel(): Class<out AssessmentQVM> {
        return AssessmentQVM::class.java
    }

    override fun setVM(binding: FragmentAssessmentQBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        getViewModel().questions.observe(this, Observer { it ->
            it?.let { data ->
                when (data.page) {
                    1 -> {
                        rvQuestions?.setUpRecyclerView(R.layout.row_risk_assessment_slider_item_start, getViewModel().sliderQuestions) { item: SliderItem, binder: RowRiskAssessmentSliderItemStartBinding, position: Int ->
                            binder.item = item
                            binder.executePendingBindings()
                            binder.tvTitle.setOnClickListener {
                                //getViewModel().sliderQuestions.forEach { it.isSelected = false }
                                getViewModel().sliderQuestions.forEachIndexed { index, it ->
                                    it.isSelected = false
                                    it.isMovedOver = index < position
                                }
                                getViewModel().sliderQuestions[position].isSelected = true
                            }
                        }
                    }
                    2 -> {
                        context?.simpleAlert("Page number is : ${data.page}")
                    }
                    3 -> {
                        context?.simpleAlert("Page number is : ${data.page}")
                    }
                    4 -> {
                        context?.simpleAlert("Page number is : ${data.page}")
                    }
                    5 -> {
                        context?.simpleAlert("Page number is : ${data.page}")
                    }
                    6 -> {
                        context?.simpleAlert("Page number is : ${data.page}")
                    }
                    7 -> {
                        context?.simpleAlert("Page number is : ${data.page}")
                    }
                    8 -> {
                        context?.simpleAlert("Page number is : ${data.page}")
                    }
                    9 -> {
                        context?.simpleAlert("Page number is : ${data.page}")
                    }
                    10 -> {
                        context?.simpleAlert("Page number is : ${data.page}")
                    }
                }

                btnContinue?.setOnClickListener {
                    startFragment(AssessmentQFragment.newInstance(), R.id.frmContainer)
                    data.page++
                    postSticky(data)
                }

            }
        })

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onReceive(data: RiskAssessmentQuestionsApiResponse) {
        if (getViewModel().questions.value == null) {
            getViewModel().questions.value = data
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket Bundle.
         * @return A new instance of fragment AssessmentQFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = AssessmentQFragment().apply { arguments = basket }
    }
}

package com.tarrakki.module.support.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentQuestionDetailsBinding
import com.tarrakki.module.support.QuestionsVM
import org.supportcompact.CoreFragment

/**
 * A simple [Fragment] subclass.
 * Use the [QuestionDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuestionDetailsFragment : CoreFragment<QuestionsVM, FragmentQuestionDetailsBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.support)

    override fun getLayout(): Int {
        return R.layout.fragment_question_details
    }

    override fun createViewModel(): Class<out QuestionsVM> {
        return QuestionsVM::class.java
    }

    override fun setVM(binding: FragmentQuestionDetailsBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket as Bundle.
         * @return A new instance of fragment QuestionDetailsFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = QuestionDetailsFragment().apply { arguments = basket }
    }
}
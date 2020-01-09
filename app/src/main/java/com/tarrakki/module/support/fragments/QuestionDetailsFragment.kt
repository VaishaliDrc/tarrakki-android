package com.tarrakki.module.support.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tarrakki.R
import com.tarrakki.api.model.SupportQueryListResponse
import com.tarrakki.api.model.SupportQuestionListResponse
import com.tarrakki.databinding.FragmentQuestionDetailsBinding
import com.tarrakki.module.support.QuestionsVM
import com.tarrakki.module.support.raiseticket.RaiseTicketFragment
import kotlinx.android.synthetic.main.fragment_question_details.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.startFragment

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
        mWebView?.setBackgroundColor(0)
        frmGetInTouch?.setOnClickListener {
            startFragment(RaiseTicketFragment.newInstance(), R.id.frmContainer)
            getViewModel().query.value?.let { postSticky(it) }
            getViewModel().question.get()?.let { postSticky(it) }
        }
    }

    @Subscribe(sticky = true)
    fun onReceived(data: SupportQuestionListResponse.Question) {
        if (getViewModel().question.get() == null) {
            getViewModel().question.set(data)
        }
    }

    @Subscribe(sticky = true)
    fun onReceived(data: SupportQueryListResponse.Data) {
        if (getViewModel().query.value == null) {
            getViewModel().query.value = data
        }
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

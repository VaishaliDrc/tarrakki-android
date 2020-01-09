package com.tarrakki.module.support.fragments


import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tarrakki.R
import com.tarrakki.api.model.SupportQueryListResponse
import com.tarrakki.api.model.SupportQuestionListResponse
import com.tarrakki.databinding.FragmentQuestionsBinding
import com.tarrakki.databinding.RowQuestionsListItemBinding
import com.tarrakki.module.support.QuestionsVM
import com.tarrakki.module.support.raiseticket.RaiseTicketFragment
import kotlinx.android.synthetic.main.fragment_questions.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [QuestionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class QuestionsFragment : CoreFragment<QuestionsVM, FragmentQuestionsBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.support)

    override fun getLayout(): Int {
        return R.layout.fragment_questions
    }

    override fun createViewModel(): Class<out QuestionsVM> {
        return QuestionsVM::class.java
    }

    override fun setVM(binding: FragmentQuestionsBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        getViewModel().query.observe(this, Observer { query ->
            query?.let {
                getViewModel().getQuestionList().observe(this, Observer { questionResponse ->
                    questionResponse?.questions?.let { questions ->
                        rvQuestions?.setUpRecyclerView(R.layout.row_questions_list_item, questions) { item: SupportQuestionListResponse.Question, binder: RowQuestionsListItemBinding, position: Int ->
                            binder.item = item
                            binder.executePendingBindings()
                            binder.root.setOnClickListener {
                                if (item.isMyQuestion == true) {
                                    startFragment(RaiseTicketFragment.newInstance(), R.id.frmContainer)
                                    postSticky(query)
                                } else {
                                    startFragment(QuestionDetailsFragment.newInstance(), R.id.frmContainer)
                                    getViewModel().query.value?.let { postSticky(it) }
                                }
                                postSticky(item)
                            }
                        }
                    }
                })
            }
        })
    }

    @Subscribe(sticky = true)
    fun onReceived(data: SupportQueryListResponse.Data) {
        if (getViewModel().query.value == null) {
            getViewModel().query.value = data
            getViewModel().queryTitle.set(data.subqueryName)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment SubQueriesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = QuestionsFragment().apply { arguments = basket }
    }
}

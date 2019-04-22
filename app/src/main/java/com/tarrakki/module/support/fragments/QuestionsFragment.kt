package com.tarrakki.module.support.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentSubQueriesBinding
import com.tarrakki.databinding.RowQuestionsListItemBinding
import com.tarrakki.databinding.RowSubqueryListItemBinding
import com.tarrakki.module.support.QuestionsVM
import com.tarrakki.module.support.SubQueriesVM
import kotlinx.android.synthetic.main.fragment_sub_queries.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView

/**
 * A simple [Fragment] subclass.
 * Use the [QuestionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class QuestionsFragment : CoreFragment<QuestionsVM, FragmentSubQueriesBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.support)

    override fun getLayout(): Int {
        return R.layout.fragment_sub_queries
    }

    override fun createViewModel(): Class<out QuestionsVM> {
        return QuestionsVM::class.java
    }

    override fun setVM(binding: FragmentSubQueriesBinding) {

    }

    override fun createReference() {
        rvSubQueries?.setUpRecyclerView(R.layout.row_questions_list_item, getViewModel().questions) { item: String, binder: RowQuestionsListItemBinding, position: Int ->
            binder.item = item
            binder.executePendingBindings()
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

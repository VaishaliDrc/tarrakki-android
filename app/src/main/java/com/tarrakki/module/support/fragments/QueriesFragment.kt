package com.tarrakki.module.support.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentQueriesBinding
import com.tarrakki.databinding.RowQueryListItemBinding
import com.tarrakki.module.support.SupportVM
import kotlinx.android.synthetic.main.fragment_queries.*
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [QueriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class QueriesFragment : CoreParentFragment<SupportVM, FragmentQueriesBinding>() {

    override fun getLayout(): Int {
        return R.layout.fragment_queries
    }

    override fun createViewModel(): Class<out SupportVM> {
        return SupportVM::class.java
    }

    override fun setVM(binding: FragmentQueriesBinding) {

    }

    override fun createReference() {
        rvQueries?.setUpRecyclerView(R.layout.row_query_list_item, getViewModel().queries) { item: String, binder: RowQueryListItemBinding, position: Int ->
            binder.root.setOnClickListener {
                startFragment(SubQueriesFragment.newInstance(), R.id.frmContainer)
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket Bundle.
         * @return A new instance of fragment QueriesFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = QueriesFragment().apply { arguments = basket }
    }
}

package com.tarrakki.module.support.fragments


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.api.model.SupportQueryListResponse
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
        val imgResources = arrayListOf(R.drawable.icon_1, R.drawable.icon_2, R.drawable.icon_3, R.drawable.icon_4, R.drawable.icon_5)
        getViewModel().getQueryList().observe(this, Observer {
            it?.data?.let { queries ->
                rvQueries?.setUpRecyclerView(R.layout.row_query_list_item, queries) { item: SupportQueryListResponse.Data, binder: RowQueryListItemBinding, position: Int ->
                    binder.query = item
                    binder.imgRes = imgResources[position % 5]
                    binder.executePendingBindings()
                    binder.root.setOnClickListener {
                        startFragment(SubQueriesFragment.newInstance(), R.id.frmContainer)
                        postSticky(item)
                    }
                }
            }
        })
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

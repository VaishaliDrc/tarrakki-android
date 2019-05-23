package com.tarrakki.module.support.fragments


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.api.model.SupportQueryListResponse
import com.tarrakki.databinding.FragmentSubQueriesBinding
import com.tarrakki.databinding.RowSubqueryListItemBinding
import com.tarrakki.module.support.SubQueriesVM
import com.tarrakki.module.support.raiseticket.RaiseTicketFragment
import kotlinx.android.synthetic.main.fragment_sub_queries.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [SubQueriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SubQueriesFragment : CoreFragment<SubQueriesVM, FragmentSubQueriesBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.support)

    override fun getLayout(): Int {
        return R.layout.fragment_sub_queries
    }

    override fun createViewModel(): Class<out SubQueriesVM> {
        return SubQueriesVM::class.java
    }

    override fun setVM(binding: FragmentSubQueriesBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        getViewModel().query.observe(this, Observer { query ->
            query?.subquery?.let { subQueries ->
                rvSubQueries?.setUpRecyclerView(R.layout.row_subquery_list_item, subQueries) { item: SupportQueryListResponse.Data.Subquery, binder: RowSubqueryListItemBinding, position: Int ->
                    binder.item = item
                    binder.executePendingBindings()
                    binder.root.setOnClickListener {
                        query.subqueryId = item.id
                        query.subqueryName = item.name
                        if (item.isMyQuestion == true) {
                            startFragment(RaiseTicketFragment.newInstance(), R.id.frmContainer)
                        } else {
                            startFragment(QuestionsFragment.newInstance(), R.id.frmContainer)
                        }
                        postSticky(query)
                    }
                }
            }
        })
    }

    @Subscribe(sticky = true)
    fun onReceived(data: SupportQueryListResponse.Data) {
        if (getViewModel().query.value == null) {
            getViewModel().query.value = data
            getViewModel().queryTitle.set(data.name)
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
        fun newInstance(basket: Bundle? = null) = SubQueriesFragment().apply { arguments = basket }
    }
}

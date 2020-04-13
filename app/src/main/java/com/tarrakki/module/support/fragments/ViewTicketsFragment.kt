package com.tarrakki.module.support.fragments


import androidx.lifecycle.Observer
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.View
import com.tarrakki.App
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.databinding.FragmentViewTicketsBinding
import com.tarrakki.module.support.SupportVM
import com.tarrakki.module.support.chat.ChatFragment
import com.tarrakki.module.transactions.LoadMore
import kotlinx.android.synthetic.main.fragment_view_tickets.*
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [ViewTicketsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ViewTicketsFragment : CoreParentFragment<SupportVM, FragmentViewTicketsBinding>() {

    var mUserVisibleHint: Boolean = true

    override fun getLayout(): Int {
        return R.layout.fragment_view_tickets
    }

    override fun createViewModel(): Class<out SupportVM> {
        return SupportVM::class.java
    }

    override fun setVM(binding: FragmentViewTicketsBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        userVisibleHint = true
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.mUserVisibleHint = isVisibleToUser
        if (isVisibleToUser) {
            when (rgTicketType?.checkedRadioButtonId) {
                R.id.rbAll -> {
                    getViewModel().getTicketsList()
                }
                R.id.rbOpen -> {
                    getViewModel().getOpenTicketsList()
                }
                R.id.rbClosed -> {
                    getViewModel().getClosedTicketsList()
                }
            }
        }
    }

    override fun createReference() {
        val loadMore = LoadMore()
        val allData = ArrayList<WidgetsViewModel>()
        setData(allData, loadMore)
        getViewModel().allTicket.observe(this, Observer {
            it?.data?.conversation?.let { tickets ->
                allData.clear()
                loadMore.isLoading = false
                allData.addAll(tickets)
                if (allData.size >= 5 && it.data.totalCount ?: allData.size > allData.size) {
                    allData.add(loadMore)
                }
                rvTickets?.adapter?.notifyDataSetChanged()
            }
        })

        getViewModel().openTicket.observe(this, Observer {
            it?.data?.conversation?.let { tickets ->
                allData.clear()
                loadMore.isLoading = false
                allData.addAll(tickets)
                if (allData.size >= 5 && it.data.totalCount ?: allData.size > allData.size) {
                    allData.add(loadMore)
                }
                rvTickets?.adapter?.notifyDataSetChanged()
            }
        })

        getViewModel().closeTicket.observe(this, Observer {
            it?.data?.conversation?.let { tickets ->
                allData.clear()
                loadMore.isLoading = false
                allData.addAll(tickets)
                if (allData.size >= 5 && it.data.totalCount ?: allData.size > allData.size) {
                    allData.add(loadMore)
                }
                rvTickets?.adapter?.notifyDataSetChanged()
            }
        })

        rgTicketType?.setOnCheckedChangeListener { group, checkedId ->
            allData.clear()
            rvTickets?.adapter?.notifyDataSetChanged()
            when (rgTicketType?.checkedRadioButtonId) {
                R.id.rbAll -> {
                    getViewModel().getTicketsList()
                }
                R.id.rbOpen -> {
                    getViewModel().getOpenTicketsList()
                }
                R.id.rbClosed -> {
                    getViewModel().getClosedTicketsList()
                }
            }
            /*when (checkedId) {
                R.id.rbAll -> {
                    if (getViewModel().allTicket.value == null) {
                        getViewModel().getTicketsList()
                    } else {
                        getViewModel().allTicket.postValue(getViewModel().allTicket.value)
                    }
                }
                R.id.rbOpen -> {
                    if (getViewModel().openTicket.value == null) {
                        getViewModel().getOpenTicketsList()
                    } else {
                        getViewModel().openTicket.postValue(getViewModel().openTicket.value)
                    }
                }
                R.id.rbClosed -> {
                    if (getViewModel().closeTicket.value == null) {
                        getViewModel().getClosedTicketsList()
                    } else {
                        getViewModel().closeTicket.postValue(getViewModel().closeTicket.value)
                    }
                }
            }*/
        }

        App.INSTANCE.isRefreshing.observe(this, Observer {
            if (mUserVisibleHint)
                when (rgTicketType?.checkedRadioButtonId) {
                    R.id.rbAll -> {
                        getViewModel().tvNoDataFoundVisibility.set(
                                if (getViewModel().allTicket.value?.data?.conversation?.isNotEmpty() == true)
                                    View.GONE
                                else
                                    View.VISIBLE
                        )
                    }
                    R.id.rbOpen -> {
                        getViewModel().tvNoDataFoundVisibility.set(
                                if (getViewModel().openTicket.value?.data?.conversation?.isNotEmpty() == true)
                                    View.GONE
                                else
                                    View.VISIBLE
                        )
                    }
                    R.id.rbClosed -> {
                        getViewModel().tvNoDataFoundVisibility.set(
                                if (getViewModel().closeTicket.value?.data?.conversation?.isNotEmpty() == true)
                                    View.GONE
                                else
                                    View.VISIBLE
                        )
                    }
                }
        })
    }

    fun setData(tickets: ArrayList<WidgetsViewModel>, loadMore: LoadMore) {
        rvTickets?.setUpMultiViewRecyclerAdapter(tickets) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.ticket, item)
            binder.executePendingBindings()
            binder.root.setOnClickListener {
                startFragment(ChatFragment.newInstance(), R.id.frmContainer)
                postSticky(item)
            }
            if (item is LoadMore && !item.isLoading) {
                loadMore.isLoading = true
                Handler().postDelayed({
                    when (rgTicketType?.checkedRadioButtonId) {
                        R.id.rbAll -> {
                            getViewModel().getTicketsList(position)
                        }
                        R.id.rbOpen -> {
                            getViewModel().getOpenTicketsList(position)
                        }
                        R.id.rbClosed -> {
                            getViewModel().getClosedTicketsList(position)
                        }
                    }
                }, 1500)
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment ViewTicketsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = ViewTicketsFragment().apply { arguments = basket }
    }
}

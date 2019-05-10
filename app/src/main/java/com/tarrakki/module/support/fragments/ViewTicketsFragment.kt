package com.tarrakki.module.support.fragments


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.api.model.SupportViewTicketResponse
import com.tarrakki.databinding.FragmentViewTicketsBinding
import com.tarrakki.databinding.RowViewTicketListItemBinding
import com.tarrakki.module.support.SupportVM
import com.tarrakki.module.support.chat.ChatFragment
import kotlinx.android.synthetic.main.fragment_view_tickets.*
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [ViewTicketsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ViewTicketsFragment : CoreParentFragment<SupportVM, FragmentViewTicketsBinding>() {

    override fun getLayout(): Int {
        return R.layout.fragment_view_tickets
    }

    override fun createViewModel(): Class<out SupportVM> {
        return SupportVM::class.java
    }

    override fun setVM(binding: FragmentViewTicketsBinding) {

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            when (rgTicketType?.checkedRadioButtonId) {
                R.id.rbAll -> {
                    if (getViewModel().allTicket.value == null) {
                        getViewModel().getTicketsList()
                    }
                }
                R.id.rbOpen -> {
                    if (getViewModel().openTicket.value == null) {
                        getViewModel().getOpenTicketsList()
                    }
                }
                R.id.rbClosed -> {
                    if (getViewModel().closeTicket.value == null) {
                        getViewModel().getClosedTicketsList()
                    }
                }
            }
        }
    }

    override fun createReference() {

        getViewModel().allTicket.observe(this, Observer {
            it?.data?.conversation?.let { tickets ->
                setData(tickets, it.data)
            }
        })

        getViewModel().openTicket.observe(this, Observer {
            it?.data?.conversation?.let { tickets ->
                setData(tickets, it.data)
            }
        })

        getViewModel().closeTicket.observe(this, Observer {
            it?.data?.conversation?.let { tickets ->
                setData(tickets, it.data)
            }
        })

        rgTicketType?.setOnCheckedChangeListener { group, checkedId ->
            rvTickets?.adapter = null
            when (checkedId) {
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
            }
        }
    }

    fun setData(tickets: ArrayList<SupportViewTicketResponse.Data.Conversation>, data: SupportViewTicketResponse.Data) {
        rvTickets?.setUpRecyclerView(R.layout.row_view_ticket_list_item, tickets) { item: SupportViewTicketResponse.Data.Conversation, binder: RowViewTicketListItemBinding, position: Int ->
            binder.ticket = item
            binder.executePendingBindings()
            binder.root.setOnClickListener {
                startFragment(ChatFragment.newInstance(), R.id.frmContainer)
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

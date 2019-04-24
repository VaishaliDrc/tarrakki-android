package com.tarrakki.module.support.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentViewTicketsBinding
import com.tarrakki.databinding.RowViewTicketListItemBinding
import com.tarrakki.module.support.SupportVM
import com.tarrakki.module.support.Ticket
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

    override fun createReference() {
        rvTickets?.setUpRecyclerView(R.layout.row_view_ticket_list_item, getViewModel().tickets) { item: Ticket, binder: RowViewTicketListItemBinding, position: Int ->
            binder.ticket = item
            binder.executePendingBindings()
            binder.root.setOnClickListener {
                startFragment(ChatFragment.newInstance(), R.id.frmContainer)
            }
        }
        rgTicketType?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbAll -> {
                    getViewModel().tickets.forEach { it.isOpen = getViewModel().tickets.indexOf(it) % 2 == 0 }
                }
                R.id.rbOpen -> {
                    getViewModel().tickets.forEach { it.isOpen = true }
                }
                R.id.rbClosed -> {
                    getViewModel().tickets.forEach { it.isOpen = false }
                }
            }
            rvTickets?.adapter?.notifyDataSetChanged()
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

package com.tarrakki.module.support.chat


import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.databinding.FragmentChatBinding
import kotlinx.android.synthetic.main.fragment_chat.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ChatFragment : CoreFragment<ChatVM, FragmentChatBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.ticket_conversation)

    override fun getLayout(): Int {
        return R.layout.fragment_chat
    }

    override fun createViewModel(): Class<out ChatVM> {
        return ChatVM::class.java
    }

    override fun setVM(binding: FragmentChatBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        rvChat?.setUpMultiViewRecyclerAdapter(getViewModel().chats) { item: ChatMessage, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.msg, item)
            binder.executePendingBindings()
        }
        coreActivityVM?.footerVisibility?.set(View.GONE)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment ChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = ChatFragment().apply { arguments = basket }
    }
}

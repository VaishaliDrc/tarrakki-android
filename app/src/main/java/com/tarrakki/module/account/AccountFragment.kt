package com.tarrakki.module.account


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentAccountBinding
import org.supportcompact.CoreFragment

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AccountFragment : CoreFragment<AccountVM, FragmentAccountBinding>() {

    override val isBackEnabled: Boolean
        get() = false
    override val title: String
        get() = getString(R.string.account)

    override fun getLayout(): Int {
        return R.layout.fragment_account
    }

    override fun createViewModel(): Class<out AccountVM> {
        return AccountVM::class.java
    }

    override fun setVM(binding: FragmentAccountBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = AccountFragment().apply { arguments = basket }
    }
}

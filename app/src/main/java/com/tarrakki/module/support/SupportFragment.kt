package com.tarrakki.module.support


import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentSupportBinding
import com.tarrakki.fcm.IS_FROM_NOTIFICATION
import com.tarrakki.module.support.chat.ChatFragment
import com.tarrakki.module.support.fragments.QueriesFragment
import com.tarrakki.module.support.fragments.ViewTicketsFragment
import kotlinx.android.synthetic.main.fragment_support.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.Page
import org.supportcompact.adapters.setFragmentPagerAdapter
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [SupportFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SupportFragment : CoreFragment<SupportVM, FragmentSupportBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.support)

    override fun getLayout(): Int {
        return R.layout.fragment_support
    }

    override fun createViewModel(): Class<out SupportVM> {
        return SupportVM::class.java
    }

    override fun setVM(binding: FragmentSupportBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        val pages = arrayListOf(
                Page(getString(R.string.queries), QueriesFragment.newInstance()),
                Page(getString(R.string.view_tickets), ViewTicketsFragment.newInstance())
        )
        mPager?.isNestedScrollingEnabled = false
        mPager?.offscreenPageLimit = 2
        mPager?.setFragmentPagerAdapter(childFragmentManager, pages)
        mTab?.setupWithViewPager(mPager, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments?.getBoolean(IS_FROM_NOTIFICATION) == true) {
            startFragment(ChatFragment.newInstance(), R.id.frmContainer)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket Bundle.
         * @return A new instance of fragment SupportFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = SupportFragment().apply { arguments = basket }
    }
}

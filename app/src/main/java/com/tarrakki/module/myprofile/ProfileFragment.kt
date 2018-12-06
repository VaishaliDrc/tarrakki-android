package com.tarrakki.module.myprofile


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentTransactionskBinding
import kotlinx.android.synthetic.main.fragment_transactionsk.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.Page
import org.supportcompact.adapters.setFragmentPagerAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ProfileFragment : CoreFragment<MyProfileVM, FragmentTransactionskBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.transactions)

    override fun getLayout(): Int {
        return R.layout.fragment_transactionsk
    }

    override fun createViewModel(): Class<out MyProfileVM> {
        return MyProfileVM::class.java
    }

    override fun setVM(binding: FragmentTransactionskBinding) {

    }

    override fun createReference() {
        val pages = arrayListOf(
                Page(getString(R.string.personal_details), MyProfileFragment.newInstance()),
                Page(getString(R.string.nomination_details), NominationDetailsFragment.newInstance())
        )
        mPager?.isNestedScrollingEnabled = false
        mPager?.setFragmentPagerAdapter(childFragmentManager, pages)
        mPager?.isNestedScrollingEnabled = false
        mTab?.setupWithViewPager(mPager, true)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @param basket As Bundle.
         * @return A new instance of fragment TransactionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = ProfileFragment().apply { arguments = basket }
    }
}

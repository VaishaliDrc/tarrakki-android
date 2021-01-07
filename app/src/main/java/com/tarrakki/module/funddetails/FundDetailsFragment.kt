package com.tarrakki.module.funddetails


import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentFundDetailsBinding
import com.tarrakki.module.funddetails.fragments.OverviewFragment
import com.tarrakki.module.funddetails.fragments.PerformanceFragment
import com.tarrakki.module.prime_investor.PrimeInvestorMutualFundListRatingFragment
import com.tarrakki.module.zyaada.TARRAKKI_ZYAADA_ID
import kotlinx.android.synthetic.main.fragment_fund_details.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.Page
import org.supportcompact.adapters.setFragmentPagerAdapter
import org.supportcompact.events.Event
import org.supportcompact.ktx.getTarakkiPro


/**
 * A simple [Fragment] subclass.
 * Use the [FundDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

const val ITEM_ID = "item_id"

class FundDetailsFragment : CoreFragment<FundDetailsVM, FragmentFundDetailsBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.fund_details)

    override fun getLayout(): Int {
        return R.layout.fragment_fund_details
    }

    override fun createViewModel(): Class<out FundDetailsVM> {
        return FundDetailsVM::class.java
    }

    override fun setVM(binding: FragmentFundDetailsBinding) {
        binding.fund = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        arguments?.let { it ->
            it.getString(TARRAKKI_ZYAADA_ID)?.let {
                if (it.isNotBlank()) {
                    getViewModel().tarrakkiZyaadaId = it
                }
            }
            val id = it.getString(ITEM_ID)
            id?.let {
                getViewModel().getFundDetails(it)
            }
        }
        val pages = arrayListOf(
                Page(getString(R.string.overview), OverviewFragment.newInstance()),
                Page(getString(R.string.performance), PerformanceFragment.newInstance()),
                Page(getString(R.string.tarrakki_pro), PrimeInvestorMutualFundListRatingFragment.newInstance(Bundle().apply {
                    arguments?.let { it ->
                        val id = it.getString(ITEM_ID)
                        id?.let {
                            putString("title", getString(R.string.fund_details))
                            putString("id", it)
                        }
                    }


                }))
        )


        mPager?.isNestedScrollingEnabled = false
        mPager?.setFragmentPagerAdapter(childFragmentManager, pages)
        mTab?.setupWithViewPager(mPager, true)

//        mPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
//            override fun onPageSelected(position: Int) {
//                when (position) {
//                    0 -> onTitleChange(getString(R.string.fund_details))
//                    1 -> onTitleChange(getString(R.string.fund_details))
//                    2 -> onTitleChange(getString(R.string.tarrakki_pro))
//                }
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {}
//        })
    }

    @Subscribe(sticky = true)
    override fun onEvent(event: Event) {
        when (event) {
            Event.REFRESH_FUN_DETAILS -> {
                val id = arguments?.getString(ITEM_ID)
                id?.let {
                    getViewModel().getFundDetails(it)
                }
                removeStickyEvent(event)
            }
            Event.FIRST_TAB -> {
                mPager?.let {
                    it.currentItem = 0
                }
                removeStickyEvent(event)
            }
            else -> {
                super.onEvent(event)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = FundDetailsFragment().apply { arguments = basket }
    }
}

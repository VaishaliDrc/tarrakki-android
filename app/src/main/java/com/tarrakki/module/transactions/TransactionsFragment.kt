package com.tarrakki.module.transactions


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.KeyEvent
import android.view.MenuItem
import com.tarrakki.R
import com.tarrakki.databinding.FragmentTransactionskBinding
import com.tarrakki.module.support.raiseticket.IS_FROM_RAISE_TICKET
import com.tarrakki.module.transactions.childfragments.*
import kotlinx.android.synthetic.main.fragment_transactionsk.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.Page
import org.supportcompact.adapters.setFragmentPagerAdapter
import org.supportcompact.events.Event

/**
 * A simple [Fragment] subclass.
 * Use the [TransactionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
const val SET_SELECTED_PAGE = "set_selected_page"

class TransactionsFragment : CoreFragment<TransactionsVM, FragmentTransactionskBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.transactions_history)

    override fun getLayout(): Int {
        return R.layout.fragment_transactionsk
    }

    override fun createViewModel(): Class<out TransactionsVM> {
        return TransactionsVM::class.java
    }

    override fun setVM(binding: FragmentTransactionskBinding) {

    }

    override fun createReference() {
        setHasOptionsMenu(true)
        getViewModel().isFromRaiseTicket = arguments?.getBoolean(IS_FROM_RAISE_TICKET) == true
        val pages = arrayListOf(
                Page(getString(R.string.all), AllTransactionsFragment.newInstance()),
                Page(getString(R.string.in_progress), InProgressTransactionsFragment.newInstance()),
                Page(getString(R.string.completed), CompletedTransactionsFragment.newInstance()),
                Page(getString(R.string.upcoming), UpcomingTransactionsFragment.newInstance()),
                Page(getString(R.string.unpaid), UnpaidTransactionsFragment.newInstance()),
                Page(getString(R.string.failed), FailedTransactionFragment.newInstance())
        )
        mPager?.isNestedScrollingEnabled = false
        mPager?.offscreenPageLimit = 6
        mPager?.setFragmentPagerAdapter(childFragmentManager, pages)
        mPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(p0: Int) {
                getViewModel().hasOptionMenu.value = false
            }
        })
        arguments?.getInt(SET_SELECTED_PAGE)?.let {
            mPager?.setCurrentItem(it, true)
        }
        mTab?.setupWithViewPager(mPager, true)
        mTab?.tabMode = TabLayout.MODE_SCROLLABLE
        getBinding().root.isFocusableInTouchMode = true
        getBinding().root.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                if (mPager.currentItem == 4) {
                    getViewModel().onBack.value = true
                } else {
                    onBack()
                }
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    override fun onResume() {
        super.onResume()
        getBinding().root.requestFocus()
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                if (mPager.currentItem == 4) {
                    getViewModel().onBack.value = true
                } else {
                    onBack()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
        fun newInstance(basket: Bundle? = null) = TransactionsFragment().apply { arguments = basket }
    }

    @Subscribe(sticky = true)
    fun onEventData(event: Event) {
        if (event == Event.ISFROMTRANSACTIONSUCCESS) {
            getViewModel().onRefresh.value = true
        }
        removeStickyEvent(event)
    }
}

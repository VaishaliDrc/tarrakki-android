package com.tarrakki.module.cart


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.BaseActivity
import com.tarrakki.R
import com.tarrakki.api.model.CartData
import com.tarrakki.databinding.FragmentCartBinding
import com.tarrakki.databinding.RowCartItemBinding
import com.tarrakki.module.invest.InvestActivity
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.fragment_cart.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.toCalendar
import org.supportcompact.ktx.toDate
import java.text.DateFormatSymbols
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [CartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CartFragment : CoreFragment<CartVM, FragmentCartBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.my_cart)

    override fun getLayout(): Int {
        return R.layout.fragment_cart
    }

    override fun createViewModel(): Class<out CartVM> {
        return CartVM::class.java
    }

    override fun setVM(binding: FragmentCartBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

        getViewModel().getCartItem().observe(this, android.arch.lifecycle.Observer { apiResponse ->

            apiResponse?.let {
                if (it.data.totalLumpsum == null)
                    getViewModel().totalLumpsum.set("NA")
                else
                    getViewModel().totalLumpsum.set(it.data.totalLumpsum.toString())
                if (it.data.totalSip == null)
                    getViewModel().totalSip.set("NA")
                else
                    getViewModel().totalSip.set(it.data.totalSip.toString())

                rvCartItems?.setUpRecyclerView(R.layout.row_cart_item, it.data.orderLines as ArrayList<CartData.Data.OrderLine>) { item: CartData.Data.OrderLine, binder: RowCartItemBinding, position ->
                    binder.fund = item
                    binder.executePendingBindings()
                    binder.tvAddOneTimeAmount.setOnClickListener {
                        //                        item.hasOneTimeAmount = true
                    }
                    binder.tvDate.setOnClickListener {
                        lateinit var now: Calendar
                        item.startDate.toDate("dd MMM yyyy").let { date ->
                            now = date.toCalendar()
                        }
                        SpinnerDatePickerDialogBuilder()
                                .context(context)
                                .callback { view, year, monthOfYear, dayOfMonth ->
                                    item.startDate = String.format("%02d %s %d", dayOfMonth, DateFormatSymbols().months[monthOfYear].substring(0, 3), year)
                                }
                                .showTitle(true)
                                .defaultDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                                //.minDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                                .build()
                                .show()
                    }
                }

            }

        })

        btnAddFund?.setOnClickListener { _ ->
            activity?.let {
                if (it is BaseActivity) {
                    if (it is InvestActivity) {
                        it.onBackPressed()
                    } else {
                        it.mBottomNav.selectedItemId = R.id.action_invest
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle argument.
         * @return A new instance of fragment CartFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = CartFragment().apply { arguments = basket }
    }
}

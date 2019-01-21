package com.tarrakki.module.cart


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.tarrakki.App
import com.tarrakki.BaseActivity
import com.tarrakki.R
import com.tarrakki.api.model.CartData
import com.tarrakki.databinding.FragmentCartBinding
import com.tarrakki.databinding.RowCartItemBinding
import com.tarrakki.module.invest.InvestActivity
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.fragment_cart.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.*
import java.util.*

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


        val cartApi = Observer<CartData> { apiResponse ->

            apiResponse?.let {

                if (it.data.totalLumpsum == null)
                    getViewModel().totalLumpsum.set("NA")
                else
                    getViewModel().totalLumpsum.set(it.data.totalLumpsum.toCurrency())
                if (it.data.totalSip == null)
                    getViewModel().totalSip.set("NA")
                else
                    getViewModel().totalSip.set(it.data.totalSip.toCurrency())

                getViewModel().funds = it.data.orderLines as ArrayList<CartData.Data.OrderLine>

                updateCartUI()

                App.INSTANCE.cartCount.value = getViewModel().funds.size

                rvCartItems?.setUpRecyclerView(R.layout.row_cart_item, getViewModel().funds) { item: CartData.Data.OrderLine, binder: RowCartItemBinding, position ->
                    item.hasOneTimeAmount = try {
                        val num = item.lumpsumAmount.toCurrency()
                        num > 0
                    } catch (e: Exception) {
                        false
                    }
                    binder.edtLumpsum.setText(item.lumpsumAmount.toCurrency().format())
                    binder.edtSIPAmount.setText(item.sipAmount.toCurrency().format())
                    binder.fund = item
                    binder.executePendingBindings()
                    /*binder.edtLumpsum.applyCurrencyFormatPositiveOnly()
                    binder.edtSIPAmount.applyCurrencyFormatPositiveOnly()*/

                    if (item.date.isNullOrEmpty()) {
                        binder.date = "Start Day"
                    } else {
                        binder.date = item.date
                    }
                    binder.tvAddOneTimeAmount.setOnClickListener {
                        item.hasOneTimeAmount = true
                    }
                    binder.ivDelete.setOnClickListener {
                        context?.confirmationDialog(getString(R.string.cart_delete), btnPositiveClick = {
                            getViewModel().deleteGoalFromCart(item.id.toString()).observe(this, Observer { apiResponse ->
                                getViewModel().funds.removeAt(position)
                                App.INSTANCE.cartCount.value = getViewModel().funds.size
                                getViewModel().cartUpdate.value = null
                                //rvCartItems?.adapter?.notifyDataSetChanged()
                                updateCartUI()
                            })
                        })
                    }
                    binder.edtLumpsum.setOnEditorActionListener { v, actionId, event ->
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            v.dismissKeyboard()
                            v.clearFocus()
                            item.lumpsumAmount = binder.edtLumpsum.text.toString()
                            getViewModel().updateGoalFromCart(item.id.toString(), item)
                            //rvCartItems?.adapter?.notifyDataSetChanged()
                            return@setOnEditorActionListener true
                        }
                        return@setOnEditorActionListener false
                    }
                    binder.edtSIPAmount.setOnEditorActionListener { v, actionId, event ->
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            v.dismissKeyboard()
                            v.clearFocus()
                            item.sipAmount = binder.edtSIPAmount.text.toString()
                            getViewModel().updateGoalFromCart(item.id.toString(), item)
                            return@setOnEditorActionListener true
                        }
                        return@setOnEditorActionListener false
                    }
                    binder.tvDate.setOnClickListener {
                        /*var now: Calendar = Calendar.getInstance()
                        item.date?.toDate("dd MMM yyyy")?.let { date ->
                            now = date.toCalendar()
                        }
                        SpinnerDatePickerDialogBuilder()
                                .context(context)
                                .callback { view, year, monthOfYear, dayOfMonth ->
                                    item.date = String.format("%02d %s %d", dayOfMonth, DateFormatSymbols().months[monthOfYear].substring(0, 3), year)
                                    getViewModel().updateGoalFromCart(item.id.toString(), item)
                                }
                                .showTitle(true)
                                .defaultDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                                //.minDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                                .build()
                                .show()*/
                        if (item.frequencyDate.isNotEmpty()) {
                            context?.showListDialog("Start Day", item.frequencyDate) {
                                binder.date = it
                                item.date = it
                                //getViewModel().updateGoalFromCart(item.id.toString(), item)
                            }
                        }
                    }
                }
            }

        }
        getViewModel().getCartItem().observe(this, cartApi)
        getViewModel().cartUpdate.observe(this, Observer {
            getViewModel().getCartItem().observe(this, cartApi)
        })
        /*getViewModel().getCartItem().observe(this, android.arch.lifecycle.Observer { apiResponse ->

            apiResponse?.let {

                if (it.data.totalLumpsum == null)
                    getViewModel().totalLumpsum.set("NA")
                else
                    getViewModel().totalLumpsum.set(it.data.totalLumpsum.toCurrency())
                if (it.data.totalSip == null)
                    getViewModel().totalSip.set("NA")
                else
                    getViewModel().totalSip.set(it.data.totalSip.toCurrency())

                getViewModel().funds = it.data.orderLines as ArrayList<CartData.Data.OrderLine>

                updateCartUI()

                App.INSTANCE.cartCount.value = getViewModel().funds.size

                rvCartItems?.setUpRecyclerView(R.layout.row_cart_item, getViewModel().funds) { item: CartData.Data.OrderLine, binder: RowCartItemBinding, position ->
                    item.hasOneTimeAmount = try {
                        val num = item.lumpsumAmount.toCurrency()
                        num > 0
                    } catch (e: java.lang.Exception) {
                        false
                    }
                    binder.edtLumpsum.setText(item.lumpsumAmount.toCurrency().format())
                    binder.edtSIPAmount.setText(item.sipAmount.toCurrency().format())
                    binder.fund = item
                    binder.executePendingBindings()
                    *//*binder.edtLumpsum.applyCurrencyFormatPositiveOnly()
                    binder.edtSIPAmount.applyCurrencyFormatPositiveOnly()*//*

                    if (item.date.isNullOrEmpty()) {
                        binder.date = "Start Day"
                    } else {
                        binder.date = item.date
                    }
                    binder.tvAddOneTimeAmount.setOnClickListener {
                        item.hasOneTimeAmount = true
                    }
                    binder.ivDelete.setOnClickListener {
                        context?.confirmationDialog(getString(R.string.cart_delete), btnPositiveClick = {
                            getViewModel().deleteGoalFromCart(item.id.toString()).observe(this, android.arch.lifecycle.Observer { apiResponse ->
                                getViewModel().funds.removeAt(position)
                                App.INSTANCE.cartCount.value = getViewModel().funds.size
                                //rvCartItems?.adapter?.notifyDataSetChanged()
                                createReference()
                                updateCartUI()
                            })
                        })
                    }
                    binder.edtLumpsum.setOnEditorActionListener { v, actionId, event ->
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            v.dismissKeyboard()
                            v.clearFocus()
                            item.lumpsumAmount = binder.edtLumpsum.text.toString()
                            getViewModel().updateGoalFromCart(item.id.toString(), item)
                            //rvCartItems?.adapter?.notifyDataSetChanged()
                            createReference()
                            return@setOnEditorActionListener true
                        }
                        return@setOnEditorActionListener false
                    }
                    binder.edtSIPAmount.setOnEditorActionListener { v, actionId, event ->
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            v.dismissKeyboard()
                            v.clearFocus()
                            item.sipAmount = binder.edtSIPAmount.text.toString()
                            getViewModel().updateGoalFromCart(item.id.toString(), item)
                            createReference()
                            return@setOnEditorActionListener true
                        }
                        return@setOnEditorActionListener false
                    }
                    binder.tvDate.setOnClickListener {
                        *//*var now: Calendar = Calendar.getInstance()
                        item.date?.toDate("dd MMM yyyy")?.let { date ->
                            now = date.toCalendar()
                        }
                        SpinnerDatePickerDialogBuilder()
                                .context(context)
                                .callback { view, year, monthOfYear, dayOfMonth ->
                                    item.date = String.format("%02d %s %d", dayOfMonth, DateFormatSymbols().months[monthOfYear].substring(0, 3), year)
                                    getViewModel().updateGoalFromCart(item.id.toString(), item)
                                }
                                .showTitle(true)
                                .defaultDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                                //.minDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                                .build()
                                .show()*//*
                        if (item.frequencyDate.isNotEmpty()) {
                            context?.showListDialog("Start Day", item.frequencyDate) {
                                binder.date = it
                                item.date = it
                                getViewModel().updateGoalFromCart(item.id.toString(), item)
                            }
                        }
                    }
                }
            }
        })*/

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

    private fun updateCartUI() {
        if (getViewModel().funds.isEmpty()) {
            lyt_orders?.visibility = View.GONE
            coreActivityVM?.emptyView(true)
        } else {
            coreActivityVM?.emptyView(false)
            lyt_orders?.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = CartFragment().apply { arguments = basket }
    }
}

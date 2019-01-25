package com.tarrakki.module.cart


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.tarrakki.*
import com.tarrakki.api.model.CartData
import com.tarrakki.databinding.FragmentCartBinding
import com.tarrakki.databinding.RowCartItemBinding
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.invest.InvestActivity
import com.tarrakki.module.recommended.ISFROMGOALRECOMMEDED
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
        setHasOptionsMenu(true)

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
                    binder.fund = item
                    item.hasOneTimeAmount = try {
                        val num = item.lumpsumAmount.toCurrency()
                        num > 0
                    } catch (e: Exception) {
                        false
                    }
                    binder.edtLumpsum.setText(item.lumpsumAmount.toCurrency().format())
                    binder.edtSIPAmount.setText(item.sipAmount.toCurrency().format())

                    if (item.goal != null) {
                        if (item.goal.goal.isNotEmpty()) {
                            binder.tvGoal.visibility = View.VISIBLE
                            binder.goal = "Goal: " + item.goal.goal
                        } else {
                            binder.tvGoal.visibility = View.GONE
                        }
                    } else {
                        binder.tvGoal.visibility = View.GONE
                    }

                    binder.executePendingBindings()

                    if (item.day.isNullOrEmpty()) {
                        binder.date = "Start Day"
                    } else {
                        binder.date = item.day?.toInt()?.let { it1 -> getOrdinalFormat(it1) }
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
                            val lumpsumAmount = binder.edtLumpsum.text.toString()
                            if (context?.isLumpsumAmountValid(item.validminlumpsumAmount, lumpsumAmount.toCurrencyInt())!!) {
                                item.lumpsumAmount = binder.edtLumpsum.text.toString()
                                getViewModel().updateGoalFromCart(item.id.toString(), item)
                            }
                            return@setOnEditorActionListener true
                        }
                        return@setOnEditorActionListener false
                    }
                    binder.edtSIPAmount.setOnEditorActionListener { v, actionId, event ->
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            v.dismissKeyboard()
                            v.clearFocus()
                            val sipAmount = binder.edtSIPAmount.text.toString()
                            if (context?.isSIPAmountValid(item.validminSIPAmount, sipAmount.toCurrencyInt())!!) {
                                item.sipAmount = binder.edtSIPAmount.text.toString()
                                getViewModel().updateGoalFromCart(item.id.toString(), item)
                            }
                            return@setOnEditorActionListener true
                        }
                        return@setOnEditorActionListener false
                    }
                    binder.tvDate.setOnClickListener {
                        if (item.frequencyDate.isNotEmpty()) {
                            context?.showListDialog("Start Day", item.frequencyDate) {
                                binder.date = it
                                item.day = it.dropLast(2)
                                getViewModel().updateGoalFromCart(item.id.toString(), item)
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
        getBinding().root.isFocusableInTouchMode = true
        getBinding().root.requestFocus()
        getBinding().root.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (arguments?.getBoolean(ISFROMGOALRECOMMEDED, false) == true) {
                    onBack(2)
                } else {
                    onBack()
                }
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    private fun updateCartUI() {
        if (getViewModel().funds.isEmpty()) {
            lyt_orders?.visibility = View.GONE
            coreActivityVM?.emptyView(true, "No funds in your cart.")
        } else {
            coreActivityVM?.emptyView(false)
            lyt_orders?.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = CartFragment().apply { arguments = basket }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.cart_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                if (arguments?.getBoolean(ISFROMGOALRECOMMEDED, false) == true) {
                    onBack(2)
                } else {
                    onBack()
                }
                return true
            }
            R.id.item_home -> {
                activity?.startActivity<HomeActivity>()
                if (activity !is HomeActivity) {
                    activity?.finishAffinity()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}

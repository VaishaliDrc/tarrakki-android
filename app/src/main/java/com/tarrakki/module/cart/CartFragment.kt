package com.tarrakki.module.cart


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import com.tarrakki.*
import com.tarrakki.api.model.CartData
import com.tarrakki.databinding.FragmentCartBinding
import com.tarrakki.databinding.RowCartItemBinding
import com.tarrakki.module.confirmorder.ConfirmOrderFragment
import com.tarrakki.module.funddetails.FundDetailsFragment
import com.tarrakki.module.funddetails.ITEM_ID
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.invest.InvestActivity
import com.tarrakki.module.recommended.ISFROMGOALRECOMMEDED
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.fragment_cart.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.BaseAdapter
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.*
import java.math.BigInteger
import java.util.*

class CartFragment : CoreFragment<CartVM, FragmentCartBinding>() {

    var adapter: BaseAdapter<CartData.Data.OrderLine, RowCartItemBinding>? = null

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

        getViewModel().cartUpdate.observe(this, Observer {
            getViewModel().getCartItem().observe(this, cartApi)
        })

        btnAddFund?.setOnClickListener {
            if (getString(R.string.edit_funds) == btnAddFund.text.toString()) {
                if (getViewModel().funds.isNotEmpty()) {
                    getViewModel().funds[0].reuestToEdit = true
                }
                return@setOnClickListener
            }
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

        btn_check_out?.setOnClickListener {
            if (validateCart()) {
                // context?.simpleAlert("Order Confirm Screen is still under development so you will be able to test it in the next build.")
                //  return@setOnClickListener
                startFragment(ConfirmOrderFragment.newInstance(), R.id.frmContainer)
            }
        }
    }

    override fun onResume() {
        getViewModel().getCartItem().observe(this, cartApi)
        super.onResume()
    }

    val cartApi = Observer<CartData> { apiResponse ->
        apiResponse?.let {
            if (it.data.totalLumpsum == null)
                getViewModel().totalLumpsum.set("N/A")
            else
                getViewModel().totalLumpsum.set(it.data.totalLumpsum.toCurrency())
            if (it.data.totalSip == null)
                getViewModel().totalSip.set("N/A")
            else
                getViewModel().totalSip.set(it.data.totalSip.toCurrency())

            getViewModel().funds = it.data.orderLines as ArrayList<CartData.Data.OrderLine>

            App.INSTANCE.cartCount.value = getViewModel().funds.size

            if (getViewModel().funds.isNotEmpty()) {
                adapter = rvCartItems?.setUpRecyclerView(R.layout.row_cart_item, getViewModel().funds) { item: CartData.Data.OrderLine, binder: RowCartItemBinding, position ->
                    binder.fund = item
                    item.hasOneTimeAmount = try {
                        val num = item.lumpsumAmount.toCurrencyBigInt()
                        num > BigInteger.ZERO
                    } catch (e: Exception) {
                        false
                    }
                    binder.edtLumpsum.applyCurrencyFormatPositiveOnly()
                    binder.edtSIPAmount.applyCurrencyFormatPositiveOnly()
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

                    if (item.day.isNullOrEmpty() || item.day == "0") {
                        binder.date = "Start Date"
                    } else {
                        binder.date = item.day?.toInt()?.let { it1 -> getOrdinalFormat(it1) }
                    }

                    binder.startDayDisable = item.sipAmount != "" && item.sipAmount != "0"

                    binder.tvAddOneTimeAmount.setOnClickListener {
                        item.hasOneTimeAmount = true
                    }
                    binder.ivDelete.setOnClickListener {
                        context?.confirmationDialog(getString(R.string.cart_delete), btnPositiveClick = {
                            getViewModel().deleteGoalFromCart(item.id.toString()).observe(this, Observer { apiResponse ->
                                getViewModel().funds.removeAt(position)
                                App.INSTANCE.cartCount.value = getViewModel().funds.size
                                getViewModel().cartUpdate.value = null
                                updateCartUI()
                            })
                        })
                    }
                    binder.edtLumpsum.setOnEditorActionListener { v, actionId, event ->
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            v.dismissKeyboard()
                            v.clearFocus()
                            val sipAmount = binder.edtSIPAmount.text.toString()
                            val lumpsumAmount = binder.edtLumpsum.text.toString()
                            if (context?.isLumpsumAndSIPAmountValid(sipAmount.toCurrencyBigInt(), lumpsumAmount.toCurrencyBigInt()) == true) {
                                if (context?.isLumpsumAmountValid(item.validminlumpsumAmount, lumpsumAmount.toCurrencyBigInt())!!) {
                                    item.lumpsumAmount = binder.edtLumpsum.text.toString()
                                    getViewModel().updateGoalFromCart(item.id.toString(), item)
                                }
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
                            val lumpsumAmount = binder.edtLumpsum.text.toString()
                            if (context?.isLumpsumAndSIPAmountValid(sipAmount.toCurrencyBigInt(), lumpsumAmount.toCurrencyBigInt()) == true) {
                                if (context?.isSIPAmountValid(item.validminSIPAmount, sipAmount.toCurrencyBigInt())!!) {
                                    item.sipAmount = binder.edtSIPAmount.text.toString()
                                    getViewModel().updateGoalFromCart(item.id.toString(), item)
                                }
                            }
                            return@setOnEditorActionListener true
                        }
                        return@setOnEditorActionListener false
                    }
                    binder.tvDate.setOnClickListener {
                        it?.dismissKeyboard()
                        if (binder.startDayDisable == true) {
                            if (item.frequencyDate.isNotEmpty()) {
                                context?.showListDialog("Start Date", item.frequencyDate) {
                                    binder.date = it
                                    item.day = it.dropLast(2)
                                    getViewModel().updateGoalFromCart(item.id.toString(), item)
                                }
                            }
                        } else {
                            context?.simpleAlert("Please enter the valid SIP amount first.")
                        }

                    }
                    binder.edtSIPAmount.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(p0: Editable?) {
                        }

                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            item.sipAmount = binder.edtSIPAmount.text.toString().toCurrencyBigInt().toString()
                        }
                    })
                    binder.edtLumpsum.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(p0: Editable?) {
                        }

                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            item.lumpsumAmount = binder.edtLumpsum.text.toString().toCurrencyBigInt().toString()
                        }
                    })

                    binder.root.setOnClickListener {
                        startFragment(FundDetailsFragment.newInstance(Bundle().apply {
                            putString(ITEM_ID, "${item.fundIdId}")
                        }
                        ), R.id.frmContainer)
                    }

                    binder.executePendingBindings()
                }
            }
            updateCartUI()
        }
    }

    private fun updateCartUI() {
        if (getViewModel().funds.isEmpty()) {
            getViewModel().isEmptyCart.set(true)
            coreActivityVM?.emptyView(true, "No funds in your cart.")
        } else {
            getViewModel().isEmptyCart.set(false)
            coreActivityVM?.emptyView(false)
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

    private fun validateCart(): Boolean {
        var isValid = true
        val cartItems = adapter?.getItems()
        loop@ for (i in 0 until (cartItems?.size ?: 0)) {
            val item = cartItems?.get(i) as CartData.Data.OrderLine
            val sipAmount = item.sipAmount.toCurrencyBigInt()
            val lumpsumpAmount = item.lumpsumAmount.toCurrencyBigInt()
            val minlumpsumpAmount = item.validminlumpsumAmount
            val minsipAmount = item.validminSIPAmount

            if (context?.isLumpsumAndSIPAmountValid(sipAmount,lumpsumpAmount)==false) {
                context?.simpleAlert("Please enter either the lumpsum or the SIP amount first.") {
                    Handler().postDelayed({
                        if (getViewModel().funds.isNotEmpty()) {
                            getViewModel().funds[i].reuestToEdit = true
                        }
                    }, 100)
                }
                isValid = false
                break@loop
            } else {
                if (context?.isLumpsumAmountValid(minlumpsumpAmount, lumpsumpAmount) == false) {
                    Handler().postDelayed({
                        if (getViewModel().funds.isNotEmpty()) {
                            getViewModel().funds[i].reuestToEdit = true
                        }
                    }, 100)
                    isValid = false
                    break@loop
                }

                if (context?.isSIPAmountValid(minsipAmount, sipAmount) == false) {
                    Handler().postDelayed({
                        if (getViewModel().funds.isNotEmpty()) {
                            getViewModel().funds[i].reuestToEdit = true
                        }
                    }, 100)
                    isValid = false
                    break@loop
                }else {
                    if (sipAmount != BigInteger.ZERO) {
                        if (item.day == null || item.day == "" || item.day == "0") {
                            context?.simpleAlert("Please Select Start Date.") {
                                Handler().postDelayed({
                                    if (getViewModel().funds.isNotEmpty()) {
                                        getViewModel().funds[i].reuestToEdit = true
                                    }
                                }, 100)
                            }
                            isValid = false
                            break@loop
                        }
                    }
                }
            }
            isValid = true
        }
        return isValid
    }
}

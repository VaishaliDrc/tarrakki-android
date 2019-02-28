package com.tarrakki.module.invest


import android.arch.lifecycle.Observer
import android.databinding.Observable
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.addToCart
import com.tarrakki.api.model.InvestmentFunds
import com.tarrakki.databinding.*
import com.tarrakki.investDialog
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.funddetails.FundDetailsFragment
import com.tarrakki.module.funddetails.ITEM_ID
import kotlinx.android.synthetic.main.fragment_invest.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.dismissKeyboard
import org.supportcompact.ktx.parseToPercentageOrNA
import org.supportcompact.ktx.startFragment
import org.supportcompact.ktx.*
import org.supportcompact.utilise.EqualSpacingItemDecoration


/**
 * A simple [Fragment] subclass.
 * Use the [InvestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InvestFragment : CoreFragment<InvestVM, FragmentInvestBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.explore_all_funds)

    override fun getLayout(): Int {
        return R.layout.fragment_invest
    }

    override fun createViewModel(): Class<out InvestVM> {
        return InvestVM::class.java
    }

    override fun setVM(binding: FragmentInvestBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        rvFunds?.addItemDecoration(EqualSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.space_item)))

        setHasOptionsMenu(true)

        val categories = arrayListOf(getString(R.string.all))
        val subcategories = arrayListOf(getString(R.string.all))
        val adapter = ArrayAdapter(
                activity,
                R.layout.simple_spinner_item,
                categories
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spnCategory.adapter = adapter
        val adapterSub = ArrayAdapter(
                activity,
                R.layout.simple_spinner_item,
                subcategories
        )
        adapterSub.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spnSubCategory.adapter = adapterSub


        ivExClp?.setOnClickListener {
            getViewModel().filter.set(!getViewModel().filter.get()!!)
        }

        rvFunds?.isFocusable = false
        rvFunds?.isNestedScrollingEnabled = false
        val observerFundsData = Observer<InvestmentFunds> { response ->
            mRefresh?.isRefreshing = false
            response?.let {
                coreActivityVM?.emptyView(response.funds.isEmpty())
                rvFunds?.setUpRecyclerView(R.layout.row_fund_list_item, response.funds) { item: InvestmentFunds.Fund, binder: RowFundListItemBinding, position ->
                    item.FDReturn = parseToPercentageOrNA(response.fixedDepositReturn)
                    binder.fund = item
                    binder.executePendingBindings()
                    binder.btnInvest.setOnClickListener {
                        context?.investDialog(item.id, item.validminSIPAmount,
                                item.validminlumpsumAmount) { amountLumpsum, amountSIP, fundId ->
                            addToCart(fundId,amountSIP,amountLumpsum).observe(this,
                                    android.arch.lifecycle.Observer {
                                response ->
                                        context?.simpleAlert(getString(R.string.cart_fund_added)){
                                            startFragment(CartFragment.newInstance(), R.id.frmContainer)
                                        }
                                    })
                        }
                    }
                    binder.root.setOnClickListener {
                        startFragment(FundDetailsFragment.newInstance(Bundle().apply { putString(ITEM_ID, "${item.id}") }), R.id.frmContainer)
                    }
                }
                categories.clear()
                categories.add(getString(R.string.all))
                response.fscbiCategoryList?.forEach { item ->
                    categories.add(item.name)
                }
                subcategories.clear()
                subcategories.add(getString(R.string.all))
                response.fscbiBroadCategoryList?.forEach { item ->
                    subcategories.add(item.name)
                }
                adapter.notifyDataSetChanged()
                adapterSub.notifyDataSetChanged()
                if (getViewModel().isInit) {
                    Handler().postDelayed({ getViewModel().isInit = false }, 2500)
                }
            }
        }
        mScrollView?.viewTreeObserver?.addOnScrollChangedListener {
            val view: View? = mScrollView?.getChildAt(mScrollView.childCount - 1)
            if (view != null && mScrollView != null) {
                val diff = view.bottom - (mScrollView.height + mScrollView.scrollY)
                getViewModel().response.value?.let {
                    //e("Diff=>$diff")
                    if (it.funds.size >= 10 && it.funds.size < it.totalFunds && diff <= 100 && !getViewModel().loadMore.get()!!) {
                        getViewModel().loadMore.set(true)
                    }
                }
            }
        }
        var riskLevel = 0
        rvRiskLevel?.setUpRecyclerView(R.layout.row_risk_level_indicator, getViewModel().arrRiskLevel) { item: RiskLevel, binder: RowRiskLevelIndicatorBinding, position ->
            binder.riskLevel = item
            binder.executePendingBindings()
            binder.root.setOnClickListener {
                if (cbLevel.isChecked) {
                    getViewModel().arrRiskLevel[riskLevel].isSelected = false
                    item.isSelected = !item.isSelected
                    riskLevel = position
                    getViewModel().riskLevel.value = getViewModel().response.value?.getRiskLevelId(item.name)
                }
            }
        }
        cbLevel?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                getViewModel().riskLevel.value = getViewModel().response.value?.getRiskLevelId(getViewModel().arrRiskLevel[riskLevel].name)
            } else {
                getViewModel().riskLevel.value = null
                getViewModel().arrRiskLevel[riskLevel].isSelected = false
                riskLevel = 0
                getViewModel().arrRiskLevel[riskLevel].isSelected = true
                rvRiskLevel?.adapter?.notifyDataSetChanged()
            }
        }
        rvFundType?.setUpRecyclerView(R.layout.row_fund_type_list_item, getViewModel().fundTypes) { item: FundType, binder: RowFundTypeListItemBinding, position ->
            binder.fundType = item
            binder.executePendingBindings()
            binder.tvFundType.setOnClickListener {
                item.isSelected = !item.isSelected
                getViewModel().ourRecommended.value = true
            }
        }
        var selectedAt = 1
        rvFundReturns?.setUpRecyclerView(R.layout.row_fund_returns_list_item, getViewModel().fundReturns) { item: FundType, binder: RowFundReturnsListItemBinding, position ->
            binder.fundType = item
            binder.executePendingBindings()
            binder.tvFundType.setOnClickListener {
                if (selectedAt != -1) {
                    getViewModel().fundReturns[selectedAt].isSelected = false
                }
                item.isSelected = !item.isSelected
                selectedAt = position
                getViewModel().sortByReturn.value = Pair(item.key, item.value)
            }
        }
        cbGrowth?.setOnCheckedChangeListener { buttonView, isChecked ->
            getViewModel().investmentType.value = Pair(isChecked, cbDividend.isChecked)
        }

        cbDividend?.setOnCheckedChangeListener { buttonView, isChecked ->
            getViewModel().investmentType.value = Pair(cbDividend.isChecked, isChecked)
        }

        spnCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                getViewModel().category.value = if (position == 0) 0 else getViewModel().response.value?.fscbiCategoryList?.firstOrNull { c -> c.name == categories[position] }?.id
                if (!getViewModel().isInit) {
                    getViewModel().getFunds().observe(this@InvestFragment, observerFundsData)
                }
            }
        }
        spnSubCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                getViewModel().subcategory.value = if (position == 0) 0 else getViewModel().response.value?.fscbiBroadCategoryList?.firstOrNull { c -> c.name == categories[position] }?.id
                if (!getViewModel().isInit) {
                    getViewModel().getFunds().observe(this@InvestFragment, observerFundsData)
                }
            }
        }

        getViewModel().getFunds().observe(this, observerFundsData)

        mRefresh?.setOnRefreshListener {
            getViewModel().getFunds(mRefresh = true).observe(this, observerFundsData)
        }

        edtSearch?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (edtSearch?.length() == 0 && rvFunds?.childCount == 0) {
                    edtSearch?.dismissKeyboard()
                    edtSearch?.clearFocus()
                    getViewModel().searchBy.value = edtSearch?.text.toString()
                    getViewModel().getFunds().observe(this@InvestFragment, observerFundsData)
                }
            }
        })

        /**Filter Observation**/
        getViewModel().ourRecommended.observe(this, Observer {
            getViewModel().getFunds().observe(this, observerFundsData)
        })
        getViewModel().riskLevel.observe(this, Observer {
            getViewModel().getFunds().observe(this, observerFundsData)
        })
        getViewModel().sortByReturn.observe(this, Observer {
            getViewModel().getFunds().observe(this, observerFundsData)
        })
        getViewModel().investmentType.observe(this, Observer {
            getViewModel().getFunds().observe(this, observerFundsData)
        })
        getViewModel().searchBy.observe(this, Observer {
            getViewModel().getFunds().observe(this, observerFundsData)
        })
        edtSearch?.setOnEditorActionListener { v, actionId, event ->
            if (EditorInfo.IME_ACTION_SEARCH == actionId) {
                v.dismissKeyboard()
                v.clearFocus()
                getViewModel().searchBy.value = edtSearch?.text.toString()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        getViewModel().loadMore.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                getViewModel().response.value?.let { investmentFunds ->
                    if (getViewModel().loadMore.get()!!) {
                        mScrollView.post {
                            mScrollView.scrollTo(0, pbLoadMore.bottom)
                        }
                        Handler().postDelayed({
                            getViewModel().getFunds(investmentFunds.offset).observe(this@InvestFragment, observerFundsData)
                        }, 2500)
                    } else {
                        rvFunds?.adapter?.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.home_menu, menu)
        val tvCartCount = menu?.findItem(R.id.itemHome)?.actionView?.findViewById<TextView>(R.id.tvCartCount)
        App.INSTANCE.cartCount.observe(this, Observer {
            tvCartCount?.text = it.toString()
        })
        menu?.findItem(R.id.itemHome)?.actionView?.setOnClickListener {
            startFragment(CartFragment.newInstance(), R.id.frmContainer)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket as Bundle.119400041473
         * @return A new instance of fragment InvestFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = InvestFragment().apply { arguments = basket }

    }
}

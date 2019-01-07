package com.tarrakki.module.invest


import android.arch.lifecycle.Observer
import android.databinding.Observable
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.InvestmentFunds
import com.tarrakki.databinding.*
import com.tarrakki.investDialog
import com.tarrakki.module.cart.CartFragment
import kotlinx.android.synthetic.main.fragment_invest.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.dismissKeyboard
import org.supportcompact.ktx.parseToPercentageOrNA
import org.supportcompact.ktx.startFragment
import org.supportcompact.ktx.toast


/**
 * A simple [Fragment] subclass.
 * Use the [InvestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InvestFragment : CoreFragment<InvestVM, FragmentInvestBinding>() {

    override val isBackEnabled: Boolean
        get() = false
    override val title: String
        get() = getString(R.string.invest)

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
            response?.let {
                rvFunds?.setUpRecyclerView(R.layout.row_fund_list_item, response.funds) { item: InvestmentFunds.Fund, binder: RowFundListItemBinding, position ->
                    item.FDReturn = parseToPercentageOrNA(response.fixedDepositReturn)
                    binder.fund = item
                    binder.executePendingBindings()
                    binder.btnInvest.setOnClickListener {
                        context?.investDialog { amountLumpsum, amountSIP, duration ->
                            /**onInvest called*/
                        }
                        //startFragment(CartFragment.newInstance(), R.id.frmContainer)
                    }
                    binder.root.setOnClickListener {
                        //startFragment(FundDetailsFragment.newInstance(Bundle().apply { putSerializable(ITEM, item) }), R.id.frmContainer)
                    }
                    if (position == response.funds.size - 1 && !getViewModel().loadMore.get()!!) {
                        getViewModel().loadMore.set(true)
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
        rvFunds?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lm = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = lm.childCount
                val totalItemCount = lm.itemCount
                val firstVisibleItemPosition = lm.findFirstVisibleItemPosition()
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    toast("Load more")
                }
            }
        })
        var riskLevel = 0
        rvRiskLevel?.setUpRecyclerView(R.layout.row_risk_level_indicator, getViewModel().arrRiskLevel) { item: RiskLevel, binder: RowRiskLevelIndicatorBinding, position ->
            binder.riskLevel = item
            binder.executePendingBindings()
            binder.root.setOnClickListener {
                getViewModel().arrRiskLevel[riskLevel].isSelected = false
                item.isSelected = !item.isSelected
                riskLevel = position
                if (cbLevel.isChecked) {
                    getViewModel().riskLevel.value = getViewModel().response.value?.getRiskLevelId(item.name)
                }
            }
        }
        cbLevel?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                getViewModel().riskLevel.value = getViewModel().response.value?.getRiskLevelId(getViewModel().arrRiskLevel[riskLevel].name)
            } else {
                getViewModel().riskLevel.value = null
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
                if (getViewModel().loadMore.get()!!) {
                    Handler().postDelayed({
                        getViewModel().getFunds(10).observe(this@InvestFragment, observerFundsData)
                    }, 2500)
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

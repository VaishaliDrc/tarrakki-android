package com.tarrakki.module.prime_investor

import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.databinding.Observable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.Fundd
import com.tarrakki.databinding.FragmentPrimeInvestorMutualFundListBinding
import com.tarrakki.databinding.RowPrimeInvestorMutualFundListBinding
import kotlinx.android.synthetic.main.fragment_prime_investor_mutual_fund_list.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.BaseAdapter
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment

class PrimeInvestorMutualFundListFragment : CoreFragment<PrimeInvestorMutualFundListVM, FragmentPrimeInvestorMutualFundListBinding>() {


    private var adapter: BaseAdapter<Fundd, RowPrimeInvestorMutualFundListBinding>? = null
    var isFirst = false

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.tarrakki_mf_assistant)

    override fun getLayout(): Int {
        return R.layout.fragment_prime_investor_mutual_fund_list
    }

    override fun createViewModel(): Class<out PrimeInvestorMutualFundListVM> {
        return PrimeInvestorMutualFundListVM::class.java
    }

    override fun setVM(binding: FragmentPrimeInvestorMutualFundListBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

        isFirst = true
        setHasOptionsMenu(true)

        rvScheme.isFocusable = false
        rvScheme.isNestedScrollingEnabled = false


        mRefresh?.setOnRefreshListener {
            mRefresh?.isRefreshing = false
            getViewModel().schemaList.clear()
            getViewModel().offset = 0
            getViewModel().isGrowthScheme = true
            getViewModel().isASC = true
            getViewModel().search.set("")
            getViewModel().totalFund = 0
            callApi()
//            getViewModel().getLiquiloansSchemaAPI().observe(this, observerLiquiloansSchemeData)
        }

        imgSearch.setOnClickListener {
            val inputMethodManager = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it?.getWindowToken(), 0)

            getViewModel().schemaList.clear()
            getViewModel().offset = 0
            getViewModel().totalFund = 0
            callApi()
        }

        edtSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    val inputMethodManager = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(v?.getWindowToken(), 0)

                    getViewModel().schemaList.clear()
                    getViewModel().offset = 0
                    getViewModel().totalFund = 0
                    callApi()
                    return true;
                }
                return false;
            }

        })

        rgSelectFunds.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            var selectedId = rgSelectFunds.checkedRadioButtonId
            val radio: RadioButton = group.findViewById(selectedId)
            if (rbDividendScheme == radio) {
                getViewModel().schemaList.clear()
                getViewModel().offset = 0
                getViewModel().totalFund = 0
                getViewModel().isGrowthScheme = false
                callApi()
            } else {
                getViewModel().schemaList.clear()
                getViewModel().offset = 0
                getViewModel().totalFund = 0
                getViewModel().isGrowthScheme = true
                callApi()
            }


        })

        llSaveForReview.setOnClickListener {
//            val list = getViewModel().schemaList.filter { it?.isAdded == true }
            if (App.INSTANCE.primeInvestorList.isEmpty())
                context?.simpleAlert(getString(R.string.please_add_fund))
            else {

//                var obj = PrimeInvestorMutualFundListReviewFragment.ListReview()
//                obj.list.addAll(list)
                startFragment(PrimeInvestorMutualFundListReviewFragment.newInstance(), R.id.frmContainer)
//                EventBus.getDefault().postSticky(obj)
            }
        }

        txtSchemeName.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                val DRAWABLE_LEFT = 0
                val DRAWABLE_TOP = 1
                val DRAWABLE_RIGHT = 2
                val DRAWABLE_BOTTOM = 3
                if (event.getAction() === MotionEvent.ACTION_DOWN) {
                    if (event.getRawX() >= txtSchemeName.getRight() - txtSchemeName.getCompoundDrawables().get(DRAWABLE_RIGHT).getBounds().width()) {
                        getViewModel().schemaList.clear()
                        getViewModel().offset = 0
                        getViewModel().totalFund = 0
                        getViewModel().isASC = getViewModel().isASC.not()
                        callApi()
                        return true
                    }
                }
                return false
            }

        })

        adapter = rvScheme.setUpRecyclerView(
                R.layout.row_prime_investor_mutual_fund_list,
                getViewModel().schemaList) { item: Fundd?, binder: RowPrimeInvestorMutualFundListBinding, position ->
            binder.setVariable(BR.vm, item)

            if (position % 2 == 0) {
                binder.llMutualFundList.setBackgroundColor(Color.WHITE)
            } else {
                binder.llMutualFundList.setBackgroundColor(Color.parseColor("#f9f9f9"))
            }

            binder.txtAdd.setOnClickListener {
                if (item?.isAdded == false) {
                    if (App.INSTANCE.primeInvestorList.size < 15) {
                        App.INSTANCE.primeInvestorList.add(getViewModel().schemaList.get(position))
                        getViewModel().schemaList.get(position)?.isAdded = true
                        adapter?.notifyItemChanged(position)
                    } else {
                        context?.simpleAlert(getString(R.string.maximum_limit_reached))
                    }
                } else {
                    App.INSTANCE.primeInvestorList.remove(getViewModel().schemaList.get(position))
                    getViewModel().schemaList.get(position)?.isAdded = false
                    adapter?.notifyItemChanged(position)
                }
            }

            binder.executePendingBindings()
        }

        mScrollView?.viewTreeObserver?.addOnScrollChangedListener {
            val view: View? = mScrollView?.getChildAt(mScrollView.childCount - 1)
            if (view != null && mScrollView != null) {
                val diff = view.bottom - (mScrollView.height + mScrollView.scrollY)
                getViewModel().schemaList.let {
                    //e("Diff=>$diff")
                    if (it.size >= 20 && it.size < getViewModel().totalFund && diff <= 100 && !getViewModel().loadMore.get()!!) {
                        getViewModel().loadMore.set(true)
                    }
                }
            }
        }

        getViewModel().loadMore.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (getViewModel().loadMore.get()!!) {
                    mScrollView.post {
                        if (isFirst == false)
                            mScrollView.scrollTo(0, pbLoadMore.bottom)
                        else isFirst = true
                    }
                    Handler().postDelayed({
                        callApi()
                    }, 1500)
                } else {
                    setPrimeListData()
                }
            }
        })

        callApi()
    }

    private fun callApi() {
        getViewModel().getMutualFundsAPI().observe(this, Observer {
            setPrimeListData()
        })
    }

    override fun onResume() {
        super.onResume()
        if (getViewModel().schemaList.isNotEmpty())
            setPrimeListData()
    }

    private fun setPrimeListData() {

        if (App.INSTANCE.primeInvestorList.isNotEmpty()) {

            getViewModel().schemaList.forEachIndexed { index, fundd ->
                val temp = App.INSTANCE.primeInvestorList.indexOfFirst {
                    it?.id == fundd?.id
                }
                getViewModel().schemaList.get(index)?.isAdded = temp != -1
            }
        } else {
            getViewModel().schemaList.forEachIndexed { index, fundd ->
                getViewModel().schemaList.get(index)?.isAdded = false
            }
        }
        adapter?.notifyDataSetChanged()

    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = PrimeInvestorMutualFundListFragment().apply { arguments = basket }
    }
}

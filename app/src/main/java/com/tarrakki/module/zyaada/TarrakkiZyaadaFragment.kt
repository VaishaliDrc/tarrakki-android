package com.tarrakki.module.zyaada

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.TextPaint
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.view.View
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.addToCartTarrakkiZyaada
import com.tarrakki.chartformaters.BarChartCustomRenderer
import com.tarrakki.chartformaters.CustomXAxisRenderer
import com.tarrakki.chartformaters.MyYAxisValueFormatter
import com.tarrakki.databinding.FragmentTarrakkiZyaadaBinding
import com.tarrakki.databinding.PageTarrakkiZyaadaItemBinding
import com.tarrakki.databinding.RowFundKeyInfoListItemBinding
import com.tarrakki.investDialog
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.funddetails.FundDetailsFragment
import com.tarrakki.module.funddetails.ITEM_ID
import com.tarrakki.module.funddetails.KeyInfo
import kotlinx.android.synthetic.main.fragment_tarrakki_zyaada.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setAutoWrapContentPageAdapter
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.*
import org.supportcompact.utilise.ResourceUtils
import java.util.*


/**
 * A simple [Fragment]
 * Use the [TarrakkiZyaadaFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TarrakkiZyaadaFragment : CoreFragment<TarrakkiZyaadaVM, FragmentTarrakkiZyaadaBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.tarrakki_zyaada)

    override fun getLayout(): Int {
        return R.layout.fragment_tarrakki_zyaada
    }

    override fun createViewModel(): Class<out TarrakkiZyaadaVM> {
        return TarrakkiZyaadaVM::class.java
    }

    override fun setVM(binding: FragmentTarrakkiZyaadaBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

        tvWhatTarrakkii?.setOnClickListener {
            getViewModel().whatIsTarrakkiZyaada.get()?.let {
                getViewModel().whatIsTarrakkiZyaada.set(!it)
            }
        }
        tvWhereIsMyMoney?.setOnClickListener {
            getViewModel().whereIsMyMoney.get()?.let {
                getViewModel().whereIsMyMoney.set(!it)
            }
        }

        val imgs = arrayListOf(R.drawable.zyaada1, R.drawable.zyaada2, R.drawable.zyaada3)
        mAutoPager?.setAutoWrapContentPageAdapter(R.layout.page_tarrakki_zyaada_item, imgs) { binder: PageTarrakkiZyaadaItemBinding, item: Int ->
            binder.imgRes = item
            binder.executePendingBindings()
        }
        pageIndicator?.setViewPager(mAutoPager)
        mAutoPager?.interval = 4000
        mAutoPager?.startAutoScroll()
        mAutoPager?.isNestedScrollingEnabled = false

        val durationsArr = resources.getStringArray(R.array.duration_in_year)

        getViewModel().getTarrakkiZyaada().observe(this, Observer {
            it?.data?.let { response ->
                savingRate = response.bankSavingsReturn?.toDoubleOrNull() ?: 0.0
                fixedDepositRate = response.fixedDepositReturn?.toDoubleOrNull() ?: 0.0
                if (response.funds?.isNotEmpty() == true) {
                    val fund = response.funds[0]
                    val returns = arrayListOf<KeyInfo>()
                    returns.add(KeyInfo("1 Year", parseAsNoZiroReturnOrNA(fund.ttrReturn1Yr)))
                    returns.add(KeyInfo("3 Years", parseAsNoZiroReturnOrNA(fund.ttrReturn3Yr)))
                    returns.add(KeyInfo("5 Years", parseAsNoZiroReturnOrNA(fund.ttrReturn5Yr)))
                    returns.add(KeyInfo("10 Years", parseAsNoZiroReturnOrNA(fund.ttrReturn10Yr)))
                    returns.add(KeyInfo("Since Inception", parseAsNoZiroReturnOrNA(fund.ttrReturnSinceInception)))
                    rvReturns?.setUpRecyclerView(R.layout.row_fund_key_info_list_item, returns) { item: KeyInfo, binder: RowFundKeyInfoListItemBinding, position ->
                        binder.keyInfo = item
                        binder.executePendingBindings()
                    }

                    val fundDetails = object : ClickableSpan() {

                        override fun onClick(widget: View) {
                            startFragment(FundDetailsFragment.newInstance(Bundle().apply {
                                putString(ITEM_ID, "${fund.id}")
                            }), R.id.frmContainer)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.isUnderlineText = true
                            context?.color(R.color.colorAccent)?.let { ds.color = it }
                        }
                    }
                    tvFundDetails?.makeLinks(arrayOf("click here."), arrayOf(fundDetails))

                    btnInvest?.setOnClickListener {
                        val tarrakkiZyaadaId = response.tarrakkiZyaadaId
                        val minSIPAmount = fund.validminSIPAmount
                        val minLumpSumAmount = fund.validminlumpsumAmount
                        if (tarrakkiZyaadaId != null && minSIPAmount != null && minLumpSumAmount != null) {
                            context?.investDialog(tarrakkiZyaadaId, minSIPAmount, minLumpSumAmount) { amountLumpsum, amountSIP, Id ->
                                addToCartTarrakkiZyaada("$tarrakkiZyaadaId", amountSIP, amountLumpsum).observe(this,
                                        android.arch.lifecycle.Observer { response ->
                                            context?.simpleAlert(getString(R.string.cart_fund_added)) {
                                                startFragment(CartFragment.newInstance(), R.id.frmContainer)
                                            }
                                        })
                            }
                        }
                    }
                    var r = fund.getReturn(x = durations.toInt())
                    if (r == 0.0) {
                        r = fund.getReturn()
                        durations = 1.0
                    }
                    setChartData(r)
                    tvDurations.text = durationsArr[if (durations == 3.0) 2 else 0]
                    tvDurations?.setOnClickListener {
                        context?.showListDialog(R.string.duration, durationsArr) { item: String, which: Int ->
                            tvDurations.text = item
                            if (which == 10) {//Since Inception
                                val returnSince = fund.ttrReturnSinceInception?.toDoubleOrNull()
                                        ?: 0.0
                                val startDate = fund.returnsHistory?.minBy { it.date }?.date
                                startDate?.let {
                                    val months = it.monthsBetweenDates(Date())
                                    durations = (months / 12.0)
                                    setChartData(returnSince)
                                }
                            } else {
                                durations = (which + 1).toDouble()
                                var r = fund.getReturn(x = durations.toInt())
                                if (r == 0.0) {
                                    r = fund.getReturn()
                                    durations = 1.0
                                    tvDurations.text = durationsArr[0]
                                }
                                setChartData(r)
                            }
                        }
                    }
                    edtInvestAmount?.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(p: Editable?) {
                            if (p != null && p.isNotEmpty()) {
                                try {
                                    investmebtAmount = p.toString().replace(",", "").toDouble()
                                    setChartData(fundReturnsFormate)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                resetChart()
                            }
                        }

                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                        }

                        private var current = ""

                        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            if (s == null || s.isEmpty()) {
                                current = ""
                                return
                            }
                            if (s.toString() != current) {
                                try {
                                    edtInvestAmount?.removeTextChangedListener(this)
                                    var cleanString = s.toString().replace(",", "")
                                    if (cleanString.length > 16) {
                                        cleanString = cleanString.dropLast(1)
                                    }
                                    val price = cleanString.toDouble()
                                    if (price > 0) {
                                        edtInvestAmount?.format(price)
                                    } else {
                                        edtInvestAmount?.text?.clear()
                                    }
                                    current = edtInvestAmount?.text.toString()
                                    edtInvestAmount?.setSelection(current.length)
                                    edtInvestAmount?.addTextChangedListener(this)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    })
                }
            }
        })
    }

    var fundReturnsFormate = 0.0
    var savingRate = 4.0
    var fixedDepositRate = 6.5
    var investmebtAmount = 500000.0
    var durations = 3.0


    private fun setChartData(returnsFormate: Double) {

        val yVals1 = ArrayList<BarEntry>()
        fundReturnsFormate = returnsFormate

        /*Chart Settings*/
        mBarChart.setPinchZoom(false)
        mBarChart.setScaleEnabled(false)
        mBarChart.setTouchEnabled(false)
        mBarChart.description.isEnabled = false
        mBarChart.legend.isEnabled = false
        mBarChart.renderer = BarChartCustomRenderer(mBarChart, mBarChart.animator, mBarChart.viewPortHandler)
        mBarChart.setDrawValueAboveBar(true)
        mBarChart.extraBottomOffset = ResourceUtils.dpToPx(16).toFloat()
        mBarChart.setXAxisRenderer(CustomXAxisRenderer(mBarChart.viewPortHandler, mBarChart.xAxis, mBarChart.getTransformer(YAxis.AxisDependency.LEFT)))
        val typeface = context?.let { ResourcesCompat.getFont(it, R.font.lato_regular) }


        val xAxis = mBarChart.xAxis
        xAxis.isEnabled = true
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(false)
        xAxis.labelCount = 3
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.typeface = typeface
        xAxis.valueFormatter = IAxisValueFormatter { value, axis ->
            try {
                val index = value.toInt()
                when (index) {
                    1 -> "Savings\nAccount"
                    2 -> "Fixed\nDeposit"
                    else -> "Tarrakki\nZyaada"
                }
            } catch (e: Exception) {
                ""
            }
        }
        xAxis.textSize = 12f


        val leftAxis = mBarChart.axisRight
        leftAxis.isEnabled = false
        leftAxis.labelCount = 10
        leftAxis.axisMaximum = 10f//if (fixedDepositRate > fundReturns) fixedDepositRate.toFloat() else fundReturns.toFloat()
        leftAxis.axisMinimum = 0f//savingRate.toFloat()
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawZeroLine(false)
        leftAxis.setDrawGridLines(false)
        leftAxis.valueFormatter = MyYAxisValueFormatter()
        getColor(R.color.semi_black)?.let {
            leftAxis.textColor = it
        }
        mBarChart.axisLeft.isEnabled = false
        leftAxis.typeface = typeface

        for (i in 1..3) {
            when (i) {
                1 -> {
                    val val1 = 5//investmentAmount.toFloat()
                    val val2 = savingRate//calculateReturns(investmentAmount, durations, savingRate).toFloat()
                    yVals1.add(BarEntry(1f, floatArrayOf(val1.toFloat(), val2.toFloat())))
                }
                2 -> {
                    val val1 = 5//investmentAmount.toFloat()
                    val val2 = fixedDepositRate//calculateReturns(investmentAmount, durations, fixedDepositRate).toFloat()
                    yVals1.add(BarEntry(2f, floatArrayOf(val1.toFloat(), val2.toFloat())))
                }
                3 -> {
                    val val1 = 5//investmentAmount.toFloat()
                    val val2 = fundReturnsFormate//calculateReturns(investmentAmount, durations, fundReturns).toFloat()
                    yVals1.add(BarEntry(3f, floatArrayOf(val1.toFloat(), val2.toFloat())))
                }
            }
        }

        val set1: BarDataSet

        if (mBarChart.data != null && mBarChart.data.dataSetCount > 0) {
            set1 = mBarChart.data.getDataSetByIndex(0) as BarDataSet
            set1.values = yVals1
            mBarChart.data.notifyDataChanged()
            mBarChart.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(yVals1, "")
            set1.setDrawIcons(false)
            set1.colors = arrayListOf(App.INSTANCE.color(R.color.colorAccent), App.INSTANCE.color(R.color.bg_img_color))
            //set1.stackLabels = arrayOf("Savings Account", "Fixed Deposit", "Tarrakki Zyaada")

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)

            val data = BarData(dataSets)
            data.setDrawValues(true)
            data.barWidth = 0.5f
            var index = 1
            data.setValueFormatter { value, entry, dataSetIndex, viewPortHandler ->
                if (index % 2 == 0) {
                    index++
                    when (entry.x.toInt()) {
                        1 -> {
                            "${calculateReturns(investmebtAmount, durations, value.toDouble()).toCurrency()}\n(${savingRate.toReturnAsPercentage()})"
                        }
                        2 -> {
                            "${calculateReturns(investmebtAmount, durations, value.toDouble()).toCurrency()}\n(${fixedDepositRate.toReturnAsPercentage()})"
                        }
                        else -> {
                            "${calculateReturns(investmebtAmount, durations, value.toDouble()).toCurrency()}\n(${this.fundReturnsFormate.toReturnAsPercentage()})"
                        }
                    }
                } else {
                    index++
                    ""
                }
            }
            data.setValueTextSize(8f)
            data.setValueTextColor(App.INSTANCE.color(R.color.semi_black))
            mBarChart.data = data
        }
        mBarChart.animateY(500)
        mBarChart.setFitBars(true)
        mBarChart.isClickable = false
        mBarChart.invalidate()
    }

    private fun resetChart() {
        mBarChart.fitScreen()
        mBarChart.data?.clearValues()
        mBarChart.xAxis.valueFormatter = null
        mBarChart.notifyDataSetChanged()
        mBarChart.clear()
        mBarChart.invalidate()
    }

    // Calculating the compount interest using formula A = P  (1 + r/n)^nt
    private fun calculateReturns(amount: Double, durations: Double, rateOfInterest: Double = 5.0): Double {
        val floatRateOfInterest = rateOfInterest / 100
        val floatFirstValueOfPower = (1 + floatRateOfInterest)
        return amount * Math.pow(floatFirstValueOfPower, durations)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle
         * @return A new instance of fragment TarrakkiZyaadaFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = TarrakkiZyaadaFragment().apply { arguments = basket }
    }
}

package com.tarrakki.module.funddetails.fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.tarrakki.R
import com.tarrakki.addToCart
import com.tarrakki.chartformaters.MyMarkerView
import com.tarrakki.chartformaters.MyYAxisValueFormatter
import com.tarrakki.databinding.FragmentPerformanceBinding
import com.tarrakki.databinding.RowDurationListItemBinding
import com.tarrakki.databinding.RowEarningBaseReturnsListItemBinding
import com.tarrakki.databinding.RowFundKeyInfoListItemBinding
import com.tarrakki.investDialog
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.funddetails.FundDetailsVM
import com.tarrakki.module.funddetails.KeyInfo
import com.tarrakki.module.funddetails.TopHolding
import com.tarrakki.module.invest.FundType
import kotlinx.android.synthetic.main.fragment_performance.*
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.inputclasses.InputFilterMinMax
import org.supportcompact.ktx.format
import org.supportcompact.ktx.getColor
import org.supportcompact.ktx.parseAsReturnOrNA
import org.supportcompact.ktx.startFragment
import java.util.*
import java.util.concurrent.ThreadLocalRandom


/**
 * A simple [Fragment] subclass.
 * Use the [PerformanceFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class PerformanceFragment : Fragment() {

    var fundVM: FundDetailsVM? = null
    var binder: FragmentPerformanceBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (binder == null) {
            binder = DataBindingUtil.inflate(inflater, R.layout.fragment_performance, container, false)
        }
        return binder?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragment?.let {
            fundVM = ViewModelProviders.of(it).get(FundDetailsVM::class.java)
        }
        rvReturns?.isFocusable = false
        rvReturns?.isNestedScrollingEnabled = false

        rvEarned?.isFocusable = false
        rvEarned?.isNestedScrollingEnabled = false
        var amount = 100000
        var durations = 1.0
        fundVM?.let { itVM ->
            itVM.fundDetailsResponse.observe(this, Observer { fundDetailsResponse ->
                fundDetailsResponse?.let { fund ->
                    binder?.fund = fund.fundsDetails
                    binder?.executePendingBindings()

                    val returns = arrayListOf<KeyInfo>()
                    returns.add(KeyInfo("1 Month", parseAsReturnOrNA(fund.fundsDetails?.ttrReturn1Mth)))
                    returns.add(KeyInfo("3 Month", parseAsReturnOrNA(fund.fundsDetails?.ttrReturn3Mth)))
                    returns.add(KeyInfo("6 Month", parseAsReturnOrNA(fund.fundsDetails?.ttrReturn6Mth)))
                    returns.add(KeyInfo("1 Years", parseAsReturnOrNA(fund.fundsDetails?.ttrReturn1Yr)))
                    returns.add(KeyInfo("3 Years", parseAsReturnOrNA(fund.fundsDetails?.ttrReturn3Yr)))
                    returns.add(KeyInfo("5 Years", parseAsReturnOrNA(fund.fundsDetails?.ttrReturn5Yr)))
                    returns.add(KeyInfo("Since Inception", parseAsReturnOrNA(fund.fundsDetails?.ttrReturnSinceInception)))
                    rvReturns?.setUpRecyclerView(R.layout.row_fund_key_info_list_item, returns) { item: KeyInfo, binder: RowFundKeyInfoListItemBinding, position ->
                        binder.keyInfo = item
                        binder.executePendingBindings()
                    }
                    //sdjksd
                    itVM.earningBase.clear()
                    itVM.earningBase.add(TopHolding("Tarrakki Direct Plan", 100, fund.tarrakkiReturn))
                    //earningBase.add(TopHolding("Regular Plan", 65, 8.5, 109300.00))
                    itVM.earningBase.add(TopHolding("Fixed Deposit", 45, fund.fixedDepositReturn?.toDoubleOrNull()
                            ?: 0.0))
                    itVM.earningBase.add(TopHolding("Bank Savings Account", 40, fund.bankSavingsReturn?.toDoubleOrNull()
                            ?: 0.0))
                    itVM.earningBase.forEach { item ->
                        item.amount = calculateReturns(amount, if (spnDuration?.selectedItemPosition == 0) {
                            durations * 12
                        } else {
                            durations
                        }, item.percentageHolding)
                        if (itVM.earningBase.indexOf(item) == 0) {
                            item.process = 100
                        } else {
                            item.process = (item.amount * 100 / itVM.earningBase[0].amount).toInt()
                        }
                    }
                    rvEarned?.setUpRecyclerView(R.layout.row_earning_base_returns_list_item, itVM.earningBase) { item: TopHolding, binder: RowEarningBaseReturnsListItemBinding, position ->
                        binder.topFund = item
                        binder.executePendingBindings()
                    }

                    edtInvestAmount?.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(p: Editable?) {
                            if (p != null && p.isNotEmpty()) {
                                try {
                                    amount = p.toString().replace(",", "").toInt()
                                    itVM.earningBase.forEach { item ->
                                        item.amount = calculateReturns(amount, if (spnDuration?.selectedItemPosition == 0) {
                                            durations * 12
                                        } else {
                                            durations
                                        }, item.percentageHolding)
                                        if (itVM.earningBase.indexOf(item) == 0) {
                                            item.process = 100
                                        } else {
                                            item.process = (item.amount * 100 / itVM.earningBase[0].amount).toInt()
                                        }
                                    }
                                    rvEarned?.adapter?.notifyDataSetChanged()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                itVM.earningBase.forEach { item ->
                                    item.amount = 00.00
                                    item.process = 0
                                }
                                rvEarned?.adapter?.notifyDataSetChanged()
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
                                    val cleanString = s.toString().replace(",", "")
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
                    edtYears?.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(p: Editable?) {
                            if (p != null && p.isNotEmpty()) {
                                durations = p.toString().toDouble()
                                itVM.earningBase.forEach { item ->
                                    if (itVM.earningBase.indexOf(item) == 0) {
                                        item.percentageHolding = fund.getReturn(durations.toInt(), spnDuration?.selectedItemPosition
                                                ?: 0)
                                    }
                                    item.amount = calculateReturns(amount, if (spnDuration?.selectedItemPosition == 0) {
                                        durations * 12
                                    } else {
                                        durations
                                    }, item.percentageHolding)
                                    if (itVM.earningBase.indexOf(item) == 0) {
                                        if (item.percentageHolding <= 0) {
                                            item.process = 0
                                            item.amount = 0.0
                                        } else {
                                            item.process = 100
                                        }
                                    } else {
                                        item.process = (item.amount * 100 / itVM.earningBase[0].amount).toInt()
                                    }
                                }
                                rvEarned?.adapter?.notifyDataSetChanged()
                            } else {
                                itVM.earningBase.forEach { item ->
                                    item.amount = 00.00
                                    item.process = 0
                                }
                                rvEarned?.adapter?.notifyDataSetChanged()
                            }
                        }

                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                        }
                    })
                    spnDuration?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }

                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            if (spnDuration?.selectedItemPosition == 0) {
                                edtYears?.setText("1")
                                edtYears?.filters = arrayOf(InputFilterMinMax(1, 99))
                            } else {
                                edtYears?.setText("12")
                                edtYears?.filters = arrayOf(InputFilterMinMax(1, 1188))
                            }
                            itVM.earningBase.forEach { item ->
                                if (itVM.earningBase.indexOf(item) == 0) {
                                    item.percentageHolding = fund.getReturn(durations.toInt(), spnDuration?.selectedItemPosition
                                            ?: 0)
                                }
                                item.amount = calculateReturns(amount, if (spnDuration?.selectedItemPosition == 0) {
                                    durations * 12
                                } else {
                                    durations
                                }, item.percentageHolding)
                                if (itVM.earningBase.indexOf(item) == 0) {
                                    if (item.percentageHolding <= 0) {
                                        item.process = 0
                                        item.amount = 0.0
                                    } else {
                                        item.process = 100
                                    }
                                } else {
                                    item.process = (item.amount * 100 / itVM.earningBase[0].amount).toInt()
                                }
                            }
                            rvEarned?.adapter?.notifyDataSetChanged()
                        }
                    }

                }
            })

            var selectedAt = 3
            rvDurations?.setUpRecyclerView(R.layout.row_duration_list_item, itVM.durations) { item: FundType, binder: RowDurationListItemBinding, position ->
                binder.fundType = item
                binder.executePendingBindings()
                binder.tvFundType.setOnClickListener {
                    if (selectedAt != -1) {
                        itVM.durations[selectedAt].isSelected = false
                    }
                    item.isSelected = !item.isSelected
                    selectedAt = position
                    // add data
                    setUpChart(15)
                    mChart.invalidate()
                }
            }

            btn_invest_now?.setOnClickListener {
                val fund_id = itVM.fundDetailsResponse.value?.fundsDetails?.id
                val minSIPAmount = itVM.fundDetailsResponse.value?.fundsDetails?.validminSIPAmount
                val minLumpSumAmount = itVM.fundDetailsResponse.value?.fundsDetails?.validminlumpsumAmount

                if (fund_id!=null && minSIPAmount!=null && minLumpSumAmount!=null){
                    context?.investDialog(fund_id,minSIPAmount, minLumpSumAmount) {
                        amountLumpsum, amountSIP, fundId ->
                        addToCart(fundId,amountSIP,amountLumpsum).observe(this,
                                android.arch.lifecycle.Observer {
                                    response -> startFragment(CartFragment.newInstance(), R.id.frmContainer)
                                })
                    }
                }
            }
        }
        val adapter = ArrayAdapter.createFromResource(
                activity,
                R.array.durations,
                R.layout.simple_spinner_item_gray
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spnDuration.adapter = adapter
        setUpChart()
        //edtInvestAmount?.filters = arrayOf(InputFilterMinMax(1))
        edtYears?.filters = arrayOf(InputFilterMinMax(1))
    }

    // Calculating the compount interest using formula A = P  (1 + r/n)^nt
    fun calculateReturns(amount: Int, durations: Double, rateOfInterest: Double = 5.0): Double {
        val floatRateOfInterest = rateOfInterest / 100
        val floatFirstValueOfPower = (1 + floatRateOfInterest / 12)
        val tn = durations / 12 * 12
        return amount * Math.pow(floatFirstValueOfPower, tn)
    }

    private fun setUpChart() {
        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        val mv = MyMarkerView(context, R.layout.custom_marker_view)
        mv.chartView = mChart // For bounds control
        mChart.marker = mv // Set the marker to the chart
        mChart.setBackgroundColor(Color.WHITE)
        mChart.setDrawBorders(false)

        // no description text
        mChart.description.isEnabled = false


        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false)
        mChart.setScaleEnabled(false)

        val l = mChart.legend
        l.isEnabled = false

        val typeface = context?.let { ResourcesCompat.getFont(it, R.font.lato_regular) }


        val xAxis = mChart.xAxis
        xAxis.isEnabled = true
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.labelCount = 5
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IAxisValueFormatter { value, axis ->
            val index = value.toInt()
            when (index) {
                0 -> "Apr 18"
                3 -> "May 18"
                6 -> "Jun 18"
                9 -> "Jul 18"
                12 -> "Aug 18"
                else -> ""
            }
        }
        getColor(R.color.semi_black)?.let {
            xAxis.textColor = it
        }
        xAxis.typeface = typeface

        val leftAxis = mChart.axisRight
        leftAxis.labelCount = 4
        leftAxis.axisMaximum = 40f
        leftAxis.axisMinimum = 10f
        leftAxis.setDrawAxisLine(true)
        leftAxis.setDrawZeroLine(true)
        leftAxis.setDrawGridLines(true)
        leftAxis.valueFormatter = MyYAxisValueFormatter()
        getColor(R.color.semi_black)?.let {
            leftAxis.textColor = it
        }
        leftAxis.typeface = typeface
        mChart.axisLeft.isEnabled = false

        // add data
        setUpChart(15)

        mChart.invalidate()
    }

    private fun setUpChart(count: Int) {

        val yVals1 = ArrayList<Entry>()
        val x = 0.0
        for (i in 0 until count) {
            val valY = ThreadLocalRandom.current().nextDouble(10.00, 40.00).toFloat()
            //val `val` = (Math.random() * range).toFloat() + 40// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals1.add(Entry(i.toFloat(), valY))
        }

        val set1: LineDataSet

        if (mChart.data != null && mChart.data.dataSetCount > 0) {
            set1 = mChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = yVals1
            mChart.data.notifyDataChanged()
            mChart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(yVals1, "DataSet 1")

            set1.axisDependency = YAxis.AxisDependency.LEFT
            set1.color = Color.parseColor("#4EB95D")
            set1.setDrawCircles(false)
            set1.lineWidth = 1f
            set1.circleRadius = 3f
            set1.fillAlpha = 255
            set1.setDrawFilled(true)
            val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.shape_line_chart_bg) }
            set1.fillDrawable = drawable
            //set1.fillColor = Color.WHITE
            getColor(R.color.darker_gray)?.let {
                set1.highLightColor = it
            }
            set1.setDrawCircleHole(false)
            set1.fillFormatter = IFillFormatter { dataSet, dataProvider -> mChart.axisLeft.axisMinimum }

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the datasets

            // create a data object with the datasets
            val data = LineData(dataSets)
            data.setDrawValues(false)

            // set data
            mChart.data = data
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket param as Bundle.
         * @return A new instance of fragment PerformanceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = PerformanceFragment().apply { arguments = basket }
    }
}

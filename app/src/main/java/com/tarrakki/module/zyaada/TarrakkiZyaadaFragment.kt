package com.tarrakki.module.zyaada

import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.TableLayout
import com.tarrakki.*
import com.tarrakki.databinding.FragmentTarrakkiZyaadaBinding
import com.tarrakki.databinding.PageTarrakkiZyaadaItemBinding
import com.tarrakki.databinding.RowFundKeyInfoListItemBinding
import com.tarrakki.module.funddetails.KeyInfo
import kotlinx.android.synthetic.main.fragment_tarrakki_zyaada.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setAutoWrapContentPageAdapter
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.color
import org.supportcompact.ktx.parseAsNoZiroReturnOrNA
import org.supportcompact.ktx.toCurrency

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
        val returns = arrayListOf<KeyInfo>()
        returns.add(KeyInfo("1 Year", parseAsNoZiroReturnOrNA("5.6")))
        returns.add(KeyInfo("3 Years", parseAsNoZiroReturnOrNA("6.4")))
        returns.add(KeyInfo("5 Years", parseAsNoZiroReturnOrNA("12.8")))
        returns.add(KeyInfo("10 Years", parseAsNoZiroReturnOrNA("25.2")))
        rvReturns?.setUpRecyclerView(R.layout.row_fund_key_info_list_item, returns) { item: KeyInfo, binder: RowFundKeyInfoListItemBinding, position ->
            binder.keyInfo = item
            binder.executePendingBindings()
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

        /**Header View**/
        val tableRowHeader = context?.tableRow()
        tableRowHeader?.setBackgroundResource(R.color.bg_img_color)
        tableRowHeader?.addView(context?.tableRowContent("", context?.color(R.color.black)))
        tableRowHeader?.addView(context?.tableRowContent("Tarrakki\nZyaada", context?.color(R.color.black)))
        tableRowHeader?.addView(context?.tableRowContent("Savings\nAccount", context?.color(R.color.black)))
        tableRowHeader?.addView(context?.tableRowContent("Fixed\nDeposit", context?.color(R.color.black)))
        tblSchemeDetails?.addView(tableRowHeader, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))
        val names = arrayListOf("Liquidity", "Debit Card", "ATM Withdrawal", "Instant Withdrawal")
        var start = ""
        val drawableGreen = App.INSTANCE.getDrawable(R.drawable.iv_right_green)
        val drawableRed = App.INSTANCE.getDrawable(R.drawable.iv_cross_red)
        /**Body View**/
        for (name in names) {
            val tableRow = context?.tableRow()
            tableRow?.addView(context?.tableRowContentWithDrawable(name))
            tableRow?.addView(context?.tableRowContentWithDrawable(start, drawableGreen))
            tableRow?.addView(context?.tableRowContentWithDrawable(drawable = drawableGreen))
            tableRow?.addView(context?.tableRowContentWithDrawable(drawable = drawableRed))
            tblSchemeDetails?.addView(tableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))
            start += "*"
        }
        /**Footer View**/
        val tableRow = context?.tableRow()
        tableRow?.addView(context?.tableRowContent("Minimum Investment"))
        tableRow?.addView(context?.tableRowContent(500.toCurrency()))
        tableRow?.addView(context?.tableRowContent("~${10000.toCurrency()}"))
        tableRow?.addView(context?.tableRowContent("~${1000.toCurrency()}\nto ${10000.toCurrency()}"))
        tblSchemeDetails?.addView(tableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))

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

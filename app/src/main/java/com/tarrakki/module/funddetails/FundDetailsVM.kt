package com.tarrakki.module.funddetails

import android.support.annotation.IntRange
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.module.invest.Fund
import org.supportcompact.FragmentViewModel

class FundDetailsVM : FragmentViewModel() {

    var fund: Fund? = null
    val keysInfo = arrayListOf<KeyInfo>()
    val returns = arrayListOf<KeyInfo>()
    val topsHolding = arrayListOf<TopHolding>()

    init {

        keysInfo.add(KeyInfo("AMC Name", "Axis Mutual Fund"))
        keysInfo.add(KeyInfo("AMC Type", "Open-Ended"))
        keysInfo.add(KeyInfo("Investment Plan", "Growth"))
        keysInfo.add(KeyInfo("Launch Date", "Feb 01, 2013"))
        keysInfo.add(KeyInfo("Benchmark", "S&P BSE MidCap"))
        keysInfo.add(KeyInfo("Assets Size", App.INSTANCE.getString(R.string.rs_symbol).plus("80.09 cr(31 Mar, 2018)")))
        keysInfo.add(KeyInfo("Asset Date", "Mar 31, 2018"))
        keysInfo.add(KeyInfo("Minimum Investment SIP", App.INSTANCE.getString(R.string.rs_symbol).plus("2000")))
        keysInfo.add(KeyInfo("Minimum Investment Lump sum", App.INSTANCE.getString(R.string.rs_symbol).plus("5000")))
        keysInfo.add(KeyInfo("Fund Manger", "Shreyash Develkar"))
        keysInfo.add(KeyInfo("Exit Load", "1.0%"))
        keysInfo.add(KeyInfo("Volatility (VOL)", "12.05%"))

        topsHolding.add(TopHolding("Gruh Finance Ltd.", 100, "6.65%"))
        topsHolding.add(TopHolding("Bajaj Finance Ltd.", 85, "5.92%"))
        topsHolding.add(TopHolding("Page Industries Ltd.", 82, "5.82%"))
        topsHolding.add(TopHolding("City Union Bank Ltd.", 78, "5.07%"))
        topsHolding.add(TopHolding("Supreme Industries Ltd.", 78, "4.02%"))
        topsHolding.add(TopHolding("Sundaram Finance Limited.", 73, "3.86%"))
        topsHolding.add(TopHolding("Endurance Technologies Ltd.", 65, "3.72%"))
        topsHolding.add(TopHolding("Astral Poly Technik Ltd.", 60, "3.35%"))
        topsHolding.add(TopHolding("V-Guard Industries Ltd.", 55, "3.33%"))
        topsHolding.add(TopHolding("Avenue Supermarts Ltd.", 50, "2.95%"))

        returns.add(KeyInfo("1 Month", "5.6"))
        returns.add(KeyInfo("3 Month", "6.4"))
        returns.add(KeyInfo("6 Month", "12.8"))
        returns.add(KeyInfo("1 Years", "25.2"))
        returns.add(KeyInfo("3 Years", "11.5"))
        returns.add(KeyInfo("5 Years", "27.7"))
        returns.add(KeyInfo("Since Inception", "20.6"))

    }
}

data class KeyInfo(var key: String, var value: String)

data class TopHolding(var name: String, @IntRange(from = 0, to = 100) var process: Int, var percentageHolding: String)
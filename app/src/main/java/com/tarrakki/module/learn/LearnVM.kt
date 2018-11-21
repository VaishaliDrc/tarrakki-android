package com.tarrakki.module.learn

import android.support.annotation.DrawableRes
import android.view.View
import com.tarrakki.R
import org.supportcompact.FragmentViewModel
import java.io.Serializable

class LearnVM : FragmentViewModel() {

    val articles = arrayListOf<Article>()

    init {
        articles.add(Article(
                "Are FII Is buying the same stocks as MFs?",
                "Equity markets have been volatile so far this year with Nifty 50 falling abou",
                "1 August, 2018",
                46,
                R.drawable.temp_fpi))
        /* articles.add(Article(
                 "https://www.youtube.com/watch?v=IpOY-at6Dfik",
                 "",
                 "3 Sep, 2018",
                 46,
                 R.drawable.temp_video,
                 true))*/
        articles.add(Article(
                "Fundslndia Explains: Capture Ratio",
                "The market has done extremely well, has my fund kept up with it?",
                "3 Sep, 2018",
                46,
                R.drawable.temp_top_bottom_image))
        articles.add(Article(
                "When your SIP returns turn negative",
                "If you had started investments in equity SIPs in the past 3 months or 6 month",
                "3 Sep, 2018",
                46,
                R.drawable.temp_sip))
    }

}

data class Article(
        var title: String,
        var description: String,
        var data: String,
        var comments: Int,
        @DrawableRes
        var imgUrl: Int,
        var isVideo: Boolean = false) : Serializable {

    val tempTxt = "Are you looking for Best Performing Tax Saving Mutual Funds for Investment? As the financial year, 2018-19 is about to end, you must have started tax saving and tax planning exercise of 2019. ELSS – Tax saving mutual funds play a vital role during tax planning exercise. ELSS (Equity Linked Saving Scheme) is one of the best ways to save tax and generate wealth.\nELSS offers tax benefits under section 80 C of the Income Tax Act. ELSS comes with a lock-in period of 3 years. However, ELSS offers multiple benefits to the investor.\n\n<p><strong>Key Features of ELSS funds</strong></p><ul><li>ELSS is equity linked saving scheme. ELSS is a type of mutual fund that invests the major corpus in the equity market.</li><li>ELSS comes with both dividend and growth options.</li><li>ELSS funds come with Lock-in period of 3 years from the date of purchase.</li><li>Long-term capital gain tax is applicable to ELSS.</li><li>Investment in ELSS can be started with a minimum amount of Rs.500.</li></ul>\n\n<p><strong>Benefits of Investing in ELSS Mutual Funds</strong></p><ul><li>ELSS is dual purpose investment. You can save tax as well as generate wealth by making the investment in ELSS.</li><li>An investor can claim a tax deduction up to 1.5 Lakh by making the investment in ELSS.</li><li>The lock-in period of ELSS is lowest compared to all other tax saving options such as tax saving FD or PPF.</li><li>ELSS provides higher returns compared to other tax saving instruments.</li><li>ELSS fund offers SIP investment option, which brings discipline in regular investing.</li><li>ELSS has no fixed maturity date or period. You can continue to hold ELSS as long as you can.</li><li>ELSS is the best tax saving instrument in terms of expected return, lock-in period and periodicity of investment.</li></ul>"
    fun videoVisibility() = if (isVideo) View.VISIBLE else View.GONE

}
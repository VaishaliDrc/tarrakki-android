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


    fun videoVisibility() = if (isVideo) View.VISIBLE else View.GONE

}
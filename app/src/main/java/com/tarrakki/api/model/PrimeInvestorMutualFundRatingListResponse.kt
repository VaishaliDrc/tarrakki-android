package com.tarrakki.api.model
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.google.gson.annotations.SerializedName
import com.tarrakki.R
import org.supportcompact.CoreApp


data class PrimeInvestorMutualFundRatingListResponse(
    @SerializedName("data")
    val `data`: ArrayList<RatingList?>
)

data class RatingList(
    @SerializedName("category")
    val category: String?,
    @SerializedName("fund_name")
    val fundName: String?,
    @SerializedName("prime_rating")
    val primeRating: String?,
    @SerializedName("prime_review")
    val primeReview: String?
)
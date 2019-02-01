package com.tarrakki

import android.support.annotation.DrawableRes
import android.view.View

fun getRiskLevelVisibility(riskType: String?) : Int{
    return when {
        riskType.equals("HIGH RISK", false) -> View.VISIBLE
        riskType.equals("MEDIUM RISK", false) -> View.VISIBLE
        riskType.equals("LOW RISK", false) -> View.VISIBLE
        else -> View.GONE
    }
}

fun getReturnLevelVisibility(returnType: String?) : Int{
    return when {
        returnType.equals("HIGH RETURN", false) -> View.VISIBLE
        returnType.equals("MEDIUM RETURN", false) -> View.VISIBLE
        returnType.equals("LOW RETURN", false) -> View.VISIBLE
        else -> View.GONE
    }
}

fun getRiskLevelDrawable(riskType: String?) : Int{
    return when {
        riskType.equals("HIGH RISK", false) -> R.drawable.ic_red_up
        riskType.equals("MEDIUM RISK", false) -> R.drawable.ic_red_equal
        riskType.equals("LOW RISK", false) -> R.drawable.ic_red_down
        else -> R.drawable.ic_red_up
    }
}

fun getReturnLevelDrawable(returnType: String?) : Int{
    return when {
        returnType.equals("HIGH RETURN", false) -> R.drawable.ic_green_up
        returnType.equals("MEDIUM RETURN", false) -> R.drawable.ic_green_equal
        returnType.equals("LOW RETURN", false) -> R.drawable.ic_green_down
        else -> R.drawable.ic_green_up
    }
}

fun getRiskLevel(riskType: String?) : String{
    return when {
        riskType.equals("HIGH RISK", false) -> "High Risk"
        riskType.equals("MEDIUM RISK", false) -> "Medium Risk"
        riskType.equals("LOW RISK", false) -> "Low Risk"
        else -> ""
    }
}

fun getReturnLevel(returnType: String?) : String{
    return when {
        returnType.equals("HIGH RETURN", false) -> "High Return"
        returnType.equals("MEDIUM RETURN", false) -> "Medium Return"
        returnType.equals("LOW RETURN", false) -> "Low Return"
        else -> ""
    }
}
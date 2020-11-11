package com.tarrakki.api.model

data class GetLiquiLoanPortFolioBaseResponse(
    val `data`: List<GetLiquiLoanPortFolioData>
)

data class GetLiquiLoanPortFolioData(
        val created: String,
        val current_value: Double,
        val id: Int,
        val investor_id: String,
        val liquid_amount: Double,
        val lock_in_amount: Double,
        val modified: String,
        val total_investment: Double,
        val user: Int,
        var hideDetails: Boolean =false,
        val xirr_return: Double
)
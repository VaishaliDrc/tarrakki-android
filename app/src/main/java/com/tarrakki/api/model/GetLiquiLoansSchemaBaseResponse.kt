package com.tarrakki.api.model

data class GetLiquiLoansSchemaBaseResponse(
    val `data`: GetLiquiLoansSchemaData
)

data class GetLiquiLoansSchemaData(
        val data_points: List<DataPoint>,
        val schemes: List<Scheme>
)

data class DataPoint(
        val borrowers: String,
        val disbursement_month: String,
        val disbursements: String,
        val gross_npa: String,
        val is_show: Boolean,
        val lenders: String,
        val total_disbursements: String
)

data class Scheme(
        val id: Int,
        val lock_in_period: String,
        val min_investment_amount: String,
        val returns: String,
        val scheme_title: String,
        val scheme_type: String
)
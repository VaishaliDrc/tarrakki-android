package com.tarrakki.api.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

data class VerifyPANApiResponse(
        @SerializedName("data") val `data`: Data?
) {
    @Keep
    data class Data(
            @SerializedName("APP_ENTRYDT") val aPPENTRYDT: String?, // 25-09-2018 18:10:55
            @SerializedName("APP_HOLD_DEACTIVE_RMKS") val aPPHOLDDEACTIVERMKS: String?,
            @SerializedName("APP_IPV_FLAG") val aPPIPVFLAG: String?, // Y
            @SerializedName("APP_KYC_MODE") val aPPKYCMODE: String?, // 0
            @SerializedName("APP_MODDT") val aPPMODDT: String?,
            @SerializedName("APP_NAME") val aPPNAME: String?, // JAYESH SHANTILAL PRAKARIYA
            @SerializedName("APP_PAN_NO") val aPPPANNO: String?, // DGUPP2792B
            @SerializedName("APP_SIGN_FLAG") val aPPSIGNFLAG: String?,
            @SerializedName("APP_STATUSDT") val aPPSTATUSDT: String?, // 04-10-2018 14:54:10
            @SerializedName("APP_UPDT_STATUS") val aPPUPDTSTATUS: String?, // 05
            @SerializedName("CAMSKRA") val cAMSKRA: String?, // 05
            @SerializedName("CVLKRA") val cVLKRA: String?, // 02
            @SerializedName("DOTEXKRA") val dOTEXKRA: String?, // 05
            @SerializedName("KARVYKRA") val kARVYKRA: String?, // 05
            @SerializedName("NDMLKRA") val nDMLKRA: String? // 05
    )
}
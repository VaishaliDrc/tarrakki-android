package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName


data class PANDetails(
        @SerializedName("data")
        val `data`: Data?
) {
    data class Data(
            @SerializedName("flag")
            val flag: Boolean?, // true
            @SerializedName("pan_data")
            val panData: PanData?
    ) {
        data class PanData(
                @SerializedName("APP_BRANCH_CODE")
                val aPPBRANCHCODE: String?, // NA
                @SerializedName("APP_COMMENCE_DT")
                val aPPCOMMENCEDT: String?, // NA
                @SerializedName("APP_COMP_STATUS")
                val aPPCOMPSTATUS: String?, // NA
                @SerializedName("APP_COR_ADD1")
                val aPPCORADD1: String?, // H NO-228A WARD NO-8
                @SerializedName("APP_COR_ADD2")
                val aPPCORADD2: String?, // RATIA TEH-RATIA
                @SerializedName("APP_COR_ADD3")
                val aPPCORADD3: String?, // DIST-FATEHBAD
                @SerializedName("APP_COR_ADD_DT")
                val aPPCORADDDT: String?, // 00-00-0000
                @SerializedName("APP_COR_ADD_PROOF")
                val aPPCORADDPROOF: String?,
                @SerializedName("APP_COR_ADD_REF")
                val aPPCORADDREF: String?, // NA
                @SerializedName("APP_COR_CITY")
                val aPPCORCITY: String?, // FATEHBAD
                @SerializedName("APP_COR_CTRY")
                val aPPCORCTRY: String?,
                @SerializedName("APP_COR_PINCD")
                val aPPCORPINCD: String?, // 125051
                @SerializedName("APP_COR_STATE")
                val aPPCORSTATE: String?,
                @SerializedName("APP_DATE")
                val aPPDATE: String?, // 29-08-2012
                @SerializedName("APP_DNLDDT")
                val aPPDNLDDT: String?, // 07-07-2020
                @SerializedName("APP_DOB_DT")
                val aPPDOBDT: String?,
                @SerializedName("APP_DOC_PROOF")
                val aPPDOCPROOF: String?, // T
                @SerializedName("APP_DOI_DT")
                val aPPDOIDT: String?, // NA
                @SerializedName("APP_DUMP_TYPE")
                val aPPDUMPTYPE: String?, // S
                @SerializedName("APP_EMAIL")
                val aPPEMAIL: String?, // harsh_lucky89@yahoo.com
                @SerializedName("APP_ERROR_DESC")
                val aPPERRORDESC: String?,
                @SerializedName("APP_EXMT")
                val aPPEXMT: String?, // N
                @SerializedName("APP_EXMT_CAT")
                val aPPEXMTCAT: String?,
                @SerializedName("APP_EXMT_ID_PROOF")
                val aPPEXMTIDPROOF: String?,
                @SerializedName("APP_FAX_NO")
                val aPPFAXNO: String?,
                @SerializedName("APP_FILLER1")
                val aPPFILLER1: String?,
                @SerializedName("APP_FILLER2")
                val aPPFILLER2: String?, // EXISTING CLIENT
                @SerializedName("APP_FILLER3")
                val aPPFILLER3: String?, // NA
                @SerializedName("APP_F_NAME")
                val aPPFNAME: String?, // KULDEEP KUMAR
                @SerializedName("APP_GEN")
                val aPPGEN: String?,
                @SerializedName("APP_INCOME")
                val aPPINCOME: String?,
                @SerializedName("APP_INCORP_PLC")
                val aPPINCORPPLC: String?, // NA
                @SerializedName("APP_INT_CODE")
                val aPPINTCODE: String?,
                @SerializedName("APP_INTERNAL_REF")
                val aPPINTERNALREF: String?,
                @SerializedName("APP_IOP_FLG")
                val aPPIOPFLG: String?, // RS
                @SerializedName("APP_IPV_DATE")
                val aPPIPVDATE: String?,
                @SerializedName("APP_IPV_FLAG")
                val aPPIPVFLAG: String?, // N
                @SerializedName("APP_KRA_INFO")
                val aPPKRAINFO: String?, // NDMLKRA
                @SerializedName("APP_KYC_MODE")
                val aPPKYCMODE: String?, // 0
                @SerializedName("APP_MAR_STATUS")
                val aPPMARSTATUS: String?,
                @SerializedName("APP_MOB_NO")
                val aPPMOBNO: String?, // 009357845378
                @SerializedName("APP_NAME")
                val aPPNAME: String?, // HARSH BANSAL
                @SerializedName("APP_NATIONALITY")
                val aPPNATIONALITY: String?,
                @SerializedName("APP_NETWORTH_DT")
                val aPPNETWORTHDT: String?,
                @SerializedName("APP_NETWRTH")
                val aPPNETWRTH: String?,
                @SerializedName("APP_NO")
                val aPPNO: String?, // 1239905756
                @SerializedName("APP_OCC")
                val aPPOCC: String?, // 08
                @SerializedName("APP_OFF_NO")
                val aPPOFFNO: String?,
                @SerializedName("APP_OTH_COMP_STATUS")
                val aPPOTHCOMPSTATUS: String?, // NA
                @SerializedName("APP_OTHERINFO")
                val aPPOTHERINFO: String?,
                @SerializedName("APP_OTH_NATIONALITY")
                val aPPOTHNATIONALITY: String?,
                @SerializedName("APP_OTH_OCC")
                val aPPOTHOCC: String?,
                @SerializedName("APP_PAN_COPY")
                val aPPPANCOPY: String?, // Y
                @SerializedName("APP_PANEX_NO")
                val aPPPANEXNO: String?, // NA
                @SerializedName("APP_PAN_NO")
                val aPPPANNO: String?, // AVEPB4522F
                @SerializedName("APP_PER_ADD1")
                val aPPPERADD1: String?,
                @SerializedName("APP_PER_ADD2")
                val aPPPERADD2: String?,
                @SerializedName("APP_PER_ADD3")
                val aPPPERADD3: String?,
                @SerializedName("APP_PER_ADD_DT")
                val aPPPERADDDT: String?, // 00-00-0000
                @SerializedName("APP_PER_ADD_PROOF")
                val aPPPERADDPROOF: String?,
                @SerializedName("APP_PER_ADD_REF")
                val aPPPERADDREF: String?, // NA
                @SerializedName("APP_PER_CITY")
                val aPPPERCITY: String?,
                @SerializedName("APP_PER_CTRY")
                val aPPPERCTRY: String?,
                @SerializedName("APP_PER_PINCD")
                val aPPPERPINCD: String?,
                @SerializedName("APP_PER_STATE")
                val aPPPERSTATE: String?,
                @SerializedName("APP_POL_CONN")
                val aPPPOLCONN: String?,
                @SerializedName("APP_POS_CODE")
                val aPPPOSCODE: String?, // PA
                @SerializedName("APP_REGNO")
                val aPPREGNO: String?, // 1239905756
                @SerializedName("APP_REMARKS")
                val aPPREMARKS: String?,
                @SerializedName("APP_RES_NO")
                val aPPRESNO: String?,
                @SerializedName("APP_RES_STATUS")
                val aPPRESSTATUS: String?,
                @SerializedName("APP_RES_STATUS_PROOF")
                val aPPRESSTATUSPROOF: String?, // NA
                @SerializedName("APP_SIGN_FLAG")
                val aPPSIGNFLAG: String?,
                @SerializedName("APP_STATUS")
                val aPPSTATUS: String?, // 12
                @SerializedName("APP_STATUSDT")
                val aPPSTATUSDT: String?, // 21-05-2020
                @SerializedName("APP_TYPE")
                val aPPTYPE: String?, // I
                @SerializedName("APP_UID_NO")
                val aPPUIDNO: String?,
                @SerializedName("APP_UPDTFLG")
                val aPPUPDTFLG: String?
        )
    }
}
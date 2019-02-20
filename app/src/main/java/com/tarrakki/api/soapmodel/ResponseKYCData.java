package com.tarrakki.api.soapmodel;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@Root(name = "soap:Envelope")
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema", prefix = "xsd"),
        @Namespace(prefix = "soap", reference = "http://www.w3.org/2003/05/soap-envelope")
})
public class ResponseKYCData {

    /**
     * Body : {"DownloadPANDetails_eKYCResponse":{"DownloadPANDetails_eKYCResult":{"APP_RES_ROOT":{"APP_PAN_INQ":{"APP_INT_CODE":"CK5001","APP_TYPE":"I","APP_NO":"3313784","APP_DATE":"07-09-2016 12:23:26","APP_PAN_NO":"AQRPM8828K","APP_PANEX_NO":"","APP_PAN_COPY":"Y","APP_EXMT":"N","APP_EXMT_CAT":"","APP_EXMT_ID_PROOF":"01","APP_IPV_FLAG":"Y","APP_IPV_DATE":"01-09-2016 00:00:00","APP_GEN":"M","APP_NAME":"Mohamed Imran","APP_F_NAME":"Shahabudeen","APP_REGNO":"","APP_DOB_DT":"07-04-1980 00:00:00","APP_DOI_DT":"01-01-1800 00:00:00","APP_COMMENCE_DT":"01-01-1800 00:00:00","APP_NATIONALITY":"01","APP_OTH_NATIONALITY":"","APP_COMP_STATUS":"R","APP_OTH_COMP_STATUS":"","APP_RES_STATUS":"R","APP_RES_STATUS_PROOF":"31","APP_UID_NO":"N","APP_COR_ADD1":"Address Line one","APP_COR_ADD2":"Address line two","APP_COR_ADD3":"Address line three","APP_COR_CITY":"Chennai","APP_COR_PINCD":"600082","APP_COR_STATE":"033","APP_COR_CTRY":"101","APP_OFF_NO":"","APP_RES_NO":"","APP_MOB_NO":"9940043940","APP_FAX_NO":"","APP_EMAIL":"ps_charanyan@camsonline.com","APP_COR_ADD_PROOF":"31","APP_COR_ADD_REF":"31","APP_COR_ADD_DT":"07-09-2016 12:23:26","APP_PER_ADD1":"32/8 Ballaro Street","APP_PER_ADD2":"Agaram","APP_PER_ADD3":"Jawahar Nagar","APP_PER_CITY":"Chennai","APP_PER_PINCD":"600082","APP_PER_STATE":"033","APP_PER_CTRY":"101","APP_PER_ADD_PROOF":"31","APP_PER_ADD_REF":"","APP_PER_ADD_DT":"07-09-2016 12:23:26","APP_INCOME":"","APP_OCC":"01","APP_OTH_OCC":"","APP_POL_CONN":"","APP_DOC_PROOF":"S","APP_INTERNAL_REF":"CAMSKRA","APP_BRANCH_CODE":"","APP_MAR_STATUS":"01","APP_NETWRTH":"","APP_NETWORTH_DT":"01-01-1800 00:00:00","APP_INCORP_PLC":"","APP_OTHERINFO":"","APP_FILLER1":"","APP_FILLER2":"","APP_FILLER3":"","APP_STATUS":"02","APP_STATUSDT":"15-09-2016 00:00:00","APP_ERROR_DESC":"","APP_DUMP_TYPE":"S","APP_DNLDDT":"01-01-1800 00:00:00","APP_KRA_INFO":"CAMSKRA","APP_SIGNATURE":"","APP_IOP_FLG":"IE","APP_POS_CODE":"infibeam$10","APP_UPDTFLG":"","APP_KYC_MODE":"","APP_SIGN_FLAG":""},"APP_SUMM_REC":{"APP_OTHKRA_BATCH":"SAU_468","APP_OTHKRA_CODE":"PLUTOWS","APP_REQ_DATE":"14-02-2019 19:05:48","APP_TOTAL_REC":"1","APP_RESPONSE_DATE":"14-02-2019 07:05:49"}}}}}
     */

    @Element(required = false, name ="Body")
    private BodyBean Body;

    public BodyBean getBody() {
        return Body;
    }

    public void setBody(BodyBean Body) {
        this.Body = Body;
    }

    public static class BodyBean {
        /**
         * DownloadPANDetails_eKYCResponse : {"DownloadPANDetails_eKYCResult":{"APP_RES_ROOT":{"APP_PAN_INQ":{"APP_INT_CODE":"CK5001","APP_TYPE":"I","APP_NO":"3313784","APP_DATE":"07-09-2016 12:23:26","APP_PAN_NO":"AQRPM8828K","APP_PANEX_NO":"","APP_PAN_COPY":"Y","APP_EXMT":"N","APP_EXMT_CAT":"","APP_EXMT_ID_PROOF":"01","APP_IPV_FLAG":"Y","APP_IPV_DATE":"01-09-2016 00:00:00","APP_GEN":"M","APP_NAME":"Mohamed Imran","APP_F_NAME":"Shahabudeen","APP_REGNO":"","APP_DOB_DT":"07-04-1980 00:00:00","APP_DOI_DT":"01-01-1800 00:00:00","APP_COMMENCE_DT":"01-01-1800 00:00:00","APP_NATIONALITY":"01","APP_OTH_NATIONALITY":"","APP_COMP_STATUS":"R","APP_OTH_COMP_STATUS":"","APP_RES_STATUS":"R","APP_RES_STATUS_PROOF":"31","APP_UID_NO":"N","APP_COR_ADD1":"Address Line one","APP_COR_ADD2":"Address line two","APP_COR_ADD3":"Address line three","APP_COR_CITY":"Chennai","APP_COR_PINCD":"600082","APP_COR_STATE":"033","APP_COR_CTRY":"101","APP_OFF_NO":"","APP_RES_NO":"","APP_MOB_NO":"9940043940","APP_FAX_NO":"","APP_EMAIL":"ps_charanyan@camsonline.com","APP_COR_ADD_PROOF":"31","APP_COR_ADD_REF":"31","APP_COR_ADD_DT":"07-09-2016 12:23:26","APP_PER_ADD1":"32/8 Ballaro Street","APP_PER_ADD2":"Agaram","APP_PER_ADD3":"Jawahar Nagar","APP_PER_CITY":"Chennai","APP_PER_PINCD":"600082","APP_PER_STATE":"033","APP_PER_CTRY":"101","APP_PER_ADD_PROOF":"31","APP_PER_ADD_REF":"","APP_PER_ADD_DT":"07-09-2016 12:23:26","APP_INCOME":"","APP_OCC":"01","APP_OTH_OCC":"","APP_POL_CONN":"","APP_DOC_PROOF":"S","APP_INTERNAL_REF":"CAMSKRA","APP_BRANCH_CODE":"","APP_MAR_STATUS":"01","APP_NETWRTH":"","APP_NETWORTH_DT":"01-01-1800 00:00:00","APP_INCORP_PLC":"","APP_OTHERINFO":"","APP_FILLER1":"","APP_FILLER2":"","APP_FILLER3":"","APP_STATUS":"02","APP_STATUSDT":"15-09-2016 00:00:00","APP_ERROR_DESC":"","APP_DUMP_TYPE":"S","APP_DNLDDT":"01-01-1800 00:00:00","APP_KRA_INFO":"CAMSKRA","APP_SIGNATURE":"","APP_IOP_FLG":"IE","APP_POS_CODE":"infibeam$10","APP_UPDTFLG":"","APP_KYC_MODE":"","APP_SIGN_FLAG":""},"APP_SUMM_REC":{"APP_OTHKRA_BATCH":"SAU_468","APP_OTHKRA_CODE":"PLUTOWS","APP_REQ_DATE":"14-02-2019 19:05:48","APP_TOTAL_REC":"1","APP_RESPONSE_DATE":"14-02-2019 07:05:49"}}}}
         */
        @Namespace(reference = "https://camskra.com/")
        @Element(required = false, name ="DownloadPANDetails_eKYCResponse")
        private DownloadPANDetailsEKYCResponseBean DownloadPANDetailsEKYCResponse;

        public DownloadPANDetailsEKYCResponseBean getDownloadPANDetailsEKYCResponse() {
            return DownloadPANDetailsEKYCResponse;
        }

        public void setDownloadPANDetailsEKYCResponse(DownloadPANDetailsEKYCResponseBean DownloadPANDetailsEKYCResponse) {
            this.DownloadPANDetailsEKYCResponse = DownloadPANDetailsEKYCResponse;
        }

        public static class DownloadPANDetailsEKYCResponseBean {
            /**
             * DownloadPANDetails_eKYCResult : {"APP_RES_ROOT":{"APP_PAN_INQ":{"APP_INT_CODE":"CK5001","APP_TYPE":"I","APP_NO":"3313784","APP_DATE":"07-09-2016 12:23:26","APP_PAN_NO":"AQRPM8828K","APP_PANEX_NO":"","APP_PAN_COPY":"Y","APP_EXMT":"N","APP_EXMT_CAT":"","APP_EXMT_ID_PROOF":"01","APP_IPV_FLAG":"Y","APP_IPV_DATE":"01-09-2016 00:00:00","APP_GEN":"M","APP_NAME":"Mohamed Imran","APP_F_NAME":"Shahabudeen","APP_REGNO":"","APP_DOB_DT":"07-04-1980 00:00:00","APP_DOI_DT":"01-01-1800 00:00:00","APP_COMMENCE_DT":"01-01-1800 00:00:00","APP_NATIONALITY":"01","APP_OTH_NATIONALITY":"","APP_COMP_STATUS":"R","APP_OTH_COMP_STATUS":"","APP_RES_STATUS":"R","APP_RES_STATUS_PROOF":"31","APP_UID_NO":"N","APP_COR_ADD1":"Address Line one","APP_COR_ADD2":"Address line two","APP_COR_ADD3":"Address line three","APP_COR_CITY":"Chennai","APP_COR_PINCD":"600082","APP_COR_STATE":"033","APP_COR_CTRY":"101","APP_OFF_NO":"","APP_RES_NO":"","APP_MOB_NO":"9940043940","APP_FAX_NO":"","APP_EMAIL":"ps_charanyan@camsonline.com","APP_COR_ADD_PROOF":"31","APP_COR_ADD_REF":"31","APP_COR_ADD_DT":"07-09-2016 12:23:26","APP_PER_ADD1":"32/8 Ballaro Street","APP_PER_ADD2":"Agaram","APP_PER_ADD3":"Jawahar Nagar","APP_PER_CITY":"Chennai","APP_PER_PINCD":"600082","APP_PER_STATE":"033","APP_PER_CTRY":"101","APP_PER_ADD_PROOF":"31","APP_PER_ADD_REF":"","APP_PER_ADD_DT":"07-09-2016 12:23:26","APP_INCOME":"","APP_OCC":"01","APP_OTH_OCC":"","APP_POL_CONN":"","APP_DOC_PROOF":"S","APP_INTERNAL_REF":"CAMSKRA","APP_BRANCH_CODE":"","APP_MAR_STATUS":"01","APP_NETWRTH":"","APP_NETWORTH_DT":"01-01-1800 00:00:00","APP_INCORP_PLC":"","APP_OTHERINFO":"","APP_FILLER1":"","APP_FILLER2":"","APP_FILLER3":"","APP_STATUS":"02","APP_STATUSDT":"15-09-2016 00:00:00","APP_ERROR_DESC":"","APP_DUMP_TYPE":"S","APP_DNLDDT":"01-01-1800 00:00:00","APP_KRA_INFO":"CAMSKRA","APP_SIGNATURE":"","APP_IOP_FLG":"IE","APP_POS_CODE":"infibeam$10","APP_UPDTFLG":"","APP_KYC_MODE":"","APP_SIGN_FLAG":""},"APP_SUMM_REC":{"APP_OTHKRA_BATCH":"SAU_468","APP_OTHKRA_CODE":"PLUTOWS","APP_REQ_DATE":"14-02-2019 19:05:48","APP_TOTAL_REC":"1","APP_RESPONSE_DATE":"14-02-2019 07:05:49"}}}
             */

            @Element(required = false, name ="DownloadPANDetails_eKYCResult")
            private DownloadPANDetailsEKYCResultBean DownloadPANDetailsEKYCResult;

            public DownloadPANDetailsEKYCResultBean getDownloadPANDetailsEKYCResult() {
                return DownloadPANDetailsEKYCResult;
            }

            public void setDownloadPANDetailsEKYCResult(DownloadPANDetailsEKYCResultBean DownloadPANDetailsEKYCResult) {
                this.DownloadPANDetailsEKYCResult = DownloadPANDetailsEKYCResult;
            }

            public static class DownloadPANDetailsEKYCResultBean {
                /**
                 * APP_RES_ROOT : {"APP_PAN_INQ":{"APP_INT_CODE":"CK5001","APP_TYPE":"I","APP_NO":"3313784","APP_DATE":"07-09-2016 12:23:26","APP_PAN_NO":"AQRPM8828K","APP_PANEX_NO":"","APP_PAN_COPY":"Y","APP_EXMT":"N","APP_EXMT_CAT":"","APP_EXMT_ID_PROOF":"01","APP_IPV_FLAG":"Y","APP_IPV_DATE":"01-09-2016 00:00:00","APP_GEN":"M","APP_NAME":"Mohamed Imran","APP_F_NAME":"Shahabudeen","APP_REGNO":"","APP_DOB_DT":"07-04-1980 00:00:00","APP_DOI_DT":"01-01-1800 00:00:00","APP_COMMENCE_DT":"01-01-1800 00:00:00","APP_NATIONALITY":"01","APP_OTH_NATIONALITY":"","APP_COMP_STATUS":"R","APP_OTH_COMP_STATUS":"","APP_RES_STATUS":"R","APP_RES_STATUS_PROOF":"31","APP_UID_NO":"N","APP_COR_ADD1":"Address Line one","APP_COR_ADD2":"Address line two","APP_COR_ADD3":"Address line three","APP_COR_CITY":"Chennai","APP_COR_PINCD":"600082","APP_COR_STATE":"033","APP_COR_CTRY":"101","APP_OFF_NO":"","APP_RES_NO":"","APP_MOB_NO":"9940043940","APP_FAX_NO":"","APP_EMAIL":"ps_charanyan@camsonline.com","APP_COR_ADD_PROOF":"31","APP_COR_ADD_REF":"31","APP_COR_ADD_DT":"07-09-2016 12:23:26","APP_PER_ADD1":"32/8 Ballaro Street","APP_PER_ADD2":"Agaram","APP_PER_ADD3":"Jawahar Nagar","APP_PER_CITY":"Chennai","APP_PER_PINCD":"600082","APP_PER_STATE":"033","APP_PER_CTRY":"101","APP_PER_ADD_PROOF":"31","APP_PER_ADD_REF":"","APP_PER_ADD_DT":"07-09-2016 12:23:26","APP_INCOME":"","APP_OCC":"01","APP_OTH_OCC":"","APP_POL_CONN":"","APP_DOC_PROOF":"S","APP_INTERNAL_REF":"CAMSKRA","APP_BRANCH_CODE":"","APP_MAR_STATUS":"01","APP_NETWRTH":"","APP_NETWORTH_DT":"01-01-1800 00:00:00","APP_INCORP_PLC":"","APP_OTHERINFO":"","APP_FILLER1":"","APP_FILLER2":"","APP_FILLER3":"","APP_STATUS":"02","APP_STATUSDT":"15-09-2016 00:00:00","APP_ERROR_DESC":"","APP_DUMP_TYPE":"S","APP_DNLDDT":"01-01-1800 00:00:00","APP_KRA_INFO":"CAMSKRA","APP_SIGNATURE":"","APP_IOP_FLG":"IE","APP_POS_CODE":"infibeam$10","APP_UPDTFLG":"","APP_KYC_MODE":"","APP_SIGN_FLAG":""},"APP_SUMM_REC":{"APP_OTHKRA_BATCH":"SAU_468","APP_OTHKRA_CODE":"PLUTOWS","APP_REQ_DATE":"14-02-2019 19:05:48","APP_TOTAL_REC":"1","APP_RESPONSE_DATE":"14-02-2019 07:05:49"}}
                 */

                @Element(required = false, name ="APP_RES_ROOT")
                private APPRESROOTBean APPRESROOT;

                public APPRESROOTBean getAPPRESROOT() {
                    return APPRESROOT;
                }

                public void setAPPRESROOT(APPRESROOTBean APPRESROOT) {
                    this.APPRESROOT = APPRESROOT;
                }

                public static class APPRESROOTBean {
                    /**
                     * APP_PAN_INQ : {"APP_INT_CODE":"CK5001","APP_TYPE":"I","APP_NO":"3313784","APP_DATE":"07-09-2016 12:23:26","APP_PAN_NO":"AQRPM8828K","APP_PANEX_NO":"","APP_PAN_COPY":"Y","APP_EXMT":"N","APP_EXMT_CAT":"","APP_EXMT_ID_PROOF":"01","APP_IPV_FLAG":"Y","APP_IPV_DATE":"01-09-2016 00:00:00","APP_GEN":"M","APP_NAME":"Mohamed Imran","APP_F_NAME":"Shahabudeen","APP_REGNO":"","APP_DOB_DT":"07-04-1980 00:00:00","APP_DOI_DT":"01-01-1800 00:00:00","APP_COMMENCE_DT":"01-01-1800 00:00:00","APP_NATIONALITY":"01","APP_OTH_NATIONALITY":"","APP_COMP_STATUS":"R","APP_OTH_COMP_STATUS":"","APP_RES_STATUS":"R","APP_RES_STATUS_PROOF":"31","APP_UID_NO":"N","APP_COR_ADD1":"Address Line one","APP_COR_ADD2":"Address line two","APP_COR_ADD3":"Address line three","APP_COR_CITY":"Chennai","APP_COR_PINCD":"600082","APP_COR_STATE":"033","APP_COR_CTRY":"101","APP_OFF_NO":"","APP_RES_NO":"","APP_MOB_NO":"9940043940","APP_FAX_NO":"","APP_EMAIL":"ps_charanyan@camsonline.com","APP_COR_ADD_PROOF":"31","APP_COR_ADD_REF":"31","APP_COR_ADD_DT":"07-09-2016 12:23:26","APP_PER_ADD1":"32/8 Ballaro Street","APP_PER_ADD2":"Agaram","APP_PER_ADD3":"Jawahar Nagar","APP_PER_CITY":"Chennai","APP_PER_PINCD":"600082","APP_PER_STATE":"033","APP_PER_CTRY":"101","APP_PER_ADD_PROOF":"31","APP_PER_ADD_REF":"","APP_PER_ADD_DT":"07-09-2016 12:23:26","APP_INCOME":"","APP_OCC":"01","APP_OTH_OCC":"","APP_POL_CONN":"","APP_DOC_PROOF":"S","APP_INTERNAL_REF":"CAMSKRA","APP_BRANCH_CODE":"","APP_MAR_STATUS":"01","APP_NETWRTH":"","APP_NETWORTH_DT":"01-01-1800 00:00:00","APP_INCORP_PLC":"","APP_OTHERINFO":"","APP_FILLER1":"","APP_FILLER2":"","APP_FILLER3":"","APP_STATUS":"02","APP_STATUSDT":"15-09-2016 00:00:00","APP_ERROR_DESC":"","APP_DUMP_TYPE":"S","APP_DNLDDT":"01-01-1800 00:00:00","APP_KRA_INFO":"CAMSKRA","APP_SIGNATURE":"","APP_IOP_FLG":"IE","APP_POS_CODE":"infibeam$10","APP_UPDTFLG":"","APP_KYC_MODE":"","APP_SIGN_FLAG":""}
                     * APP_SUMM_REC : {"APP_OTHKRA_BATCH":"SAU_468","APP_OTHKRA_CODE":"PLUTOWS","APP_REQ_DATE":"14-02-2019 19:05:48","APP_TOTAL_REC":"1","APP_RESPONSE_DATE":"14-02-2019 07:05:49"}
                     */

                    @Element(required = false, name ="APP_PAN_INQ")
                    private APPPANINQBean APPPANINQ;
                    @Element(required = false, name ="APP_SUMM_REC")
                    private APPSUMMRECBean APPSUMMREC;

                    public APPPANINQBean getAPPPANINQ() {
                        return APPPANINQ;
                    }

                    public void setAPPPANINQ(APPPANINQBean APPPANINQ) {
                        this.APPPANINQ = APPPANINQ;
                    }

                    public APPSUMMRECBean getAPPSUMMREC() {
                        return APPSUMMREC;
                    }

                    public void setAPPSUMMREC(APPSUMMRECBean APPSUMMREC) {
                        this.APPSUMMREC = APPSUMMREC;
                    }

                    public static class APPPANINQBean {
                        /**
                         * APP_INT_CODE : CK5001
                         * APP_TYPE : I
                         * APP_NO : 3313784
                         * APP_DATE : 07-09-2016 12:23:26
                         * APP_PAN_NO : AQRPM8828K
                         * APP_PANEX_NO :
                         * APP_PAN_COPY : Y
                         * APP_EXMT : N
                         * APP_EXMT_CAT :
                         * APP_EXMT_ID_PROOF : 01
                         * APP_IPV_FLAG : Y
                         * APP_IPV_DATE : 01-09-2016 00:00:00
                         * APP_GEN : M
                         * APP_NAME : Mohamed Imran
                         * APP_F_NAME : Shahabudeen
                         * APP_REGNO :
                         * APP_DOB_DT : 07-04-1980 00:00:00
                         * APP_DOI_DT : 01-01-1800 00:00:00
                         * APP_COMMENCE_DT : 01-01-1800 00:00:00
                         * APP_NATIONALITY : 01
                         * APP_OTH_NATIONALITY :
                         * APP_COMP_STATUS : R
                         * APP_OTH_COMP_STATUS :
                         * APP_RES_STATUS : R
                         * APP_RES_STATUS_PROOF : 31
                         * APP_UID_NO : N
                         * APP_COR_ADD1 : Address Line one
                         * APP_COR_ADD2 : Address line two
                         * APP_COR_ADD3 : Address line three
                         * APP_COR_CITY : Chennai
                         * APP_COR_PINCD : 600082
                         * APP_COR_STATE : 033
                         * APP_COR_CTRY : 101
                         * APP_OFF_NO :
                         * APP_RES_NO :
                         * APP_MOB_NO : 9940043940
                         * APP_FAX_NO :
                         * APP_EMAIL : ps_charanyan@camsonline.com
                         * APP_COR_ADD_PROOF : 31
                         * APP_COR_ADD_REF : 31
                         * APP_COR_ADD_DT : 07-09-2016 12:23:26
                         * APP_PER_ADD1 : 32/8 Ballaro Street
                         * APP_PER_ADD2 : Agaram
                         * APP_PER_ADD3 : Jawahar Nagar
                         * APP_PER_CITY : Chennai
                         * APP_PER_PINCD : 600082
                         * APP_PER_STATE : 033
                         * APP_PER_CTRY : 101
                         * APP_PER_ADD_PROOF : 31
                         * APP_PER_ADD_REF :
                         * APP_PER_ADD_DT : 07-09-2016 12:23:26
                         * APP_INCOME :
                         * APP_OCC : 01
                         * APP_OTH_OCC :
                         * APP_POL_CONN :
                         * APP_DOC_PROOF : S
                         * APP_INTERNAL_REF : CAMSKRA
                         * APP_BRANCH_CODE :
                         * APP_MAR_STATUS : 01
                         * APP_NETWRTH :
                         * APP_NETWORTH_DT : 01-01-1800 00:00:00
                         * APP_INCORP_PLC :
                         * APP_OTHERINFO :
                         * APP_FILLER1 :
                         * APP_FILLER2 :
                         * APP_FILLER3 :
                         * APP_STATUS : 02
                         * APP_STATUSDT : 15-09-2016 00:00:00
                         * APP_ERROR_DESC :
                         * APP_DUMP_TYPE : S
                         * APP_DNLDDT : 01-01-1800 00:00:00
                         * APP_KRA_INFO : CAMSKRA
                         * APP_SIGNATURE :
                         * APP_IOP_FLG : IE
                         * APP_POS_CODE : infibeam$10
                         * APP_UPDTFLG :
                         * APP_KYC_MODE :
                         * APP_SIGN_FLAG :
                         */

                        @Element(required = false, name ="APP_INT_CODE")
                        private String APPINTCODE;
                        @Element(required = false, name ="APP_TYPE")
                        private String APPTYPE;
                        @Element(required = false, name ="APP_NO")
                        private String APPNO;
                        @Element(required = false, name ="APP_DATE")
                        private String APPDATE;
                        @Element(required = false, name ="APP_PAN_NO")
                        private String APPPANNO;
                        @Element(required = false, name ="APP_PANEX_NO")
                        private String APPPANEXNO;
                        @Element(required = false, name ="APP_PAN_COPY")
                        private String APPPANCOPY;
                        @Element(required = false, name ="APP_EXMT")
                        private String APPEXMT;
                        @Element(required = false, name ="APP_EXMT_CAT")
                        private String APPEXMTCAT;
                        @Element(required = false, name ="APP_EXMT_ID_PROOF")
                        private String APPEXMTIDPROOF;
                        @Element(required = false, name ="APP_IPV_FLAG")
                        private String APPIPVFLAG;
                        @Element(required = false, name ="APP_IPV_DATE")
                        private String APPIPVDATE;
                        @Element(required = false, name ="APP_GEN")
                        private String APPGEN;
                        @Element(required = false, name ="APP_NAME")
                        private String APPNAME;
                        @Element(required = false, name ="APP_F_NAME")
                        private String APPFNAME;
                        @Element(required = false, name ="APP_REGNO")
                        private String APPREGNO;
                        @Element(required = false, name ="APP_DOB_DT")
                        private String APPDOBDT;
                        @Element(required = false, name ="APP_DOI_DT")
                        private String APPDOIDT;
                        @Element(required = false, name ="APP_COMMENCE_DT")
                        private String APPCOMMENCEDT;
                        @Element(required = false, name ="APP_NATIONALITY")
                        private String APPNATIONALITY;
                        @Element(required = false, name ="APP_OTH_NATIONALITY")
                        private String APPOTHNATIONALITY;
                        @Element(required = false, name ="APP_COMP_STATUS")
                        private String APPCOMPSTATUS;
                        @Element(required = false, name ="APP_OTH_COMP_STATUS")
                        private String APPOTHCOMPSTATUS;
                        @Element(required = false, name ="APP_RES_STATUS")
                        private String APPRESSTATUS;
                        @Element(required = false, name ="APP_RES_STATUS_PROOF")
                        private String APPRESSTATUSPROOF;
                        @Element(required = false, name ="APP_UID_NO")
                        private String APPUIDNO;
                        @Element(required = false, name ="APP_COR_ADD1")
                        private String APPCORADD1;
                        @Element(required = false, name ="APP_COR_ADD2")
                        private String APPCORADD2;
                        @Element(required = false, name ="APP_COR_ADD3")
                        private String APPCORADD3;
                        @Element(required = false, name ="APP_COR_CITY")
                        private String APPCORCITY;
                        @Element(required = false, name ="APP_COR_PINCD")
                        private String APPCORPINCD;
                        @Element(required = false, name ="APP_COR_STATE")
                        private String APPCORSTATE;
                        @Element(required = false, name ="APP_COR_CTRY")
                        private String APPCORCTRY;
                        @Element(required = false, name ="APP_OFF_NO")
                        private String APPOFFNO;
                        @Element(required = false, name ="APP_RES_NO")
                        private String APPRESNO;
                        @Element(required = false, name ="APP_MOB_NO")
                        private String APPMOBNO;
                        @Element(required = false, name ="APP_FAX_NO")
                        private String APPFAXNO;
                        @Element(required = false, name ="APP_EMAIL")
                        private String APPEMAIL;
                        @Element(required = false, name ="APP_COR_ADD_PROOF")
                        private String APPCORADDPROOF;
                        @Element(required = false, name ="APP_COR_ADD_REF")
                        private String APPCORADDREF;
                        @Element(required = false, name ="APP_COR_ADD_DT")
                        private String APPCORADDDT;
                        @Element(required = false, name ="APP_PER_ADD1")
                        private String  APPPERADD1;
                        @Element(required = false, name ="APP_PER_ADD2")
                        private String APPPERADD2;
                        @Element(required = false, name ="APP_PER_ADD3")
                        private String APPPERADD3;
                        @Element(required = false, name ="APP_PER_CITY")
                        private String APPPERCITY;
                        @Element(required = false, name ="APP_PER_PINCD")
                        private String APPPERPINCD;
                        @Element(required = false, name ="APP_PER_STATE")
                        private String APPPERSTATE;
                        @Element(required = false, name ="APP_PER_CTRY")
                        private String APPPERCTRY;
                        @Element(required = false, name ="APP_PER_ADD_PROOF")
                        private String APPPERADDPROOF;
                        @Element(required = false, name ="APP_PER_ADD_REF")
                        private String APPPERADDREF;
                        @Element(required = false, name ="APP_PER_ADD_DT")
                        private String APPPERADDDT;
                        @Element(required = false, name ="APP_INCOME")
                        private String APPINCOME;
                        @Element(required = false, name ="APP_OCC")
                        private String APPOCC;
                        @Element(required = false, name ="APP_OTH_OCC")
                        private String APPOTHOCC;
                        @Element(required = false, name ="APP_POL_CONN")
                        private String APPPOLCONN;
                        @Element(required = false, name ="APP_DOC_PROOF")
                        private String APPDOCPROOF;
                        @Element(required = false, name ="APP_INTERNAL_REF")
                        private String APPINTERNALREF;
                        @Element(required = false, name ="APP_BRANCH_CODE")
                        private String APPBRANCHCODE;
                        @Element(required = false, name ="APP_MAR_STATUS")
                        private String APPMARSTATUS;
                        @Element(required = false, name ="APP_NETWRTH")
                        private String APPNETWRTH;
                        @Element(required = false, name ="APP_NETWORTH_DT")
                        private String APPNETWORTHDT;
                        @Element(required = false, name ="APP_INCORP_PLC")
                        private String APPINCORPPLC;
                        @Element(required = false, name ="APP_OTHERINFO")
                        private String APPOTHERINFO;
                        @Element(required = false, name ="APP_FILLER1")
                        private String APPFILLER1;
                        @Element(required = false, name ="APP_FILLER2")
                        private String APPFILLER2;
                        @Element(required = false, name ="APP_FILLER3")
                        private String APPFILLER3;
                        @Element(required = false, name ="APP_STATUS")
                        private String APPSTATUS;
                        @Element(required = false, name ="APP_STATUSDT")
                        private String APPSTATUSDT;
                        @Element(required = false, name ="APP_ERROR_DESC")
                        private String APPERRORDESC;
                        @Element(required = false, name ="APP_DUMP_TYPE")
                        private String APPDUMPTYPE;
                        @Element(required = false, name ="APP_DNLDDT")
                        private String APPDNLDDT;
                        @Element(required = false, name ="APP_KRA_INFO")
                        private String APPKRAINFO;
                        @Element(required = false, name ="APP_SIGNATURE")
                        private String APPSIGNATURE;
                        @Element(required = false, name ="APP_IOP_FLG")
                        private String APPIOPFLG;
                        @Element(required = false, name ="APP_POS_CODE")
                        private String APPPOSCODE;
                        @Element(required = false, name ="APP_UPDTFLG")
                        private String APPUPDTFLG;
                        @Element(required = false, name ="APP_KYC_MODE")
                        private String APPKYCMODE;
                        @Element(required = false, name ="APP_SIGN_FLAG")
                        private String APPSIGNFLAG;

                        public String getAPPINTCODE() {
                            return APPINTCODE;
                        }

                        public void setAPPINTCODE(String APPINTCODE) {
                            this.APPINTCODE = APPINTCODE;
                        }

                        public String getAPPTYPE() {
                            return APPTYPE;
                        }

                        public void setAPPTYPE(String APPTYPE) {
                            this.APPTYPE = APPTYPE;
                        }

                        public String getAPPNO() {
                            return APPNO;
                        }

                        public void setAPPNO(String APPNO) {
                            this.APPNO = APPNO;
                        }

                        public String getAPPDATE() {
                            return APPDATE;
                        }

                        public void setAPPDATE(String APPDATE) {
                            this.APPDATE = APPDATE;
                        }

                        public String getAPPPANNO() {
                            return APPPANNO;
                        }

                        public void setAPPPANNO(String APPPANNO) {
                            this.APPPANNO = APPPANNO;
                        }

                        public String getAPPPANEXNO() {
                            return APPPANEXNO;
                        }

                        public void setAPPPANEXNO(String APPPANEXNO) {
                            this.APPPANEXNO = APPPANEXNO;
                        }

                        public String getAPPPANCOPY() {
                            return APPPANCOPY;
                        }

                        public void setAPPPANCOPY(String APPPANCOPY) {
                            this.APPPANCOPY = APPPANCOPY;
                        }

                        public String getAPPEXMT() {
                            return APPEXMT;
                        }

                        public void setAPPEXMT(String APPEXMT) {
                            this.APPEXMT = APPEXMT;
                        }

                        public String getAPPEXMTCAT() {
                            return APPEXMTCAT;
                        }

                        public void setAPPEXMTCAT(String APPEXMTCAT) {
                            this.APPEXMTCAT = APPEXMTCAT;
                        }

                        public String getAPPEXMTIDPROOF() {
                            return APPEXMTIDPROOF;
                        }

                        public void setAPPEXMTIDPROOF(String APPEXMTIDPROOF) {
                            this.APPEXMTIDPROOF = APPEXMTIDPROOF;
                        }

                        public String getAPPIPVFLAG() {
                            return APPIPVFLAG;
                        }

                        public void setAPPIPVFLAG(String APPIPVFLAG) {
                            this.APPIPVFLAG = APPIPVFLAG;
                        }

                        public String getAPPIPVDATE() {
                            return APPIPVDATE;
                        }

                        public void setAPPIPVDATE(String APPIPVDATE) {
                            this.APPIPVDATE = APPIPVDATE;
                        }

                        public String getAPPGEN() {
                            return APPGEN;
                        }

                        public void setAPPGEN(String APPGEN) {
                            this.APPGEN = APPGEN;
                        }

                        public String getAPPNAME() {
                            return APPNAME;
                        }

                        public void setAPPNAME(String APPNAME) {
                            this.APPNAME = APPNAME;
                        }

                        public String getAPPFNAME() {
                            return APPFNAME;
                        }

                        public void setAPPFNAME(String APPFNAME) {
                            this.APPFNAME = APPFNAME;
                        }

                        public String getAPPREGNO() {
                            return APPREGNO;
                        }

                        public void setAPPREGNO(String APPREGNO) {
                            this.APPREGNO = APPREGNO;
                        }

                        public String getAPPDOBDT() {
                            return APPDOBDT;
                        }

                        public void setAPPDOBDT(String APPDOBDT) {
                            this.APPDOBDT = APPDOBDT;
                        }

                        public String getAPPDOIDT() {
                            return APPDOIDT;
                        }

                        public void setAPPDOIDT(String APPDOIDT) {
                            this.APPDOIDT = APPDOIDT;
                        }

                        public String getAPPCOMMENCEDT() {
                            return APPCOMMENCEDT;
                        }

                        public void setAPPCOMMENCEDT(String APPCOMMENCEDT) {
                            this.APPCOMMENCEDT = APPCOMMENCEDT;
                        }

                        public String getAPPNATIONALITY() {
                            return APPNATIONALITY;
                        }

                        public void setAPPNATIONALITY(String APPNATIONALITY) {
                            this.APPNATIONALITY = APPNATIONALITY;
                        }

                        public String getAPPOTHNATIONALITY() {
                            return APPOTHNATIONALITY;
                        }

                        public void setAPPOTHNATIONALITY(String APPOTHNATIONALITY) {
                            this.APPOTHNATIONALITY = APPOTHNATIONALITY;
                        }

                        public String getAPPCOMPSTATUS() {
                            return APPCOMPSTATUS;
                        }

                        public void setAPPCOMPSTATUS(String APPCOMPSTATUS) {
                            this.APPCOMPSTATUS = APPCOMPSTATUS;
                        }

                        public String getAPPOTHCOMPSTATUS() {
                            return APPOTHCOMPSTATUS;
                        }

                        public void setAPPOTHCOMPSTATUS(String APPOTHCOMPSTATUS) {
                            this.APPOTHCOMPSTATUS = APPOTHCOMPSTATUS;
                        }

                        public String getAPPRESSTATUS() {
                            return APPRESSTATUS;
                        }

                        public void setAPPRESSTATUS(String APPRESSTATUS) {
                            this.APPRESSTATUS = APPRESSTATUS;
                        }

                        public String getAPPRESSTATUSPROOF() {
                            return APPRESSTATUSPROOF;
                        }

                        public void setAPPRESSTATUSPROOF(String APPRESSTATUSPROOF) {
                            this.APPRESSTATUSPROOF = APPRESSTATUSPROOF;
                        }

                        public String getAPPUIDNO() {
                            return APPUIDNO;
                        }

                        public void setAPPUIDNO(String APPUIDNO) {
                            this.APPUIDNO = APPUIDNO;
                        }

                        public String getAPPCORADD1() {
                            return APPCORADD1;
                        }

                        public void setAPPCORADD1(String APPCORADD1) {
                            this.APPCORADD1 = APPCORADD1;
                        }

                        public String getAPPCORADD2() {
                            return APPCORADD2;
                        }

                        public void setAPPCORADD2(String APPCORADD2) {
                            this.APPCORADD2 = APPCORADD2;
                        }

                        public String getAPPCORADD3() {
                            return APPCORADD3;
                        }

                        public void setAPPCORADD3(String APPCORADD3) {
                            this.APPCORADD3 = APPCORADD3;
                        }

                        public String getAPPCORCITY() {
                            return APPCORCITY;
                        }

                        public void setAPPCORCITY(String APPCORCITY) {
                            this.APPCORCITY = APPCORCITY;
                        }

                        public String getAPPCORPINCD() {
                            return APPCORPINCD;
                        }

                        public void setAPPCORPINCD(String APPCORPINCD) {
                            this.APPCORPINCD = APPCORPINCD;
                        }

                        public String getAPPCORSTATE() {
                            return APPCORSTATE;
                        }

                        public void setAPPCORSTATE(String APPCORSTATE) {
                            this.APPCORSTATE = APPCORSTATE;
                        }

                        public String getAPPCORCTRY() {
                            return APPCORCTRY;
                        }

                        public void setAPPCORCTRY(String APPCORCTRY) {
                            this.APPCORCTRY = APPCORCTRY;
                        }

                        public String getAPPOFFNO() {
                            return APPOFFNO;
                        }

                        public void setAPPOFFNO(String APPOFFNO) {
                            this.APPOFFNO = APPOFFNO;
                        }

                        public String getAPPRESNO() {
                            return APPRESNO;
                        }

                        public void setAPPRESNO(String APPRESNO) {
                            this.APPRESNO = APPRESNO;
                        }

                        public String getAPPMOBNO() {
                            return APPMOBNO;
                        }

                        public void setAPPMOBNO(String APPMOBNO) {
                            this.APPMOBNO = APPMOBNO;
                        }

                        public String getAPPFAXNO() {
                            return APPFAXNO;
                        }

                        public void setAPPFAXNO(String APPFAXNO) {
                            this.APPFAXNO = APPFAXNO;
                        }

                        public String getAPPEMAIL() {
                            return APPEMAIL;
                        }

                        public void setAPPEMAIL(String APPEMAIL) {
                            this.APPEMAIL = APPEMAIL;
                        }

                        public String getAPPCORADDPROOF() {
                            return APPCORADDPROOF;
                        }

                        public void setAPPCORADDPROOF(String APPCORADDPROOF) {
                            this.APPCORADDPROOF = APPCORADDPROOF;
                        }

                        public String getAPPCORADDREF() {
                            return APPCORADDREF;
                        }

                        public void setAPPCORADDREF(String APPCORADDREF) {
                            this.APPCORADDREF = APPCORADDREF;
                        }

                        public String getAPPCORADDDT() {
                            return APPCORADDDT;
                        }

                        public void setAPPCORADDDT(String APPCORADDDT) {
                            this.APPCORADDDT = APPCORADDDT;
                        }

                        public String getAPPPERADD1() {
                            return APPPERADD1;
                        }

                        public void setAPPPERADD1(String APPPERADD1) {
                            this.APPPERADD1 = APPPERADD1;
                        }

                        public String getAPPPERADD2() {
                            return APPPERADD2;
                        }

                        public void setAPPPERADD2(String APPPERADD2) {
                            this.APPPERADD2 = APPPERADD2;
                        }

                        public String getAPPPERADD3() {
                            return APPPERADD3;
                        }

                        public void setAPPPERADD3(String APPPERADD3) {
                            this.APPPERADD3 = APPPERADD3;
                        }

                        public String getAPPPERCITY() {
                            return APPPERCITY;
                        }

                        public void setAPPPERCITY(String APPPERCITY) {
                            this.APPPERCITY = APPPERCITY;
                        }

                        public String getAPPPERPINCD() {
                            return APPPERPINCD;
                        }

                        public void setAPPPERPINCD(String APPPERPINCD) {
                            this.APPPERPINCD = APPPERPINCD;
                        }

                        public String getAPPPERSTATE() {
                            return APPPERSTATE;
                        }

                        public void setAPPPERSTATE(String APPPERSTATE) {
                            this.APPPERSTATE = APPPERSTATE;
                        }

                        public String getAPPPERCTRY() {
                            return APPPERCTRY;
                        }

                        public void setAPPPERCTRY(String APPPERCTRY) {
                            this.APPPERCTRY = APPPERCTRY;
                        }

                        public String getAPPPERADDPROOF() {
                            return APPPERADDPROOF;
                        }

                        public void setAPPPERADDPROOF(String APPPERADDPROOF) {
                            this.APPPERADDPROOF = APPPERADDPROOF;
                        }

                        public String getAPPPERADDREF() {
                            return APPPERADDREF;
                        }

                        public void setAPPPERADDREF(String APPPERADDREF) {
                            this.APPPERADDREF = APPPERADDREF;
                        }

                        public String getAPPPERADDDT() {
                            return APPPERADDDT;
                        }

                        public void setAPPPERADDDT(String APPPERADDDT) {
                            this.APPPERADDDT = APPPERADDDT;
                        }

                        public String getAPPINCOME() {
                            return APPINCOME;
                        }

                        public void setAPPINCOME(String APPINCOME) {
                            this.APPINCOME = APPINCOME;
                        }

                        public String getAPPOCC() {
                            return APPOCC;
                        }

                        public void setAPPOCC(String APPOCC) {
                            this.APPOCC = APPOCC;
                        }

                        public String getAPPOTHOCC() {
                            return APPOTHOCC;
                        }

                        public void setAPPOTHOCC(String APPOTHOCC) {
                            this.APPOTHOCC = APPOTHOCC;
                        }

                        public String getAPPPOLCONN() {
                            return APPPOLCONN;
                        }

                        public void setAPPPOLCONN(String APPPOLCONN) {
                            this.APPPOLCONN = APPPOLCONN;
                        }

                        public String getAPPDOCPROOF() {
                            return APPDOCPROOF;
                        }

                        public void setAPPDOCPROOF(String APPDOCPROOF) {
                            this.APPDOCPROOF = APPDOCPROOF;
                        }

                        public String getAPPINTERNALREF() {
                            return APPINTERNALREF;
                        }

                        public void setAPPINTERNALREF(String APPINTERNALREF) {
                            this.APPINTERNALREF = APPINTERNALREF;
                        }

                        public String getAPPBRANCHCODE() {
                            return APPBRANCHCODE;
                        }

                        public void setAPPBRANCHCODE(String APPBRANCHCODE) {
                            this.APPBRANCHCODE = APPBRANCHCODE;
                        }

                        public String getAPPMARSTATUS() {
                            return APPMARSTATUS;
                        }

                        public void setAPPMARSTATUS(String APPMARSTATUS) {
                            this.APPMARSTATUS = APPMARSTATUS;
                        }

                        public String getAPPNETWRTH() {
                            return APPNETWRTH;
                        }

                        public void setAPPNETWRTH(String APPNETWRTH) {
                            this.APPNETWRTH = APPNETWRTH;
                        }

                        public String getAPPNETWORTHDT() {
                            return APPNETWORTHDT;
                        }

                        public void setAPPNETWORTHDT(String APPNETWORTHDT) {
                            this.APPNETWORTHDT = APPNETWORTHDT;
                        }

                        public String getAPPINCORPPLC() {
                            return APPINCORPPLC;
                        }

                        public void setAPPINCORPPLC(String APPINCORPPLC) {
                            this.APPINCORPPLC = APPINCORPPLC;
                        }

                        public String getAPPOTHERINFO() {
                            return APPOTHERINFO;
                        }

                        public void setAPPOTHERINFO(String APPOTHERINFO) {
                            this.APPOTHERINFO = APPOTHERINFO;
                        }

                        public String getAPPFILLER1() {
                            return APPFILLER1;
                        }

                        public void setAPPFILLER1(String APPFILLER1) {
                            this.APPFILLER1 = APPFILLER1;
                        }

                        public String getAPPFILLER2() {
                            return APPFILLER2;
                        }

                        public void setAPPFILLER2(String APPFILLER2) {
                            this.APPFILLER2 = APPFILLER2;
                        }

                        public String getAPPFILLER3() {
                            return APPFILLER3;
                        }

                        public void setAPPFILLER3(String APPFILLER3) {
                            this.APPFILLER3 = APPFILLER3;
                        }

                        public String getAPPSTATUS() {
                            return APPSTATUS;
                        }

                        public void setAPPSTATUS(String APPSTATUS) {
                            this.APPSTATUS = APPSTATUS;
                        }

                        public String getAPPSTATUSDT() {
                            return APPSTATUSDT;
                        }

                        public void setAPPSTATUSDT(String APPSTATUSDT) {
                            this.APPSTATUSDT = APPSTATUSDT;
                        }

                        public String getAPPERRORDESC() {
                            return APPERRORDESC;
                        }

                        public void setAPPERRORDESC(String APPERRORDESC) {
                            this.APPERRORDESC = APPERRORDESC;
                        }

                        public String getAPPDUMPTYPE() {
                            return APPDUMPTYPE;
                        }

                        public void setAPPDUMPTYPE(String APPDUMPTYPE) {
                            this.APPDUMPTYPE = APPDUMPTYPE;
                        }

                        public String getAPPDNLDDT() {
                            return APPDNLDDT;
                        }

                        public void setAPPDNLDDT(String APPDNLDDT) {
                            this.APPDNLDDT = APPDNLDDT;
                        }

                        public String getAPPKRAINFO() {
                            return APPKRAINFO;
                        }

                        public void setAPPKRAINFO(String APPKRAINFO) {
                            this.APPKRAINFO = APPKRAINFO;
                        }

                        public String getAPPSIGNATURE() {
                            return APPSIGNATURE;
                        }

                        public void setAPPSIGNATURE(String APPSIGNATURE) {
                            this.APPSIGNATURE = APPSIGNATURE;
                        }

                        public String getAPPIOPFLG() {
                            return APPIOPFLG;
                        }

                        public void setAPPIOPFLG(String APPIOPFLG) {
                            this.APPIOPFLG = APPIOPFLG;
                        }

                        public String getAPPPOSCODE() {
                            return APPPOSCODE;
                        }

                        public void setAPPPOSCODE(String APPPOSCODE) {
                            this.APPPOSCODE = APPPOSCODE;
                        }

                        public String getAPPUPDTFLG() {
                            return APPUPDTFLG;
                        }

                        public void setAPPUPDTFLG(String APPUPDTFLG) {
                            this.APPUPDTFLG = APPUPDTFLG;
                        }

                        public String getAPPKYCMODE() {
                            return APPKYCMODE;
                        }

                        public void setAPPKYCMODE(String APPKYCMODE) {
                            this.APPKYCMODE = APPKYCMODE;
                        }

                        public String getAPPSIGNFLAG() {
                            return APPSIGNFLAG;
                        }

                        public void setAPPSIGNFLAG(String APPSIGNFLAG) {
                            this.APPSIGNFLAG = APPSIGNFLAG;
                        }
                    }

                    public static class APPSUMMRECBean {
                        /**
                         * APP_OTHKRA_BATCH : SAU_468
                         * APP_OTHKRA_CODE : PLUTOWS
                         * APP_REQ_DATE : 14-02-2019 19:05:48
                         * APP_TOTAL_REC : 1
                         * APP_RESPONSE_DATE : 14-02-2019 07:05:49
                         */

                        @Element(required = false, name ="APP_OTHKRA_BATCH")
                        private String APPOTHKRABATCH;
                        @Element(required = false, name ="APP_OTHKRA_CODE")
                        private String APPOTHKRACODE;
                        @Element(required = false, name ="APP_REQ_DATE")
                        private String APPREQDATE;
                        @Element(required = false, name ="APP_TOTAL_REC")
                        private String APPTOTALREC;
                        @Element(required = false, name ="APP_RESPONSE_DATE")
                        private String APPRESPONSEDATE;

                        public String getAPPOTHKRABATCH() {
                            return APPOTHKRABATCH;
                        }

                        public void setAPPOTHKRABATCH(String APPOTHKRABATCH) {
                            this.APPOTHKRABATCH = APPOTHKRABATCH;
                        }

                        public String getAPPOTHKRACODE() {
                            return APPOTHKRACODE;
                        }

                        public void setAPPOTHKRACODE(String APPOTHKRACODE) {
                            this.APPOTHKRACODE = APPOTHKRACODE;
                        }

                        public String getAPPREQDATE() {
                            return APPREQDATE;
                        }

                        public void setAPPREQDATE(String APPREQDATE) {
                            this.APPREQDATE = APPREQDATE;
                        }

                        public String getAPPTOTALREC() {
                            return APPTOTALREC;
                        }

                        public void setAPPTOTALREC(String APPTOTALREC) {
                            this.APPTOTALREC = APPTOTALREC;
                        }

                        public String getAPPRESPONSEDATE() {
                            return APPRESPONSEDATE;
                        }

                        public void setAPPRESPONSEDATE(String APPRESPONSEDATE) {
                            this.APPRESPONSEDATE = APPRESPONSEDATE;
                        }
                    }
                }
            }
        }
    }
}

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
public class ResponseKYCStates {

    /**
     * Body : {"VerifyPANDetails_eKYCResponse":{"VerifyPANDetails_eKYCResult":{"APP_RES_ROOT":{"APP_PAN_INQ":{"APP_PAN_NO":"DGUPP2792B","APP_NAME":"","APP_STATUSDT":"","APP_ENTRYDT":"","APP_MODDT":"","APP_UPDT_STATUS":"","APP_HOLD_DEACTIVE_RMKS":"","APP_KYC_MODE":"","APP_SIGN_FLAG":"","APP_IPV_FLAG":"","CAMSKRA":"05","CVLKRA":"99","NDMLKRA":"99","DOTEXKRA":"99","KARVYKRA":"99"}}}}}
     */
    @Element(name = "Body", required = false)
    private BodyBean Body;

    public BodyBean getBody() {
        return Body;
    }

    public void setBody(BodyBean Body) {
        this.Body = Body;
    }

    public static class BodyBean {
        /**
         * VerifyPANDetails_eKYCResponse : {"VerifyPANDetails_eKYCResult":{"APP_RES_ROOT":{"APP_PAN_INQ":{"APP_PAN_NO":"DGUPP2792B","APP_NAME":"","APP_STATUSDT":"","APP_ENTRYDT":"","APP_MODDT":"","APP_UPDT_STATUS":"","APP_HOLD_DEACTIVE_RMKS":"","APP_KYC_MODE":"","APP_SIGN_FLAG":"","APP_IPV_FLAG":"","CAMSKRA":"05","CVLKRA":"99","NDMLKRA":"99","DOTEXKRA":"99","KARVYKRA":"99"}}}}
         */
        @Namespace(reference = "https://camskra.com/")
        @Element(name = "VerifyPANDetails_eKYCResponse", required = false)
        private VerifyPANDetailsEKYCResponseBean VerifyPANDetailsEKYCResponse;

        public VerifyPANDetailsEKYCResponseBean getVerifyPANDetailsEKYCResponse() {
            return VerifyPANDetailsEKYCResponse;
        }

        public void setVerifyPANDetailsEKYCResponse(VerifyPANDetailsEKYCResponseBean VerifyPANDetailsEKYCResponse) {
            this.VerifyPANDetailsEKYCResponse = VerifyPANDetailsEKYCResponse;
        }

        public static class VerifyPANDetailsEKYCResponseBean {
            /**
             * VerifyPANDetails_eKYCResult : {"APP_RES_ROOT":{"APP_PAN_INQ":{"APP_PAN_NO":"DGUPP2792B","APP_NAME":"","APP_STATUSDT":"","APP_ENTRYDT":"","APP_MODDT":"","APP_UPDT_STATUS":"","APP_HOLD_DEACTIVE_RMKS":"","APP_KYC_MODE":"","APP_SIGN_FLAG":"","APP_IPV_FLAG":"","CAMSKRA":"05","CVLKRA":"99","NDMLKRA":"99","DOTEXKRA":"99","KARVYKRA":"99"}}}
             */

            @Element(name = "VerifyPANDetails_eKYCResult", required = false)
            private VerifyPANDetailsEKYCResultBean VerifyPANDetailsEKYCResult;

            public VerifyPANDetailsEKYCResultBean getVerifyPANDetailsEKYCResult() {
                return VerifyPANDetailsEKYCResult;
            }

            public void setVerifyPANDetailsEKYCResult(VerifyPANDetailsEKYCResultBean VerifyPANDetailsEKYCResult) {
                this.VerifyPANDetailsEKYCResult = VerifyPANDetailsEKYCResult;
            }

            public static class VerifyPANDetailsEKYCResultBean {
                /**
                 * APP_RES_ROOT : {"APP_PAN_INQ":{"APP_PAN_NO":"DGUPP2792B","APP_NAME":"","APP_STATUSDT":"","APP_ENTRYDT":"","APP_MODDT":"","APP_UPDT_STATUS":"","APP_HOLD_DEACTIVE_RMKS":"","APP_KYC_MODE":"","APP_SIGN_FLAG":"","APP_IPV_FLAG":"","CAMSKRA":"05","CVLKRA":"99","NDMLKRA":"99","DOTEXKRA":"99","KARVYKRA":"99"}}
                 */
                @Element(name = "APP_RES_ROOT", required = false)
                private APPRESROOTBean APPRESROOT;

                public APPRESROOTBean getAPPRESROOT() {
                    return APPRESROOT;
                }

                public void setAPPRESROOT(APPRESROOTBean APPRESROOT) {
                    this.APPRESROOT = APPRESROOT;
                }

                public static class APPRESROOTBean {
                    /**
                     * APP_PAN_INQ : {"APP_PAN_NO":"DGUPP2792B","APP_NAME":"","APP_STATUSDT":"","APP_ENTRYDT":"","APP_MODDT":"","APP_UPDT_STATUS":"","APP_HOLD_DEACTIVE_RMKS":"","APP_KYC_MODE":"","APP_SIGN_FLAG":"","APP_IPV_FLAG":"","CAMSKRA":"05","CVLKRA":"99","NDMLKRA":"99","DOTEXKRA":"99","KARVYKRA":"99"}
                     */

                    @Element(name = "APP_PAN_INQ", required = false)
                    private APPPANINQBean APPPANINQ;

                    public APPPANINQBean getAPPPANINQ() {
                        return APPPANINQ;
                    }

                    public void setAPPPANINQ(APPPANINQBean APPPANINQ) {
                        this.APPPANINQ = APPPANINQ;
                    }

                    public static class APPPANINQBean {
                        /**
                         * APP_PAN_NO : DGUPP2792B
                         * APP_NAME :
                         * APP_STATUSDT :
                         * APP_ENTRYDT :
                         * APP_MODDT :
                         * APP_UPDT_STATUS :
                         * APP_HOLD_DEACTIVE_RMKS :
                         * APP_KYC_MODE :
                         * APP_SIGN_FLAG :
                         * APP_IPV_FLAG :
                         * CAMSKRA : 05
                         * CVLKRA : 99
                         * NDMLKRA : 99
                         * DOTEXKRA : 99
                         * KARVYKRA : 99
                         */

                        @Element(name = "APP_PAN_NO", required = false)
                        private String APPPANNO;
                        @Element(name = "APP_NAME", required = false)
                        private String APPNAME;
                        @Element(name = "APP_STATUSDT", required = false)
                        private String APPSTATUSDT;
                        @Element(name = "APP_ENTRYDT", required = false)
                        private String APPENTRYDT;
                        @Element(name = "APP_MODDT", required = false)
                        private String APPMODDT;
                        @Element(name = "APP_UPDT_STATUS", required = false)
                        private String APPUPDTSTATUS;
                        @Element(name = "APP_HOLD_DEACTIVE_RMKS", required = false)
                        private String APPHOLDDEACTIVERMKS;
                        @Element(name = "APP_KYC_MODE", required = false)
                        private String APPKYCMODE;
                        @Element(name = "APP_SIGN_FLAG", required = false)
                        private String APPSIGNFLAG;
                        @Element(name = "APP_IPV_FLAG", required = false)
                        private String APPIPVFLAG;
                        @Element(name = "CAMSKRA", required = false)
                        private String CAMSKRA;
                        @Element(name = "CVLKRA", required = false)
                        private String CVLKRA;
                        @Element(name = "NDMLKRA", required = false)
                        private String NDMLKRA;
                        @Element(name = "DOTEXKRA", required = false)
                        private String DOTEXKRA;
                        @Element(name = "KARVYKRA", required = false)
                        private String KARVYKRA;

                        public String getAPPPANNO() {
                            return APPPANNO;
                        }

                        public void setAPPPANNO(String APPPANNO) {
                            this.APPPANNO = APPPANNO;
                        }

                        public String getAPPNAME() {
                            return APPNAME;
                        }

                        public void setAPPNAME(String APPNAME) {
                            this.APPNAME = APPNAME;
                        }

                        public String getAPPSTATUSDT() {
                            return APPSTATUSDT;
                        }

                        public void setAPPSTATUSDT(String APPSTATUSDT) {
                            this.APPSTATUSDT = APPSTATUSDT;
                        }

                        public String getAPPENTRYDT() {
                            return APPENTRYDT;
                        }

                        public void setAPPENTRYDT(String APPENTRYDT) {
                            this.APPENTRYDT = APPENTRYDT;
                        }

                        public String getAPPMODDT() {
                            return APPMODDT;
                        }

                        public void setAPPMODDT(String APPMODDT) {
                            this.APPMODDT = APPMODDT;
                        }

                        public String getAPPUPDTSTATUS() {
                            return APPUPDTSTATUS;
                        }

                        public void setAPPUPDTSTATUS(String APPUPDTSTATUS) {
                            this.APPUPDTSTATUS = APPUPDTSTATUS;
                        }

                        public String getAPPHOLDDEACTIVERMKS() {
                            return APPHOLDDEACTIVERMKS;
                        }

                        public void setAPPHOLDDEACTIVERMKS(String APPHOLDDEACTIVERMKS) {
                            this.APPHOLDDEACTIVERMKS = APPHOLDDEACTIVERMKS;
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

                        public String getAPPIPVFLAG() {
                            return APPIPVFLAG;
                        }

                        public void setAPPIPVFLAG(String APPIPVFLAG) {
                            this.APPIPVFLAG = APPIPVFLAG;
                        }

                        public String getCAMSKRA() {
                            return CAMSKRA;
                        }

                        public void setCAMSKRA(String CAMSKRA) {
                            this.CAMSKRA = CAMSKRA;
                        }

                        public String getCVLKRA() {
                            return CVLKRA;
                        }

                        public void setCVLKRA(String CVLKRA) {
                            this.CVLKRA = CVLKRA;
                        }

                        public String getNDMLKRA() {
                            return NDMLKRA;
                        }

                        public void setNDMLKRA(String NDMLKRA) {
                            this.NDMLKRA = NDMLKRA;
                        }

                        public String getDOTEXKRA() {
                            return DOTEXKRA;
                        }

                        public void setDOTEXKRA(String DOTEXKRA) {
                            this.DOTEXKRA = DOTEXKRA;
                        }

                        public String getKARVYKRA() {
                            return KARVYKRA;
                        }

                        public void setKARVYKRA(String KARVYKRA) {
                            this.KARVYKRA = KARVYKRA;
                        }
                    }
                }
            }
        }
    }
}

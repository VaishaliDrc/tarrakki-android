package com.tarrakki.api.soapmodel;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@Root(name = "soap12:Envelope")
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema", prefix = "xsd"),
        @Namespace(prefix = "soap12", reference = "http://www.w3.org/2003/05/soap-envelope")
})
public class RequestEnvelopeDownloadPANDetailsEKYC {

    @Element(name = "soap12:Body")
    private RequestBody requestBody;

    public static class RequestBody {

        @Namespace(reference = "https://camskra.com/")
        //@Element(name = "DownloadPANDetails_eKYC")
        @Element(name = "DownloadPANDetails_eKYC")
        private DownloadPANDetailsEKYC ekyc;

        public RequestBody() {
        }

        public static class DownloadPANDetailsEKYC {

            /*@Element(name = "InputXML")
                        /**
             * APP_REQ_ROOT : {"APP_PAN_INQ":{"APP_PAN_NO":"BAMPM9343K","APP_IOP_FLG":"RS","APP_POS_CODE":"infibeam$10"},"APP_SUMM_REC":{"APP_OTHKRA_CODE":"PLUTOWS","APP_OTHKRA_BATCH":"SAU_468","APP_REQ_DATE":"08-02-2019 15:10:23","APP_TOTAL_REC":"1"}}
             */

            @Element(name = "InputXML")
            private InputXML input;
            @Element(name = "USERNAME")
            private String userName;
            @Element(name = "POSCODE")
            private String PosCode;
            @Element(name = "PASSWORD")
            private String password;
            @Element(name = "PASSKEY")
            private String passKey;

            public InputXML getInput() {
                return input;
            }

            public DownloadPANDetailsEKYC setInput(InputXML input) {
                this.input = input;
                return this;
            }

            public String getUserName() {
                return userName;
            }

            public DownloadPANDetailsEKYC setUserName(String userName) {
                this.userName = userName;
                return this;
            }

            public String getPosCode() {
                return PosCode;
            }

            public DownloadPANDetailsEKYC setPosCode(String posCode) {
                PosCode = posCode;
                return this;
            }

            public String getPassword() {
                return password;
            }

            public DownloadPANDetailsEKYC setPassword(String password) {
                this.password = password;
                return this;
            }

            public String getPassKey() {
                return passKey;
            }

            public DownloadPANDetailsEKYC setPassKey(String passKey) {
                this.passKey = passKey;
                return this;
            }

            public static class APPREQROOTBean {
                /**
                 * APP_PAN_INQ : {"APP_PAN_NO":"BAMPM9343K","APP_IOP_FLG":"RS","APP_POS_CODE":"infibeam$10"}
                 * APP_SUMM_REC : {"APP_OTHKRA_CODE":"PLUTOWS","APP_OTHKRA_BATCH":"SAU_468","APP_REQ_DATE":"08-02-2019 15:10:23","APP_TOTAL_REC":"1"}
                 */

                @Element(name = "APP_PAN_INQ")
                private APPPANINQBean APPPANINQ;
                @Element(name = "APP_SUMM_REC")
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
                     * APP_PAN_NO : BAMPM9343K
                     * APP_IOP_FLG : RS
                     * APP_POS_CODE : infibeam$10
                     */

                    @Element(name = "APP_PAN_NO")
                    private String APPPANNO;
                    @Element(name = "APP_IOP_FLG")
                    private String APPIOPFLG;
                    @Element(name = "APP_POS_CODE")
                    private String APPPOSCODE;
                    @Element(name = "APP_PAN_DOB")
                    private String panDOB;


                    public String getAPPPANNO() {
                        return APPPANNO;
                    }

                    public void setAPPPANNO(String APPPANNO) {
                        this.APPPANNO = APPPANNO;
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

                    public String getPanDOB() {
                        return panDOB;
                    }

                    public APPPANINQBean setPanDOB(String panDOB) {
                        this.panDOB = panDOB;
                        return this;
                    }
                }

                public static class APPSUMMRECBean {
                    /**
                     * APP_OTHKRA_CODE : PLUTOWS
                     * APP_OTHKRA_BATCH : SAU_468
                     * APP_REQ_DATE : 08-02-2019 15:10:23
                     * APP_TOTAL_REC : 1
                     */

                    @Element(name = "APP_OTHKRA_CODE")
                    private String APPOTHKRACODE;
                    @Element(name = "APP_OTHKRA_BATCH")
                    private String APPOTHKRABATCH;
                    @Element(name = "APP_REQ_DATE")
                    private String APPREQDATE;
                    @Element(name = "APP_TOTAL_REC")
                    private String APPTOTALREC;

                    public String getAPPOTHKRACODE() {
                        return APPOTHKRACODE;
                    }

                    public void setAPPOTHKRACODE(String APPOTHKRACODE) {
                        this.APPOTHKRACODE = APPOTHKRACODE;
                    }

                    public String getAPPOTHKRABATCH() {
                        return APPOTHKRABATCH;
                    }

                    public void setAPPOTHKRABATCH(String APPOTHKRABATCH) {
                        this.APPOTHKRABATCH = APPOTHKRABATCH;
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
                }
            }

            public static class InputXML {

                @Element(name = "APP_REQ_ROOT")
                private APPREQROOTBean input;

                public InputXML() {
                }

                public InputXML(APPREQROOTBean input) {
                    this.input = input;
                }

                public APPREQROOTBean getInput() {
                    return input;
                }

                public InputXML setInput(APPREQROOTBean input) {
                    this.input = input;
                    return this;
                }
            }


        }

        public DownloadPANDetailsEKYC getEkyc() {
            return ekyc;
        }

        public RequestBody setEkyc(DownloadPANDetailsEKYC ekyc) {
            this.ekyc = ekyc;
            return this;
        }
    }

    public RequestEnvelopeDownloadPANDetailsEKYC(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public RequestEnvelopeDownloadPANDetailsEKYC() {
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public RequestEnvelopeDownloadPANDetailsEKYC setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }
}



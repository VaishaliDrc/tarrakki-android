package com.tarrakki.api.soapmodel;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;

public class PasswordResponse {

    @Namespace(reference = "https://camskra.com/")
    @Element(name = "GetPasswordResult")
    public GetPasswordResponse getPassword;

    public static class GetPasswordResponse {

        @Element(name = "GetPasswordResult", required = false)
        String getPasswordResult;

        public String getGetPasswordResult() {
            return getPasswordResult;
        }

        public GetPasswordResponse setGetPasswordResult(String getPasswordResult) {
            this.getPasswordResult = getPasswordResult;
            return this;
        }
    }
}

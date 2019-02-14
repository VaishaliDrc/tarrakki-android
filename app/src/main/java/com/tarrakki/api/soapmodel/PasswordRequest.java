package com.tarrakki.api.soapmodel;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;

public class PasswordRequest {

    @Namespace(reference = "https://camskra.com/")
    @Element(name = "GetPassword", required = false)
    public GetPassword getPassword;

    public PasswordRequest() {
    }

    public PasswordRequest(GetPassword getPassword) {
        this.getPassword = getPassword;
    }

    public static class GetPassword {

        public GetPassword() {
        }

        public GetPassword(String password, String passkey) {
            this.password = password;
            this.passkey = passkey;
        }

        @Element(name = "PASSWORD", required = false)
        public String password;
        @Element(name = "PASSKEY", required = false)
        String passkey = "";

        public String getPassword() {
            return password;
        }

        public GetPassword setPassword(String password) {
            this.password = password;
            return this;
        }

        public String getPasskey() {
            return passkey;
        }

        public GetPassword setPasskey(String passkey) {
            this.passkey = passkey;
            return this;
        }
    }

    public GetPassword getGetPassword() {
        return getPassword;
    }

    public PasswordRequest setGetPassword(GetPassword getPassword) {
        this.getPassword = getPassword;
        return this;
    }
}

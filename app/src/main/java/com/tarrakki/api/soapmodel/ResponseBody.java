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
public class ResponseBody {

    @Element(name = "Body")
    private ResBody resBody;

    public ResponseBody() {
    }

    public ResponseBody(ResBody resBody) {
        this.resBody = resBody;
    }

    public ResBody getResBody() {
        return resBody;
    }

    public ResponseBody setResBody(ResBody resBody) {
        this.resBody = resBody;
        return this;
    }

    public static class ResBody {

        @Namespace(reference = "https://camskra.com/")
        @Element(name = "GetPasswordResponse")
        private GetPasswordResponse response;

        public ResBody() {
        }

        public ResBody(GetPasswordResponse response) {
            this.response = response;
        }

        public GetPasswordResponse getResponse() {
            return response;
        }

        public ResBody setResponse(GetPasswordResponse response) {
            this.response = response;
            return this;
        }

        public static class GetPasswordResponse {

            @Element(name = "GetPasswordResult")
            private String getPasswordResult;

            public GetPasswordResponse() {
            }

            public GetPasswordResponse(String getPasswordResult) {
                this.getPasswordResult = getPasswordResult;
            }

            public String getGetPasswordResult() {
                return getPasswordResult;
            }

            public GetPasswordResponse setGetPasswordResult(String getPasswordResult) {
                this.getPasswordResult = getPasswordResult;
                return this;
            }
        }
    }

}

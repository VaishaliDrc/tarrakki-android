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
public class RequestBody {

    @Element(name = "soap12:Body", required = false)
    private PasswordRequest object;

    public RequestBody() {
    }

    public RequestBody(PasswordRequest object) {
        this.object = object;
    }
}

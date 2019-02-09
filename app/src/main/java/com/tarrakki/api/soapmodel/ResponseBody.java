package com.tarrakki.api.soapmodel;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name = "soap:Envelope", strict = false)
public class ResponseBody {

    @Element(required = false)
    ResBody resBody;

    public ResBody getResBody() {
        return resBody;
    }

    public ResponseBody setResBody(ResBody resBody) {
        this.resBody = resBody;
        return this;
    }
}

@Element(name = "soap:Body")
class ResBody {

}

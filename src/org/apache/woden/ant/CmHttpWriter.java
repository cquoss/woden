/**
 * Copyright 2006 Apache Software Foundation 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package org.apache.woden.ant;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.woden.wsdl20.extensions.http.HTTPAuthenticationScheme;
import org.apache.woden.wsdl20.extensions.http.HTTPBindingExtensions;
import org.apache.woden.wsdl20.extensions.http.HTTPBindingFaultExtensions;
import org.apache.woden.wsdl20.extensions.http.HTTPBindingMessageReferenceExtensions;
import org.apache.woden.wsdl20.extensions.http.HTTPBindingOperationExtensions;
import org.apache.woden.wsdl20.extensions.http.HTTPEndpointExtensions;
import org.apache.woden.wsdl20.extensions.http.HTTPErrorStatusCode;
import org.apache.woden.wsdl20.extensions.http.HTTPHeader;

/**
 * @author Arthur Ryman (ryman@ca.ibm.com, arthur.ryman@gmail.com)
 *
 */
public class CmHttpWriter extends NamespaceWriter {

    public final static String NS = "http://www.w3.org/2002/ws/desc/wsdl/component-http";

    public final static String PREFIX = "cmhttp";

    private CmBaseWriter cmbase;

    public CmHttpWriter(XMLWriter out) {

        super(out, NS, PREFIX);

        cmbase = (CmBaseWriter) out.lookup(CmBaseWriter.NS);
    }

    public void httpBindingExtension(HTTPBindingExtensions http) {

        if (http == null)
            return;

        out.beginElement(PREFIX + ":httpBindingExtension");

        out.write(PREFIX + ":httpCookies", http.isHttpCookies());
        out.write(PREFIX + ":httpMethodDefault", http.getHttpMethodDefault());
        out.write(PREFIX + ":httpQueryParameterSeparatorDefault", http
                .getHttpQueryParameterSeparatorDefault());
        out.write(PREFIX + ":httpTransferCodingDefault", http
                .getHttpTransferCodingDefault());

        out.endElement();
    }

    public void httpBindingFaultExtension(HTTPBindingFaultExtensions http) {

        if (http != null) {

            out.beginElement(PREFIX + ":httpBindingFaultExtension");

            write(PREFIX + ":httpErrorStatusCode", http
                    .getHttpErrorStatusCode());
            write(PREFIX + ":httpHeaders", http.getHttpHeaders());
            out.write(PREFIX + ":httpTransferCoding", http
                    .getHttpTransferCoding());

            out.endElement();
        }
    }

    public void httpBindingOperationExtension(
            HTTPBindingOperationExtensions http) {

        if (http == null)
            return;
        out.beginElement(PREFIX + ":httpBindingOperationExtension");

        out.write(PREFIX + ":httpFaultSerialization", http
                .getHttpFaultSerialization());
        out.write(PREFIX + ":httpInputSerialization", http
                .getHttpInputSerialization());
        cmbase.write(PREFIX + ":httpLocation", http.getHttpLocation());
        out.write(PREFIX + ":httpLocationIgnoreUncited", http
                .isHttpLocationIgnoreUncited());
        out.write(PREFIX + ":httpMethod", http.getHttpMethod());
        out.write(PREFIX + ":httpOutputSerialization", http
                .getHttpOutputSerialization());
        out.write(PREFIX + ":httpQueryParameterSeparator", http
                .getHttpQueryParameterSeparator());
        out.write(PREFIX + ":httpTransferCodingDefault", http
                .getHttpTransferCodingDefault());

        out.endElement();

    }

    public void httpBindingMessageReferenceExtension(
            HTTPBindingMessageReferenceExtensions http) {

        if (http == null)
            return;

        out.beginElement(PREFIX + ":httpBindingMessageReferenceExtension");

        write(PREFIX + ":httpHeaders", http.getHttpHeaders());
        out.write(PREFIX + ":httpTransferCoding", http.getHttpTransferCoding());

        out.endElement();

    }

    public void httpEndpointExtension(HTTPEndpointExtensions http) {

        if (http == null)
            return;

        out.beginElement(PREFIX + ":httpEndpointExtension");

        out.write(PREFIX + ":httpAuthenticationRealm", http
                .getHttpAuthenticationRealm());
        write(PREFIX + ":httpAuthenticationScheme", http
                .getHttpAuthenicationScheme());

        out.endElement();
    }

    private void write(String tag, HTTPErrorStatusCode httpErrorStatusCode) {

        if (httpErrorStatusCode == null)
            return;

        out.beginElement(tag);

        if (httpErrorStatusCode.isCodeUsed()) {

            out.write(PREFIX + ":code", httpErrorStatusCode.toString());
        }

        out.endElement();
    }

    private void write(String tag, HTTPHeader[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {

                String n1 = ((HTTPHeader) o1).getName();
                String n2 = ((HTTPHeader) o2).getName();

                return n1.compareTo(n2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++)
            write(PREFIX + ":HttpHeaderComponent", components[i]);

        out.endElement();
    }

    private void write(String tag, HTTPHeader component) {

        out.beginElement(tag, CmBaseWriter.idAttribute(component));

        out.write(PREFIX + ":name", component.getName());

        cmbase.writeRef(PREFIX + ":typeDefinition", component
                .getTypeDefinition());

        out.write(PREFIX + ":required", component.isRequired());

        cmbase.parent(component.getParent());

        out.endElement();
    }

    private void write(String tag, HTTPAuthenticationScheme scheme) {

        if (scheme == null)
            return;

        out.write(tag, scheme.toString());
    }

}

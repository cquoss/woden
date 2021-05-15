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
package org.apache.woden.internal.wsdl20.extensions.http;

import org.apache.woden.internal.wsdl20.extensions.ComponentExtensionsImpl;
import org.apache.woden.wsdl20.extensions.http.HTTPBindingExtensions;
import org.apache.woden.wsdl20.xml.WSDLElement;
import org.apache.woden.xml.BooleanAttr;
import org.apache.woden.xml.StringAttr;

/**
 * This class defines the properties from the HTTP namespace
 * added to the WSDL <code>Binding</code> component as part 
 * of the HTTP binding extension defined by the WSDL 2.0 spec. 
 * 
 * @author John Kaputin (jkaputin@apache.org)
 */
public class HTTPBindingExtensionsImpl extends ComponentExtensionsImpl
                                       implements HTTPBindingExtensions 
{

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.http.HTTPBindingExtensions#getHttpMethodDefault()
     */
    public String getHttpMethodDefault() 
    {
        StringAttr methodDef = (StringAttr) ((WSDLElement)fParent)
            .getExtensionAttribute(HTTPConstants.Q_ATTR_METHOD_DEFAULT);
        return methodDef != null ? methodDef.getString() : null;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.http.HTTPBindingExtensions#getHttpQueryParameterSeparatorDefault()
     */
    public String getHttpQueryParameterSeparatorDefault() 
    {
        //TODO monitor ws-desc proposal 19May06 on changing handling of defaults in spec Part 2
        StringAttr qpsDef = (StringAttr) ((WSDLElement)fParent)
            .getExtensionAttribute(HTTPConstants.Q_ATTR_QUERY_PARAMETER_SEPARATOR_DEFAULT);
        return qpsDef != null ? qpsDef.getString() : HTTPConstants.QUERY_SEP_AMPERSAND;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.http.HTTPBindingExtensions#isHttpCookies()
     */
    public Boolean isHttpCookies() 
    {
        BooleanAttr cookiesUsed = (BooleanAttr) ((WSDLElement)fParent)
            .getExtensionAttribute(HTTPConstants.Q_ATTR_COOKIES);
        return cookiesUsed != null ? cookiesUsed.getBoolean() : new Boolean(false); //defaults to false if omitted
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.http.HTTPBindingExtensions#getHttpTransferCodingDefault()
     */
    public String getHttpTransferCodingDefault() 
    {
        StringAttr tcDef = (StringAttr) ((WSDLElement)fParent)
            .getExtensionAttribute(HTTPConstants.Q_ATTR_TRANSFER_CODING_DEFAULT);
        return tcDef != null ? tcDef.getString() : null;
    }

}

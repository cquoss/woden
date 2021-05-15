/**
 * Copyright 2005 Apache Software Foundation 
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
package org.apache.woden.internal.wsdl20.extensions.soap;

import javax.xml.namespace.QName;

/**
 * @author jkaputin@apache.org
 */
public class SOAPConstants 
{
    // Namespace URIs.
    public static final String NS_URI_SOAP =
        "http://www.w3.org/2006/01/wsdl/soap";
    
    // Protocol URIs.
    public static final String PROTOCOL_URI_HTTP =
        "http://www.w3.org/2003/05/soap/bindings/HTTP/";

    // Element names.
    public static final String ELEM_MODULE = "module";
    public static final String ELEM_HEADER = "header";

    // Attribute names.
    public static final String ATTR_VERSION = "version";
    public static final String ATTR_PROTOCOL = "protocol";
    public static final String ATTR_MEPDEFAULT = "mepDefault";
    public static final String ATTR_CODE = "code";
    public static final String ATTR_SUBCODES = "subcodes";
    public static final String ATTR_MEP = "mep";
    public static final String ATTR_ACTION = "action";
    public static final String ATTR_MUSTUNDERSTAND = "mustUnderstand";
    
    // Prefixes
    public static final String PFX_WSOAP = "wsoap";

    // Qualified element names.
    
    public static final QName Q_ELEM_SOAP_MODULE =
      new QName(NS_URI_SOAP, ELEM_MODULE, PFX_WSOAP);
    
    public static final QName Q_ELEM_SOAP_HEADER =
        new QName(NS_URI_SOAP, ELEM_HEADER, PFX_WSOAP);

    // Qualified attribute names.
    
    public static final QName Q_ATTR_SOAP_VERSION =
      new QName(NS_URI_SOAP, ATTR_VERSION, PFX_WSOAP);
    
    public static final QName Q_ATTR_SOAP_PROTOCOL =
        new QName(NS_URI_SOAP, ATTR_PROTOCOL, PFX_WSOAP);
    
    public static final QName Q_ATTR_SOAP_MEPDEFAULT =
        new QName(NS_URI_SOAP, ATTR_MEPDEFAULT, PFX_WSOAP);
    
    public static final QName Q_ATTR_SOAP_CODE =
        new QName(NS_URI_SOAP, ATTR_CODE, PFX_WSOAP);
    
    public static final QName Q_ATTR_SOAP_SUBCODES =
        new QName(NS_URI_SOAP, ATTR_SUBCODES, PFX_WSOAP);
    
    public static final QName Q_ATTR_SOAP_MEP =
        new QName(NS_URI_SOAP, ATTR_MEP, PFX_WSOAP);
    
    public static final QName Q_ATTR_SOAP_ACTION =
        new QName(NS_URI_SOAP, ATTR_ACTION, PFX_WSOAP);

}

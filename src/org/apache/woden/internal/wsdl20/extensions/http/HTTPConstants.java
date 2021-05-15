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

import java.net.URI;

import javax.xml.namespace.QName;

/**
 * @author John Kaputin (jkaputin@apache.org)
 */
public class HTTPConstants 
{
    // Namespace URIs.
    public static final String NS_STRING_HTTP =
        "http://www.w3.org/2006/01/wsdl/http";
    
    public static final URI NS_URI_HTTP = URI.create(NS_STRING_HTTP);
    
    // Element names.
    public static final String ELEM_HEADER = "header";

    // Attribute names.
    public static final String ATTR_AUTHENTICATION_REALM = "authenticationRealm";
    public static final String ATTR_AUTHENTICATION_TYPE = "authenticationType";
    public static final String ATTR_CODE = "code";
    public static final String ATTR_COOKIES = "cookies";
    public static final String ATTR_FAULT_SERIALIZATION = "faultSerialization";
    public static final String ATTR_IGNORE_UNCITED = "ignoreUncited";
    public static final String ATTR_INPUT_SERIALIZATION = "inputSerialization";
    public static final String ATTR_LOCATION = "location";
    public static final String ATTR_METHOD = "method";
    public static final String ATTR_METHOD_DEFAULT = "methodDefault";
    public static final String ATTR_OUTPUT_SERIALIZATION = "outputSerialization";
    public static final String ATTR_QUERY_PARAMETER_SEPARATOR = "queryParameterSeparator";
    public static final String ATTR_QUERY_PARAMETER_SEPARATOR_DEFAULT = "queryParameterSeparatorDefault";
    public static final String ATTR_TRANSFER_CODING = "transferCoding";
    public static final String ATTR_TRANSFER_CODING_DEFAULT = "transferCodingDefault";
    
    // Prefixes
    public static final String PFX_WHTTP = "whttp";

    // Qualified element names.
    
    public static final QName Q_ELEM_HTTP_HEADER =
        new QName(NS_STRING_HTTP, ELEM_HEADER, PFX_WHTTP);

    // Qualified attribute names.
    
    public static final QName Q_ATTR_AUTHENTICATION_REALM =
        new QName(NS_STRING_HTTP, ATTR_AUTHENTICATION_REALM, PFX_WHTTP);
      
    public static final QName Q_ATTR_AUTHENTICATION_TYPE =
        new QName(NS_STRING_HTTP, ATTR_AUTHENTICATION_TYPE, PFX_WHTTP);
      
    public static final QName Q_ATTR_CODE =
        new QName(NS_STRING_HTTP, ATTR_CODE, PFX_WHTTP);
      
    public static final QName Q_ATTR_COOKIES =
        new QName(NS_STRING_HTTP, ATTR_COOKIES, PFX_WHTTP);
      
    public static final QName Q_ATTR_FAULT_SERIALIZATION =
        new QName(NS_STRING_HTTP, ATTR_FAULT_SERIALIZATION, PFX_WHTTP);
      
    public static final QName Q_ATTR_IGNORE_UNCITED =
        new QName(NS_STRING_HTTP, ATTR_IGNORE_UNCITED, PFX_WHTTP);
      
    public static final QName Q_ATTR_INPUT_SERIALIZATION =
        new QName(NS_STRING_HTTP, ATTR_INPUT_SERIALIZATION, PFX_WHTTP);
      
    public static final QName Q_ATTR_LOCATION =
        new QName(NS_STRING_HTTP, ATTR_LOCATION, PFX_WHTTP);
      
    public static final QName Q_ATTR_METHOD =
        new QName(NS_STRING_HTTP, ATTR_METHOD, PFX_WHTTP);
      
    public static final QName Q_ATTR_METHOD_DEFAULT =
        new QName(NS_STRING_HTTP, ATTR_METHOD_DEFAULT, PFX_WHTTP);
      
    public static final QName Q_ATTR_OUTPUT_SERIALIZATION =
        new QName(NS_STRING_HTTP, ATTR_OUTPUT_SERIALIZATION, PFX_WHTTP);
      
    public static final QName Q_ATTR_QUERY_PARAMETER_SEPARATOR =
        new QName(NS_STRING_HTTP, ATTR_QUERY_PARAMETER_SEPARATOR, PFX_WHTTP);
      
    public static final QName Q_ATTR_QUERY_PARAMETER_SEPARATOR_DEFAULT =
        new QName(NS_STRING_HTTP, ATTR_QUERY_PARAMETER_SEPARATOR_DEFAULT, PFX_WHTTP);
      
    public static final QName Q_ATTR_TRANSFER_CODING =
        new QName(NS_STRING_HTTP, ATTR_TRANSFER_CODING, PFX_WHTTP);
      
    public static final QName Q_ATTR_TRANSFER_CODING_DEFAULT =
        new QName(NS_STRING_HTTP, ATTR_TRANSFER_CODING_DEFAULT, PFX_WHTTP);
    
    //{http method} constants
    
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";
    
    
    //{input/output serialization} constants
    
    public static final String SERIAL_APP_URLENCODED = "application/x-www-form-urlencoded";
    public static final String SERIAL_APP_XML = "application/xml";

    public static final String QUERY_SEP_AMPERSAND = "&";

}

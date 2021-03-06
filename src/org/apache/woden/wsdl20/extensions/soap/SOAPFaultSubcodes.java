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
package org.apache.woden.wsdl20.extensions.soap;

import javax.xml.namespace.QName;

/**
 * This class represents the {soap fault subcodes} property that forms part of the 
 * SOAP extensions to the WSDL <code>BindingFault</code> component.
 * This property may contain either a list of QNames representing the subcodes or 
 * the xs:token #any.
 * <p>
 * This class will restrict the possible values to a collection of QNames or the string "#any".
 * It provides methods to query whether the property contains QNames or a token and 
 * methods to retrieve the property value of the appropriate type.
 * 
 * @author jkaputin@apache.org
 */
public class SOAPFaultSubcodes 
{
    private final String fToken;
    private final QName[] fSubcodeQNs;
    public static final SOAPFaultSubcodes ANY = new SOAPFaultSubcodes("#any");
    
    private SOAPFaultSubcodes(String token) 
    { 
        fToken = token; 
        fSubcodeQNs = new QName[0];
    }
    
    public SOAPFaultSubcodes(QName[] subcodeQNs) 
    { 
        fToken = null; 
        fSubcodeQNs = subcodeQNs;
    }
    
    public boolean isQNames() {return fSubcodeQNs.length > 0;}
    
    public boolean isToken() {return fToken != null;}
    
    public QName[] getQNames() {return fSubcodeQNs;}
    
    public String getToken() {return fToken;}
    
}

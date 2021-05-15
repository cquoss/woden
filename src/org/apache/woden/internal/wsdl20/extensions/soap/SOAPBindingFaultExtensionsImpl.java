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
package org.apache.woden.internal.wsdl20.extensions.soap;

import org.apache.woden.internal.wsdl20.extensions.ComponentExtensionsImpl;
import org.apache.woden.internal.xml.QNameListOrTokenAnyAttrImpl;
import org.apache.woden.internal.xml.QNameOrTokenAnyAttrImpl;
import org.apache.woden.wsdl20.extensions.ExtensionElement;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingFaultExtensions;
import org.apache.woden.wsdl20.extensions.soap.SOAPFaultCode;
import org.apache.woden.wsdl20.extensions.soap.SOAPFaultSubcodes;
import org.apache.woden.wsdl20.extensions.soap.SOAPHeaderBlock;
import org.apache.woden.wsdl20.extensions.soap.SOAPModule;

/**
 * This class defines the properties from the SOAP namespace
 * added to the WSDL <code>BindingFault</code> component as part 
 * of the SOAP binding extension defined by the WSDL 2.0 spec. 
 * 
 * @author John Kaputin (jkaputin@apache.org)
 */
public class SOAPBindingFaultExtensionsImpl extends ComponentExtensionsImpl
                                            implements SOAPBindingFaultExtensions 
{

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.soap.SOAPBindingFaultExtensions#getSoapFaultCode()
     */
    public SOAPFaultCode getSoapFaultCode() 
    {
        /* QNameOrTokenAnyAttrImpl is the class registered for this extension attribute. Use this type 
         * here, rather than the QNameOrTokenAttr interface, to guarantee that if the code contains an 
         * xs:token it is of type #any.
         */
        QNameOrTokenAnyAttrImpl code = 
            (QNameOrTokenAnyAttrImpl)fParentElement.getExtensionAttribute(SOAPConstants.Q_ATTR_SOAP_CODE);
        
        if(code == null)
        {
            //defaults to xs:token #any if the attribute is omitted from the WSDL.
            return SOAPFaultCode.ANY;
        }

        if(code.isToken()) {
            return SOAPFaultCode.ANY;
        }
        
        if(code.isQName())
        {
            return new SOAPFaultCode(code.getQName());
        }
        else
        {
            //the wsoap:code attribute contains an invalid value (i.e. not an xs:QName or the xs:token #any)
            //TODO confirm if this should be represented in the Component model as a null
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.soap.SOAPBindingFaultExtensions#getSoapFaultSubcodes()
     */
    public SOAPFaultSubcodes getSoapFaultSubcodes() 
    {
        /* QNameListOrTokenAnyAttrImpl is the class registered for this extension attribute. Use this type 
         * here, rather than the QNameListOrTokenAttr interface, to gaurantee that if the code contains an 
         * xs:token it is of type #any.
         */
        QNameListOrTokenAnyAttrImpl subcodes = 
            (QNameListOrTokenAnyAttrImpl)fParentElement.getExtensionAttribute(SOAPConstants.Q_ATTR_SOAP_SUBCODES);
        
        if(subcodes == null)
        {
            //defaults to xs:token #any if the attribute is omitted from the WSDL.
            return SOAPFaultSubcodes.ANY;
        }

        if(subcodes.isToken()) {
            return SOAPFaultSubcodes.ANY;
        }
        
        if(subcodes.isQNameList())
        {
            return new SOAPFaultSubcodes(subcodes.getQNames());
        }
        else
        {
            //the wsoap:subcodes attribute contains an invalid value (i.e. not a list of xs:QName or the xs:token #any)
            //TODO confirm if this should be represented in the Component model as a null
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.soap.SOAPBindingFaultExtensions#getSoapModules()
     */
    public SOAPModule[] getSoapModules() 
    {
        ExtensionElement[] extEls = fParentElement.getExtensionElementsOfType(SOAPConstants.Q_ELEM_SOAP_MODULE);
        int len = extEls.length;
        SOAPModule[] soapMods = new SOAPModule[len];
        System.arraycopy(extEls, 0, soapMods, 0, len);
        return soapMods;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.soap.SOAPBindingFaultExtensions#getSoapHeaders()
     */
    public SOAPHeaderBlock[] getSoapHeaders() 
    {
        ExtensionElement[] extEls = fParentElement.getExtensionElementsOfType(SOAPConstants.Q_ELEM_SOAP_HEADER);
        int len = extEls.length;
        SOAPHeaderBlock[] soapHeaders = new SOAPHeaderBlock[len];
        System.arraycopy(extEls, 0, soapHeaders, 0, len);
        return soapHeaders;
    }

}

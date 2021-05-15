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

import java.net.URI;

import org.apache.woden.internal.wsdl20.extensions.ComponentExtensionsImpl;
import org.apache.woden.wsdl20.extensions.ExtensionElement;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingOperationExtensions;
import org.apache.woden.wsdl20.extensions.soap.SOAPModule;
import org.apache.woden.xml.URIAttr;

/**
 * This class defines the properties from the SOAP namespace
 * added to the WSDL <code>BindingOperation</code> component as part 
 * of the SOAP binding extension defined by the WSDL 2.0 spec. 
 * 
 * @author jkaputin@apache.org
 */
public class SOAPBindingOperationExtensionsImpl extends ComponentExtensionsImpl
                                                implements SOAPBindingOperationExtensions 
{

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.soap.SOAPBindingOperationExtensions#getSoapMep()
     */
    public URI getSoapMep() 
    {
        URIAttr mep = 
            (URIAttr)fParentElement.getExtensionAttribute(SOAPConstants.Q_ATTR_SOAP_MEP);
        return mep != null ? mep.getURI() : null;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.soap.SOAPBindingOperationExtensions#getSoapAction()
     */
    public URI getSoapAction() 
    {
        URIAttr action = 
            (URIAttr)fParentElement.getExtensionAttribute(SOAPConstants.Q_ATTR_SOAP_ACTION);
        return action != null ? action.getURI() : null;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.soap.SOAPBindingOperationExtensions#getSoapModules()
     */
    public SOAPModule[] getSoapModules() 
    {
        ExtensionElement[] extEls = fParentElement.getExtensionElementsOfType(SOAPConstants.Q_ELEM_SOAP_MODULE);
        int len = extEls.length;
        SOAPModule[] soapMods = new SOAPModule[len];
        System.arraycopy(extEls, 0, soapMods, 0, len);
        return soapMods;
    }

}

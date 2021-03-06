/**
 * Copyright 2005, 2006 Apache Software Foundation 
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

import org.apache.woden.ErrorReporter;
import org.apache.woden.WSDLException;
import org.apache.woden.internal.ErrorLocatorImpl;
import org.apache.woden.internal.util.dom.DOMUtils;
import org.apache.woden.internal.util.dom.QNameUtils;
import org.apache.woden.internal.wsdl20.Constants;
import org.apache.woden.wsdl20.extensions.ExtensionDeserializer;
import org.apache.woden.wsdl20.extensions.ExtensionElement;
import org.apache.woden.wsdl20.extensions.ExtensionRegistry;
import org.apache.woden.wsdl20.extensions.soap.SOAPHeaderBlockElement;
import org.apache.woden.wsdl20.xml.DescriptionElement;
import org.apache.woden.wsdl20.xml.DocumentableElement;
import org.apache.woden.wsdl20.xml.DocumentationElement;
import org.apache.woden.wsdl20.xml.WSDLElement;
import org.w3c.dom.Element;

/**
 * Deserializes the &lt;wsoap:header&gt; extension element into a SOAPHeaderBlockElement.
 * 
 * @author jkaputin@apache.org
 *
 */
public class SOAPHeaderBlockDeserializer implements ExtensionDeserializer 
{

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.ExtensionDeserializer#unmarshall(java.lang.Class, java.lang.Object, javax.xml.namespace.QName, org.w3c.dom.Element, org.apache.woden.wsdl20.xml.DescriptionElement, org.apache.woden.wsdl20.extensions.ExtensionRegistry)
     */
    public ExtensionElement unmarshall(Class parentType, 
                                       Object parent,
                                       QName extType, 
                                       Element extEl, 
                                       DescriptionElement desc,
                                       ExtensionRegistry extReg) 
                                       throws WSDLException 
    {
        SOAPHeaderBlockElement soapHdr = 
            (SOAPHeaderBlockElement) extReg.createExtElement(parentType, extType);
        
        soapHdr.setExtensionType(extType);
        soapHdr.setParentElement((WSDLElement)parent);
        
        String elemDeclQN = DOMUtils.getAttribute(extEl, Constants.ATTR_ELEMENT);
        if(elemDeclQN != null)
        {
            try {
                QName qname = DOMUtils.getQName(elemDeclQN, extEl, desc);
                soapHdr.setElementName(qname);
            } catch (WSDLException e) {
                extReg.getErrorReporter().reportError( 
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL505",
                        new Object[] {elemDeclQN, extEl.getLocalName()}, 
                        ErrorReporter.SEVERITY_ERROR);
            }
        }
        
        //This property defaults to 'false' if it is omitted.
        String mustUnderstand = DOMUtils.getAttribute(extEl, SOAPConstants.ATTR_MUSTUNDERSTAND);
        soapHdr.setMustUnderstand(new Boolean(mustUnderstand));

        //This property defaults to 'false' if it is omitted.
        String required = DOMUtils.getAttribute(extEl, Constants.ATTR_REQUIRED);
        soapHdr.setRequired(new Boolean(required));

        ((SOAPHeaderBlockImpl)soapHdr).setTypes(desc.getTypesElement());
        
        //TODO parseExtensionAttributes(el, SOAPHeaderBlockElement.class, soapHdr, desc);
        
        Element tempEl = DOMUtils.getFirstChildElement(extEl);

        while (tempEl != null)
        {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl))
            {
                soapHdr.addDocumentationElement(parseDocumentation(tempEl, desc));
            }
            else
            {
                //TODO parse ext elements
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        
        return soapHdr;
    }

    private DocumentationElement parseDocumentation(Element docEl, 
                                                    DocumentableElement parent) 
                                                    throws WSDLException
    {
        DocumentationElement documentation = parent.addDocumentationElement();
        documentation.setContent(docEl);
        
        //TODO parseExtensionAttributes(docEl, DocumentationElement.class, documentation, desc);
        //TODO parseExtensionElements(...)
        
        return documentation;
    }

}

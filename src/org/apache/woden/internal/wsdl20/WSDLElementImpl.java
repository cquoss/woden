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
package org.apache.woden.internal.wsdl20;

import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.woden.internal.wsdl20.extensions.AttributeExtensibleImpl;
import org.apache.woden.internal.wsdl20.extensions.ElementExtensibleImpl;
import org.apache.woden.wsdl20.extensions.ExtensionElement;
import org.apache.woden.wsdl20.xml.WSDLElement;
import org.apache.woden.xml.XMLAttr;

/**
 * This abstract class defines the behaviour common to all WSDL elements.
 * That is, it implements support for extension attributes and elements.
 * This interface can be used as a common reference for all WSDL elements
 * represented by the Element API.
 * 
 * @author jkaputin@apache.org
 */
public class WSDLElementImpl implements WSDLElement 
{
    private AttributeExtensibleImpl fAttrExt = new AttributeExtensibleImpl();
    private ElementExtensibleImpl fElemExt = new ElementExtensibleImpl();
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.AttributeExtensible#setExtensionAttribute(javax.xml.namespace.QName, org.apache.woden.xml.XMLAttr)
     */
    public void setExtensionAttribute(QName attrType, XMLAttr attr) 
    {
        fAttrExt.setExtensionAttribute(attrType, attr);
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.AttributeExtensible#getExtensionAttribute(javax.xml.namespace.QName)
     */
    public XMLAttr getExtensionAttribute(QName attrType) 
    {
        return fAttrExt.getExtensionAttribute(attrType);
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.AttributeExtensible#getExtensionAttributesForNamespace(java.net.URI)
     */
    public XMLAttr[] getExtensionAttributesForNamespace(URI namespace) 
    {
        return fAttrExt.getExtensionAttributesForNamespace(namespace);
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.AttributeExtensible#getExtensionAttributes()
     */
    public XMLAttr[] getExtensionAttributes() 
    {
        return fAttrExt.getExtensionAttributes();
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.AttributeExtensible#hasExtensionAttributesForNamespace(java.net.URI)
     */
    public boolean hasExtensionAttributesForNamespace(URI namespace) 
    {
        return fAttrExt.hasExtensionAttributesForNamespace(namespace);
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.ElementExtensible#addExtensionElement(org.apache.woden.wsdl20.extensions.ExtensionElement)
     */
    public void addExtensionElement(ExtensionElement extEl) 
    {
        fElemExt.addExtensionElement(extEl);
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.ElementExtensible#removeExtensionElement(org.apache.woden.wsdl20.extensions.ExtensionElement)
     */
    public void removeExtensionElement(ExtensionElement extEl) 
    {
        fElemExt.removeExtensionElement(extEl);
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.ElementExtensible#getExtensionElements()
     */
    public ExtensionElement[] getExtensionElements() 
    {
        return fElemExt.getExtensionElements();
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.ElementExtensible#getExtensionElementsOfType(javax.xml.namespace.QName)
     */
    public ExtensionElement[] getExtensionElementsOfType(QName elemType) 
    {
        return fElemExt.getExtensionElementsOfType(elemType);
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.ElementExtensible#hasExtensionElementsForNamespace(java.net.URI)
     */
    public boolean hasExtensionElementsForNamespace(URI namespace) 
    {
        return fElemExt.hasExtensionElementsForNamespace(namespace);
    }

}

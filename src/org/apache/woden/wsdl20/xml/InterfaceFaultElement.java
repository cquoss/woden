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
package org.apache.woden.wsdl20.xml;

import javax.xml.namespace.QName;

import org.apache.woden.types.NCName;
import org.apache.ws.commons.schema.XmlSchemaElement;

/**
 * This interface represents a &lt;fault&gt; child element of the
 * WSDL &lt;interface&gt; element. 
 * It declares the behaviour required to support parsing, 
 * creating and manipulating a &lt;fault&gt; element.
 * 
 * @author jkaputin@apache.org
 */
public interface InterfaceFaultElement extends DocumentableElement, 
                                               ConfigurableElement,
                                               NestedElement
{
    /**
     * Set the QName that represens the 'name' attribute of the interface 
     * &lt;fault&gt; element. This identifies the interface fault.
     * 
     * @param name the NCName that identifies the interface fault
     */
    public void setName(NCName name);
    public QName getName();
    
    /**
     * Set the QName that represents the 'element' attribute of the interface 
     * &lt;fault&gt; element. This identifies a Schema element declaration.
     * 
     * @param qname the QName that identifies a Schema element declaration
     */
    public void setElementName(QName qname);
    public QName getElementName();
    
    /**
     * Returns the Schema element declaration identified by the QName in the 'element' 
     * attribute of the interface &lt;fault&gt; element. 
     * If this QName does not resolve to an element declaration in a schema that is visible 
     * to the containing WSDL description, null will be returned by this method. 
     * To be visible, the Schema must have been correctly imported or inlined within 
     * the &lt;types&gt; element.
     * 
     * @return the XmlSchemaElement identified by the 'element' attribute
     */
    public XmlSchemaElement getElement();

}

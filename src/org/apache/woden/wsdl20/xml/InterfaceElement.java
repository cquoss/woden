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

import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.woden.types.NCName;

/**
 * This interface represents a &lt;interface&gt; XML element 
 * information item. It declares the behaviour required to support 
 * parsing, creating and manipulating a &lt;interface&gt; element.
 * 
 * @author jkaputin@apache.org
 */
public interface InterfaceElement extends DocumentableElement, 
                                          ConfigurableElement,
                                          NestedElement
{
    /*
     * Attributes
     */

    public void setName(NCName name);
    public QName getName();
    
    public void addExtendedInterfaceName(QName qname);
    public void removeExtendedInterfaceName(QName qname);
    public QName[] getExtendedInterfaceNames();
    
    public InterfaceElement getExtendedInterfaceElement(QName qname);
    public InterfaceElement[] getExtendedInterfaceElements();
    
    public void addStyleDefaultURI(URI uri);
    //TODO public void removeStyleDefaultURI(URI uri);
    public URI[] getStyleDefault();
    //TODO add a remove method
    
    /*
     * Elements
     */
    
    public InterfaceFaultElement addInterfaceFaultElement();
    //TODO public void removeInterfaceFaultElement(QName qname);
    
    /**
     * Returns the InterfaceFaultElement representing the &lt;fault&gt; element 
     * with the specified QName from the set of faults declared directly within this 
     * &lt;interface&gt; element. 
     * This set does not include faults derived from extended interfaces.
     * 
     * @param qname the qualified name of the required InterfaceFault
     * @return the InterfaceFault object
     */
    public InterfaceFaultElement getInterfaceFaultElement(QName qname);
    
    /**
     * Returns the set of InterfaceFaultElements representing the &lt;fault&gt; elements 
     * declared directly within this &lt;interface&gt; element.
     * This set does not include faults derived from extended interfaces.
     * 
     * @return array of InterfaceFaultElement objects
     */
    public InterfaceFaultElement[] getInterfaceFaultElements();
    
    public InterfaceOperationElement addInterfaceOperationElement();
    //TODO public void removeInterfaceOperationElement(QName qname);
    
    /**
     * Returns the InterfaceOperationElement representing the &lt;operation&gt; element 
     * with the specified QName from the set of operations declared directly within this 
     * &lt;interface&gt; element. 
     * This set does not include operations derived from extended interfaces.
     * 
     * @param qname the qualified name of the required InterfaceOperation
     * @return the InterfaceOperation object
     */
    public InterfaceOperationElement getInterfaceOperationElement(QName qname);
    
    /**
     * Returns the set of InterfaceOperationElements representing the &lt;operation&gt; elements 
     * declared directly within this &lt;interface&gt; element.
     * This set does not include operations derived from extended interfaces.
     * 
     * @return array of InterfaceOperationElement objects
     */
    public InterfaceOperationElement[] getInterfaceOperationElements();
    
}

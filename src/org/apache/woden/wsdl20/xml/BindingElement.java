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
 * This interface represents a WSDL &lt;binding&gt; element. 
 * 
 * @author jkaputin@apache.org
 */
public interface BindingElement extends DocumentableElement,
                                        ConfigurableElement,
                                        NestedElement
{
    /*
     * Attributes
     */

    /**
     * Set the NCName that represents the <tt>name</tt> attribute of the  
     * &lt;binding&gt; element. 
     * 
     * @param name the NCName that identifies the binding.
     */
    public void setName(NCName name);
    public QName getName();
    
    /**
     * Set the QName that represents the <tt>interface</tt> attribute of the 
     * &lt;binding&gt; element. This associates the binding with an interface.
     * 
     * @param qname the QName that identifies interface for this binding
     */
    public void setInterfaceName(QName qname);
    public QName getInterfaceName();
    
    /**
     * Get the InterfaceElement identified by the QName specified in the
     * <tt>interface</tt> attribute of this &lt;binding&gt; element.
     * 
     * @return InterfaceElement the interface associated with this binding
     */
    public InterfaceElement getInterfaceElement();
    
    /**
     * Set the URI that represents the <tt>type</tt> attribute of the 
     * &lt;binding&gt; element. This indicates the type of concrete binding
     * extensions contained within this binding.
     * 
     * @param type the URI indicating the concrete binding
     */
    public void setType(URI type);
    public URI getType();
    
    /*
     * Elements
     */
    
    public BindingFaultElement addBindingFaultElement();
    public BindingFaultElement[] getBindingFaultElements();
    //TODO remove method
    
    public BindingOperationElement addBindingOperationElement();
    public BindingOperationElement[] getBindingOperationElements();
    //TODO remove method
    
    //TODO extension elements

}

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

/**
 * This interface represents the &lt;operation&gt; child element of a
 * WSDL &lt;binding&gt; element. 
 * 
 * @author jkaputin@apache.org
 */
public interface BindingOperationElement extends DocumentableElement,
                                                 ConfigurableElement, 
                                                 NestedElement 
{
    /*
     * Attributes
     */
    
    /**
     * Set the 'ref' attribute to the specified QName, which identifies the
     * interface operation referenced by this binding operation.
     *
     * @param qname identifies the associated interface operation.
     */
    public void setRef(QName qname);
    public QName getRef();

    /**
     * Get the InterfaceOperationElement identified by the QName specified in the
     * <tt>ref</tt> attribute of this binding &lt;operation&gt; element.
     * 
     * @return the InterfaceOperationElement associated with this binding operation
     */
    public InterfaceOperationElement getInterfaceOperationElement();
    
    /*
     * Elements
     */
    
    public BindingMessageReferenceElement addBindingMessageReferenceElement();
    public void removeBindingMessageReferenceElement(BindingMessageReferenceElement msgRef);
    public BindingMessageReferenceElement[] getBindingMessageReferenceElements();

    public BindingFaultReferenceElement addBindingFaultReferenceElement();
    public void removeBindingFaultReferenceElement(BindingFaultReferenceElement faultRef);
    public BindingFaultReferenceElement[] getBindingFaultReferenceElements();
    
}

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
 * This interface represents the &lt;fault&gt; child element of a &lt;binding&gt; element. 
 * 
 * @author jkaputin@apache.org
 */
public interface BindingFaultElement extends DocumentableElement,
                                             ConfigurableElement,
                                             NestedElement
{
    /**
     * Set the 'ref' attribute to the specified QName, which identifies the
     * interface fault referenced by this binding fault.
     *
     * @param qname identifies the associated interface fault.
     */
    public void setRef(QName qname);
    public QName getRef();
    
    /**
     * Get the InterfaceFaultElement identified by the QName specified in the
     * <tt>ref</tt> attribute of this binding &lt;fault&gt; element.
     * 
     * @return the InterfaceFaultElement associated with this binding fault
     */
    public InterfaceFaultElement getInterfaceFaultElement();
}

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
package org.apache.woden.wsdl20.xml;

import javax.xml.namespace.QName;

import org.apache.woden.wsdl20.enumeration.Direction;
import org.apache.woden.wsdl20.enumeration.MessageLabel;

/**
 * This interface represents the &lt;infault&gt; and &lt;outfault&gt; 
 * child elements of a WSDL interface &lt;operation&gt; element. 
 * 
 * @author jkaputin@apache.org
 */
public interface InterfaceFaultReferenceElement extends DocumentableElement,
                                                        ConfigurableElement, 
                                                        NestedElement 
{
    /**
     * Set the 'ref' attribute to the specified QName.
     * @param faultQName the QName identifying the associated InterfaceFaultElement. 
     */
    public void setRef(QName faultQName);
    
    /**
     * Get the QName specified in the 'ref' attribute.
     * @return QName identifies the associated InterfaceFaultElement. 
     */
    public QName getRef();
    
    /**
     * Get the InterfaceFaultElement associated with this InterfaceFaultReferenceElement
     * by the QName specified in the 'ref' attribute.
     * @return InterfaceFaultElement the associated interface fault. 
     */
    public InterfaceFaultElement getInterfaceFaultElement();
    
    public void setMessageLabel(MessageLabel msgLabel);
    public MessageLabel getMessageLabel();
    
    public void setDirection(Direction dir);
    public Direction getDirection();
    
}

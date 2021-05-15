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
package org.apache.woden.wsdl20;

import javax.xml.namespace.QName;

import org.apache.woden.wsdl20.xml.InterfaceElement;

/**
 * Represents the Interface component from the WSDL 2.0 Component model.
 * This component provides a read-only, abstract view of the WSDL 
 * interface, including any interface information defined within
 * imported or included WSDL documents. 
 *
 * @author jkaputin@apache.org
 */
public interface Interface extends ConfigurableComponent {
    
    public QName getName();
    
    public Interface getExtendedInterface(QName qname);
    
    public Interface[] getExtendedInterfaces();
    
    /**
     * Returns the InterfaceFault with the specified QName from the {interface faults}
     * property of the Interface component. This property contains directly declared faults
     * but not any derived from extended interfaces.
     * 
     * @param qname the qualified name of the required InterfaceFault
     * @return the InterfaceFault object
     */
    public InterfaceFault getInterfaceFault(QName qname);
    
    /**
     * Returns the set of InterfaceFault components representing the {interface faults} 
     * property of the Interface component, which includes the directly declared interface faults
     * but not any derived from extended interfaces.
     * 
     * @return array of InterfaceFault components
     */
    public InterfaceFault[] getInterfaceFaults();
    
    /**
     * Returns the InterfaceOperation with the specified QName from the {interface operations}
     * property of the Interface component. This property contains directly declared operations
     * but not any derived from extended interfaces.
     * 
     * @param qname the qualified name of the required InterfaceOperation
     * @return the InterfaceOperation object
     */
    public InterfaceOperation getInterfaceOperation(QName qname);
    
    /**
     * Returns the set of InterfaceOperation components representing the {interface operations} 
     * property of the Interface component, which includes the directly declared interface operations
     * but not any derived from extended interfaces.
     * 
     * @return array of InterfaceOperation components
     */
    public InterfaceOperation[] getInterfaceOperations();
    
    public InterfaceElement toElement();
    
}

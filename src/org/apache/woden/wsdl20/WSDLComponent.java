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

import java.net.URI;

import org.apache.woden.wsdl20.extensions.ComponentExtensions;

/**
 * All components directly or indirectly extend this interface, so it provides 
 * a common term of reference for all components.
 * 
 * @author John Kaputin (jkaputin@apache.org)
 */
public interface WSDLComponent 
{
    /**
     * Tests whether this component is logically equivalent the specified component.
     * Equivalence is determined per spec WSDL 2.0 Part 1 Section 2.17 Equivalence
     * of Components.
     * 
     * @param comp the WSDL component that this component will be compared to
     * @return true if the components are logically equivalent
     */
    public boolean equals(WSDLComponent comp);
    
    /**
     * Gets the group of extension properties, belonging to the specified non-WSDL
     * namespace, that extend this WSDL component.
     * 
     * @param namespace a namespace URI different to the WSDL 2.0 namespace.
     * @return the <code>ComponentExtensions</code> with the specified namespace.
     */
    public ComponentExtensions getComponentExtensionsForNamespace(URI namespace);
    
    //TODO consider a getExtensionProperty(N/S, propName) method here. 
    
}

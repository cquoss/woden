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

/**
 * Components which are nested within a 'top-level' component will extend
 * this interface. These are Property, Feature and the sub components of
 * Interface, Binding and Service. 
 * <p>
 * The components which are not nested are Description, Interface, 
 * Binding, Service, ElementDeclaration and TypeDefinition.
 * 
 * @author jkaputin@apache.org
 */
public interface NestedComponent extends WSDLComponent {

    public WSDLComponent getParent();
    
}

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
 * Components which can be configured with features and properties will
 * extend this interface. That is, Interface and its sub components,
 * Binding and its sub components, and Service and its sub components.
 * <p>
 * The components which are not configurable are Description,
 * ElementDeclaration and TypeDefinition.
 * 
 * @author jkaputin@apache.org
 */
public interface ConfigurableComponent extends WSDLComponent {
    
    public Feature[] getFeatures();

    public Property[] getProperties();

}
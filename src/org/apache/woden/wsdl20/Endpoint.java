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

import org.apache.woden.types.NCName;
import org.apache.woden.wsdl20.xml.EndpointElement;

/**
 * Represents the Endpoint component from the WSDL 2.0 Component model.
 * 
 * @author John Kaputin (jkaputin@apache.org)
 */
public interface Endpoint extends NestedComponent, ConfigurableComponent 
{
    public NCName getName();
    
    public Binding getBinding();
    
    public URI getAddress();
    
    public EndpointElement toElement();
}

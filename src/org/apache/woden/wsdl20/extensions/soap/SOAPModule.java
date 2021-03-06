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
package org.apache.woden.wsdl20.extensions.soap;

import java.net.URI;

import org.apache.woden.wsdl20.WSDLComponent;

/**
 * This interface represents the SOAPModule Component that can appear in the
 * optional {soap modules} property of the WSDL 2.0 components Binding, BindingFault, 
 * BindingOperation, BindingFaultReference or BindingMessageReference.
 * 
 * @author jkaputin@apache.org
 */
public interface SOAPModule 
{
    public URI getRef();
    
    public Boolean isRequired();

    public WSDLComponent getParent();
    
    public SOAPModuleElement toElement();
}

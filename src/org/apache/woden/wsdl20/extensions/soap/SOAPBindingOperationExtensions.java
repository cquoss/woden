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
package org.apache.woden.wsdl20.extensions.soap;

import java.net.URI;

import org.apache.woden.wsdl20.extensions.ComponentExtensions;

/**
 * This interface represents the properties from the SOAP namespace
 * added to the WSDL 2.0 <code>BindingOperation</code> component as part 
 * of the SOAP binding extension.
 * <p>
 * These include:
 * <ul>
 * <li>{soap mep}</li>
 * <li>{soap action}</li>
 * <li>{soap modules}</li>
 * </ul> 
 * 
 * @author jkaputin@apache.org
 */
public interface SOAPBindingOperationExtensions extends ComponentExtensions 
{
    /**
     * Returns an object representing the {soap mep} property, of type xs:anyURI.
     */
    public URI getSoapMep();
    
    /**
     * Returns an object representing the {soap action} property, of type xs:anyURI.
     */
    public URI getSoapAction();
    
    /**
     * Returns an array representing the {soap modules} property, of type wsoap:module.
     */
    public SOAPModule[] getSoapModules();

}

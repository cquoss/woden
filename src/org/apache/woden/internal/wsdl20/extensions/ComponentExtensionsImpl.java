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
package org.apache.woden.internal.wsdl20.extensions;

import java.net.URI;

import org.apache.woden.wsdl20.WSDLComponent;
import org.apache.woden.wsdl20.extensions.ComponentExtensions;
import org.apache.woden.wsdl20.xml.WSDLElement;

/**
 * This class represents group of WSDL extension properties (i.e. extension
 * elements or attributes) related by their non-WSDL namespace. 
 * The only behaviour of this class is to return the namespace.
 * Implementations may extend this class to define behaviour specific to 
 * their required extensions.
 * 
 * @author John Kaputin (jkaputin@apache.org)
 */
public abstract class ComponentExtensionsImpl implements ComponentExtensions 
{
    protected WSDLElement fParentElement = null;
    
    //TODO fParent added for http bindings. Refactor fParentElement to fParent.
    protected WSDLComponent fParent = null;
    
    private URI fNamespace = null;

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.extensions.ComponentExtensions#getNamespace()
     */
    public URI getNamespace() {
        return fNamespace;
    }
    
    /* ************************************************************
     *  Non-API implementation methods
     * ************************************************************/

    public void init(WSDLElement parentElement, URI namespace)
    {
        fParentElement = parentElement;
        fParent = (WSDLComponent)fParentElement; //TODO see todo above about refactoring fParentElement to fParent
        fNamespace = namespace;
    }
}

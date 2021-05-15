/**
 * Copyright 2005, 2006 Apache Software Foundation 
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
package org.apache.woden.internal.wsdl20;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.woden.wsdl20.WSDLComponent;
import org.apache.woden.wsdl20.extensions.ComponentExtensions;

/**
 * All classes implementing the WSDL 2.0 Component and Element
 * model interfaces directly or indirectly extend this 
 * abstract class. It implements the WSDL20Component interface
 * which just provides a common reference for objects from the WSDL 2.0 
 * Component API. This class also inherits common behaviour for 
 * WSDL Elements from WSDLElementImpl, which in turn provides a common
 * reference for objects from the the WSDL 2.0 Element API. 
 * 
 * @author jkaputin@apache.org
 */
public abstract class WSDLObjectImpl extends WSDLElementImpl
                                     implements WSDLComponent
{
    private Map fCompExtensions = new HashMap(); //map of ComponentExtensions keyed by namespace
    
    /* ************************************************************
     *  WSDLComponent interface methods (i.e. WSDL Component API)
     * ************************************************************/
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.WSDLComponent#equals(WSDLComponent)
     * 
     * TODO implement this method in all concrete component classes and make this
     * implementation abstract or throw UnsupportedExc.
     */
    public boolean equals(WSDLComponent comp)
    {
        return super.equals(comp);
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.WSDLComponent#getWSDLExtensionsForNamespace(java.net.URI)
     */
    public ComponentExtensions getComponentExtensionsForNamespace(URI namespace)
    {
        return (ComponentExtensions)fCompExtensions.get(namespace);
    }
    
    /* ************************************************************
     *  Non-API implementation methods
     * ************************************************************/

    /* 
     * Check if a component already exists in a list of those components. Used when 
     * retrieving sets of components to de-duplicate logically equivalent components.
     */
    protected boolean containsComponent(WSDLComponent comp, List components)
    {
        for(Iterator i=components.iterator(); i.hasNext(); )
        {
            WSDLComponent tempComp = (WSDLComponent)i.next();
            if(tempComp.equals(comp)) {
                return true;
            }
        }
        return false;
    }
    
    
    /*
     * Store the extensions in a map using the namespace string as the key.
     * If the extensions value is null, delete any existing entry in the map
     * for this namespace. If the namespace string is null, do nothing.
     * TODO check if still needed after compbuilder refactored.
     */
    public void setComponentExtensions(URI namespace, ComponentExtensions extensions)
    {
        if(namespace != null)
        {
            if(extensions != null) {
                fCompExtensions.put(namespace, extensions);
            } else {
                fCompExtensions.remove(namespace);
            }
        }
    }
}

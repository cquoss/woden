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
package org.apache.woden.internal.wsdl20;

import org.apache.woden.wsdl20.NestedComponent;
import org.apache.woden.wsdl20.WSDLComponent;
import org.apache.woden.wsdl20.xml.NestedElement;
import org.apache.woden.wsdl20.xml.WSDLElement;

/**
 * This abstract superclass implements support for accessing or
 * setting the 'parent' of a nested, configurable WSDL component.
 * All such classes will directly or indirectly extend this abstract 
 * class.
 * 
 * @author jkaputin@apache.org
 */
public abstract class NestedConfigurableImpl extends ConfigurableImpl 
                                             implements NestedComponent, 
                                                        NestedElement 
{
    private WSDLElement fParentElem = null;

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.NestedComponent#getParent()
     */
    public WSDLComponent getParent() {
        return (WSDLComponent)fParentElem;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.NestedElement#setParentElement(org.apache.woden.wsdl20.xml.WSDL20Element)
     */
    public void setParentElement(WSDLElement parent) {
        fParentElem = parent;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.NestedElement#getParentElement()
     */
    public WSDLElement getParentElement() {
        return fParentElem;
    }
}

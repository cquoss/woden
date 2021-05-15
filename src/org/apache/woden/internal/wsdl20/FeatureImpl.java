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

import java.net.URI;

import org.apache.woden.wsdl20.Feature;
import org.apache.woden.wsdl20.WSDLComponent;
import org.apache.woden.wsdl20.xml.FeatureElement;
import org.apache.woden.wsdl20.xml.WSDLElement;

/**
 * This class implements support for the &lt;wsdl:feature&gt; element 
 * and the Feature component in the WSDL Component model.
 * 
 * @author jkaputin@apache.org
 */
public class FeatureImpl extends DocumentableImpl
                         implements Feature, FeatureElement 
{
    private URI fRef = null;
    private boolean fRequired = false;
    private WSDLElement fParent = null;
    
    /* ************************************************************
     *  WSDL Component model methods
     * ************************************************************/
    
    /* 
     * @see org.apache.woden.wsdl20.Feature#getRef()
     * @see org.apache.woden.wsdl20.xml.FeatureElement#getRef()
     */
    public URI getRef() {
        return fRef;
    }

    /* 
     * @see org.apache.woden.wsdl20.Feature#isRequired()
     * @see org.apache.woden.wsdl20.xml.FeatureElement#isRequired()
     */
    public boolean isRequired() {
        return fRequired;
    }

    /* 
     * @see org.apache.woden.wsdl20.NestedComponent#getParent()
     */
    public WSDLComponent getParent() {
        return (WSDLComponent)fParent;
    }
    
    /* 
     * @see org.apache.woden.wsdl20.Feature#toElement()
     */
    public FeatureElement toElement() {
        return this;
    }
    
    /* ************************************************************
     *  Methods specific to the XML Element model)
     * ************************************************************/
    
    /*
     * @see org.apache.woden.wsdl20.xml.FeatureElement#setRequired(boolean)
     */
    public void setRequired(boolean required) {
        fRequired = required;
    }
    
    /*
     * @see org.apache.woden.wsdl20.xml.FeatureElement#setRef(URI)
     */
    public void setRef(URI ref) {
        fRef = ref;
    }
    
    /* 
     * @see org.apache.woden.wsdl20.xml.NestedElement#setParentElement(WSDL20Element)
     */
    public void setParentElement(WSDLElement parent) {
        fParent = parent;
    }

    /* 
     * @see org.apache.woden.wsdl20.xml.NestedElement#getParentElement()
     */
    public WSDLElement getParentElement() {
        return fParent;
    }

}

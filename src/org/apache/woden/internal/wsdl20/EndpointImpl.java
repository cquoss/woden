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

import javax.xml.namespace.QName;

import org.apache.woden.types.NCName;
import org.apache.woden.wsdl20.Binding;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.Endpoint;
import org.apache.woden.wsdl20.xml.BindingElement;
import org.apache.woden.wsdl20.xml.EndpointElement;

/**
 * This class represents the Endpoint component and the &lt;endpoint&gt; element.
 * 
 * @author John Kaputin (jkaputin@apache.org)
 */
public class EndpointImpl extends NestedConfigurableImpl 
                          implements Endpoint,
                                     EndpointElement 
{
    private NCName fName = null;
    private QName fBindingName = null;
    private URI fAddress = null;

    /* ************************************************************
     *  Endpoint interface methods (the WSDL Component model)
     * ************************************************************/
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.Endpoint#getName()
     * @see org.apache.woden.wsdl20.xml.EndpointElement#getName()
     */
    public NCName getName() {
        return fName;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.Endpoint#getBinding()
     */
    public Binding getBinding() 
    {
        ServiceImpl service = (ServiceImpl)getParent();
        Description desc = service.getDescriptionComponent();
        Binding binding = desc.getBinding(fBindingName);
        return binding;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.Endpoint#getAddress()
     * @see org.apache.woden.wsdl20.xml.EndpointElement#getAddress()
     */
    public URI getAddress() {
        return fAddress;
    }

    /*
     * @see org.apache.woden.wsdl20.Endpoint#toElement()
     */
    public EndpointElement toElement() {
        return this;
    }
    
    /* ************************************************************
     *  EndpointElement interface methods (the XML Element model)
     * ************************************************************/
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.EndpointElement#setName(org.apache.woden.types.NCName)
     */
    public void setName(NCName name) {
        fName = name;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.EndpointElement#setBindingName(javax.xml.namespace.QName)
     */
    public void setBindingName(QName qname) {
        fBindingName = qname;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.EndpointElement#getBindingName()
     */
    public QName getBindingName() {
        return fBindingName;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.EndpointElement#getBindingElement()
     */
    public BindingElement getBindingElement() 
    {
        ServiceImpl service = (ServiceImpl)getParentElement();
        
        //Cast the containing description element to a description component to re-use its
        //logic for navigating a composite wsdl to retrieve the in-scope top-level components.
        Description desc = (Description)service.getParentElement();
        
        BindingElement binding = (BindingElement)desc.getBinding(fBindingName);
        return binding;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.EndpointElement#setAddress(java.net.URI)
     */
    public void setAddress(URI uri) {
        fAddress = uri;
    }

    /* ************************************************************
     *  Non-API implementation methods
     * ************************************************************/
    
}

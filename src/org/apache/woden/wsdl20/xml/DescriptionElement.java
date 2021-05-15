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
package org.apache.woden.wsdl20.xml;

import java.net.URI;
import java.util.Map;

import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.extensions.ExtensionRegistry;

/**
 * This interface represents a &lt;wsdl:description&gt; element. 
 * It declares the behaviour required to support 
 * parsing, creating and manipulating a &lt;description&gt; element.
 * 
 * TODO initially, have added a method to get the WSDL component
 * model from the xml instance (i.e. get a Description from this 
 * instance of DescriptionElement). Still need to decide 
 * where on the API to put this. Maybe in WSDLReader? 
 * 
 * @author jkaputin@apache.org
 */
public interface DescriptionElement extends DocumentableElement 
{

    public void setDocumentBaseURI(URI documentBaseURI);
    public URI getDocumentBaseURI();
    
    /*
     * Attributes and namespaces
     */
    public void setTargetNamespace(URI namespaceURI);
    public URI getTargetNamespace();

    /**
     * This is a way to add a namespace association to a definition.
     * It is similar to adding a namespace prefix declaration to the
     * top of a &lt;wsdl:definition&gt; element. This has nothing to do
     * with the &lt;wsdl:import&gt; element; there are separate methods for
     * dealing with information described by &lt;wsdl:import&gt; elements.
     *
     * @param prefix the prefix to use for this namespace (when
     * rendering this information as XML). Use null or an empty string
     * to describe the default namespace (i.e. xmlns="...").
     * @param namespace the namespace URI to associate the prefix
     * with. If you use null, the namespace association will be removed.
     */
    public void addNamespace(String prefix, URI namespace);
    
    public void removeNamespace(String prefix);
    
    /**
     * Get the namespace URI associated with this prefix. Or null if
     * there is no namespace URI associated with this prefix. This is
     * unrelated to the &lt;wsdl:import&gt; element.
     *
     * @see #addNamespace(String, URI)
     */
    public URI getNamespace(String prefix);
    
    public Map getNamespaces();//TODO return arrays instead of Map?
    
    /*
     * Element factory methods
     */
    
    public ImportElement addImportElement();
    
    public IncludeElement addIncludeElement();

    /**
     * Create a new InterfaceElement in this DescriptionElement
     * @return the InterfaceElement created
     */
    public InterfaceElement addInterfaceElement();

    public BindingElement addBindingElement();

    public ServiceElement addServiceElement();
    
    /*
     * Element accessor and modifier methods
     * 
     * TODO removeXXX(obj), getXXX(key) methods
     * 
     */
    
    public ImportElement[] getImportElements();
    
    public IncludeElement[] getIncludeElements();
    
    public TypesElement getTypesElement();
    
    public InterfaceElement[] getInterfaceElements();
    
    public BindingElement[] getBindingElements();
    
    public ServiceElement[] getServiceElements();

    //TODO extension elements
    
    public void setExtensionRegistry(ExtensionRegistry extReg);
    public ExtensionRegistry getExtensionRegistry();

    /**
     * @return the Description component for this DescriptionElement.
     */
    public Description toComponent();
    
    
}

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

import javax.xml.namespace.QName;

import org.apache.woden.wsdl20.xml.DescriptionElement;


/**
 * This interface represents the Description component from the WSDL Component
 * Model, as described in the WSDL 2.0 specification.  It provides an abstract
 * view of a WSDL document by flattening the composite document structure created 
 * by the use of &lt;wsdl:import&gt; or &lt;wsdl:include&gt; into a single top-level
 * WSDL Description containing all of the inlined, imported or included WSDL content.
 * In doing so, it elimates the need to 'walk' the XML tree to access the WSDL content.
 * This also means that a Description component may not correspond 1-to-1 
 * to a lt;wsdl:description&gt; element in XML (i.e. it may contain WSDL content 
 * from composite WSDL documents, each with its own &lt;wsdl:description&gt; root
 * element). 
 * 
 * @author jkaputin@apache.org
 */
public interface Description extends WSDLComponent 
{
    
    /**
     * Represents the {interfaces} property of the Description component. This is the set of
     * all interfaces available to the description, including those that are directly declared
     * and any that are imported or included.
     * 
     * @return an array of Interface objects
     */
    public Interface[] getInterfaces();
    
    public Interface getInterface(QName name);
    
    /**
     * Represents the {bindings} property of the Description component. This is the set of
     * all bindings available to the description, including those that are directly declared
     * and any that are imported or included.
     * 
     * @return an array of Binding objects
     */
    public Binding[] getBindings();
    
    public Binding getBinding(QName name);
    
    /**
     * Represents the {services} property of the Description component. This is the set of
     * all services available to the description, including those that are directly declared
     * and any that are imported or included.
     * 
     * @return an array of Service objects
     */
    public Service[] getServices();

    public Service getService(QName name);

    /**
     * Represents the {element declarations} property of the Description component. This is the set of
     * all global element declarations available to the description, including those that are directly 
     * declared in this description's &lt;types&gt; element and any that are that are imported or included.
     * 
     * @return an array of ElementDeclaration objects
     */
    public ElementDeclaration[] getElementDeclarations();
    
    public ElementDeclaration getElementDeclaration(QName name);
    
    /**
     * Represents the {type definitions} property of the Description component. This is the set of
     * all global type definitions available to the description, including those that are directly 
     * declared in this description's &lt;types&gt; element and any that are that are imported or included.
     * 
     * @return an array of TypeDefinition objects
     */
    public TypeDefinition[] getTypeDefinitions();
    
    public TypeDefinition getTypeDefinition(QName qname);
    
    public DescriptionElement toElement();
}

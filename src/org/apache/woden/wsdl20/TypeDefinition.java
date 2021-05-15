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

import javax.xml.namespace.QName;

/**
 * This interface represents the TypeDefinition component described
 * in the WSDL 2.0 Component Model specification (within the Description 
 * Component section). This component refers to simple or complex data types 
 * defined in a type system such as XML Schema 
 * (e.g. &lt;xs:simpleType&gt; or &lt;xs:complexType&gt;).
 * However, it does not impose XML Schema as the type system.  
 * It returns a String representing the content model or type system 
 * (e.g. "http://www.w3.org/2001/XMLSchema") and a java.lang.Object 
 * representing the content of the type definition. This Object may
 * be cast to a type appropriate for the content model.
 * 
 * TODO consider whether this interface should extend WSDLComponent too
 * (i.e. it is described in the spec within the Description component section, 
 * but it doesn't correspond directly to a WSDL element).
 *  
 * @author jkaputin@apache.org
 */
public interface TypeDefinition {
    
    /**
     * A constant representing the DOM API, 
     * used to indicate the content model.
     */
    public static final String API_W3C_DOM =
        "org.w3c.dom";

    /**
     * A constant representing the Apache Ws-Commons XmlSchema API, 
     * used to indicate the content model.
     */
    public static final String API_APACHE_WS_XS =
        "org.apache.ws.commons.schema";

    /**
     * @return the QName identifying this type definition in the
     * underlying type system definition.
     *  
     */
    public QName getName();
    
    /**
     * Indicates the underlying type system of this type definition.
     * For example, "http://www.w3.org/2001/XMLSchema" indicates the W3 XML Schema
     * type system.
     *  
     * @return the URI identifying the type system
     */
    public URI getSystem();
    
    /**
     * Indicates the type of model or API used to represent components from the 
     * underlying type system identified by the getSystem() method.
     * <p>
     * For example:
     * <ul>
     * <li>"org.w3c.dom" indicates that the DOM API is used, so the type definition 
     * content will be represented by a org.w3c.dom.Element.
     * <li>"org.apache.ws.commons.schema" indicates that the XmlSchema API from the
     * Apache WebServices project is used, so an XmlSchemaType will represent the 
     * type definition content. 
     * </ul>
     * 
     * @return a String identifying the content model or API used to represent this 
     * type definition
     */
    public String getContentModel();
    
    /**
     * Returns the content of the type definition in an object
     * specific to the underlying content model API. The caller may then
     * cast this Object to the appropriate type, based on the content model
     * API indicated by the getContent() method.
     * 
     * @return the Object representing the content of the type definition
     */
    public Object getContent();

}

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
package org.apache.woden.wsdl20.extensions;

import java.net.URI;

/**
 * This interface represents a group of properties that extend a <code>WSDLComponent</code>.
 * These properties share the same namespace and it is different to the WSDL 2.0 namespace.
 * The XML representation of these properties are the elements and attributes
 * from outside the WSDL 2.0 namespace that extend a WSDL element.
 * For example, the elements and attributes from the SOAP namespace that extend the 
 * WSDL &lt;binding&gt; element are represented by this interface as extension
 * properties of the <code>Binding</code> component. 
 * <p>
 * This interface provides a common point of reference to a WSDL component's extension
 * properties that belong to a particular namespace. 
 * The interface does not define any behaviour specific to the individual properties.
 * Woden implementations that need to support WSDL extensions from a particular namespace
 * should implement this interface and add support specific to those extensions.
 * <p>
 * For example, Woden implements this interface to support the SOAP and HTTP binding
 * extensions defined in the W3C WSDL 2.0 specification.
 * 
 * @author jkaputin@apache.org
 * @author Arthur Ryman (ryman@ca.ibm.com)
 */
public interface ComponentExtensions 
{
    /**
     * Namespace URIs for extensions defined by WSDL 2.0 Specification.
     */
    public static final URI URI_NS_SOAP = URI.create("http://www.w3.org/2006/01/wsdl/soap");
    public static final URI URI_NS_HTTP = URI.create("http://www.w3.org/2006/01/wsdl/http");
    public static final URI URI_NS_RPC = URI.create("http://www.w3.org/2006/01/wsdl/rpc");
    public static final URI URI_NS_EXTENSIONS = URI.create("http://www.w3.org/2006/01/wsdl-extensions");
     
    /**
     * @return the non-WSDL URI shared by this group of extension properties
     */
    public URI getNamespace();
    
}

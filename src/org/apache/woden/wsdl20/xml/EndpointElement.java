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
package org.apache.woden.wsdl20.xml;

import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.woden.types.NCName;

/**
 * This interface represents an &lt;endpoint&gt; element 
 * information item, a child element of WSDL &lt;service&gt;.
 * 
 * @author jkaputin@apache.org
 */
public interface EndpointElement extends DocumentableElement,
                                         ConfigurableElement, 
                                         NestedElement 
{
    public void setName(NCName name);
    public NCName getName();
    
    public void setBindingName(QName qname);
    public QName getBindingName();
    public BindingElement getBindingElement();
    
    public void setAddress(URI uri);
    public URI getAddress();
}

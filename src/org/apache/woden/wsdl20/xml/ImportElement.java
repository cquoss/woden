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

/**
 * This interface represents a &lt;import&gt; XML element 
 * information item. It declares the behaviour required to support 
 * parsing, creating and manipulating a &lt;import&gt; element.
 * 
 * @author jkaputin@apache.org
 */
public interface ImportElement extends DocumentableElement 
{
    public void setNamespace(URI nsURI);
    public URI getNamespace();
    
    public void setLocation(URI locURI);
    public URI getLocation();
    
    public void setDescriptionElement(DescriptionElement desc);
    public DescriptionElement getDescriptionElement();
}

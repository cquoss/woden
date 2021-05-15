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

/**
 * Interfaces for WSDL 2.0 elements which may have &lt;documentation&gt; 
 * child elements will extend this interface. That is, all WSDL 2.0 elements
 * except the &lt;documentation&gt; element itself.
 * 
 * @author jkaputin@apache.org
 */
public interface DocumentableElement extends WSDLElement 
{
    public DocumentationElement addDocumentationElement();
    
    public DocumentationElement[] getDocumentationElements();
    
    //TODO a remove method
}

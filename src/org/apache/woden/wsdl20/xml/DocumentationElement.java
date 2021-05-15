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
 * This interface represents the WSDL 2.0 &lt;wsdl:documentation&gt; XML element.
 * The &lt;wsdl:documentation&gt; element may contain mixed content, but this 
 * interface does not attempt to understand that content. Instead it just wraps
 * the &lt;wsdl:documentation&gt; element's content model as a java.lang.Object. 
 * 
 * @author jkaputin@apache.org
 */
public interface DocumentationElement extends WSDLElement {
    
    public void setContent(Object docEl);
    
    public Object getContent();
}

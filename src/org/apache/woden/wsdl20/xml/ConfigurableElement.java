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
 * Interfaces for elements which can have &lt;feature&gt; and &lt;property&gt;
 * child elements will extend this interface. That is, &lt;types&gt;, 
 * &lt;interface&gt; and its child elements, 
 * &lt;binding&gt; and its child elements, and
 * &lt;service&gt; and its child elements.
 * <p>
 * The WSDL elements which are not configurable are
 * &lt;description&gt;, &lt;feature&gt;, &lt;property&gt;, 
 * &lt;import&gt;, &lt;include&gt; and &lt;documentation&gt;.
 * 
 * @author jkaputin@apache.org
 */
public interface ConfigurableElement extends WSDLElement {
    
    public FeatureElement addFeatureElement();
    public FeatureElement[] getFeatureElements();
    //TODO a remove method
    
    public PropertyElement addPropertyElement();
    public PropertyElement[] getPropertyElements();
    //TODO a remove method
    
}

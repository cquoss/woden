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

import org.apache.woden.wsdl20.extensions.AttributeExtensible;
import org.apache.woden.wsdl20.extensions.ElementExtensible;

/**
 * All WSDL 2.0 element interfaces will directly or indirectly extend this interface, 
 * so it provides a common type for all such elements.
 * 
 * @author jkaputin@apache.org
 */
public interface WSDLElement extends AttributeExtensible, ElementExtensible
{
    /*
     * All elements in the WSDL 2.0 namespace support attribute extensibility and
     * element extensibility, so by inheriting directly or indirectly from this 
     * interface they also inherit the extensibility interfaces.
     */
}

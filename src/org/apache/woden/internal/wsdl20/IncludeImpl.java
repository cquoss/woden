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
package org.apache.woden.internal.wsdl20;

import org.apache.woden.wsdl20.xml.IncludeElement;

/**
 * This class implements the &lt;wsdl:include&gt; element. 
 * 
 * @author jkaputin@apache.org
 */
public class IncludeImpl extends WSDLReferenceImpl implements IncludeElement 
{
    /* No additional definitions required. This class inherits all of its behaviour 
     * from WSDLReferenceImpl. We just need this subclass so we can create an
     * object representing IncludeElement, which maps to <wsdl:include>.
     */
}

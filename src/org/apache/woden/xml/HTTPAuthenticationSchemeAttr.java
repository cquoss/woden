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
package org.apache.woden.xml;

import org.apache.woden.wsdl20.extensions.http.HTTPAuthenticationScheme;

/**
 * This interface represents the value of the whttp:authenticationType
 * attribute.
 * 
 * @author Arthur Ryman (ryman@ca.ibm.ca, arthur.ryman@gmail.com)
 * 
 */
public interface HTTPAuthenticationSchemeAttr extends XMLAttr {

	HTTPAuthenticationScheme getScheme();

}

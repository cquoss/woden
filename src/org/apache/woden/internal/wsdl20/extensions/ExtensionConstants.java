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
package org.apache.woden.internal.wsdl20.extensions;

import javax.xml.namespace.QName;

/**
 * Constants for predefined WSDL extensions.
 * 
 * @author Arthur Ryman (ryman@ca.ibm.com)
 */
public class ExtensionConstants {

	// Namespace URI

	public static final String NS_URI_WSDL_EXTENSIONS = "http://www.w3.org/2006/01/wsdl-extensions";

	// Attribute name

	public static final String ATTR_SAFE = "safe";

	// Prefix

	public static final String PFX_WSDL_EXTENSIONS = "wsdlx";

	// Qualified attribute name

	public static final QName Q_ATTR_SAFE = new QName(
			NS_URI_WSDL_EXTENSIONS, ATTR_SAFE, PFX_WSDL_EXTENSIONS);

}

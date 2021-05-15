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

import org.apache.woden.wsdl20.extensions.InterfaceOperationExtensions;
import org.apache.woden.xml.BooleanAttr;

/**
 * This class defines the properties from the WSDL extensions namespace added to
 * the WSDL <code>Interface Operation</code> component as part of the WSDL
 * extension defined by the WSDL 2.0 spec.
 * 
 * @author Arthur Ryman (ryman@ca.ibm.com)
 */

public class InterfaceOperationExtensionsImpl extends ComponentExtensionsImpl
		implements InterfaceOperationExtensions {

	public boolean isSafety() {

		BooleanAttr safe = 
            (BooleanAttr)fParentElement.getExtensionAttribute(ExtensionConstants.Q_ATTR_SAFE);
        return safe != null ? safe.getBoolean().booleanValue() : false;
	}
}

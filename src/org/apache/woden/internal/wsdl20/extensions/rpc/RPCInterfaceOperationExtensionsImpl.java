/*
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

package org.apache.woden.internal.wsdl20.extensions.rpc;

import org.apache.woden.internal.wsdl20.extensions.ComponentExtensionsImpl;
import org.apache.woden.wsdl20.extensions.rpc.Argument;
import org.apache.woden.wsdl20.extensions.rpc.RPCInterfaceOperationExtensions;
import org.apache.woden.xml.ArgumentArrayAttr;

/**
 * This class defines the properties from the WSDL RPC extensions namespace
 * added to the WSDL <code>Interface Operation</code> component as part of the
 * WSDL RPC extension defined by the WSDL 2.0 spec.
 * 
 * @author Arthur Ryman (ryman@ca.ibm.com)
 */

public class RPCInterfaceOperationExtensionsImpl extends
		ComponentExtensionsImpl implements RPCInterfaceOperationExtensions {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.woden.wsdl20.extensions.rpc.RPCInterfaceOperationExtensions#getRPCSignature()
	 */
	public Argument[] getRPCSignature() {

		ArgumentArrayAttr args = (ArgumentArrayAttr) fParentElement
				.getExtensionAttribute(RPCConstants.Q_ATTR_RPC_SIGNATURE);

		if (args == null)
			return new Argument[0];

		return args.getArgumentArray();
	}

}

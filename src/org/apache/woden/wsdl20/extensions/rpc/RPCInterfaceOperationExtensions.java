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

package org.apache.woden.wsdl20.extensions.rpc;

import org.apache.woden.wsdl20.extensions.ComponentExtensions;

/**
 * <code>RPCInterfaceOperationExtensions</code> represents the WSDL 2.0 predefined
 * RPC extensions, as specified on Part 2: Adjuncts, for the Interface Operation
 * component.
 * 
 * The only predefined extension attribute is:
 * {rpc signature} 
 *  
 * @author Arthur Ryman (ryman@ca.ibm.com)
 *
 */
public interface RPCInterfaceOperationExtensions extends ComponentExtensions {
	
	/*
	 * Returns the {rpc signature} extension property of Interface Operation 
	 * as defined by the wrpc:signature attribute.
	 */
	public Argument[] getRPCSignature();
}

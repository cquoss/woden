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

import java.net.URI;

import javax.xml.namespace.QName;

/**
 * Constants for predefined WSDL RPC extensions.
 * 
 * @author Arthur Ryman (ryman@ca.ibm.com)
 */
public class RPCConstants {

	// Namespace URI

	public static final String NS_URI_RPC = "http://www.w3.org/2006/01/wsdl/rpc";
	
	// Style URI

	public static final String STYLE_URI_RPC = "http://www.w3.org/2006/01/wsdl/style/rpc";
    public static final URI URI_STYLE_RPC = URI.create("http://www.w3.org/2006/01/wsdl/style/rpc");
	
	// Attribute name

	public static final String ATTR_SIGNATURE = "signature";

	// Prefix

	public static final String PFX_RPC = "wrpc";

	// Qualified attribute name

	public static final QName Q_ATTR_RPC_SIGNATURE = new QName(
			NS_URI_RPC, ATTR_SIGNATURE, PFX_RPC);

}

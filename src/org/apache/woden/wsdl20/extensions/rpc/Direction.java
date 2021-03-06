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

package org.apache.woden.wsdl20.extensions.rpc;

/**
 * <code>Direction</code> is a typesafe enumeration of the four
 * possible values, #in, #out, #inout, and #return.
 * 
 * @author Arthur Ryman (ryman@ca.ibm.com)
 */

public class Direction {
	
	private String token;
	
	private Direction(String token) {
		
		this.token = token;
	}

	public static final Direction IN = new Direction("#in");
	public static final Direction OUT = new Direction("#out");
	public static final Direction INOUT = new Direction("#inout");
	public static final Direction RETURN = new Direction("#return");
	
	public String toString() {
		
		return token;
	}
}

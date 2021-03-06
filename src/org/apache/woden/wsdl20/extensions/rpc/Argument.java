/*
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

import javax.xml.namespace.QName;

/**
 * <code>Argument</code> represents a (name,direction) pair where
 * name is the name of an argument and direction is its direction
 * as defined in Part 2: Adjuncts.
 * 
 * @author Arthur Ryman (ryman@ca.ibm.com)
 *
 */
public class Argument {
	
	private QName name;
	
	private Direction direction;
	
	public Argument(QName name, Direction direction) {
		
		this.name = name;
		this.direction = direction;
	}
	
	public QName getName() {
		
		return name;
	}

	public Direction getDirection() {
		
		return direction;
	}
}

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

package org.apache.woden.wsdl20.extensions;

/**
 * <code>InterfaceOperationExtensions</code> represents the WSDL 2.0
 * predefined extensions, as specified on Part 2: Adjuncts, for the Interface
 * Operation component.
 * 
 * The only predefined extension property is:
 * {safety}
 * 
 * @author Arthur Ryman (ryman@ca.ibm.com)
 * 
 */
public interface InterfaceOperationExtensions extends ComponentExtensions {

	/*
	 * Returns the value of the {safety} extension property of Interface
	 * Operation as defined by the wsdlx:safe attribute.
	 */
	public boolean isSafety();

}

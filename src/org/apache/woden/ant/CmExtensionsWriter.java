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

package org.apache.woden.ant;

import org.apache.woden.wsdl20.extensions.InterfaceOperationExtensions;

/**
 * @author Arthur Ryman (ryman@ca.ibm.com, arthur.ryman@gmail.com)
 *
 */
public class CmExtensionsWriter extends NamespaceWriter {

    public final static String NS = "http://www.w3.org/2002/ws/desc/wsdl/component-extensions";

    public final static String PREFIX = "cmextensions";

    public static final String WSDL_INTERFACE_OPERATION_EXTENSION = PREFIX
            + ":wsdlInterfaceOperationExtension";

    public static final String SAFETY = PREFIX + ":safety";

    public CmExtensionsWriter(XMLWriter out) {

        super(out, NS, PREFIX);
    }

    public void wsdlInterfaceOperationExtension(
            InterfaceOperationExtensions extensions) {

        if (extensions == null)
            return;

        out.beginElement(WSDL_INTERFACE_OPERATION_EXTENSION);

        out.write(SAFETY, extensions.isSafety());

        out.endElement();
    }
}

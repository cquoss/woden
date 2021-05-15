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
package org.apache.woden.internal;

import org.apache.woden.WSDLException;
import org.apache.woden.WSDLFactory;
import org.apache.woden.WSDLReader;
import org.apache.woden.internal.wsdl20.DescriptionImpl;
import org.apache.woden.internal.wsdl20.extensions.PopulatedExtensionRegistry;
import org.apache.woden.wsdl20.extensions.ExtensionRegistry;
import org.apache.woden.wsdl20.xml.DescriptionElement;

public class OMWSDLFactory extends WSDLFactory {

    //Returns an OMWSDLReader
    public WSDLReader newWSDLReader() throws WSDLException {
        return new OMWSDLReader();
    }

    public DescriptionElement newDescription() {
        DescriptionElement desc = new DescriptionImpl();
        ExtensionRegistry extReg = newPopulatedExtensionRegistry();
        desc.setExtensionRegistry(extReg);
        return desc;
    }

    public ExtensionRegistry newPopulatedExtensionRegistry() {
      return new PopulatedExtensionRegistry();
    }

}

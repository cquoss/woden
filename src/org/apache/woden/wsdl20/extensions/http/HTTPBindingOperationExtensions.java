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
package org.apache.woden.wsdl20.extensions.http;

import java.net.URI;

import org.apache.woden.wsdl20.extensions.ComponentExtensions;

/**
 * This interface represents the properties from the HTTP namespace
 * added to the WSDL 2.0 <code>BindingOperation</code> component as part 
 * of the HTTP binding extension. 
 * <p>
 * These include:
 * <ul>
 * <li>{http location}</li>
 * <li>{http location ignore uncited}</li>
 * <li>{http method}</li>
 * <li>{http input serialization}</li>
 * <li>{http output serialization}</li>
 * <li>{http fault serialization}</li>
 * <li>{http query parameter separator}</li>
 * <li>{http transfer coding default}</li>
 * </ul> 
 * 
 * @author John Kaputin (jkaputin@apache.org)
 * @author Arthur Ryman (ryman@ca.ibm.com, arthur.ryman@gmail.com)
 * - added support for {http location ignore uncited}
 */
public interface HTTPBindingOperationExtensions extends ComponentExtensions 
{
    /**
     * @return URI the {http location} property, represented by the whttp:location extension attribute
     */
    public URI getHttpLocation();
    
    /**
     * @return Boolean the {http location ignore uncited} property, represented by the whttp:ignoreUncited extension attribute
     */
    public Boolean isHttpLocationIgnoreUncited();
    
    /**
     * @return String the {http method} property, represented by the whttp:method extension attribute
     */
    public String getHttpMethod();
    
    /**
     * @return String the {http input serialization} property, represented by the whttp:inputSerialization extension attribute
     */
    public String getHttpInputSerialization();
    
    /**
     * @return String the {http output serialization} property, represented by the whttp:outputSerialization extension attribute
     */
    public String getHttpOutputSerialization();
    
    /**
     * @return String the {http fault serialization} property, represented by the whttp:faultSerialization extension attribute
     */
    public String getHttpFaultSerialization();
    
    /**
     * @return String the {http query parameter separator}, represented by the whttp:queryParameterSeparator extension attribute
     */
    public String getHttpQueryParameterSeparator();
    
    /**
     * @return String the {http transfer coding default}, represented by the whttp:transferCodingDefault extension attribute
     */
    public String getHttpTransferCodingDefault();
    
}

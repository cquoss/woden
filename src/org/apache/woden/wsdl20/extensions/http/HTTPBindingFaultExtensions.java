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

import org.apache.woden.wsdl20.extensions.ComponentExtensions;

/**
 * This interface represents the properties from the HTTP namespace
 * added to the WSDL 2.0 <code>BindingFault</code> component as part 
 * of the HTTP binding extension. 
 * <p>
 * These include:
 * <ul>
 * <li>{http error status code}</li>
 * <li>{http transfer coding}</li>
 * <li>{http headers}</li>
 * </ul> 
 * 
 * @author John Kaputin (jkaputin@apache.org)
 */
public interface HTTPBindingFaultExtensions extends ComponentExtensions 
{
    public HTTPErrorStatusCode getHttpErrorStatusCode();
    
    public String getHttpTransferCoding();
    
    public HTTPHeader[] getHttpHeaders();
    
}

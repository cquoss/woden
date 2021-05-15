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
package org.apache.woden;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class OMWSDLFactoryTest extends TestCase{

  public static Test suite(){
	return new TestSuite(OMWSDLFactoryTest.class);
  }

    public void testCreateOMWSDLFactory(){
      try{
        WSDLFactory factory = WSDLFactory.newInstance("org.apache.woden.internal.OMWSDLFactory");
        assertNotNull("WSDL factory created is null.", factory);
      }
      catch(WSDLException e){
        fail("Unable to create WSDL factory." + e.getMessage());
      }
    }

    public void testCreateOMWSDLReader(){
      WSDLFactory factory = null;
      try{
        factory = WSDLFactory.newInstance("org.apache.woden.internal.OMWSDLFactory");
        WSDLReader reader = factory.newWSDLReader();
        assertNotNull("WSDL reader created is null.", reader);
      }
      catch (WSDLException e){
        fail("Unable to create WSDL reader. " + e.getMessage());
      }
    }

}

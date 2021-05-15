/**
 * Copyright 2005, 2006 Apache Software Foundation
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

import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.woden.tests.TestErrorHandler;
import org.apache.woden.wsdl20.xml.DescriptionElement;

public class OMWSDLReaderTest extends TestCase{

    private WSDLFactory omWSDLFactory = null;
    private WSDLReader omWSDLReader = null;
    private ErrorHandler handler = null;

    public static Test suite(){
	    return new TestSuite(OMWSDLReaderTest.class);
    }

    protected void setUp() throws Exception{

        handler = new TestErrorHandler();
	    try{
            omWSDLFactory = WSDLFactory.newInstance("org.apache.woden.internal.OMWSDLFactory");
            omWSDLReader = omWSDLFactory.newWSDLReader();
        }
	    catch (Exception e){
            e.printStackTrace();
        }
    }

    public void testReadValidWSDL20FromOM(){
        DescriptionElement desc = null;
        try{
          URL wsdlURL = getClass().getClassLoader().getResource("./org/apache/woden/primer-hotelReservationService.wsdl");
          desc = omWSDLReader.readWSDL(wsdlURL.toString(), handler);
        }
        catch(WSDLException e){
            fail("Unexpected exception: " + e.getMessage());
        }
        assertNotNull("The description returned is null.", desc);
    }

    public void testReadInvalidWSDL20FromOM(){
        try{
            URL wsdlURL = getClass().getClassLoader().getResource("./org/apache/woden/badDescriptionTags.wsdl");
            omWSDLReader.readWSDL(wsdlURL.toString(), handler);
            fail("Expected a WSDLException because the \"description\" tag was deliberately misspelt.");
        }
        catch(WSDLException e){
            assertTrue("Expected a WSDLException with message containing \"WSDL501\", but got: " + e.getMessage() ,
            e.getMessage().indexOf("WSDL501") > -1);
        }
    }
}
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

import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.woden.ErrorHandler;
import org.apache.woden.WSDLFactory;
import org.apache.woden.WSDLReader;
import org.apache.woden.tests.TestErrorHandler;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.Endpoint;
import org.apache.woden.wsdl20.Service;
import org.apache.woden.wsdl20.extensions.ComponentExtensions;
import org.apache.woden.wsdl20.xml.DescriptionElement;

/**
 * Functional verification test of HTTPEndpointExtensions.
 * Checks that the expected API behaviour is supported by the implementation.
 * 
 * @author John Kaputin (jkaputin@apache.org)
 */
public class HTTPEndpointExtensionsTest extends TestCase {
    
    private Endpoint[] fEndpoints = null;
    private String fWsdlPath = 
        "org/apache/woden/wsdl20/extensions/http/resources/HTTPEndpointExtensions.wsdl";

    public static Test suite()
    {
        return new TestSuite(HTTPEndpointExtensionsTest.class);
    }
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        ErrorHandler handler = new TestErrorHandler();
        //Don't set validation on, as the testcase WSDL is not intended to be a valid WSDL 2.0 doc.
        reader.getErrorReporter().setErrorHandler(handler);
        
        URL wsdlURL = getClass().getClassLoader().getResource(fWsdlPath);
        assertNotNull("Failed to find the WSDL document on the classpath using the path: " + fWsdlPath + ".", 
                wsdlURL);
        
        DescriptionElement descElem = reader.readWSDL(wsdlURL.toString());
        assertNotNull("The reader did not return a WSDL description.", descElem);
        Description descComp = descElem.toComponent();
        
        Service service = descComp.getServices()[0];
        assertNotNull("The Description should contain 1 Service component.", 
                service);
        
        fEndpoints = service.getEndpoints();
        assertEquals("The Service contained unexpected number of Endpoint components.", 3, fEndpoints.length);
    }

    /**
     * Testcases for the {http authentication scheme} property returned by the
     * <code>getHttpAuthenticationScheme</code> method.
     * <p>
     * Test that the method returns "basic" if the whttp:authenticationType 
     * attribute parsed from the WSDL contains "basic".
     * <p>
     * Test that the method returns "digest" if the whttp:authenticationType 
     * attribute parsed from the WSDL contains "digest".
     * <p>
     * Test that it returns null by default when the attribute is omitted 
     * from the WSDL.
     */
    public void testGetHttpAuthenicationScheme() {
        
        //test "basic" parsed from WSDL
        Endpoint endpoint = fEndpoints[0];
        HTTPEndpointExtensions httpEndpointExts = 
            (HTTPEndpointExtensions) endpoint
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        HTTPAuthenticationScheme actual = httpEndpointExts.getHttpAuthenicationScheme();
        assertEquals("Unexpected value for http authentication scheme.", 
                HTTPAuthenticationScheme.BASIC,
                actual);
        
        //test "digest" parsed from WSDL
        Endpoint endpoint2 = fEndpoints[1];
        HTTPEndpointExtensions httpEndpointExts2 = 
            (HTTPEndpointExtensions) endpoint2
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        HTTPAuthenticationScheme actual2 = httpEndpointExts2.getHttpAuthenicationScheme();
        assertEquals("Unexpected value for http authentication scheme.", 
                HTTPAuthenticationScheme.DIGEST,
                actual2);
        
        //test default to null
        Endpoint endpoint3 = fEndpoints[2];
        HTTPEndpointExtensions httpEndpointExts3 = 
            (HTTPEndpointExtensions) endpoint3
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        HTTPAuthenticationScheme actual3 = httpEndpointExts3.getHttpAuthenicationScheme();
        assertNull("Http authentication scheme did not default to null.", 
                actual3);
    }

    /**
     * Testcases for the {http authentication realm} property returned by the
     * <code>getHttpAuthenticationRealm</code> method.
     * <p>
     * Test that the method returns "abc" if the whttp:authenticationRealm 
     * attribute parsed from the WSDL contains "abc".
     */
    public void testGetHttpAuthenticationRealm() {
        
        //test "abc" parsed from WSDL
        Endpoint endpoint = fEndpoints[0];
        HTTPEndpointExtensions httpEndpointExts = 
            (HTTPEndpointExtensions) endpoint
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual = httpEndpointExts.getHttpAuthenticationRealm();
        assertEquals("Unexpected value for http authentication realm.", 
                "abc",
                actual);
    }

}

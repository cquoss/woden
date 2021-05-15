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
package org.apache.woden.wsdl20.xml;

import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.woden.ErrorHandler;
import org.apache.woden.WSDLFactory;
import org.apache.woden.WSDLReader;
import org.apache.woden.internal.wsdl20.EndpointImpl;
import org.apache.woden.tests.TestErrorHandler;
import org.apache.woden.types.NCName;

/**
 * Functional verification test of org.apache.woden.wsdl20.xml.EndpointElement.
 * Checks that the expected API behaviour is supported by the implementation.
 */
public class OMEndpointElementTest extends TestCase{

    private WSDLFactory fFactory = null;
    private WSDLReader fReader = null;
    private ErrorHandler fHandler = null;
    private DescriptionElement fParsedDesc = null;
    private EndpointElement[] fParsedEndpoints = null;

    private String fTargetNS = "http://ws.apache.woden/endpoint";

    public static Test suite(){
        return new TestSuite(OMEndpointElementTest.class);
    }

    protected void setUp() throws Exception {

        fFactory = WSDLFactory.newInstance("org.apache.woden.internal.OMWSDLFactory");
        fReader = fFactory.newWSDLReader();
        fHandler = new TestErrorHandler();
        fReader.getErrorReporter().setErrorHandler(fHandler);

        URL wsdlURL = getClass().getClassLoader().getResource(
            "org/apache/woden/wsdl20/xml/resources/EndpointElementTest.wsdl");
        assertNotNull("Failed to find the WSDL document on the classpath.", wsdlURL);

        fParsedDesc = fReader.readWSDL(wsdlURL.toString());
        assertNotNull("The reader did not return a description.", fParsedDesc);

        ServiceElement service = fParsedDesc.getServiceElements()[0];
        assertNotNull("The description does not contain a service.", service);

        fParsedEndpoints = service.getEndpointElements();
        assertTrue("The service does not contain 3 endpoints as expected.",
                fParsedEndpoints.length == 3);
    }

    protected void tearDown() throws Exception{
        fFactory = null;
        fReader = null;
        fHandler = null;
        fParsedDesc = null;
        fParsedEndpoints = null;
    }

    /**
     * Test that the getName method returns the expected NCName parsed from a WSDL document.
     */
    public void testGetNameParsedFromOM(){
        NCName ncName = fParsedEndpoints[0].getName();
        assertNotNull("EndpointElement.getName() returned null, but an NCName was expected.",
                ncName);

        assertTrue("NCName returned by EndpointElement.getName() was not the one expected.",
                "endpoint1".equals(ncName.toString()) );
    }

    /**
     * Test that the NCName specified on the setName method is returned by getName.
     */
    public void testSetAndGetNameFromOM(){
        EndpointElement endpoint = new EndpointImpl();
        NCName ncName = new NCName("dummy");

        endpoint.setName(ncName);
        assertTrue("NCName returned by EndpointElement.getName() was not the one set by setName().",
                ncName.equals(endpoint.getName()));
    }

    /**
     * Test that the getBindingName method returns the QName of the binding
     * associated with this endpoint, as specified by the "binding" attribute
     * of the &lt;endpoint&gt; element in a parsed WSDL document.
     */
    public void testGetBindingNameParsedFromOM(){
        QName qname = fParsedEndpoints[0].getBindingName();
        assertNotNull("EndpointElement.getBindingName() returned null, but a QName was expected.",
                      qname);

        QName expectedQN = new QName(fTargetNS, "binding1");
        assertTrue("QName returned by EndpointElement.getBindingName() was not the one expected.",
                   expectedQN.equals(qname));
    }

    /**
     * Test that the QName specified on the setBindingName method is returned by
     * the getBindingName method.
     */
    public void testSetAndGetBindingNameFromOM(){
        EndpointElement endpoint = new EndpointImpl();
        QName qname = new QName("urn:woden","dummy");
        endpoint.setBindingName(qname);
        QName returnedQN = endpoint.getBindingName();
        assertTrue("QName returned by EndpointElement.getBindingName() was not the one set by setBindingName().",
                   returnedQN.equals(qname));
    }

    /**
     * Test that the getBindingElement method returns a BindingElement
     * defined within the description, that is referred to by QName in the
     * "binding" attribute of the &lt;endpoint&gt; element of a parsed WSDL
     * document. This tests that the QName is correctly dereferenced to an object.
     */
    public void testGetBindingElementParsedFromOM(){
        BindingElement bindingDefined = fParsedDesc.getBindingElements()[0];
        BindingElement bindingReferred = fParsedEndpoints[0].getBindingElement();
        assertNotNull("EndpointElement.getBindingElement() returned null, but a BindingElement was expected.",
                bindingReferred);

        assertTrue("The BindingElement returned by EndpointElement.getBindingElement() was not the one expected.",
                bindingReferred == bindingDefined);
    }

    /**
     * Test that the getAddress method returns the expected URI parsed from a WSDL document.
     */
    public void testGetAddressParsedFromOM(){
        URI uri = fParsedEndpoints[0].getAddress();
        assertNotNull("EndpointElement.getAddress() returned null, but a URI was expected.",
                uri);

        assertTrue("URI returned by EndpointElement.getAddress() was not the one expected.",
                "urn:abc".equals(uri.toString()) );
    }

    /**
     * Test the optionality of the 'address' attribute by invoking the getAddress
     * method on a parsed &lt;endpoint&gt; element that does not have an
     * 'address' specified and check that it returns null.
     */
    public void testGetAddressParsedOptionalFromOM(){
        URI uri = fParsedEndpoints[1].getAddress();
        assertNull("EndpointElement.getAddress() did not return null, as expected.",
                uri);
    }

    /**
     * Test that the URI specified on the setAddress method is returned by getAddress.
     */
    public void testSetAndGetAddressFromOM() throws Exception{
        EndpointElement endpoint = new EndpointImpl();
        URI uri = new URI("urn:dummy");
        endpoint.setAddress(uri);
        assertTrue("URI returned by EndpointElement.getAddress() was not the one set by setAddress().",
                   "urn:dummy".equals(endpoint.getAddress().toString()));
    }
}

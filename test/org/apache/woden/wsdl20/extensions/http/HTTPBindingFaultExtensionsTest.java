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
import org.apache.woden.wsdl20.Binding;
import org.apache.woden.wsdl20.BindingFault;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.extensions.ComponentExtensions;
import org.apache.woden.wsdl20.xml.DescriptionElement;

/**
 * Functional verification test of HTTPBindingFaultExtensions.
 * Checks that the expected API behaviour is supported by the implementation.
 * 
 * @author John Kaputin (jkaputin@apache.org)
 */
public class HTTPBindingFaultExtensionsTest extends TestCase 
{
    private BindingFault[] fBindFaults = null;
    private String fWsdlPath = "org/apache/woden/wsdl20/extensions/http/resources/HTTPBindingFaultExtensions.wsdl";

    public static Test suite()
    {
        return new TestSuite(HTTPBindingFaultExtensionsTest.class);
    }
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception 
    {
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
        
        Binding[] bindings = descComp.getBindings();
        assertEquals("The Description should contain 1 Binding component.", 1, bindings.length);
        
        fBindFaults = bindings[0].getBindingFaults();
        assertEquals("The Binding should contain 4 BindingFault components.", 4, fBindFaults.length);
        
    }
    
    /**
     * Test that the value for the {http error status code} property returned by the 
     * <code>getHttpErrorStatusCode</code> method matches the value parsed from the WSDL.
     */
    public void testGetHttpErrorStatusCode()
    {
        BindingFault bindFault = fBindFaults[0];
        HTTPBindingFaultExtensions httpBindFaultExts = (HTTPBindingFaultExtensions)bindFault
            .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP);
        assertNotNull("The BindingFault does not contain an HTTPBindingFaultExtensions object.",
                httpBindFaultExts);
        
        HTTPErrorStatusCode actual = httpBindFaultExts.getHttpErrorStatusCode();
        assertNotNull("The value for http error status code was null", actual);
        assertEquals("Unexpected value for http error status code.", 
                123,
                actual.getCode().intValue());

        bindFault = fBindFaults[1];
        httpBindFaultExts =  (HTTPBindingFaultExtensions)bindFault
            .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP);
        assertNotNull("The BindingFault does not contain an HTTPBindingFaultExtensions object.",
                httpBindFaultExts);
        
        actual = httpBindFaultExts.getHttpErrorStatusCode();
        assertNotNull("The value for http error status code was null", actual);
        assertEquals("Unexpected value for http error status code.", 
                HTTPErrorStatusCode.ANY,
                actual);
    }

    /**
     * Test that the value for the {http transfer coding} property returned by the 
     * <code>getHttpTransferCoding</code> method matches the value from the WSDL.
     */
    public void testGetHttpTransferCoding()
    {
        BindingFault bindFault = fBindFaults[0];
        HTTPBindingFaultExtensions httpBindFaultExts = (HTTPBindingFaultExtensions)bindFault
            .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP);
        
        String actual = httpBindFaultExts.getHttpTransferCoding();
        assertNotNull("The value for http transfer coding was null", actual);
        assertEquals("Unexpected value for http transfer coding.", 
                "compress;chunked",
                actual);
    }
    
    /**
     * Test that when binding type is "http://www.w3.org/2006/01/wsdl/http" and the binding's 
     * wsdl:fault element has no HTTP extension attributes, the BindingFault component still 
     * contains an HTTPBindingFaultExtensions object to handle the default values for the 
     * REQUIRED extension properties.
     * <p>
     * Test that the REQUIRED property {http error status code} defaults to "#any"
     * when the whttp:code attribute is omitted from the WSDL.
     * <p>
     * TODO {http transfer coding} defaults to null, pending clarification
     * on whether it should default to Binding {http transfer coding default}. 
     * If this is confirmed, then apply the following test too.
     * Test that the OPTIONAL property {http transfer coding} defaults to the Binding's
     * {http transfer coding default} property when whttp:transferCoding is omitted.
     */
    public void testHttpPropertyDefaults()
    {
        BindingFault bindFault = fBindFaults[2];
        HTTPBindingFaultExtensions httpBindFaultExts =  (HTTPBindingFaultExtensions)bindFault
            .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP);
        assertNotNull("The BindingFault does not contain an HTTPBindingFaultExtensions object.",
                httpBindFaultExts);
        
        HTTPErrorStatusCode actual = httpBindFaultExts.getHttpErrorStatusCode();
        assertNotNull("The value for http error status code was null", actual);
        assertEquals("Unexpected value for http error status code.", 
                HTTPErrorStatusCode.ANY,
                actual);

        /* See above TODO about fixing the spec before using this test.
         * 
         * //get the http transfer coding default from the parent Binding
         * Binding binding = (Binding)bindFault.getParent();
         * HTTPBindingExtensions httpBindExts =  (HTTPBindingExtensions)binding
         *     .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP);
         * assertNotNull("The parent Binding does not contain an HTTPBindingExtensions object.",
         *         httpBindExts);
         * String tfrCodingDef = httpBindExts.getHttpTransferCodingDefault();
         * 
         * //check that the http transfer coding is equal to the default obtained from the parent
         * assertEquals("The {http transfer coding} does not match the {http transfer coding default}.",
         *         tfrCodingDef,
         *         httpBindFaultExts.getHttpTransferCoding());
         * 
         * //check that the transfer coding is "chunked" (the default in the parent)
         * assertEquals("The {http transfer coding} is not 'chunked' as expected.",
         *         "chunked",
         *         httpBindFaultExts.getHttpTransferCoding());
         */
        
        //TODO replace the following assertNull with the code above, once it's confirmed.
        assertNull("The {http transfer coding} is not null as expected.",
                httpBindFaultExts.getHttpTransferCoding());  
    }
    
    /**
     * Test that the {http headers} property returned by the <code>getHttpHeaders</code> 
     * method contains the expected number of HTTPHeader objects parsed from the WSDL.
     */
    public void testGetHttpHeaders()
    {
        BindingFault bindFault = fBindFaults[3];
        HTTPBindingFaultExtensions httpBindFaultExts = (HTTPBindingFaultExtensions)bindFault
            .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP);
        assertNotNull("The BindingFault does not contain an HTTPBindingFaultExtensions object.",
                httpBindFaultExts);

        HTTPHeader[] actual = httpBindFaultExts.getHttpHeaders();
        assertEquals("Unexpected number of HTTPHeader objects.",
                2,
                actual.length);
    }
        
}

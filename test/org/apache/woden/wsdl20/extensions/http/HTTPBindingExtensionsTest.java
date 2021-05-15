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
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.extensions.ComponentExtensions;
import org.apache.woden.wsdl20.xml.DescriptionElement;

/**
 * Functional verification test of HTTPBindingExtensions.
 * Checks that the expected API behaviour is supported by the implementation.
 * 
 * @author John Kaputin (jkaputin@apache.org)
 */
public class HTTPBindingExtensionsTest extends TestCase 
{
    private Binding[] fBindings = null;
    private String fWsdlPath = "org/apache/woden/wsdl20/extensions/http/resources/HTTPBindingExtensions.wsdl";

    public static Test suite()
    {
        return new TestSuite(HTTPBindingExtensionsTest.class);
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
        
        fBindings = descComp.getBindings();
        assertEquals("The Description should contain 3 Binding components.", 3, fBindings.length);
        
    }
    
    /**
     * Test that the value for the {http method default} property returned by the <code>getHttpMethodDefault</code> 
     * method matches the expected value parsed from the WSDL.
     */
    public void testGetHttpMethodDefault()
    {
        Binding binding1 = fBindings[0];
        HTTPBindingExtensions httpBindExts = (HTTPBindingExtensions)binding1
            .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP);
        assertNotNull("The Binding '" + binding1.getName() + "' does not contain an HTTPBindingExtensions object.",
                httpBindExts);
        
        String actual = httpBindExts.getHttpMethodDefault();
        assertNotNull("The value for http method default was null", actual);
        assertEquals("Unexpected value for http method default.", 
                "POST",
                actual);
    }


    /**
     * Test that the value for the {http query parameter separator default} property returned by the 
     * <code>getHttpQueryParameterSeparatorDefault</code> method matches the expected value parsed 
     * from the WSDL.
     */
    public void testGetHttpQueryParameterSeparatorDefault()
    {
        Binding binding1 = fBindings[0];
        HTTPBindingExtensions httpBindExts = (HTTPBindingExtensions)binding1
            .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP);
        
        String actual = httpBindExts.getHttpQueryParameterSeparatorDefault();
        assertNotNull("The value for http query parameter separator default was null", actual);
        assertEquals("Unexpected value for http query parameter separator default.", 
                "$",
                actual);
    }

    /**
     * Test that the value for the {http cookies} property returned by the 
     * <code>isHttpCookies</code> method matches the expected value parsed 
     * from the WSDL.
     */
    public void testIsHttpCookies()
    {
        //test that a whttp:cookies value "true" equates to Boolean(true)
        Binding binding1 = fBindings[0];
        HTTPBindingExtensions httpBindExts = (HTTPBindingExtensions)binding1
            .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP);
        
        assertTrue("Expected 'true' for for http cookies.",
                httpBindExts.isHttpCookies().booleanValue());
        
        //test that a whttp:cookies value "false" equates to Boolean(false)
        Binding binding3 = fBindings[2];
        httpBindExts = (HTTPBindingExtensions)binding3
            .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP);
        
        assertFalse("Expected 'false' for for http cookies.",
                httpBindExts.isHttpCookies().booleanValue());
    }

    /**
     * Test that the value for the {http transfer coding default} property returned by the 
     * <code>getTransferCodingDefault</code> method matches the expected value parsed 
     * from the WSDL.
     */
    public void testGetHttpTransferCodingDefault()
    {
        Binding binding1 = fBindings[0];
        HTTPBindingExtensions httpBindExts = (HTTPBindingExtensions)binding1
            .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP);
        
        String actual = httpBindExts.getHttpTransferCodingDefault();
        assertEquals("Unexpected value for http transfer coding default.", 
                "chunked",
                actual);
    }


    /**
     * Test that when binding type is "http://www.w3.org/2006/01/wsdl/http" and the wsdl:binding element 
     * has no HTTP extension attributes, the Binding component still contains an HTTPBindingExtensions 
     * object to handle the default values for the REQUIRED extension properties.
     * <p>
     * Test that the REQUIRED property {http query parameter separator default} defaults to "&"
     * when the whttp:queryParameterSeparatorDefault attribute is omitted from the WSDL.
     * <p>
     * Test that the REQUIRED property {http cookies} defaults to false 
     * when the whttp:cookies attribute is omitted from the WSDL. 
     * <p>
     * Test that the OPTIONAL property {http method default} defaults to null 
     * when the whttp:methodDefault attribute is omitted from the WSDL.
     * <p>
     * Test that the OPTIONAL property {transfer coding default} defaults to null
     * when the whttp:transferCodingDefault attribute is omitted from the WSDL.
     */
    public void testHttpPropertyDefaults()
    {
        Binding binding2 = fBindings[1];
        HTTPBindingExtensions httpBindExts = (HTTPBindingExtensions)binding2
            .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP);
        assertNotNull("The Binding '" + 
                binding2.getName() + 
                "' does not contain an HTTPBindingExtensions object.");
        
        assertEquals("The {http query parameter separator default} property should default to ampersand '&'",
                "&",
                httpBindExts.getHttpQueryParameterSeparatorDefault());
        
        assertEquals("The {http cookies} property should default to 'false'",
                new Boolean(false),
                httpBindExts.isHttpCookies());
        
        assertNull("The {http method default} property should default to null",
                httpBindExts.getHttpMethodDefault());
        
        assertNull("The {http transfer coding default} property should default to null",
                httpBindExts.getHttpTransferCodingDefault());
    }
    
}

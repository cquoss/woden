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
package org.apache.woden.wsdl20.extensions.soap;

import java.net.URI;
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
 * Functional verification test of SoapBindingExtensions.
 * Checks that the expected API behaviour is supported by the implementation.
 * 
 * @author John Kaputin (jkaputin@apache.org)
 */
public class SOAPBindingExtensionsTest extends TestCase 
{
    private WSDLFactory fFactory = null;
    private WSDLReader fReader = null;
    private ErrorHandler fHandler = null;
    private DescriptionElement fDescElem = null;
    
    private SOAPBindingExtensions fSoapBindExts = null;
    private String fWsdlPath = "org/apache/woden/wsdl20/extensions/soap/resources/SOAPBindingExtensions.wsdl";
    
    public static Test suite()
    {
        return new TestSuite(SOAPBindingExtensionsTest.class);
    }
    
    protected void setUp() throws Exception 
    {
        fFactory = WSDLFactory.newInstance();
        fReader = fFactory.newWSDLReader();
        fHandler = new TestErrorHandler();
        fReader.getErrorReporter().setErrorHandler(fHandler);
        
        URL wsdlURL = getClass().getClassLoader().getResource(fWsdlPath);
        assertNotNull("Failed to find the WSDL document on the classpath using the path: " + fWsdlPath + ".", 
                wsdlURL);
        
        fDescElem = fReader.readWSDL(wsdlURL.toString());
        assertNotNull("The reader did not return a WSDL description.", fDescElem);
        Description descComp = fDescElem.toComponent();
        
        Binding binding = descComp.getBindings()[0];
        assertNotNull("The Description does not contain a Binding.", binding);
        
        fSoapBindExts = (SOAPBindingExtensions)binding.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        assertNotNull("The Binding does not contain a SOAPBindingExtensions object.");
    }
    
    /**
     * Test that the value for the {soap version} property returned by the <code>getSoapVersion</code> method
     * matches the expected value parsed from the WSDL.
     */
    public void testGetSoapVersion()
    {
        String actual = fSoapBindExts.getSoapVersion();
        assertNotNull("The value for soap version was null", actual);
        assertEquals("Expected '1.2' for soap version but the actual value was '" + actual + "'.", 
                "1.2",
                actual);
    }

    /**
     * Test that the value for the {soap underlying protocol} property returned by the <code>getSoapUnderlyingProtocol</code> method
     * matches the expected value parsed from the WSDL.
     */
    public void testGetSoapUnderlyingProtocol()
    {
        URI actual = fSoapBindExts.getSoapUnderlyingProtocol();
        assertNotNull("The value for soap underlying protocol was null", actual);
        
        URI expected = URI.create("http://www.w3.org/2003/05/soap/bindings/HTTP");
        assertEquals("Unexpected value for soap underlying protocol.", 
                expected,
                actual);
    }
    
    /**
     * Test that the value for the {soap mep default} property returned by the <code>getSoapMepDefault</code> method
     * matches the expected value parsed from the WSDL.
     */
    public void testGetSoapMepDefault()
    {
        URI actual = fSoapBindExts.getSoapMepDefault();
        assertNotNull("The value for soap mep default was null", actual);
        
        URI expected = URI.create("http://www.w3.org/2003/05/soap/mep/request-response");
        assertEquals("Unexpected value for soap mep default.", 
                expected,
                actual);
    }
    
    /**
     * Test that the {soap modules} property returned by the <code>getSoapModules</code> method 
     * contains the expected number of SOAPModule objects parsed from the WSDL.
     */
    public void testGetSoapModules()
    {
        SOAPModule[] actual = fSoapBindExts.getSoapModules();
        assertEquals("Unexpected number of SOAPModule objects.",
                2,
                actual.length);
    }

}
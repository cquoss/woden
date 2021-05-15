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

import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.woden.ErrorHandler;
import org.apache.woden.WSDLFactory;
import org.apache.woden.WSDLReader;
import org.apache.woden.tests.TestErrorHandler;
import org.apache.woden.wsdl20.Binding;
import org.apache.woden.wsdl20.BindingFaultReference;
import org.apache.woden.wsdl20.BindingOperation;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.InterfaceFaultReference;
import org.apache.woden.wsdl20.enumeration.Direction;
import org.apache.woden.wsdl20.extensions.ComponentExtensions;
import org.apache.woden.wsdl20.xml.DescriptionElement;

/**
 * Functional verification test of SoapBindingFaultReferenceExtensions.
 * Checks that the expected API behaviour is supported by the implementation.
 * 
 * @author jkaputin@apache.org
 */
public class SOAPBindingFaultReferenceExtensionsTest extends TestCase 
{
    private WSDLFactory fFactory = null;
    private WSDLReader fReader = null;
    private ErrorHandler fHandler = null;
    private DescriptionElement fDescElem = null;
    
    private BindingOperation fBindOper = null;
    private String fWsdlPath = "org/apache/woden/wsdl20/extensions/soap/resources/SOAPBindingFaultReferenceExtensions.wsdl";
    
    public static Test suite()
    {
        return new TestSuite(SOAPBindingFaultReferenceExtensionsTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception 
    {
        super.setUp();
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
        fBindOper = binding.getBindingOperations()[0];
        assertNotNull("The Binding does not contain a BindingOperation.", fBindOper);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
        
    /**
     * Test that the <code>getSoapModules</code> method returns the expected number of SOAPModule objects 
     * parsed from &lt;wsoap:module&gt; elements within an &lt;infault&gt; element.
     */
    public void testGetSoapModules_infault()
    {
        BindingFaultReference bindFaultRef = fBindOper.getBindingFaultReferences()[0];
        assertNotNull("The BindingOperation does not contain a BindingFaultReference.", bindFaultRef);

        InterfaceFaultReference intFaultRef = bindFaultRef.getInterfaceFaultReference();
        Direction direction = intFaultRef.getDirection();
        assertTrue("The BindingFaultReference does not represent an <infault> element.", Direction.IN.equals(direction));

        SOAPBindingFaultReferenceExtensions soapBindFaultRefExts = 
            (SOAPBindingFaultReferenceExtensions) bindFaultRef.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        SOAPModule[] actual = soapBindFaultRefExts.getSoapModules();
        assertEquals("Unexpected number of SOAPModule objects.", 2, actual.length);
    }

    /**
     * Test that the <code>getSoapModules</code> method returns the expected number of SOAPModule objects 
     * parsed from &lt;wsoap:module&gt; elements within an &lt;outfault&gt; element.
     */
    public void testGetSoapModules_outfault()
    {
        BindingFaultReference bindFaultRef = fBindOper.getBindingFaultReferences()[1];
        assertNotNull("The BindingOperation does not contain a second BindingFaultReference.", bindFaultRef);

        InterfaceFaultReference intFaultRef = bindFaultRef.getInterfaceFaultReference();
        Direction direction = intFaultRef.getDirection();
        assertTrue("The BindingFaultReference does not represent an <outfault> element.", Direction.OUT.equals(direction));

        SOAPBindingFaultReferenceExtensions soapBindFaultRefExts = 
            (SOAPBindingFaultReferenceExtensions) bindFaultRef.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        SOAPModule[] actual = soapBindFaultRefExts.getSoapModules();
        assertEquals("Unexpected number of SOAPModule objects.", 1, actual.length);
    }

}

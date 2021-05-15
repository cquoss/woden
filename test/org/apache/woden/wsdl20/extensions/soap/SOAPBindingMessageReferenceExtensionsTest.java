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
import org.apache.woden.wsdl20.BindingMessageReference;
import org.apache.woden.wsdl20.BindingOperation;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.enumeration.Direction;
import org.apache.woden.wsdl20.extensions.ComponentExtensions;
import org.apache.woden.wsdl20.xml.BindingMessageReferenceElement;
import org.apache.woden.wsdl20.xml.DescriptionElement;

/**
 * Functional verification test of SoapBindingMessageReferenceExtensions.
 * Checks that the expected API behaviour is supported by the implementation.
 * 
 * @author jkaputin@apache.org
 */
public class SOAPBindingMessageReferenceExtensionsTest extends TestCase 
{
    private WSDLFactory fFactory = null;
    private WSDLReader fReader = null;
    private ErrorHandler fHandler = null;
    private DescriptionElement fDescElem = null;
    
    private BindingOperation fBindOper = null;
    private String fWsdlPath = "org/apache/woden/wsdl20/extensions/soap/resources/SOAPBindingMessageReferenceExtensions.wsdl";
    
    public static Test suite()
    {
        return new TestSuite(SOAPBindingMessageReferenceExtensionsTest.class);
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
     * parsed from &lt;wsoap:module&gt; elements within an &lt;input&gt; element.
     */
    public void testGetSoapModules_input()
    {
        BindingMessageReference bindMsgRef = fBindOper.getBindingMessageReferences()[0];
        assertNotNull("The BindingOperation does not contain a BindingMessageReference.", bindMsgRef);

        BindingMessageReferenceElement bindMsgRefEl = bindMsgRef.toElement();
        Direction direction = bindMsgRefEl.getDirection();
        assertTrue("The BindingMessageReference does not represent an <input> element.", Direction.IN.equals(direction));

        SOAPBindingMessageReferenceExtensions soapBindMsgRefExts = 
            (SOAPBindingMessageReferenceExtensions) bindMsgRef.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        SOAPModule[] actual = soapBindMsgRefExts.getSoapModules();
        assertEquals("Unexpected number of SOAPModule objects.", 2, actual.length);
    }

    /**
     * Test that the <code>getSoapModules</code> method returns the expected number of SOAPModule objects 
     * parsed from &lt;wsoap:module&gt; elements within an &lt;output&gt; element.
     */
    public void testGetSoapModules_output()
    {
        BindingMessageReference bindMsgRef = fBindOper.getBindingMessageReferences()[1];
        assertNotNull("The BindingOperation does not contain a second BindingMessageReference.", bindMsgRef);

        BindingMessageReferenceElement bindMsgRefEl = bindMsgRef.toElement();
        Direction direction = bindMsgRefEl.getDirection();
        assertTrue("The BindingMessageReference does not represent an <output> element.", Direction.OUT.equals(direction));

        SOAPBindingMessageReferenceExtensions soapBindMsgRefExts = 
            (SOAPBindingMessageReferenceExtensions) bindMsgRef.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        SOAPModule[] actual = soapBindMsgRefExts.getSoapModules();
        assertEquals("Unexpected number of SOAPModule objects.", 1, actual.length);
    }

    /**
     * Test that the <code>getSoapHeaders</code> method returns the expected number of SOAPHeader objects 
     * parsed from &lt;wsoap:header&gt; elements within an &lt;input&gt; element.
     */
    public void testGetSoapHeaders_input()
    {
        BindingMessageReference bindMsgRef = fBindOper.getBindingMessageReferences()[0];
        assertNotNull("The BindingOperation does not contain a BindingMessageReference.", bindMsgRef);

        SOAPBindingMessageReferenceExtensions soapBindMsgRefExts = 
            (SOAPBindingMessageReferenceExtensions) bindMsgRef.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        SOAPHeaderBlock[] actual = soapBindMsgRefExts.getSoapHeaders();
        assertEquals("Unexpected number of SOAPHeaderBlock objects.", 2, actual.length);
    }

    /**
     * Test that the <code>getSoapHeaders</code> method returns the expected number of SOAPHeader objects 
     * parsed from &lt;wsoap:header&gt; elements within an &lt;output&gt; element.
     */
    public void testGetSoapHeaders_output()
    {
        BindingMessageReference bindMsgRef = fBindOper.getBindingMessageReferences()[1];
        assertNotNull("The BindingOperation does not contain the expected BindingMessageReference.", bindMsgRef);

        SOAPBindingMessageReferenceExtensions soapBindMsgRefExts = 
            (SOAPBindingMessageReferenceExtensions) bindMsgRef.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        SOAPHeaderBlock[] actual = soapBindMsgRefExts.getSoapHeaders();
        assertEquals("Unexpected number of SOAPHeaderBlock objects.", 1, actual.length);
    }

}

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
import java.util.Arrays;

import javax.xml.namespace.QName;

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
 * Functional verification test of SoapBindingFaultExtensions.
 * Checks that the expected API behaviour is supported by the implementation.
 * 
 * @author John Kaputin (jkaputin@apache.org)
 */
public class SOAPBindingFaultExtensionsTest extends TestCase 
{
    private WSDLFactory fFactory = null;
    private WSDLReader fReader = null;
    private ErrorHandler fHandler = null;
    private DescriptionElement fDescElem = null;
    
    private Binding fBinding = null;
    private String fWsdlPath = "org/apache/woden/wsdl20/extensions/soap/resources/SOAPBindingFaultExtensions.wsdl";
    
    public static Test suite()
    {
        return new TestSuite(SOAPBindingFaultExtensionsTest.class);
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
        
        fBinding = descComp.getBindings()[0];
        assertNotNull("The Description does not contain a Binding.", fBinding);
        assertEquals("The Binding contains an unexpected number of BindingFaults.", 3, fBinding.getBindingFaults().length);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    /**
     * Test that a <code>wsoap:code</code> extension attribute with a value of type xs:QName is represented 
     * in the Component model extensions by the expected QName. 
     */
    public void testSoapFaultCode_QName()
    {
        BindingFault bindFault = fBinding.getBindingFaults()[0];
        SOAPBindingFaultExtensions soapBindFaultExts = 
            (SOAPBindingFaultExtensions) bindFault.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        SOAPFaultCode soapFaultCode = soapBindFaultExts.getSoapFaultCode();
        
        assertNotNull("The SOAPBindingFaultExtensions did not return a SOAPFaultCode.", soapFaultCode);
        assertTrue("The SOAPFaultCode does not represent a QName.", soapFaultCode.isQName());
        assertEquals("The QName represented by the SOAPFaultCode is not the expected one.",
                new QName("http://www.w3.org/2003/05/soap-envelope","fault1"),
                soapFaultCode.getQName());
    }
        
    /**
     * Test that a <code>wsoap:code</code> extension attribute with a value of type xs:token "#any" is 
     * represented in the Component model extensions by SOAPFaultCode.ANY. 
     */
    public void testSoapFaultCode_TokenAny()
    {
        BindingFault bindFault = fBinding.getBindingFaults()[1];
        SOAPBindingFaultExtensions soapBindFaultExts = 
            (SOAPBindingFaultExtensions) bindFault.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        SOAPFaultCode soapFaultCode = soapBindFaultExts.getSoapFaultCode();
        
        assertNotNull("The SOAPBindingFaultExtensions did not return a SOAPFaultCode.", soapFaultCode);
        assertEquals("The SOAPFaultCode does not represent the xs:token #any.", 
                SOAPFaultCode.ANY,
                soapFaultCode);
    }
        
    /**
     * Test that if the <code>wsoap:code</code> extension attribute is omitted from the WSDL, the {soap fault code}
     * property for the corresponding binding fault defaults to token "#any". 
     * This should be represented in the Component model extensions by SOAPFaultCode.ANY. 
     */
    public void testSoapFaultCode_Default()
    {
        BindingFault bindFault = fBinding.getBindingFaults()[2];
        SOAPBindingFaultExtensions soapBindFaultExts = 
            (SOAPBindingFaultExtensions) bindFault.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        SOAPFaultCode soapFaultCode = soapBindFaultExts.getSoapFaultCode();
        
        assertNotNull("The SOAPBindingFaultExtensions did not return a SOAPFaultCode.", soapFaultCode);
        assertEquals("The wsoap:code extension attribute was omitted, so SOAPFaultCode.ANY was expected by default.", 
                SOAPFaultCode.ANY,
                soapFaultCode);
    }

    /**
     * Test that a <code>wsoap:subcodes</code> extension attribute with a value of type list of xs:QName is represented 
     * in the Component model extensions by an array of the expected QNames. 
     */
    public void testSoapFaultSubcodes_QNames()
    {
        BindingFault bindFault = fBinding.getBindingFaults()[0];
        SOAPBindingFaultExtensions soapBindFaultExts = 
            (SOAPBindingFaultExtensions) bindFault.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        SOAPFaultSubcodes soapFaultSubcodes = soapBindFaultExts.getSoapFaultSubcodes();
        
        assertNotNull("The SOAPBindingFaultExtensions did not return a SOAPFaultSubcodes.", soapFaultSubcodes);
        assertTrue("The SOAPFaultSubcodes does not represent a QName.", soapFaultSubcodes.isQNames());
        assertTrue("The QNames represented by the SOAPFaultSubcodes are not the expected ones.",
                   Arrays.equals( soapFaultSubcodes.getQNames(),
                                  new QName[] {new QName("http://www.w3.org/2003/05/soap-envelope","ABC"),
                                               new QName("http://www.w3.org/2003/05/soap-envelope","JKL"),
                                               new QName("http://www.w3.org/2003/05/soap-envelope","XYZ")} )
                  );
    }

    /**
     * Test that a <code>wsoap:subcodes</code> extension attribute with a value of type xs:token "#any" is 
     * represented in the Component model extensions by SOAPFaultSubcodes.ANY. 
     */
    public void testSoapFaultSubcodes_TokenAny()
    {
        BindingFault bindFault = fBinding.getBindingFaults()[1];
        SOAPBindingFaultExtensions soapBindFaultExts = 
            (SOAPBindingFaultExtensions) bindFault.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        SOAPFaultSubcodes soapFaultSubcodes = soapBindFaultExts.getSoapFaultSubcodes();
        
        assertNotNull("The SOAPBindingFaultExtensions did not return a SOAPFaultSubcodes.", soapFaultSubcodes);
        assertEquals("The SOAPFaultSubcodes does not represent the xs:token #any.", 
                SOAPFaultSubcodes.ANY,
                soapFaultSubcodes);
    }
    
    /**
     * Test that if the <code>wsoap:subcodes</code> extension attribute is omitted from the WSDL, the {soap fault subcodes}
     * property for the corresponding binding fault defaults to token "#any". 
     * This should be represented in the Component model extensions by SOAPFaultSubcodes.ANY. 
     */
    public void testSoapFaultSubcodes_Default()
    {
        BindingFault bindFault = fBinding.getBindingFaults()[2];
        SOAPBindingFaultExtensions soapBindFaultExts = 
            (SOAPBindingFaultExtensions) bindFault.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        SOAPFaultSubcodes soapFaultSubcodes = soapBindFaultExts.getSoapFaultSubcodes();
        
        assertNotNull("The SOAPBindingFaultExtensions did not return a SOAPFaultSubcodes.", soapFaultSubcodes);
        assertEquals("The SOAPFaultSubcodes does not represent the xs:token #any.", 
                SOAPFaultSubcodes.ANY,
                soapFaultSubcodes);
    }
    
    /**
     * Test that the <code>getSoapModules</code> method returns the expected number of SOAPModule objects 
     * parsed from &lt;wsoap:module&gt; elements within a binding &lt;fault&gt; element.
     */
    public void testGetSoapModules()
    {
        BindingFault bindFault = fBinding.getBindingFaults()[1];
        SOAPBindingFaultExtensions soapBindFaultExts = 
            (SOAPBindingFaultExtensions) bindFault.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        SOAPModule[] actual = soapBindFaultExts.getSoapModules();
        assertEquals("Unexpected number of SOAPModule objects.", 3, actual.length);
    }

    /**
     * Test that the <code>getSoapHeaders</code> method returns the expected number of SOAPHeaderBlock 
     * objects parsed from &lt;wsoap:header&lt; elements within a binding &lt;fault&gt; element.
     */
    public void testGetSoapHeaders()
    {
        BindingFault bindFault = fBinding.getBindingFaults()[2];
        SOAPBindingFaultExtensions soapBindFaultExts = 
            (SOAPBindingFaultExtensions) bindFault.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        SOAPHeaderBlock[] actual = soapBindFaultExts.getSoapHeaders();
        assertEquals("Unexpected number of SOAPHeaderBlock objects.", 2, actual.length);
    }

}

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
import org.apache.woden.wsdl20.BindingMessageReference;
import org.apache.woden.wsdl20.BindingOperation;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.extensions.ComponentExtensions;
import org.apache.woden.wsdl20.xml.DescriptionElement;

/**
 * Functional verification test of HTTPBindingMessageReferenceExtensions.
 * Checks that the expected API behaviour is supported by the implementation.
 * 
 * @author John Kaputin (jkaputin@apache.org)
 */
public class HTTPBindingMessageReferenceExtensionsTest extends TestCase {

    private BindingOperation[] fBindOpers = null;
    private String fWsdlPath = 
        "org/apache/woden/wsdl20/extensions/http/resources/HTTPBindingMessageReferenceExtensions.wsdl";

    public static Test suite()
    {
        return new TestSuite(HTTPBindingMessageReferenceExtensionsTest.class);
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
        
        Binding[] bindings = descComp.getBindings();
        assertEquals("The Description should contain 1 Binding component.", 1, bindings.length);
        
        fBindOpers = bindings[0].getBindingOperations();
        assertEquals("The Binding should contain 2 BindingOperation components.", 2, fBindOpers.length);
    }

    /**
     * Testcases for the {http transfer coding} property returned 
     * by the <code>getHttpTransferCoding</code> method.
     * <p>
     * 1. Test that the method returns "compress" if the whttp:transferCoding
     * attribute parsed from the WSDL contains "compress".
     * <p>
     * 2. Test that it defaults to the {http transfer coding default} property
     * of BindingOperation when the whttp:transferCoding attribute is omitted 
     * from the WSDL. In this test, whttp:transferCodingDefault is set to
     * "chunked".
     */
    public void testGetHttpTransferCoding() {
        
        BindingMessageReference[] bindMsgRefs = fBindOpers[0].getBindingMessageReferences();
        assertEquals("The first BindingOperation should contain 2 BindingMessageReference components.", 2, bindMsgRefs.length);
        
        //1. test that the property is parsed correctly from the WSDL
        BindingMessageReference inputMsg = bindMsgRefs[0];
        HTTPBindingMessageReferenceExtensions httpBindMsgRefExts = 
            (HTTPBindingMessageReferenceExtensions) inputMsg
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual = httpBindMsgRefExts.getHttpTransferCoding();
        assertEquals("Unexpected value for http transfer coding.",
                "compress",
                actual);
        
        //2. test the default 
        BindingMessageReference outputMsg = bindMsgRefs[1];
        HTTPBindingMessageReferenceExtensions httpBindMsgRefExts2 = 
            (HTTPBindingMessageReferenceExtensions) outputMsg
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual2 = httpBindMsgRefExts2.getHttpTransferCoding();
        assertEquals("Unexpected default value for http transfer coding.",
                "chunked",
                actual2);
    }

    /**
     * Testcases for the {http headers} property returned 
     * by the <code>getHttpHeaders</code> method.
     * <p>
     * 1. Test that the method returns an array size 2 when there are
     * two whttp:header extension elements in the WSDL.
     * <p>
     * 2. Test that the method returns an array size 1 when there is
     * one whttp:header extension element in the WSDL.
     */
    public void testGetHttpHeaders() {
        
        BindingMessageReference[] bindMsgRefs = fBindOpers[0].getBindingMessageReferences();
        assertEquals("The first BindingOperation should contain 2 BindingMessageReference components.", 2, bindMsgRefs.length);
        
        //1. test for 2 HTTPHeader components
        BindingMessageReference inputMsg = bindMsgRefs[0];
        HTTPBindingMessageReferenceExtensions httpBindMsgRefExts = 
            (HTTPBindingMessageReferenceExtensions) inputMsg
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        HTTPHeader[] actual = httpBindMsgRefExts.getHttpHeaders();
        assertEquals("Unexpected size of {http headers}.",
                2,
                actual.length);
        
        //2. test the default 
        BindingMessageReference outputMsg = bindMsgRefs[1];
        HTTPBindingMessageReferenceExtensions httpBindMsgRefExts2 = 
            (HTTPBindingMessageReferenceExtensions) outputMsg
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        HTTPHeader[] actual2 = httpBindMsgRefExts2.getHttpHeaders();
        assertEquals("Unexpected size of {http headers}.",
                1,
                actual2.length);
        
    }
    
}

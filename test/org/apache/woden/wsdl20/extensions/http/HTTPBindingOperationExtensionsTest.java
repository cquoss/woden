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
import org.apache.woden.wsdl20.BindingOperation;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.extensions.ComponentExtensions;
import org.apache.woden.wsdl20.xml.DescriptionElement;

/**
 * Functional verification test of HTTPBindingOperationExtensions.
 * Checks that the expected API behaviour is supported by the implementation.
 * 
 * @author John Kaputin (jkaputin@apache.org)
 */
public class HTTPBindingOperationExtensionsTest extends TestCase {

    private BindingOperation[] fBindOpers = null;
    private String fWsdlPath = "org/apache/woden/wsdl20/extensions/http/resources/HTTPBindingOperationExtensions.wsdl";

    public static Test suite()
    {
        return new TestSuite(HTTPBindingOperationExtensionsTest.class);
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
        assertEquals("The Binding should contain 7 BindingOperation components.", 7, fBindOpers.length);
    }

    /**
     * Testcases for the {http location} property returned by the
     * <code>getHttpLocation</code> method.
     * <p>
     * Test that <code>getHttpLocation</code> returns the URI specified 
     * in the whttp:location attribute parsed from the WSDL.
     * <p>
     * Test that it returns null by default when the attribute is omitted 
     * from the WSDL.
     */
    public void testGetHttpLocation() {
        
        //test that the property is parsed correctly from the WSDL
        BindingOperation oper = fBindOpers[0];
        HTTPBindingOperationExtensions httpBindOperExts = 
            (HTTPBindingOperationExtensions) oper
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        URI expected = URI.create("http://ws.apache.woden/location");
        URI actual = httpBindOperExts.getHttpLocation();
        assertEquals("Unexpected value for http location",
                expected,
                actual);
        
        //test that the property defaults to null
        BindingOperation oper2 = fBindOpers[1];
        HTTPBindingOperationExtensions httpBindOperExts2 = 
            (HTTPBindingOperationExtensions) oper2
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        URI actual2 = httpBindOperExts2.getHttpLocation();
        assertNull("Http location did not default to null", actual2);
    }

    /**
     * Testcases for the {http location ignore uncited} property returned 
     * by the <code>isHttpLocationIgnoreUncited</code> method.
     * <p>
     * Test that the method returns True if the whttp:ignoreUncited 
     * attribute parsed from the WSDL contains "true".
     * <p>
     * Test that it returns False by default when the attribute is omitted 
     * from the WSDL.
     */
    public void testIsHttpLocationIgnoreUncited() {
        
        //test that the property is parsed correctly from the WSDL
        BindingOperation oper = fBindOpers[0];
        HTTPBindingOperationExtensions httpBindOperExts = 
            (HTTPBindingOperationExtensions) oper
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        Boolean actual = httpBindOperExts.isHttpLocationIgnoreUncited();
        assertTrue("Http location ignore uncited was not True", 
                actual.booleanValue());
        
        //test that the property defaults to false
        BindingOperation oper2 = fBindOpers[1];
        HTTPBindingOperationExtensions httpBindOperExts2 = 
            (HTTPBindingOperationExtensions) oper2
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        Boolean actual2 = httpBindOperExts2.isHttpLocationIgnoreUncited();
        assertFalse("Http location ignore uncited did not default to False", 
                actual2.booleanValue());
    }

    /**
     * Testcases for the {http method} property returned 
     * by the <code>getHttpMethod</code> method.
     * <p>
     * Test that the method returns GET if the whttp:method attribute
     * parsed from the WSDL contains "GET".
     * <p>
     * Test that it returns null by default when the attribute is omitted 
     * from the WSDL.
     * <p>
     * TODO if the spec is updated to move default behaviour from the
     * concreate binding into the component model, update this test
     * to reflect that change.
     */
    public void testGetHttpMethod() {
        
        //test that the property is parsed correctly from the WSDL
        BindingOperation oper = fBindOpers[0];
        HTTPBindingOperationExtensions httpBindOperExts = 
            (HTTPBindingOperationExtensions) oper
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual = httpBindOperExts.getHttpMethod();
        assertEquals("Unexpected value for http method.",
                "GET",
                actual);
        
        //test that the property defaults to null
        BindingOperation oper2 = fBindOpers[1];
        HTTPBindingOperationExtensions httpBindOperExts2 = 
            (HTTPBindingOperationExtensions) oper2
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual2 = httpBindOperExts2.getHttpMethod();
        assertNull("Http method was not null by default.",
                actual2);
    }

    /**
     * Testcases for the {http input serialization} property returned 
     * by the <code>getHttpInputSerialization</code> method.
     * <p>
     * 1. Test that the method returns text/plain if the whttp:inputSerialization
     * attribute parsed from the WSDL contains "text/plain".
     * <p>
     * 2. Test that it defaults to application/xml when the attribute is omitted 
     * from the WSDL and http method defaults to POST (which it will be for 
     * this operation because whttp:method is omitted so the binding rules 
     * default it to POST).
     * <p>
     * 3. Test that it defaults to application/x-www-form-urlencoded when the 
     * attribute is omitted from the WSDL and the http method is GET.
     * <p> 
     * 4. Test that it defaults to application/x-www-form-urlencoded when the 
     * attribute is omitted from the WSDL and the http method is DELETE.
     * <p> 
     * 5. Test that it defaults to application/xml when the 
     * attribute is omitted from the WSDL and the http method is PUT.
     * <p> 
     * 6. Test that it defaults to application/xml when the 
     * attribute is omitted from the WSDL and the http method is POST.
     * <p>
     * 7. Test that it defaults to application/xml when the 
     * attribute is omitted from the WSDL and the http method is CONNECT
     * (i.e. when http method is something other than GET, DELETE, PUT
     * or POST).
     */
    public void testGetHttpInputSerialization() {
        
        //1. test that the property is parsed correctly from the WSDL
        BindingOperation oper = fBindOpers[0];
        HTTPBindingOperationExtensions httpBindOperExts = 
            (HTTPBindingOperationExtensions) oper
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual = httpBindOperExts.getHttpInputSerialization();
        assertEquals("Unexpected value for http input serialization.",
                "text/plain",
                actual);
        
        //2. test that the property defaults to application/xml if the http method
        //defaults to POST.
        BindingOperation oper2 = fBindOpers[1];
        HTTPBindingOperationExtensions httpBindOperExts2 = 
            (HTTPBindingOperationExtensions) oper2
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual2 = httpBindOperExts2.getHttpInputSerialization();
        assertEquals("Unexpected default value for http input serialization.",
                "application/xml",
                actual2);
        
        //3. test that the property defaults to application/x-www-form-urlencoded
        //if the http method is GET.
        BindingOperation oper3 = fBindOpers[2];
        HTTPBindingOperationExtensions httpBindOperExts3 = 
            (HTTPBindingOperationExtensions) oper3
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual3 = httpBindOperExts3.getHttpInputSerialization();
        assertEquals("Unexpected default value for http input serialization.",
                "application/x-www-form-urlencoded",
                actual3);
        
        //4. test that the property defaults to application/x-www-form-urlencoded
        //if the http method is DELETE.
        BindingOperation oper4 = fBindOpers[3];
        HTTPBindingOperationExtensions httpBindOperExts4 = 
            (HTTPBindingOperationExtensions) oper4
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual4 = httpBindOperExts4.getHttpInputSerialization();
        assertEquals("Unexpected default value for http input serialization.",
                "application/x-www-form-urlencoded",
                actual4);
        
        //5. test that the property defaults to application/xml
        //if the http method is PUT.
        BindingOperation oper5 = fBindOpers[4];
        HTTPBindingOperationExtensions httpBindOperExts5 = 
            (HTTPBindingOperationExtensions) oper5
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual5 = httpBindOperExts5.getHttpInputSerialization();
        assertEquals("Unexpected default value for http input serialization.",
                "application/xml",
                actual5);
        
        //6. test that the property defaults to application/xml
        //if the http method is POST.
        BindingOperation oper6 = fBindOpers[5];
        HTTPBindingOperationExtensions httpBindOperExts6 = 
            (HTTPBindingOperationExtensions) oper6
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual6 = httpBindOperExts6.getHttpInputSerialization();
        assertEquals("Unexpected default value for http input serialization.",
                "application/xml",
                actual6);
        
        //7. test that the property defaults to application/xml
        //if the http method is CONNECT.
        BindingOperation oper7 = fBindOpers[6];
        HTTPBindingOperationExtensions httpBindOperExts7 = 
            (HTTPBindingOperationExtensions) oper7
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual7 = httpBindOperExts7.getHttpInputSerialization();
        assertEquals("Unexpected default value for http input serialization.",
                "application/xml",
                actual7);
        
    }

    /**
     * Testcases for the {http output serialization} property returned 
     * by the <code>getHttpOutputSerialization</code> method.
     * <p>
     * 1. Test that the method returns text/plain if the whttp:outputSerialization
     * attribute parsed from the WSDL contains "text/plain".
     * <p>
     * 2. Test that it defaults to application/xml when the attribute is omitted 
     * from the WSDL.
     */
    public void testGetHttpOutputSerialization() {
        
        //1. test that the property is parsed correctly from the WSDL
        BindingOperation oper = fBindOpers[0];
        HTTPBindingOperationExtensions httpBindOperExts = 
            (HTTPBindingOperationExtensions) oper
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual = httpBindOperExts.getHttpOutputSerialization();
        assertEquals("Unexpected value for http output serialization.",
                "text/plain",
                actual);
        
        //2. test that the property defaults to application/xml.
        BindingOperation oper2 = fBindOpers[1];
        HTTPBindingOperationExtensions httpBindOperExts2 = 
            (HTTPBindingOperationExtensions) oper2
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual2 = httpBindOperExts2.getHttpOutputSerialization();
        assertEquals("Unexpected default value for http output serialization.",
                "application/xml",
                actual2);
    }

    /**
     * Testcases for the {http fault serialization} property returned 
     * by the <code>getHttpFaultSerialization</code> method.
     * <p>
     * 1. Test that the method returns text/html if the whttp:faultSerialization
     * attribute parsed from the WSDL contains "text/html".
     * <p>
     * 2. Test that it defaults to application/xml when the attribute is omitted 
     * from the WSDL.
     */
    public void testGetHttpFaultSerialization() {
        
        //1. test that the property is parsed correctly from the WSDL
        BindingOperation oper = fBindOpers[0];
        HTTPBindingOperationExtensions httpBindOperExts = 
            (HTTPBindingOperationExtensions) oper
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual = httpBindOperExts.getHttpFaultSerialization();
        assertEquals("Unexpected value for http fault serialization.",
                "text/html",
                actual);
        
        //2. test that the property defaults to application/xml.
        BindingOperation oper2 = fBindOpers[1];
        HTTPBindingOperationExtensions httpBindOperExts2 = 
            (HTTPBindingOperationExtensions) oper2
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual2 = httpBindOperExts2.getHttpFaultSerialization();
        assertEquals("Unexpected default value for http fault serialization.",
                "application/xml",
                actual2);
    }

    /**
     * Testcases for the {http query parameter separator} property returned 
     * by the <code>getHttpQueryParameterSeparator</code> method.
     * <p>
     * 1. Test that the method returns "%" if the whttp:queryParameterSeparator
     * attribute parsed from the WSDL contains "%".
     * <p>
     * 2. Test that it defaults to ampersand when the attribute is omitted 
     * from the WSDL.
     */
    public void testGetHttpQueryParameterSeparator() {
        
        //1. test that the property is parsed correctly from the WSDL
        BindingOperation oper = fBindOpers[0];
        HTTPBindingOperationExtensions httpBindOperExts = 
            (HTTPBindingOperationExtensions) oper
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual = httpBindOperExts.getHttpQueryParameterSeparator();
        assertEquals("Unexpected value for http query parameter separator.",
                "%",
                actual);
        
        //2. test that the property defaults to ampersand.
        BindingOperation oper2 = fBindOpers[1];
        HTTPBindingOperationExtensions httpBindOperExts2 = 
            (HTTPBindingOperationExtensions) oper2
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual2 = httpBindOperExts2.getHttpQueryParameterSeparator();
        assertNull("Expected a null value for http query parameter separator.",
                actual2);
    }

    /**
     * Testcases for the {http transfer coding default} property returned 
     * by the <code>getHttpTransferCodingDefault</code> method.
     * <p>
     * 1. Test that the method returns "chunked" if the whttp:transferCodingDefault
     * attribute parsed from the WSDL contains "chunked".
     * <p>
     * 2. Test that it defaults to null when the attribute is omitted 
     * from the WSDL.
     */
    public void testGetHttpTransferCodingDefault() {
        
        //1. test that the property is parsed correctly from the WSDL
        BindingOperation oper = fBindOpers[0];
        HTTPBindingOperationExtensions httpBindOperExts = 
            (HTTPBindingOperationExtensions) oper
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual = httpBindOperExts.getHttpTransferCodingDefault();
        assertEquals("Unexpected value for http transfer coding default.",
                "chunked",
                actual);
        
        //2. test that the property defaults to null.
        BindingOperation oper2 = fBindOpers[1];
        HTTPBindingOperationExtensions httpBindOperExts2 = 
            (HTTPBindingOperationExtensions) oper2
                .getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_HTTP);
        
        String actual2 = httpBindOperExts2.getHttpTransferCodingDefault();
        assertNull("Http transfer coding default did not default to null.",
                actual2);
    }

}

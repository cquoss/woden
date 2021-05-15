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
package org.apache.woden.xml;

import javax.xml.namespace.QName;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.woden.ErrorInfo;
import org.apache.woden.ErrorReporter;
import org.apache.woden.internal.ErrorReporterImpl;
import org.apache.woden.internal.xml.IntOrTokenAnyAttrImpl;
import org.apache.woden.tests.TestErrorHandler;
import org.w3c.dom.Element;

/**
 * Functional verification test of IntOrTokenAttr.
 * Checks that the expected API behaviour is supported by the implementation.
 * 
 * @author jkaputin@apache.org
 */
public class IntOrTokenAttrTest extends TestCase 
{
    private ErrorReporter reporter;
    private TestErrorHandler handler;
    private Element el = null; //Element arg not used to initialize this attr
    private QName qn = new QName("http://wsdl/http","code","whttp");
    private IntOrTokenAttr attr = null;

    public static Test suite()
    {
        
         return new TestSuite(IntOrTokenAttrTest.class);
    }
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception 
    {
        super.setUp();
        handler = new TestErrorHandler();
        reporter = new ErrorReporterImpl();
        reporter.setErrorHandler(handler);
        attr = new IntOrTokenAnyAttrImpl();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception 
    {
        super.tearDown();
        handler = null;
        reporter = null;
        attr = null;
    }
    
    public void testAttributeType() throws Exception
    {
        String attrVal = "#any";
        attr.init(el, qn, attrVal);
        String expectedQN = qn.toString();
        String actualQN = attr.getAttributeType().toString();
        assertEquals("Expected attribute type qname '" + expectedQN + "' but actual qname was '" + actualQN + "'.",
                expectedQN, 
                actualQN );
    }
    
    public void testValidToken() throws Exception
    {
        String attrVal = "#any";
        attr.init(el, qn, attrVal);
        
        assertTrue("isValid() should return true", attr.isValid());
        assertTrue("getContent() should return instance of 'String' but actual object type was '" + attr.getContent().getClass().getName() + "'.", 
                attr.getContent() instanceof String);
        assertEquals("Expected toExternalForm() to return '" + attrVal + "' but actual value was '" + attr.toExternalForm() + "'.",
                attrVal,
                attr.toExternalForm() );
        
        assertTrue("isToken() should return true", attr.isToken());
        assertEquals("Expected getToken() to return the string '#any', but actual string was '" + attr.getToken() + "'.",
                "#any",
                attr.getToken() );
        assertFalse("isInt() should return false", attr.isInt());
        assertNull("getInt() should return null", attr.getInt());
    }

    public void testValidInt() throws Exception
    {
        String attrVal = "123";
        attr.init(el, qn, attrVal);
        
        assertTrue("isValid() should return true", attr.isValid());
        assertTrue("getContent() should return instance of 'Integer' but actual object type was '" + attr.getContent().getClass().getName() + "'.", 
                attr.getContent() instanceof Integer);
        assertEquals("Expected toExternalForm() to return '" + attrVal + "' but actual value was '" + attr.toExternalForm() + "'.",
                attrVal,
                attr.toExternalForm() );
        
        assertTrue("isInt() should return true", attr.isInt());
        assertEquals("Expected getInt() to return 123, but actual value was '" + attr.getInt() + "'.",
                new Integer("123"),
                attr.getInt() );
        assertFalse("isToken() should return false", attr.isToken());
        assertNull("getToken() should return null", attr.getToken());
    }

    public void testInvalidValue() throws Exception
    {
        String attrVal = "#rubbish";
        attr.setErrorReporter(reporter); //to check expected error msg
        attr.init(el, qn, attrVal);
        
        ErrorInfo errInfo = (ErrorInfo)handler.errors.get("WSDL512");
        assertNotNull("An error should have been reported", errInfo);
        assertEquals("Error 'WSDL512' expected but error reported was '" + errInfo.getKey() + "'.",
                "WSDL512",
                errInfo.getKey());
        
        assertFalse("isValid() should return false", attr.isValid());
        assertFalse("isInt() should return false", attr.isInt());
        assertFalse("isToken() should return false", attr.isToken());
        assertNull("getInt() should return null", attr.getInt());
        assertNull("getToken() should return null", attr.getToken());
    }

    public void testNullValue() throws Exception
    {
        String attrVal = null;
        attr.setErrorReporter(reporter); //to check expected error msg
        attr.init(el, qn, attrVal);
        
        ErrorInfo errInfo = (ErrorInfo)handler.errors.get("WSDL512");
        assertNotNull("An error should have been reported", errInfo);
        assertEquals("Error 'WSDL512' expected but error reported was '" + errInfo.getKey() + "'.",
                "WSDL512",
                errInfo.getKey());
        
        assertFalse("isValid() should return false", attr.isValid());
        assertFalse("isInt() should return false", attr.isInt());
        assertFalse("isToken() should return false", attr.isToken());
        assertNull("getInt() should return null", attr.getInt());
        assertNull("getToken() should return null", attr.getToken());
    }
}

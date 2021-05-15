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
import org.apache.woden.internal.xml.TokenAttrImpl;
import org.apache.woden.tests.TestErrorHandler;
import org.w3c.dom.Element;

/**
 * Functional verification test of TokenAttr.
 * Checks that the expected API behaviour is supported by the implementation.
 * 
 * @author jkaputin@apache.org
 */
public class TokenAttrTest extends TestCase 
{
    private ErrorReporter reporter;
    private TestErrorHandler handler;
    private Element el = null; //Element arg not used to initialize this attr
    private QName qn = new QName("http://wsdl/http","authenticationType","whttp");;
    private TokenAttr attr = null;

    public static Test suite()
    {
         return new TestSuite(TokenAttrTest.class);
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
        attr = new TokenAttrImpl();
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
        
        assertNotNull("getToken() should not return null", attr.getToken());
        assertEquals("Expected attribute value '#any' from getToken(), but actual value was '" + attr.getToken() + "'.",
                   "#any",
                   attr.getToken() );
        assertTrue("Expected attribute value object to be a 'String' but actual object type was '" + attr.getContent().getClass().getName() + "'.", 
                   attr.getContent() instanceof String);
        assertTrue("Expected external form '#any' but actual was '" + attr.toExternalForm() + "'.",
                     "#any".equals(attr.toExternalForm()) );
    }

    public void testNullValue() throws Exception
    {
        String attrVal = null;
        attr.setErrorReporter(reporter); //to check expected error msg
        attr.init(el, qn, attrVal);
        
        ErrorInfo errInfo = (ErrorInfo)handler.errors.get("WSDL508");
        assertNotNull("An error should have been reported", errInfo);
        assertEquals("Error 'WSDL508' expected but error reported was '" + errInfo.getKey() + "'.",
                "WSDL508",
                errInfo.getKey());
        
        assertNull("Attribute value is not null", attr.getToken());
        assertFalse("Attribute value should not be valid", attr.isValid());
    }

}

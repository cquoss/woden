/**
 * Copyright 2005 Apache Software Foundation 
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
package javax.xml.namespace;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * junit tests for the QName class.
 * 
 * @author jkaputin@apache.org
 */
public class QNameTest extends TestCase {

    private static final String emptyString = "";
    private QName qname;
    
    
    public static Test suite()
    {
        return new TestSuite(QNameTest.class);
    }

    /* ************************************************
     * Tests for ctor QName(locPart)
     * ************************************************/
    
    public void testOneArgCtorGoodLocalPart()
    {
        qname = new QName("myLocalPart");
        
        if(qname != null)
        {
            assertTrue("QName was not initialized correctly",
                       (qname.getNamespaceURI().equals(emptyString) &&
                        qname.getLocalPart().equals("myLocalPart") &&
                        qname.getPrefix().equals(emptyString))
                      );
        }
        else
        {
            fail("QName was not instantiated.");
        }
    }

    public void testOneArgCtorGoodLocalPartEmptyString()
    {
        qname = new QName("");
        
        if(qname != null)
        {
            assertTrue("QName was not initialized correctly",
                       (qname.getNamespaceURI().equals(emptyString) &&
                        qname.getLocalPart().equals(emptyString) &&
                        qname.getPrefix().equals(emptyString))
                      );
        }
        else
        {
            fail("QName was not instantiated.");
        }
    }

    public void testOneArgCtorBadLocalPartNull()
    {
        boolean b = false;
        
        try {
            qname = new QName(null);
        } catch (IllegalArgumentException e) {
            b = true;
        }
        
        assertTrue("Expected an IllegalArgumentException because of null localPart.", b);
    }

    /* ************************************************
     * Tests for ctor QName(NS, locPart)
     * ************************************************/
    
    public void testTwoArgCtorGoodNonEmptyStrings()
    {
        qname = new QName("myNamespace", "myLocalPart");
        
        if(qname != null)
        {
            assertTrue("QName was not initialized correctly",
                       (qname.getNamespaceURI().equals("myNamespace") &&
                        qname.getLocalPart().equals("myLocalPart") &&
                        qname.getPrefix().equals(emptyString))
                      );
        }
        else
        {
            fail("QName was not instantiated.");
        }
    }

    public void testTwoArgCtorGoodEmptyStrings()
    {
        qname = new QName("", "");
        
        if(qname != null)
        {
            assertTrue("QName was not initialized correctly",
                       (qname.getNamespaceURI().equals(emptyString) &&
                        qname.getLocalPart().equals(emptyString) &&
                        qname.getPrefix().equals(emptyString))
                      );
        }
        else
        {
            fail("QName was not instantiated.");
        }
    }

    public void testTwoArgCtorGoodNullNamespaceURI()
    {
        qname = new QName(null, "myLocalPart");
        
        if(qname != null)
        {
            assertTrue("QName was not initialized correctly",
                       (qname.getNamespaceURI().equals(emptyString) &&
                        qname.getLocalPart().equals("myLocalPart") &&
                        qname.getPrefix().equals(emptyString))
                      );
        }
        else
        {
            fail("QName was not instantiated.");
        }
    }
    
    public void testTwoArgCtorBadNullLocalPart()
    {
        boolean b = false;
        
        try {
            qname = new QName("myNamespace", null);
        } catch (IllegalArgumentException e) {
            b = true;
        }
        
        assertTrue("Expected an IllegalArgumentException because of null localPart.", b);
    }
    
    /* ************************************************
     * Tests for ctor QName(NS, locPart, prefix)
     * ************************************************/
    
    public void testThreeArgCtorGoodNonEmptyStrings()
    {
        qname = new QName("myNamespace", "myLocalPart", "myPrefix");
        
        if(qname != null)
        {
            assertTrue("QName was not initialized correctly",
                       (qname.getNamespaceURI().equals("myNamespace") &&
                        qname.getLocalPart().equals("myLocalPart") &&
                        qname.getPrefix().equals("myPrefix"))
                      );
        }
        else
        {
            fail("QName was not instantiated.");
        }
    }

    public void testThreeArgCtorGoodEmptyStrings()
    {
        qname = new QName("", "", "");
        
        if(qname != null)
        {
            assertTrue("QName was not initialized correctly",
                       (qname.getNamespaceURI().equals(emptyString) &&
                        qname.getLocalPart().equals(emptyString) &&
                        qname.getPrefix().equals(emptyString))
                      );
        }
        else
        {
            fail("QName was not instantiated.");
        }
    }

    public void testThreeArgCtorGoodNullNamespaceURI()
    {
        qname = new QName(null, "myLocalPart", "myPrefix");
        
        if(qname != null)
        {
            assertTrue("QName was not initialized correctly",
                       (qname.getNamespaceURI().equals(emptyString) &&
                        qname.getLocalPart().equals("myLocalPart") &&
                        qname.getPrefix().equals("myPrefix"))
                      );
        }
        else
        {
            fail("QName was not instantiated.");
        }
    }
    
    public void testThreeArgCtorBadNullLocalPart()
    {
        boolean b = false;
        
        try {
            qname = new QName("myNamespace", null, "myPrefix");
        } catch (IllegalArgumentException e) {
            b = true;
        }
        
        assertTrue("Expected an IllegalArgumentException because of null localPart.", b);
    }
    
    public void testThreeArgCtorBadNullPrefix()
    {
        boolean b = false;
        
        try {
            qname = new QName("myNamespace", "myLocalPart", null);
        } catch (IllegalArgumentException e) {
            b = true;
        }
        
        assertTrue("Expected an IllegalArgumentException because of null prefix.", b);
    }

    /* ************************************************
     * Tests for valueOf(String) method
     * ************************************************/
    
    public void testValueOfGoodNamespaceAndLocalPart()
    {
        qname = QName.valueOf("{myNamespace}myLocalPart");
        
        if(qname != null)
        {
            assertTrue("QName was not initialized correctly",
                       (qname.getNamespaceURI().equals("myNamespace") &&
                        qname.getLocalPart().equals("myLocalPart") &&
                        qname.getPrefix().equals(emptyString))
                      );
        }
        else
        {
            fail("QName was not instantiated.");
        }
    }
    
    public void testValueOfGoodLocalPartOnly()
    {
        qname = QName.valueOf("myLocalPart");
        
        if(qname != null)
        {
            assertTrue("QName was not initialized correctly",
                       (qname.getNamespaceURI().equals(emptyString) &&
                        qname.getLocalPart().equals("myLocalPart") &&
                        qname.getPrefix().equals(emptyString))
                      );
        }
        else
        {
            fail("QName was not instantiated.");
        }
    }
    
    public void testValueOfBadNullString()
    {
        boolean b = false;
        
        try {
            qname = QName.valueOf(null);
        } catch (IllegalArgumentException e) {
            b = true;
        }
        
        assertTrue("Expected an IllegalArgumentException because " +
                   "qname string argument cannot be null.",
                   b);
    }
    
    public void testValueOfBadEmptyString()
    {
        boolean b = false;
        
        try {
            qname = QName.valueOf("");
        } catch (IllegalArgumentException e) {
            b = true;
        }
        
        assertTrue("Expected an IllegalArgumentException because " +
                   "qname string argument cannot be the empty string \"\".",
                   b);
    }
    
    public void testValueOfBadLeftBraceNotLeftmost()
    {
        boolean b = false;
        
        try {
            qname = QName.valueOf("x{myNamespace}myLocalPart");
        } catch (IllegalArgumentException e) {
            b = true;
        }
        
        assertTrue("Expected an IllegalArgumentException because " +
                   "'{' is not the leftmost character.",
                   b);
    }
    
    public void testValueOfBadMultipleLeftBraces()
    {
        boolean b = false;
        
        try {
            qname = QName.valueOf("{myNam{espace}myLocalPart");
        } catch (IllegalArgumentException e) {
            b = true;
        }
        
        assertTrue("Expected an IllegalArgumentException because " +
                   "there is more that one '{'.",
                   b);
    }
    
    public void testValueOfBadUnmatchedLeftBrace()
    {
        boolean b = false;
        
        try {
            qname = QName.valueOf("{myNamespacemyLocalPart");
        } catch (IllegalArgumentException e) {
            b = true;
        }
        
        assertTrue("Expected an IllegalArgumentException because " +
                   "a '{' does not have a matching '}'.",
                   b);
    }
    
    public void testValueOfBadUnmatchedRightBrace()
    {
        boolean b = false;
        
        try {
            qname = QName.valueOf("myNamespace}myLocalPart");
        } catch (IllegalArgumentException e) {
            b = true;
        }
        
        assertTrue("Expected an IllegalArgumentException because " +
                   "a '}' does not have a matching '{'.",
                   b);
    }

    public void testValueOfBadMultipleRightBraces()
    {
        boolean b = false;
        
        try {
            qname = QName.valueOf("{myNam}espace}myLocalPart");
        } catch (IllegalArgumentException e) {
            b = true;
        }
        
        assertTrue("Expected an IllegalArgumentException because " +
                   "there is more that one '}'.",
                   b);
    }
    
    public void testValueOfBadNoLocalpart()
    {
        boolean b = false;
        
        try {
            qname = QName.valueOf("{myNamespace}");
        } catch (IllegalArgumentException e) {
            b = true;
        }
        
        assertTrue("Expected an IllegalArgumentException because " +
                   "the string only has a namespaceURI, but no localPart.",
                   b);
    }

    /* ************************************************
     * Tests for deserializing.
     * ************************************************/
    
    public void testGoodSerializeThenDeserialize() throws IOException,
                                                          ClassNotFoundException
    {
        //Serialize a QName then deserialize it to test it has been initialized 
        //correctly by the readObject() method.
        
        qname = new QName("myNamespace", "myLocalPart", "myPrefix");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(qname);
        oos.close();

        QName qnameCopy = null;
        
        ObjectInputStream ois =
            new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        Object o = ois.readObject();
        qnameCopy = (QName)o;
        ois.close();

        assertTrue("QName was not initialized correctly after deserialization.",
                (qnameCopy.getNamespaceURI().equals("myNamespace") &&
                 qnameCopy.getLocalPart().equals("myLocalPart") &&
                 qnameCopy.getPrefix().equals("myPrefix")));
        
    }
    
    public void testGoodDeserializeQNameWithoutPrefix() throws IOException, 
                                                               ClassNotFoundException, Exception
    {
        /* 
         * Test that 'prefix' is initialized correctly when deserializing a v1.0 QName.
         * The v1.0 QName does not have a 'prefix' field, so when it's deserialized using
         * the v1.1 QName, its 'prefix' field should be set to the empty string "", not 
         * just initialized as null.
         * 
         * The input to this testcase is a file containing a v1.0 QName serialized 
         * from the WSDL4J version of QName.
         */
        
        URL url = getClass().getClassLoader().getResource("javax/xml/namespace/serialized_QName_no_prefix");

        File f = new File(url.getFile());
        
        ObjectInputStream ois =
            new ObjectInputStream(new FileInputStream(f));
        
        Object o = ois.readObject();
        qname = (QName)o;
        
        assertTrue("Expected a null 'prefix' to be initialized to the empty string \"\".",
                qname.getPrefix() != null && qname.getPrefix().equals(emptyString));
            
    }
    

    /* ************************************************
     * Tests for equals(obj) method
     * ************************************************/
    
    public void testEqualsGoodSameValues()
    {
        QName qn1, qn2;
        qn1 = new QName("myNamespace", "myLocalPart");
        qn2 = QName.valueOf("{myNamespace}myLocalPart");
        
        assertTrue("Expected two QNames objects with the same values to be equal.",
                   qn1.equals(qn2));
    }

    public void testEqualsGoodDifferentPrefix()
    {
        /* the prefix is not used to determine equality, so even though the prefixes
         * are different, the namespacesURIs and localParts are equivalent so the
         * objects are equal.
         */
        
        QName qn1, qn2;
        qn1 = new QName("myNamespace", "myLocalPart", "myPrefix");
        qn2 = QName.valueOf("{myNamespace}myLocalPart");   //prefix defaults to ""
        
        assertTrue("Expected two QNames objects with the same values to be equal.",
                   qn1.equals(qn2));
    }

    public void testEqualsGoodDifferentNamespaceAndLocalPart()
    {
        QName qn1, qn2;
        qn1 = new QName("myNamespace", "myLocalPart", "");
        qn2 = QName.valueOf("{yourNamespace}yourLocalPart"); //prefix defaults to ""
        
        assertFalse("Expected two QNames objects with different values to be not equal.",
                   qn1.equals(qn2));
    }

    public void testEqualsGoodSameObjectRef()
    {
        QName qn1, qn2;
        qn1 = new QName("myNamespace", "myLocalPart", "");
        qn2 = qn1;
        
        //qn1 and qn2 now refer to the same object.
        
        assertTrue("Expected two QNames with the same object reference to be equal.",
                    qn1.equals(qn2));
    }

    public void testEqualsGoodNotAQName()
    {
        QName qn1;
        qn1 = new QName("myNamespace", "myLocalPart", "");
        Object o = new Object();
        
        assertFalse("Expected a QName and a different type of object to be not equal.",
                   qn1.equals(o));
    }


    
}

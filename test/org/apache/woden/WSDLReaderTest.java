/**
 * Copyright 2005, 2006 Apache Software Foundation 
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
package org.apache.woden;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.woden.tests.TestErrorHandler;
import org.apache.woden.wsdl20.xml.DescriptionElement;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WSDLReaderTest extends TestCase 
{
  private WSDLFactory factory = null;
  private WSDLReader reader = null;
  private ErrorHandler handler = null;
  
  public static Test suite()
  {
	return new TestSuite(WSDLReaderTest.class);
  }

  protected void setUp() throws Exception 
  {
	handler = new TestErrorHandler();
	try
	{
      factory = WSDLFactory.newInstance();
      reader = factory.newWSDLReader();  
    } 
	catch (Exception e) 
	{
    }
  }

  protected void tearDown() throws Exception 
  {
	factory = null;
	reader = null;
	handler = null;
  }
  
  public void testReadValidWSDL20()
  {
	  DescriptionElement desc = null;
	  try
	  {
        URL wsdlURL = getClass().getClassLoader().getResource("org/apache/woden/primer-hotelReservationService.wsdl");
	    desc = reader.readWSDL(wsdlURL.toString(), handler);
	  }
	  catch(WSDLException e)
	  {
          fail("Unexpected exception: " + e.getMessage());
	  }
      assertNotNull("The description returned is null.", desc);
  }
  
  public void testReadInvalidWSDL20()
  {
	  try
	  {
		URL wsdlURL = getClass().getClassLoader().getResource("./org/apache/woden/badDescriptionTags.wsdl");
		reader.readWSDL(wsdlURL.toString(), handler);
        fail("Expected a WSDLException because the \"description\" tag was deliberately misspelt.");
	  }
	  catch(WSDLException e)
	  {
          assertTrue("Expected a WSDLException with message containing \"WSDL501\", but got: " + e.getMessage() ,
             e.getMessage().indexOf("WSDL501") > -1);
	  }
  }
  
  public void testReadWSDLSourceDoc()
  {
      DescriptionElement desc = null;
      try
      {
        URL wsdlURL = getClass().getClassLoader().getResource("org/apache/woden/primer-hotelReservationService.wsdl");
        String wsdlURLStr = wsdlURL.toString();
        URI wsdlURI = URI.create(wsdlURLStr);
        
        Document doc = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            doc = builder.parse(new InputSource(wsdlURLStr));
        } catch (FactoryConfigurationError e1) {
            fail("Unexpected exception: " + e1.getMessage());
        } catch (ParserConfigurationException e1) {
            fail("Unexpected exception: " + e1.getMessage());
        } catch (SAXException e1) {
           fail("Unexpected exception: " + e1.getMessage());
        } catch (IOException e1) {
            fail("Unexpected exception: " + e1.getMessage());
        }
        
        WSDLSource wsdlSource = reader.createWSDLSource();
        wsdlSource.setBaseURI(wsdlURI);
        wsdlSource.setSource(doc);
        desc = reader.readWSDL(wsdlSource, handler);
      }
      catch(WSDLException e)
      {
          fail("Unexpected exception: " + e.getMessage());
      }
      assertNotNull("The description returned is null.", desc);
  }

  public void testReadWSDLSourceIS()
  {
      DescriptionElement desc = null;
      try
      {
        URL wsdlURL = getClass().getClassLoader().getResource("org/apache/woden/primer-hotelReservationService.wsdl");
        String wsdlURLStr = wsdlURL.toString();
        
        InputSource is = new InputSource(wsdlURLStr);
        URI wsdlURI = URI.create(wsdlURLStr);
        
        WSDLSource wsdlSource = reader.createWSDLSource();
        wsdlSource.setBaseURI(wsdlURI);
        wsdlSource.setSource(is);
        desc = reader.readWSDL(wsdlSource, handler);
      }
      catch(WSDLException e)
      {
          fail("Unexpected exception: " + e.getMessage());
      }
      assertNotNull("The description returned is null.", desc);
  }
}

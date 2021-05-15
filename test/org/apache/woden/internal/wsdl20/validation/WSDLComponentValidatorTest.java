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
package org.apache.woden.internal.wsdl20.validation;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.woden.ErrorReporter;
import org.apache.woden.WSDLException;
import org.apache.woden.internal.ErrorReporterImpl;
import org.apache.woden.internal.wsdl20.BindingFaultImpl;
import org.apache.woden.internal.wsdl20.BindingFaultReferenceImpl;
import org.apache.woden.internal.wsdl20.Constants;
import org.apache.woden.internal.wsdl20.DescriptionImpl;
import org.apache.woden.internal.wsdl20.ElementDeclarationImpl;
import org.apache.woden.internal.wsdl20.EndpointImpl;
import org.apache.woden.internal.wsdl20.FeatureImpl;
import org.apache.woden.internal.wsdl20.InterfaceFaultImpl;
import org.apache.woden.internal.wsdl20.InterfaceFaultReferenceImpl;
import org.apache.woden.internal.wsdl20.InterfaceImpl;
import org.apache.woden.internal.wsdl20.InterfaceMessageReferenceImpl;
import org.apache.woden.internal.wsdl20.InterfaceOperationImpl;
import org.apache.woden.internal.wsdl20.PropertyImpl;
import org.apache.woden.internal.wsdl20.ServiceImpl;
import org.apache.woden.internal.wsdl20.TypeDefinitionImpl;
import org.apache.woden.internal.wsdl20.extensions.PopulatedExtensionRegistry;
import org.apache.woden.tests.TestErrorHandler;
import org.apache.woden.types.NCName;
import org.apache.woden.wsdl20.Binding;
import org.apache.woden.wsdl20.BindingFault;
import org.apache.woden.wsdl20.BindingFaultReference;
import org.apache.woden.wsdl20.BindingMessageReference;
import org.apache.woden.wsdl20.BindingOperation;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.Endpoint;
import org.apache.woden.wsdl20.Feature;
import org.apache.woden.wsdl20.Interface;
import org.apache.woden.wsdl20.InterfaceFault;
import org.apache.woden.wsdl20.InterfaceFaultReference;
import org.apache.woden.wsdl20.InterfaceMessageReference;
import org.apache.woden.wsdl20.InterfaceOperation;
import org.apache.woden.wsdl20.Property;
import org.apache.woden.wsdl20.Service;
import org.apache.woden.wsdl20.enumeration.MessageLabel;
import org.apache.woden.wsdl20.xml.BindingElement;
import org.apache.woden.wsdl20.xml.BindingFaultElement;
import org.apache.woden.wsdl20.xml.BindingFaultReferenceElement;
import org.apache.woden.wsdl20.xml.BindingMessageReferenceElement;
import org.apache.woden.wsdl20.xml.BindingOperationElement;
import org.apache.woden.wsdl20.xml.DescriptionElement;
import org.apache.woden.wsdl20.xml.EndpointElement;
import org.apache.woden.wsdl20.xml.InterfaceElement;
import org.apache.woden.wsdl20.xml.InterfaceFaultElement;
import org.apache.woden.wsdl20.xml.InterfaceFaultReferenceElement;
import org.apache.woden.wsdl20.xml.InterfaceMessageReferenceElement;
import org.apache.woden.wsdl20.xml.InterfaceOperationElement;
import org.apache.woden.wsdl20.xml.ServiceElement;

/**
 * A test class to test the assertion tests in the WSDLComponentValidator.
 */
public class WSDLComponentValidatorTest extends TestCase 
{
  private WSDLComponentValidator val;
  private ErrorReporter reporter;
  private TestErrorHandler handler;
  // Helper test values
  private final static URI namespace1 = URI.create("http://www.sample.org");
  private final static NCName name1 = new NCName("name1");
  private final static NCName name2 = new NCName("name2");
  private final static NCName name3 = new NCName("name3");
  private final static NCName name4 = new NCName("name4");
  private final static QName name1QN = new QName(namespace1.toString(), name1.toString());
  private final static QName name2QN = new QName(namespace1.toString(), name2.toString());
  private final static QName name3QN = new QName(namespace1.toString(), name3.toString());
  private final static QName name4QN = new QName(namespace1.toString(), name4.toString());
  
  /**
   * Create a test suite from this test class.
   * 
   * @return A test suite from this test class.
   */
  public static Test suite()
  {
    return new TestSuite(WSDLComponentValidatorTest.class);
  }
	  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception 
  {
    val = new WSDLComponentValidator();
	handler = new TestErrorHandler();
	reporter = new ErrorReporterImpl();
	reporter.setErrorHandler(handler);
  }

  /* (non-Javadoc)
   * @see junit.framework.TestCase#tearDown()
   */
  protected void tearDown() throws Exception 
  {
	val = null;
	reporter = null;
	handler = null;
  }
  
  /**
   * Test that the testAssertionInterface0027 method returns
   * true if the interface does not appear in the list of its
   * extended interfaces, false otherwise.
   */
  public void testTestAssertionInterface0027()
  {
    // Test that the assertion returns true for an interace that extends no other interfaces.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceImpl interfac = (InterfaceImpl)desc.addInterfaceElement();
	  if(!val.testAssertionInterface0027(interfac, reporter))
	  {
	    fail("The testAssertionInterface0027 method returned false for an interface that extends no other interfaces.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true if the interface is not in the direct or indirect list.
	try
	{
      DescriptionElement desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      
      // Create an interface element, name it and add to the description element
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      
      // Create another interface element, name it and add to the description element
      InterfaceElement interfac2 = desc.addInterfaceElement();
      interfac2.setName(name2);

      interfac.addExtendedInterfaceName(interfac2.getName());
      
      InterfaceElement interfac3 = desc.addInterfaceElement();
      interfac3.setName(name3);
      
      interfac.addExtendedInterfaceName(interfac3.getName());
      
      InterfaceElement interfac4 = desc.addInterfaceElement();
      interfac4.setName(name4);
      
      interfac2.addExtendedInterfaceName(interfac4.getName());
        
      desc.toComponent().getInterfaces(); //init Interface's ref to its Description, needed for interface extension
	  
	  if(!val.testAssertionInterface0027((Interface)interfac, reporter))
	  {
	    fail("The testAssertionInterface0027 method returned false for an interface that is not in the list of exteneded interfaces.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false if the interface is in the direct list.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
	  InterfaceImpl interfac = (InterfaceImpl)desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceImpl interfac2 = (InterfaceImpl)desc.addInterfaceElement();
      interfac2.setName(name2);
      InterfaceImpl interfac3 = (InterfaceImpl)desc.addInterfaceElement();
      interfac3.setName(name3);
      interfac.addExtendedInterfaceName(interfac.getName());
      interfac.addExtendedInterfaceName(interfac2.getName());
      interfac.addExtendedInterfaceName(interfac3.getName());
      
      desc.getInterfaces(); //init Interface's ref to its Description, needed for interface extension
	  
	  if(val.testAssertionInterface0027(interfac, reporter))
	  {
	    fail("The testAssertionInterface0027 method returned true for an interface that is in the direct extended interface list.");
	  }
	}
	catch(Exception e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false if the interface is in the indirect list.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceImpl interfac = (InterfaceImpl)desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceImpl interfac2 = (InterfaceImpl)desc.addInterfaceElement();
      interfac2.setName(name2);
      InterfaceImpl interfac3 = (InterfaceImpl)desc.addInterfaceElement();
      interfac3.setName(name3);
      interfac.addExtendedInterfaceName(interfac2.getName());
      interfac.addExtendedInterfaceName(interfac3.getName());
      interfac2.addExtendedInterfaceName(interfac.getName());
      
      desc.getInterface(interfac.getName()); //to ensure the Interface can reference its containing Description
	  
	  if(val.testAssertionInterface0027(interfac, reporter))
	  {
	    fail("The testAssertionInterface0027 method returned true for an interface that is in the indirect extended interface list.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionInterface0030 method returns
   * true if the interface name is unique in the description,
   * false otherwise.
   */
  public void testTestAssertionInterface0030()
  {
    // Test that the assertion returns true for an empty list of interfaces.
	try
	{
	  if(!val.testAssertionInterface0030(new Interface[]{}, reporter))
	  {
	    fail("The testAssertionInterface0030 method returned false for an empty list of interfaces.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for an interface that is the only interface defined.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceImpl interfac = (InterfaceImpl)desc.addInterfaceElement();
	  interfac.setName(name1);
	  if(!val.testAssertionInterface0030(new Interface[]{interfac}, reporter))
	  {
	    fail("The testAssertionInterface0030 method returned false for an list of interfaces that contains only one interface.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true for a list of interfaces that contains no duplicate names.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceImpl interfac = (InterfaceImpl)desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceImpl interfac2 = (InterfaceImpl)desc.addInterfaceElement();
	  interfac2.setName(name2);
      InterfaceImpl interfac3 = (InterfaceImpl)desc.addInterfaceElement();
	  interfac3.setName(name3);
	  
	  Interface[] interfaces = new Interface[]{interfac, interfac2, interfac3};
	  
	  if(!val.testAssertionInterface0030(interfaces, reporter))
	  {
	    fail("The testAssertionInterface0030 method returned false for a list of interfaces that contains no duplicates.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for two interfaces that are defined with the same QName object.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceImpl interfac = (InterfaceImpl)desc.addInterfaceElement();
	  interfac.setName(name1);
      InterfaceImpl interfac2 = (InterfaceImpl)desc.addInterfaceElement();
	  interfac2.setName(name2);
      InterfaceImpl interfac3 = (InterfaceImpl)desc.addInterfaceElement();
	  interfac3.setName(name1);
	  
	  Interface[] interfaces = new Interface[]{interfac, interfac2, interfac3};
	  
	  if(val.testAssertionInterface0030(interfaces, reporter))
	  {
	    fail("The testAssertionInterface0030 method returned true for a list of interfaces that contains two interfaces defined with the same QName object.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for two interfaces that are defined with the same name and
	// different QName objects.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceImpl interfac = (InterfaceImpl)desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceImpl interfac2 = (InterfaceImpl)desc.addInterfaceElement();
      interfac2.setName(name2);
      InterfaceImpl interfac3 = (InterfaceImpl)desc.addInterfaceElement();
      interfac3.setName(new NCName("name1"));
          
      Interface[] interfaces = new Interface[]{interfac, interfac2, interfac3};
	  
	  if(val.testAssertionInterface0030(interfaces, reporter))
	  {
	    fail("The testAssertionInterface0030 method returned true for a list of interfaces that contains two interfaces with the same name defined with different QName objects.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionMEP0037 method returns
   * true if the pattern is an absolute IRI, false otherwise.
   */
  public void testTestAssertionMEP0037()
  {
	// Test that an absolute IRI is valid.
	try
	{
	  if(!val.testAssertionMEP0037(new URI("http://www.sample.org"), reporter))
	  {
		  fail("The testAssertionMEP0037 method returned false for an absolute pattern.");
	  }
	}
	catch(URISyntaxException e)
	{
	  fail("There was a problem creating the test IRI: " + e);
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that a relative IRI is not valid.
	try
	{
	  if(val.testAssertionMEP0037(new URI("sample.org"), reporter))
	  {
		  fail("The testAssertionMEP0037 method returned true for a relative pattern.");
	  }
	}
	catch(URISyntaxException e)
	{
	  fail("There was a problem creating the test IRI: " + e);
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionInterfaceOperation0029 method returns
   * true if the namespace of the interface operation name is the same 
   * as the namespace of the interface name, false otherwise.
   */
  public void testTestAssertionInterfaceOperation0029()
  {
    // Test that the method returns true if the namespaces are the same.
	try
	{
      // create an element model by setting interface and interfaceoperation elements
      // on the DescriptionElement. This is synonymous to parsing a WSDL file 
      QName interfaceName = new QName("http://www.sample.org", "interfacename");
      DescriptionElement descElem = new DescriptionImpl();
      descElem.setTargetNamespace(new URI(interfaceName.getNamespaceURI()));
      
      // Create and add an interface element to the description element
      InterfaceElement interfaceElem = descElem.addInterfaceElement();
      
	  interfaceElem.setName(new NCName(interfaceName.getLocalPart()));
      
      // Create and add an interface operation element to the interface element
      InterfaceOperationElement interfaceOperationElem = interfaceElem.addInterfaceOperationElement();
      
	  interfaceOperationElem.setName(new NCName("interfaceoperation"));
      
      // turn the DescriptionElement to a Description component - the only way to do
      // this is to cast it. Then run the validator over the Description
      QName testIfName = new QName("http://www.sample.org", "interfacename");
      QName testIfOpName = new QName("http://www.sample.org", "interfaceoperation");
      Interface testIf = ((Description)descElem).getInterface(testIfName);
      InterfaceOperation testIfOp = testIf.getInterfaceOperation(testIfOpName);
      
      if(!val.testAssertionInterfaceOperation0029(testIfOp, testIf, reporter))
	  {
	    fail("The testAssertionInterfaceOperation0029 method returned false for an interface operation with the same namespace as the containing interface.");
	  }
	}
	catch(Exception e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
		
  /**
   * Test that the testAssertionInterfaceOperation0035 method returns
   * true if the list of interface operations contains no interface
   * operations with duplicate names, false otherwise.
   */
  public void testTestAssertionInterfaceOperation0035()
  {
    // Test that the assertion returns true for an empty list of interface operations.
	try
	{
	  if(!val.testAssertionInterfaceOperation0035(new InterfaceOperation[]{}, reporter))
	  {
	    fail("The testAssertionInterfaceOperation0035 method returned false for an empty list of interface operations.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for an interface operation with no name.
	try
	{
      InterfaceOperationImpl interfaceOperation = new InterfaceOperationImpl();
	  if(!val.testAssertionInterfaceOperation0035(new InterfaceOperation[]{interfaceOperation}, reporter))
	  {
	    fail("The testAssertionInterfaceOperation0035 method returned false for an interface operation with no name.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for an interface operation that is the only interface operation defined.
	try
	{
      // Use this DescriptionElement as the container for an InterfaceElement which is a
      // container for InterfaceOperationElements we're going to test. The
      // InterfaceOperationElement.getName() method fetches the targetNamespace from the
      // enclosing DescriptionElement.
      DescriptionElement descElem = new DescriptionImpl();
      InterfaceElement ie = descElem.addInterfaceElement();
      InterfaceOperationElement ioe = ie.addInterfaceOperationElement();
      InterfaceOperation interfaceOperation = (InterfaceOperation)ioe;
      
      ioe.setName(name1);
	  if(!val.testAssertionInterfaceOperation0035(new InterfaceOperation[]{interfaceOperation}, reporter))
	  {
	    fail("The testAssertionInterfaceOperation0035 method returned false for an interface operation that is the only interface operation defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true for a list of interface operations that contains no duplicate names.
	try
	{
      DescriptionElement descElem = new DescriptionImpl();
      InterfaceElement ie = descElem.addInterfaceElement();
          
	  InterfaceOperationElement interfaceOperation = ie.addInterfaceOperationElement();
	  interfaceOperation.setName(name1);
	  InterfaceOperationElement interfaceOperation2 = ie.addInterfaceOperationElement();
	  interfaceOperation2.setName(name2);
	  InterfaceOperationElement interfaceOperation3 = ie.addInterfaceOperationElement();
	  interfaceOperation3.setName(name3);
	  
	  InterfaceOperation[] interfaceOperations = new InterfaceOperation[]{(InterfaceOperationImpl)interfaceOperation, (InterfaceOperationImpl)interfaceOperation2, (InterfaceOperationImpl)interfaceOperation3};
	  
	  if(!val.testAssertionInterfaceOperation0035(interfaceOperations, reporter))
	  {
	    fail("The testAssertionInterfaceOperation0035 method returned false for a list of interface operations that contains no duplicate names.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for two interface operations that are defined with the same QName object.
	try
	{
      DescriptionElement descElem = new DescriptionImpl();
      InterfaceElement ie = descElem.addInterfaceElement();
      
      InterfaceOperationElement interfaceOperation = ie.addInterfaceOperationElement();
      interfaceOperation.setName(name1);
      InterfaceOperationElement interfaceOperation2 = ie.addInterfaceOperationElement();
      interfaceOperation2.setName(name2);
      InterfaceOperationElement interfaceOperation3 = ie.addInterfaceOperationElement();
      interfaceOperation3.setName(name1);
	  
	  InterfaceOperation[] interfaceOperations = new InterfaceOperation[]{(InterfaceOperationImpl)interfaceOperation, (InterfaceOperationImpl)interfaceOperation2, (InterfaceOperationImpl)interfaceOperation3};
	  
	  if(val.testAssertionInterfaceOperation0035(interfaceOperations, reporter))
	  {
	    fail("The testAssertionInterfaceOperation0035 method returned true for a list of interface operations that contains two interface operations defined with the same QName object.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for two interface operations that are defined with the same name and
	// different NCName objects.
	try
	{
      DescriptionElement descElem = new DescriptionImpl();
      InterfaceElement ie = descElem.addInterfaceElement();
      
      InterfaceOperationElement interfaceOperation = ie.addInterfaceOperationElement();
      interfaceOperation.setName(name1);
      InterfaceOperationElement interfaceOperation2 = ie.addInterfaceOperationElement();
      interfaceOperation2.setName(name2);
      InterfaceOperationElement interfaceOperation3 = ie.addInterfaceOperationElement();
	  interfaceOperation3.setName(new NCName("name1"));
	  
	  InterfaceOperation[] interfaceOperations = new InterfaceOperation[]{(InterfaceOperationImpl)interfaceOperation, (InterfaceOperationImpl)interfaceOperation2, (InterfaceOperationImpl)interfaceOperation3};
	  
	  if(val.testAssertionInterfaceOperation0035(interfaceOperations, reporter))
	  {
	    fail("The testAssertionInterfaceOperation0035 method returned true for a list of interface operations that contains two interface operations with the same name defined with different QName objects.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionInterfaceFault0032 method returns
   * true if the list of interface faults contains no interface
   * faults with duplicate names, false otherwise.
   */
  public void testTestAssertionInterfaceFault0032()
  {
    // Test that the assertion returns true for an empty list of interface faults.
	try
	{
	  if(!val.testAssertionInterfaceFault0032(new InterfaceFault[]{}, reporter))
	  {
	    fail("The testAssertionInterfaceFault0032 method returned false for an empty list of interface faults.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for an interface fault that is the only interface fault defined.
	try
	{
      DescriptionElement descElem = new DescriptionImpl();
      InterfaceElement ie = descElem.addInterfaceElement();
      
	  InterfaceFaultElement interfaceFault = ie.addInterfaceFaultElement();
	  interfaceFault.setName(name1);
	  if(!val.testAssertionInterfaceFault0032(new InterfaceFault[]{(InterfaceFaultImpl)interfaceFault}, reporter))
	  {
	    fail("The testAssertionInterfaceFault0032 method returned false for an interface fault that is the only interface fault defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true for a list of interface faults that contains no duplicate names.
	try
	{
      DescriptionElement descElem = new DescriptionImpl();
      InterfaceElement ie = descElem.addInterfaceElement();
      
      InterfaceFaultElement interfaceFault = ie.addInterfaceFaultElement();
	  interfaceFault.setName(name1);
      InterfaceFaultElement interfaceFault2 = ie.addInterfaceFaultElement();
	  interfaceFault2.setName(name2);
      InterfaceFaultElement interfaceFault3 = ie.addInterfaceFaultElement();
	  interfaceFault3.setName(name3);
	  
	  InterfaceFault[] interfaceFaults = new InterfaceFault[]{(InterfaceFaultImpl)interfaceFault, (InterfaceFaultImpl)interfaceFault2, (InterfaceFaultImpl)interfaceFault3};
	  
	  if(!val.testAssertionInterfaceFault0032(interfaceFaults, reporter))
	  {
	    fail("The testAssertionInterfaceFault0032 method returned false for a list of interface faults that contains no duplicate names.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for two interface faults that are defined with the same QName object.
	try
	{
      DescriptionElement descElem = new DescriptionImpl();
      InterfaceElement ie = descElem.addInterfaceElement();
      
      InterfaceFaultElement interfaceFault = ie.addInterfaceFaultElement();
      interfaceFault.setName(name1);
      InterfaceFaultElement interfaceFault2 = ie.addInterfaceFaultElement();
      interfaceFault2.setName(name2);
      InterfaceFaultElement interfaceFault3 = ie.addInterfaceFaultElement();
      interfaceFault3.setName(name1);
      
      InterfaceFault[] interfaceFaults = new InterfaceFault[]{(InterfaceFaultImpl)interfaceFault, (InterfaceFaultImpl)interfaceFault2, (InterfaceFaultImpl)interfaceFault3};
	  
	  if(val.testAssertionInterfaceFault0032(interfaceFaults, reporter))
	  {
	    fail("The testAssertionInterfaceFault0032 method returned true for a list of interface faults that contains two interface faults defined with the same QName object.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for two interface faults that are defined with the same name and
	// different NCName objects.
	try
	{
      DescriptionElement descElem = new DescriptionImpl();
      InterfaceElement ie = descElem.addInterfaceElement();
      
      InterfaceFaultElement interfaceFault = ie.addInterfaceFaultElement();
      interfaceFault.setName(name1);
      InterfaceFaultElement interfaceFault2 = ie.addInterfaceFaultElement();
      interfaceFault2.setName(name2);
      InterfaceFaultElement interfaceFault3 = ie.addInterfaceFaultElement();
	  interfaceFault3.setName(new NCName("name1"));
	  
      InterfaceFault[] interfaceFaults = new InterfaceFault[]{(InterfaceFaultImpl)interfaceFault, (InterfaceFaultImpl)interfaceFault2, (InterfaceFaultImpl)interfaceFault3};
	  
	  if(val.testAssertionInterfaceFault0032(interfaceFaults, reporter))
	  {
	    fail("The testAssertionInterfaceFault0032 method returned true for a list of interface faults that contains two interface faults with the same name defined with different QName objects.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionInterfaceFault0028 method returns
   * true if the namespace of the interface fault name is the same 
   * as the namespace of the interface name, false otherwise.
   */
  public void testTestAssertionInterfaceFault0028()
  {
    // Test that the method returns true if the namespaces are the same.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceImpl interfac = (InterfaceImpl)desc.addInterfaceElement();
	  interfac.setName(new NCName("interfacename"));
      InterfaceFaultElement interfaceFault = interfac.addInterfaceFaultElement();
      interfaceFault.setName(new NCName("interfaceoperation"));
	  if(!val.testAssertionInterfaceFault0028((InterfaceFaultImpl)interfaceFault, interfac, reporter))
	  {
	    fail("The testAssertionInterfaceFault0028 method returned false for an interface fault with the same namespace as the containing interface.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
		
    // This test no longer possible as can't set namespace of name attribute
    // as it's now represented (correctly) an NCName
//    // Test that the method returns false if the namespaces are different.
//	try
//	{
//      DescriptionImpl desc = new DescriptionImpl();
//      InterfaceImpl interfac = (InterfaceImpl)desc.createInterfaceElement();
//	  interfac.setName(new QName("http://www.sample.org", "interfacename"));
//	  InterfaceFaultImpl interfaceFault = new InterfaceFaultImpl();
//	  interfaceFault.setName(new QName("http://www.sample2.org", "interfaceoperation"));
//	  if(val.testAssertionInterfaceFault0028(interfaceFault, interfac, reporter))
//	  {
//	    fail("The testAssertionInterfaceFault0028 method returned true for an interface fault with a different namespace than the containing interface.");
//	  }
//	}
//	catch(WSDLException e)
//	{
//	  fail("There was a problem running the test assertion method " + e);
//	}
  }
  
  /**
   * Test that the testAssertionInterfaceMessageReference0041 method returns
   * true if the message content model is #any or #none and the element
   * declartion is empty, false otherwise.
   */
  public void testTestAssertionInterfaceMessageReference0041()
  {
    QName name1 = new QName("http://www.sample.org", "name1");
    
    // Test that the method returns true if the message content model is #any and the element declaration is empty.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setExtensionRegistry(new PopulatedExtensionRegistry());
      ElementDeclarationImpl ed = new ElementDeclarationImpl();
      ed.setName(name1);
      desc.addElementDeclaration(ed);
      InterfaceElement interfac = desc.addInterfaceElement();
      InterfaceOperationElement oper = interfac.addInterfaceOperationElement();
          
	  InterfaceMessageReferenceElement interfaceMessageReference = oper.addInterfaceMessageReferenceElement();
	  interfaceMessageReference.setMessageContentModel(Constants.NMTOKEN_ANY);

      desc.getInterfaces(); //init Interface's ref to its Description
      
      if(!val.testAssertionInterfaceMessageReference0041((InterfaceMessageReferenceImpl)interfaceMessageReference, reporter))
	  {
	    fail("The testAssertionInterfaceMessageReference0041 method returned false for an interface message reference with the message content model #any and an empty element declaration.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
		
    // Test that the method returns true if the message content model is #none and the element declaration is empty.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setExtensionRegistry(new PopulatedExtensionRegistry());
      ElementDeclarationImpl ed = new ElementDeclarationImpl();
      ed.setName(name1);
      desc.addElementDeclaration(ed);
      InterfaceElement interfac = desc.addInterfaceElement();
      InterfaceOperationElement oper = interfac.addInterfaceOperationElement();
              
      InterfaceMessageReferenceElement interfaceMessageReference = oper.addInterfaceMessageReferenceElement();
	  interfaceMessageReference.setMessageContentModel(Constants.NMTOKEN_NONE);

      desc.getInterfaces(); //init Interface's ref to its Description
      
	  if(!val.testAssertionInterfaceMessageReference0041((InterfaceMessageReferenceImpl)interfaceMessageReference, reporter))
	  {
	    fail("The testAssertionInterfaceMessageReference0041 method returned false for an interface message reference with the message content model #none and an empty element declaration.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}	
	
    // Test that the method returns false if the message content model is #any and the element declaration is not empty.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setExtensionRegistry(new PopulatedExtensionRegistry());
      ElementDeclarationImpl ed = new ElementDeclarationImpl();
      ed.setName(name1);
      desc.addElementDeclaration(ed);
      InterfaceElement interfac = desc.addInterfaceElement();
      InterfaceOperationElement oper = interfac.addInterfaceOperationElement();
      
      InterfaceMessageReferenceElement interfaceMessageReference = oper.addInterfaceMessageReferenceElement();
	  interfaceMessageReference.setMessageContentModel(Constants.NMTOKEN_ANY);
	  interfaceMessageReference.setElementName(name1);

      desc.getInterfaces(); //init Interface's ref to its Description
      
	  if(val.testAssertionInterfaceMessageReference0041((InterfaceMessageReferenceImpl)interfaceMessageReference, reporter))
	  {
	    fail("The testAssertionInterfaceMessageReference0041 method returned true for an interface message reference with the message content model #any and a non-empty element declaration.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the method returns false if the message content model is #none and the element declaration is not empty.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setExtensionRegistry(new PopulatedExtensionRegistry());
      ElementDeclarationImpl ed = new ElementDeclarationImpl();
      ed.setName(name1);
      desc.addElementDeclaration(ed);
      InterfaceElement interfac = desc.addInterfaceElement();
      InterfaceOperationElement oper = interfac.addInterfaceOperationElement();
          
      InterfaceMessageReferenceElement interfaceMessageReference = oper.addInterfaceMessageReferenceElement();
	  interfaceMessageReference.setMessageContentModel(Constants.NMTOKEN_NONE);
	  interfaceMessageReference.setElementName(name1);
      
      desc.getInterfaces(); //init Interface's ref to its Description
      
	  if(val.testAssertionInterfaceMessageReference0041((InterfaceMessageReferenceImpl)interfaceMessageReference, reporter))
	  {
	    fail("The testAssertionInterfaceMessageReference0041 method returned true for an interface message reference with the message content model #none and a non-empty element declaration.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the method returns true if the message content model is #other and the element declaration is not empty.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      ElementDeclarationImpl ed = new ElementDeclarationImpl();
      ed.setName(name1);
      desc.addElementDeclaration(ed);
      InterfaceElement interfac = desc.addInterfaceElement();
      InterfaceOperationElement oper = interfac.addInterfaceOperationElement();
              
	  InterfaceMessageReferenceElement interfaceMessageReference = oper.addInterfaceMessageReferenceElement();
	  interfaceMessageReference.setMessageContentModel(Constants.NMTOKEN_OTHER);
	  interfaceMessageReference.setElementName(name1);
      
      desc.getInterfaces(); //init Interface's ref to its Description
      
	  if(!val.testAssertionInterfaceMessageReference0041((InterfaceMessageReferenceImpl)interfaceMessageReference, reporter))
	  {
	    fail("The testAssertionInterfaceMessageReference0041 method returned false for an interface message reference with the message content model #other and a non-empty element declaration.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the method returns true if the message content model is #element and the element declaration is not empty.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      ElementDeclarationImpl ed = new ElementDeclarationImpl();
      ed.setName(name1);
      desc.addElementDeclaration(ed);
      InterfaceElement interfac = desc.addInterfaceElement();
      InterfaceOperationElement oper = interfac.addInterfaceOperationElement();
          
      InterfaceMessageReferenceElement interfaceMessageReference = oper.addInterfaceMessageReferenceElement();
	  interfaceMessageReference.setMessageContentModel(Constants.NMTOKEN_ELEMENT);
      interfaceMessageReference.setElementName(name1);
      
      desc.getInterfaces(); //init Interface's ref to its Description
      
	  if(!val.testAssertionInterfaceMessageReference0041((InterfaceMessageReferenceImpl)interfaceMessageReference, reporter))
	  {
	    fail("The testAssertionInterfaceMessageReference0041 method returned false for an interface message reference with the message content model #element and a non-empty element declaration.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionInterfaceMessageReferencet0042 method returns
   * true if the list of interface message references contains no duplicate
   * message labels, false otherwise.
   */
  public void testTestAssertionInterfaceMessageReference0042()
  {
    // Test that the assertion returns true for an empty list of message references.
	try
	{
	  if(!val.testAssertionInterfaceMessageReference0042(new InterfaceMessageReference[]{}, reporter))
	  {
	    fail("The testAssertionInterfaceMessageReference0042 method returned false for an empty list of interface message references.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for an interface message reference that is the only interface message reference defined.
	try
	{
	  InterfaceMessageReferenceImpl interfaceMessageReference = new InterfaceMessageReferenceImpl();
	  interfaceMessageReference.setMessageLabel(MessageLabel.IN);
	  if(!val.testAssertionInterfaceMessageReference0042(new InterfaceMessageReference[]{interfaceMessageReference}, reporter))
	  {
	    fail("The testAssertionInterfaceMessageReference0042 method returned false for an interface message reference that is the only interface message reference defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true for a list of interface message references that contains no duplicate message labels.
	try
	{
	  InterfaceMessageReferenceImpl interfaceMessageReference = new InterfaceMessageReferenceImpl();
	  interfaceMessageReference.setMessageLabel(MessageLabel.IN);
	  InterfaceMessageReferenceImpl interfaceMessageReference2 = new InterfaceMessageReferenceImpl();
	  interfaceMessageReference2.setMessageLabel(MessageLabel.OUT);
	  
	  InterfaceMessageReference[] interfaceMessageReferences = new InterfaceMessageReference[]{interfaceMessageReference, interfaceMessageReference2};
	  
	  if(!val.testAssertionInterfaceMessageReference0042(interfaceMessageReferences, reporter))
	  {
	    fail("The testAssertionInterfaceMessageReference0042 method returned false for a list of interface message references that contains no duplicate message labels.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for two interface message references that are defined with the same message label.
	try
	{
	  InterfaceMessageReferenceImpl interfaceMessageReference = new InterfaceMessageReferenceImpl();
	  interfaceMessageReference.setMessageLabel(MessageLabel.IN);
	  InterfaceMessageReferenceImpl interfaceMessageReference2 = new InterfaceMessageReferenceImpl();
	  interfaceMessageReference2.setMessageLabel(MessageLabel.OUT);
	  InterfaceMessageReferenceImpl interfaceMessageReference3 = new InterfaceMessageReferenceImpl();
	  interfaceMessageReference3.setMessageLabel(MessageLabel.IN);
	  
	  InterfaceMessageReference[] interfaceMessageReferences = new InterfaceMessageReference[]{interfaceMessageReference, interfaceMessageReference2, interfaceMessageReference3};
	  
	  if(val.testAssertionInterfaceMessageReference0042(interfaceMessageReferences, reporter))
	  {
	    fail("The testAssertionInterfaceMessageReference0042 method returned true for a list of interface message references that contains two interface message references defined with the same message label.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionInterfaceFaultReference0045 method returns
   * true if the list of interface fault references contains no duplicate
   * fault/message label pairs, false otherwise.
   */
  public void testTestAssertionInterfaceFaultReference0045()
  {
    // Test that the assertion returns true for an interface fault reference list that is empty.
	try
	{
	  if(!val.testAssertionInterfaceFaultReference0045(new InterfaceFaultReference[]{}, reporter))
	  {
	    fail("The testAssertionInterfaceFaultReference0045 method returned false for an interface fault reference list that is empty.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true for an interface fault reference list with one entry.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceElement interfac = desc.addInterfaceElement();
	  InterfaceFaultElement fault = interfac.addInterfaceFaultElement();
      fault.setName(name1);
      InterfaceOperationElement oper = interfac.addInterfaceOperationElement();
      
	  InterfaceFaultReferenceElement faultReference = oper.addInterfaceFaultReferenceElement();
	  faultReference.setRef(name1QN);
	  faultReference.setMessageLabel(MessageLabel.IN);

      
      desc.getInterfaces(); //init Interface's ref to its Description
      
	  if(!val.testAssertionInterfaceFaultReference0045(new InterfaceFaultReference[]{(InterfaceFaultReferenceImpl)faultReference}, reporter))
	  {
	    fail("The testAssertionInterfaceFaultReference0045 method returned false for an interface fault reference that is the only interface fault reference defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true for two interface fault references that have both different
	// faults and different message labels.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceElement interfac = desc.addInterfaceElement();
      InterfaceFaultElement fault = interfac.addInterfaceFaultElement();
      fault.setName(name1);
      InterfaceFaultElement fault2 = interfac.addInterfaceFaultElement();
      fault2.setName(name2);
      InterfaceOperationElement oper = interfac.addInterfaceOperationElement();
          
	  InterfaceFaultReferenceElement faultReference = oper.addInterfaceFaultReferenceElement();
	  faultReference.setRef(name1QN);
	  faultReference.setMessageLabel(MessageLabel.IN);
      InterfaceFaultReferenceElement faultReference2 = oper.addInterfaceFaultReferenceElement();
	  faultReference2.setRef(name2QN);
	  faultReference2.setMessageLabel(MessageLabel.OUT);
      
      desc.getInterfaces(); //init Interface's ref to its Description
      
	  if(!val.testAssertionInterfaceFaultReference0045(new InterfaceFaultReference[]{(InterfaceFaultReferenceImpl)faultReference, (InterfaceFaultReferenceImpl)faultReference2}, reporter))
	  {
	    fail("The testAssertionInterfaceFaultReference0045 method returned false for two interface fault references that have different faults and message labels.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true for two interface fault references that have the same fault
	// but different message labels
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceElement interfac = desc.addInterfaceElement();
      InterfaceFaultElement fault = interfac.addInterfaceFaultElement();
      fault.setName(name1);
      InterfaceOperationElement oper = interfac.addInterfaceOperationElement();
          
      InterfaceFaultReferenceElement faultReference = oper.addInterfaceFaultReferenceElement();
      faultReference.setRef(name1QN);
      faultReference.setMessageLabel(MessageLabel.IN);
      InterfaceFaultReferenceElement faultReference2 = oper.addInterfaceFaultReferenceElement();
      faultReference2.setRef(name1QN);
      faultReference2.setMessageLabel(MessageLabel.OUT);
      
      desc.getInterfaces(); //init Interface's ref to its Description
      
	  if(!val.testAssertionInterfaceFaultReference0045(new InterfaceFaultReference[]{(InterfaceFaultReferenceImpl)faultReference, (InterfaceFaultReferenceImpl)faultReference2}, reporter))
	  {
	    fail("The testAssertionInterfaceFaultReference0045 method returned false for two interface fault references that have the same fault but different message labels.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true for two interface fault references that have the same
	// message label but different faults.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceElement interfac = desc.addInterfaceElement();
      InterfaceFaultElement fault = interfac.addInterfaceFaultElement();
      fault.setName(name1);
      InterfaceFaultElement fault2 = interfac.addInterfaceFaultElement();
      fault2.setName(name2);
      InterfaceOperationElement oper = interfac.addInterfaceOperationElement();
              
      InterfaceFaultReferenceElement faultReference = oper.addInterfaceFaultReferenceElement();
      faultReference.setRef(name1QN);
      faultReference.setMessageLabel(MessageLabel.IN);
      InterfaceFaultReferenceElement faultReference2 = oper.addInterfaceFaultReferenceElement();
      faultReference2.setRef(name2QN);
      faultReference2.setMessageLabel(MessageLabel.IN);
      
      desc.getInterfaces(); //init Interface's ref to its Description
      
	  if(!val.testAssertionInterfaceFaultReference0045(new InterfaceFaultReference[]{(InterfaceFaultReferenceImpl)faultReference, (InterfaceFaultReferenceImpl)faultReference2}, reporter))
	  {
	    fail("The testAssertionInterfaceFaultReference0045 method returned false for two interface fault references that have different faults but the same message labels.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for two interface fault references that have the same
	// fault and message label.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      InterfaceFaultElement fault = interfac.addInterfaceFaultElement();
      fault.setName(name1);
      InterfaceOperationElement oper = interfac.addInterfaceOperationElement();
          
      InterfaceFaultReferenceElement faultReference = oper.addInterfaceFaultReferenceElement();
      faultReference.setRef(name1QN);
      faultReference.setMessageLabel(MessageLabel.IN);
      InterfaceFaultReferenceElement faultReference2 = oper.addInterfaceFaultReferenceElement();
      faultReference2.setRef(name1QN);
      faultReference2.setMessageLabel(MessageLabel.IN);
      
      desc.getInterfaces(); //init Interface's ref to its Description
      
	  if(val.testAssertionInterfaceFaultReference0045(new InterfaceFaultReference[]{(InterfaceFaultReferenceImpl)faultReference, (InterfaceFaultReferenceImpl)faultReference2}, reporter))
	  {
	    fail("The testAssertionInterfaceFaultReference0045 method returned true for two interface fault references that have the same fault and message label.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the method returns true for an interface fault reference with a null message label.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      InterfaceFaultElement fault = interfac.addInterfaceFaultElement();
      fault.setName(name1);
      InterfaceOperationElement oper = interfac.addInterfaceOperationElement();

      InterfaceFaultReferenceElement faultReference = oper.addInterfaceFaultReferenceElement();
      faultReference.setRef(name1QN);
      
      desc.getInterfaces(); //init Interface's ref to its Description
      
	  if(!val.testAssertionInterfaceFaultReference0045(new InterfaceFaultReference[]{(InterfaceFaultReferenceImpl)faultReference}, reporter))
	  {
	    fail("The testAssertionInterfaceFaultReference0045 method returned false for an interface fault references with a null message labels.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the method returns true for an interface fault reference with a null fault.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      InterfaceOperationElement oper = interfac.addInterfaceOperationElement();
      
      InterfaceFaultReferenceElement faultReference = oper.addInterfaceFaultReferenceElement();
      faultReference.setMessageLabel(MessageLabel.IN);
      
      desc.getInterfaces(); //init Interface's ref to its Description
      
	  if(!val.testAssertionInterfaceFaultReference0045(new InterfaceFaultReference[]{(InterfaceFaultReferenceImpl)faultReference}, reporter))
	  {
	    fail("The testAssertionInterfaceFaultReference0045 method returned false for ano interface fault reference that has a null fault.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the method returns false for two interface fault references that have the same
	// fault/message label and a third interface fault reference that has a null fault and is 
	// defined second.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      InterfaceFaultElement fault = interfac.addInterfaceFaultElement();
      fault.setName(name1);
      InterfaceOperationElement oper = interfac.addInterfaceOperationElement();
          
      InterfaceFaultReferenceElement faultReference = oper.addInterfaceFaultReferenceElement();
      faultReference.setRef(name1QN);
      faultReference.setMessageLabel(MessageLabel.IN);
      InterfaceFaultReferenceElement faultReference2 = oper.addInterfaceFaultReferenceElement();
      faultReference2.setRef(name1QN);
      faultReference2.setMessageLabel(MessageLabel.IN);
      InterfaceFaultReferenceElement faultReference3 = oper.addInterfaceFaultReferenceElement();
      faultReference3.setMessageLabel(MessageLabel.OUT);

      desc.getInterfaces(); //init Interface's ref to its Description
      
	  if(val.testAssertionInterfaceFaultReference0045(new InterfaceFaultReference[]{(InterfaceFaultReferenceImpl)faultReference, (InterfaceFaultReferenceImpl)faultReference3, (InterfaceFaultReferenceImpl)faultReference2}, reporter))
	  {
	    fail("The testAssertionInterfaceFaultReference0045 method returned true for two interface fault references that have the same fault and message label and a third interface fault reference that has a null fault and is defined second.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionFeatureRef0046 method returns
   * true if the feature ref is absolute, false otherwise.
   */
  public void testTestAssertionFeatureRef0046()
  {
    // Test that the assertion returns true for a feature with an absolute ref.
	try
	{
	  FeatureImpl feature = new FeatureImpl();
	  feature.setRef(new URI("http://www.sample.org"));
	  if(!val.testAssertionFeatureRef0046(feature, reporter))
	  {
	    fail("The testAssertionFeatureRef0046 method returned false for a feature with an absolute ref.");
	  }
	}
	catch(URISyntaxException e)
	{
	  fail("There was a problem creating the ref URI for the test method " + e);
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns false for a feature with a relative ref.
	try
	{
	  FeatureImpl feature = new FeatureImpl();
	  feature.setRef(new URI("sample.org"));
	  if(val.testAssertionFeatureRef0046(feature, reporter))
	  {
	    fail("The testAssertionFeatureRef0046 method returned true for a feature with a relative ref.");
	  }
	}
	catch(URISyntaxException e)
	{
	  fail("There was a problem creating the ref URI for the test method " + e);
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for a feature with a null ref. This will be
	// caught be schema validation.
	try
	{
	  FeatureImpl feature = new FeatureImpl();
	  if(!val.testAssertionFeatureRef0046(feature, reporter))
	  {
	    fail("The testAssertionFeatureRef0046 method returned false for a feature with a null ref.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionFeatureRef0047 method returns
   * true if all the features in the list have unique refs, false otherwise.
   */
  public void testTestAssertionFeatureRef0047()
  {
	// Test that the assertion returns true for an empty list. 
	try
	{
	  if(!val.testAssertionFeatureRef0047(new Feature[]{}, reporter))
	  {
	    fail("The testAssertionFeatureRef0047 method returned false for an empty feature list.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for only one feature.
	try
	{
	  FeatureImpl feature = new FeatureImpl();
	  feature.setRef(new URI("http://www.sample.org"));
	  if(!val.testAssertionFeatureRef0047(new Feature[]{feature}, reporter))
	  {
	    fail("The testAssertionFeatureRef0047 method returned false for a feature list with only one feature.");
	  }
	}
	catch(URISyntaxException e)
	{
	  fail("There was a problem creating the ref URI for the test method " + e);
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true for multiple features with different refs.
	try
	{
	  FeatureImpl feature = new FeatureImpl();
	  feature.setRef(new URI("http://www.sample.org"));
	  FeatureImpl feature2 = new FeatureImpl();
	  feature2.setRef(new URI("http://www.sample2.org"));
	  FeatureImpl feature3 = new FeatureImpl();
	  feature3.setRef(new URI("http://www.sample3.org"));
	  if(!val.testAssertionFeatureRef0047(new Feature[]{feature, feature2, feature3}, reporter))
	  {
	    fail("The testAssertionFeatureRef0047 method returned false for a feature list with three features with unique refs.");
	  }
	}
	catch(URISyntaxException e)
	{
	  fail("There was a problem creating the ref URI for the test method " + e);
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for two features with the same ref.
	try
	{
	  FeatureImpl feature = new FeatureImpl();
	  feature.setRef(new URI("http://www.sample.org"));
	  FeatureImpl feature2 = new FeatureImpl();
	  feature2.setRef(new URI("http://www.sample.org"));
	  if(val.testAssertionFeatureRef0047(new Feature[]{feature, feature2}, reporter))
	  {
	    fail("The testAssertionFeatureRef0047 method returned true for a feature list with two features with the same ref.");
	  }
	}
	catch(URISyntaxException e)
	{
	  fail("There was a problem creating the ref URI for the test method " + e);
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionPropertyRef0048 method returns
   * true if the property ref is absolute, false otherwise.
   */
  public void testTestAssertionPropertyRef0048()
  {
    // Test that the assertion returns true for a property with an absolute ref.
	try
	{
	  PropertyImpl property = new PropertyImpl();
	  property.setRef(new URI("http://www.sample.org"));
	  if(!val.testAssertionPropertyRef0048(property, reporter))
	  {
	    fail("The testAssertionPropertyRef0048 method returned false for a property with an absolute ref.");
	  }
	}
	catch(URISyntaxException e)
	{
	  fail("There was a problem creating the ref URI for the test method " + e);
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns false for a feature with a relative ref.
	try
	{
	  PropertyImpl property = new PropertyImpl();
	  property.setRef(new URI("sample.org"));
	  if(val.testAssertionPropertyRef0048(property, reporter))
	  {
	    fail("The testAssertionPropertyRef0048 method returned true for a property with a relative ref.");
	  }
	}
	catch(URISyntaxException e)
	{
	  fail("There was a problem creating the ref URI for the test method " + e);
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for a feature with a null ref. This will be
	// caught be schema validation.
	try
	{
	  PropertyImpl property = new PropertyImpl();
	  if(!val.testAssertionPropertyRef0048(property, reporter))
	  {
	    fail("The testAssertionPropertyRef0048 method returned false for a property with a null ref.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionProperty0050 method returns
   * true if all the properties in the list have unique refs, false otherwise.
   */
  public void testTestAssertionProperty0050()
  {
	// Test that the assertion returns true for an empty list. 
	try
	{
	  if(!val.testAssertionProperty0050(new Property[]{}, reporter))
	  {
	    fail("The testAssertionProperty0050 method returned false for an empty property list.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for only one property.
	try
	{
	  PropertyImpl property = new PropertyImpl();
	  property.setRef(new URI("http://www.sample.org"));
	  if(!val.testAssertionProperty0050(new Property[]{property}, reporter))
	  {
	    fail("The testAssertionProperty0050 method returned false for a property list with only one property.");
	  }
	}
	catch(URISyntaxException e)
	{
	  fail("There was a problem creating the ref URI for the test method " + e);
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true for multiple property with different refs.
	try
	{
	  PropertyImpl property = new PropertyImpl();
	  property.setRef(new URI("http://www.sample.org"));
	  PropertyImpl property2 = new PropertyImpl();
	  property2.setRef(new URI("http://www.sample2.org"));
	  PropertyImpl property3 = new PropertyImpl();
	  property3.setRef(new URI("http://www.sample3.org"));
	  if(!val.testAssertionProperty0050(new Property[]{property, property2, property3}, reporter))
	  {
	    fail("The testAssertionProperty0050 method returned false for a property list with three property with unique refs.");
	  }
	}
	catch(URISyntaxException e)
	{
	  fail("There was a problem creating the ref URI for the test method " + e);
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for two property with the same ref.
	try
	{
	  PropertyImpl property = new PropertyImpl();
	  property.setRef(new URI("http://www.sample.org"));
	  PropertyImpl property2 = new PropertyImpl();
	  property2.setRef(new URI("http://www.sample.org"));
	  if(val.testAssertionProperty0050(new Property[]{property, property2}, reporter))
	  {
	    fail("The testAssertionProperty0050 method returned true for a property list with two property with the same ref.");
	  }
	}
	catch(URISyntaxException e)
	{
	  fail("There was a problem creating the ref URI for the test method " + e);
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionProperty0049 method returns
   * true if the type reference is valid, false otherwise.
   */
  public void testTestAssertionProperty0049()
  {
	// Test that the assertion returns true for a valid type.
	try
	{
	  DescriptionImpl desc = new DescriptionImpl();
	  TypeDefinitionImpl type = new TypeDefinitionImpl();
	  type.setName(new QName("http://www.sample.org", "myType"));
	  desc.addTypeDefinition(type);
	  PropertyImpl property = new PropertyImpl();
	  property.setHasValueToken(false);
	  property.setConstraintName(new QName("http://www.sample.org", "myType"));
	  if(!val.testAssertionProperty0049(property, desc, reporter))
	  {
		fail("The testAssertionProperty0049 method returned false for a property with a constraint with a valid type.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for a type that does not exist.
	try
	{
	  DescriptionImpl desc = new DescriptionImpl();
	  TypeDefinitionImpl type = new TypeDefinitionImpl();
	  type.setName(new QName("http://www.sample.org", "myType"));
	  desc.addTypeDefinition(type);
	  PropertyImpl property = new PropertyImpl();
	  property.setHasValueToken(false);
	  property.setConstraintName(new QName("http://www.sample.org", "myType2"));
	  if(val.testAssertionProperty0049(property, desc, reporter))
	  {
		fail("The testAssertionProperty0049 method returned true for a property with a constraint with an invalid type.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true for a null type.
	try
	{
	  DescriptionImpl desc = new DescriptionImpl();
	  TypeDefinitionImpl type = new TypeDefinitionImpl();
	  type.setName(new QName("http://www.sample.org", "myType"));
	  desc.addTypeDefinition(type);
	  PropertyImpl property = new PropertyImpl();
	  property.setHasValueToken(false);
	  property.setConstraintName(null);
	  if(!val.testAssertionProperty0049(property, desc, reporter))
	  {
		fail("The testAssertionProperty0049 method returned false for a property with a constraint with null type.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionProperty0049b method returns
   * true if constraint is #value if value is set, false otherwise.
   */
  public void testTestAssertionProperty0049b()
  {
    QName name1 = new QName("http://www.sample.org", "name1");
    
    // Test that the assertion returns true for a constraint that is not #value when value is
	// not set.
	try
	{
	  PropertyImpl property = new PropertyImpl();
	  property.setHasValueToken(false);
	  property.setConstraintName(name1);
	  if(!val.testAssertionProperty0049b(property, reporter))
	  {
	    fail("The testAssertionProperty0049b method returned false for a property with a constraint and no value.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for a constraint that is #value when value is
	// set.
	try
	{
	  PropertyImpl property = new PropertyImpl();
	  property.setHasValueToken(true);
	  property.setValue(new Object());
	  if(!val.testAssertionProperty0049b(property, reporter))
	  {
	    fail("The testAssertionProperty0049b method returned false for a property with a value and constraint set to #value.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns false for a constraint that is not #value when value is
	// set.
	try
	{
	  PropertyImpl property = new PropertyImpl();
	  property.setHasValueToken(false);
	  property.setConstraintName(name1);
	  property.setValue(new Object());
	  if(val.testAssertionProperty0049b(property, reporter))
	  {
	    fail("The testAssertionProperty0049b method returned true for a property with a value and constraint not set to #value.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionBinding0054 method returns
   * true if interface is specified when a binding operation 
   * or fault is specified, false otherwise.
   */
  public void testTestAssertionBinding0054()
  {
    NCName name1 = new NCName("name1");
    NCName name2 = new NCName("name2");
    
    // Test that the assertion returns true when no operation or fault is specified
	// and an interface is not specified.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      BindingElement bindingEl = desc.addBindingElement();
      bindingEl.setName(name1);
	  if(!val.testAssertionBinding0054(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0054 method returned false for a binding with no interface, operation, or faults specified.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true when no operation or fault is specified
	// and an interface is specified.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
	  BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
	  if(!val.testAssertionBinding0054(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0054 method returned false for a binding with an interface and no operation or faults specified.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true when an operation is specified
	// and an interface is specified.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
	  if(!val.testAssertionBinding0054(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0054 method returned false for a binding with an interface and an operation specified.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true when a fault is specified
	// and an interface is specified.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      BindingFaultElement bindingFault = binding.addBindingFaultElement();
	  if(!val.testAssertionBinding0054(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0054 method returned false for a binding with an interface and a fault specified.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true when an operation and a fault are specified
	// and an interface is specified.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      BindingFaultElement bindingFault = binding.addBindingFaultElement();
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
	  if(!val.testAssertionBinding0054(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0054 method returned false for a binding with an interface and an operation and a fault specified.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false when an operation is specified
	// and no interface is specified.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name1);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
	  if(val.testAssertionBinding0054(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0054 method returned true for a binding with an operation and no interface.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false when a fault is specified
	// and no interface is specified.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name1);
      BindingFaultElement bindingFault = binding.addBindingFaultElement();
	  if(val.testAssertionBinding0054(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0054 method returned true for a binding with a fault and no interface.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false when an operation and a fault are specified
	// and no interface is specified.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      BindingElement binding = desc.addBindingElement();
      binding.setName(name1);
      BindingFaultElement bindingFault = binding.addBindingFaultElement();
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
	  if(val.testAssertionBinding0054(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0054 method returned true for a binding with an operation and a fault and no interface.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
  }
  
  /**
   * Test that the testAssertionBinding0056 method returns
   * true if the binding type is absolute, false otherwise.
   */
  public void testTestAssertionBinding0056()
  {
    // Test that the assertion returns true for a binding with an absolute type.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
	  BindingElement binding = desc.addBindingElement();
	  binding.setType(new URI("http://www.sample.org"));
	  if(!val.testAssertionBinding0056(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0056 method returned false for a binding with an absolute type.");
	  }
	}
	catch(URISyntaxException e)
	{
	  fail("There was a problem creating the type URI for the test method " + e);
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns false for a binding with a relative type.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
	  BindingElement binding = desc.addBindingElement();
	  binding.setType(new URI("sample.org"));
	  if(val.testAssertionBinding0056(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0056 method returned true for a binding with a relative type.");
	  }
	}
	catch(URISyntaxException e)
	{
	  fail("There was a problem creating the type URI for the test method " + e);
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for a binding with a null type. This will be
	// caught be schema validation.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
	  BindingElement binding = desc.addBindingElement();
	  if(!val.testAssertionBinding0056(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0056 method returned false for a binding with a null type.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionBinding0057 method returns
   * true if all the binding names are unique in the description,
   * false otherwise.
   */
  public void testTestAssertionBinding0057()
  {
    // Test that the assertion returns true for an empty list of bindings.
	try
	{
	  if(!val.testAssertionBinding0057(new Binding[]{}, reporter))
	  {
	    fail("The testAssertionBinding0057 method returned false for an empty list of bindings.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for a list of bindings that only contains one binding.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
	  BindingElement binding = desc.addBindingElement();
	  binding.setName(name1);
	  if(!val.testAssertionBinding0057(desc.getBindings(), reporter))
	  {
	    fail("The testAssertionBinding0057 method returned false for an list of bindings that contains only one binding.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true for a list of bindings that contains no duplicate names.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
	  BindingElement binding = desc.addBindingElement();
	  binding.setName(name1);
      BindingElement binding2 = desc.addBindingElement();
	  binding2.setName(name2);
      BindingElement binding3 = desc.addBindingElement();
	  binding3.setName(name3);
	  
	  Binding[] bindings = desc.getBindings();
	  
	  if(!val.testAssertionBinding0057(bindings, reporter))
	  {
	    fail("The testAssertionBinding0057 method returned false for a list of bindings that contains no duplicates.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for two bindings that are defined with the same QName object.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name1);
      BindingElement binding2 = desc.addBindingElement();
      binding2.setName(name2);
      BindingElement binding3 = desc.addBindingElement();
	  binding3.setName(name1);
	  
	  Binding[] bindings = desc.getBindings();
	  
	  if(val.testAssertionBinding0057(bindings, reporter))
	  {
	    fail("The testAssertionBinding0057 method returned true for a list of binginds that contains two bindings defined with the same QName object.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for two bindings that are defined with the same name and
	// different QName objects.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      BindingElement binding = desc.addBindingElement();
	  binding.setName(name1);
      BindingElement binding2 = desc.addBindingElement();
      binding2.setName(name2);
      BindingElement binding3 = desc.addBindingElement();
	  binding3.setName(new NCName("name1"));
	  
	  Binding[] bindings = desc.getBindings();
	  
	  if(val.testAssertionBinding0057(bindings, reporter))
	  {
	    fail("The testAssertionBinding0057 method returned true for a list of bindings that contains two bindings with the same name defined with different QName objects.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionBinding0055 method returns
   * true if all the interface operations have bindings defined,
   * false otherwise.
   */
  public void testTestAssertionBinding0055()
  {
    // Test that the assertion returns true when the binding does not specify an interface.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name1);
	  if(!val.testAssertionBinding0055(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0055 method returned false for a binding with no defined interface.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true when an interface is specified with no operations and
    // the binding has no operations defined.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
	  if(!val.testAssertionBinding0055(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0055 method returned false for a binding with an interface with no operations.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true when an interface is specified with no operations and
	// the binding has an operation defined.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
	  if(!val.testAssertionBinding0055(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0055 method returned false for a binding with an interface with no operations and one binding operation defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true when an interface is specified with one operation and
	// the binding defines a binding operation for the interface operation.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceOperationElement interfaceOperation = interfac.addInterfaceOperationElement();
      interfaceOperation.setName(name2);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
	  bindingOperation.setRef(name2QN);
      
	  if(!val.testAssertionBinding0055(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0055 method returned false for a binding with an interface with one operation and one binding operation defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false when an interface is specified with one operation and
	// the binding defines no operations.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceOperationElement interfaceOperation = interfac.addInterfaceOperationElement();
      interfaceOperation.setName(name2);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      
	  if(val.testAssertionBinding0055(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0055 method returned true for a binding with an interface with one operation and no binding operation defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true when an interface is specified with one operation through
	// an extended interface and the binding defines a binding operation for the operation.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      interfac.addExtendedInterfaceName(name2QN);
      InterfaceElement interfac2 = desc.addInterfaceElement();
      interfac2.setName(name2);
      InterfaceOperationElement interfaceOperation = interfac2.addInterfaceOperationElement();
      interfaceOperation.setName(name3);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
	  binding.setInterfaceName(name1QN);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
	  bindingOperation.setRef(name3QN);
      
	  if(!val.testAssertionBinding0055(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0055 method returned false for a binding with an interface with one extended operation and one binding operation defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false when an interface is specified with one operation through
	// an extended interface and the binding defines no operations.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      interfac.addExtendedInterfaceName(name2QN);
      InterfaceElement interfac2 = desc.addInterfaceElement();
      interfac2.setName(name2);
      InterfaceOperationElement interfaceOperation = interfac2.addInterfaceOperationElement();
      interfaceOperation.setName(name3);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      
	  if(val.testAssertionBinding0055(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0055 method returned true for a binding with an interface with one extended operation and no binding operation defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true when an interface is specified with two operations, one
	// explicit and one inherited, and the binding defines operations for both.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      interfac.addExtendedInterfaceName(name2QN);
      InterfaceOperationElement interfaceOperation = interfac.addInterfaceOperationElement();
      interfaceOperation.setName(name3);
      InterfaceImpl interfac2 = new InterfaceImpl();
      interfac2.setName(name2);
      InterfaceOperationElement interfaceOperation2 = interfac2.addInterfaceOperationElement();
      interfaceOperation2.setName(name4);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
      bindingOperation.setRef(name3QN);
      BindingOperationElement bindingOperation2 = binding.addBindingOperationElement();
      bindingOperation2.setRef(name4QN);
      
	  if(!val.testAssertionBinding0055(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0055 method returned false for a binding with an interface with one defined operation and one extended operation and two binding operations defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false when an interface is specified with two operations, one
	// explicit and one inherited, and the binding defines an operation only for the explicit operation.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      interfac.addExtendedInterfaceName(name2QN);
      InterfaceOperationElement interfaceOperation = interfac.addInterfaceOperationElement();
      interfaceOperation.setName(name3);
      InterfaceElement interfac2 = desc.addInterfaceElement();
      interfac2.setName(name2);
      InterfaceOperationElement interfaceOperation2 = interfac2.addInterfaceOperationElement();
      interfaceOperation2.setName(name4);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
      bindingOperation.setRef(name3QN);
      
	  if(val.testAssertionBinding0055(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0055 method returned true for a binding with an interface with one defined operation and one extended operation and one binding operation defined for the defined operation.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false when an interface is specified with two operations, one
	// explicit and one inherited, and the binding defines an operation only for the inherited operation.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      interfac.addExtendedInterfaceName(name2QN);
      InterfaceOperationElement interfaceOperation = interfac.addInterfaceOperationElement();
      interfaceOperation.setName(name3);
      InterfaceImpl interfac2 = new InterfaceImpl();
      interfac2.setName(name2);
      InterfaceOperationElement interfaceOperation2 = interfac2.addInterfaceOperationElement();
      interfaceOperation2.setName(name4);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
      bindingOperation.setRef(name4QN);
      
	  if(val.testAssertionBinding0055(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0055 method returned true for a binding with an interface with one defined operation and one inherited operation and one binding operation defined for the inherited operation.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	handler.reset();
	
	// Test that two messages are returned when an interface with two operations, one explicit and one
	// inherited, is specified and the binding defines no operations.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      interfac.addExtendedInterfaceName(name2QN);
      InterfaceOperationElement interfaceOperation = interfac.addInterfaceOperationElement();
      interfaceOperation.setName(name3);
      InterfaceElement interfac2 = desc.addInterfaceElement();
      interfac2.setName(name2);
      InterfaceOperationElement interfaceOperation2 = interfac2.addInterfaceOperationElement();
      interfaceOperation2.setName(name4);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      
	  if(val.testAssertionBinding0055(desc.getBindings()[0], reporter))
	  {
	    fail("The testAssertionBinding0055 method returned true for a binding with an interface with one defined operation and one inherited operation and no binding operations defined.");
	  }
	  if(handler.numErrors != 2)
	  {
		  fail("The testAssertionBinding0055 method did not report two errors for a binding with an interface with one defined operation and one inherited operation and no binding operations defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionBindingOperation0059 method returns
   * true if all the binding operations have unique interface 
   * operations specified, false otherwise.
   */
  public void testTestAssertionBindingOperation0059()
  {
    // Test that the assertion returns true when there are no binding operations defined.
	try
	{
	  if(!val.testAssertionBindingOperation0059(new BindingOperation[]{}, reporter))
	  {
	    fail("The testAssertionBindingOperation0059 method returned false with no binding operations defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true when there is one binding operation defined.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceOperationElement interfaceOperation = interfac.addInterfaceOperationElement();
      interfaceOperation.setName(name2);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
	  bindingOperation.setRef(name2QN);
      
	  if(!val.testAssertionBindingOperation0059(desc.getBindings()[0].getBindingOperations(), reporter))
	  {
	    fail("The testAssertionBindingOperation0059 method returned false with one valid binding operation defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true when there are two binding operations defined with
	// unique interface operations.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceOperationElement interfaceOperation = interfac.addInterfaceOperationElement();
      interfaceOperation.setName(name3);
      InterfaceOperationElement interfaceOperation2 = interfac.addInterfaceOperationElement();
      interfaceOperation2.setName(name4);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
      bindingOperation.setRef(name3QN);
      BindingOperationElement bindingOperation2 = binding.addBindingOperationElement();
      bindingOperation2.setRef(name4QN);
      
	  if(!val.testAssertionBindingOperation0059(desc.getBindings()[0].getBindingOperations(), reporter))
	  {
	    fail("The testAssertionBindingOperation0059 method returned false with two valid binding operations defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	
	// Test that the assertion returns false when there are two binding operations defined with
	// the same interface operation.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceOperationElement interfaceOperation = interfac.addInterfaceOperationElement();
      interfaceOperation.setName(name3);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
      bindingOperation.setRef(name3QN);
      BindingOperationElement bindingOperation2 = binding.addBindingOperationElement();
      bindingOperation2.setRef(name3QN);
      
	  if(val.testAssertionBindingOperation0059(desc.getBindings()[0].getBindingOperations(), reporter))
	  {
	    fail("The testAssertionBindingOperation0059 method returned true with two binding operations defined with the same interface operation.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionBindingMessageReference0060 method returns
   * true if all the binding message references have unique interface 
   * message references specified, false otherwise.
   */
  public void testTestAssertionBindingMessageReference0060()
  {
    // Test that the assertion returns true when there are no binding message references defined.
	try
	{
	  if(!val.testAssertionBindingMessageReference0060(new BindingMessageReference[]{}, reporter))
	  {
	    fail("The testAssertionBindingMessageReference0060 method returned false with no binding message references defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true when there is one binding message reference defined.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceOperationElement interfaceOperation = interfac.addInterfaceOperationElement();
      interfaceOperation.setName(name3);
      InterfaceMessageReferenceElement interfaceMessageReference = interfaceOperation.addInterfaceMessageReferenceElement();
      interfaceMessageReference.setMessageLabel(MessageLabel.IN);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
      bindingOperation.setRef(name3QN);

      BindingMessageReferenceElement bindingMessageReference = bindingOperation.addBindingMessageReferenceElement();
      bindingMessageReference.setMessageLabel(MessageLabel.IN);
          
	  if(!val.testAssertionBindingMessageReference0060(desc.getBindings()[0].getBindingOperations()[0].getBindingMessageReferences(), reporter))
	  {
	    fail("The testAssertionBindingMessageReference0060 method returned false with one valid binding message reference defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true when there are two binding message references defined with
	// unique interface message references.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceOperationElement interfaceOperation = interfac.addInterfaceOperationElement();
      interfaceOperation.setName(name3);
      InterfaceMessageReferenceElement interfaceMessageReference = interfaceOperation.addInterfaceMessageReferenceElement();
      interfaceMessageReference.setMessageLabel(MessageLabel.IN);
      InterfaceMessageReferenceElement interfaceMessageReference2 = interfaceOperation.addInterfaceMessageReferenceElement();
      interfaceMessageReference2.setMessageLabel(MessageLabel.OUT);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
      bindingOperation.setRef(name3QN);
      BindingMessageReferenceElement bindingMessageReference = bindingOperation.addBindingMessageReferenceElement();
      bindingMessageReference.setMessageLabel(MessageLabel.IN);
      BindingMessageReferenceElement bindingMessageReference2 = bindingOperation.addBindingMessageReferenceElement();
      bindingMessageReference2.setMessageLabel(MessageLabel.OUT);
          
	  if(!val.testAssertionBindingMessageReference0060(desc.getBindings()[0].getBindingOperations()[0].getBindingMessageReferences(), reporter))
	  {
	    fail("The testAssertionBindingMessageReference0060 method returned false with two valid binding message references defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	
	// Test that the assertion returns false when there are two binding message references define with
	// the same interface message reference.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceOperationElement interfaceOperation = interfac.addInterfaceOperationElement();
      interfaceOperation.setName(name3);
      InterfaceMessageReferenceElement interfaceMessageReference = interfaceOperation.addInterfaceMessageReferenceElement();
      interfaceMessageReference.setMessageLabel(MessageLabel.IN);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
      bindingOperation.setRef(name3QN);
      BindingMessageReferenceElement bindingMessageReference = bindingOperation.addBindingMessageReferenceElement();
      bindingMessageReference.setMessageLabel(MessageLabel.IN);
      BindingMessageReferenceElement bindingMessageReference2 = bindingOperation.addBindingMessageReferenceElement();
      bindingMessageReference2.setMessageLabel(MessageLabel.IN);
          
	  if(val.testAssertionBindingMessageReference0060(desc.getBindings()[0].getBindingOperations()[0].getBindingMessageReferences(), reporter))
	  {
	    fail("The testAssertionBindingMessageReference0060 method returned true with two binding message references defined with the same interface message reference.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionBindingFaultReference0061 method returns
   * true if all the binding fault references have unique interface 
   * fault references specified, false otherwise.
   */
  public void testTestAssertionBindingFaultReference0061()
  {
    // Test that the assertion returns true when there are no binding fault references defined.
	try
	{
	  if(!val.testAssertionBindingFaultReference0061(new BindingFaultReference[]{}, reporter))
	  {
	    fail("The testAssertionBindingFaultReference0061 method returned false with no binding fault references defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true when there is one binding fault reference defined.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceFaultElement interfaceFault = interfac.addInterfaceFaultElement();
      interfaceFault.setName(name3);
      InterfaceOperationElement interfaceOperation = interfac.addInterfaceOperationElement();
      interfaceOperation.setName(name4);
      InterfaceFaultReferenceElement interfaceFaultReference = interfaceOperation.addInterfaceFaultReferenceElement();
      interfaceFaultReference.setMessageLabel(MessageLabel.IN);
      interfaceFaultReference.setRef(name3QN);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
      bindingOperation.setRef(name4QN);
      BindingFaultReferenceElement bindingFaultReference = bindingOperation.addBindingFaultReferenceElement();

      bindingFaultReference.setMessageLabel(MessageLabel.IN);
      bindingFaultReference.setRef(name3QN);
          
	  if(!val.testAssertionBindingFaultReference0061(desc.getBindings()[0].getBindingOperations()[0].getBindingFaultReferences(), reporter))
	  {
	    fail("The testAssertionBindingFaultReference0061 method returned false with one valid binding fault reference defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true when there are two binding fault references defined with
	// unique interface fault references.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceFaultElement interfaceFault = interfac.addInterfaceFaultElement();
      interfaceFault.setName(name3);
      InterfaceOperationElement interfaceOperation = interfac.addInterfaceOperationElement();
      interfaceOperation.setName(name4);
      InterfaceFaultReferenceElement interfaceFaultReference = interfaceOperation.addInterfaceFaultReferenceElement();
      interfaceFaultReference.setMessageLabel(MessageLabel.IN);
      interfaceFaultReference.setRef(name3QN);
      InterfaceFaultReferenceElement interfaceFaultReference2 = interfaceOperation.addInterfaceFaultReferenceElement();
      interfaceFaultReference2.setMessageLabel(MessageLabel.OUT);
      interfaceFaultReference2.setRef(name3QN);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
      bindingOperation.setRef(name4QN);
      BindingFaultReferenceElement bindingFaultReference = bindingOperation.addBindingFaultReferenceElement();
      bindingFaultReference.setMessageLabel(MessageLabel.IN);
      bindingFaultReference.setRef(name3QN);
      BindingFaultReferenceElement bindingFaultReference2 = bindingOperation.addBindingFaultReferenceElement();
      bindingFaultReference2.setMessageLabel(MessageLabel.OUT);
      bindingFaultReference2.setRef(name3QN);
          
	  if(!val.testAssertionBindingFaultReference0061(desc.getBindings()[0].getBindingOperations()[0].getBindingFaultReferences(), reporter))
	  {
	    fail("The testAssertionBindingFaultReference0061 method returned false with two valid binding fault references defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	
	// Test that the assertion returns false when there are two binding fault references define with
	// the same interface fault reference.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceFaultElement interfaceFault = interfac.addInterfaceFaultElement();
      interfaceFault.setName(name3);
      InterfaceOperationElement interfaceOperation = interfac.addInterfaceOperationElement();
      interfaceOperation.setName(name4);
      InterfaceFaultReferenceElement interfaceFaultReference = interfaceOperation.addInterfaceFaultReferenceElement();
      interfaceFaultReference.setMessageLabel(MessageLabel.IN);
      interfaceFaultReference.setRef(name3QN);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
      bindingOperation.setRef(name4QN);
      BindingFaultReferenceElement bindingFaultReference = bindingOperation.addBindingFaultReferenceElement();
      bindingFaultReference.setMessageLabel(MessageLabel.IN);
      bindingFaultReference.setRef(name3QN);
      BindingFaultReferenceElement bindingFaultReference2 = bindingOperation.addBindingFaultReferenceElement();
      bindingFaultReference2.setMessageLabel(MessageLabel.IN);
      bindingFaultReference2.setRef(name3QN);
          
	  if(val.testAssertionBindingFaultReference0061(desc.getBindings()[0].getBindingOperations()[0].getBindingFaultReferences(), reporter))
	  {
	    fail("The testAssertionBindingFaultReference0061 method returned true with two binding fault references defined with the same interface fault reference.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionBindingFaultReference0062 method returns
   * true if all the binding fault reference has a valid reference to an
   * interface fault reference, false otherwise.
   */
  public void testTestAssertionBindingFaultReference0062()
  {
    // Test that the assertion returns true when the binding fault reference defines a valid
	// interface fault reference.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceFaultElement interfaceFault = interfac.addInterfaceFaultElement();
      interfaceFault.setName(name2);
      InterfaceOperationElement interfaceOper = interfac.addInterfaceOperationElement();
      interfaceOper.setName(name1);
      InterfaceFaultReferenceElement interfaceFaultReference = interfaceOper.addInterfaceFaultReferenceElement();
      interfaceFaultReference.setRef(name2QN);
      interfaceFaultReference.setMessageLabel(MessageLabel.IN);
      
      BindingElement binding = desc.addBindingElement();
      binding.setInterfaceName(name1QN);
      BindingOperationElement bindingOperation = binding.addBindingOperationElement();
      bindingOperation.setRef(name1QN);
      BindingFaultReferenceElement bindingFaultReference = bindingOperation.addBindingFaultReferenceElement();
	  bindingFaultReference.setRef(name2QN);
      bindingFaultReference.setMessageLabel(MessageLabel.IN);
      
      desc.getBindings(); //init Binding's ref to its Description
      
	  if(!val.testAssertionBindingFaultReference0062((BindingFaultReferenceImpl)bindingFaultReference, reporter))
	  {
	    fail("The testAssertionBindingFaultReference0062 method returned false with a binding fault reference with a valid interface fault reference defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false when the binding fault reference does not define an
	// interface fault reference.
	try
	{
	  BindingFaultReferenceImpl bindingFaultReference = new BindingFaultReferenceImpl();
	  if(val.testAssertionBindingFaultReference0062(bindingFaultReference, reporter))
	  {
	    fail("The testAssertionBindingFaultReference0062 method returned true with a binding fault reference that does not define an interface fault reference.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// TODO: Test with an invalid interface fault reference - how does the model represent this?
  }
  
  /**
   * Test that the testAssertionBindingFault0058 method returns
   * true if all the binding faults have unique references to
   * interface faults, false otherwise.
   */
  public void testTestAssertionBindingFault0058()
  {	
    // Test that the assertion returns true for an empty list of binding faults.
	try
	{
	  if(!val.testAssertionBindingFault0058(new BindingFault[]{}, reporter))
	  {
	    fail("The testAssertionBindingFault0058 method returned false for an empty list of binding faults.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for a list of binding faults that only contains one binding.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceFaultElement interfaceFault = interfac.addInterfaceFaultElement();
      interfaceFault.setName(name2);
      
      BindingElement binding = desc.addBindingElement();
      binding.setInterfaceName(name1QN);
      BindingFaultElement bindingFault = binding.addBindingFaultElement();
	  bindingFault.setRef(name2QN);
      
      desc.getBindings(); //init Binding's ref to its Description
      
      if(!val.testAssertionBindingFault0058(new BindingFault[]{(BindingFaultImpl)bindingFault}, reporter))
	  {
	    fail("The testAssertionBindingFault0058 method returned false for an list of binding faults that contains only one binding.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true for a list of binding faults that contains no duplicate interface fault references.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceFaultElement interfaceFault = interfac.addInterfaceFaultElement();
      interfaceFault.setName(name1);
      InterfaceFaultElement interfaceFault2 = interfac.addInterfaceFaultElement();
      interfaceFault2.setName(name2);
      InterfaceFaultElement interfaceFault3 = interfac.addInterfaceFaultElement();
      interfaceFault.setName(name3);
      
      BindingElement binding = desc.addBindingElement();
      binding.setInterfaceName(name1QN);
      BindingFaultElement bindingFault = binding.addBindingFaultElement();
	  bindingFault.setRef(name1QN);
      BindingFaultElement bindingFault2 = binding.addBindingFaultElement();
	  bindingFault2.setRef(name2QN);
      BindingFaultElement bindingFault3 = binding.addBindingFaultElement();
	  bindingFault3.setRef(name3QN);
	  
      desc.getBindings(); //init Binding's ref to its Description
      
	  BindingFault[] bindingFaults = new BindingFault[]{(BindingFaultImpl)bindingFault, (BindingFaultImpl)bindingFault2, (BindingFaultImpl)bindingFault3};
	  
	  if(!val.testAssertionBindingFault0058(bindingFaults, reporter))
	  {
	    fail("The testAssertionBindingFault0058 method returned false for a list of binding faults that contains no duplicate interface fault references.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for two binding faults that are defined with the same interface fault reference.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceFaultElement interfaceFault = interfac.addInterfaceFaultElement();
      interfaceFault.setName(name1);
      InterfaceFaultElement interfaceFault2 = interfac.addInterfaceFaultElement();
      interfaceFault2.setName(name2);
      
      BindingElement binding = desc.addBindingElement();
      binding.setInterfaceName(name1QN);
      BindingFaultElement bindingFault = binding.addBindingFaultElement();
      bindingFault.setRef(name1QN);
      BindingFaultElement bindingFault2 = binding.addBindingFaultElement();
      bindingFault2.setRef(name2QN);
      BindingFaultElement bindingFault3 = binding.addBindingFaultElement();
      bindingFault3.setRef(name1QN);
      
      desc.getBindings(); //init Binding's ref to its Description
      
	  BindingFault[] bindingFaults = new BindingFault[]{(BindingFaultImpl)bindingFault, (BindingFaultImpl)bindingFault2, (BindingFaultImpl)bindingFault3};
	  
	  if(val.testAssertionBindingFault0058(bindingFaults, reporter))
	  {
	    fail("The testAssertionBindingFault0058 method returned true for a list of binging faults that contains two binding faults defined with the same interface fault reference.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionService0063 method returns
   * true if the list of services contains no services
   * with duplicate names, false otherwise.
   */
  public void testTestAssertionService0063()
  {
	// Test that the assertion returns true for an empty list of services.
	try
	{
	  if(!val.testAssertionService0063(new Service[]{}, reporter))
	  {
	    fail("The testAssertionService0063 method returned false for an empty list of services.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for a service with no name.
	try
	{
	  ServiceImpl service = new ServiceImpl();
	  if(!val.testAssertionService0063(new Service[]{service}, reporter))
	  {
	    fail("The testAssertionService0063 method returned false for a service with no defined name.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for a service that is the only service defined.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      ServiceImpl service = new ServiceImpl();
	  service.setName(name1);
	  if(!val.testAssertionService0063(new Service[]{service}, reporter))
	  {
	    fail("The testAssertionService0063 method returned false for a service that is the only service defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true for a list of services that contains no duplicate names.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      ServiceImpl service = new ServiceImpl();
	  service.setName(name1);
	  ServiceImpl service2 = new ServiceImpl();
      service2.setName(name2);
	  ServiceImpl service3 = new ServiceImpl();
      service3.setName(name3);
	  
	  Service[] services = new Service[]{service, service2, service3};
	  
	  if(!val.testAssertionService0063(services, reporter))
	  {
	    fail("The testAssertionService0063 method returned false for a list of services that contains no duplicate names.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for two services that are defined with the same QName object.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      ServiceImpl service = new ServiceImpl();
      service.setName(name1);
      ServiceImpl service2 = new ServiceImpl();
      service2.setName(name2);
      ServiceImpl service3 = new ServiceImpl();
	  service3.setName(name1);
	  
	  Service[] services = new Service[]{service, service2, service3};
	  
	  if(val.testAssertionService0063(services, reporter))
	  {
	    fail("The testAssertionService0063 method returned true for a list of services that contains two services defined with the same QName object.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for two services that are defined with the same name and
	// different QName objects.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
      ServiceImpl service = new ServiceImpl();
      service.setName(name1);
      ServiceImpl service2 = new ServiceImpl();
      service2.setName(name2);
      ServiceImpl service3 = new ServiceImpl();
      service3.setName(new NCName("name1"));
	  
	  Service[] services = new Service[]{service, service2, service3};
	  
	  if(val.testAssertionService0063(services, reporter))
	  {
	    fail("The testAssertionService0063 method returned true for a list of services that contains two services with the same name defined with different QName objects.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionEndpoint0065 method returns
   * true if the list of endpoints contains no endpoints
   * with duplicate names, false otherwise.
   */
  public void testTestAssertionEndpoint0065()
  {
	NCName name1 = new NCName("name1");
	NCName name2 = new NCName("name2");
	NCName name3 = new NCName("name3");
	
	// Test that the assertion returns true for an empty list of endpoints.
	try
	{
	  if(!val.testAssertionEndpoint0065(new Endpoint[]{}, reporter))
	  {
	    fail("The testAssertionEndpoint0065 method returned false for an empty list of endpoints.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for an endpoint with no name.
	try
	{
	  EndpointImpl endpoint = new EndpointImpl();
	  if(!val.testAssertionEndpoint0065(new Endpoint[]{endpoint}, reporter))
	  {
	    fail("The testAssertionEndpoint0065 method returned false for an endpoint with no defined name.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true for an endpoint an empty NCname.
	try
	{
	  EndpointImpl endpoint = new EndpointImpl();
	  endpoint.setName(new NCName());
	  if(!val.testAssertionEndpoint0065(new Endpoint[]{endpoint}, reporter))
	  {
	    fail("The testAssertionEndpoint0065 method returned false for an endpoint with an empty NCName.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for an endpoint that is the only endpoint defined.
	try
	{
	  EndpointImpl endpoint = new EndpointImpl();
	  endpoint.setName(name1);
	  if(!val.testAssertionEndpoint0065(new Endpoint[]{endpoint}, reporter))
	  {
	    fail("The testAssertionEndpoint0065 method returned false for an endpoint that is the only endpoint defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true for a list of endpoints that contains no duplicate names.
	try
	{
	  EndpointImpl endpoint = new EndpointImpl();
	  endpoint.setName(name1);
	  EndpointImpl endpoint2 = new EndpointImpl();
	  endpoint2.setName(name2);
	  EndpointImpl endpoint3 = new EndpointImpl();
	  endpoint3.setName(name3);
	  
	  Endpoint[] endpoints = new Endpoint[]{endpoint, endpoint2, endpoint3};
	  
	  if(!val.testAssertionEndpoint0065(endpoints, reporter))
	  {
	    fail("The testAssertionEndpoint0065 method returned false for a list of endpoints that contains no duplicate names.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for two endpoints that are defined with the same NCName object.
	try
	{
	  EndpointImpl endpoint = new EndpointImpl();
	  endpoint.setName(name1);
	  EndpointImpl endpoint2 = new EndpointImpl();
	  endpoint2.setName(name2);
	  EndpointImpl endpoint3 = new EndpointImpl();
	  endpoint3.setName(name1);
	  
	  Endpoint[] endpoints = new Endpoint[]{endpoint, endpoint2, endpoint3};
	  
	  if(val.testAssertionEndpoint0065(endpoints, reporter))
	  {
	    fail("The testAssertionEndpoint0065 method returned true for a list of endpoints that contains two endpoints defined with the same NCName object.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for two endpoints that are defined with the same name and
	// different NCName objects.
	try
	{
	  EndpointImpl endpoint = new EndpointImpl();
	  endpoint.setName(name1);
	  EndpointImpl endpoint2 = new EndpointImpl();
	  endpoint2.setName(name2);
	  EndpointImpl endpoint3 = new EndpointImpl();
	  endpoint3.setName(new NCName("name1"));
	  
	  Endpoint[] endpoints = new Endpoint[]{endpoint, endpoint2, endpoint3};
	  
	  if(val.testAssertionEndpoint0065(endpoints, reporter))
	  {
	    fail("The testAssertionEndpoint0065 method returned true for a list of endpoints that contains two endpoints with the same name defined with different NCName objects.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionEndpoint0064 method returns
   * true if the endpoint address is absolute, false otherwise.
   */
  public void testTestAssertionEndpoint0064()
  {
    // Test that the assertion returns true for an endpoint with an absolute address.
	try
	{
	  EndpointImpl endpoint = new EndpointImpl();
	  endpoint.setAddress(new URI("http://www.sample.org"));
	  if(!val.testAssertionEndpoint0064(endpoint, reporter))
	  {
	    fail("The testAssertionEndpoint0064 method returned false for an endpoint with an absolute address.");
	  }
	}
	catch(URISyntaxException e)
	{
	  fail("There was a problem creating the address URI for the test method " + e);
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns false for an endpoint with a relative address.
	try
	{
	  EndpointImpl endpoint = new EndpointImpl();
	  endpoint.setAddress(new URI("sample.org"));
	  if(val.testAssertionEndpoint0064(endpoint, reporter))
	  {
	    fail("The testAssertionEndpoint0064 method returned true for an endpoint with a relative address.");
	  }
	}
	catch(URISyntaxException e)
	{
	  fail("There was a problem creating the address URI for the test method " + e);
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    // Test that the assertion returns true for an endpoint with a null address. This will be
	// caught be schema validation.
	try
	{
	  EndpointImpl endpoint = new EndpointImpl();
	  if(!val.testAssertionEndpoint0064(endpoint, reporter))
	  {
	    fail("The testAssertionEndpoint0064 method returned false for an endpoint with a null address.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
  
  /**
   * Test that the testAssertionEndpoint0066 method returns
   * true if the endpoint binding does not specify an interface or
   * specifies the same interface as the endpoint's parent service,
   * false otherwise.
   */
  public void testTestAssertionEndpoint0066()
  {
    /* (jkaputin) By following the Woden programming model, the implementation will initialize 'parent' references.
     * To create an endpoint without a parent, this test case cannot use the correct programming model and a NPE 
     * is thrown on endpoint.getParent(). This test is commented out while the issue is being considered.
     * 
     * TODO either assume that model is schema-validated prior to semantic checking and remove this test case OR
     * change the getParent() behaviour across the whole model to check for nulls. Probably the latter option,
     * to better support programmatic creation of a wsdl model. 
     */
    /*
    // Test that the assertion returns true for an endpoint with no binding or parent defined.
	try
	{
	  EndpointImpl endpoint = new EndpointImpl();
	  if(!val.testAssertionEndpoint0066(endpoint, reporter))
	  {
	    fail("The testAssertionEndpoint0066 method returned false for an endpoint with no binding or parent defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
    */
	
	// Test that the assertion returns true for an endpoint with no binding defined.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      ServiceElement service = desc.addServiceElement();
	  EndpointElement endpoint = service.addEndpointElement();
	  if(!val.testAssertionEndpoint0066(desc.getServices()[0].getEndpoints()[0], reporter))
	  {
	    fail("The testAssertionEndpoint0066 method returned false for an endpoint with no binding defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
    /* (jkaputin) ditto the comment above for the first test case (NPE on getParent)
	// Test that the assertion returns true for an endpoint with no parent defined.
	try
	{
	  EndpointImpl endpoint = new EndpointImpl();
	  BindingElement binding = desc.createBindingElement();
      binding.setName(name1);
	  endpoint.setBindingName(name1);
	  if(!val.testAssertionEndpoint0066(endpoint, reporter))
	  {
	    fail("The testAssertionEndpoint0066 method returned false for an endpoint with no parent defined.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
    */
	
	// Test that the assertion returns true for an endpoint that specifies a binding with no interface specified.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      ServiceElement service = desc.addServiceElement();
      service.setInterfaceName(name1QN);
      EndpointElement endpoint = service.addEndpointElement();
      endpoint.setBindingName(name2QN);
	  if(!val.testAssertionEndpoint0066(desc.getServices()[0].getEndpoints()[0], reporter))
	  {
	    fail("The testAssertionEndpoint0066 method returned false for an endpoint that specifies a binding with no specified interface.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns true for an endpoint that specifies a binding with the same interface
	// as the parent service specified.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name2);
      binding.setInterfaceName(name1QN);
      ServiceElement service = desc.addServiceElement();
      service.setInterfaceName(name1QN);
      EndpointElement endpoint = service.addEndpointElement();
      endpoint.setBindingName(name2QN);
	  if(!val.testAssertionEndpoint0066(desc.getServices()[0].getEndpoints()[0], reporter))
	  {
	    fail("The testAssertionEndpoint0066 method returned false for an endpoint that specifies a binding with the same interface specified as the parent service specifies.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
	
	// Test that the assertion returns false for an endpoint that specifies a binding with a different interface
	// than the parent service specified.
	try
	{
      DescriptionImpl desc = new DescriptionImpl();
      desc.setTargetNamespace(namespace1);
	  InterfaceElement interfac = desc.addInterfaceElement();
      interfac.setName(name1);
      InterfaceElement interfac2 = desc.addInterfaceElement();
      interfac2.setName(name2);
      BindingElement binding = desc.addBindingElement();
      binding.setName(name3);
      binding.setInterfaceName(name2QN);
      ServiceElement service = desc.addServiceElement();
      service.setInterfaceName(name1QN);
      EndpointElement endpoint = service.addEndpointElement();
      endpoint.setBindingName(name3QN);
	  if(val.testAssertionEndpoint0066(desc.getServices()[0].getEndpoints()[0], reporter))
	  {
	    fail("The testAssertionEndpoint0066 method returned true for an endpoint that specifies a binding with a different interface specified than the parent service specifies.");
	  }
	}
	catch(WSDLException e)
	{
	  fail("There was a problem running the test assertion method " + e);
	}
  }
}

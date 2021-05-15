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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.woden.ErrorReporter;
import org.apache.woden.WSDLException;
import org.apache.woden.internal.ErrorLocatorImpl;
import org.apache.woden.internal.wsdl20.Constants;
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
import org.apache.woden.wsdl20.WSDLComponent;
import org.apache.woden.wsdl20.enumeration.MessageLabel;

/**
 * The WSDL component validator can validate a WSDL 2.0 component model
 * against the assertions defined in the WSDL 2.0 specification. The
 * WSDL 2.0 component assertions are currently available in the
 * non-normative version of the WSDL 2.0 specification and can be viewed at
 * http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/wsdl20/wsdl20.html?content-type=text/html;%20charset=utf-8#assertionsummary
 */
public class WSDLComponentValidator 
{
  /**
   * Validate the WSDL 2.0 component model described by the
   * description component.
   * 
   * @param desc The description component of the WSDL 2.0 component model.
   * @param errorReporter An error reporter to be used for reporting errors found with the model.
   * @throws WSDLException A WSDLException is thrown if a problem occurs while validating the WSDL 2.0 component model.
   */
  public void validate(Description desc, ErrorReporter errorReporter) throws WSDLException
  {
    validateInterfaces(desc.getInterfaces(), desc, errorReporter);
    validateBindings(desc.getBindings(), desc, errorReporter);
    validateServices(desc.getServices(), desc, errorReporter);	
  }
	
  /**
   * Validate the interfaces in the WSDL component model.
   * 
   * @param interfaces The interfaces in the WSDL component model.
   * @param desc The WSDL 2.0 description component.
   * @param errorReporter An error reporter to be used for reporting errors.
   * @throws WSDLException A WSDLException is thrown if a problem occurs while validating the interface components.
   */
  protected void validateInterfaces(Interface[] interfaces, Description desc, ErrorReporter errorReporter) throws WSDLException
  {
	testAssertionInterface0030(interfaces, errorReporter);
	  
	int numInterfaces = interfaces.length;
	for(int i = 0; i < numInterfaces; i++)
	{
      Interface interfac = interfaces[i];
      
	  testAssertionInterface0027(interfac, errorReporter);
	  
	  InterfaceOperation[] interfaceOperations = interfac.getInterfaceOperations();
	  testAssertionInterfaceOperation0035(interfaceOperations, errorReporter);
	  
	  int numInterfaceOperations = interfaceOperations.length;
	  for(int j = 0; j < numInterfaceOperations; j++)
	  {
		InterfaceOperation interfaceOperation = interfaceOperations[j];
		
		testAssertionMEP0037(interfaceOperation.getMessageExchangePattern(), errorReporter);
		testAssertionInterfaceOperation0029(interfaceOperation, interfac, errorReporter);
		
		InterfaceMessageReference[] messageReferences = interfaceOperation.getInterfaceMessageReferences();
		
		testAssertionInterfaceMessageReference0042(messageReferences, errorReporter);
		
		int numMessageReferences = messageReferences.length;
		for(int k = 0; k < numMessageReferences; k++)
		{
		  InterfaceMessageReference messageReference = messageReferences[k];
		  
		  testAssertionInterfaceMessageReference0041(messageReference, errorReporter);
		  
		  validateFeatures(messageReference.getFeatures(), errorReporter);
		  validateProperties(messageReference.getProperties(), desc, errorReporter);
		}
		
		InterfaceFaultReference[] faultReferences = interfaceOperation.getInterfaceFaultReferences();
		
		testAssertionInterfaceFaultReference0045(faultReferences, errorReporter);
		
		int numFaultReferences = faultReferences.length;
		for(int k = 0; k < numFaultReferences; k++)
		{
		  InterfaceFaultReference faultReference = faultReferences[k];
		  
		  validateFeatures(faultReference.getFeatures(), errorReporter);
		  validateProperties(faultReference.getProperties(), desc, errorReporter);
		}
		
		validateFeatures(interfaceOperation.getFeatures(), errorReporter);
		validateProperties(interfaceOperation.getProperties(), desc, errorReporter);
	  }
	  
	  InterfaceFault[] interfaceFaults = interfac.getInterfaceFaults();
	  testAssertionInterfaceFault0032(interfaceFaults, errorReporter);
	  
	  int numInterfaceFaults = interfaceFaults.length;
	  for(int j = 0; j < numInterfaceFaults; j++)
	  {
		InterfaceFault interfaceFault = interfaceFaults[j];
		
		testAssertionInterfaceFault0028(interfaceFault, interfac, errorReporter);
		
		validateFeatures(interfaceFault.getFeatures(), errorReporter);
		validateProperties(interfaceFault.getProperties(), desc, errorReporter);
	  }
	  
	  validateFeatures(interfac.getFeatures(), errorReporter);
	  validateProperties(interfac.getProperties(), desc, errorReporter);
	}

//		
//		# test for equivalence of two fault definitions. Should the equivalence code be in the InterfaceFault class.
//		interface-fault-0033 = 
//		interface-fault-0033.ref = 2.3.1
//		interface-fault-0033.assertion = In cases where, due to an interface extending one or more other interfaces, two or more Interface Fault components have the same value for their {name} property, then the component models of those Interface Fault components MUST be equivalent (see 2.17 Equivalence of Components).
	//  # next three messages are MEP related
//		interface-fault-ref-0043 = 
//		interface-fault-ref-0043.ref = 2.6.1
//		interface-fault-ref-0043.assertion = The value of this property MUST match the name of a placeholder message defined by the message exchange pattern.
	//
//		interface-fault-ref-0044 = 
//		interface-fault-ref-0044.ref = 2.6.1
//		interface-fault-ref-0044.assertion = The direction MUST be consistent with the direction implied by the fault propagation ruleset used in the message exchange pattern of the operation.
	//
//		
//		interface-message-ref-0040 = 
//		interface-message-ref-0040.ref = 2.5.1
//		interface-message-ref-0040.assertion = The direction MUST be the same as the direction of the message identified by the {message label} property in the {message exchange pattern} of the Interface Operation component this is contained within.
	//
//		
//		
//		
//		interface-operation-0036 = 
//		interface-operation-0036 = 2.4.1
//		interface-operation-0036.assertion = In cases where, due to an interface extending one or more other interfaces, two or more Interface Operation components have the same value for their {name} property, then the component models of those Interface Operation components MUST be equivalent (see 2.17 Equivalence of Components).
	//
//		interface-operation-0038 = 
//		interface-operation-0038.ref = 2.4.1.2
//		interface-operation-0038.assertion = An Interface Operation component MUST satisfy the specification defined by each operation style identified by its {style} property.
	//
		
  }
  
  /**
   * Validate the bindings in the WSDL component model.
   * 
   * @param bindings The bindings in the WSDL component model.
   * @param desc The WSDL 2.0 description component.
   * @param errorReporter An error reporter to be used for reporting errors.
   * @throws WSDLException A WSDLException is thrown if a problem occurs while validating the binding components.
   */
  protected void validateBindings(Binding[] bindings, Description desc, ErrorReporter errorReporter) throws WSDLException
  {
	  testAssertionBinding0057(bindings, errorReporter);
	  
	  int numBindings = bindings.length;
	  for(int i = 0; i < numBindings; i++)
	  {
		  Binding binding = bindings[i];
		  testAssertionBinding0054(binding, errorReporter);
		  testAssertionBinding0055(binding, errorReporter);
		  testAssertionBinding0056(binding, errorReporter);
		  
		  validateBindingOperations(binding.getBindingOperations(), desc, errorReporter);
		  
		  validateBindingFault(binding.getBindingFaults(), desc, errorReporter);
		  
		  validateFeatures(binding.getFeatures(), errorReporter);
		  validateProperties(binding.getProperties(), desc, errorReporter);
	  }
  }
  
  /**
   * Validate the binding operations in the WSDL component model.
   *  
   * @param bindingOperations The bindings operations in the WSDL component model.
   * @param desc The WSDL 2.0 description component.
   * @param errorReporter An error reporter to be used for reporting errors.
   * @throws WSDLException A WSDLException is thrown if a problem occurs while validating the binding operation components.
   */
  protected void validateBindingOperations(BindingOperation[] bindingOperations, Description desc, ErrorReporter errorReporter) throws WSDLException
  {
	  testAssertionBindingOperation0059(bindingOperations, errorReporter);
	  
	  int numBindingOperations = bindingOperations.length;
	  for(int i = 0; i < numBindingOperations; i++)
	  {
		  BindingOperation bindingOperation = bindingOperations[i];
		  validateBindingMessageReferences(bindingOperation.getBindingMessageReferences(), desc, errorReporter);
		  validateBindingFaultReferences(bindingOperation.getBindingFaultReferences(), desc, errorReporter);
		  
		  validateFeatures(bindingOperation.getFeatures(), errorReporter);
		  validateProperties(bindingOperation.getProperties(), desc, errorReporter);
	  }
	  
  }
  
  /**
   * Validate the binding message references in the WSDL component model.
   * 
   * @param bindingMessageReferences The bindings message references in the WSDL component model.
   * @param desc The WSDL 2.0 description component.
   * @param errorReporter An error reporter to be used for reporting errors.
   * @throws WSDLException A WSDLException is thrown if a problem occurs while validating the binding message reference components.
   */
  protected void validateBindingMessageReferences(BindingMessageReference[] bindingMessageReferences, Description desc, ErrorReporter errorReporter) throws WSDLException
  {
	  testAssertionBindingMessageReference0060(bindingMessageReferences, errorReporter);
	  
	  int numBindingMessageReferences = bindingMessageReferences.length;
	  
	  for(int i = 0; i < numBindingMessageReferences; i++)
	  {
		BindingMessageReference bindingMessageReference = bindingMessageReferences[i];
		
		validateFeatures(bindingMessageReference.getFeatures(), errorReporter);
		validateProperties(bindingMessageReference.getProperties(), desc, errorReporter);
	  }
  }
  
  /**
   * Validate the binding fault references in the WSDL component model.
   * 
   * @param bindingFaultReferences The bindings fault references in the WSDL component model.
   * @param desc The WSDL 2.0 description component.
   * @param errorReporter An error reporter to be used for reporting errors.
   * @throws WSDLException A WSDLException is thrown if a problem occurs while validating the binding fault reference components.
   */
  protected void validateBindingFaultReferences(BindingFaultReference[] bindingFaultReferences, Description desc, ErrorReporter errorReporter) throws WSDLException
  {
	  testAssertionBindingFaultReference0061(bindingFaultReferences, errorReporter);
	  
	  int numBindingFaultReferences = bindingFaultReferences.length;
	  for(int i = 0; i < numBindingFaultReferences; i++)
	  {
		BindingFaultReference bindingFaultReference = bindingFaultReferences[i];
	    testAssertionBindingFaultReference0062(bindingFaultReference, errorReporter);
	    
	    validateFeatures(bindingFaultReference.getFeatures(), errorReporter);
	    validateProperties(bindingFaultReference.getProperties(), desc, errorReporter);
	  }
  }
  
  /**
   * Validate the binding faults in the WSDL component model.
   * 
   * @param bindingFaults The bindings faults in the WSDL component model.
   * @param desc The WSDL 2.0 description component.
   * @param errorReporter An error reporter to be used for reporting errors.
   * @throws WSDLException A WSDLException is thrown if a problem occurs while validating the binding fault components.
   */
  protected void validateBindingFault(BindingFault[] bindingFaults, Description desc, ErrorReporter errorReporter) throws WSDLException
  {
	testAssertionBindingFault0058(bindingFaults, errorReporter);
	
	int numBindingFaults = bindingFaults.length;
	
	for(int i = 0; i < numBindingFaults; i++)
	{
	  BindingFault bindingFault = bindingFaults[i];
	  
	  validateFeatures(bindingFault.getFeatures(), errorReporter);
	  validateProperties(bindingFault.getProperties(), desc, errorReporter);
	}
  }
  
  /**
   * Validate the services in the WSDL component model.
   * 
   * @param services The services in the WSDL component model.
   * @param desc The WSDL 2.0 description component.
   * @param errorReporter An error reporter to be used for reporting errors.
   * @throws WSDLException A WSDLException is thrown if a problem occurs while validating the service components.
   */
  protected void validateServices(Service[] services, Description desc, ErrorReporter errorReporter) throws WSDLException
  {
	  testAssertionService0063(services, errorReporter);
	  
	  int numServices = services.length;
	  for(int i = 0; i < numServices; i++)
	  {
		  Service service = services[i];
		  
		  validateEndpoints(service.getEndpoints(), desc, errorReporter);
		  
		  validateFeatures(service.getFeatures(), errorReporter);
		  validateProperties(service.getProperties(), desc, errorReporter);
	  }
  }
  
  /**
   * Validate the endpoints in a service component in the WSDL component model.
   * 
   * @param endpoints The endpoints in a service in the WSDL component model.
   * @param desc The WSDL 2.0 description component.
   * @param errorReporter An error reporter to be used for reporting errors.
   * @throws WSDLException A WSDLException is thrown if a problem occurs while validating the endpoint components.
   */
  protected void  validateEndpoints(Endpoint[] endpoints, Description desc, ErrorReporter errorReporter) throws WSDLException
  {
	  testAssertionEndpoint0065(endpoints, errorReporter);

	  int numEndpoints = endpoints.length;
	  for(int i = 0; i < numEndpoints; i++)
	  {
		  Endpoint endpoint = endpoints[i];
		  testAssertionEndpoint0064(endpoint, errorReporter);
		  testAssertionEndpoint0066(endpoint, errorReporter);
		  
		  validateFeatures(endpoint.getFeatures(), errorReporter);
		  validateProperties(endpoint.getProperties(), desc, errorReporter);
	  }
  }
  
  /**
   * Validate the properties in the WSDL component model.
   * 
   * @param properties The properties in the WSDL component model.
   * @param desc The WSDL 2.0 description component.
   * @param errorReporter An error reporter to be used for reporting errors.
   * @throws WSDLException A WSDLException is thrown if a problem occurs while validating the property components.
   */
  protected void validateProperties(Property[] properties, Description desc, ErrorReporter errorReporter) throws WSDLException
  {
	testAssertionProperty0050(properties, errorReporter);
	
	int numProperties = properties.length;
	
	for(int i = 0; i < numProperties; i++)
	{
	  Property property = properties[i];
	  
	  testAssertionPropertyRef0048(property, errorReporter);
	  testAssertionProperty0049(property, desc, errorReporter);
	  testAssertionProperty0049b(property, errorReporter);
	}
  }
  
  /**
   * Validate the features in the WSDL component model.
   * 
   * @param features The features in the WSDL component model.
   * @param errorReporter An error reporter to be used for reporting errors.
   * @throws WSDLException A WSDLException is thrown if a problem occurs while validating the feature components.
   */
  protected void validateFeatures(Feature[] features, ErrorReporter errorReporter) throws WSDLException
  {
	testAssertionFeatureRef0047(features, errorReporter);
		
	int numFeatures = features.length;
	for(int i = 0; i < numFeatures; i++)
	{
      testAssertionFeatureRef0046(features[i], errorReporter);
	}
  }
	
  /**
   * Test assertion Interface-0027. An interface cannot appear, either directly or indirectly,
   * in the list of interfaces it extends.
   * 
   * @param interfac The interface to check.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionInterface0027(Interface interfac, ErrorReporter errorReporter) throws WSDLException
  {
	Interface[] extendedInterfaces = interfac.getExtendedInterfaces();
	Interface extendedInterface = containsInterface(interfac, extendedInterfaces);
	if(extendedInterface != null)
	{
	  errorReporter.reportError(new ErrorLocatorImpl(), "Interface-0027", new Object[]{extendedInterface.getName()}, ErrorReporter.SEVERITY_ERROR);
	  return false;
	}
	return true;
  }
	
  /**
   * Helper method for testAssertionInterface0027.
   * Check whether the specified interface is in the array of provided
   * interfaces or in an array of interfaces one of the interfaces extends.
   * 
   * @param interfac The interface to check for.
   * @param extendedInterfaces An array of extened interfaces to check for this interface.
   * @return The interface that is equal to or extends this interface, or null if the interface is not contained.
   */
  private Interface containsInterface(Interface interfac, Interface[] extendedInterfaces)
  {
	int numExtInterfaces = extendedInterfaces.length;
	for(int i = 0; i < numExtInterfaces; i++)
	{
	  if(interfac.equals(extendedInterfaces[i]))
	    return extendedInterfaces[i];
	  else if(containsInterface(interfac, extendedInterfaces[i].getExtendedInterfaces()) != null)
		return extendedInterfaces[i];
	}
	return null;
  }
  
  /**
   * Test assertion Interface-0030. An interface must have a unique name out of all the interfaces
   * in the description component.
   * 
   * @param interfaces The interfaces in the description component.
   * @param errorReporter The error reporter.
   * @return True if all the interfaces have unique names, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionInterface0030(Interface[] interfaces, ErrorReporter errorReporter) throws WSDLException
  {
	boolean duplicateFound = false;
	List names = new ArrayList();
	int numInterfaces = interfaces.length;
	for(int i = 0; i < numInterfaces; i++)
	{
	  QName name = interfaces[i].getName();
	  if(names.contains(name))
	  {
		errorReporter.reportError(new ErrorLocatorImpl(), "Interface-0030", new Object[]{name}, ErrorReporter.SEVERITY_ERROR);
		duplicateFound = true;
	  }
	  else
	  {
		names.add(name);
	  }
	}
	return !duplicateFound;
  }
  
  /**
   * Test assertion MEP-0037. A message exchange pattern must be an absolute IRI.
   * 
   * @param pattern The message exchange pattern to check.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionMEP0037(URI pattern, ErrorReporter errorReporter) throws WSDLException
  {
	if(!pattern.isAbsolute())
	{
	  errorReporter.reportError(new ErrorLocatorImpl(), "MEP-0037", new Object[]{pattern}, ErrorReporter.SEVERITY_ERROR);
	  return false;
	}
	return true;
  }
  
  /**
   * Test assertion InterfaceOperation-0029. The namespace name of the interface operation
   * must be the same as the namespace name of the interface.
   * 
   * @param interfaceOperation The interface operation to check the namespace of.
   * @param interfac The interface parent of the interface operation.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionInterfaceOperation0029(InterfaceOperation interfaceOperation, Interface interfac, ErrorReporter errorReporter) throws WSDLException
  {
	if(!interfaceOperation.getName().getNamespaceURI().equals(interfac.getName().getNamespaceURI())) //TODO null checks
	{
	  errorReporter.reportError(new ErrorLocatorImpl(), "InterfaceOperation-0029", new Object[]{}, ErrorReporter.SEVERITY_ERROR);
	  return false;
	}
	return true;
  }
  
  /**
   * Test assertion InterfaceOperation-0035. An interface operation must have a unique name out of all the interface
   * operations defined in an interface component.
   * 
   * @param interfaceOperations The interface operations to check for duplicate names.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionInterfaceOperation0035(InterfaceOperation[] interfaceOperations, ErrorReporter errorReporter) throws WSDLException
  {
	List names = new ArrayList();
	int numInterfaces = interfaceOperations.length;
	for(int i = 0; i < numInterfaces; i++)
	{
	  QName name = interfaceOperations[i].getName();
	  if(names.contains(name))
	  {
		errorReporter.reportError(new ErrorLocatorImpl(), "InterfaceOperation-0035", new Object[]{name}, ErrorReporter.SEVERITY_ERROR);
		return false;
	  }
	  else
	  {
		names.add(name);
	  }
	}
	return true;
  }
  
  /**
   * Test assertion InterfaceFault-0032. An interface fault must have a unique name out of all the interface
   * faults defined in an interface component.
   * 
   * @param interfaceFaults The interface faults to check for duplicate names.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionInterfaceFault0032(InterfaceFault[] interfaceFaults, ErrorReporter errorReporter) throws WSDLException
  {
	List names = new ArrayList();
	int numInterfaceFaults = interfaceFaults.length;
	for(int i = 0; i < numInterfaceFaults; i++)
	{
	  QName name = interfaceFaults[i].getName();
	  if(names.contains(name))
	  {
		errorReporter.reportError(new ErrorLocatorImpl(), "InterfaceFault-0032", new Object[]{name}, ErrorReporter.SEVERITY_ERROR);
		return false;
	  }
	  else
	  {
		names.add(name);
	  }
	}
	return true;
  }
  
  /**
   * Test assertion InterfaceFault-0028. The namespace name of the interface fault
   * must be the same as the namespace name of the interface.
   * 
   * @param interfaceFault The interface fault to check the namespace of.
   * @param interfac The interface parent of the interface fault.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionInterfaceFault0028(InterfaceFault interfaceFault, Interface interfac, ErrorReporter errorReporter) throws WSDLException
  {
	if(!interfaceFault.getName().getNamespaceURI().equals(interfac.getName().getNamespaceURI())) //TODO null checks
	{
	  errorReporter.reportError(new ErrorLocatorImpl(), "InterfaceFault-0028", new Object[]{}, ErrorReporter.SEVERITY_ERROR);
	  return false;
	}
	return true;
  }
  
  /**
   * Test assertion InterfaceMessageReference-0041. When the {message content model} property 
   * has the value #any or #none the {element declaration} property MUST be empty.
   * 
   * @param messageReference The interface message reference to check the message content model and element declarations.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionInterfaceMessageReference0041(InterfaceMessageReference messageReference, ErrorReporter errorReporter) throws WSDLException
  {
	String messContentModel = messageReference.getMessageContentModel();
	if((messContentModel.equals(Constants.NMTOKEN_ANY) || messContentModel.equals(Constants.NMTOKEN_NONE)) 
		&& messageReference.getElementDeclaration() != null)
	{
	  errorReporter.reportError(new ErrorLocatorImpl(), "InterfaceMessageReference-0041", new Object[]{}, ErrorReporter.SEVERITY_ERROR);
	  return false;
	}
	return true;
  }
  
  /**
   * Test assertion InterfaceMessageReference-0042. For each Interface Message Reference 
   * component in the {interface message references} property of an Interface Operation 
   * component, its {message label} property MUST be unique.
   * 
   * @param messageReferences The message references to check for duplicate names.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionInterfaceMessageReference0042(InterfaceMessageReference[] messageReferences, ErrorReporter errorReporter) throws WSDLException
  {
	List messageLabels = new ArrayList();
	int numMessageReferences = messageReferences.length;
	for(int i = 0; i < numMessageReferences; i++)
	{
	  MessageLabel messageLabel = messageReferences[i].getMessageLabel();
	  if(messageLabels.contains(messageLabel))
	  {
		errorReporter.reportError(new ErrorLocatorImpl(), "InterfaceMessageReference-0042", new Object[]{messageLabel}, ErrorReporter.SEVERITY_ERROR);
		return false;
	  }
	  else
	  {
		messageLabels.add(messageLabel);
	  }
	}
	return true;
  }
  
  /**
   * Test assertion InterfaceFaultReference-0045. For each Interface Fault Reference 
   * component in the {interface fault references} property of an Interface Operation 
   * component, the combination of its {interface fault} and {message label} properties 
   * MUST be unique.
   * 
   * @param faultReferences The fault references to check for duplicate fault/message label pairs.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionInterfaceFaultReference0045(InterfaceFaultReference[] faultReferences, ErrorReporter errorReporter) throws WSDLException
  {
	Hashtable identifiers = new Hashtable();
	
	int numFaultReferences = faultReferences.length;
	for(int i = 0; i < numFaultReferences; i++)
	{
	  InterfaceFault fault = faultReferences[i].getInterfaceFault();
	  MessageLabel messageLabel = faultReferences[i].getMessageLabel();
      if(fault == null || messageLabel == null)
    	continue;
	  List messageLabels = (List)identifiers.get(fault);
	  if(messageLabels != null && messageLabels.contains(messageLabel))
	  {
	    errorReporter.reportError(new ErrorLocatorImpl(), "InterfaceFaultReference-0045", new Object[]{fault, messageLabel}, ErrorReporter.SEVERITY_ERROR);
		return false;
      }
	  else
	  {
		if(messageLabels == null)
		  messageLabels = new ArrayList();
		messageLabels.add(messageLabel);
		identifiers.put(fault, messageLabels);
	  }
	}
	return true;
  }
  
  /**
   * Test assertion FeatureRef-0046. The feature ref xs:anyURI MUST be an 
   * absolute IRI as defined by [IETF RFC 3987].
   * 
   * @param feature The feature to check the ref of.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionFeatureRef0046(Feature feature, ErrorReporter errorReporter) throws WSDLException
  {
	URI ref = feature.getRef();
    if(ref != null && !ref.isAbsolute())
    {
      errorReporter.reportError(new ErrorLocatorImpl(), "FeatureRef-0046", new Object[]{ref}, ErrorReporter.SEVERITY_ERROR);
	  return false;
    }
    return true;
  }
  
  /**
   * Test assertion FeatureRef-0047. The {ref} property of a 
   * Feature component MUST be unique within the {features} 
   * property of an Interface, Interface Fault, Interface Operation, 
   * Interface Message Reference, Interface Fault Reference, Binding, 
   * Binding Fault, Binding Operation, Binding Message Reference, 
   * Binding Fault Reference, Service, or Endpoint component.
   * 
   * @param features The features within a given component to check the refs for uniqueness.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionFeatureRef0047(Feature[] features, ErrorReporter errorReporter) throws WSDLException
  {
	boolean isValid = true;
	List refs = new ArrayList();
	int numFeatures = features.length;
	for(int i = 0; i < numFeatures; i++)
	{
	  URI ref = features[i].getRef();
	  if(ref == null)
		continue;
      if(refs.contains(ref))
      {
        errorReporter.reportError(new ErrorLocatorImpl(), "FeatureRef-0047", new Object[]{ref}, ErrorReporter.SEVERITY_ERROR);
	    isValid = false;
      }
      else
      {
    	refs.add(ref);
      }
	}
    return isValid;
  }
  
  /**
   * Test assertion PropertyRef-0048. The property ref xs:anyURI MUST be an 
   * absolute IRI as defined by [IETF RFC 3987].
   * 
   * @param property The property to check the ref of.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionPropertyRef0048(Property property, ErrorReporter errorReporter) throws WSDLException
  {
	URI ref = property.getRef();
    if(ref != null && !ref.isAbsolute())
    {
      errorReporter.reportError(new ErrorLocatorImpl(), "PropertyRef-0048", new Object[]{ref}, ErrorReporter.SEVERITY_ERROR);
	  return false;
    }
    return true;
  }
  
  /**
   * Test assertion Property-0050. The {ref} property of a Property 
   * component MUST be unique within the {properties} property of an 
   * Interface, Interface Fault, Interface Operation, Interface Message 
   * Reference, Interface Fault Reference, Binding, Binding Fault, 
   * Binding Operation, Binding Message Reference, Binding Fault Reference, 
   * Service, or Endpoint component.
   * 
   * @param properties The properties within a given component to check the refs for uniqueness.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionProperty0050(Property[] properties, ErrorReporter errorReporter) throws WSDLException
  {
	boolean isValid = true;
	List refs = new ArrayList();
	int numProperties = properties.length;
	for(int i = 0; i < numProperties; i++)
	{
	  URI ref = properties[i].getRef();
	  if(ref == null)
		continue;
      if(refs.contains(ref))
      {
        errorReporter.reportError(new ErrorLocatorImpl(), "Property-0050", new Object[]{ref}, ErrorReporter.SEVERITY_ERROR);
	    isValid = false;
      }
      else
      {
    	refs.add(ref);
      }
	}
    return isValid;
  }
  
  /**
   * Test assertion Property-0049. A reference to a Type Definition 
   * component in the {type definitions} property of the Description 
   * component constraining the value of the Property, or the token 
   * #value if the {value} property is not empty.
   * 
   * This test focuses on the first part of the statement, that the type
   * definition references a type definition in the description component.
   * 
   * @param property The property for which to check the contstraint.
   * @param desc The description component.
   * @param errorReporter The error Reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionProperty0049(Property property, Description desc, ErrorReporter errorReporter) throws WSDLException
  {
	QName constraint = property.toElement().getConstraintName();
	if(constraint != null && desc.getTypeDefinition(constraint) == null)
	{
	  errorReporter.reportError(new ErrorLocatorImpl(), "Property-0049", new Object[]{constraint}, ErrorReporter.SEVERITY_ERROR);
  	  return false;
	}
    return true;
  }
  
  /**
   * Test assertion Property-0049. A reference to a Type Definition 
   * component in the {type definitions} property of the Description 
   * component constraining the value of the Property, or the token 
   * #value if the {value} property is not empty.
   * 
   * This test focuses on the second part of the statement, checking that
   * the constraint is set to #value if the value is not empty.
   * 
   * @param property The property for which to check the contstraint.
   * @param errorReporter The error Reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionProperty0049b(Property property, ErrorReporter errorReporter) throws WSDLException
  {
	if(property.getValue() != null && !property.toElement().hasValueToken())
	{
	  errorReporter.reportError(new ErrorLocatorImpl(), "Property-0049", new Object[]{}, ErrorReporter.SEVERITY_ERROR);
	  return false;
	}
    return true;
  }
  
  /**
   * Test assertion Binding-0054. If a Binding component specifies any 
   * operation-specific binding details (by including Binding Operation 
   * components) or any fault binding details (by including Binding Fault 
   * components) then it MUST specify an interface the Binding  component 
   * applies to, so as to indicate which interface the operations come from.
   * 
   * @param binding The binding for which to check the contstraint.
   * @param errorReporter The error Reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionBinding0054(Binding binding, ErrorReporter errorReporter) throws WSDLException
  {
	BindingOperation[] bindingOperations = binding.getBindingOperations();
	BindingFault[] bindingFaults = binding.getBindingFaults();
	Interface bindingInterface = binding.getInterface();
	if(((bindingOperations != null && bindingOperations.length > 0) || 
		(bindingFaults != null && bindingFaults.length > 0)) && 
		 bindingInterface == null)
	{
	  errorReporter.reportError(new ErrorLocatorImpl(), "Binding-0054", new Object[]{}, ErrorReporter.SEVERITY_ERROR);
	  return false;
	}
    return true;
  }
  
  /**
   * Test assertion Binding-0055. A Binding component that defines bindings 
   * for an Interface component MUST define bindings for all the operations 
   * of that Interface component.
   * 
   * @param binding The binding of which to check the binding operations.
   * @param errorReporter The error reporter.
   * @return True if the all the operations specified on the interface have bindings defined, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionBinding0055(Binding binding, ErrorReporter errorReporter) throws WSDLException
  {
	boolean allInterfaceOperationsHaveBinding = true;
    QName bindingQN = binding.getName();
	String bindingName = bindingQN != null ? bindingQN.getLocalPart() : null;
	
	Interface interfac = binding.getInterface();
	if(interfac == null)
	  return true;
	
	BindingOperation[] bindingOperations = binding.getBindingOperations();
	int numBindingOperations = bindingOperations.length;
	List usedInterfaceOperationList = new ArrayList();
	for(int i = 0; i < numBindingOperations; i++)
	{
	  InterfaceOperation io = bindingOperations[i].getInterfaceOperation();
	  if(io != null)
		usedInterfaceOperationList.add(io);
	}
    // Check the interface operations.
	if(!checkAllInterfaceOperationsHaveBinding(bindingName, interfac, usedInterfaceOperationList, errorReporter))
	  allInterfaceOperationsHaveBinding = false;
	
	Interface[] extendedInterfaces = interfac.getExtendedInterfaces();
	if(extendedInterfaces != null)
	{
	  int numExtendedInterfaces = extendedInterfaces.length;
	  for(int i = 0; i < numExtendedInterfaces; i++)
  	  {
	    if(!checkAllInterfaceOperationsHaveBinding(bindingName, extendedInterfaces[i], usedInterfaceOperationList, errorReporter))
	      allInterfaceOperationsHaveBinding = false;
	  }
	}
	
    return allInterfaceOperationsHaveBinding;
  }
  
  /**
   * Helper method for testAssertionBinding0055. This method checks that
   * each interface operation in the provided interface has a binding 
   * defined.
   * 
   * @param bindingName The name of the binding. Used in error reporting.
   * @param interfac The interface of which to check the operations.
   * @param usedInterfaceOperations A list of interface operations that have bindings specified.
   * @param errorReporter The error reporter.
   * @return True if all the interface operations have bindings defined, false otherwise.
   * @throws WSDLException
   */
  private boolean checkAllInterfaceOperationsHaveBinding(String bindingName, Interface interfac, List usedInterfaceOperations, ErrorReporter errorReporter) throws WSDLException
  {
	boolean allInterfaceOperationsHaveBinding = true;
	InterfaceOperation[] interfaceOperations = interfac.getInterfaceOperations();
	if(interfaceOperations == null)
	  return true;
	
	int numInterfaceOperations = interfaceOperations.length;
	
	// Check the interface operations.
	for(int i = 0; i < numInterfaceOperations; i++)
	{
	  if(!usedInterfaceOperations.contains(interfaceOperations[i]))
	  {
	    errorReporter.reportError(new ErrorLocatorImpl(), "Binding-0055", new Object[]{bindingName, interfaceOperations[i].getName()}, ErrorReporter.SEVERITY_ERROR);
		allInterfaceOperationsHaveBinding = false;
	  }
    }
	return allInterfaceOperationsHaveBinding;
  }
  
  /**
   * Test assertion Binding-0056. The binding type xs:anyURI MUST be an 
   * absolute IRI as defined by [IETF RFC 3987].
   * 
   * @param binding The binding of which to check the type.
   * @param errorReporter The error reporter.
   * @return True if the type is absolute, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionBinding0056(Binding binding, ErrorReporter errorReporter) throws WSDLException
  {
	URI type = binding.getType();
    if(type != null && !type.isAbsolute())
    {
      errorReporter.reportError(new ErrorLocatorImpl(), "Binding-0056", new Object[]{type}, ErrorReporter.SEVERITY_ERROR);
	  return false;
    }
    return true;
  }
  
  /**
   * Test assertion Binding-0057. For each Binding component in the {bindings} property of a 
   * Description component, the {name} property MUST be unique.
   * 
   * @param bindings The bindings in the description component.
   * @param errorReporter The error reporter.
   * @return True if the all the bindings have unique names, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionBinding0057(Binding[] bindings, ErrorReporter errorReporter) throws WSDLException
  {
	boolean duplicateFound = false;
	List names = new ArrayList();
	int numBindings = bindings.length;
	for(int i = 0; i < numBindings; i++)
	{
	  QName name = bindings[i].getName();
	  if(names.contains(name))
	  {
		errorReporter.reportError(new ErrorLocatorImpl(), "Binding-0057", new Object[]{name}, ErrorReporter.SEVERITY_ERROR);
		duplicateFound = true;
	  }
	  else
	  {
		names.add(name);
	  }
	}
	return !duplicateFound;
  }
  
  /**
   * Test assertion BindingFault-0058. For each Binding Fault component in the 
   * {binding faults} property of a Binding component, the {interface fault} 
   * property MUST be unique.
   * 
   * @param bindingFaults The binding faults in the description component.
   * @param errorReporter The error reporter.
   * @return True if the all the bindings have unique names, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionBindingFault0058(BindingFault[] bindingFaults, ErrorReporter errorReporter) throws WSDLException
  {
	boolean duplicateFound = false;
	List usedInterfaceFaults = new ArrayList();
	int numBindingFaults = bindingFaults.length;
	for(int i = 0; i < numBindingFaults; i++)
	{
	  InterfaceFault interfaceFault = bindingFaults[i].getInterfaceFault();
	  if(usedInterfaceFaults.contains(interfaceFault))
	  {
		errorReporter.reportError(new ErrorLocatorImpl(), "BindingFault-0058", new Object[]{}, ErrorReporter.SEVERITY_ERROR);
		duplicateFound = true;
	  }
	  else
	  {
		usedInterfaceFaults.add(interfaceFault);
	  }
	}
	return !duplicateFound;
  }
  
  /**
   * Test assertion BindingOperation-0059. For each Binding Operation component 
   * in the {binding operations} property of a Binding component, the {interface 
   * operation} property MUST be unique.
   * 
   * @param bindingOperations The binding operations to check for unique interface operations.
   * @param errorReporter The error reporter.
   * @return True if the all the binding operations have specified unique interface operations, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionBindingOperation0059(BindingOperation[] bindingOperations, ErrorReporter errorReporter) throws WSDLException
  {
	boolean duplicateFound = false;
	List specifiedInterfaceOperations = new ArrayList();
	int numBindingOperations = bindingOperations.length;
	for(int i = 0; i < numBindingOperations; i++)
	{
	  InterfaceOperation interfaceOperation = bindingOperations[i].getInterfaceOperation();
	  if(specifiedInterfaceOperations.contains(interfaceOperation))
	  {
		errorReporter.reportError(new ErrorLocatorImpl(), "BindingOperation-0059", new Object[]{interfaceOperation.getName()}, ErrorReporter.SEVERITY_ERROR);
		duplicateFound = true;
	  }
	  else
	  {
		specifiedInterfaceOperations.add(interfaceOperation);
	  }
	}
	return !duplicateFound;
  }
  
  /**
   * Test assertion BindingMessageReference-0060. For each Binding Message 
   * Reference component in the {binding message references} property of a 
   * Binding Operation component, the {interface message reference} property 
   * MUST be unique.
   * 
   * @param bindingMessageReferences The binding message references to check for unique interface message references.
   * @param errorReporter The error reporter.
   * @return True if the all the binding message references have specified unique interface message references, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionBindingMessageReference0060(BindingMessageReference[] bindingMessageReferences, ErrorReporter errorReporter) throws WSDLException
  {
	boolean duplicateFound = false;
	List specifiedInterfaceMessageReferences = new ArrayList();
	int numBindingMessageReferences = bindingMessageReferences.length;
	for(int i = 0; i < numBindingMessageReferences; i++)
	{
	  InterfaceMessageReference interfaceMessageReference = bindingMessageReferences[i].getInterfaceMessageReference();
	  if(specifiedInterfaceMessageReferences.contains(interfaceMessageReference))
	  {
		errorReporter.reportError(new ErrorLocatorImpl(), "BindingMessageReference-0060", new Object[]{interfaceMessageReference.getMessageLabel()}, ErrorReporter.SEVERITY_ERROR);
		duplicateFound = true;
	  }
	  else
	  {
		specifiedInterfaceMessageReferences.add(interfaceMessageReference);
	  }
	}
	return !duplicateFound;
  }
  
  /**
   * Test assertion BindingFaultReference-0061. For each Binding Fault Reference 
   * component in the {binding fault references} property of a Binding Operation 
   * component, the {interface fault reference} property MUST be unique.
   * 
   * @param bindingFaultReferences The binding fault references to check for unique interface fault references.
   * @param errorReporter The error reporter.
   * @return True if the all the binding fault references have specified unique interface fault references, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionBindingFaultReference0061(BindingFaultReference[] bindingFaultReferences, ErrorReporter errorReporter) throws WSDLException
  {
	boolean duplicateFound = false;
	List specifiedInterfaceFaultReferences = new ArrayList();
	int numBindingFaultReferences = bindingFaultReferences.length;
	for(int i = 0; i < numBindingFaultReferences; i++)
	{
	  InterfaceFaultReference interfaceFaultReference = bindingFaultReferences[i].getInterfaceFaultReference();
	  if(specifiedInterfaceFaultReferences.contains(interfaceFaultReference))
	  {
		errorReporter.reportError(new ErrorLocatorImpl(), "BindingFaultReference-0061", new Object[]{interfaceFaultReference.getMessageLabel()}, ErrorReporter.SEVERITY_ERROR);
		duplicateFound = true;
	  }
	  else
	  {
		specifiedInterfaceFaultReferences.add(interfaceFaultReference);
	  }
	}
	return !duplicateFound;
  }
  
  /**
   * Test assertion BindingFaultReference-0062. There MUST be an Interface Fault 
   * Reference component in the {interface fault references} of the Interface 
   * Operation being bound with {message label} equal to the effective message 
   * label and with {interface fault} equal to an Interface Fault component with 
   * {name} equal to the actual value of the ref attribute information item.
   * 
   * @param bindingFaultReference The binding fault reference to check if the specified interface fault reference exists.
   * @param errorReporter The error reporter.
   * @return True if the binding fault reference specifies a valid interface fault reference, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionBindingFaultReference0062(BindingFaultReference bindingFaultReference, ErrorReporter errorReporter) throws WSDLException
  {
	InterfaceFaultReference interfaceFaultReference = bindingFaultReference.getInterfaceFaultReference();
	if(interfaceFaultReference == null)
	{
	  errorReporter.reportError(new ErrorLocatorImpl(), "BindingFaultReference-0062", new Object[]{}, ErrorReporter.SEVERITY_ERROR);
      return false;
	}
	return true;
  }
  
  /**
   * Test assertion Service-0063. For each Service  component in the {services} property 
   * of a Description component, the {name} property MUST be unique.
   * 
   * @param services An array containing all the services in the description component.
   * @param errorReporter The error reporter.
   * @return True if all services contain unique names, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionService0063(Service[] services, ErrorReporter errorReporter) throws WSDLException
  {
	List names = new ArrayList();
	int numServices = services.length;
	for(int i = 0; i < numServices; i++)
	{
	  QName name = services[i].getName();
	  if(names.contains(name))
	  {
		errorReporter.reportError(new ErrorLocatorImpl(), "Service-0063", new Object[]{name}, ErrorReporter.SEVERITY_ERROR);
		return false;
	  }
	  else
	  {
		names.add(name);
	  }
	}
	return true;
  }
  
  /**
   * Test assertion Endpoint-0065. For each Endpoint component in the {endpoints} property 
   * of a Service component, the {name} property MUST be unique.
   * 
   * @param endpoints An array containing all the endpoints for a service in the description component.
   * @param errorReporter The error reporter.
   * @return True if all endpoints in the array contain unique names, false otherwise.
   * @throws WSDLException
   */
  protected boolean  testAssertionEndpoint0065(Endpoint[] endpoints, ErrorReporter errorReporter) throws WSDLException
  {
	List names = new ArrayList();
	int numEndpoints = endpoints.length;
	for(int i = 0; i < numEndpoints; i++)
	{
	  NCName name = endpoints[i].getName();
	  if(name == null)
		continue;
	  String sName = name.toString();
	  if(names.contains(sName))
	  {
		errorReporter.reportError(new ErrorLocatorImpl(), "Endpoint-0065", new Object[]{sName}, ErrorReporter.SEVERITY_ERROR);
		return false;
	  }
	  else
	  {
		names.add(sName);
	  }
	}
	return true;
  }
  
  /**
   * Test assertion Endpoint-0064. This xs:anyURI MUST be an absolute IRI as 
   * defined by [IETF RFC 3987]. This xs:anyURI refers to the address IRI.
   * 
   * @param endpoint The endpoint of which the address should be checked.
   * @param errorReporter The error reporter.
   * @return True if the address IRI is absolute, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionEndpoint0064(Endpoint endpoint, ErrorReporter errorReporter) throws WSDLException
  {
	URI address = endpoint.getAddress();
    if(address != null && !address.isAbsolute())
    {
      errorReporter.reportError(new ErrorLocatorImpl(), "Endpoint-0064", new Object[]{address}, ErrorReporter.SEVERITY_ERROR);
	  return false;
    }
    return true;
  }
  
  /**
   * Test assertion Endpoint-0066. For each Endpoint component in the {endpoints} property 
   * of a Service component, the {binding} property MUST either be a Binding component with 
   * an unspecified {interface} property or a Binding component with an {interface} property 
   * equal to the {interface} property of the Service component.
   * 
   * @param endpoint The endpoint of which the binding should be checked.
   * @param errorReporter The error reporter.
   * @return True if the binding specified the interface specified by the service or no interface, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionEndpoint0066(Endpoint endpoint, ErrorReporter errorReporter) throws WSDLException
  {
	Binding binding = endpoint.getBinding();
	// If no binding has been specified this assertion does not apply.
	if(binding == null)
	  return true;
	
	Interface bindingInterface = binding.getInterface();
	WSDLComponent parent = endpoint.getParent();
	if(parent != null)
	{
	  Service service = (Service)parent;
	  Interface serviceInterface = service.getInterface();
	  
	  // If an interface hasn't been specified on the service this assertion doesn't apply.
	  // If the binding interface is null this assertion passes.
	  if(serviceInterface != null && bindingInterface != null && !serviceInterface.equals(bindingInterface))
	  {
		errorReporter.reportError(new ErrorLocatorImpl(), "Endpoint-0066", new Object[]{binding, bindingInterface, serviceInterface}, ErrorReporter.SEVERITY_ERROR);
		return false;  
	  }
	}
	return true;
  }
 
//	property-0051 = 
//	property-0051.ref = 2.8.1.1
//	property-0051.assertion = All specified values MUST be equal and belong to each specified value set.
//
//	
  
//
//	# ---------------- WSDL Component Level Assertions -------------------
//
	// # This assertion may be a dup of others.
//	description-0024 = 
//	description-0024.ref = 2.1.2
//	description-0024.assertion = Each WSDL 2.0 or type system component MUST be uniquely identified by its qualified name.
//
	// # for interface, binding, and service
//	feature-ref-0046 = 
//	feature-ref-0046.ref = 2.7.1
//	feature-ref-0046.assertion = This xs:anyURI MUST be an absolute IRI as defined by [IETF RFC 3987].
//
	// # for interface, binding, and service
//	feature-ref-0047 = 
//	feature-ref-0047.ref = 2.7.1
//	feature-ref-0047.assertion = The {ref} property of a Feature component MUST be unique within the {features} property of an Interface, Interface Fault, Interface Operation, Interface Message Reference, Interface Fault Reference, Binding, Binding Fault, Binding Operation, Binding Message Reference, Binding Fault Reference, Service, or Endpoint component.
//
//	
//	message-label-0039 = 
//	message-label-0039.ref = 2.5.1
//	message-label-0039.assertion = The value of this property MUST match the name of a placeholder message defined by the message exchange pattern.
//
//	property-0049 = 
//	property-0049.ref = 2.8.1
//	property-0049.assertion = A reference to a Type Definition component in the {type definitions} property of the Description component constraining the value of the Property, or the token #value if the {value} property is not empty.
//
//	property-0050 = 
//	property-0050.ref = 2.8.1
//	property-0050.assertion = The {ref} property of a Property component MUST be unique within the {properties} property of an Interface, Interface Fault, Interface Operation, Interface Message Reference, Interface Fault Reference, Binding, Binding Fault, Binding Operation, Binding Message Reference, Binding Fault Reference, Service, or Endpoint component.
//
//	property-0051 = 
//	property-0051.ref = 2.8.1.1
//	property-0051.assertion = All specified values MUST be equal and belong to each specified value set.
//
//	property-ref-0048 = 
//	property-ref-0048.ref = 2.8.1
//	property-ref-0048.assertion = This xs:anyURI MUST an absolute IRI as defined by [IETF RFC 3987].
//
//	qname-0002 = 
//	qname-0002.assertion = Furthermore, all QName references, whether to the same of to difference namespace MUST resolve to components (see 2.1.9 QName resolution).
//
//	types-0026 = 
//	types-0026.ref = 2.1.2.1
//	types-0026.assertion = It is an error if there are multiple type definitions for each QName.

}

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
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.woden.ErrorReporter;
import org.apache.woden.WSDLException;
import org.apache.woden.internal.ErrorLocatorImpl;
import org.apache.woden.internal.wsdl20.Constants;
import org.apache.woden.schema.ImportedSchema;
import org.apache.woden.schema.InlinedSchema;
import org.apache.woden.schema.Schema;
import org.apache.woden.wsdl20.xml.DescriptionElement;
import org.apache.woden.wsdl20.xml.ImportElement;
import org.apache.woden.wsdl20.xml.InterfaceElement;
import org.apache.woden.wsdl20.xml.InterfaceFaultElement;
import org.apache.woden.wsdl20.xml.InterfaceMessageReferenceElement;
import org.apache.woden.wsdl20.xml.InterfaceOperationElement;
import org.apache.woden.wsdl20.xml.TypesElement;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObjectTable;

/**
 * The WSDL document validator validates a WSDL XML model against the
 * document assertions specified in the WSDL 2.0 specification.
 */
public class WSDLDocumentValidator 
{
  /**
   * Validate the document representation of the WSDL document against
   * the WSDL 2.0 specification.
   * 
   * @param descElement The WSDL 2.0 XML model description element.
   * @param errorReporter The error reporter to use for any errors.
   * @return True if the WSDL document representation is valid, false otherwise.
   * @throws WSDLException
   */
  public boolean validate(DescriptionElement descElement, ErrorReporter errorReporter) throws WSDLException
  {
    boolean isValid = true;
    
    // Test the description element.
    isValid = testAssertionDescription0025(descElement, errorReporter);
    
    // Test the import elements.
    ImportElement[] imports = descElement.getImportElements();
    int numImports = imports.length;
    for(int i = 0; i < numImports; i++)
    {
      // TODO: Implement methods once import elements are supported.
//	  if(!testAssertionImport0001(imports[i], errorReporter))
//		isValid = false;
//	  if(!testAssertionImport0003(imports[i], errorReporter))
//		isValid = false;
    }
	if(!validateTypes(descElement.getTypesElement(), errorReporter))
	  isValid = false;
	
	if(!validateInterfaces(descElement, descElement.getInterfaceElements(), errorReporter))
	  isValid = false;

		// 1. Call the validators for specific namespaces
		//    - Does this need to be broken up into XML specific and compoent model?
		// 2. Call post validators
	//	validateTypes(descElement.getTypesElement(), errorReporter);
//		Description descComponent = descElement.getDescriptionComponent();
//		validateInterfaces(descComponent.getInterfaces(), errorReporter);
//		
		// TODO: validate bindings, services, and extension elements
	  
    return isValid;
  }
	
  /**
   * Validate the contents of the types element. This method runs the assertion
   * tests for inline and imported types.
   * 
   * @param types The types element of which to validate the contents.
   * @param errorReporter The error reporter.
   * @return True if all the types related assertions pass, false otherwise.
   * @throws WSDLException
   */
  protected boolean validateTypes(TypesElement types, ErrorReporter errorReporter) throws WSDLException
  {
	boolean isValid = true;
	
	// If there is no types element all types assertions are true.
	if(types == null)
	  return true;
	
	// Test imported schema assertions.
	ImportedSchema[] importedSchemas = types.getImportedSchemas();
	int numImportedSchemas = importedSchemas.length;
	for(int i = 0; i < numImportedSchemas; i++)
	{
	  ImportedSchema schema = (ImportedSchema)importedSchemas[i];
		
	  if(!testAssertionSchema0017(schema, errorReporter))
		isValid = false;
		
	  if(!testAssertionSchema0052(schema, errorReporter))
		isValid = false;
		  
	}
	
	// Test inlined schema assertions.
	InlinedSchema[] inlinedSchemas = types.getInlinedSchemas();
	if(!testAssertionSchema0018(inlinedSchemas, errorReporter))
	  isValid = false;
	int numInlinedSchemas = inlinedSchemas.length;
	for(int i = 0; i < numInlinedSchemas; i++)
	{
	  InlinedSchema schema = (InlinedSchema)inlinedSchemas[i];
		
	  if(!testAssertionSchema0019(schema, errorReporter))
		isValid = false;
		  
	}
	return isValid;
  }
  
  /**
   * Validate the contents of the interface element. This method runs the assertion
   * tests for interface element components.
   * 
   * @param interfaces An array of interface elements for which to validate the contents.
   * @param errorReporter The error reporter.
   * @return True if all the interface related assertions pass, false otherwise.
   * @throws WSDLException
   */
  protected boolean validateInterfaces(DescriptionElement descElement, InterfaceElement[] interfaces, ErrorReporter errorReporter) throws WSDLException
  {
	boolean isValid = true;
	
	int numInterfaceElements = interfaces.length;
	for(int i = 0; i < numInterfaceElements; i++)
	{
	  InterfaceElement interfaceElem = interfaces[i];
	  
	  if(!testAssertionInterface0031(interfaceElem, errorReporter))
		isValid = false;
	  
	  InterfaceFaultElement[] faultElements = interfaceElem.getInterfaceFaultElements();
	  int numFaultElements = faultElements.length;
	  for(int j = 0; j < numFaultElements; j++)
	  {
		InterfaceFaultElement faultElement = faultElements[j];
		if(!testAssertionSchema0020b(descElement, faultElement, errorReporter))
	      isValid = false;
		if(!testAssertionSchema0016(descElement, faultElement.getElementName().getNamespaceURI(), errorReporter))
	      isValid = false;
	  }
	  
	  
	  InterfaceOperationElement[] interfaceOperations = interfaceElem.getInterfaceOperationElements();
	  int numInterfaceOperations = interfaceOperations.length;
	  for(int j = 0; j < numInterfaceOperations; j++)
	  {
		InterfaceOperationElement interfaceOperation = interfaceOperations[j];
		InterfaceMessageReferenceElement[] messageReferences = interfaceOperation.getInterfaceMessageReferenceElements();
		int numMessageReferences = messageReferences.length;
		for(int k = 0; k < numMessageReferences; k++)
		{
	      InterfaceMessageReferenceElement messageReference = messageReferences[k];
	      if(!testAssertionSchema0020(descElement, messageReference, errorReporter))
	        isValid = false;
	      
	      // Only call the namespace assertion if the referenced element name is not null.
	      QName elementName = messageReference.getElementName();
	      if(elementName != null)
	      {
	    	if(!testAssertionSchema0016(descElement, elementName.getNamespaceURI(), errorReporter))
	  	      isValid = false;
	    	
	      }
	      
		}
		
//		FaultReferenceElement[] faultReferences = interfaceOperation.getFaultReferenceElements();
//		int numFaultReferences = faultReferences.length;
//		for(int k = 0; k < numFaultReferences; k++)
//		{
//	      FaultReferenceElement faultReference = faultReferences[k];
//	      if(!testAssertionSchema0020b(descElement, faultReference, errorReporter))
//	        isValid = false;
//		}
	  }
	}

	// The message label assertions use MEP definitions.
//	MessageLabel-0004 = 
//	MessageLabel-0004.assertion = The messageLabel attribute information item of an interface message reference element information item MUST be present if the message exchange pattern has more than one placeholder message with {direction} equal to the message direction.
	//
//	MessageLabel-0005 = 
//	MessageLabel-0005.assertion = The messageLabel attribute information item of an interface fault reference element information item  MUST be present if the message exchange pattern has more than one placeholder message with {direction} equal to the message direction.
	//

//	MessageLabel-0008 = 
//	MessageLabel-0008.assertion =  If the messageLabel attribute information item  of an interface message reference element information item  is present then its actual value MUST match the {message label} of some placeholder message with {direction} equal to the message direction.
	//
//	MessageLabel-0009 = 
//	MessageLabel-0009.assertion =  If the messageLabel attribute information item  of an interface fault reference element information item  is present then its actual value MUST match the {message label} of some placeholder message with {direction} equal to the message direction.
	//

//	MessageLabel-0012 = 
//	MessageLabel-0012.assertion =  If the messageLabel attribute information item  of an interface message reference element information item  is absent then there MUST be a unique placeholder message with {direction} equal to the message direction.
	//
//	MessageLabel-0013 = 
//	MessageLabel-0013.assertion =  If the messageLabel attribute information item  of an interface fault reference element information item  is absent then there MUST be a unique placeholder message with {direction} equal to the message direction.
	//
	
	return isValid;
  }
  /**
   * Test assertion Description-0025. Tests whether the target namespace
   * specified is an absolute IRI.
   * 
   * @param descElement The description element for which to check the target namespace.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionDescription0025(DescriptionElement descElement, ErrorReporter errorReporter) throws WSDLException
  {
    URI targetNS = descElement.getTargetNamespace();
    if(!targetNS.isAbsolute())
    {
      errorReporter.reportError(new ErrorLocatorImpl(), "Description-0025", new Object[]{targetNS}, ErrorReporter.SEVERITY_ERROR);
      return false;
	}
    return true;
  }

  /**
   * Test assertion Schema-0017. An imported schema must contain a
   * target namespace.
   * 
   * @param schema The imported schema to check.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionSchema0017(ImportedSchema schema, ErrorReporter errorReporter) throws WSDLException
  {
	XmlSchema schemaDef = schema.getSchemaDefinition();
	// The assertion is true if the schema definition is not available.
	// Problems locating the schema will be reported elseware and are
	// not part of this assertion.
	if(schemaDef == null)
	  return true;
	
	String targetNS = schemaDef.getTargetNamespace();
	if(targetNS == null || targetNS.equals(""))
	{
	  errorReporter.reportError(new ErrorLocatorImpl(), "Schema-0017", new Object[]{schema.getSchemaLocation()}, ErrorReporter.SEVERITY_ERROR);
	  return false;
	}
	return true;
  }
  
  /**
   * Test assertion Schema-0052. An imported schema must specify the
   * same target namespace as the import element.
   * 
   * @param schema The imported schema to check.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionSchema0052(ImportedSchema schema, ErrorReporter errorReporter) throws WSDLException
  {
	XmlSchema schemaDef = schema.getSchemaDefinition();
    // The assertion is true if the schema definition is not available.
	// Problems locating the schema will be reported elseware and are
	// not part of this assertion.
	if(schemaDef == null)
	  return true;
	
	String importedSchemaTargetNS = schemaDef.getTargetNamespace();
	String specifiedTargetNS = schema.getNamespace().toString();
	if(specifiedTargetNS != null && !specifiedTargetNS.equals(importedSchemaTargetNS))
	{
	  errorReporter.reportError(new ErrorLocatorImpl(), "Schema-0052", new Object[]{specifiedTargetNS}, ErrorReporter.SEVERITY_ERROR);
	  return false;
	}
	return true;
  }
  
  /**
   * Test assertion Schema-0019. Inlined XML Schemas must define
   * a target namespace.
   * 
   * @param schema The inline schema to check.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionSchema0019(InlinedSchema schema, ErrorReporter errorReporter) throws WSDLException
  {
	URI targetNS = schema.getNamespace();
	if(targetNS == null || targetNS.toString().equals(""))
	{
	  errorReporter.reportError(new ErrorLocatorImpl(), "Schema-0019", new Object[]{}, ErrorReporter.SEVERITY_ERROR);
	  return false;
	}
	return true;
  }
  
  /**
   * Test assertion Schema-0018. Inlined XML Schemas must not define
   * an element that has already been defined by another inline schema
   * with the same target namespace.
   * 
   * @param schema An array containing all the inline schemas in the order in which they are defined.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionSchema0018(InlinedSchema[] schema, ErrorReporter errorReporter) throws WSDLException
  {
	boolean isValid = true;
	int numInlineSchemas = schema.length;
	Hashtable schemas = new Hashtable();
	for(int i = 0; i < numInlineSchemas; i++)
	{
	  InlinedSchema iSchema = schema[i];
	  URI iSchemaNs = iSchema.getNamespace();
	  // If the namespace isn't defined this assertion doesn't apply.
	  if(iSchemaNs == null)
		continue;
	  String ns = iSchemaNs.toString();
	  
	  if(schemas.containsKey(ns))
	  {
		List schemaList = (List)schemas.get(ns);
		XmlSchemaObjectTable elements = iSchema.getSchemaDefinition().getElements();
		Iterator elementNames = elements.getNames();
		while(elementNames.hasNext())
		{
		  QName elementName = (QName)elementNames.next();
		  Iterator otherInlineSchemas = schemaList.iterator();
		  while(otherInlineSchemas.hasNext())
		  {
			if(((InlinedSchema)otherInlineSchemas.next()).getSchemaDefinition().getElementByName(elementName) != null)
			{
			  // Duplicate element defined.
			  errorReporter.reportError(new ErrorLocatorImpl(), "Schema-0018", new Object[]{elementName, ns}, ErrorReporter.SEVERITY_ERROR);
			  isValid = false;
			}
		  }
		
		}
		
		XmlSchemaObjectTable types = iSchema.getSchemaDefinition().getSchemaTypes();
		Iterator typeNames = types.getNames();
		while(typeNames.hasNext())
		{
		  QName typeName = (QName)typeNames.next();
		  Iterator otherInlineSchemas = schemaList.iterator();
		  while(otherInlineSchemas.hasNext())
		  {
		    if(((InlinedSchema)otherInlineSchemas.next()).getSchemaDefinition().getTypeByName(typeName) != null)
		    {
			  // Duplicate type defined.
			  errorReporter.reportError(new ErrorLocatorImpl(), "Schema-0018b", new Object[]{typeName, ns}, ErrorReporter.SEVERITY_ERROR);
			  isValid = false;
		    }
		  }
			
		}
		  //Check if another element has been defined.
		  //check if another type has been defined.
		  //add to the existing list of schemas
		schemaList.add(iSchema);
	  }
	  else
	  {
		List schemaList = new ArrayList();
		schemaList.add(iSchema);
		schemas.put(ns, schemaList);
	  }
		 
	}
	return isValid;
  }
  
  /**
   * Test assertion Interface-0031. All style defaults specified on an interface
   * element must be absolute.
   * 
   * @param interfaceElem The interface element to check the style default list.
   * @param errorReporter The error reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionInterface0031(InterfaceElement interfaceElem, ErrorReporter errorReporter) throws WSDLException
  {
	boolean isValid = true;
	
	URI[] styleDefaults = interfaceElem.getStyleDefault();
	int numStyleDefaults = styleDefaults.length;
	for(int i = 0; i < numStyleDefaults; i++)
	{
	  if(!styleDefaults[i].isAbsolute())
	  {
	    errorReporter.reportError(new ErrorLocatorImpl(), "Interface-0031", new Object[]{styleDefaults[i].toString()}, ErrorReporter.SEVERITY_ERROR);
	    isValid = false;
	  }
	}
	return isValid;
  }
  
  /**
   * Test assertion Schema-0020. An interface message reference element must
   * not refer to an xs:simpleType or xs:complexType.
   * 
   * @param descElement The description element of the document.
   * @param messageReference The message reference to check.
   * @param errorReporter The error Reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionSchema0020(DescriptionElement descElement, InterfaceMessageReferenceElement messageReference, ErrorReporter errorReporter) throws WSDLException
  {
	XmlSchemaElement element = messageReference.getElement();
	String contentModel = messageReference.getMessageContentModel();
    if(element == null && (contentModel == null || !contentModel.equals(Constants.NMTOKEN_NONE)))
    {
      QName elementName = messageReference.getElementName();
      if(descElement.toComponent().getTypeDefinition(elementName) != null)
      {
    	errorReporter.reportError(new ErrorLocatorImpl(), "Schema-0020", new Object[]{messageReference.getMessageLabel(), elementName}, ErrorReporter.SEVERITY_ERROR);
  	    return false;
      }
    }
    return true;
  }
  
  /**
   * Test assertion Schema-0020. An interface fault element must
   * not refer to an xs:simpleType or xs:complexType.
   * 
   * @param descElement The description element of the document.
   * @param faultElement The fault element to check.
   * @param errorReporter The error Reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionSchema0020b(DescriptionElement descElement, InterfaceFaultElement faultElement, ErrorReporter errorReporter) throws WSDLException
  {
    if(faultElement.getElement() == null)
    {
      QName elementName = faultElement.getElementName();
      if(descElement.toComponent().getTypeDefinition(elementName) != null)
      {
    	errorReporter.reportError(new ErrorLocatorImpl(), "Schema-0020b", new Object[]{faultElement.getName(), elementName}, ErrorReporter.SEVERITY_ERROR);
  	    return false;
      }
    }
    return true;
  }
  
  /**
   * Test assertion Schema-0016. References to XML schema components must only refer
   * to elements and types in namespaces that have been imported or inlined or that
   * are part of the XML schema namespace.
   * 
   * @param descElement The description element of the document.
   * @param namespace Check this namespace to see if it has been defined.
   * @param errorReporter The error Reporter.
   * @return True if the assertion passes, false otherwise.
   * @throws WSDLException
   */
  protected boolean testAssertionSchema0016(DescriptionElement descElement, String namespace, ErrorReporter errorReporter) throws WSDLException
  {
	// If the namespace is null it can't be checked.
    if(namespace != null && !namespace.equals(Constants.TYPE_XSD_2001))
    {
      TypesElement types = descElement.getTypesElement();
      if(types == null)
      {
    	errorReporter.reportError(new ErrorLocatorImpl(), "Schema-0016", new Object[]{namespace}, ErrorReporter.SEVERITY_ERROR);
        return false;
      }
      Schema[] schemas = types.getSchemas();
      int numSchemas = schemas.length;
      boolean schemaNotFound = true;
      // TODO: This linear search should be improved for performance.
      for(int i = 0; i < numSchemas; i++)
      {
    	URI schemaNs = schemas[i].getNamespace();
    	// If the schema namespace is null continue to the next one. This is not the
    	// schema we're looking for.
    	if(schemaNs == null)
    	  continue;
    	if(schemaNs.toString().equals(namespace))
    	{
          schemaNotFound = false;
    	  break;
    	}
      }
      if(schemaNotFound)
      {
        errorReporter.reportError(new ErrorLocatorImpl(), "Schema-0016", new Object[]{namespace}, ErrorReporter.SEVERITY_ERROR);
      	return false;
      } 
    }
    return true;
  }

  // # may also be needed for binding.
  //Schema-0016 = A component in the XML Schema namespace '{0}' has been referenced but this namespace is not available. In order to reference components from a XML Schema namespace the namespace must be imported or defined inline.
//	Schema-0016.ref = 3.1
//	Schema-0016.assertion = A WSDL 2.0 document MUST NOT refer to XML Schema components in a given namespace unless an xs:import or xs:schema element information item for that namespace is present or the namespace is the XML Schema namespace which contains built-in types as defined in XML Schema Part 2: Datatypes Second Edition [XML Schema: Datatypes].


//	Schema-0021 = The alternative schema language makes use of the XML Schema namespace.
//	Schema-0021.ref = 3.2
//	Schema-0021.assertion = A specification of extension syntax for an alternative schema language MUST use a namespace that is different than the namespace of XML Schema.
//
//	Schema-0022 = The binding '{0}' specified is not consistent with the interface '{1}' specified. The binding must refer to the same interface as is specified by the interace attribute.
//	Schema-0022.ref = 3.3.3
//	Schema-0022.assertion = If wsdlx:interface and wsdlx:binding are used together then they MUST satisfy the same consistency rules that apply to the {interface} property of a Service component and the {binding} property of a nested Endpoint component, that is either the binding refers the interface of the service or the binding refers to no interface.
//
//	Schema-0053 = The namespace '{0}' specified is not an absolute IRI.
//	Schema-0053.assertion = The namespace used for an alternate schema language MUST be an absolute IRI.
//
//	Types-0023 = The alternate schema language does not include a declaration of an element information item to appear as a child of the types element.
//	Types-0023.ref = 3.2
//	Types-0023.assertion = A specification of extension syntax for an alternative schema language MUST include the declaration of an element information item, intended to appear as a child of the wsdl:types element information item, which references, names, and locates the schema instance (an "import" element information item).

//Import-0001 = The component '{0}' is in the namespace '{1}', which has not been imported. A namespace must be imported before components from it can be referenced in this document.
//Import-0001.assertion =  However, any WSDL 2.0 document that contains component definitions that refer by QName to WSDL 2.0 components that belong to a different namespace MUST contain a wsdl:import element information item  for that namespace (see 4.2 Importing Descriptions).
//
//Import-0003 = The imported document located at '{0}' has the same namespace as this document. An imported document's target namespace must be different than the target namespace of the document that imports it. If the target namespaces are the same a WSDL include should be used instead of a WSDL import.
//Import-0003.assertion = Imported components have different target namespace values from the WSDL 2.0 document that is importing them.
//

//MessageLabel-0006 = 
//MessageLabel-0006.assertion =  The messageLabel attribute information item  of a binding message reference element information item  MUST be present if the message exchange pattern has more than one placeholder message with {direction} equal to the message direction.
//
//MessageLabel-0007 = 
//MessageLabel-0007.assertion =  The messageLabel attribute information item  of a binding fault reference element information item  MUST be present if the message exchange pattern has more than one placeholder message with {direction} equal to the message direction.
//
//MessageLabel-0010 = 
//MessageLabel-0010.assertion =  If the messageLabel attribute information item  of a binding message reference element information item  is present then its actual value MUST match the {message label} of some placeholder message with {direction} equal to the message direction.
//
//MessageLabel-0011 = 
//MessageLabel-0011.assertion =  If the messageLabel attribute information item  of a binding fault reference element information item  is present then its actual value MUST match the {message label} of some placeholder message with {direction} equal to the message direction.
//
//MessageLabel-0014 = 
//MessageLabel-0014.assertion =  If the messageLabel attribute information item  of a binding message reference element information item  is absent then there MUST be a unique placeholder message with {direction} equal to the message direction.
//
//MessageLabel-0015 = 
//MessageLabel-0015.assertion =  If the messageLabel attribute information item  of a binding fault reference element information item  is absent then there MUST be a unique placeholder message with {direction} equal to the message direction.

  
}

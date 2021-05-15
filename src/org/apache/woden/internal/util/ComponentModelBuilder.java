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
package org.apache.woden.internal.util;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.woden.WSDLException;
import org.apache.woden.internal.wsdl20.BindingFaultImpl;
import org.apache.woden.internal.wsdl20.BindingFaultReferenceImpl;
import org.apache.woden.internal.wsdl20.BindingImpl;
import org.apache.woden.internal.wsdl20.BindingMessageReferenceImpl;
import org.apache.woden.internal.wsdl20.BindingOperationImpl;
import org.apache.woden.internal.wsdl20.Constants;
import org.apache.woden.internal.wsdl20.DescriptionImpl;
import org.apache.woden.internal.wsdl20.ElementDeclarationImpl;
import org.apache.woden.internal.wsdl20.EndpointImpl;
import org.apache.woden.internal.wsdl20.InterfaceImpl;
import org.apache.woden.internal.wsdl20.InterfaceOperationImpl;
import org.apache.woden.internal.wsdl20.ServiceImpl;
import org.apache.woden.internal.wsdl20.TypeDefinitionImpl;
import org.apache.woden.internal.wsdl20.TypesImpl;
import org.apache.woden.internal.wsdl20.extensions.ComponentExtensionsImpl;
import org.apache.woden.internal.wsdl20.extensions.rpc.RPCConstants;
import org.apache.woden.wsdl20.Binding;
import org.apache.woden.wsdl20.BindingFault;
import org.apache.woden.wsdl20.BindingFaultReference;
import org.apache.woden.wsdl20.BindingMessageReference;
import org.apache.woden.wsdl20.BindingOperation;
import org.apache.woden.wsdl20.Endpoint;
import org.apache.woden.wsdl20.InterfaceOperation;
import org.apache.woden.wsdl20.extensions.ComponentExtensions;
import org.apache.woden.wsdl20.extensions.ExtensionRegistry;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingExtensions;
import org.apache.woden.wsdl20.xml.BindingElement;
import org.apache.woden.wsdl20.xml.BindingFaultElement;
import org.apache.woden.wsdl20.xml.BindingFaultReferenceElement;
import org.apache.woden.wsdl20.xml.BindingMessageReferenceElement;
import org.apache.woden.wsdl20.xml.BindingOperationElement;
import org.apache.woden.wsdl20.xml.DescriptionElement;
import org.apache.woden.wsdl20.xml.EndpointElement;
import org.apache.woden.wsdl20.xml.ImportElement;
import org.apache.woden.wsdl20.xml.IncludeElement;
import org.apache.woden.wsdl20.xml.InterfaceElement;
import org.apache.woden.wsdl20.xml.InterfaceOperationElement;
import org.apache.woden.wsdl20.xml.ServiceElement;
import org.apache.woden.wsdl20.xml.TypesElement;
import org.apache.woden.wsdl20.xml.WSDLElement;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaImport;
import org.apache.ws.commons.schema.XmlSchemaInclude;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaObjectTable;

/**
 * Converts the xml representation of a WSDL document to the WSDL component
 * model representation defined by the W3C WSDL 2.0 spec. The xml model is
 * contained within a DescriptionElement object. The component model is
 * contained within a Description object.
 * 
 * TODO consider moving this logic inside DescriptionImpl, maybe as an inner
 * class.
 * 
 * @author John Kaputin (jkaputin@apache.org)
 * @author Arthur Ryman (ryman@ca.ibm.com, arthur.ryman@gmail.com) - added
 *         Interface Operation extensions, - added Endpoint extensions
 */
public class ComponentModelBuilder {

	// TODO support for other (non-Schema) type systems

	private DescriptionImpl fDesc;

	// TODO private ErrorReporter fErrorRpt; see todo in
	// buildElementDeclarations()
	private List fSchemasDone = new Vector();

	private List fInterfacesDone = new Vector();

	private List fBindingsDone = new Vector();

	private List fServicesDone = new Vector();
    
    private URI fBindingType = null;
    
    private boolean fIsSoapUnderlyingProtocolHttp = false;
    
	public ComponentModelBuilder(DescriptionImpl desc) {
		fDesc = desc;
		// TODO fErrorRpt = errorRpt; see todo in buildElementDeclarations()
		initComponents(fDesc);
	}

	private void initComponents(DescriptionImpl desc) {

		buildElementsAndTypes(desc);
		buildInterfaces(desc);
		buildBindings(desc);
		buildServices(desc);

		IncludeElement[] includes = desc.getIncludeElements();
		for (int i = 0; i < includes.length; i++) {
			DescriptionElement inclDesc = includes[i].getDescriptionElement();
			if (inclDesc != null) {
				initComponents((DescriptionImpl) inclDesc);
			}
		}

		ImportElement[] imports = desc.getImportElements();
		for (int i = 0; i < imports.length; i++) {
			DescriptionElement impDesc = imports[i].getDescriptionElement();
			if (impDesc != null) {
				initComponents((DescriptionImpl) impDesc);
			}
		}
	}

	/***************************************************************************
	 * TYPES
	 **************************************************************************/

	/*
	 * Extract from the collections of in-scope schemas the element declarations
	 * and type definitions.
	 */
	private void buildElementsAndTypes(DescriptionImpl desc) {
		TypesElement types = desc.getTypesElement();

		URI typeSystemURI = URI.create(Constants.TYPE_XSD_2001); //TODO support other type systems?

		if (types != null) {
			List referenceableSchemaDefs = ((TypesImpl) types)
					.getReferenceableSchemaDefs();
			Iterator i = referenceableSchemaDefs.iterator();
			while (i.hasNext()) {
				XmlSchema schemaDef = (XmlSchema) i.next();
                buildElementsAndTypes(schemaDef, schemaDef.getTargetNamespace(), typeSystemURI);
			}
		}
	}
    
    private void buildElementsAndTypes(XmlSchema schemaDef, String schemaTns, URI typeSystemURI) {
        
        if(!fSchemasDone.contains(schemaDef)) {
            
            //TODO recurse imported schemas
            
            //recurse included schemas
            XmlSchemaObjectCollection includeColl = schemaDef.getIncludes();
            Iterator includes = includeColl.getIterator();
            while(includes.hasNext()) {
                Object o = includes.next();
                if(o instanceof XmlSchemaImport) continue;  //TODO seems to be a bug in XmlSchema...includes contains an XmlSchemaImport object?
                XmlSchemaInclude include = (XmlSchemaInclude)o;
                XmlSchema schema = include.getSchema();
                if(schemaTns.equals(schema.getTargetNamespace()) || 
                   "DEFAULT".equals(schema.getTargetNamespace()) ) //this is how XmlSchema stores a null tns
                {
                    buildElementsAndTypes(schema, schemaTns, typeSystemURI);
                }
            }
            
            //parse elements and types declared directly in this schema
            buildElementDeclarations(schemaDef, schemaTns, typeSystemURI);
            buildTypeDefinitions(schemaDef, schemaTns, typeSystemURI);
            fSchemasDone.add(schemaDef);
        }
    }

	/*
	 * Extract the element declarations from the given schema.
	 */
	private void buildElementDeclarations(XmlSchema schemaDef, String schemaTns, URI typeSystemURI) {
        
	    XmlSchemaObjectTable elementTable = schemaDef.getElements();
	    Iterator qnames = elementTable.getNames();
	    while (qnames.hasNext()) {
	        QName xseQN = (QName) qnames.next();
            QName edQN = xseQN;
            if(xseQN.getNamespaceURI().equals("DEFAULT")) {
                //this is how XmlSchema represents tns for chameleon xs:includes,
                //so replace it with the including schema's tns.
                edQN = new QName(schemaTns, xseQN.getLocalPart(), xseQN.getPrefix());
            }
	        if(edQN.getNamespaceURI().equals(schemaTns)) //TODO test with schema imports, may be incorrect.
            {
	            ElementDeclarationImpl ed = new ElementDeclarationImpl();
	            ed.setName(edQN);
	            ed.setSystem(typeSystemURI);
	            ed.setContentModel(Constants.API_APACHE_WS_XS);
	            ed.setContent(elementTable.getItem(xseQN));
	            fDesc.addElementDeclaration(ed);
	        }
	    }
	    
	}

	/*
	 * Extract the type definitions from the given schema.
	 */
	private void buildTypeDefinitions(XmlSchema schemaDef, String schemaTns, URI typeSystemURI) {
        
	    XmlSchemaObjectTable typeTable = schemaDef.getSchemaTypes();
	    Iterator qnames = typeTable.getNames();
	    while (qnames.hasNext()) {
	        QName xsdQN = (QName) qnames.next();
            QName tdQN = xsdQN;
            if(xsdQN.getNamespaceURI().equals("DEFAULT")) {
                //this is how XmlSchema represents tns for chameleon xs:includes,
                //so replace it with the including schema's tns.
                tdQN = new QName(schemaTns, xsdQN.getLocalPart(), xsdQN.getPrefix());
            }
	        if (tdQN.getNamespaceURI().equals(schemaTns)) {
	            TypeDefinitionImpl td = new TypeDefinitionImpl();
	            td.setName(tdQN);
	            td.setSystem(typeSystemURI);
	            td.setContentModel(Constants.API_APACHE_WS_XS);
	            td.setContent(typeTable.getItem(xsdQN));
	            fDesc.addTypeDefinition(td);
	        }
	    }
	}

	/***************************************************************************
	 * INTERFACE
	 **************************************************************************/

	/*
	 * Initialize the Interface component and its child components from the
	 * InterfaceElement and its child elements.
	 */
	private void buildInterfaces(DescriptionImpl desc) {
		InterfaceElement[] interfaceEls = desc.getInterfaceElements();
		for (int i = 0; i < interfaceEls.length; i++) {
			InterfaceImpl interfaceImpl = (InterfaceImpl) interfaceEls[i];
			if (!fInterfacesDone.contains(interfaceImpl)) {
				buildInterfaceOperations(interfaceImpl);
				fInterfacesDone.add(interfaceImpl);
			}
		}
	}

	private void buildInterfaceOperations(InterfaceImpl interfaceImpl) {
		InterfaceOperationElement[] operations = interfaceImpl
				.getInterfaceOperationElements();
		for (int i = 0; i < operations.length; i++) {
			InterfaceOperationImpl oper = (InterfaceOperationImpl) operations[i];
			buildInterfaceOperationExtensions(oper);
		}
	}

	private void buildInterfaceOperationExtensions(InterfaceOperationImpl oper) {
        
        /*
         * Create a ComponentExtensions object for each registered extension
         * namespace used within this operation by extension elements or attributes.
         */
		ExtensionRegistry er = fDesc.getExtensionRegistry();
		URI[] extNamespaces = er
				.queryComponentExtensionNamespaces(InterfaceOperation.class);

		for (int i = 0; i < extNamespaces.length; i++) {
			URI extNS = extNamespaces[i];
			if (oper.hasExtensionAttributesForNamespace(extNS)) {
				ComponentExtensions compExt = createComponentExtensions(
						InterfaceOperation.class, oper, extNS);
				oper.setComponentExtensions(extNS, compExt);
			}
		}
        
        /*
         * {safety} is a REQUIRED extension property on interface operation
         * so if an InterfaceOperationExtensions object has not already been
         * created, create one now.
         */
        if (oper.getComponentExtensionsForNamespace(
                ComponentExtensions.URI_NS_EXTENSIONS) == null) {
            ComponentExtensions compExt = createComponentExtensions(
                    InterfaceOperation.class, oper,
                    ComponentExtensions.URI_NS_EXTENSIONS);
            oper.setComponentExtensions(
                    ComponentExtensions.URI_NS_EXTENSIONS, compExt);
        }
        
        /*
         * If interface operation style includes RPC then if an
         * RPCInterfaceOperationExtensions object has not already been
         * created, create one now.
         */
        boolean isRPCStyle = false;
        URI[] style = oper.getStyle();
        for(int i=0; i<style.length; i++)
        {
            URI temp = style[i];
            if(RPCConstants.URI_STYLE_RPC.equals(temp)) {
                isRPCStyle = true;
                break;
            }
        }
        
        if(isRPCStyle) {
            if (oper.getComponentExtensionsForNamespace(
                    ComponentExtensions.URI_NS_RPC) == null) {
                ComponentExtensions compExt = createComponentExtensions(
                        InterfaceOperation.class, oper,
                        ComponentExtensions.URI_NS_RPC);
                oper.setComponentExtensions(
                        ComponentExtensions.URI_NS_RPC, compExt);
            }
        }
	}

	/***************************************************************************
	 * BINDING
	 **************************************************************************/

	/*
	 * Initialize the Binding component and its child components from the
	 * BindingElement and its child elements.
	 */
	private void buildBindings(DescriptionImpl desc) {
		BindingElement[] bindingEls = desc.getBindingElements();
		for (int i = 0; i < bindingEls.length; i++) {
			BindingImpl bindImpl = (BindingImpl) bindingEls[i];
			if (!fBindingsDone.contains(bindImpl)) {
                buildBindingExtensions(bindImpl);
				buildBindingFaults(bindImpl);
				buildBindingOperations(bindImpl);
				fBindingsDone.add(bindImpl);
			}
		}
	}

	private void buildBindingFaults(BindingImpl binding) {
		BindingFaultElement[] bindFaults = binding.getBindingFaultElements();
		for (int i = 0; i < bindFaults.length; i++) {
			BindingFaultImpl bindFault = (BindingFaultImpl) bindFaults[i];
			buildBindingFaultExtensions(bindFault);
		}
	}

	private void buildBindingOperations(BindingImpl binding) {
		BindingOperationElement[] operations = binding
				.getBindingOperationElements();
		for (int i = 0; i < operations.length; i++) {
			BindingOperationImpl oper = (BindingOperationImpl) operations[i];
			buildBindingFaultReferences(oper);
			buildBindingMessageReferences(oper);
			buildBindingOperationExtensions(oper);
		}
	}

	private void buildBindingFaultReferences(BindingOperationImpl oper) {
		BindingFaultReferenceElement[] faultRefs = oper
				.getBindingFaultReferenceElements();
		for (int i = 0; i < faultRefs.length; i++) {
			BindingFaultReferenceImpl faultRef = (BindingFaultReferenceImpl) faultRefs[i];

			buildBindingFaultReferenceExtensions(faultRef);

		}
	}

	private void buildBindingMessageReferences(BindingOperationImpl oper) {
		BindingMessageReferenceElement[] messages = oper
				.getBindingMessageReferenceElements();
		for (int i = 0; i < messages.length; i++) {
			BindingMessageReferenceImpl message = (BindingMessageReferenceImpl) messages[i];

			buildBindingMessageReferenceExtensions(message);
		}
	}

	private void buildBindingExtensions(BindingImpl binding) {
        
        /*
         * Create a ComponentExtensions object for each registered extension
         * namespace used within this binding by extension elements or attributes.
         */
		ExtensionRegistry er = fDesc.getExtensionRegistry();
		URI[] extNamespaces = er
				.queryComponentExtensionNamespaces(Binding.class);

		for (int i = 0; i < extNamespaces.length; i++) {
			URI extNS = extNamespaces[i];
			if (binding.hasExtensionAttributesForNamespace(extNS)
					|| binding.hasExtensionElementsForNamespace(extNS)) {
				ComponentExtensions compExt = createComponentExtensions(
						Binding.class, binding, extNS);
				binding.setComponentExtensions(extNS, compExt);
			}
		}

		/*
		 * Second, apply the rules from WSDL 2.0 Part 2 Adjuncts spec for
		 * default values for SOAP and HTTP extension properties to create
		 * ComponentExtension objects even if the related extension attributes
		 * or elements do not exist in the WSDL.
		 * 
		 * TODO chg this hardcoded behaviour so that any default rules for SOAP,
		 * HTTP or user-defined extensions can be registered in some way and
		 * interpreted here at run time.
		 */
		if (ComponentExtensions.URI_NS_SOAP.equals(binding.getType())) {
            
            fBindingType = ComponentExtensions.URI_NS_SOAP;
            
			/*
			 * If the binding type is SOAP and a SOAPBindingExtensions object has
             * not already been created, create one now.
			 */
			if (binding
					.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP) == null) {
				ComponentExtensions compExt = createComponentExtensions(
						Binding.class, binding, ComponentExtensions.URI_NS_SOAP);
				binding.setComponentExtensions(ComponentExtensions.URI_NS_SOAP,
						compExt);
			}

			SOAPBindingExtensions sbe = (SOAPBindingExtensions) binding
					.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);

			if (ComponentExtensions.URI_NS_HTTP.equals(sbe
					.getSoapUnderlyingProtocol())) {
                
                fIsSoapUnderlyingProtocolHttp = true;
                
				/*
				 * If the binding type is SOAP and the {soap underlying protocol}
                 * property is HTTP, then if an HTTPBindingExtensions object has
				 * not already been created, create one now.
				 */
				if (binding
						.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP) == null) {
					ComponentExtensions compExt = createComponentExtensions(
							Binding.class, binding,
							ComponentExtensions.URI_NS_HTTP);
					binding.setComponentExtensions(
							ComponentExtensions.URI_NS_HTTP, compExt);
				}
			}
		}

		if (ComponentExtensions.URI_NS_HTTP.equals(binding.getType())) {
            
            fBindingType = ComponentExtensions.URI_NS_HTTP;
            
			/*
			 * If the binding type is HTTP, then if an HTTPBindingExtensions 
             * object has not already been created, create one now.
			 */
			if (binding
					.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP) == null) {
				ComponentExtensions compExt = createComponentExtensions(
						Binding.class, binding, ComponentExtensions.URI_NS_HTTP);
				binding.setComponentExtensions(ComponentExtensions.URI_NS_HTTP,
						compExt);
			}
		}

	}

	private void buildBindingFaultExtensions(BindingFaultImpl bindFault) {
        
        /*
         * Create a ComponentExtensions object for each registered extension
         * namespace used within this binding fault by extension elements 
         * or attributes.
         */
		ExtensionRegistry er = fDesc.getExtensionRegistry();
		URI[] extNamespaces = er
				.queryComponentExtensionNamespaces(BindingFault.class);

		for (int i = 0; i < extNamespaces.length; i++) {
			URI extNS = extNamespaces[i];
			if (bindFault.hasExtensionAttributesForNamespace(extNS)
					|| bindFault.hasExtensionElementsForNamespace(extNS)) {
				ComponentExtensions compExt = createComponentExtensions(
						BindingFault.class, bindFault, extNS);
				bindFault.setComponentExtensions(extNS, compExt);
			}
		}

		/*
		 * Second, apply the rules from WSDL 2.0 Part 2 Adjuncts spec for
		 * default values for SOAP and HTTP extension properties to create
		 * ComponentExtension objects even if the related extension attributes
		 * or elements do not exist in the WSDL.
		 * 
		 * TODO chg this hardcoded behaviour so that any default rules for SOAP,
		 * HTTP or user-defined extensions can be registered in some way and
		 * interpreted here at run time.
		 */
		if (ComponentExtensions.URI_NS_SOAP.equals(fBindingType)) {
			/*
			 * If the binding type is SOAP and a SOAPBindingFaultExtensions object
             * has not already been created, create one now.
			 */
			if (bindFault
					.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP) == null) {
				ComponentExtensions compExt = createComponentExtensions(
						BindingFault.class, bindFault,
						ComponentExtensions.URI_NS_SOAP);
				bindFault.setComponentExtensions(
						ComponentExtensions.URI_NS_SOAP, compExt);
			}
            
            if (fIsSoapUnderlyingProtocolHttp) {
                /*
                 * If the binding type is SOAP and the {soap underlying protocol}
                 * property is HTTP, then if an HTTPBindingFaultExtensions object
                 * has not already been created, create one now.
                 */
                if (bindFault
                        .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP) == null) {
                    ComponentExtensions compExt = createComponentExtensions(
                            BindingFault.class, bindFault,
                            ComponentExtensions.URI_NS_HTTP);
                    bindFault.setComponentExtensions(
                            ComponentExtensions.URI_NS_HTTP, compExt);
                }
            }
		}

		if (ComponentExtensions.URI_NS_HTTP.equals(fBindingType)) {
			/*
			 * If the binding type is HTTP and an HTTPBindingFaultExtensions object 
             * has not already been created, create one now.
			 */
			if (bindFault
					.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP) == null) {
				ComponentExtensions compExt = createComponentExtensions(
						BindingFault.class, bindFault,
						ComponentExtensions.URI_NS_HTTP);
				bindFault.setComponentExtensions(
						ComponentExtensions.URI_NS_HTTP, compExt);
			}
		}

	}

	private void buildBindingOperationExtensions(BindingOperationImpl bindOper) {
        
        /*
         * Create a ComponentExtensions object for each registered extension
         * namespace used within this binding operation by extension elements 
         * or attributes.
         */
		ExtensionRegistry er = fDesc.getExtensionRegistry();
		URI[] extNamespaces = er
				.queryComponentExtensionNamespaces(BindingOperation.class);

		for (int i = 0; i < extNamespaces.length; i++) {
			URI extNS = extNamespaces[i];
			if (bindOper.hasExtensionAttributesForNamespace(extNS)
					|| bindOper.hasExtensionElementsForNamespace(extNS)) {
				ComponentExtensions compExt = createComponentExtensions(
						BindingOperation.class, bindOper, extNS);
				bindOper.setComponentExtensions(extNS, compExt);
			}
		}

		/*
		 * Second, apply the rules from WSDL 2.0 Part 2 Adjuncts spec for
		 * default values for SOAP and HTTP extension properties to create
		 * ComponentExtension objects even if the related extension attributes
		 * or elements do not exist in the WSDL.
		 * 
		 * TODO chg this hardcoded behaviour so that any default rules for SOAP,
		 * HTTP or user-defined extensions can be registered in some way and
		 * interpreted here at run time.
		 */
        if (ComponentExtensions.URI_NS_SOAP.equals(fBindingType)) {
            /*
             * If the binding type is SOAP and a SOAPBindingOperationExtensions object
             * has not already been created, create one now.
             */
            if (bindOper
                    .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP) == null) {
                ComponentExtensions compExt = createComponentExtensions(
                        BindingOperation.class, bindOper,
                        ComponentExtensions.URI_NS_SOAP);
                bindOper.setComponentExtensions(
                        ComponentExtensions.URI_NS_SOAP, compExt);
            }
            
            if (fIsSoapUnderlyingProtocolHttp) {
                /*
                 * If the binding type is SOAP and the {soap underlying protocol}
                 * property is HTTP, then if an HTTPBindingOperationExtensions object
                 * has not already been created, create one now.
                 */
                if (bindOper
                        .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP) == null) {
                    ComponentExtensions compExt = createComponentExtensions(
                            BindingOperation.class, bindOper,
                            ComponentExtensions.URI_NS_HTTP);
                    bindOper.setComponentExtensions(
                            ComponentExtensions.URI_NS_HTTP, compExt);
                }
            }
        }
        
		if (ComponentExtensions.URI_NS_HTTP.equals(fBindingType)) {
			/*
			 * If the binding type is HTTP and an HTTPBindingOperationExtensions
             * object has not already been created, create one now.
			 */
			if (bindOper
					.getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP) == null) {
				ComponentExtensions compExt = createComponentExtensions(
						BindingOperation.class, bindOper,
						ComponentExtensions.URI_NS_HTTP);
				bindOper.setComponentExtensions(
						ComponentExtensions.URI_NS_HTTP, compExt);
			}
		}
	}

	private void buildBindingMessageReferenceExtensions(
			BindingMessageReferenceImpl bindMsgRef) {
        
        /*
         * Create a ComponentExtensions object for each registered extension
         * namespace used within this binding message reference by extension 
         * elements or attributes.
         */
		ExtensionRegistry er = fDesc.getExtensionRegistry();
		URI[] extNamespaces = er
				.queryComponentExtensionNamespaces(BindingMessageReference.class);

		for (int i = 0; i < extNamespaces.length; i++) {
			URI extNS = extNamespaces[i];
			if (bindMsgRef.hasExtensionAttributesForNamespace(extNS)
					|| bindMsgRef.hasExtensionElementsForNamespace(extNS)) {
				ComponentExtensions compExt = createComponentExtensions(
						BindingMessageReference.class, bindMsgRef, extNS);
				bindMsgRef.setComponentExtensions(extNS, compExt);
			}
		}
        
        if (ComponentExtensions.URI_NS_SOAP.equals(fBindingType)) {
            /*
             * If the binding type is SOAP and a SOAPBindingMessageReferenceExtensions 
             * object has not already been created, create one now.
             */
            if (bindMsgRef
                    .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP) == null) {
                ComponentExtensions compExt = createComponentExtensions(
                        BindingMessageReference.class, bindMsgRef,
                        ComponentExtensions.URI_NS_SOAP);
                bindMsgRef.setComponentExtensions(
                        ComponentExtensions.URI_NS_SOAP, compExt);
            }
            
            if (fIsSoapUnderlyingProtocolHttp) {
                /*
                 * If the binding type is SOAP and the {soap underlying protocol}
                 * property is HTTP, then if an HTTPBindingMessageReferenceExtensions 
                 * object has not already been created, create one now.
                 */
                if (bindMsgRef
                        .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP) == null) {
                    ComponentExtensions compExt = createComponentExtensions(
                            BindingMessageReference.class, bindMsgRef,
                            ComponentExtensions.URI_NS_HTTP);
                    bindMsgRef.setComponentExtensions(
                            ComponentExtensions.URI_NS_HTTP, compExt);
                }
            }
        }
        
        if (ComponentExtensions.URI_NS_HTTP.equals(fBindingType)) {
            /*
             * If the binding type is HTTP and an HTTPBindingMessageReferenceExtensions
             * object has not already been created, create one now.
             */
            if (bindMsgRef
                    .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP) == null) {
                ComponentExtensions compExt = createComponentExtensions(
                        BindingMessageReference.class, bindMsgRef,
                        ComponentExtensions.URI_NS_HTTP);
                bindMsgRef.setComponentExtensions(
                        ComponentExtensions.URI_NS_HTTP, compExt);
            }
        }
	}

	private void buildBindingFaultReferenceExtensions(
			BindingFaultReferenceImpl bindFaultRef) {
        
        /*
         * Create a ComponentExtensions object for each registered extension
         * namespace used within this binding message reference by extension 
         * elements or attributes.
         */
		ExtensionRegistry er = fDesc.getExtensionRegistry();
		URI[] extNamespaces = er
				.queryComponentExtensionNamespaces(BindingFaultReference.class);

		for (int i = 0; i < extNamespaces.length; i++) {
			URI extNS = extNamespaces[i];
			if (bindFaultRef.hasExtensionAttributesForNamespace(extNS)
					|| bindFaultRef.hasExtensionElementsForNamespace(extNS)) {
				ComponentExtensions compExt = createComponentExtensions(
						BindingFaultReference.class, bindFaultRef, extNS);
				bindFaultRef.setComponentExtensions(extNS, compExt);
			}
		}
        
        if (ComponentExtensions.URI_NS_SOAP.equals(fBindingType)) {
            /*
             * If the binding type is SOAP and a SOAPBindingFaultReferenceExtensions 
             * object has not already been created, create one now.
             */
            if (bindFaultRef
                    .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP) == null) {
                ComponentExtensions compExt = createComponentExtensions(
                        BindingFaultReference.class, bindFaultRef,
                        ComponentExtensions.URI_NS_SOAP);
                bindFaultRef.setComponentExtensions(
                        ComponentExtensions.URI_NS_SOAP, compExt);
            }
        }
	}

	private void buildServices(DescriptionImpl desc) {

		ServiceElement[] serviceEls = desc.getServiceElements();
		for (int i = 0; i < serviceEls.length; i++) {
			ServiceImpl serviceImpl = (ServiceImpl) serviceEls[i];
			if (!fServicesDone.contains(serviceImpl)) {
				buildEndpoints(serviceImpl);
				fServicesDone.add(serviceImpl);
			}
		}
	}

	private void buildEndpoints(ServiceImpl serviceImpl) {

		EndpointElement[] endpoints = serviceImpl.getEndpointElements();
		for (int i = 0; i < endpoints.length; i++) {
			EndpointImpl endpoint = (EndpointImpl) endpoints[i];
			buildEndpointExtensions(endpoint);
		}
	}

	private void buildEndpointExtensions(EndpointImpl endpoint) {
		
        /*
         * Create a ComponentExtensions object for each registered extension
         * namespace used within this endpoint by extension 
         * elements or attributes.
         */
		ExtensionRegistry er = fDesc.getExtensionRegistry();
		URI[] extNamespaces = er
				.queryComponentExtensionNamespaces(Endpoint.class);

		for (int i = 0; i < extNamespaces.length; i++) {
			URI extNS = extNamespaces[i];
			if (endpoint.hasExtensionAttributesForNamespace(extNS)
					|| endpoint.hasExtensionElementsForNamespace(extNS)) {
				ComponentExtensions compExt = createComponentExtensions(
						Endpoint.class, endpoint, extNS);
				endpoint.setComponentExtensions(extNS, compExt);
			}
		}
        
        if ((ComponentExtensions.URI_NS_SOAP.equals(fBindingType) && 
                fIsSoapUnderlyingProtocolHttp) 
                ||
             ComponentExtensions.URI_NS_HTTP.equals(fBindingType)) {
            
            /*
             * If the binding type is SOAP and the {soap underlying protocol}
             * property is HTTP or if the binding type is HTTP, then if an 
             * HTTPEndpointExtensions object has not already been created, 
             * create one now.
             */
            if (endpoint
                    .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP) == null) {
                ComponentExtensions compExt = createComponentExtensions(
                        Endpoint.class, endpoint,
                        ComponentExtensions.URI_NS_HTTP);
                endpoint.setComponentExtensions(
                        ComponentExtensions.URI_NS_HTTP, compExt);
            }
        }
	}

	/*
	 * This helper method factors out common code for creating
	 * ComponentExtensions registered in the ExtensionRegistry.
	 */
	private ComponentExtensions createComponentExtensions(Class parentClass,
			WSDLElement parentElem, URI extNS) {
		ExtensionRegistry er = fDesc.getExtensionRegistry();
		ComponentExtensions compExt = null;
		try {
			compExt = er.createComponentExtension(parentClass, extNS);
			((ComponentExtensionsImpl) compExt).init(parentElem, extNS);
		} catch (WSDLException e) {
			// This exception occurs if there is no Java class registered for
			// the namespace, but
			// this namespace was obtained from the extension registry so we
			// know that a Java class is
			// registered and that this exception cannot occur. Ignore the catch
			// block.
		}
		return compExt;
	}

}

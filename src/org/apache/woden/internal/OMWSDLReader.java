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
package org.apache.woden.internal;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.woden.ErrorHandler;
import org.apache.woden.ErrorReporter;
import org.apache.woden.WSDLException;
import org.apache.woden.WSDLSource;
import org.apache.woden.internal.schema.ImportedSchemaImpl;
import org.apache.woden.internal.schema.InlinedSchemaImpl;
import org.apache.woden.internal.schema.SchemaConstants;
import org.apache.woden.internal.util.StringUtils;
import org.apache.woden.internal.util.om.OMUtils;
import org.apache.woden.internal.util.om.QNameUtils;
import org.apache.woden.internal.wsdl20.Constants;
import org.apache.woden.schema.Schema;
import org.apache.woden.types.NCName;
import org.apache.woden.wsdl20.enumeration.Direction;
import org.apache.woden.wsdl20.enumeration.MessageLabel;
import org.apache.woden.wsdl20.extensions.ExtensionDeserializer;
import org.apache.woden.wsdl20.extensions.ExtensionElement;
import org.apache.woden.wsdl20.extensions.ExtensionRegistry;
import org.apache.woden.wsdl20.xml.BindingElement;
import org.apache.woden.wsdl20.xml.BindingFaultElement;
import org.apache.woden.wsdl20.xml.BindingFaultReferenceElement;
import org.apache.woden.wsdl20.xml.BindingMessageReferenceElement;
import org.apache.woden.wsdl20.xml.BindingOperationElement;
import org.apache.woden.wsdl20.xml.ConfigurableElement;
import org.apache.woden.wsdl20.xml.DescriptionElement;
import org.apache.woden.wsdl20.xml.DocumentableElement;
import org.apache.woden.wsdl20.xml.DocumentationElement;
import org.apache.woden.wsdl20.xml.EndpointElement;
import org.apache.woden.wsdl20.xml.FeatureElement;
import org.apache.woden.wsdl20.xml.ImportElement;
import org.apache.woden.wsdl20.xml.IncludeElement;
import org.apache.woden.wsdl20.xml.InterfaceElement;
import org.apache.woden.wsdl20.xml.InterfaceFaultElement;
import org.apache.woden.wsdl20.xml.InterfaceFaultReferenceElement;
import org.apache.woden.wsdl20.xml.InterfaceMessageReferenceElement;
import org.apache.woden.wsdl20.xml.InterfaceOperationElement;
import org.apache.woden.wsdl20.xml.PropertyElement;
import org.apache.woden.wsdl20.xml.ServiceElement;
import org.apache.woden.wsdl20.xml.TypesElement;
import org.apache.woden.wsdl20.xml.WSDLElement;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaException;
import org.apache.ws.commons.schema.utils.NamespaceMap;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implements WSDL reader behaviour for OM based parsing
 */
public class OMWSDLReader extends BaseWSDLReader{

    //A map of imported schema definitions keyed by schema location URI
    private Map fImportedSchemas = new Hashtable();

    public OMWSDLReader() throws WSDLException {
        super();
    }

    public DescriptionElement readWSDL(String wsdlURI) throws WSDLException {
        //This conversion to a URL is necessary to import the schema
        URL url;
        try {
            url = StringUtils.getURL(null, wsdlURI);
        }
        catch (MalformedURLException e) {
            String msg = getErrorReporter().getFormattedMessage(
                            "WSDL502", new Object[] {null, wsdlURI});
            throw new WSDLException(WSDLException.PARSER_ERROR, msg, e);
        }

        //This is needed because StAXOMBuilder expects a URI to read a WSDL
        int index = wsdlURI.indexOf(':');
        String wsdlURL = (index != -1)
                        ? wsdlURI
                        : ("file://"+wsdlURI);

        OMElement wsdlDescription = OMUtils.getElement(wsdlURL);

        return parseDescription(wsdlURL, wsdlDescription, null);
    }

    private DescriptionElement parseDescription(String documentBaseURI,
                                                OMElement omDescription,
                                                Map wsdlModules)
                                                throws WSDLException {

        checkElementName(omDescription,Constants.Q_ELEM_DESCRIPTION);

        //Get the description element that the components to be assigned to
        DescriptionElement desc = getFactory().newDescription();

        if(wsdlModules == null){
            //This is the initial WSDL document. No imports or includes yet.
            //TODO this might be the place to flag the initial Desc if necessary.
            wsdlModules = new HashMap();
        }

        if(getExtensionRegistry() != null){
            desc.setExtensionRegistry(getExtensionRegistry());
        }
        if(getErrorReporter() != null){
            (desc.getExtensionRegistry()).setErrorReporter(getErrorReporter());
        }

        desc.setDocumentBaseURI(getURI(documentBaseURI));

        //Set the target namespace
        String targetNamespace = OMUtils.getAttribute(omDescription, Constants.ATTR_TARGET_NAMESPACE);
        if(targetNamespace != null){
            desc.setTargetNamespace(getURI(targetNamespace));
        }

        //Parse namespace declarations
        Iterator namespaces = omDescription.getAllDeclaredNamespaces();
        while(namespaces.hasNext()){
            OMNamespace namespace = (OMNamespace)namespaces.next();
            String localPart = namespace.getPrefix();
            String value = namespace.getNamespaceURI();

          if (!(Constants.ATTR_XMLNS).equals(localPart)){
            desc.addNamespace(localPart, getURI(value));  //a prefixed namespace
          }
          else{
            desc.addNamespace(null, getURI(value));       //the default namespace
          }
        }

        //todo - XMLAttr have to be fixed by removing the DOM dependencies
        parseExtensionAttributes(omDescription, DescriptionElement.class, desc, desc);

        //Parse the child elements found in the WSDL Description
        Iterator wsdlComponents = omDescription.getChildElements();
        while (wsdlComponents.hasNext()){
            OMElement wsdlComponent = ((OMElement)wsdlComponents.next());
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, wsdlComponent)){
                parseDocumentation(wsdlComponent, desc, desc);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_TYPES, wsdlComponent)){
                parseTypes(wsdlComponent, desc);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_INTERFACE, wsdlComponent)){
                parseInterface(wsdlComponent, desc);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_BINDING, wsdlComponent)){
                parseBinding(wsdlComponent, desc);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_SERVICE, wsdlComponent)){
                parseService(wsdlComponent, desc);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_IMPORT, wsdlComponent)){
                if(documentBaseURI != null && !wsdlModules.containsKey(documentBaseURI)){
                    wsdlModules.put(documentBaseURI, desc);
                }
                parseImport(wsdlComponent, desc, wsdlModules);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_INCLUDE, wsdlComponent)){
                if(documentBaseURI != null && !wsdlModules.containsKey(documentBaseURI)){
                    wsdlModules.put(documentBaseURI, desc);
                }
                parseInclude(wsdlComponent, desc, wsdlModules);
            }
            else{
                desc.addExtensionElement(parseExtensionElement(DescriptionElement.class, desc, wsdlComponent, desc) );
            }
        }

        // TODO: Parse the schema for schema to include the built in schema types from the spec in the Woden model.

        return desc;
    }


   private DocumentationElement parseDocumentation(OMElement docEl,
                                                    DescriptionElement desc,
                                                    DocumentableElement parent)
                                                    throws WSDLException {

        DocumentationElement documentation = parent.addDocumentationElement();

        //Stores the documentation values as a string
        documentation.setContent(docEl.getText());

        //Now parse any extensibility attributes or elements
        parseExtensionAttributes(docEl, DocumentationElement.class, documentation, desc);

        //And then any child elements
        Iterator docElChildren = docEl.getChildElements();
        while (docElChildren.hasNext()){
            OMElement docChildElement = (OMElement)docElChildren.next();
            documentation.addExtensionElement(parseExtensionElement(
                    DocumentationElement.class, documentation, docChildElement, desc) );
        }
        return documentation;
    }


   private TypesElement parseTypes(OMElement typesEl,
                                    DescriptionElement desc)
                                    throws WSDLException {

        TypesElement types = desc.getTypesElement();

        //TODO for now set to W3 XML Schema. Later, add support for non-XML Schema type systems
        types.setTypeSystem(Constants.TYPE_XSD_2001);

        parseExtensionAttributes(typesEl, TypesElement.class, types, desc);

        //Get the first element within the types (which is xs:schema)
        //TODO validate
        OMElement typesChildElement = typesEl.getFirstElement();

        if (typesChildElement != null) {
            QName elementType = QNameUtils.newQName(typesChildElement);

            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, typesChildElement)){
                parseDocumentation(typesChildElement, desc, types);
            }
            else if (SchemaConstants.XSD_IMPORT_QNAME_LIST.contains(typesChildElement)){
                types.addSchema(parseSchemaImport(typesChildElement, desc));
            }
            else if (SchemaConstants.XSD_SCHEMA_QNAME_LIST.contains(elementType)){
                types.addSchema(parseSchemaInline(typesChildElement, desc));
            }
            else {
                types.addExtensionElement(parseExtensionElement(
                        TypesElement.class, types, typesChildElement, desc) );
            }
        }
        return types;
    }


   private ServiceElement parseService(OMElement serviceEl,
                                       DescriptionElement desc)
                                       throws WSDLException{

        ServiceElement service = desc.addServiceElement();

        String name = OMUtils.getAttribute(serviceEl, Constants.ATTR_NAME);
        if(name != null){
            service.setName(new NCName(name));
        }

        QName intfaceQN;
        String intface = OMUtils.getAttribute(serviceEl, Constants.ATTR_INTERFACE);
        if(intface != null){
            try{
                intfaceQN = OMUtils.getQName(intface, serviceEl);
                service.setInterfaceName(intfaceQN);
            }
            catch(WSDLException e){
                getErrorReporter().reportError(
                        new ErrorLocatorImpl(),
                        "WSDL505",
                        serviceEl.getLocalName(),
                        ErrorReporter.SEVERITY_ERROR);
            }
        }

        parseExtensionAttributes(serviceEl, ServiceElement.class, service, desc);

        /* Parse the child elements of <service>.*/
        Iterator serviceElChildren = serviceEl.getChildElements();
        while (serviceElChildren.hasNext()){
            OMElement serviceElChild = (OMElement)serviceElChildren.next();
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, serviceElChild)){
                parseDocumentation(serviceElChild, desc, service);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_ENDPOINT, serviceElChild)){
                parseEndpoint(serviceElChild, desc, service);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, serviceElChild)){
                parseFeature(serviceElChild, desc, service);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, serviceElChild)){
                parseProperty(serviceElChild, desc, service);
            }
            else{
                service.addExtensionElement(parseExtensionElement(ServiceElement.class, service, serviceElChild, desc) );
            }
        }
        return service;
    }


   private EndpointElement parseEndpoint(OMElement endpointEl,
                                          DescriptionElement desc,
                                          ServiceElement parent)
                                          throws WSDLException{

        EndpointElement endpoint = parent.addEndpointElement();
        endpoint.setParentElement(parent);

        String name = OMUtils.getAttribute(endpointEl, Constants.ATTR_NAME);
        if(name != null){
            NCName ncname = new NCName(name);
            endpoint.setName(ncname);
        }

        QName bindingQN;
        String binding = OMUtils.getAttribute(endpointEl, Constants.ATTR_BINDING);
        if(binding != null){
            try{
                bindingQN = OMUtils.getQName(binding, endpointEl);
                endpoint.setBindingName(bindingQN);
            }
            catch(WSDLException e){
                getErrorReporter().reportError(
                        new ErrorLocatorImpl(),
                        "WSDL505",
                        new Object[] {binding, QNameUtils.newQName(endpointEl)},
                        ErrorReporter.SEVERITY_ERROR);
            }
        }

        String address = OMUtils.getAttribute(endpointEl, Constants.ATTR_ADDRESS);
        if(address != null){
            endpoint.setAddress(getURI(address));
        }

        parseExtensionAttributes(endpointEl, EndpointElement.class, endpoint, desc);

        /* Parse the child elements of <endpoint>*/
        Iterator endpointElChildren = endpointEl.getChildElements();
        while (endpointElChildren.hasNext()){
            OMElement endpointElChild = (OMElement)endpointElChildren.next();
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, endpointElChild)){
                parseDocumentation(endpointElChild, desc, endpoint);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, endpointElChild)){
                parseFeature(endpointElChild, desc, endpoint);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, endpointElChild)){
                parseProperty(endpointElChild, desc, endpoint);
            }
            else{
                endpoint.addExtensionElement(
                    parseExtensionElement(ServiceElement.class, endpoint, endpointElChild, desc) );
            }
        }
        return endpoint;
    }


    private BindingElement parseBinding(OMElement bindEl,
                                        DescriptionElement desc)
                                        throws WSDLException{

        BindingElement binding = desc.addBindingElement();

        String name = OMUtils.getAttribute(bindEl, Constants.ATTR_NAME);
        if(name != null){
            binding.setName(new NCName(name));
        }

        QName intfaceQN = null;
        String intface = OMUtils.getAttribute(bindEl, Constants.ATTR_INTERFACE);
        if(intface != null){
            try{
                intfaceQN = OMUtils.getQName(intface, bindEl);
                binding.setInterfaceName(intfaceQN);
            }
            catch(WSDLException e){
                getErrorReporter().reportError(
                    new ErrorLocatorImpl(),
                    "WSDL505",
                    new Object[] {intface, QNameUtils.newQName(bindEl)},
                    ErrorReporter.SEVERITY_ERROR);
            }
        }

        String type = OMUtils.getAttribute(bindEl, Constants.ATTR_TYPE);
        if(type != null) {
            binding.setType(getURI(type));
        }

        parseExtensionAttributes(bindEl, BindingElement.class, binding, desc);

        /* Parse the child elements of <binding>.*/
        Iterator bindElChildren = bindEl.getChildElements();
        while (bindElChildren.hasNext()){
            OMElement bindElChild = (OMElement)bindElChildren.next();
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, bindElChild)){
                parseDocumentation(bindElChild, desc, binding);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FAULT, bindElChild)){
                parseBindingFault(bindElChild, desc, binding);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_OPERATION, bindElChild)){
                parseBindingOperation(bindElChild, desc, binding);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, bindElChild)){
                parseFeature(bindElChild, desc, binding);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, bindElChild)){
                parseProperty(bindElChild, desc, binding);
            }
            else{
                binding.addExtensionElement(parseExtensionElement(BindingElement.class, binding, bindElChild, desc) );
            }
        }
        return binding;
    }


    private BindingOperationElement parseBindingOperation(OMElement bindOpEl,
                                                          DescriptionElement desc,
                                                          BindingElement parent)
                                                          throws WSDLException{

        BindingOperationElement oper = parent.addBindingOperationElement();
        oper.setParentElement(parent);

        QName refQN = null;
        String ref = OMUtils.getAttribute(bindOpEl, Constants.ATTR_REF);
        if(ref != null){
            try{
                refQN = OMUtils.getQName(ref, bindOpEl);
                oper.setRef(refQN);
            }
            catch(WSDLException e){
                getErrorReporter().reportError(
                        new ErrorLocatorImpl(),
                        "WSDL505",
                        new Object[] {ref, QNameUtils.newQName(bindOpEl)},
                        ErrorReporter.SEVERITY_ERROR);
            }
        }

        parseExtensionAttributes(bindOpEl, BindingOperationElement.class, oper, desc);

        /* Parse the child elements of binding <operation>.*/
        Iterator bindOpElChildren = bindOpEl.getChildElements();
        while (bindOpElChildren.hasNext()){
            OMElement bindOpElChild = (OMElement)bindOpElChildren.next();
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, bindOpElChild)){
                parseDocumentation(bindOpElChild, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, bindOpElChild)){
                parseFeature(bindOpElChild, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, bindOpElChild)){
                parseProperty(bindOpElChild, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_INPUT, bindOpElChild)){
                parseBindingMessageReference(bindOpElChild, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_OUTPUT, bindOpElChild)){
                parseBindingMessageReference(bindOpElChild, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_INFAULT, bindOpElChild)){
                parseBindingFaultReference(bindOpElChild, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_OUTFAULT, bindOpElChild)){
                parseBindingFaultReference(bindOpElChild, desc, oper);
            }
            else{
                oper.addExtensionElement(
                    parseExtensionElement(BindingOperationElement.class, oper, bindOpElChild, desc) );
            }
        }
        return oper;
    }


    private BindingFaultReferenceElement parseBindingFaultReference(
                                                  OMElement faultRefEl,
                                                  DescriptionElement desc,
                                                  BindingOperationElement parent)
                                                  throws WSDLException{

        BindingFaultReferenceElement faultRef = parent.addBindingFaultReferenceElement();
        faultRef.setParentElement(parent);

        QName refQN = null;
        String ref = OMUtils.getAttribute(faultRefEl, Constants.ATTR_REF);
        if(ref != null){
            try{
                refQN = OMUtils.getQName(ref, faultRefEl);
                faultRef.setRef(refQN);
            }
            catch(WSDLException e){
                getErrorReporter().reportError(
                        new ErrorLocatorImpl(),
                        "WSDL505",
                        new Object[] {ref, QNameUtils.newQName(faultRefEl)},
                        ErrorReporter.SEVERITY_ERROR);
            }
        }

        String msgLabel = OMUtils.getAttribute(faultRefEl, Constants.ATTR_MESSAGE_LABEL);
        if(msgLabel != null){
            if(msgLabel.equals(MessageLabel.IN.toString())){
                faultRef.setMessageLabel(MessageLabel.IN);
            }
            else if(msgLabel.equals(MessageLabel.OUT.toString())){
                faultRef.setMessageLabel(MessageLabel.OUT);
            }
            else {
                //invalid value, but capture it anyway.
                faultRef.setMessageLabel(MessageLabel.invalidValue(msgLabel));
            }
        }

        parseExtensionAttributes(faultRefEl, BindingFaultReferenceElement.class, faultRef, desc);

        /* Parse the child elements of binding operation <infault> or <outfault>.*/
        Iterator faultRefElChildren = faultRefEl.getChildElements();
        while (faultRefElChildren.hasNext()){
            OMElement faultRefChild = (OMElement)faultRefElChildren.next();
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, faultRefChild)){
                parseDocumentation(faultRefChild, desc, faultRef);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, faultRefChild)){
                parseFeature(faultRefChild, desc, faultRef);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, faultRefChild)){
                parseProperty(faultRefChild, desc, faultRef);
            }
            else{
                faultRef.addExtensionElement(parseExtensionElement(BindingFaultReferenceElement.class, faultRef, faultRefChild, desc) );
            }
        }
        return faultRef;
    }


    private BindingMessageReferenceElement parseBindingMessageReference(
                                                 OMElement msgRefEl,
                                                 DescriptionElement desc,
                                                 BindingOperationElement parent)
                                                 throws WSDLException {

        BindingMessageReferenceElement message = parent.addBindingMessageReferenceElement();
        message.setParentElement(parent);

        if(Constants.ELEM_INPUT.equals(msgRefEl.getLocalName())) {
            message.setDirection(Direction.IN);
        }
        else if(Constants.ELEM_OUTPUT.equals(msgRefEl.getLocalName())) {
            message.setDirection(Direction.OUT);
        }

        String msgLabel = OMUtils.getAttribute(msgRefEl, Constants.ATTR_MESSAGE_LABEL);
        if(msgLabel != null){
            if(msgLabel.equals(MessageLabel.IN.toString())) {
                message.setMessageLabel(MessageLabel.IN);
            }
            else if(msgLabel.equals(MessageLabel.OUT.toString())) {
                message.setMessageLabel(MessageLabel.OUT);
            }
            else {
                //invalid value, but capture it anyway.
                message.setMessageLabel(MessageLabel.invalidValue(msgLabel));
            }
        }
        else{
            //TODO this is a temp fix, correct action to use MEP to determine default
            if(message.getDirection().equals(Direction.IN)){
                message.setMessageLabel(MessageLabel.IN);
            }
            else{
                message.setMessageLabel(MessageLabel.OUT);
            }
        }

        parseExtensionAttributes(msgRefEl, BindingMessageReferenceElement.class, message, desc);

        /* Parse the child elements of binding operation <input> or <output>.*/
        Iterator msgRefElChildren = msgRefEl.getChildElements();
        while (msgRefElChildren.hasNext()){
            OMElement msgRefChild = (OMElement)msgRefElChildren.next();
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, msgRefChild)){
                parseDocumentation(msgRefChild, desc, message);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, msgRefChild)){
                parseFeature(msgRefChild, desc, message);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, msgRefChild)){
                parseProperty(msgRefChild, desc, message);
            }
            else{
                message.addExtensionElement(parseExtensionElement(BindingMessageReferenceElement.class, message, msgRefChild, desc) );
            }
        }
        return message;
    }


    private BindingFaultElement parseBindingFault(OMElement bindFaultEl,
                                                  DescriptionElement desc,
                                                  BindingElement parent)
                                                  throws WSDLException{

        BindingFaultElement fault = parent.addBindingFaultElement();
        fault.setParentElement(parent);

        QName intFltQN = null;
        String ref = OMUtils.getAttribute(bindFaultEl, Constants.ATTR_REF);
        if(ref != null){
            try{
                intFltQN = OMUtils.getQName(ref, bindFaultEl);
                fault.setRef(intFltQN);
            }
            catch(WSDLException e){
                getErrorReporter().reportError(
                        new ErrorLocatorImpl(),
                        "WSDL505",
                        new Object[] {ref, QNameUtils.newQName(bindFaultEl)},
                        ErrorReporter.SEVERITY_ERROR);
            }
        }

        parseExtensionAttributes(bindFaultEl, BindingFaultElement.class, fault, desc);

        /* Parse the child elements of binding <fault>.*/
        Iterator bindFaultElChildren = bindFaultEl.getChildElements();
        while (bindFaultElChildren.hasNext()){
            OMElement bindFaultChild = (OMElement)bindFaultElChildren.next();
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, bindFaultChild)){
                parseDocumentation(bindFaultChild, desc, fault);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, bindFaultChild)){
                parseFeature(bindFaultChild, desc, fault);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, bindFaultChild)){
                parseProperty(bindFaultChild, desc, fault);
            }
            else{
                fault.addExtensionElement(parseExtensionElement(BindingFaultElement.class, fault, bindFaultChild, desc) );
            }
        }
        return fault;
    }

    private InterfaceElement parseInterface(OMElement interfaceEl,
                                            DescriptionElement desc)
                                            throws WSDLException {

        InterfaceElement intface = desc.addInterfaceElement();
        String name = OMUtils.getAttribute(interfaceEl, Constants.ATTR_NAME);
        if(name != null){
            intface.setName(new NCName(name));
        }
        String styleDefault = OMUtils.getAttribute(interfaceEl, Constants.ATTR_STYLE_DEFAULT);
        if(styleDefault != null){
            List stringList = StringUtils.parseNMTokens(styleDefault);
            String uriString = null;
            Iterator it = stringList.iterator();
            while(it.hasNext()){
                uriString = (String)it.next();
                intface.addStyleDefaultURI(getURI(uriString));
            }
        }

        String extendsAttr = OMUtils.getAttribute(interfaceEl, Constants.ATTR_EXTENDS);
        if(extendsAttr != null){
            List stringList = StringUtils.parseNMTokens(extendsAttr);
            String qnString = null;
            Iterator it = stringList.iterator();
            while(it.hasNext()){
                qnString = (String)it.next();
                intface.addExtendedInterfaceName(OMUtils.getQName(qnString, interfaceEl));
            }
        }

        parseExtensionAttributes(interfaceEl, InterfaceElement.class, intface, desc);

        /* Parse the child elements of <interface>.*/
        Iterator interfaceChildren = interfaceEl.getChildElements();
        while (interfaceChildren.hasNext()){
            OMElement interfaceChild = (OMElement)interfaceChildren.next();
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, interfaceChild)){
                parseDocumentation(interfaceChild, desc, intface);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FAULT, interfaceChild)){
                parseInterfaceFault(interfaceChild, desc, intface);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_OPERATION, interfaceChild)){
                parseInterfaceOperation(interfaceChild, desc, intface);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, interfaceChild)){
                parseFeature(interfaceChild, desc, intface);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, interfaceChild)){
                parseProperty(interfaceChild, desc, intface);
            }
            else{
                intface.addExtensionElement(
                    parseExtensionElement(InterfaceElement.class, intface, interfaceChild, desc) );
            }
        }
        return intface;
    }


    private InterfaceOperationElement parseInterfaceOperation(
                                                 OMElement operEl,
                                                 DescriptionElement desc,
                                                 InterfaceElement parent)
                                                 throws WSDLException{

        InterfaceOperationElement oper = parent.addInterfaceOperationElement();
        oper.setParentElement(parent);

        String name = OMUtils.getAttribute(operEl, Constants.ATTR_NAME);
        if(name != null){
            oper.setName(new NCName(name));
        }

        String style = OMUtils.getAttribute(operEl, Constants.ATTR_STYLE);
        if(style != null){
            List stringList = StringUtils.parseNMTokens(style);
            String uriString = null;
            Iterator it = stringList.iterator();
            while(it.hasNext()){
                uriString = (String)it.next();
                oper.addStyleURI(getURI(uriString));
            }
        }

        String pat = OMUtils.getAttribute(operEl, Constants.ATTR_PATTERN);
        if(pat != null){
            oper.setPattern(getURI(pat));
        }

        parseExtensionAttributes(operEl, InterfaceOperationElement.class, oper, desc);

        /* Parse the child elements of interface <operation>.*/
        Iterator operElChildren = operEl.getChildElements();
        while (operElChildren.hasNext()){
            OMElement operElChild = (OMElement)operElChildren.next();
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, operElChild)){
                parseDocumentation(operElChild, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, operElChild)){
                parseFeature(operElChild, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, operElChild)){
                parseProperty(operElChild, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_INPUT, operElChild)){
                parseInterfaceMessageReference(operElChild, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_OUTPUT, operElChild)){
                parseInterfaceMessageReference(operElChild, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_INFAULT, operElChild)){
                parseInterfaceFaultReference(operElChild, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_OUTFAULT, operElChild)){
                parseInterfaceFaultReference(operElChild, desc, oper);
            }
            else{
                oper.addExtensionElement(
                    parseExtensionElement(InterfaceOperationElement.class, oper, operElChild, desc) );
            }
        }
        return oper;
    }


    private InterfaceFaultReferenceElement parseInterfaceFaultReference(
                                                 OMElement faultRefEl,
                                                 DescriptionElement desc,
                                                 InterfaceOperationElement parent)
                                                 throws WSDLException{

        InterfaceFaultReferenceElement faultRef = parent.addInterfaceFaultReferenceElement();
        faultRef.setParentElement(parent);

        if(Constants.ELEM_INFAULT.equals(faultRefEl.getLocalName())) {
            faultRef.setDirection(Direction.IN);
        }
        else if(Constants.ELEM_OUTFAULT.equals(faultRefEl.getLocalName())){
            faultRef.setDirection(Direction.OUT);
        }

        String ref = OMUtils.getAttribute(faultRefEl, Constants.ATTR_REF);
        if(ref != null){
            try{
                QName qname = OMUtils.getQName(ref, faultRefEl);
                faultRef.setRef(qname);
            }
            catch(WSDLException e){
                getErrorReporter().reportError(
                        new ErrorLocatorImpl(),
                        "WSDL505",
                        new Object[] {ref, QNameUtils.newQName(faultRefEl)},
                        ErrorReporter.SEVERITY_ERROR);
            }
        }

        String msgLabel = OMUtils.getAttribute(faultRefEl, Constants.ATTR_MESSAGE_LABEL);
        if(msgLabel != null){
            if(msgLabel.equals(MessageLabel.IN.toString())) {
                faultRef.setMessageLabel(MessageLabel.IN);
            }
            else if(msgLabel.equals(MessageLabel.OUT.toString())) {
                faultRef.setMessageLabel(MessageLabel.OUT);
            }
            else {
                //invalid value, but capture it anyway.
                faultRef.setMessageLabel(MessageLabel.invalidValue(msgLabel));
            }
        }

        parseExtensionAttributes(faultRefEl, InterfaceFaultReferenceElement.class, faultRef, desc);

        Iterator faultRefElChildren = faultRefEl.getChildElements();

        while (faultRefElChildren.hasNext()){
            OMElement faultRefElChild = (OMElement)faultRefElChildren.next();
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, faultRefElChild)){
                parseDocumentation(faultRefElChild, desc, faultRef);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, faultRefElChild)){
                parseFeature(faultRefElChild, desc, faultRef);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, faultRefElChild)){
                parseProperty(faultRefElChild, desc, faultRef);
            }
            else{
                faultRef.addExtensionElement(
                    parseExtensionElement(InterfaceFaultReferenceElement.class, faultRef, faultRefElChild, desc) );
            }
        }
        return faultRef;
    }


    private InterfaceMessageReferenceElement parseInterfaceMessageReference(
                                                 OMElement msgRefEl,
                                                 DescriptionElement desc,
                                                 InterfaceOperationElement parent)
                                                 throws WSDLException{

        InterfaceMessageReferenceElement message = parent.addInterfaceMessageReferenceElement();
        message.setParentElement(parent);

        if(Constants.ELEM_INPUT.equals(msgRefEl.getLocalName())) {
            message.setDirection(Direction.IN);
        }
        else if(Constants.ELEM_OUTPUT.equals(msgRefEl.getLocalName())) {
            message.setDirection(Direction.OUT);
        }

        String msgLabel = OMUtils.getAttribute(msgRefEl, Constants.ATTR_MESSAGE_LABEL);
        if(msgLabel != null){
            if(msgLabel.equals(MessageLabel.IN.toString())) {
                message.setMessageLabel(MessageLabel.IN);
            }
            else if(msgLabel.equals(MessageLabel.OUT.toString())) {
                message.setMessageLabel(MessageLabel.OUT);
            }
            else {
                //invalid value, but capture it anyway.
                message.setMessageLabel(MessageLabel.invalidValue(msgLabel));
            }
        }
        else{
            //TODO this is a temp fix, correct action to use MEP to determine default
            if(message.getDirection().equals(Direction.IN)){
                message.setMessageLabel(MessageLabel.IN);
            }
            else{
                message.setMessageLabel(MessageLabel.OUT);
            }
        }

        String element = OMUtils.getAttribute(msgRefEl, Constants.ATTR_ELEMENT);
        if(element != null){
            if(element.equals(Constants.NMTOKEN_ANY) ||
               element.equals(Constants.NMTOKEN_NONE) ||
               element.equals(Constants.NMTOKEN_OTHER)){
                message.setMessageContentModel(element);
            }
            else{
                //element is not #any, #none or #other, so it must be an element qname
                message.setMessageContentModel(Constants.NMTOKEN_ELEMENT);
                QName qname = OMUtils.getQName(element, msgRefEl);
                message.setElementName(qname);
            }
        }
        else{
            //Per mapping defined in WSDL 2.0 Part 2 spec section 2.5.3,
            //if element attribute not present, message content model is #other
            message.setMessageContentModel(Constants.NMTOKEN_OTHER);
        }

        parseExtensionAttributes(msgRefEl, InterfaceMessageReferenceElement.class, message, desc);

        Iterator msgRefElChildren = msgRefEl.getChildElements();

        while(msgRefElChildren.hasNext()){
            OMElement msgRefChild = (OMElement)msgRefElChildren.next();
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, msgRefChild)){
                parseDocumentation(msgRefChild, desc, message);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, msgRefChild)){
                parseFeature(msgRefChild, desc, message);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, msgRefChild)){
                parseProperty(msgRefChild, desc, message);
            }
            else{
                message.addExtensionElement(
                    parseExtensionElement(InterfaceMessageReferenceElement.class, message, msgRefChild, desc) );
            }
        }
        return message;
    }


    private InterfaceFaultElement parseInterfaceFault(
                                             OMElement faultEl,
                                             DescriptionElement desc,
                                             InterfaceElement parent)
                                             throws WSDLException{

        InterfaceFaultElement fault = parent.addInterfaceFaultElement();
        fault.setParentElement(parent);

        String name = OMUtils.getAttribute(faultEl, Constants.ATTR_NAME);

        if(name != null){
            fault.setName(new NCName(name));
        }

        String element = OMUtils.getAttribute(faultEl, Constants.ATTR_ELEMENT);

        if(element != null){
            try {
                QName qname = OMUtils.getQName(element, faultEl);
                fault.setElementName(qname);
            }
            catch (WSDLException e) {
                getErrorReporter().reportError(
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL505",
                        new Object[] {element, QNameUtils.newQName(faultEl)},
                        ErrorReporter.SEVERITY_ERROR);
            }
        }

        parseExtensionAttributes(faultEl, InterfaceFaultElement.class, fault, desc);

        Iterator faultElChildren = faultEl.getChildElements();
        while(faultElChildren.hasNext()){
            OMElement faultElChild = (OMElement)faultElChildren.next();
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, faultElChild)){
                parseDocumentation(faultElChild, desc, fault);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, faultElChild)){
                parseFeature(faultElChild, desc, fault);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, faultElChild)){
                parseProperty(faultElChild, desc, fault);
            }
            else{
                fault.addExtensionElement(
                    parseExtensionElement(InterfaceFaultElement.class, fault, faultElChild, desc) );
            }
        }
        return fault;
    }


    private PropertyElement parseProperty(OMElement propEl,
                                          DescriptionElement desc,
                                          ConfigurableElement parent)
                                          throws WSDLException{

        PropertyElement property = parent.addPropertyElement();
        property.setParentElement(parent);

        String ref = OMUtils.getAttribute(propEl, Constants.ATTR_REF);
        if(ref != null){
            property.setRef(getURI(ref));
        }

        parseExtensionAttributes(propEl, PropertyElement.class, property, desc);

        Iterator propElChildren = propEl.getChildElements();

        while (propElChildren.hasNext()){
            OMElement propElChild = (OMElement)propElChildren.next();

            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, propElChild)){
                parseDocumentation(propElChild, desc, property);
            }
            else if(QNameUtils.matches(Constants.Q_ELEM_VALUE, propElChild)){
                //the property value consists of the child info items of <value>
                Iterator propChildValueEls = propElChild.getChildElements();
                property.setValue(propChildValueEls);
            }
            else if(QNameUtils.matches(Constants.Q_ELEM_CONSTRAINT, propElChild)){
                //todo
            }
            else{
                property.addExtensionElement(parseExtensionElement(PropertyElement.class, property, propElChild, desc) );
            }
        }
        return property;
    }


    /*
     * Parse the &lt;xs:import&gt; element and retrieve the imported
     * schema document if schemaLocation specified. Failure to retrieve
     * the schema will only matter if any WSDL components contain elements or
     * constraints that refer to the schema, and typically this will be
     * determined later by WSDL validation. So just report any such errors
     * and return the SchemaImport object (i.e. with a null schema property).
     *
     * WSDL 2.0 spec validation:
     * - namespace attribute is REQUIRED
     * - imported schema MUST have a targetNamespace
     * - namespace and targetNamespace MUST be the same
     */
    private Schema parseSchemaImport(OMElement importEl,
                                     DescriptionElement desc)
                                     throws WSDLException {

        ImportedSchemaImpl schema = new ImportedSchemaImpl();

        String ns = OMUtils.getAttribute(importEl, Constants.ATTR_NAMESPACE);

        if(ns != null) {
            schema.setNamespace(getURI(ns));
        }

        String sloc = OMUtils.getAttribute(importEl, SchemaConstants.ATTR_SCHEMA_LOCATION);
        if(sloc != null) {
            schema.setSchemaLocation(getURI(sloc));
        }

        if(schema.getNamespace() == null){
            //The namespace attribute is REQUIRED on xs:import, so don't continue.
            schema.setReferenceable(false);
            return schema;
        }

        if(schema.getSchemaLocation() == null){
            //This is a namespace-only import, no schema document to be retrieved so don't continue.

            /* TODO investigate whether/how to try to resolve the imported namespace to known schema
             * components from that namespace (e.g. via a URI catalog resolver). Currently, any attempt
             * to resolve a QName against schema components from this namespace will search ALL
             * schemas imported from this namespace (see methods in TypesImpl).
             */

            return schema;
        }

        //Now try to retrieve the schema import using schemaLocation

        OMElement importedSchemaDoc = null;
        OMElement schemaEl = null;
        URI contextURI = null;
        String schemaLoc = null;
        URL url = null;

        try{
            contextURI = desc.getDocumentBaseURI();
            System.out.println(contextURI);
            System.out.println(contextURI.toURL());
            URL contextURL = (contextURI != null) ? contextURI.toURL() : null;
            schemaLoc = schema.getSchemaLocation().toString();
            url = StringUtils.getURL(contextURL, schemaLoc);

        }
        catch (MalformedURLException e) {

            String baseLoc = contextURI != null ? contextURI.toString() : null;
            getErrorReporter().reportError(
                    new ErrorLocatorImpl(),  //TODO line&col nos.
                    "WSDL502",
                    new Object[] {baseLoc, schemaLoc},
                    ErrorReporter.SEVERITY_ERROR);

            //can't continue schema retrieval with a bad URL.
            schema.setReferenceable(false);
            return schema;
        }

        String schemaURL = url.toString();

        //If the schema has already been imported, reuse it.
        XmlSchema schemaDef = (XmlSchema)fImportedSchemas.get(schemaURL);

        if(schemaDef == null){
            //not previously imported, so retrieve it now.
            importedSchemaDoc = OMUtils.getElement(schemaURL);

            /*
            * First get the first element and serialize that into a byte array.
            * This is used in getting an InputSource which is later used as an argument
            * to the XMLSchemaCollection object.
            */
            schemaEl = importedSchemaDoc.getFirstElement();
            String schemaElStr = null;
            try {
                schemaElStr = schemaEl.toStringWithConsume();
            }
            catch (XMLStreamException e) {
                e.printStackTrace();
            }
            byte[] schemaElbytes = schemaElStr.getBytes();
            InputSource schemaSource = new InputSource(new ByteArrayInputStream(schemaElbytes));

            try {
                XmlSchemaCollection xsc = new XmlSchemaCollection();
                schemaDef = xsc.read(schemaSource, null);
                fImportedSchemas.put(schemaURL, schemaDef);
            }
            catch (XmlSchemaException e){
                getErrorReporter().reportError(
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL522",
                        new Object[] {schemaURL},
                        ErrorReporter.SEVERITY_WARNING,
                        e);
            }
        }
        if(schemaDef != null) {
            schema.setSchemaDefinition(schemaDef);
        }
        else {
            schema.setReferenceable(false);
        }
        return schema;
    }


    private Schema parseSchemaInline(OMElement schemaElement,
                                     DescriptionElement desc)
                                     throws WSDLException{

        InlinedSchemaImpl schema = new InlinedSchemaImpl();
        schema.setId(OMUtils.getAttribute(schemaElement, Constants.ATTR_ID));
        String tns = OMUtils.getAttribute(schemaElement, Constants.ATTR_TARGET_NAMESPACE);
        if(tns != null) {
            schema.setNamespace(getURI(tns));
        }

        String baseURI = desc.getDocumentBaseURI() != null ?
                         desc.getDocumentBaseURI().toString() : null;

        XmlSchema schemaDef = null;

        try {
            InputSource schemaSource = OMUtils.getInputSource(schemaElement);
            XmlSchemaCollection xsc = new XmlSchemaCollection();

            //Set the baseURI and the namespaces from the DescriptionElement in the XMLSchemaCollection
            xsc.setBaseUri(baseURI);
            NamespaceMap namespaces = new NamespaceMap(desc.getNamespaces());
            xsc.setNamespaceContext(namespaces);
            schemaDef = xsc.read(schemaSource, null);
        }
        catch (XmlSchemaException e){

            getErrorReporter().reportError(
                    new ErrorLocatorImpl(),  //TODO line&col nos.
                    "WSDL521",
                    new Object[] {baseURI},
                    ErrorReporter.SEVERITY_WARNING,
                    e);
        }
        if(schemaDef != null) {
            schema.setSchemaDefinition(schemaDef);
        }
        else {
            schema.setReferenceable(false);
        }

        return schema;
    }


    private FeatureElement parseFeature(OMElement featEl,
                                        DescriptionElement desc,
                                        ConfigurableElement parent)
                                        throws WSDLException {

        FeatureElement feature = parent.addFeatureElement();
        feature.setParentElement(parent);

        String ref = OMUtils.getAttribute(featEl, Constants.ATTR_REF);
        if(ref != null){
            feature.setRef(getURI(ref));
        }

        String req = OMUtils.getAttribute(featEl, Constants.ATTR_REQUIRED);
        feature.setRequired(Constants.VALUE_TRUE.equals(req) ? true : false);

        //TODO t.b.c. what if attr value is not 'true' or 'false'? (eg, missing, mispelt or not lower case.

        parseExtensionAttributes(featEl, FeatureElement.class, feature, desc);

        /* Parse the child elements of the <feature> element.*/
        //TODO: first checking with the parent element and then going inwards.
        // Check if this step is necessary
        while (featEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, featEl)){
                parseDocumentation(featEl, desc, feature);
            }
            else{
                feature.addExtensionElement(parseExtensionElement(FeatureElement.class, feature, featEl, desc) );
            }

            Iterator featElChildren = featEl.getChildElements();
            while (featElChildren.hasNext()){
                OMElement featElChild = (OMElement)featElChildren.next();
                if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, featElChild)){
                    parseDocumentation(featElChild, desc, feature);
                }
                else{
                    feature.addExtensionElement(parseExtensionElement(
                            FeatureElement.class, feature, featElChild, desc) );
                }
            }
        }
        return feature;
    }

    private ImportElement parseImport(OMElement importEl,
                                      DescriptionElement desc,
                                      Map wsdlModules)
                                      throws WSDLException{
        ImportElement imp = desc.addImportElement();

        String namespaceURI = OMUtils.getAttribute(importEl, Constants.ATTR_NAMESPACE);
        String locationURI = OMUtils.getAttribute(importEl, Constants.ATTR_LOCATION);

        parseExtensionAttributes(importEl, ImportElement.class, imp, desc);

        if(namespaceURI != null){
            //TODO handle missing namespace attribute (REQUIRED attr)
            imp.setNamespace(getURI(namespaceURI));
        }

        if(locationURI != null){
            //TODO handle missing locationURI (OPTIONAL attr)
            imp.setLocation(getURI(locationURI));
            DescriptionElement importedDesc =
                getWSDLFromLocation(locationURI, desc, wsdlModules);
            imp.setDescriptionElement(importedDesc);
        }

        return imp;
    }

    private IncludeElement parseInclude(OMElement includeEl,
                                        DescriptionElement desc,
                                        Map wsdlModules)
                                        throws WSDLException{
        IncludeElement include = desc.addIncludeElement();

        String locationURI = OMUtils.getAttribute(includeEl, Constants.ATTR_LOCATION);

        parseExtensionAttributes(includeEl, IncludeElement.class, include, desc);

        if(locationURI != null){
            include.setLocation(getURI(locationURI));
            DescriptionElement includedDesc =
                getWSDLFromLocation(locationURI, desc, wsdlModules);
            include.setDescriptionElement(includedDesc);
        }

        return include;
    }


    //TODO
    private ExtensionElement parseExtensionElement(Class parentType,
                                                   WSDLElement parent,
                                                   OMElement el,
                                                   DescriptionElement desc)
                                                   throws WSDLException {

        QName elementType = QNameUtils.newQName(el);
        String namespaceURI = el.getNamespace().getNamespaceURI();
        try{
            if (namespaceURI == null || namespaceURI.equals(Constants.NS_URI_WSDL20)){
                getErrorReporter().reportError(
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL520",
                        new Object[] {elementType, parentType.getName()},
                        ErrorReporter.SEVERITY_ERROR);
                return null;
            }

            ExtensionRegistry extReg = desc.getExtensionRegistry();

            if (extReg == null){
                getErrorReporter().reportError(
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL514",
                        new Object[] {elementType, parentType.getName()},
                        ErrorReporter.SEVERITY_ERROR);
                return null;
            }

            ExtensionDeserializer extDS = extReg.queryDeserializer(parentType,elementType);

            //TODO FIXME unmarshall method to accept OMElements?
//            return extDS.unmarshall(parentType, parent, elementType, el, desc, extReg);

            return null;
        }
        catch (WSDLException e){
            throw e;
        }
    }


    //TODO
    private void parseExtensionAttributes(OMElement domEl,
                                          Class wsdlClass,
                                          WSDLElement wsdlObj,
                                          DescriptionElement desc)
                                          throws WSDLException {
    }


    public DescriptionElement readWSDL(String wsdlURI,
                                       ErrorHandler errorHandler)
                                       throws WSDLException {
        if(errorHandler != null)
            getErrorReporter().setErrorHandler(errorHandler);

        return readWSDL(wsdlURI);
    }

    ///////////////////////////////////////
    //  METHODS FOR READING FROM A SOURCE
    ///////////////////////////////////////

    //TODO
    public DescriptionElement readWSDL(WSDLSource wsdlSource)
                                    throws WSDLException {
        return null;
    }


    //TODO
    public DescriptionElement readWSDL(WSDLSource wsdlSource,
                                       ErrorHandler errorHandler)
                                       throws WSDLException {
        return null;
    }


    //////////////////////////
    //  HELPER METHODS
    //////////////////////////

    /*
     * Convert a string of type xs:anyURI to a java.net.URI.
     * An empty string argument will return an empty string URI.
     * A null argument will return a null.
     */
    private URI getURI(String anyURI) throws WSDLException{
        URI uri = null;
        if(anyURI != null){
            try {
                uri = new URI(anyURI);
            }
            catch (URISyntaxException e) {
                getErrorReporter().reportError(
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL506",
                        new Object[] {anyURI},
                        ErrorReporter.SEVERITY_ERROR,
                        e);
            }
        }
        return uri;
    }

    /*
     * Check the actual OM element encountered against the expected qname
     *
     * @param el actual element encountered
     * @param qname expected element's qname
     * @throws WSDLException
     */
    private void checkElementName(OMElement el,
                                  QName qname)
                                  throws WSDLException {

        if (!QNameUtils.matches(qname, el)){
            getErrorReporter().reportError(
                new ErrorLocatorImpl(),  //TODO line&col nos.
                "WSDL501",
                new Object[] {qname, QNameUtils.newQName(el)},
                ErrorReporter.SEVERITY_FATAL_ERROR);

            //TODO wsdlExc.setLocation(XPathUtils.getXPathExprFromNode(el));
        }
    }

    /*
     * Retrieve a WSDL document by resolving the location URI specified
     * on a WSDL &lt;import&gt; or &lt;include&gt; element.
     *
     * TODO add support for a URL Catalog Resolver
     */
    private DescriptionElement getWSDLFromLocation(String locationURI,
                                                   DescriptionElement desc,
                                                   Map wsdlModules)
                                               throws WSDLException{
        DescriptionElement referencedDesc = null;
        OMElement docEl;
        URL locationURL = null;
        URI contextURI = null;

        try{
            contextURI = desc.getDocumentBaseURI();
            URL contextURL = (contextURI != null) ? contextURI.toURL() : null;
            locationURL = StringUtils.getURL(contextURL, locationURI);
        }
        catch (MalformedURLException e){
            String baseURI = contextURI != null ? contextURI.toString() : null;

            getErrorReporter().reportError(
                    new ErrorLocatorImpl(),  //TODO line&col nos.
                    "WSDL502",
                    new Object[] {baseURI, locationURI},
                    ErrorReporter.SEVERITY_ERROR);

            //can't continue import with a bad URL.
            return null;
        }

        String locationStr = locationURL.toString();

        //Check if WSDL imported or included previously from this location.
        referencedDesc = (DescriptionElement)wsdlModules.get(locationStr);

        if(referencedDesc == null){
            //not previously imported or included, so retrieve the WSDL.
            docEl = OMUtils.getElement(locationStr);

            //The referenced document should contain a WSDL <description>
            if(!QNameUtils.matches(Constants.Q_ELEM_DESCRIPTION, docEl)){
                getErrorReporter().reportError(
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL501",
                        new Object[] {Constants.Q_ELEM_DESCRIPTION,
                                      QNameUtils.newQName(docEl)},
                        ErrorReporter.SEVERITY_ERROR);

                //cannot continue without a <description> element
                return null;
            }

            referencedDesc = parseDescription(locationStr,
                                              docEl,
                                              wsdlModules);

            if(!wsdlModules.containsKey(locationStr)){
                wsdlModules.put(locationStr, referencedDesc);
            }
        }

        return referencedDesc;
    }

    //TODO
    public WSDLSource createWSDLSource() {
        return null;
    }


}
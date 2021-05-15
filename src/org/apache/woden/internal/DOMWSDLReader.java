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
package org.apache.woden.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.woden.ErrorHandler;
import org.apache.woden.ErrorReporter;
import org.apache.woden.WSDLException;
import org.apache.woden.WSDLReader;
import org.apache.woden.WSDLSource;
import org.apache.woden.internal.schema.ImportedSchemaImpl;
import org.apache.woden.internal.schema.InlinedSchemaImpl;
import org.apache.woden.internal.schema.SchemaConstants;
import org.apache.woden.internal.util.StringUtils;
import org.apache.woden.internal.util.dom.DOMUtils;
import org.apache.woden.internal.util.dom.QNameUtils;
import org.apache.woden.internal.util.dom.XPathUtils;
import org.apache.woden.internal.wsdl20.Constants;
import org.apache.woden.internal.wsdl20.validation.WSDLComponentValidator;
import org.apache.woden.internal.wsdl20.validation.WSDLDocumentValidator;
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
import org.apache.woden.xml.XMLAttr;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaException;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

/**
 * Implements the WSDLReader behaviour for DOM-based parsing.
 * 
 * @author John Kaputin (jkaputin@apache.org)
 */
public class DOMWSDLReader extends BaseWSDLReader {
    
    //a map of imported schema definitions keyed by schema location URI
    private Map fImportedSchemas = new Hashtable();
    
    /**
     * WSDL document validator. Only one instance is needed.
     */
    private WSDLDocumentValidator docValidator = null;
    
    /**
     * WSDL component validator. Only one instance is needed.
     */
    private WSDLComponentValidator compValidator = null;
    
    public DOMWSDLReader() throws WSDLException {
        super();
    }
    
    /* ************************************************************
     *  API public methods
     * ************************************************************/
    
    /* (non-Javadoc)
     * @see org.apache.woden.WSDLReader#createWSDLSource()
     */
    public WSDLSource createWSDLSource() {
        return new DOMWSDLSource(getErrorReporter());
    }
    
    /*
     * @see org.apache.woden.WSDLReader#readWSDL(String)
     * 
     * TODO change uri args on public readWSDL methods to java.net.URI
     */
    public DescriptionElement readWSDL(String wsdlURI) throws WSDLException 
    {
        URL url;
        try {
            url = StringUtils.getURL(null, wsdlURI);
            
        } catch (MalformedURLException e) {
            
            String msg = getErrorReporter().getFormattedMessage(
                            "WSDL502", new Object[] {null, wsdlURI});
            throw new WSDLException(WSDLException.PARSER_ERROR, msg, e);
        }
        String wsdlURL = url.toString();
            
        InputSource inputSource = new InputSource(wsdlURL);
        return readWSDL(wsdlURL, inputSource);
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.WSDLReader#readWSDL(java.lang.String, org.apache.woden.ErrorHandler)
     */
    public DescriptionElement readWSDL(String wsdlURI, ErrorHandler errorHandler) throws WSDLException 
    {
    	if(errorHandler != null)
    		getErrorReporter().setErrorHandler(errorHandler);
    	
    	return readWSDL(wsdlURI);
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.WSDLReader#readWSDL(org.apache.woden.WSDLSource)
     */
    public DescriptionElement readWSDL(WSDLSource wsdlSource) throws WSDLException {
        
        //TODO decide on how to handle null args in readWSDL methods (e.g.
        //IllegalArgExc, WSDLExc, return null, etc).

        Object source = wsdlSource.getSource();
        URI baseURI = wsdlSource.getBaseURI();
        
        String wsdlURL = null;
        if(baseURI != null)
        {
            URL url;
            try {
                url = StringUtils.getURL(null, baseURI.toString());
                
            } catch (MalformedURLException e) {
                
                String msg = getErrorReporter().getFormattedMessage(
                        "WSDL502", new Object[] {null, baseURI.toString()});
                throw new WSDLException(WSDLException.PARSER_ERROR, msg, e);
            }
            wsdlURL = url.toString();
        }
        
        if(source instanceof Element) {
            return readWSDL(wsdlURL, (Element)source);
        }
        else if(source instanceof Document) {
            return readWSDL(wsdlURL, (Document)source);
        }
        else if(source instanceof InputSource) {
            return readWSDL(wsdlURL, (InputSource)source);
        }
        else {
            //This exception is checked in WSDLSource.setSource but we check
            //again here in case the wrong type of WSDLSource has been used
            //with this type of WSDLReader.
            String sourceClass = source.getClass().getName();
            String readerClass = this.getClass().getName();
            String msg = getErrorReporter().getFormattedMessage(
                    "WSDL017", new Object[] {sourceClass, readerClass});
            throw new WSDLException(WSDLException.PARSER_ERROR, msg);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.WSDLReader#readWSDL(org.apache.woden.WSDLSource, org.apache.woden.ErrorHandler)
     */
    public DescriptionElement readWSDL(WSDLSource wsdlSource, ErrorHandler errorHandler) 
        throws WSDLException {
        
        if(errorHandler != null)
            getErrorReporter().setErrorHandler(errorHandler);
        
        return readWSDL(wsdlSource);
    }
    
    /*
     * Helper method for readWSDL(WSDLSource)
     */
    private DescriptionElement readWSDL(String wsdlURL, Element docEl) 
        throws WSDLException {
        
        DescriptionElement desc = parseDescription(wsdlURL, docEl, null);
        
        // Validate the model if validation is enabled.
        if(features.getValue(WSDLReader.FEATURE_VALIDATION))
        {
            if(docValidator == null)
            {
                docValidator = new WSDLDocumentValidator();
            }
            if(docValidator.validate(desc, getErrorReporter()))
            {
                if(compValidator == null)
                {
                    compValidator = new WSDLComponentValidator();
                }
                compValidator.validate(desc.toComponent(), getErrorReporter());
            }
        }
        return desc;
    }
    
    /*
     * Helper method for readWSDL(WSDLSource)
     */
    private DescriptionElement readWSDL(String wsdlURL, Document domDoc) 
        throws WSDLException {
        
        return readWSDL(wsdlURL, domDoc.getDocumentElement());
    }
    
    /*
     * Helper method for readWSDL(WSDLSource)
     */
    private DescriptionElement readWSDL(String wsdlURL, InputSource inputSource) 
        throws WSDLException {
    
        try
        {
            Document wsdlDocument = getDocument(inputSource, wsdlURL);
            
            return readWSDL(wsdlURL, wsdlDocument);
            
        } catch (IOException e) {
            
            String msg = getErrorReporter().getFormattedMessage(
                    "WSDL503", new Object[] {wsdlURL});
            throw new WSDLException(WSDLException.PARSER_ERROR, msg, e);
        }
    }
    
    
    /* ************************************************************
     *  Parsing methods - e.g. parseXXXX()
     * ************************************************************/
    
    /* Parse the attributes and child elements of the <description> element.
     * As per the WSDL 2.0 spec, the child elements must be in the 
     * following order if present:
     * <documentation>
     * <import> <include> or WSDL extension elements in any order
     * <types>
     * <interface> <binding> <service> or WSDL extension elements in any order.
     * TODO validate that the elements are in correct order
     */ 
    private DescriptionElement parseDescription(String documentBaseURI, 
                                                Element descEl, 
                                                Map wsdlModules) 
                                                throws WSDLException 
    {
        checkElementName(descEl, Constants.Q_ELEM_DESCRIPTION);
        
        DescriptionElement desc = 
            ((DOMWSDLFactory)getFactory()).newDescription();
        
        if(wsdlModules == null) 
        {
            //This is the initial WSDL document. No imports or includes yet.
            //TODO this might be the place to flag the initial Desc if necessary.
            wsdlModules = new HashMap();
        }
        
        if(getExtensionRegistry() != null) 
        {
            desc.setExtensionRegistry(getExtensionRegistry());
        }
        
        if(getErrorReporter() != null) 
        {
            (desc.getExtensionRegistry()).setErrorReporter(getErrorReporter());
        }
        
        desc.setDocumentBaseURI(getURI(documentBaseURI));

        String targetNamespace = 
            DOMUtils.getAttribute(descEl, Constants.ATTR_TARGET_NAMESPACE);
        
        if(targetNamespace != null)
        {
            desc.setTargetNamespace(getURI(targetNamespace));
        }
        
        //parse the namespace declarations
        NamedNodeMap attrs = descEl.getAttributes();
        int size = attrs.getLength();

        for (int i = 0; i < size; i++)
        {
          Attr attr = (Attr)attrs.item(i);
          String namespaceURI = attr.getNamespaceURI();
          String localPart = attr.getLocalName();
          String value = attr.getValue();

          if ((Constants.NS_URI_XMLNS).equals(namespaceURI))
          {
            if (!(Constants.ATTR_XMLNS).equals(localPart))
            {
              desc.addNamespace(localPart, getURI(value));  //a prefixed namespace
            }
            else
            {
              desc.addNamespace(null, getURI(value));       //the default namespace
            }
          }
        }
        
        parseExtensionAttributes(descEl, DescriptionElement.class, desc, desc);
        
        //parse the child elements
        Element tempEl = DOMUtils.getFirstChildElement(descEl);

        while (tempEl != null)
        {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl))
            {
                parseDocumentation(tempEl, desc, desc);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_IMPORT, tempEl))
            {
                if(documentBaseURI != null && !wsdlModules.containsKey(documentBaseURI)) 
                {
                    wsdlModules.put(documentBaseURI, desc);
                }
                parseImport(tempEl, desc, wsdlModules);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_INCLUDE, tempEl))
            {
                if(documentBaseURI != null && !wsdlModules.containsKey(documentBaseURI)) 
                {
                    wsdlModules.put(documentBaseURI, desc);
                }
                parseInclude(tempEl, desc, wsdlModules);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_TYPES, tempEl))
            {
                parseTypes(tempEl, desc);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_INTERFACE, tempEl))
            {
                parseInterface(tempEl, desc);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_BINDING, tempEl))
            {
                parseBinding(tempEl, desc);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_SERVICE, tempEl))
            {
                parseService(tempEl, desc);
            }
            else
            {
                desc.addExtensionElement(
                    parseExtensionElement(DescriptionElement.class, desc, tempEl, desc) );
            }
            
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        
        // Parse the schema for schema to include the built in schema types in the Woden model.
        // TODO: As there are a finite number of built in schema types it may be better to create
        // constants rather than reading the schema for schema on the creation of every model. 
        // Also, this method currently requires that the schema elements exist in the types element.
        // This may not be the best idea as it may imply that this schema contains an actual import
        // statement in a WSDL 2.0 document. This method also does not work for when building the
        // model programmatically.
        // This method should be reevaluated at a later point.
        TypesElement types = desc.getTypesElement();
        if (types.getTypeSystem() == null)
        {
          types.setTypeSystem(Constants.TYPE_XSD_2001);
        }
        try
        {
          Document schemaDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
          Element schemaElem = schemaDoc.createElementNS("http://www.w3.org/2001/XMLSchema", "import");
          schemaElem.setAttribute("namespace", "http://www.w3.org/2001/XMLSchema");
          schemaElem.setAttribute("schemaLocation", "http://www.w3.org/2001/XMLSchema.xsd");
          desc.getTypesElement().addSchema(parseSchemaImport(schemaElem, desc));
        }
        catch(Exception e)
        {
          System.out.println("A problem was encountered while creating the build in XML schema types: " + e);
        }
          
        
        return desc;
    }

    private DocumentationElement parseDocumentation(Element docEl, 
                                                    DescriptionElement desc,
                                                    DocumentableElement parent) 
                                                    throws WSDLException
    {
        DocumentationElement documentation = parent.addDocumentationElement();
        
        //TODO store docEl as below, or just extract any text? 
        documentation.setContent(docEl);
        
        //Now parse any extensibility attributes or elements
        
        parseExtensionAttributes(docEl, DocumentationElement.class, documentation, desc);

        Element tempEl = DOMUtils.getFirstChildElement(docEl);

        while (tempEl != null)
        {
            documentation.addExtensionElement(
                parseExtensionElement(DocumentationElement.class, documentation, tempEl, desc) );
        
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        
        return documentation;
    }
    
    private ImportElement parseImport(Element importEl,
                                      DescriptionElement desc,
                                      Map wsdlModules) 
                                      throws WSDLException
    {
        ImportElement imp = desc.addImportElement();
        
        String namespaceURI = DOMUtils.getAttribute(importEl, Constants.ATTR_NAMESPACE);
        String locationURI = DOMUtils.getAttribute(importEl, Constants.ATTR_LOCATION);
        
        parseExtensionAttributes(importEl, ImportElement.class, imp, desc);

        if(namespaceURI != null) 
        {
            //TODO handle missing namespace attribute (REQUIRED attr)
            imp.setNamespace(getURI(namespaceURI));
        }
        
        if(locationURI != null)
        {
            //TODO handle missing locationURI (OPTIONAL attr)
            imp.setLocation(getURI(locationURI));
            DescriptionElement importedDesc = 
                getWSDLFromLocation(locationURI, desc, wsdlModules);
            imp.setDescriptionElement(importedDesc);
        }
        
        return imp;
    }
        
    private IncludeElement parseInclude(Element includeEl,
                                        DescriptionElement desc,
                                        Map wsdlModules) 
                                        throws WSDLException
    {
        IncludeElement include = desc.addIncludeElement();
        
        String locationURI = DOMUtils.getAttribute(includeEl, Constants.ATTR_LOCATION);
        
        parseExtensionAttributes(includeEl, IncludeElement.class, include, desc);

        if(locationURI != null)
        {
            include.setLocation(getURI(locationURI));
            DescriptionElement includedDesc = 
                getWSDLFromLocation(locationURI, desc, wsdlModules);
            include.setDescriptionElement(includedDesc);
        }
        
        return include;
    }

    /*
     * TODO Initial schema parsing is specific to XML Schema. 
     * Need generic support for other type systems.
     * Consider extension architecture with serializer/deserializer.
     */
    private TypesElement parseTypes(Element typesEl,
                                    DescriptionElement desc) 
                                    throws WSDLException
    {
        TypesElement types = desc.getTypesElement();
        
        //TODO for now set to W3 XML Schema. Later, add support for non-XML Schema type systems
        types.setTypeSystem(Constants.TYPE_XSD_2001);
        
        parseExtensionAttributes(typesEl, TypesElement.class, types, desc);

        Element tempEl = DOMUtils.getFirstChildElement(typesEl);

        while (tempEl != null)
        {
            QName tempElType = QNameUtils.newQName(tempEl);
            
            //TODO validate element order? <documentation> must be first.
            
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl))
            {
                parseDocumentation(tempEl, desc, types);
            }
            else if (SchemaConstants.XSD_IMPORT_QNAME_LIST.contains(tempElType))
            {
                types.addSchema(parseSchemaImport(tempEl, desc));
            }
            else if (SchemaConstants.XSD_SCHEMA_QNAME_LIST.contains(tempElType))
            {
                types.addSchema(parseSchemaInline(tempEl, desc));
            }
            else
            {
                types.addExtensionElement(
                    parseExtensionElement(TypesElement.class, types, tempEl, desc) );
            }
            
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        
        return types;
    }

    private Schema parseSchemaInline(Element schemaEl,
                                     DescriptionElement desc) 
                                     throws WSDLException
    {
        InlinedSchemaImpl schema = new InlinedSchemaImpl();
        
        schema.setId(DOMUtils.getAttribute(schemaEl, Constants.ATTR_ID));
        
        String tns = DOMUtils.getAttribute(schemaEl, Constants.ATTR_TARGET_NAMESPACE);
        if(tns != null) {
            schema.setNamespace(getURI(tns));
        }
        
        String baseURI = desc.getDocumentBaseURI() != null ?
                         desc.getDocumentBaseURI().toString() : null;
        XmlSchema schemaDef = null;
        
        try {
            XmlSchemaCollection xsc = new XmlSchemaCollection();
            schemaDef = xsc.read(schemaEl, baseURI);
        } 
        catch (XmlSchemaException e) 
        {
            getErrorReporter().reportError(
                    new ErrorLocatorImpl(),  //TODO line&col nos.
                    "WSDL521", 
                    new Object[] {baseURI}, 
                    ErrorReporter.SEVERITY_WARNING,
                    e);
        }
        
        if(schemaDef != null) {
            schema.setSchemaDefinition(schemaDef);
        } else {
            schema.setReferenceable(false);
        }
        
        return schema;
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
    private Schema parseSchemaImport(Element importEl,
                                     DescriptionElement desc) 
                                     throws WSDLException
    {
        ImportedSchemaImpl schema = new ImportedSchemaImpl();
        
        String ns = DOMUtils.getAttribute(importEl, Constants.ATTR_NAMESPACE);
        if(ns != null) {
            schema.setNamespace(getURI(ns));
        }
        
        String sloc = DOMUtils.getAttribute(importEl, SchemaConstants.ATTR_SCHEMA_LOCATION);
        if(sloc != null) {
            schema.setSchemaLocation(getURI(sloc));
        }
        
        if(schema.getNamespace() == null)
        {
            //The namespace attribute is REQUIRED on xs:import, so don't continue.
            schema.setReferenceable(false);
            return schema;
        }
        
        if(schema.getSchemaLocation() == null)
        {
            //This is a namespace-only import, no schema document to be retrieved so don't continue.
            
            /* TODO investigate whether/how to try to resolve the imported namespace to known schema 
             * components from that namespace (e.g. via a URI catalog resolver). Currently, any attempt
             * to resolve a QName against schema components from this namespace will search ALL
             * schemas imported from this namespace (see methods in TypesImpl).
             */
            
            return schema;
        }
        
        //Now try to retrieve the schema import using schemaLocation
        
        Document importedSchemaDoc = null;
        Element schemaEl = null;
        URI contextURI = null;
        String schemaLoc = null;
        URL url = null;
        
        try 
        {
            contextURI = desc.getDocumentBaseURI();
            URL contextURL = (contextURI != null) ? contextURI.toURL() : null;
            schemaLoc = schema.getSchemaLocation().toString();
            url = StringUtils.getURL(contextURL, schemaLoc);
                    
        } catch (MalformedURLException e) {
 
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
        
        if(schemaDef == null)
        {
            //not previously imported, so retrieve it now.
            try {
                importedSchemaDoc = getDocument(new InputSource(schemaURL), schemaURL);
                
            } catch (IOException e4) {
                
                //schema retrieval failed (e.g. 'not found')
                getErrorReporter().reportError(
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL504", 
                        new Object[] {schemaURL}, 
                        ErrorReporter.SEVERITY_WARNING, 
                        e4);
                
                //cannot continue without an imported schema
                schema.setReferenceable(false);
                return schema;
            }
            
            schemaEl = importedSchemaDoc.getDocumentElement();
            
            try {
                String baseLoc = contextURI != null ? contextURI.toString() : null;
                XmlSchemaCollection xsc = new XmlSchemaCollection();
                schemaDef = xsc.read(schemaEl, baseLoc);
                fImportedSchemas.put(schemaURL, schemaDef);
            } 
            catch (XmlSchemaException e) 
            {
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
        } else {
            schema.setReferenceable(false);
        }
        
        return schema;
    }
    
    private InterfaceElement parseInterface(Element interfaceEl,
                                            DescriptionElement desc) 
                                            throws WSDLException
    {
        InterfaceElement intface = desc.addInterfaceElement();

        String name = DOMUtils.getAttribute(interfaceEl, Constants.ATTR_NAME);
        
        if(name != null)
        {
            intface.setName(new NCName(name));
        }
        
        String styleDefault = DOMUtils.getAttribute(interfaceEl, Constants.ATTR_STYLE_DEFAULT);
        if(styleDefault != null)
        {
            List stringList = StringUtils.parseNMTokens(styleDefault);
            String uriString = null;
            Iterator it = stringList.iterator();
            while(it.hasNext())
            {
                uriString = (String)it.next();
                intface.addStyleDefaultURI(getURI(uriString));
            }
        }
        
        String extendsAtt = DOMUtils.getAttribute(interfaceEl, Constants.ATTR_EXTENDS);
        if(extendsAtt != null)
        {
            List stringList = StringUtils.parseNMTokens(extendsAtt);
            String qnString = null;
            Iterator it = stringList.iterator();
            while(it.hasNext())
            {
                qnString = (String)it.next();
                intface.addExtendedInterfaceName(DOMUtils.getQName(qnString, interfaceEl, desc));
            }
        }
        
        parseExtensionAttributes(interfaceEl, InterfaceElement.class, intface, desc);
        
        /* Parse the child elements of <interface>. 
         * As per WSDL 2.0 spec, they must be in the following order if present:
         * <documentation>
         * <fault> <operation> <feature> <property> or extension elements in any order
         * TODO validate that the elements are in correct order
         */ 

        Element tempEl = DOMUtils.getFirstChildElement(interfaceEl);

        while (tempEl != null)
        {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl))
            {
                parseDocumentation(tempEl, desc, intface);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FAULT, tempEl))
            {
                parseInterfaceFault(tempEl, desc, intface);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_OPERATION, tempEl))
            {
                parseInterfaceOperation(tempEl, desc, intface);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, tempEl))
            {
                parseFeature(tempEl, desc, intface);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, tempEl))
            {
                parseProperty(tempEl, desc, intface);
            }
            else
            {
                intface.addExtensionElement(
                    parseExtensionElement(InterfaceElement.class, intface, tempEl, desc) );
            }
            
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        
        return intface;
    }

    /* Parse the attributes and child elements of interface <fault>. 
     * As per WSDL 2.0 spec, the child elements must be in the following order if present:
     * <documentation>
     * <feature> <property> or extension elements in any order
     * 
     * TODO validate that the elements are in correct order
     */ 
    private InterfaceFaultElement parseInterfaceFault(
                                             Element faultEl,
                                             DescriptionElement desc,
                                             InterfaceElement parent) 
                                             throws WSDLException
    {
        InterfaceFaultElement fault = parent.addInterfaceFaultElement();
        fault.setParentElement(parent);
        
        String name = DOMUtils.getAttribute(faultEl, Constants.ATTR_NAME);
        if(name != null)
        {
            fault.setName(new NCName(name));
        }
        
        String element = DOMUtils.getAttribute(faultEl, Constants.ATTR_ELEMENT);
        if(element != null)
        {
            try {
                QName qname = DOMUtils.getQName(element, faultEl, desc);
                fault.setElementName(qname);
            } catch (WSDLException e) {
                getErrorReporter().reportError( 
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL505",
                        new Object[] {element, QNameUtils.newQName(faultEl)}, 
                        ErrorReporter.SEVERITY_ERROR);
            }
        }
        
        parseExtensionAttributes(faultEl, InterfaceFaultElement.class, fault, desc);

        Element tempEl = DOMUtils.getFirstChildElement(faultEl);

        while (tempEl != null)
        {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl))
            {
                parseDocumentation(tempEl, desc, fault);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, tempEl))
            {
                parseFeature(tempEl, desc, fault);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, tempEl))
            {
                parseProperty(tempEl, desc, fault);
            }
            else
            {
                fault.addExtensionElement(
                    parseExtensionElement(InterfaceFaultElement.class, fault, tempEl, desc) );
            }
            
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }

        return fault;
    }
    
    private InterfaceOperationElement parseInterfaceOperation(
                                                 Element operEl, 
                                                 DescriptionElement desc,
                                                 InterfaceElement parent) 
                                                 throws WSDLException
    {
        InterfaceOperationElement oper = parent.addInterfaceOperationElement();
        oper.setParentElement(parent);
        
        String name = DOMUtils.getAttribute(operEl, Constants.ATTR_NAME);
        if(name != null)
        {
            oper.setName(new NCName(name));
        }
        
        String style = DOMUtils.getAttribute(operEl, Constants.ATTR_STYLE);
        if(style != null)
        {
            List stringList = StringUtils.parseNMTokens(style);
            String uriString = null;
            Iterator it = stringList.iterator();
            while(it.hasNext())
            {
                uriString = (String)it.next();
                oper.addStyleURI(getURI(uriString));
            }
        }
        
        String pat = DOMUtils.getAttribute(operEl, Constants.ATTR_PATTERN);
        if(pat != null)
        {
            oper.setPattern(getURI(pat));
        }
       
        parseExtensionAttributes(operEl, InterfaceOperationElement.class, oper, desc);
        
        /* Parse the child elements of interface <operation>. 
         * As per WSDL 2.0 spec, they must be in the following order if present:
         * <documentation> 
         * <input> <output> <infault> <outfault> <feature> <property> or extension elements in any order
         * TODO validate that the elements are in correct order
         */ 

        Element tempEl = DOMUtils.getFirstChildElement(operEl);

        while (tempEl != null)
        {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl))
            {
                parseDocumentation(tempEl, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, tempEl))
            {
                parseFeature(tempEl, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, tempEl))
            {
                parseProperty(tempEl, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_INPUT, tempEl))
            {
                parseInterfaceMessageReference(tempEl, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_OUTPUT, tempEl))
            {
                parseInterfaceMessageReference(tempEl, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_INFAULT, tempEl))
            {
                parseInterfaceFaultReference(tempEl, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_OUTFAULT, tempEl))
            {
                parseInterfaceFaultReference(tempEl, desc, oper);
            }
            else
            {
                oper.addExtensionElement(
                    parseExtensionElement(InterfaceOperationElement.class, oper, tempEl, desc) );
            }
            
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }

        return oper;
    }
    
    private InterfaceFaultReferenceElement parseInterfaceFaultReference(
                                                 Element faultRefEl,
                                                 DescriptionElement desc,
                                                 InterfaceOperationElement parent)
                                                 throws WSDLException
    {
        InterfaceFaultReferenceElement faultRef = parent.addInterfaceFaultReferenceElement();
        faultRef.setParentElement(parent);
        
        if(Constants.ELEM_INFAULT.equals(faultRefEl.getLocalName())) {
            faultRef.setDirection(Direction.IN);
        } 
        else if(Constants.ELEM_OUTFAULT.equals(faultRefEl.getLocalName())){
            faultRef.setDirection(Direction.OUT);
        }
        
        String ref = DOMUtils.getAttribute(faultRefEl, Constants.ATTR_REF);
        if(ref != null)
        {
            try {
                QName qname = DOMUtils.getQName(ref, faultRefEl, desc);
                faultRef.setRef(qname);
            } catch (WSDLException e) {
                getErrorReporter().reportError( 
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL505",
                        new Object[] {ref, QNameUtils.newQName(faultRefEl)}, 
                        ErrorReporter.SEVERITY_ERROR);
            }
        }
        
        String msgLabel = DOMUtils.getAttribute(faultRefEl, Constants.ATTR_MESSAGE_LABEL);
        if(msgLabel != null)
        {
            if(msgLabel.equals(MessageLabel.IN.toString())) {
                faultRef.setMessageLabel(MessageLabel.IN);
            } else if(msgLabel.equals(MessageLabel.OUT.toString())) {
                faultRef.setMessageLabel(MessageLabel.OUT);
            } else {
                //invalid value, but capture it anyway.
                faultRef.setMessageLabel(MessageLabel.invalidValue(msgLabel));
            }
        }
        
        parseExtensionAttributes(faultRefEl, InterfaceFaultReferenceElement.class, faultRef, desc);
        
        Element tempEl = DOMUtils.getFirstChildElement(faultRefEl);
        
        while (tempEl != null)
        {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl))
            {
                parseDocumentation(tempEl, desc, faultRef);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, tempEl))
            {
                parseFeature(tempEl, desc, faultRef);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, tempEl))
            {
                parseProperty(tempEl, desc, faultRef);
            }
            else
            {
                faultRef.addExtensionElement(
                    parseExtensionElement(InterfaceFaultReferenceElement.class, faultRef, tempEl, desc) );
            }
            
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        
        return faultRef;
    }

    private InterfaceMessageReferenceElement parseInterfaceMessageReference(
                                                 Element msgRefEl,
                                                 DescriptionElement desc,
                                                 InterfaceOperationElement parent)
                                                 throws WSDLException
    {
        InterfaceMessageReferenceElement message = parent.addInterfaceMessageReferenceElement();
        message.setParentElement(parent);
        
        if(Constants.ELEM_INPUT.equals(msgRefEl.getLocalName())) {
            message.setDirection(Direction.IN);
        } 
        else if(Constants.ELEM_OUTPUT.equals(msgRefEl.getLocalName())) {
            message.setDirection(Direction.OUT);
        }
        
        String msgLabel = DOMUtils.getAttribute(msgRefEl, Constants.ATTR_MESSAGE_LABEL);
        if(msgLabel != null) 
        {
            if(msgLabel.equals(MessageLabel.IN.toString())) {
                message.setMessageLabel(MessageLabel.IN);
            } else if(msgLabel.equals(MessageLabel.OUT.toString())) {
                message.setMessageLabel(MessageLabel.OUT);
            } else {
                //invalid value, but capture it anyway.
                message.setMessageLabel(MessageLabel.invalidValue(msgLabel));
            }
        }
        else
        {
            //TODO this is a temp fix, correct action to use MEP to determine default
            if(message.getDirection().equals(Direction.IN))
            {
                message.setMessageLabel(MessageLabel.IN);
            }
            else
            {
                message.setMessageLabel(MessageLabel.OUT);
            }
        }
        
        String element = DOMUtils.getAttribute(msgRefEl, Constants.ATTR_ELEMENT);
        if(element != null)
        {
            if(element.equals(Constants.NMTOKEN_ANY) ||
               element.equals(Constants.NMTOKEN_NONE) ||
               element.equals(Constants.NMTOKEN_OTHER))
            {
                message.setMessageContentModel(element);
            }
            else
            {
                //element is not #any, #none or #other, so it must be an element qname
                message.setMessageContentModel(Constants.NMTOKEN_ELEMENT);
                try {
                    QName qname = DOMUtils.getQName(element, msgRefEl, desc);
                    message.setElementName(qname);
                } catch (WSDLException e) {
                    getErrorReporter().reportError( 
                            new ErrorLocatorImpl(),  //TODO line&col nos.
                            "WSDL505",
                            new Object[] {element, QNameUtils.newQName(msgRefEl)}, 
                            ErrorReporter.SEVERITY_ERROR);
                }
            }
        }
        else
        {
            //Per mapping defined in WSDL 2.0 Part 2 spec section 2.5.3,
            //if element attribute not present, message content model is #other
            message.setMessageContentModel(Constants.NMTOKEN_OTHER);
        }

        parseExtensionAttributes(msgRefEl, InterfaceMessageReferenceElement.class, message, desc);

        Element tempEl = DOMUtils.getFirstChildElement(msgRefEl);

        while (tempEl != null)
        {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl))
            {
                parseDocumentation(tempEl, desc, message);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, tempEl))
            {
                parseFeature(tempEl, desc, message);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, tempEl))
            {
                parseProperty(tempEl, desc, message);
            }
            else
            {
                message.addExtensionElement(
                    parseExtensionElement(InterfaceMessageReferenceElement.class, message, tempEl, desc) );
            }
            
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        
        return message;
    }

    private BindingElement parseBinding(Element bindEl,
                                        DescriptionElement desc)
                                        throws WSDLException
    {
        BindingElement binding = desc.addBindingElement();

        String name = DOMUtils.getAttribute(bindEl, Constants.ATTR_NAME);
        if(name != null)
        {
            binding.setName(new NCName(name));
        }
        
        QName intfaceQN = null;
        String intface = DOMUtils.getAttribute(bindEl, Constants.ATTR_INTERFACE);
        if(intface != null)
        {
            try {
                intfaceQN = DOMUtils.getQName(intface, bindEl, desc);
                binding.setInterfaceName(intfaceQN);
            } catch (WSDLException e) {
                getErrorReporter().reportError( 
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL505",
                        new Object[] {intface, QNameUtils.newQName(bindEl)}, 
                        ErrorReporter.SEVERITY_ERROR);
            }
        }
        
        String type = DOMUtils.getAttribute(bindEl, Constants.ATTR_TYPE);
        if(type != null) {
            binding.setType(getURI(type));
        }
        
        parseExtensionAttributes(bindEl, BindingElement.class, binding, desc);
        
        /* Parse the child elements of <binding>. 
         * As per WSDL 2.0 spec, they must be in the following order if present:
         * <documentation>
         * <fault> <operation> <feature> <property> or extension elements in any order
         * TODO validate that the elements are in correct order
         */ 

        Element tempEl = DOMUtils.getFirstChildElement(bindEl);

        while (tempEl != null)
        {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl))
            {
                parseDocumentation(tempEl, desc, binding);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FAULT, tempEl))
            {
                parseBindingFault(tempEl, desc, binding);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_OPERATION, tempEl))
            {
                parseBindingOperation(tempEl, desc, binding);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, tempEl))
            {
                parseFeature(tempEl, desc, binding);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, tempEl))
            {
                parseProperty(tempEl, desc, binding);
            }
            else
            {
                binding.addExtensionElement(
                    parseExtensionElement(BindingElement.class, binding, tempEl, desc) );
            }
            
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        
        return binding;
    }

    private BindingFaultElement parseBindingFault(Element bindFaultEl,
                                                  DescriptionElement desc,
                                                  BindingElement parent)
                                                  throws WSDLException
    {
        BindingFaultElement fault = parent.addBindingFaultElement();
        fault.setParentElement(parent);
        
        QName intFltQN = null;
        String ref = DOMUtils.getAttribute(bindFaultEl, Constants.ATTR_REF);
        if(ref != null)
        {
            try {
                intFltQN = DOMUtils.getQName(ref, bindFaultEl, desc);
                fault.setRef(intFltQN);
            } catch (WSDLException e) {
                getErrorReporter().reportError( 
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL505",
                        new Object[] {ref, QNameUtils.newQName(bindFaultEl)}, 
                        ErrorReporter.SEVERITY_ERROR);
            }
        }
        
        parseExtensionAttributes(bindFaultEl, BindingFaultElement.class, fault, desc);
        
        /* Parse the child elements of binding <fault>. 
         * As per WSDL 2.0 spec, they must be in the following order if present:
         * <documentation> 
         * <feature> <property> or extension elements in any order
         * TODO validate that the elements are in correct order
         */ 

        Element tempEl = DOMUtils.getFirstChildElement(bindFaultEl);

        while (tempEl != null)
        {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl))
            {
                parseDocumentation(tempEl, desc, fault);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, tempEl))
            {
                parseFeature(tempEl, desc, fault);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, tempEl))
            {
                parseProperty(tempEl, desc, fault);
            }
            else
            {
                fault.addExtensionElement(
                    parseExtensionElement(BindingFaultElement.class, fault, tempEl, desc) );
            }
            
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        
        return fault;
    }

    private BindingOperationElement parseBindingOperation(
                                                 Element bindOpEl,
                                                 DescriptionElement desc,
                                                 BindingElement parent)
                                                 throws WSDLException
    {
        BindingOperationElement oper = parent.addBindingOperationElement();
        oper.setParentElement(parent);
        
        QName refQN = null;
        String ref = DOMUtils.getAttribute(bindOpEl, Constants.ATTR_REF);
        if(ref != null)
        {
            try {
                refQN = DOMUtils.getQName(ref, bindOpEl, desc);
                oper.setRef(refQN);
            } catch (WSDLException e) {
                getErrorReporter().reportError( 
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL505",
                        new Object[] {ref, QNameUtils.newQName(bindOpEl)}, 
                        ErrorReporter.SEVERITY_ERROR);
            }
        }
        
        parseExtensionAttributes(bindOpEl, BindingOperationElement.class, oper, desc);
        
        /* Parse the child elements of binding <operation>. 
         * As per WSDL 2.0 spec, they must be in the following order if present:
         * <documentation> 
         * <input> <output> <infault> <outfault> <feature> <property> or extension elements in any order
         * TODO validate that the elements are in correct order
         */ 

        Element tempEl = DOMUtils.getFirstChildElement(bindOpEl);

        while (tempEl != null)
        {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl))
            {
                parseDocumentation(tempEl, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, tempEl))
            {
                parseFeature(tempEl, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, tempEl))
            {
                parseProperty(tempEl, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_INPUT, tempEl))
            {
                parseBindingMessageReference(tempEl, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_OUTPUT, tempEl))
            {
                parseBindingMessageReference(tempEl, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_INFAULT, tempEl))
            {
                parseBindingFaultReference(tempEl, desc, oper);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_OUTFAULT, tempEl))
            {
                parseBindingFaultReference(tempEl, desc, oper);
            }
            else
            {
                oper.addExtensionElement(
                    parseExtensionElement(BindingOperationElement.class, oper, tempEl, desc) );
            }
            
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        
        return oper;
    }

    private BindingFaultReferenceElement parseBindingFaultReference(
                                                  Element faultRefEl,
                                                  DescriptionElement desc,
                                                  BindingOperationElement parent)
                                                  throws WSDLException
    {
        BindingFaultReferenceElement faultRef = parent.addBindingFaultReferenceElement();
        faultRef.setParentElement(parent);
        
        QName refQN = null;
        String ref = DOMUtils.getAttribute(faultRefEl, Constants.ATTR_REF);
        if(ref != null)
        {
            try {
                refQN = DOMUtils.getQName(ref, faultRefEl, desc);
                faultRef.setRef(refQN);
            } catch (WSDLException e) {
                getErrorReporter().reportError( 
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL505",
                        new Object[] {ref, QNameUtils.newQName(faultRefEl)}, 
                        ErrorReporter.SEVERITY_ERROR);
            }
        }
        
        String msgLabel = DOMUtils.getAttribute(faultRefEl, Constants.ATTR_MESSAGE_LABEL);
        if(msgLabel != null)
        {
            if(msgLabel.equals(MessageLabel.IN.toString())) {
                faultRef.setMessageLabel(MessageLabel.IN);
            } else if(msgLabel.equals(MessageLabel.OUT.toString())) {
                faultRef.setMessageLabel(MessageLabel.OUT);
            } else {
                //invalid value, but capture it anyway.
                faultRef.setMessageLabel(MessageLabel.invalidValue(msgLabel));
            }
        }
        
        parseExtensionAttributes(faultRefEl, BindingFaultReferenceElement.class, faultRef, desc);
        
        /* Parse the child elements of binding operation <infault> or <outfault>. 
         * As per WSDL 2.0 spec, they must be in the following order if present:
         * <documentation> 
         * <feature> <property> or extension elements in any order
         * TODO validate that the elements are in correct order
         */ 

        Element tempEl = DOMUtils.getFirstChildElement(faultRefEl);
        
        while (tempEl != null)
        {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl))
            {
                parseDocumentation(tempEl, desc, faultRef);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, tempEl))
            {
                parseFeature(tempEl, desc, faultRef);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, tempEl))
            {
                parseProperty(tempEl, desc, faultRef);
            }
            else
            {
                faultRef.addExtensionElement(
                    parseExtensionElement(BindingFaultReferenceElement.class, faultRef, tempEl, desc) );
            }
            
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        
        return faultRef;
    }
    
    private BindingMessageReferenceElement parseBindingMessageReference(
                                                 Element msgRefEl,
                                                 DescriptionElement desc,
                                                 BindingOperationElement parent)
                                                 throws WSDLException
    {
        BindingMessageReferenceElement message = parent.addBindingMessageReferenceElement();
        message.setParentElement(parent);
        
        if(Constants.ELEM_INPUT.equals(msgRefEl.getLocalName())) {
            message.setDirection(Direction.IN);
        } 
        else if(Constants.ELEM_OUTPUT.equals(msgRefEl.getLocalName())) {
            message.setDirection(Direction.OUT);
        }
        
        String msgLabel = DOMUtils.getAttribute(msgRefEl, Constants.ATTR_MESSAGE_LABEL);
        if(msgLabel != null) 
        {
            if(msgLabel.equals(MessageLabel.IN.toString())) {
                message.setMessageLabel(MessageLabel.IN);
            } else if(msgLabel.equals(MessageLabel.OUT.toString())) {
                message.setMessageLabel(MessageLabel.OUT);
            } else {
                //invalid value, but capture it anyway.
                message.setMessageLabel(MessageLabel.invalidValue(msgLabel));
            }
        }
        else
        {
            //TODO this is a temp fix, correct action to use MEP to determine default
            if(message.getDirection().equals(Direction.IN))
            {
                message.setMessageLabel(MessageLabel.IN);
            }
            else
            {
                message.setMessageLabel(MessageLabel.OUT);
            }
        }
        
        parseExtensionAttributes(msgRefEl, BindingMessageReferenceElement.class, message, desc);

        /* Parse the child elements of binding operation <input> or <output>. 
         * As per WSDL 2.0 spec, they must be in the following order if present:
         * <documentation> 
         * <feature> <property> or extension elements in any order
         * TODO validate that the elements are in correct order
         */ 

        Element tempEl = DOMUtils.getFirstChildElement(msgRefEl);

        while (tempEl != null)
        {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl))
            {
                parseDocumentation(tempEl, desc, message);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, tempEl))
            {
                parseFeature(tempEl, desc, message);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, tempEl))
            {
                parseProperty(tempEl, desc, message);
            }
            else
            {
                message.addExtensionElement(
                    parseExtensionElement(BindingMessageReferenceElement.class, message, tempEl, desc) );
            }
            
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        
        return message;
    }

    private ServiceElement parseService(Element serviceEl,
                                        DescriptionElement desc)
                                        throws WSDLException
    {
        ServiceElement service = desc.addServiceElement();

        String name = DOMUtils.getAttribute(serviceEl, Constants.ATTR_NAME);
        if(name != null)
        {
            service.setName(new NCName(name));
        }
        
        QName intfaceQN = null;
        String intface = DOMUtils.getAttribute(serviceEl, Constants.ATTR_INTERFACE);
        if(intface != null)
        {
            try {
                intfaceQN = DOMUtils.getQName(intface, serviceEl, desc);
                service.setInterfaceName(intfaceQN);
            } catch (WSDLException e) {
                getErrorReporter().reportError( 
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL505",
                        new Object[] {intface, QNameUtils.newQName(serviceEl)}, 
                        ErrorReporter.SEVERITY_ERROR);
            }
        }
        
        parseExtensionAttributes(serviceEl, ServiceElement.class, service, desc);

        /* Parse the child elements of <service>. 
         * As per WSDL 2.0 spec, they must be in the following order if present:
         * <documentation> 
         * <endpoint>
         * <feature> <property> or extension elements in any order
         * TODO validate that the elements are in correct order
         */ 

        Element tempEl = DOMUtils.getFirstChildElement(serviceEl);

        while (tempEl != null)
        {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl))
            {
                parseDocumentation(tempEl, desc, service);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_ENDPOINT, tempEl))
            {
                parseEndpoint(tempEl, desc, service);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, tempEl))
            {
                parseFeature(tempEl, desc, service);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, tempEl))
            {
                parseProperty(tempEl, desc, service);
            }
            else
            {
                service.addExtensionElement(
                    parseExtensionElement(ServiceElement.class, service, tempEl, desc) );
            }
            
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        
        return service;
    }
                                        
    private EndpointElement parseEndpoint(Element endpointEl,
                                          DescriptionElement desc,
                                          ServiceElement parent)
                                          throws WSDLException
    {
        EndpointElement endpoint = parent.addEndpointElement();
        endpoint.setParentElement(parent);

        String name = DOMUtils.getAttribute(endpointEl, Constants.ATTR_NAME);
        if(name != null)
        {
            endpoint.setName(new NCName(name));
        }
        
        QName bindingQN = null;
        String binding = DOMUtils.getAttribute(endpointEl, Constants.ATTR_BINDING);
        if(binding != null)
        {
            try {
                bindingQN = DOMUtils.getQName(binding, endpointEl, desc);
                endpoint.setBindingName(bindingQN);
            } catch (WSDLException e) {
                getErrorReporter().reportError( 
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL505",
                        new Object[] {binding, QNameUtils.newQName(endpointEl)}, 
                        ErrorReporter.SEVERITY_ERROR);
            }
        }
        
        String address = 
            DOMUtils.getAttribute(endpointEl, Constants.ATTR_ADDRESS);
        
        if(address != null)
        {
            endpoint.setAddress(getURI(address));
        }

        parseExtensionAttributes(endpointEl, EndpointElement.class, endpoint, desc);
        
        /* Parse the child elements of <endpoint>. 
         * As per WSDL 2.0 spec, they must be in the following order if present:
         * <documentation> 
         * <feature> <property> or extension elements in any order
         * TODO validate that the elements are in correct order
         */ 

        Element tempEl = DOMUtils.getFirstChildElement(endpointEl);

        while (tempEl != null)
        {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl))
            {
                parseDocumentation(tempEl, desc, endpoint);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_FEATURE, tempEl))
            {
                parseFeature(tempEl, desc, endpoint);
            }
            else if (QNameUtils.matches(Constants.Q_ELEM_PROPERTY, tempEl))
            {
                parseProperty(tempEl, desc, endpoint);
            }
            else
            {
                endpoint.addExtensionElement(
                    parseExtensionElement(ServiceElement.class, endpoint, tempEl, desc) );
            }
            
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        
        return endpoint;
    }

    private FeatureElement parseFeature(Element featEl, 
                                        DescriptionElement desc,
                                        ConfigurableElement parent) 
                                        throws WSDLException
    {
        FeatureElement feature = parent.addFeatureElement();
        feature.setParentElement(parent);
        
        String ref = DOMUtils.getAttribute(featEl, Constants.ATTR_REF);
        if(ref != null)
        {
            feature.setRef(getURI(ref));
        }
        
        String req = DOMUtils.getAttribute(featEl, Constants.ATTR_REQUIRED);
        feature.setRequired(Constants.VALUE_TRUE.equals(req) ? true : false);
        //TODO t.b.c. what if attr value is not 'true' or 'false'? (eg, missing, mispelt or not lower case.
        
        parseExtensionAttributes(featEl, FeatureElement.class, feature, desc);
        
        /* Parse the child elements of the <feature> element. 
         * As per WSDL 2.0 spec, they must be in the following order if present:
         * <documentation>
         * extension elements.
         * 
         * TODO validate that the elements are in correct order
         */ 
        
        Element tempEl = DOMUtils.getFirstChildElement(featEl);
        
        while (tempEl != null)
        {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl))
            {
                parseDocumentation(tempEl, desc, feature);
            }
            else
            {
                feature.addExtensionElement(
                    parseExtensionElement(FeatureElement.class, feature, tempEl, desc) );
            }
            
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        
        return feature;
    }

    /* ******************************************************************
     * Parse the attributes and child elements of the <property> element.
     *  
     * As per WSDL 2.0 spec, they must be in the following order if present:
     * <documentation>
     * <value> or <constraint>
     * extension elements.
     * 
     * TODO validate that the elements are in correct order
     * 
     * Child elements may include either a <value> or a <constraint>,
     * but not both. If a <value> element is present, a <constraint> 
     * may only be present if it contains the NMToken "#value", which
     * indicates that this <property> specifies a value, not a constraint.
     * 
     * This method will parse all child elements of <property> without 
     * checking for any erroneous use of  <value> and <constraint>.
     * This will be done later by validation, if it's enabled. 
     * If the NMToken "#value" is present in a <constraint> element,
     * this will be flagged with a boolean field in PropertyElement 
     * and the constraint field will be set to null. 
     * 
     */ 
    private PropertyElement parseProperty(Element propEl, 
                                          DescriptionElement desc,
                                          ConfigurableElement parent)
                                          throws WSDLException
    {
        PropertyElement property = parent.addPropertyElement();
        property.setParentElement(parent);
        
        String ref = DOMUtils.getAttribute(propEl, Constants.ATTR_REF);
        if(ref != null)
        {
            property.setRef(getURI(ref));
        }
        
        parseExtensionAttributes(propEl, PropertyElement.class, property, desc);
        
        Element tempEl = DOMUtils.getFirstChildElement(propEl);

        while (tempEl != null)
        {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl))
            {
                parseDocumentation(tempEl, desc, property);
            }
            else if(QNameUtils.matches(Constants.Q_ELEM_VALUE, tempEl))
            {
                //the property value consists of the child info items of <value>
                NodeList nodeList = tempEl.getChildNodes();
                property.setValue(nodeList);
            }
            else if(QNameUtils.matches(Constants.Q_ELEM_CONSTRAINT, tempEl))
            {
                //TODO t.b.c. assume <constraint> has just '#value' or a qname 
                //and don't check for extraneous text or child elements?
                
                Node node = tempEl.getFirstChild();
                if(node != null && node instanceof Text)
                {
                    Text textNode = (Text)node;
                    String textValue = textNode.getData().trim();
                    if(textValue.length() > 0)
                    {
                        if(textValue.equals(Constants.NMTOKEN_VALUE))
                        {
                            property.setHasValueToken(true);
                        }
                        else 
                        {
                            try {
                                QName qname = DOMUtils.getQName(textValue, tempEl, desc);
                                property.setConstraintName(qname);
                            } catch (WSDLException e) {
                                getErrorReporter().reportError(
                                        new ErrorLocatorImpl(),  //TODO line&col nos.
                                        "WSDL505",
                                        new Object[] {textValue, QNameUtils.newQName(tempEl)},
                                        ErrorReporter.SEVERITY_ERROR,
                                        e);
                            }
                        }
                    }
                }
            }
            else
            {
                property.addExtensionElement(
                    parseExtensionElement(PropertyElement.class, property, tempEl, desc) );
            }
            
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        
        return property;
    }
    
    private void parseExtensionAttributes(Element domEl, 
                                          Class wsdlClass, 
                                          WSDLElement wsdlObj,
                                          DescriptionElement desc)
                                          throws WSDLException
    {
        NamedNodeMap nodeMap = domEl.getAttributes();
        int length = nodeMap.getLength();
        
        for (int i = 0; i < length; i++)
        {
            Attr domAttr = (Attr)nodeMap.item(i);
            String localName = domAttr.getLocalName();
            String namespaceURI = domAttr.getNamespaceURI();
            String prefix = domAttr.getPrefix();
            QName attrType = new QName(namespaceURI, localName, (prefix != null ? prefix : ""));
            
            if (namespaceURI != null && !namespaceURI.equals(Constants.NS_URI_WSDL20))
            {
                if (!namespaceURI.equals(Constants.NS_URI_XMLNS) && 
                    !namespaceURI.equals(Constants.NS_URI_XSI))  //TODO handle xsi attrs elsewhere, without need to register
                {
                    //TODO reg namespaces at appropriate element scope, not just at desc.
                    //DOMUtils.registerUniquePrefix(prefix, namespaceURI, desc);
                    
                    XMLAttr xmlAttr = null;
                    ExtensionRegistry extReg = desc.getExtensionRegistry();
                    if (extReg != null)
                    {
                        xmlAttr = extReg.createExtAttribute(wsdlClass, attrType);
                        if(xmlAttr != null) //TODO use an 'UnknownAttr' class in place of null
                        {
                            String attrValue = domAttr.getValue();
                            xmlAttr.init(domEl, attrType, attrValue);
                            wsdlObj.setExtensionAttribute(attrType, xmlAttr);
                        }
                    }
                    else
                    {
                        //This reader cannot handle extensions, so stop.
                        break;
                    }
                }
                else
                {
                    //TODO parse xmlns namespace declarations - here or elsewhere?
                }
            }
            else
            {
                //TODO confirm non-native attrs in WSDL 2.0 namespace will be detected by schema validation,
                //so no need to handle error here.
            }
        }
        
    }
    
    private ExtensionElement parseExtensionElement(Class parentType,
                                                   WSDLElement parent,
                                                   Element el,
                                                   DescriptionElement desc)
                                                   throws WSDLException
    {
        QName elementType = QNameUtils.newQName(el);
        String namespaceURI = el.getNamespaceURI();
        
        try
        {
            if (namespaceURI == null || namespaceURI.equals(Constants.NS_URI_WSDL20))
            {
                getErrorReporter().reportError(
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL520",
                        new Object[] {elementType, parentType.getName()},
                        ErrorReporter.SEVERITY_ERROR);
                return null;
            }
            
            ExtensionRegistry extReg = desc.getExtensionRegistry();
            
            if (extReg == null)
            {
                getErrorReporter().reportError(
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL514",
                        new Object[] {elementType, parentType.getName()},
                        ErrorReporter.SEVERITY_ERROR);
                return null;
            }
            
            ExtensionDeserializer extDS = extReg.queryDeserializer(parentType,
                    elementType);
            
            return extDS.unmarshall(parentType, parent, elementType, el, desc, extReg);
        }
        catch (WSDLException e)
        {
            if (e.getLocation() == null)
            {
                e.setLocation(XPathUtils.getXPathExprFromNode(el));
            }
            
            throw e;
        }
    }
    

    /* ************************************************************
     *  Utility/helper methods
     * ************************************************************/
    
    /**
     * Check the actual element encountered against the expected qname
     * 
     * @param el actual element encountered
     * @param qname expected element's qname
     * @throws WSDLException
     */
    private void checkElementName(Element el, QName qname)
    throws WSDLException
    {
        if (!QNameUtils.matches(qname, el))
        {
            getErrorReporter().reportError(
                new ErrorLocatorImpl(),  //TODO line&col nos.
                "WSDL501", 
                new Object[] {qname, QNameUtils.newQName(el)}, 
                ErrorReporter.SEVERITY_FATAL_ERROR);
                
            //TODO wsdlExc.setLocation(XPathUtils.getXPathExprFromNode(el));
                
        }
    }
        
    private Document getDocument(InputSource inputSource, String desc)
                                 throws WSDLException, IOException
    {
        //TODO use 'desc' URL in any error message(s) for problem resolution.
        
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            
//        factory.setNamespaceAware(true);
        
        DOMParser parser = new DOMParser();
        try
        {
        	parser.setFeature(org.apache.xerces.impl.Constants.SAX_FEATURE_PREFIX + org.apache.xerces.impl.Constants.NAMESPACES_FEATURE, true);
        	parser.setFeature(org.apache.xerces.impl.Constants.SAX_FEATURE_PREFIX + org.apache.xerces.impl.Constants.NAMESPACE_PREFIXES_FEATURE, true);
        }
        catch(SAXNotRecognizedException e)
        {
        	
        }
        catch(SAXNotSupportedException e)
        {
        	
        }
        
        // Enable validation on the XML parser if it has been enabled 
        // for the Woden parser.
        if(features.getValue(WSDLReader.FEATURE_VALIDATION))
        {
        	//factory.setValidating(true);
        	try
        	{
        		parser.setFeature(org.apache.xerces.impl.Constants.SAX_FEATURE_PREFIX + org.apache.xerces.impl.Constants.VALIDATION_FEATURE, true);
        		parser.setFeature(org.apache.xerces.impl.Constants.XERCES_FEATURE_PREFIX + org.apache.xerces.impl.Constants.SCHEMA_VALIDATION_FEATURE, true);
        		// TODO: This external schema location should be removed once an URI resolution framework
        		// with a catalog is added to Woden.
        		parser.setProperty(org.apache.xerces.impl.Constants.XERCES_PROPERTY_PREFIX + org.apache.xerces.impl.Constants.SCHEMA_LOCATION, "http://www.w3.org/2006/01/wsdl http://www.w3.org/2006/01/wsdl/wsdl20.xsd http://www.w3.org/2006/01/wsdl-extensions http://www.w3.org/2006/01/wsdl-extensions.xsd http://www.w3.org/2001/XMLSchema http://www.w3.org/2001/XMLSchema.xsd");
        	}
            catch(SAXNotRecognizedException e)
            {
            	System.out.println("validation not supported by parser.");
            }
            catch(SAXNotSupportedException e)
            {
            	
            }
        }
        else
        {
        	//factory.setValidating(false);
        }
            
        Document doc = null;
            
        try {
              
            //DocumentBuilder builder = factory.newDocumentBuilder();
            //builder.getDOMImplementation().hasFeature();
            //builder.setErrorHandler(new ErrorHandlerWrapper(getErrorReporter()));
            //builder.setEntityResolver(new DefaultHandler());
            //doc = builder.parse(inputSource);
        	parser.parse(inputSource);
        	doc = parser.getDocument();
                
        } 
//        catch (ParserConfigurationException e) 
//        {
//            String msg = getErrorReporter().getFormattedMessage("WSDL002", new Object[] {"XML"});
//            throw new WSDLException(WSDLException.CONFIGURATION_ERROR, msg, e);
//        } 
        catch (SAXException e) 
        {
            getErrorReporter().reportError(
                new ErrorLocatorImpl(),  //TODO line&col nos.
                "WSDL500", 
                new Object[] {"SAX", desc}, 
                ErrorReporter.SEVERITY_FATAL_ERROR, 
                e);
        } 
            
        //TODO - potentially returns null. correct after deciding how 
        //to handle exceptions (e.g. return inside try block).
        return doc;
    }

    /*
     * Convert a string of type xs:anyURI to a java.net.URI.
     * An empty string argument will return an empty string URI.
     * A null argument will return a null.
     */
    private URI getURI(String anyURI) throws WSDLException
    {
        URI uri = null;
        if(anyURI != null)
        {
            try {
                uri = new URI(anyURI);
            } catch (URISyntaxException e) {
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
     * Retrieve a WSDL document by resolving the location URI specified 
     * on a WSDL &lt;import&gt; or &lt;include&gt; element.
     * 
     * TODO add support for a URL Catalog Resolver
     */
    private DescriptionElement getWSDLFromLocation(String locationURI,
                                               DescriptionElement desc,
                                               Map wsdlModules)
                                               throws WSDLException
    {
        DescriptionElement referencedDesc = null;
        Element docEl;
        URL locationURL = null;
        URI contextURI = null;
        
        try 
        {
            contextURI = desc.getDocumentBaseURI();
            URL contextURL = (contextURI != null) ? contextURI.toURL() : null;
            locationURL = StringUtils.getURL(contextURL, locationURI);
        } 
        catch (MalformedURLException e) 
        {
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
        
        if(referencedDesc == null)
        {
            //not previously imported or included, so retrieve the WSDL.
            try {
                Document doc = getDocument(
                        new InputSource(locationStr), locationStr);
                docEl = doc.getDocumentElement();
            } 
            catch (IOException e) 
            {
                //document retrieval failed (e.g. 'not found')
                getErrorReporter().reportError(
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL503", 
                        new Object[] {locationStr}, 
                        ErrorReporter.SEVERITY_WARNING, 
                        e);
                
                //cannot continue without the referenced document
                return null;
            }
            
            //The referenced document should contain a WSDL <description>
            if(!QNameUtils.matches(Constants.Q_ELEM_DESCRIPTION, docEl))
            {
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
            
            if(!wsdlModules.containsKey(locationStr))
            {
                wsdlModules.put(locationStr, referencedDesc);
            }
        }
            
        return referencedDesc;
    }
    
    /**
     * A wrapper that plugs Woden's error reporter mechanism into the
     * XML parser used to parse the WSDL document.
     */
    class ErrorHandlerWrapper implements org.xml.sax.ErrorHandler
    {
    	/**
    	 * The error reporter used to report errors in Woden.
    	 */
    	private ErrorReporter errorReporter;
    	
    	/**
    	 * Constructor.
    	 * 
    	 * @param errorReporter The error reporter to be wrapped.
    	 */
    	public ErrorHandlerWrapper(ErrorReporter errorReporter)
    	{
    		this.errorReporter = errorReporter;
    	}

		/* (non-Javadoc)
		 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
		 */
		public void error(SAXParseException error) throws SAXException 
		{
			ErrorLocatorImpl locator = new ErrorLocatorImpl();
			locator.setLineNumber(error.getLineNumber());
			locator.setColumnNumber(error.getColumnNumber());
			try
			{
			    errorReporter.reportError(locator, null, error.getMessage(), ErrorReporter.SEVERITY_ERROR, error.getException());
			}
			catch(WSDLException e)
			{
				throw new SAXException("A problem occurred setting the error in the Woden error reporter wrapper.", e);
			}
			
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
		 */
		public void fatalError(SAXParseException error) throws SAXException 
		{
			ErrorLocatorImpl locator = new ErrorLocatorImpl();
			locator.setLineNumber(error.getLineNumber());
			locator.setColumnNumber(error.getColumnNumber());
			try
			{
			    errorReporter.reportError(locator, null, error.getMessage(), ErrorReporter.SEVERITY_FATAL_ERROR, error.getException());
			}
			catch(WSDLException e)
			{
				throw new SAXException("A problem occurred setting the error in the Woden error reporter wrapper.", e);
			}
			
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
		 */
		public void warning(SAXParseException warning) throws SAXException 
		{
			ErrorLocatorImpl locator = new ErrorLocatorImpl();
			locator.setLineNumber(warning.getLineNumber());
			locator.setColumnNumber(warning.getColumnNumber());
			try
			{
			    errorReporter.reportError(locator, null, warning.getMessage(), ErrorReporter.SEVERITY_WARNING, warning.getException());
			}
			catch(WSDLException e)
			{
				throw new SAXException("A problem occurred setting the error in the Woden error reporter wrapper.", e);
			}
			
		}
    	
    }
    
    class WSDLEntityResolver implements org.xml.sax.EntityResolver
    {

		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			// TODO Auto-generated method stub
			
			return null;
		}
    	
    }
    
}

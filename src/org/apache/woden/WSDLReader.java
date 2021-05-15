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

import org.apache.woden.wsdl20.extensions.ExtensionRegistry;
import org.apache.woden.wsdl20.xml.DescriptionElement;

/**
 * This interface declares the WSDL reader API for parsing WSDL documents.
 * <p>
 * TODO after WSDL 2.0 parsing is implemented, consider if/how to make this reader
 * API independent of the WSDL version (definition/description) or whether to make it
 * support both versions. Also, make it independent of the type of XML parser if
 * possible (e.g. no DOM objects in signatures).
 * <p>
 * TODO add to the API methods to get/set features and properties of the
 * Woden framework (i.e. as distinct from features/properties of the WSDL 2.0
 * component model). Similar to WSDLReader.setFeature in WSDL4J, a named 
 * feature will be turned on or off with a boolean. A named property will be
 * set with some object representing the property value.
 *   
 * @author John Kaputin (jkaputin@apache.org)
 */
public interface WSDLReader {
    
    
    //TODO Create wsdl-version-independent methods. 
    //E.g. public WSDLDocument readWSDL(String uri); where
    //WSDLDocument is a wrapper for a 1.1 Definition and a 
    //2.0 Description. The client app could/should then use
    //WSDLDocument to determine which version is has and use
    //the appropriate WSDL API.

    //for the 2.0 prototype, just use 2.0 specific methods
    
    /**
     * Constants for reader configuration feature names. 
     * Features are associated with a boolean value (true if they are 
     * enabled, false if not).
     * 
     * TODO Decide if these features should be exposed on the Woden API
     * as constants defined here and used via the generic set/getFeature 
     * methods, or whether we should have a finite set of feature-specific
     * methods on the WSDLReader interface for the features we know about 
     * (e.g. setValidationFeature(boolean) or setValidationFeatureOn() / 
     * setValidationFeatureOff()) and remove the constants defined here.
     * Note - even in the latter case, we still need the generic 
     * set/getFeature methods.
     */
    
    
    /**
     * Set to <code>true</code> to enable verbose diagnostic tracing, <code>false</code> otherwise.
     */
    public static String FEATURE_VERBOSE = 
        "http://ws.apache.org/woden/features/verbose";
    
    /**
     * Set to <code>true</code> to enable the WSDL validation feature, <code>false</code> otherwise.
     */
    public static String FEATURE_VALIDATION = 
        "http://ws.apache.org/woden/features/validation";
    
    /**
     * Set to <code>true</code> if parsing should continue after 
     * encountering a non-fatal error in the WSDL which might result
     * in incomplete WSDL model being returned by the reader, 
     * <code>false</code> otherwise.
     */
    public static String FEATURE_CONTINUE_ON_ERROR =
        "http://ws.apache.org/woden/features/continue_on_error";
    
    /**
     * Constants for reader configuration property names.
     * Properties have a value represented by an object. 
     * 
     * TODO ditto the comment on features, about whether to define 
     * property constants here and use generic set/getProperty methods
     * or remove the constants and use property-specific methods.
     */
    public static String PROPERTY_XML_PARSER_API = 
        "http://ws.apache.org/woden/property/xml_parser_api";

    public static String PROPERTY_TYPE_SYSTEM_API = 
        "http://ws.apache.org/woden/property/type_system_api";
    
    /**
     * A constant representing the W3C XML Schema type system. All
     * implementations of the Woden API must support W3C XML Schema.
     * An implementation configured to use this type system can 
     * use this constant to specify its value for the property
     * "http://ws.apache.org/woden/property/type_system_api".
     */
    public static final String TYPE_XSD_2001 =
        "http://www.w3.org/2001/XMLSchema";
    
    /**
     * Read the WSDL document accessible via the specified
     * URI into a WSDL description.
     * 
     * TODO: return value is WSDL 2.0 specific. May be refactored.
     *
     * @param wsdlURI a URI (absolute filename or URL) pointing to a
     * WSDL document.
     * @return the description element of the WSDL document.
     * @throws WSDLException for terminating errors and as wrapper
     * for checked exceptions.
     */
    public DescriptionElement readWSDL(String wsdlURI) throws WSDLException;

    /**
     * Read the WSDL document accessible via the specified
     * URI into a WSDL description.
     * 
     * TODO: return value is WSDL 2.0 specific. May be refactored.
     *
     * @param wsdlURI a URI (absolute filename or URL) pointing to a
     * WSDL document.
     * @param errorHandler An error handler that will handle reporting of errors and warnings.
     * @return the description element of the WSDL document.
     * @throws WSDLException for terminating errors and as wrapper
     * for checked exceptions.
     */
    public DescriptionElement readWSDL(String wsdlURI, ErrorHandler errorHandler) throws WSDLException;
    
    /**
     * Read the WSDL document contained in the specified WSDLSource object.
     * The WSDLSource must contain the WSDL in a format compatible with the 
     * concrete WSDLReader implementation.
     * <p>
     * For example, if the WSDLReader implementation is Woden's DOMWSDLReader
     * then the WSDLSource may contain a DOM Element or Document representing
     * the &lt;wsdl:description&gt; element.
     * <p>
     * TODO update this Javadoc comment at development of WSDLSource progresses.
     * 
     * @param wsdlSource contains an object representing the WSDL
     * @return the DescriptionElement representing the parsed WSDL
     * @throws WSDLException for terminating errors and as a wrapper
     * for checked exceptions
     */
    public DescriptionElement readWSDL(WSDLSource wsdlSource) throws WSDLException;
    
    /**
     * Read the WSDL document contained in the specified WSDLSource object,
     * using the specified custom ErrorHandler to report any errors.
     * The WSDLSource must contain the WSDL in a format compatible with the 
     * concrete WSDLReader implementation.
     * <p>
     * For example, if the WSDLReader implementation is Woden's DOMWSDLReader
     * then the WSDLSource may contain a DOM Element or Document representing
     * the &lt;wsdl:description&gt; element.
     * <p>
     * TODO update this Javadoc comment at development of WSDLSource progresses.
     *
     * @param wsdlSource contains an object representing the WSDL
     * @param errorHandler a custom error handler that overrides the default handler
     * @return the DescriptionElement representing the parsed WSDL
     * @throws WSDLException for terminating errors and as a wrapper
     * for checked exceptions
     */
    public DescriptionElement readWSDL(WSDLSource wsdlSource, ErrorHandler errorHandler) throws WSDLException;
   
    //TODO - a readWSDL method that returns a Description (eg a component)
    
    /**
     * Returns a concrete implementation of WSDLSource that is compatible with
     * the WSDLReader implementation. That is, it will accept objects representing
     * WSDL source in formats that the WSDLReader implementation can understand.
     * For example, a DOM-based implementation of WSDLReader will return a DOM-
     * based implementation of WSDLSource that accepts WSDL source as a DOM
     * Element or Document.
     * 
     * @return the WSDLSource class compatible with the WSDLReader implementation.
     */
    public WSDLSource createWSDLSource();
    
    /**
     * @return the ErrorReporter used by this reader
     */
    public ErrorReporter getErrorReporter();
    
    /**
     * Store the name of the WSDLFactory implementation class to be used for  
     * any subsequent WSDLFactory requests. The named factory class will 
     * replace any existing factory object in use.
     * 
     * @param factoryImplName the WSDLFactory implementation classname
     */
    public void setFactoryImplName(String factoryImplName);
    
    /**
     * @return the WSDLFactory implementation classname
     */
    public String getFactoryImplName();
    
    public void setExtensionRegistry(ExtensionRegistry extReg);
    
    public ExtensionRegistry getExtensionRegistry();
    
    /**
     * Set a named feature on or off with a boolean. Note, this relates to 
     * features of the Woden framework, not to WSDL-specific features such
     * as the WSDL 2.0 Feature component.
     * <p>
     * All feature names should be fully-qualified, Java package style to 
     * avoid name clashes. All names starting with org.apache.woden. are 
     * reserved for features defined by the Woden implementation. 
     * Features specific to other implementations should be fully-qualified  
     * to match the package name structure of that implementation. 
     * For example: com.abc.featureName
     * 
     * @param name the name of the feature to be set
     * @param value a boolean value where true sets the feature on, false sets it off
     * @throws IllegalArgumentException if the feature name is not recognized.
     */
    public void setFeature(String name, boolean value);
    
    /**
     * Returns the on/off setting of the named feature, represented as a boolean. 
     * 
     * @param name the name of the feature to get the value of
     * @return a boolean representing the on/off state of the named feature
     * @throws IllegalArgumentException if the feature name is not recognized.
     */
    public boolean getFeature(String name);
    
    
    /**
     * Set a named property to the specified object.  Note, this relates to 
     * properties of the Woden implementation, not to WSDL-specific properties
     * such as the WSDL 2.0 Property component.
     * <p>
     * All property names should be fully-qualified, Java package style to 
     * avoid name clashes. All names starting with org.apache.woden. are 
     * reserved for properties defined by the Woden implementation. 
     * Properties specific to other implementations should be fully-qualified  
     * to match the package name structure of that implementation. 
     * For example: com.abc.propertyName
     * 
     * @param name the name of the property to be set
     * @param value an Object representing the value to set the property to
     * @throws IllegalArgumentException if the property name is not recognized.
     */
    public void setProperty(String name, Object value);
    
    /**
     * Returns the value of the named property.
     * 
     * @param name the name of the property to get the value of
     * @return an Object representing the property's value
     * @throws IllegalArgumentException if the property name is not recognized.
     */
    public Object getProperty(String name);
    
}

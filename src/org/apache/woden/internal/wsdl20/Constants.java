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
//TODO - consider separating common constants from 2.0 constants
//TODO - check if any constants copied from w4j can be deleted
package org.apache.woden.internal.wsdl20;

import javax.xml.namespace.QName;

/**
 * Constants for WSDL 2.0 elements, attributes and URIs.
 * 
 * @author jkaputin@apache.org
 */
public class Constants
{
    // Namespace URIs.
    public static final String NS_URI_WSDL20 =
        "http://www.w3.org/2006/01/wsdl";
    public static final String NS_URI_XMLNS =
        "http://www.w3.org/2000/xmlns/";
    public static final String NS_URI_XSI =
        "http://www.w3.org/2001/XMLSchema-instance";

    // Top-level WSDL 2.0 element names.
    public static final String ELEM_DESCRIPTION = "description";
    public static final String ELEM_DOCUMENTATION = "documentation";
    public static final String ELEM_IMPORT = "import";
    public static final String ELEM_INCLUDE = "include";
    public static final String ELEM_TYPES = "types";
    public static final String ELEM_INTERFACE = "interface";
    public static final String ELEM_BINDING = "binding";
    public static final String ELEM_SERVICE = "service";

    // Nested WSDL 2.0 element names.
    public static final String ELEM_FAULT = "fault";
    public static final String ELEM_OPERATION = "operation";
    public static final String ELEM_INPUT = "input";
    public static final String ELEM_OUTPUT = "output";
    public static final String ELEM_INFAULT = "infault";
    public static final String ELEM_OUTFAULT = "outfault";
    public static final String ELEM_FEATURE = "feature";
    public static final String ELEM_PROPERTY = "property";
    public static final String ELEM_VALUE = "value";
    public static final String ELEM_CONSTRAINT = "constraint";
    public static final String ELEM_ENDPOINT = "endpoint";

    // Top-level WSDL 2.0 qualified element names.
    public static final QName Q_ELEM_DESCRIPTION =
        new QName(NS_URI_WSDL20, ELEM_DESCRIPTION);
    public static final QName Q_ELEM_DOCUMENTATION =
        new QName(NS_URI_WSDL20, ELEM_DOCUMENTATION);
    public static final QName Q_ELEM_IMPORT =
        new QName(NS_URI_WSDL20, ELEM_IMPORT);
    public static final QName Q_ELEM_INCLUDE =
        new QName(NS_URI_WSDL20, ELEM_INCLUDE);
    public static final QName Q_ELEM_TYPES =
        new QName(NS_URI_WSDL20, ELEM_TYPES);
    public static final QName Q_ELEM_INTERFACE =
        new QName(NS_URI_WSDL20, ELEM_INTERFACE);
    public static final QName Q_ELEM_BINDING =
        new QName(NS_URI_WSDL20, ELEM_BINDING);
    public static final QName Q_ELEM_SERVICE =
        new QName(NS_URI_WSDL20, ELEM_SERVICE);

    // Nested WSDL 2.0 qualified element names.
    public static final QName Q_ELEM_FAULT =
        new QName(NS_URI_WSDL20, ELEM_FAULT);
    public static final QName Q_ELEM_OPERATION =
        new QName(NS_URI_WSDL20, ELEM_OPERATION);
    public static final QName Q_ELEM_INPUT =
        new QName(NS_URI_WSDL20, ELEM_INPUT);
    public static final QName Q_ELEM_OUTPUT =
        new QName(NS_URI_WSDL20, ELEM_OUTPUT);
    public static final QName Q_ELEM_INFAULT =
        new QName(NS_URI_WSDL20, ELEM_INFAULT);
    public static final QName Q_ELEM_OUTFAULT =
        new QName(NS_URI_WSDL20, ELEM_OUTFAULT);
    public static final QName Q_ELEM_FEATURE =
        new QName(NS_URI_WSDL20, ELEM_FEATURE);
    public static final QName Q_ELEM_PROPERTY =
        new QName(NS_URI_WSDL20, ELEM_PROPERTY);
    public static final QName Q_ELEM_VALUE =
        new QName(NS_URI_WSDL20, ELEM_VALUE);
    public static final QName Q_ELEM_CONSTRAINT =
        new QName(NS_URI_WSDL20, ELEM_CONSTRAINT);
    public static final QName Q_ELEM_ENDPOINT =
        new QName(NS_URI_WSDL20, ELEM_ENDPOINT);
    
    // Attribute names.
    public static final String ATTR_ID = "id";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_TARGET_NAMESPACE = "targetNamespace";
    public static final String ATTR_NAMESPACE = "namespace";
    public static final String ATTR_XMLNS = "xmlns";
    public static final String ATTR_EXTENDS = "extends";
    public static final String ATTR_STYLE_DEFAULT = "styleDefault";
    public static final String ATTR_ELEMENT = "element";
    public static final String ATTR_PATTERN = "pattern";
    public static final String ATTR_STYLE = "style";
    public static final String ATTR_MESSAGE_LABEL = "messageLabel";
    public static final String ATTR_REF = "ref";
    public static final String ATTR_REQUIRED = "required";
    public static final String ATTR_INTERFACE = "interface";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_BINDING = "binding";
    public static final String ATTR_ADDRESS = "address";
    public static final String ATTR_LOCATION = "location";

    // Attribute values and NMTokens
    public static final String VALUE_EMPTY_STRING = "";
    public static final String VALUE_TRUE = "true";
    public static final String VALUE_FALSE = "false";
    public static final String NMTOKEN_VALUE = "#value";
    public static final String NMTOKEN_ANY = "#any";
    public static final String NMTOKEN_NONE = "#none";
    public static final String NMTOKEN_OTHER = "#other";
    public static final String NMTOKEN_ELEMENT = "#element";
    
    /* Constants representing the values of the properties used to 
     * configure the Woden runtime (i.e. different to WSDL 2.0 properties).
     * These typically describe the standards, APIs, etc, supported by this 
     * implementation of the Woden API.
     * 
     * The first part of the constant name indicates its usage:
     * TYPE_  describes a supported type system, such as the W3C XML Schema.
     * API_   describes a supported external API, such as DOM
     */ 
    public static final String TYPE_XSD_2001 =
        "http://www.w3.org/2001/XMLSchema";
    public static final String API_W3C_DOM =
        "org.w3c.dom";
    public static final String API_W3C_XS =
        "http://www.w3.org/Submission/xmlschema-api/";   //XML Schema API implemented in Xerces
    public static final String API_APACHE_WS_XS =
        "org.apache.ws.commons.schema";   //Apache WS-Commons XmlSchema

  //TODO determine which lists are needed
  /*
  // Lists of native attribute names.
  public static final String[] PART_ATTR_NAMES =
    new String[]{ATTR_NAME, ATTR_TYPE, ATTR_ELEMENT};
  public static final String[] BINDING_ATTR_NAMES =
    new String[]{ATTR_NAME, ATTR_TYPE};
  public static final String[] BINDING_FAULT_ATTR_NAMES =
    new String[]{ATTR_NAME};
  public static final String[] BINDING_INPUT_ATTR_NAMES =
    new String[]{ATTR_NAME};
  public static final String[] BINDING_OPERATION_ATTR_NAMES =
    new String[]{ATTR_NAME};
  public static final String[] BINDING_OUTPUT_ATTR_NAMES =
    new String[]{ATTR_NAME};
  public static final String[] FAULT_ATTR_NAMES =
    new String[]{ATTR_NAME, ATTR_MESSAGE};
  public static final String[] IMPORT_ATTR_NAMES =
    new String[]{ATTR_NAMESPACE, ATTR_LOCATION};
  public static final String[] INPUT_ATTR_NAMES =
    new String[]{ATTR_NAME, ATTR_MESSAGE};
  public static final String[] MESSAGE_ATTR_NAMES =
    new String[]{ATTR_NAME};
  public static final String[] OPERATION_ATTR_NAMES =
    new String[]{ATTR_NAME, ATTR_PARAMETER_ORDER};
  public static final String[] OUTPUT_ATTR_NAMES =
    new String[]{ATTR_NAME, ATTR_MESSAGE};
  public static final String[] PORT_ATTR_NAMES =
    new String[]{ATTR_NAME, ATTR_BINDING};
  public static final String[] PORT_TYPE_ATTR_NAMES =
    new String[]{ATTR_NAME};
  public static final String[] SERVICE_ATTR_NAMES =
    new String[]{ATTR_NAME};
  public static final String[] TYPES_ATTR_NAMES =
    new String[]{};
  */

  //TODO determine if remaining constants are still required?
  
  // Qualified attribute names.
  public static final QName Q_ATTR_REQUIRED =
    new QName(NS_URI_WSDL20, ATTR_REQUIRED);

  // XML Declaration string.
  public static final String XML_DECL_DEFAULT = "UTF-8";
  public static final String XML_DECL_START =
    "<?xml version=\"1.0\" encoding=\"";
  public static final String XML_DECL_END = "\"?>";

  // Feature names.
  // TODO org.apache and import optionality
  public static final String FEATURE_VERBOSE = "javax.wsdl.verbose";
  public static final String FEATURE_IMPORT_DOCUMENTS =
    "javax.wsdl.importDocuments";

}

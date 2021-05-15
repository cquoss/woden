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
package org.apache.woden.internal.util.om;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.woden.ErrorReporter;
import org.apache.woden.WSDLException;
import org.apache.woden.internal.ErrorLocatorImpl;
import org.apache.woden.internal.ErrorReporterImpl;
import org.apache.woden.wsdl20.xml.DescriptionElement;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

/**
 * This class contains utility methods required for parsing elements
 * in a WSDL using AXIOM.
 */
public class OMUtils {

    private static final String emptyString = "";

    /**
     * @param strUri The URI where the WSDL can be found
     * @return A StAXOMBuilder which could be used in obtaining the document object
     */
    public static StAXOMBuilder getOMBuilder(String strUri) {
        StAXOMBuilder builder = null;
        try {
            URI uri = new URI(strUri);
            URL url = uri.toURL();

            InputStream in = url.openStream();
            builder = new StAXOMBuilder(in);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return builder;
    }


    /**
     * todo add validation
     * @param uri of the OMDocument
     * @return an OMElement representing the document just read
     */
    public static OMElement getElement(String uri){
        StAXOMBuilder builder = OMUtils.getOMBuilder(uri);
        OMElement elem = builder.getDocumentElement();
        return elem;
    }

    /**
     * Returns the value of an attribute of an element. Returns null
     * if the attribute is not found
     * @param omElement Element whose attrib is looked for
     * @param attrName  name of attribute to look for
     * @return the attribute value
     */
    public static String getAttribute(OMElement omElement, String attrName) {
        String val = null;
        Iterator allAttr = omElement.getAllAttributes();
        while(allAttr.hasNext()){
            OMAttribute attr = (OMAttribute)allAttr.next();
            if (attr.getLocalName().equals(attrName)){
                val = attr.getAttributeValue();
            }
        }
        return val;
    }

    /**
     * @param element OMElement which would most probably contain <xs:schema>
     * @return a SAX inputsource from the OMElement
     */
    public static InputSource getInputSource(OMElement element){

        String elementString = null;

        //Obtain the String value of the OMElement after building the OM tree
        try {
            elementString = element.toStringWithConsume();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        byte[] bytes = elementString.getBytes();

        //Deserialize from a byte array
        InputStream inputStream = new ByteArrayInputStream(bytes);
        InputSource inputSource = new InputSource(inputStream);

        return inputSource;
    }

    /**
     * @param prefixedValue to which the QName is prefixed
     * @param contextEl Element in which the QName is sought for
     * @return  The relevant QName for the prefix
     * @throws WSDLException
     */
    public static QName getQName(String prefixedValue,
                                 OMElement contextEl)
                                 throws WSDLException{
        int index = prefixedValue.indexOf(':');
        String prefix = (index != -1)
                        ? prefixedValue.substring(0, index)
                        : null;
        String localPart    = prefixedValue.substring(index + 1);
        String namespaceURI = null;

        if (prefix != null){
            namespaceURI = contextEl.findNamespaceURI(prefix).getNamespaceURI();
            //TODO investigate changing the registration of namespaces and prefixes (i.e. namespace decls)
            //so it can happen within any WSDL element, not just Description (current behaviour is copied from WSDL4J)
            //registerUniquePrefix(prefix, namespaceURI, desc);

            //TODO when passing prefix to QName ctor, what if it was modified by
            //registerUniquePrefix because of a name clash (pass original or modification)?
            return new QName(namespaceURI,
                    localPart,
                    prefix != null ? prefix : emptyString);
        }
        else{
            //TODO use ErrorReporter here or in callers to report the problem

            String faultCode = (prefix == null)
            ? WSDLException.NO_PREFIX_SPECIFIED
                    : WSDLException.UNBOUND_PREFIX;

            WSDLException wsdlExc = new WSDLException(faultCode,
                    "Unable to determine " +
                    "namespace of '" +
                    prefixedValue + "'.");

            throw wsdlExc;
        }
    }

    /**
     * @param el Element whose attrib is looked for
     * @param namespaceURI namespace URI of attribute to look for
     * @param localPart local part of attribute to look for
     * @return the attribute value
     */
    static public String getAttributeNS (OMElement el,
                                         String namespaceURI,
                                         String localPart) {
      String sRet = null;
      OMAttribute   attr = el.getAttribute(new QName(namespaceURI, localPart));
      if (attr != null) {
        sRet = attr.getAttributeValue();
      }
      return sRet;
    }


    /*This is the same method taken from DOMUtils*/
    public static void registerUniquePrefix(String prefix, String namespaceURI, DescriptionElement desc)
            throws WSDLException {
          URI nsUri = desc.getNamespace(prefix);
          String tempNSUri = nsUri != null ? nsUri.toString() : null;

          if (tempNSUri != null && tempNSUri.equals(namespaceURI)){
            return;
          }

          while (tempNSUri != null && !tempNSUri.equals(namespaceURI)){
            prefix += "_";
            nsUri = desc.getNamespace(prefix);
            tempNSUri = nsUri != null ? nsUri.toString() : null;
          }

          URI uri = null;
          try {
              uri = new URI(namespaceURI);
          } catch (URISyntaxException e) {
              //TODO make sure custom err handler is used, if configured
              new ErrorReporterImpl().reportError(
                      new ErrorLocatorImpl(),  //TODO line&col nos.
                      "WSDL506",
                      new Object[] {namespaceURI},
                      ErrorReporter.SEVERITY_ERROR,
                      e);
          }
          desc.addNamespace(prefix, uri);
    }
}



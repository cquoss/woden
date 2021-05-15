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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;

import javax.xml.namespace.QName;

/**
 * Class to handle some trivial functionality related to QNames.
 */
public class QNameUtils {

    /**
     * @param qname The relevant QName to be matched
     * @param element
     * @return true if the given QName matches the element's and false otherwise
     */
    public static boolean matches(QName qname, OMElement element){
      return (element != null && qname.equals(newQName(element)));
    }

    /* TODO: this method could be unnecessary!
     * @param element
     * @return a QName based on the OMElement
     */
    public static QName newQName(OMElement element){
      if (element != null){
        OMNamespace namespace = element.getNamespace();
        return new QName(namespace.getNamespaceURI(), element.getLocalName());
      }
      else{
        return null;
      }
    }
}
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
package org.apache.woden.xml;

import javax.xml.namespace.QName;

import org.apache.woden.ErrorReporter;
import org.apache.woden.WSDLException;
import org.w3c.dom.Element;

/**
 * This class represents extension attributes (those not in the WSDL namespace)
 * that do not have a Java type registered in the Extension Registry.
 * It treats the attribute's value as a string.
 * 
 * @author jkaputin@apache.org
 */
public class UnknownAttr implements XMLAttr 
{
    private QName fAttrType = null;
    private Object fContent = null;
    private String fExternalForm = null;
    private boolean fValid = true;
    private ErrorReporter fErrorReporter = null;

    /* (non-Javadoc)
     * @see org.apache.woden.xml.XMLAttr#setErrorReporter(org.apache.woden.ErrorReporter)
     */
    public void setErrorReporter(ErrorReporter errRpt) {
        fErrorReporter = errRpt;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.xml.XMLAttr#init(org.w3c.dom.Element, java.lang.String)
     */
    public void init(Element ownerEl, QName attrType, String attrValue) throws WSDLException 
    {
        fAttrType = attrType;
        fExternalForm = attrValue;
        fContent = attrValue;
        if(fContent == null) fValid = false;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.xml.XMLAttr#getAttributeType()
     */
    public QName getAttributeType() {
        return fAttrType;
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.xml.XMLAttr#getContent()
     */
    public Object getContent() {
        return fContent;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.xml.XMLAttr#toExternalForm()
     */
    public String toExternalForm() {
        return fExternalForm;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.xml.XMLAttr#isValid()
     */
    public boolean isValid() {
        return fValid;
    }

}

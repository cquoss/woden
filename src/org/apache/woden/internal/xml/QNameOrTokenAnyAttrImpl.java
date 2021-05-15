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
package org.apache.woden.internal.xml;

import javax.xml.namespace.QName;

import org.apache.woden.ErrorReporter;
import org.apache.woden.WSDLException;
import org.apache.woden.internal.ErrorLocatorImpl;
import org.apache.woden.internal.util.dom.DOMUtils;
import org.apache.woden.xml.QNameOrTokenAttr;
import org.w3c.dom.Element;

/**
 * This class represents XML attribute information items of type
 * "Union of QName or xs:token #any"
 * (e.g. the wsoap:code extension attribute of binding fault).
 * 
 * @author jkaputin@apache.org
 */
public class QNameOrTokenAnyAttrImpl extends XMLAttrImpl 
                                     implements QNameOrTokenAttr 
{
    public QNameOrTokenAnyAttrImpl() {}
    
    /* ************************************************************
     *  QNameOrTokenAttr interface declared methods 
     * ************************************************************/

    /* (non-Javadoc)
     * @see org.apache.woden.xml.QNameOrTokenAttr#isQName()
     */
    public boolean isQName() 
    {
        return fContent instanceof QName;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.xml.QNameOrTokenAttr#isToken()
     */
    public boolean isToken() 
    {
        if(!isQName() && isValid()) {
            return true;
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.woden.xml.QNameOrTokenAttr#getQName()
     */
    public QName getQName() 
    {
        if(isQName()) {
            return (QName)fContent;
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.woden.xml.QNameOrTokenAttr#getToken()
     */
    public String getToken() 
    {
        if(!isQName() && isValid()) {
            return (String)fContent;
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.woden.internal.xml.XMLAttrImpl#convert(org.w3c.dom.Element, java.lang.String)
     *
     * Convert a string of type "Union of xs:QName or xs:token #any" to a 
     * java.xml.namespace.QName or a String.
     * A null argument will return a null value.
     * Any conversion error will be reported and a null value will be returned.
     */
    protected Object convert(Element ownerEl, String attrValue) throws WSDLException
    {
        //First, check if the attribute contains the xs:token '#any'.
        if("#any".equals(attrValue)) return attrValue;
        
        //Second, assume the attribute contains a xs:QName value.
        Exception ex = null;
        QName qn = null;
        
        if(attrValue != null)
        {
            try
            {
                qn = DOMUtils.getQName(attrValue, ownerEl);
            } 
            catch (WSDLException e) 
            {
                ex = e;
            }
        }
        
        if(qn == null)
        {
            setValid(false);
            getErrorReporter().reportError(
                    new ErrorLocatorImpl(),  //TODO line&col nos.
                    "WSDL507",
                    new Object[] {attrValue}, 
                    ErrorReporter.SEVERITY_ERROR, 
                    ex);
        }
        
        return qn;
    }
    
}

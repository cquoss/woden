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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.woden.ErrorReporter;
import org.apache.woden.WSDLException;
import org.apache.woden.internal.ErrorLocatorImpl;
import org.apache.woden.internal.util.StringUtils;
import org.apache.woden.internal.util.dom.DOMUtils;
import org.apache.woden.xml.QNameListOrTokenAttr;
import org.w3c.dom.Element;

/**
 * This class represents XML attribute information items of type
 * "Union of list of QName or xs:token #any"
 * (e.g. the wsoap:subcodes extension attribute of binding fault).
 * 
 * @author jkaputin@apache.org
 */
public class QNameListOrTokenAnyAttrImpl extends XMLAttrImpl 
                                         implements QNameListOrTokenAttr 
{
    public QNameListOrTokenAnyAttrImpl() {}
    
    /* ************************************************************
     *  QNameListOrTokenAttr interface declared methods 
     * ************************************************************/

    /* (non-Javadoc)
     * @see org.apache.woden.xml.QNameListOrTokenAttr#isQNameList()
     */
    public boolean isQNameList() 
    {
        return fContent instanceof QName[];
    }

    /* (non-Javadoc)
     * @see org.apache.woden.xml.QNameListOrTokenAttr#isToken()
     */
    public boolean isToken() 
    {
        if(!isQNameList() && isValid()) {
            return true;
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.woden.xml.QNameListOrTokenAttr#getQNames()
     */
    public QName[] getQNames() 
    {
        if(isQNameList()) {
            return (QName[])fContent;
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.woden.xml.QNameListOrTokenAttr#getToken()
     */
    public String getToken() 
    {
        if(!isQNameList() && isValid()) {
            return (String)fContent;
        } else {
            return null;
        }
    }

    /* ************************************************************
     *  Non-API implementation methods 
     * ************************************************************/
    
    /* (non-Javadoc)
     * @see org.apache.woden.internal.xml.XMLAttrImpl#convert(org.w3c.dom.Element, java.lang.String)
     *
     * Convert a string of type "Union of list of xs:QName or xs:token #any" to a 
     * java.xml.namespace.QName array or a String.
     * A null argument will return a null value.
     * Any conversion error will be reported and a null value will be returned.
     */
    protected Object convert(Element ownerEl, String attrValue) throws WSDLException
    {
        //First, check if the attribute contains the xs:token '#any'.
        if("#any".equals(attrValue)) return attrValue;
        
        //Second, assume the attribute contains a list of xs:QName.
        if(attrValue == null || "".equals(attrValue))
        {
            setValid(false);
            getErrorReporter().reportError(
                    new ErrorLocatorImpl(),  //TODO line&col nos.
                    "WSDL509", 
                    new Object[] {attrValue}, 
                    ErrorReporter.SEVERITY_ERROR);
            return null;
        }
        
        List qnStrings = StringUtils.parseNMTokens(attrValue);
        Iterator i = qnStrings.iterator();
        String qnString = null;
        QName qname = null;
        List qnames = new Vector();
        
        while(i.hasNext())
        {
            qnString = (String)i.next();
            try
            {
                qname = DOMUtils.getQName(qnString, ownerEl);
            } 
            catch (WSDLException e) 
            {
                setValid(false);
                getErrorReporter().reportError(
                        new ErrorLocatorImpl(),  //TODO line&col nos.
                        "WSDL510", 
                        new Object[] {qnString, attrValue}, 
                        ErrorReporter.SEVERITY_ERROR, 
                        e);
                continue;
            }
            qnames.add(qname);
        }
        QName[] qnArray = new QName[qnames.size()];
        qnames.toArray(qnArray);
        return qnArray;    
    }
    
}

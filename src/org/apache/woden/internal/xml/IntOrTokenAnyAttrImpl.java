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

import org.apache.woden.ErrorReporter;
import org.apache.woden.WSDLException;
import org.apache.woden.internal.ErrorLocatorImpl;
import org.apache.woden.xml.IntOrTokenAttr;
import org.w3c.dom.Element;

/**
 * This class represents XML attribute information items of type
 * 'Union of xs:int, xs:token #any', for example the 
 * whttp:code extension attribute of binding &lt;fault&gt;.
 * 
 * @author jkaputin@apache.org
 */
public class IntOrTokenAnyAttrImpl extends XMLAttrImpl implements IntOrTokenAttr 
{

    /* ************************************************************
     *  QNameOrTokenAttr interface declared methods 
     * ************************************************************/
    
    /* (non-Javadoc)
     * @see org.apache.woden.xml.IntOrTokenAttr#isInt()
     */
    public boolean isInt() 
    {
        return fContent instanceof Integer;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.xml.IntOrTokenAttr#isToken()
     */
    public boolean isToken() 
    {
        if(!isInt() && isValid()) {
            return true;
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.woden.xml.IntOrTokenAttr#getInt()
     */
    public Integer getInt() 
    {
        if(isInt()) {
            return (Integer)fContent;
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.woden.xml.IntOrTokenAttr#getToken()
     */
    public String getToken() 
    {
        if(!isInt() && isValid()) {
            return (String)fContent;
        } else {
            return null;
        }
    }

    /* ************************************************************
     *  Implementation of abstract method inherited from XmlAttrImpl 
     * ************************************************************/
    
    /* (non-Javadoc)
     * @see org.apache.woden.internal.xml.XMLAttrImpl#convert(org.w3c.dom.Element, java.lang.String)
     * 
     * Convert a string of type "Union of xs:int, xs:token #any" to a 
     * java.lang.Integer or a String.
     * A null argument will return a null value.
     * Any conversion error will be reported and a null value will be returned.
     */
    protected Object convert(Element ownerEl, String attrValue) throws WSDLException 
    {
        //First, check if the attribute contains the xs:token '#any'.
        if("#any".equals(attrValue)) return attrValue;
        
        //Second, assume the attribute contains a xs:int value.
        Integer intVal = null;
        try 
        {
            intVal = new Integer(attrValue);
        } 
        catch (NumberFormatException e) 
        {
            setValid(false);
            getErrorReporter().reportError(
                    new ErrorLocatorImpl(),  //TODO line&col nos.
                    "WSDL512",
                    new Object[] {attrValue}, 
                    ErrorReporter.SEVERITY_ERROR, 
                    e);
        }
        return intVal;
    }

}

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
package org.apache.woden.internal.xml;

import javax.xml.namespace.QName;

import org.apache.woden.ErrorReporter;
import org.apache.woden.WSDLException;
import org.apache.woden.internal.ErrorLocatorImpl;
import org.apache.woden.xml.StringAttr;
import org.w3c.dom.Element;


/**
 * This class represents XML attribute information items of type xs:string.
 * 
 * @author jkaputin@apache.org
 */
public class StringAttrImpl extends XMLAttrImpl implements StringAttr 
{
    public StringAttrImpl() {}
    
    /*
     * TODO This ctor is not used for extension attributes, but may be useful if
     * parsing of native WSDL attributes is changed to use the XMLAttr interface.
     */
    public StringAttrImpl(Element ownerEl, QName attrType, String attrValue, ErrorReporter errRpt) 
                       throws WSDLException
    {
        super(ownerEl, attrType, attrValue, errRpt);
    }
    
    /* ************************************************************
     *  StringAttr interface declared methods 
     * ************************************************************/
    
    public String getString() {
        return (String)getContent();
    }
    
    /* ************************************************************
     *  Non-API implementation methods 
     * ************************************************************/

    /*
     * Convert a string of type xs:string to a java.lang.String.
     * A null argument will return a null value.
     * Any conversion error will be reported and a null value will be returned.
     */
    protected Object convert(Element ownerEl, String attrValue) throws WSDLException
    {
        String str = attrValue;
        
        if(str == null)
        {
            setValid(false);
            getErrorReporter().reportError(
                    new ErrorLocatorImpl(),  //TODO line&col nos.
                    "WSDL508", 
                    new Object[] {attrValue}, 
                    ErrorReporter.SEVERITY_ERROR);
        }
        return str;
    }

}

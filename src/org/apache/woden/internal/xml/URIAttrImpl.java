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

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;

import org.apache.woden.ErrorReporter;
import org.apache.woden.WSDLException;
import org.apache.woden.internal.ErrorLocatorImpl;
import org.apache.woden.xml.URIAttr;
import org.w3c.dom.Element;


/**
 * This class represents XML attribute information items of type xs:anyURI.
 * 
 * @author jkaputin@apache.org
 */
public class URIAttrImpl extends XMLAttrImpl implements URIAttr 
{
    public URIAttrImpl() {}
    
    /*
     * TODO This ctor is not used for extension attributes, but may be useful if
     * parsing of native WSDL attributes is changed to use the XMLAttr interface.
     */
    public URIAttrImpl(Element ownerEl,  QName attrType, String attrValue, ErrorReporter errRpt) 
                       throws WSDLException
    {
        super(ownerEl, attrType, attrValue, errRpt);
    }
    
    /* ************************************************************
     *  URIAttr interface declared methods 
     * ************************************************************/
    
    public URI getURI() {
        return (URI)getContent();
    }
    
    /* ************************************************************
     *  Non-API implementation methods 
     * ************************************************************/

    /*
     * Convert a string of type xs:anyURI to a java.net.URI.
     * An empty string argument will return an empty string URI.
     * A null argument will return a null value.
     * Any conversion error will be reported and a null value will be returned.
     */
    protected Object convert(Element ownerEl, String attrValue) throws WSDLException
    {
        Exception ex = null;
        URI uri = null;
        
        if(attrValue != null)
        {
            try 
            {
                uri = new URI(attrValue);
            } 
            catch (URISyntaxException e) 
            {
                ex = e;
            }
        }
        
        if(uri == null)
        {
            setValid(false);
            getErrorReporter().reportError(
                    new ErrorLocatorImpl(),  //TODO line&col nos.
                    "WSDL506", 
                    new Object[] {attrValue}, 
                    ErrorReporter.SEVERITY_ERROR, 
                    ex);
        }
        return uri;
    }

}

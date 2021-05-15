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
package org.apache.woden.wsdl20.xml;

import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.woden.types.NCName;

/**
 * This interface represents an &lt;operation&gt; child element 
 * of the WSDL &lt;interface&gt; element. 
 * It declares the behaviour required to support parsing, 
 * creating and manipulating an &lt;operation&gt; element.
 * 
 * @author jkaputin@apache.org
 */
public interface InterfaceOperationElement extends DocumentableElement,
                                                   ConfigurableElement,
                                                   NestedElement
{
    /*
     * Attributes
     */
    
    public void setName(NCName name);
    public QName getName();
    
    public void setPattern(URI uri);
    public URI getPattern();
    
    public void addStyleURI(URI uri);
    public void removeStyleURI(URI uri);
    public URI[] getStyle();
    
    /*
     * Elements
     */
    
    public InterfaceMessageReferenceElement addInterfaceMessageReferenceElement();
    public void removeInterfaceMessageReferenceElement(InterfaceMessageReferenceElement msgRef);
    public InterfaceMessageReferenceElement[] getInterfaceMessageReferenceElements();

    public InterfaceFaultReferenceElement addInterfaceFaultReferenceElement();
    public void removeInterfaceFaultReferenceElement(InterfaceFaultReferenceElement faultRef);
    public InterfaceFaultReferenceElement[] getInterfaceFaultReferenceElements();

}

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

import org.apache.ws.commons.schema.XmlSchemaType;

/**
 * Represents the &lt;property&gt; element and its child elements;
 * &lt;documentation&gt;, 
 * &lt;value&gt; or &lt;constraint&gt; and
 * any extension elements.
 * <p>
 * A property may have a value or a constraint but not both. If it
 * has a value, the &lt;constraint&gt; element may only contain the
 * the NMToken <code>#value</code>, in which case 
 * <code>hasValueToken()</code> will return true
 * and <code>getConstraint</code> will return null.
 * 
 * @author jkaputin@apache.org
 */
public interface PropertyElement extends DocumentableElement, NestedElement
{
    public void setRef(URI uri);
    public URI getRef();
    
    public void setValue(Object o);
    public Object getValue();
    
    /**
     * Set the QName represented by the &lt;constraint&gt; element within the
     * &lt;property&gt; element. This QName identifies a global type definition 
     * from a schema inlined or imported within the &lt;types&gt; element. 
     * Note that the use of the NMToken <code>#value</code> 
     * as the constraint, instead of a QName, is captured using the 
     * <code>setHasValueToken</code> method.
     */ 
    public void setConstraintName(QName qname);
    public QName getConstraintName();
    
    /**
     * Returns the Schema type definition identified by the QName in the &lt;constraint&gt;
     * element within the &lt;property&gt; element.
     * If this QName does not resolve to an type definition in a schema that is visible 
     * to the containing WSDL description, null will be returned by this method. 
     * To be visible, the Schema must have been correctly imported or inlined within 
     * the &lt;types&gt; element.
     * 
     * @return the XmlSchemaType identified by the &lt;constraint&gt; element
     */
    public XmlSchemaType getConstraint();

    /**
     * Set to true if &lt;constraint&gt; specifies the NMToken <code>"#value"</code>
     * rather than a QName, indicating that this property specifies a value rather 
     * than a constraint.
     * Set to false if &lt;constraint&gt; does not specify the NMToken "#value", or if
     * it is omitted. 
     */
    public void setHasValueToken(boolean b);
    
    /**
     * Returns true if &lt;constraint&gt; specifies the NMToken "#value" rather than a 
     * QName, indicating that this property specifies a value rather than a constraint.
     * Returns false if &lt;constraint&gt; does not specify the NMToken "#value", or if
     * it is omitted.
     */ 
    public boolean hasValueToken();
    
}

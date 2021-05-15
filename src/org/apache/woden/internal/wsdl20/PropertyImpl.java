/**
 * Copyright 2005, 2006 Apache Software Foundation 
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
package org.apache.woden.internal.wsdl20;

import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.NestedComponent;
import org.apache.woden.wsdl20.Property;
import org.apache.woden.wsdl20.TypeDefinition;
import org.apache.woden.wsdl20.WSDLComponent;
import org.apache.woden.wsdl20.xml.DescriptionElement;
import org.apache.woden.wsdl20.xml.NestedElement;
import org.apache.woden.wsdl20.xml.PropertyElement;
import org.apache.woden.wsdl20.xml.WSDLElement;
import org.apache.ws.commons.schema.XmlSchemaType;

/**
 * This class implements support for the &lt;wsdl:property&gt; element 
 * and the Property component in the WSDL Component model.
 * 
 * @author jkaputin@apache.org
 */
public class PropertyImpl extends DocumentableImpl
                          implements Property, PropertyElement 
{
    //WSDL Component model data
    private URI fRef = null;
    private Object fValue = null; //TODO decide how to handle value contents
    private WSDLElement fParent = null;
    
    //XML Element data
    private QName fConstraintName = null;
    private boolean fHasValueToken = false; //true if property specifies a value, not a constraint.
    
    /* ************************************************************
     *  Property interface methods (the WSDL Component model)
     * ************************************************************/
    
    /* 
     * @see org.apache.woden.wsdl20.Property#getRef()
     * @see org.apache.woden.wsdl20.xml.PropertyElement#getRef()
     */
    public URI getRef() {
        return fRef;
    }

    /*
     * @see org.apache.woden.wsdl20.Property#getValue()
     * @see org.apache.woden.wsdl20.xml.PropertyElement#getValue()
     */
    public Object getValue() {
        return fValue;
    }
    
    /* 
     * @see org.apache.woden.wsdl20.Property#getValueConstraint()
     */
    public TypeDefinition getValueConstraint() 
    {
        TypeDefinition typeDef = null;
        Description desc = (Description)getDescriptionComponent(this);
        typeDef = desc.getTypeDefinition(fConstraintName);
        return typeDef;
    }

    private WSDLComponent getDescriptionComponent(WSDLComponent wsdlComp)
    {
        if(wsdlComp instanceof InterfaceImpl) {
            return ((InterfaceImpl)wsdlComp).getDescriptionComponent();
        } 
        else if(wsdlComp instanceof BindingImpl) {
            return ((BindingImpl)wsdlComp).getDescriptionComponent();
        }
        else if(wsdlComp instanceof ServiceImpl) {
            return ((ServiceImpl)wsdlComp).getDescriptionComponent();
        }
        else {
            WSDLComponent parentComp = ((NestedComponent)wsdlComp).getParent();
            return getDescriptionComponent(parentComp);
        } 
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.NestedComponent#getParent()
     */
    public WSDLComponent getParent() {
        return (WSDLComponent)fParent;
    }
    
    /* 
     * @see org.apache.woden.wsdl20.Property#toElement()
     */
    public PropertyElement toElement() {
        return this;
    }
    
    /* ************************************************************
     *  PropertyElement interface methods (the XML Element model)
     * ************************************************************/
    
    /*
     * @see org.apache.woden.wsdl20.xml.PropertyElement#setRef(URI)
     */
    public void setRef(URI ref) {
        fRef = ref;
    }
    
    /*
     * @see org.apache.woden.wsdl20.xml.PropertyElement#setValue(Object)
     */
    public void setValue(Object value) {
        fValue = value;
    }
    
    /*
     * @see org.apache.woden.wsdl20.xml.PropertyElement#setConstraintName(QName)
     */
    public void setConstraintName(QName constraint) {
        fConstraintName = constraint;
    }
    
    /*
     * @see org.apache.woden.wsdl20.xml.PropertyElement#getConstraintName()
     */
    public QName getConstraintName() {
        return fConstraintName;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.PropertyElement#getConstraint()
     */
    public XmlSchemaType getConstraint() 
    {
        XmlSchemaType xst = null;
        DescriptionElement desc = (DescriptionElement)getDescriptionElement(this);
        TypesImpl types = (TypesImpl)desc.getTypesElement();
        if(types != null) {
            xst = types.getTypeDefinition(fConstraintName);
        }
        return xst;
    }
    
    private WSDLElement getDescriptionElement(WSDLElement wsdlElem)
    {
        if(wsdlElem instanceof InterfaceImpl ||
           wsdlElem instanceof BindingImpl   ||
           wsdlElem instanceof ServiceImpl) 
        {
            return ((NestedElement)wsdlElem).getParentElement();
        }
        else
        {
            WSDLElement parentElem = ((NestedElement)wsdlElem).getParentElement();
            return getDescriptionElement(parentElem);
        }
    }
    
    /*
     * @see org.apache.woden.wsdl20.xml.PropertyElement#setHasValueToken(boolean)
     */
    public void setHasValueToken(boolean b) {
        fHasValueToken = b;
    }
    
    /*
     * @see org.apache.woden.wsdl20.xml.PropertyElement#hasValueToken()
     */
    public boolean hasValueToken() {
        return fHasValueToken;
    }
    
    /*
     * @see org.apache.woden.wsdl20.xml.NestedElement#setParentElement(WSDL20Element)
     */
    public void setParentElement(WSDLElement parent) {
        fParent = parent;
    }
    
    /*
     * @see org.apache.woden.wsdl20.xml.NestedElement#getParentElement()
     */
    public WSDLElement getParentElement() {
        return fParent;
    }
    
    /* ************************************************************
     *  Non-API implementation methods
     * ************************************************************/

}

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
package org.apache.woden.internal.wsdl20;

import javax.xml.namespace.QName;

import org.apache.woden.types.NCName;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.ElementDeclaration;
import org.apache.woden.wsdl20.Interface;
import org.apache.woden.wsdl20.InterfaceFault;
import org.apache.woden.wsdl20.xml.DescriptionElement;
import org.apache.woden.wsdl20.xml.InterfaceElement;
import org.apache.woden.wsdl20.xml.InterfaceFaultElement;
import org.apache.woden.wsdl20.xml.TypesElement;
import org.apache.ws.commons.schema.XmlSchemaElement;

/**
 * This class represents the InterfaceFault component from the WSDL 2.0 Component 
 * Model and the &lt;fault&gt; child element of the &lt;interface&gt; element. 
 * 
 * @author jkaputin@apache.org
 */
public class InterfaceFaultImpl extends NestedConfigurableImpl
                                implements InterfaceFault,
                                           InterfaceFaultElement 
{
    //WSDL Component model data
    private NCName fName = null;
    private ElementDeclaration fElementDeclaration = null;

    //XML Element model data
    private QName fElementName = null;
    
    /* ************************************************************
     *  InterfaceFault methods (i.e. WSDL Component model)
     * ************************************************************/
    
    /*
     * @see org.apache.woden.wsdl20.InterfaceFault#getName()
     * @see org.apache.woden.wsdl20.xml.InterfaceFaultElement#getName()
     */
    public QName getName() 
    {
        QName name = null;
        if (fName != null) {
            String tns = DescriptionImpl.getTargetNamespace(this);
            name = new QName(tns, fName.toString());
        }
        return name;
    }

    /*
     * @see org.apache.woden.wsdl20.InterfaceFault#getElementDeclaration()
     */
    public ElementDeclaration getElementDeclaration() 
    {
        Interface interfac = (Interface)getParent();
        Description desc = ((InterfaceImpl)interfac).getDescriptionComponent();
        return desc.getElementDeclaration(fElementName);
    }
    
    /*
     * @see org.apache.woden.wsdl20.InterfaceFault#toElement()
     */
    public InterfaceFaultElement toElement() {
        return this;
    }
    
    /* ************************************************************
     *  InterfaceFaultElement methods (i.e. XML Element model)
     * ************************************************************/
    
    /* 
     * @see org.apache.woden.wsdl20.xml.InterfaceFaultElement#setName(NCName)
     */
    public void setName(NCName name)
    {
        fName = name;
    }
    
    /* 
     * @see org.apache.woden.wsdl20.xml.InterfaceFaultElement#setElementName(QName)
     */
    public void setElementName(QName qname)
    {
        fElementName = qname;
    }
    
    /*
     * @see org.apache.woden.wsdl20.xml.InterfaceFaultElement#getElementName()
     */
    public QName getElementName() 
    {
        return fElementName;
    }

    /*
     * @see org.apache.woden.wsdl20.xml.InterfaceFaultElement#getElement()
     */
    public XmlSchemaElement getElement() 
    {
        XmlSchemaElement xse = null;
        InterfaceElement interfac = (InterfaceElement)getParentElement();
        DescriptionElement desc = (DescriptionElement)interfac.getParentElement();
        TypesElement types = desc.getTypesElement();
        if(types != null) {
            xse = ((TypesImpl)types).getElementDeclaration(fElementName);
        }
        return xse;
    }

    /* ************************************************************
     *  Non-API implementation methods
     * ************************************************************/
    
}

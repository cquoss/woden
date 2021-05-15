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

import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.ElementDeclaration;
import org.apache.woden.wsdl20.Interface;
import org.apache.woden.wsdl20.InterfaceMessageReference;
import org.apache.woden.wsdl20.InterfaceOperation;
import org.apache.woden.wsdl20.enumeration.Direction;
import org.apache.woden.wsdl20.enumeration.MessageLabel;
import org.apache.woden.wsdl20.xml.DescriptionElement;
import org.apache.woden.wsdl20.xml.InterfaceElement;
import org.apache.woden.wsdl20.xml.InterfaceMessageReferenceElement;
import org.apache.woden.wsdl20.xml.InterfaceOperationElement;
import org.apache.woden.wsdl20.xml.TypesElement;
import org.apache.ws.commons.schema.XmlSchemaElement;

/**
 * This class represents the &lt;input&gt; and &lt;output&gt; 
 * child elements of interface operation. 
 * 
 * @author jkaputin@apache.org
 */
public class InterfaceMessageReferenceImpl extends NestedConfigurableImpl
                                  implements InterfaceMessageReference, 
                                             InterfaceMessageReferenceElement 
{
    //WSDL Component model data
    private MessageLabel fMessageLabel = null;
    private Direction fDirection = null;
    private String fMessageContentModel = null;
    
    //XML Element model data
    private QName fElementName = null;
    
    
    /* ************************************************************
     *  InterfaceMessageReference methods (the WSDL Component model)
     * ************************************************************/
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.InterfaceMessageReference#getMessageLabel()
     * @see org.apache.woden.wsdl20.xml.InterfaceMessageReferenceElement#getMessageLabel()
     */
    public MessageLabel getMessageLabel() {
        return fMessageLabel;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.InterfaceMessageReference#getDirection()
     * @see org.apache.woden.wsdl20.xml.InterfaceMessageReferenceElement#getDirection()
     */
    public Direction getDirection() {
        return fDirection;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.InterfaceMessageReference#getMessageContentModel()
     * @see org.apache.woden.wsdl20.xml.InterfaceMessageReferenceElement#getMessageContentModel()
     */
    public String getMessageContentModel() {
        return fMessageContentModel;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.InterfaceMessageReference#getElementDeclaration()
     */
    public ElementDeclaration getElementDeclaration() 
    {
        InterfaceOperation oper = (InterfaceOperation)getParent();
        Interface interfac = (Interface)oper.getParent();
        Description desc = ((InterfaceImpl)interfac).getDescriptionComponent();
        ElementDeclaration elemDecl = desc.getElementDeclaration(fElementName);
        return elemDecl;
    }

    /*
     * @see org.apache.woden.wsdl20.InterfaceMessageReference#toElement()
     */
    public InterfaceMessageReferenceElement toElement() {
        return this;
    }
    
    /* ************************************************************
     *  InterfaceMessageReferenceElement methods (the XML Element model)
     * ************************************************************/
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.InterfaceMessageReferenceElement#setMessageLabel(org.apache.woden.wsdl20.enumeration.MessageLabel)
     */
    public void setMessageLabel(MessageLabel msgLabel) {
        fMessageLabel = msgLabel;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.InterfaceMessageReferenceElement#setMessageContentModel(java.lang.String)
     */
    public void setMessageContentModel(String nmToken) {
        fMessageContentModel = nmToken;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.InterfaceMessageReferenceElement#setElementName(javax.xml.namespace.QName)
     */
    public void setElementName(QName element) {
        fElementName = element;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.InterfaceMessageReferenceElement#getElementName()
     */
    public QName getElementName() {
        return fElementName;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.InterfaceMessageReferenceElement#getElement()
     */
    public XmlSchemaElement getElement() 
    {
        XmlSchemaElement xse = null;
        if(Constants.NMTOKEN_ELEMENT.equals(fMessageContentModel))
        {
            InterfaceOperationElement oper = (InterfaceOperationElement)getParentElement();
            InterfaceElement interfac = (InterfaceElement)oper.getParentElement();
            DescriptionElement desc = (DescriptionElement)interfac.getParentElement();
            TypesElement types = desc.getTypesElement();
            if(types != null) {
                xse = ((TypesImpl)types).getElementDeclaration(fElementName);
            }
        }
        return xse;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.InterfaceMessageReferenceElement#setDirection(org.apache.woden.wsdl20.enumeration.Direction)
     */
    public void setDirection(Direction dir) {
        fDirection = dir;
    }

    /* ************************************************************
     *  Non-API implementation methods
     * ************************************************************/
    
}

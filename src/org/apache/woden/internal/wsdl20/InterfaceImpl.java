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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.woden.types.NCName;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.Interface;
import org.apache.woden.wsdl20.InterfaceFault;
import org.apache.woden.wsdl20.InterfaceOperation;
import org.apache.woden.wsdl20.WSDLComponent;
import org.apache.woden.wsdl20.xml.InterfaceElement;
import org.apache.woden.wsdl20.xml.InterfaceFaultElement;
import org.apache.woden.wsdl20.xml.InterfaceOperationElement;
import org.apache.woden.wsdl20.xml.WSDLElement;

/**
 * This class represents the Interface component from the 
 * WSDL 2.0 Component Model and &lt;interface&gt; element.
 * 
 * @author jkaputin@apache.org
 */
public class InterfaceImpl extends ConfigurableImpl
                           implements Interface, InterfaceElement 
{
    private WSDLElement fParentElem = null;
    
    /* This field refers to the Description component which contains this Interface
     * component in its {interfaces} property. It is set whenever this Interface is 
     * returned by that Description's getInterfaces() or getInterface(QName) methods. 
     * Note that with modularization via a wsdl import or include, this 
     * reference may be different to fDescriptionElement because it refers to the 
     * importing or including description at the top of the wsdl tree (whereas the 
     * latter refers to the description in which this interface is directly declared).
     * This field is used to retrieve components that are available (i.e. in-scope) 
     * to the top-level Description component. e.g. it is used with interface extension 
     * to retrieve Interface components in this Interface's {extended interfaces} 
     * property from the set of Interfaces available (i.e. in-scope) to the Description.
     */ 
    private Description fDescriptionComponent = null;
    
    private NCName fName = null;
    private List fExtends = new Vector();
    private List fStyleDefault = new Vector();
    private List fInterfaceFaultElements = new Vector();
    private List fInterfaceOperationElements = new Vector();

    /* ************************************************************
     *  Interface interface methods (the WSDL Component model)
     * ************************************************************/
    
    /* 
     * @see org.apache.woden.wsdl20.Interface#getName()
     * @see org.apache.woden.wsdl20.xml.InterfaceElement#getName()
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
     * @see org.apache.woden.wsdl20.Interface#getExtendedInterface(javax.xml.namespace.QName)
     */
    public Interface getExtendedInterface(QName qname) 
    {
        Interface intface = fDescriptionComponent.getInterface(qname); 
        return intface;
    }
    
    /* 
     * @see org.apache.woden.wsdl20.Interface#getExtendedInterfaces()
     */
    public Interface[] getExtendedInterfaces() 
    {
        List interfaces = new Vector();
        for(Iterator it = fExtends.iterator(); it.hasNext();)
        {
            QName qn = (QName)it.next();
            Interface intface = getExtendedInterface(qn);
            if(intface != null) interfaces.add(intface);
        }
        
        Interface[] array = new Interface[interfaces.size()];
        interfaces.toArray(array);
        return array;
    }

    /* 
     * @see org.apache.woden.wsdl20.Interface#getInterfaceFaults()
     */
    public InterfaceFault[] getInterfaceFaults() 
    {
        InterfaceFault[] array = new InterfaceFault[fInterfaceFaultElements.size()];
        fInterfaceFaultElements.toArray(array);
        return array;
    }

    /* 
     * @see org.apache.woden.wsdl20.Interface#getInterfaceFault(javax.xml.namespace.QName)
     */
    public InterfaceFault getInterfaceFault(QName qname) 
    {
        return (InterfaceFault)getInterfaceFaultElement(qname);
    }
    
    /* 
     * @see org.apache.woden.wsdl20.Interface#getInterfaceOperations()
     */
    public InterfaceOperation[] getInterfaceOperations() 
    {
        InterfaceOperation[] array = new InterfaceOperation[fInterfaceOperationElements.size()];
        fInterfaceOperationElements.toArray(array);
        return array;
    }
    
    /* 
     * @see org.apache.woden.wsdl20.Interface#getInterfaceOperation(javax.xml.namespace.QName)
     */
    public InterfaceOperation getInterfaceOperation(QName qname) 
    {
        return (InterfaceOperation)getInterfaceOperationElement(qname);
    }
    
    /* 
     * @see org.apache.woden.wsdl20.Interface#toElement()
     */
    public InterfaceElement toElement() {
        return this;
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.WSDLComponent#equals(WSDLComponent)
     */
    public boolean equals(WSDLComponent comp)
    {
        //compare object refs
        if(this == comp) return true;
        
        if(!(comp instanceof Interface)) {
            return false;
        }
        
        Interface other = (Interface)comp;
        
        //compare {name}
        QName myName = getName();
        if(myName != null && !myName.equals(other.getName())) return false;
        
        /* To compare {extended interfaces} we cannot just retrieve and compare the two sets 
         * of extended Interface components because we'd enter a recursive loop. To get the
         * extended interfaces (i.e. to resolve the qnames in the 'extends' attribute)
         * we need to get the set of interfaces available to the Description, which in turn 
         * invokes this equivalence checking method.
         * Instead, compare just the qnames in the 'extends' attributes, but we first 
         * eliminate any duplicate qnames to ensure we make a logical test for 
         * equivalence (i.e. use Set comparison).
         */
        Set thisExtendsSet = new HashSet(fExtends);
        QName[] otherExtends = ((InterfaceElement)other).getExtendedInterfaceNames();
        Set otherExtendsSet = new HashSet(); 
        for(int i=0; i<otherExtends.length; i++)
        {
            otherExtendsSet.add(otherExtends[i]);
        }
        if(thisExtendsSet.size() != otherExtendsSet.size()) return false;
        if(!(thisExtendsSet.containsAll(otherExtendsSet) && otherExtendsSet.containsAll(thisExtendsSet)))
        {
            return false;
        }
        
        //TODO compare {interface faults}
        //TODO compare {interface operations}
        //TODO compare {features}
        //TODO compare {properties}
            
        return true;    
    }
    
    /* ************************************************************
     *  InterfaceElement interface methods (the XML Element model)
     * ************************************************************/
    
    /* 
     * @see org.apache.woden.wsdl20.xml.InterfaceElement#setName(NCName)
     */
    // TODO: need to store NCName and not QName because it is valid for somone
    // to create an InterfaceImpl and set the name without first having added
    // the interfaceimpl to the DescriptionElement.
    public void setName(NCName name) {
        fName = name;
    }
    
    /* 
     * @see org.apache.woden.wsdl20.xml.InterfaceElement#addStyleDefaultURI(URI)
     */
    public void addStyleDefaultURI(URI uri)
    {
        if(uri != null) {
            fStyleDefault.add(uri);
        }
    }
    
    /* 
     * @see org.apache.woden.wsdl20.xml.InterfaceElement#getStyleDefault()
     */
    public URI[] getStyleDefault()
    {
        URI[] array = new URI[fStyleDefault.size()];
        fStyleDefault.toArray(array);
        return array;
    }
    
    /* 
     * @see org.apache.woden.wsdl20.xml.InterfaceElement#addExtendedInterfaceName(javax.xml.namespace.QName)
     */
    public void addExtendedInterfaceName(QName qname)
    {
        if(qname != null) {
            fExtends.add(qname);
        }
    }
    
    /* 
     * @see org.apache.woden.wsdl20.xml.InterfaceElement#removeExtendedInterfaceName(javax.xml.namespace.QName)
     */
    public void removeExtendedInterfaceName(QName qname)
    {
        if(qname != null) {
            fExtends.remove(qname);
        }
    }
    
    /* 
     * @see org.apache.woden.wsdl20.xml.InterfaceElement#getExtendedInterfaceNames()
     */
    public QName[] getExtendedInterfaceNames()
    {
        QName[] array = new QName[fExtends.size()];
        fExtends.toArray(array);
        return array;
    }
    
    /* 
     * @see org.apache.woden.wsdl20.xml.InterfaceElement#getExtendedInterfaceElement(javax.xml.namespace.QName)
     */
    public InterfaceElement getExtendedInterfaceElement(QName qname)
    {
        //Cast the containing description element to a description component to re-use its
        //logic for navigating a composite wsdl to retrieve the in-scope top-level components.
        Description desc = (Description)fParentElem;
        InterfaceElement intface = (InterfaceElement)desc.getInterface(qname); 
        return intface;
    }
    
    /* 
     * @see org.apache.woden.wsdl20.xml.InterfaceElement#getExtendedInterfaceElements()
     */
    public InterfaceElement[] getExtendedInterfaceElements()
    {
        List interfaces = new Vector();
        for(Iterator it = fExtends.iterator(); it.hasNext();)
        {
            QName qn = (QName)it.next();
            InterfaceElement intface = getExtendedInterfaceElement(qn);
            if(intface != null) interfaces.add(intface);
        }
        
        InterfaceElement[] array = new InterfaceElement[interfaces.size()];
        interfaces.toArray(array);
        return array;
    }
    
    /* 
     * @see org.apache.woden.wsdl20.xml.InterfaceElement#addInterfaceFaultElement()
     */
    public InterfaceFaultElement addInterfaceFaultElement()
    {
        InterfaceFaultElement fault = new InterfaceFaultImpl();
        fInterfaceFaultElements.add(fault);
        fault.setParentElement(this);
        return fault;
    }

    /* 
     * @see org.apache.woden.wsdl20.xml.InterfaceElement#getInterfaceFaultElement(javax.xml.namespace.QName)
     */
    public InterfaceFaultElement getInterfaceFaultElement(QName qname)
    {
        InterfaceFaultElement fault = null;
        
        if(qname != null)
        {
            InterfaceFaultElement tempFault = null;
            for(Iterator i=fInterfaceFaultElements.iterator(); i.hasNext(); )
            {
                tempFault = (InterfaceFaultElement)i.next();
                if(qname.equals(tempFault.getName()))
                {
                    fault = tempFault;
                    break;
                }
            }
        }
        
        return fault;
    }

    /* 
     * @see org.apache.woden.wsdl20.xml.InterfaceElement#getInterfaceFaultElements()
     */
    public InterfaceFaultElement[] getInterfaceFaultElements()
    {
        InterfaceFaultElement[] array = new InterfaceFaultElement[fInterfaceFaultElements.size()];
        fInterfaceFaultElements.toArray(array);
        return array;
    }

    /* 
     * @see org.apache.woden.wsdl20.xml.InterfaceElement#addInterfaceOperationElement()
     */
    public InterfaceOperationElement addInterfaceOperationElement()
    {
        InterfaceOperationElement operation = new InterfaceOperationImpl();
        fInterfaceOperationElements.add(operation);
        operation.setParentElement(this);
        return operation;
    }
    
    /* 
     * @see org.apache.woden.wsdl20.xml.InterfaceElement#getInterfaceOperationElement(javax.xml.namespace.QName)
     */
    public InterfaceOperationElement getInterfaceOperationElement(QName qname)
    {
        InterfaceOperationElement oper = null;
        
        if(qname != null)
        {
            InterfaceOperationElement tempOper = null;
            for(Iterator i=fInterfaceOperationElements.iterator(); i.hasNext(); )
            {
                tempOper = (InterfaceOperationElement)i.next();
                if(qname.equals(tempOper.getName()))
                {
                    oper = tempOper;
                    break;
                }
            }
        }
        
        return oper;
    }

    /* 
     * @see org.apache.woden.wsdl20.xml.InterfaceElement#getInterfaceOperationElements()
     */
    public InterfaceOperationElement[] getInterfaceOperationElements()
    {
        InterfaceOperationElement[] array = new InterfaceOperationElement[fInterfaceOperationElements.size()];
        fInterfaceOperationElements.toArray(array);
        return array;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.NestedElement#setParentElement(org.apache.woden.wsdl20.xml.WSDL20Element)
     */
    public void setParentElement(WSDLElement parent) {
        fParentElem = parent;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.NestedElement#getParentElement()
     */
    public WSDLElement getParentElement() {
        return fParentElem;
    }
    
    /* ************************************************************
     *  Non-API implementation methods
     * ************************************************************/

    /*
     * This method searches for the QName-specified operation across all operations
     * available (or in-scope) to this interface. i.e. it searches the directly declared
     * operations and the operations derived from any extended interfaces.
     * 
     * TODO consider whether this is needed on the API (currently just used by validator)
     * TODO also consider whether getInScopeInterfaceOperations() is needed. 
     */
    public InterfaceOperation getInScopeInterfaceOperation(QName qname) 
    {
        InterfaceOperation oper = null;
        
        if(qname != null)
        {
            //First, try the operations declared directly in this interface
            oper = getInterfaceOperation(qname);
            
            //Next, try any operations derived from extended interfaces
            if(oper == null)
            {
                List derivedOperations = getDerivedInterfaceOperations();
                InterfaceOperation tempOper = null;
                for(Iterator j=derivedOperations.iterator(); j.hasNext(); )
                {
                    tempOper = (InterfaceOperation)j.next();
                    if(qname.equals(tempOper.getName()))
                    {
                        oper = tempOper;
                        break;
                    }
                }
            }
        }
        return oper;
    }
    
    /*
     * Return the interface operations derived from extended interfaces. 
     */
    private List getDerivedInterfaceOperations()
    {
        List derivedOperations = new Vector();
        Interface[] interfaces = getExtendedInterfaces();
        for(int i=0; i<interfaces.length; i++)
        {
            Interface interfac = (Interface)interfaces[i];
            InterfaceOperation[] opers = interfac.getInterfaceOperations();
            for(int j=0; j<opers.length; j++)
            {
                InterfaceOperation oper = opers[j];
                if(!containsComponent(oper, derivedOperations)) {
                    derivedOperations.add(oper);
                }
            }
        }
        return derivedOperations;
    }
    
    /*
     * This method searches for the QName-specified fault across all faults
     * available (or in-scope) to this interface. i.e. it searches the directly declared
     * faults and the faults derived from any extended interfaces.
     * 
     * TODO consider whether this is needed on the API (currently just used by validator) 
     * TODO also consider whether getInScopeInterfaceFaults() is needed. 
     */
    public InterfaceFault getInScopeInterfaceFault(QName qname) 
    {
        InterfaceFault fault = null;
        
        if(qname != null)
        {
            //First, try the faults declared directly in this interface
            fault = getInterfaceFault(qname);
            
            //Next, try any faults derived from extended interfaces
            if(fault == null)
            {
                List derivedFaults = getDerivedInterfaceFaults();
                InterfaceFault tempFault = null;
                for(Iterator j=derivedFaults.iterator(); j.hasNext(); )
                {
                    tempFault = (InterfaceFault)j.next();
                    if(qname.equals(tempFault.getName()))
                    {
                        fault = tempFault;
                        break;
                    }
                }
            }
        }
        return fault;
    }
    
    /*
     * Return the interface faults derived from extended interfaces. 
     */
    private List getDerivedInterfaceFaults()
    {
        List derivedFaults = new Vector();
        Interface[] interfaces = getExtendedInterfaces();
        for(int i=0; i<interfaces.length; i++)
        {
            Interface interfac = (Interface)interfaces[i];
            InterfaceFault[] faults = interfac.getInterfaceFaults();
            for(int j=0; j<faults.length; j++)
            {
                InterfaceFault fault = faults[j];
                if(!containsComponent(fault, derivedFaults)) {
                    derivedFaults.add(fault);
                }
            }
        }
        return derivedFaults;
    }
    
    /*
     * These package private accessors refer to the Description component
     * in which this Interface is contained (i.e. contained in its {interfaces}
     * property). They are declared package private so that they can be used by the
     * Woden implementation without exposing them to the API (i.e. by DescriptionImpl)
     */
    void setDescriptionComponent(Description desc)
    {
        fDescriptionComponent = desc;
    }
    
    Description getDescriptionComponent() {
        return fDescriptionComponent;
    }
    
}

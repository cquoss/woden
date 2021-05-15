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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.woden.internal.util.ComponentModelBuilder;
import org.apache.woden.wsdl20.Binding;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.ElementDeclaration;
import org.apache.woden.wsdl20.Interface;
import org.apache.woden.wsdl20.Service;
import org.apache.woden.wsdl20.TypeDefinition;
import org.apache.woden.wsdl20.extensions.ExtensionRegistry;
import org.apache.woden.wsdl20.xml.BindingElement;
import org.apache.woden.wsdl20.xml.DescriptionElement;
import org.apache.woden.wsdl20.xml.ImportElement;
import org.apache.woden.wsdl20.xml.IncludeElement;
import org.apache.woden.wsdl20.xml.InterfaceElement;
import org.apache.woden.wsdl20.xml.NestedElement;
import org.apache.woden.wsdl20.xml.ServiceElement;
import org.apache.woden.wsdl20.xml.TypesElement;
import org.apache.woden.wsdl20.xml.WSDLElement;

/**
 * This class provides the implementation for a Description component from 
 * the WSDL Component Model, as described in the WSDL 2.0 specification.  
 * <p>
 * Note: this class is different to the other WSDL implementation classes, 
 * which all implement two Java interfaces; a component model interface and the 
 * interface for the corresponding WSDL element. Different implementations
 * are used for the Description component and for the &lt;wsdl:description&gt; 
 * element because the latter exposes the composite structure of imported and 
 * included WSDL documents, while the Description component 'flattens' this
 * structure into an abstract view of the WSDL. A separate implementation
 * class, <code>DescriptionElementImpl</code>, exists to represent
 * the &lt;wsdl:description&gt; element.
 * 
 * @author jkaputin@apache.org
 */
public class DescriptionImpl extends DocumentableImpl
                             implements Description, DescriptionElement 
{
    /*
     * WSDL Component model data (flattened properties of Description Component)
     * TODO cache top-level components here with a flush-on-update mechanism
     */
    private List fAllInterfaces = null;
    private List fAllBindings = null;
    private List fAllServices = null;
    private List fAllElementDeclarations = new Vector();
    private List fAllTypeDefinitions = new Vector();
    
    /*
     * WSDL Element model data
     */
    private URI fDocumentBaseURI = null;

    //<description> attributes
    private URI fTargetNamespace = null;
    private Map fNamespaces = new HashMap();

    //<description> child elements
    private List fImportElements = new Vector();
    private List fIncludeElements = new Vector();
    private TypesElement fTypesElement = null;
    private List fInterfaceElements = new Vector();
    private List fBindingElements = new Vector();
    private List fServiceElements = new Vector();
    
    /*
     * Woden-specific instance variables
     */
    
    private boolean fComponentsInitialized = false;
    private ExtensionRegistry fExtReg = null;
    
    
    /* ************************************************************
     *  Description interface methods (the WSDL Component model)
     * ************************************************************/
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.Description#getInterfaces()
     * 
     * TODO performance to be addressed in this method. Suggest caching with
     * some sort of notification-on-update to flush the cash and let lazy init
     * reset it. But best to deal with performance after wsdl create/update has 
     * been implemented, as we can then baseline the existing dynamic approach
     * to test for correct behaviour after performance mods have been made. 
     * (but see to do comment in getNestedDescriptions method too)
     */
    public Interface[] getInterfaces() 
    {
        List allInterfaces = new Vector();
        
        //declared interfaces
        for(Iterator i=fInterfaceElements.iterator(); i.hasNext(); )
        {
            Interface intface = (Interface)i.next();
            if(!containsComponent(intface, allInterfaces)) {
                ((InterfaceImpl)intface).setDescriptionComponent(this);
                allInterfaces.add(intface);
            }
        }
        
        //nested interfaces
        List nestedDescs = new Vector(getNestedDescriptions());
        for(Iterator i=nestedDescs.iterator(); i.hasNext(); )
        {
            DescriptionElement desc = (DescriptionElement)i.next();
            InterfaceElement[] interfaces = desc.getInterfaceElements();
            for(int j=0; j<interfaces.length; j++)
            {
                Interface intface = (Interface)interfaces[j];
                if(!containsComponent(intface, allInterfaces)) {
                    ((InterfaceImpl)intface).setDescriptionComponent(this);
                    allInterfaces.add(intface);
                }
            }
        }
        
        Interface[] array = new Interface[allInterfaces.size()];
        allInterfaces.toArray(array);
        return array;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.Description#getInterface(javax.xml.namespace.QName)
     */
    public Interface getInterface(QName name) 
    {
        Interface intface = null;
        
        if(name != null) 
        {
            Interface[] interfaces = getInterfaces();
            
            for(int i=0; i<interfaces.length; i++)
            {
                if(name.equals(interfaces[i].getName()))
                {
                    intface = interfaces[i];
                    break;
                }
            }
        }
        
        return intface;
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.Description#getBindings()
     * 
     * TODO performance to be addressed in this method - see ToDo in getInterface()
     */
    public Binding[] getBindings() 
    {
        List allBindings = new Vector();
        
        //declared bindings
        for(Iterator i=fBindingElements.iterator(); i.hasNext(); )
        {
            Binding binding = (Binding)i.next();
            if(!containsComponent(binding, allBindings)) {
                ((BindingImpl)binding).setDescriptionComponent(this);
                allBindings.add(binding);
            }
        }
        
        //nested bindings
        List nestedDescs = new Vector(getNestedDescriptions());
        for(Iterator i=nestedDescs.iterator(); i.hasNext(); )
        {
            DescriptionElement desc = (DescriptionElement)i.next();
            BindingElement[] bindings = desc.getBindingElements();
            for(int j=0; j<bindings.length; j++)
            {
                Binding binding = (Binding)bindings[j];
                if(!containsComponent(binding, allBindings)) {
                    ((BindingImpl)binding).setDescriptionComponent(this);
                    allBindings.add(binding);
                }
            }
        }
        
        Binding[] array = new Binding[allBindings.size()];
        allBindings.toArray(array);
        return array;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.Description#getBinding(javax.xml.namespace.QName)
     */
    public Binding getBinding(QName name) 
    {
        Binding binding = null;
        
        if(name != null) 
        {
            Binding[] bindings = getBindings();
            
            for(int i=0; i<bindings.length; i++)
            {
                if(name.equals(bindings[i].getName()))
                {
                    binding = bindings[i];
                    break;
                }
            }
        }
        
        return binding;
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.Description#getServices()
     * 
     * TODO performance to be addressed in this method - see ToDo in getInterface()
     */
    public Service[] getServices() 
    {
        List allServices = new Vector();
        
        //declared services
        for(Iterator i=fServiceElements.iterator(); i.hasNext(); )
        {
            Service service = (Service)i.next();
            if(!containsComponent(service, allServices)) {
                ((ServiceImpl)service).setDescriptionComponent(this);
                allServices.add(service);
            }
        }
        
        //nested services
        List nestedDescs = new Vector(getNestedDescriptions());
        for(Iterator i=nestedDescs.iterator(); i.hasNext(); )
        {
            DescriptionElement desc = (DescriptionElement)i.next();
            ServiceElement[] services = desc.getServiceElements();
            for(int j=0; j<services.length; j++)
            {
                Service service = (Service)services[j];
                if(!containsComponent(service, allServices)) {
                    ((ServiceImpl)service).setDescriptionComponent(this);
                    allServices.add(service);
                }
            }
        }
        
        Service[] array = new Service[allServices.size()];
        allServices.toArray(array);
        return array;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.Description#getService(javax.xml.namespace.QName)
     */
    public Service getService(QName name) 
    {
        Service service = null;
        
        if(name != null) 
        {
            Service[] services = getServices();
            
            for(int i=0; i<services.length; i++)
            {
                if(name.equals(services[i].getName()))
                {
                    service = services[i];
                    break;
                }
            }
        }
        
        return service;
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.Description#getElementDeclarations()
     */
    public ElementDeclaration[] getElementDeclarations() 
    {
        if(!fComponentsInitialized) initComponents();
        ElementDeclaration[] array = new ElementDeclaration[fAllElementDeclarations.size()];
        fAllElementDeclarations.toArray(array);
        return array;
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.Description#getElementDeclaration(javax.xml.namespace.QName)
     */
    public ElementDeclaration getElementDeclaration(QName qname)
    {
        if(!fComponentsInitialized) initComponents();
        ElementDeclaration elDec = null;
        if(qname != null)
        {
            Iterator i = fAllElementDeclarations.iterator();
            while(i.hasNext())
            {
                ElementDeclaration ed = (ElementDeclaration)i.next();
                if(qname.equals(ed.getName())) 
                {
                    elDec = ed;
                    break;
                }
            }
        }
        return elDec;
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.Description#getTypeDefinitions()
     */
    public TypeDefinition[] getTypeDefinitions() 
    {
        if(!fComponentsInitialized) initComponents();
        TypeDefinition[] array = new TypeDefinition[fAllTypeDefinitions.size()];
        fAllTypeDefinitions.toArray(array);
        return array;
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.Description#getTypeDefinition(javax.xml.namespace.QName)
     * TODO consider using Map instead of List
     */
    public TypeDefinition getTypeDefinition(QName qname)
    {
        if(!fComponentsInitialized) initComponents();
        TypeDefinition typeDef = null;
        if(qname != null)
        {
            Iterator i = fAllTypeDefinitions.iterator();
            while(i.hasNext())
            {
                TypeDefinition td = (TypeDefinition)i.next();
                if(qname.equals(td.getName())) 
                {
                    typeDef = td;
                    break;
                }
            }
        }
        return typeDef;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.Description#toElement()
     */
    public DescriptionElement toElement()
    {
        return this; 
    }
    
    /* ************************************************************
     *  DescriptionElement interface methods (the XML Element model)
     * ************************************************************/
    
    public void setDocumentBaseURI(URI documentBaseURI) {
        fDocumentBaseURI = documentBaseURI;
    }
    
    public URI getDocumentBaseURI() {
        return fDocumentBaseURI;
    }
    
    public void setTargetNamespace(URI namespace) {
        fTargetNamespace = namespace;    
    }
    
    public URI getTargetNamespace() {
        return fTargetNamespace;    
    }

    public void addNamespace(String prefix, URI namespace) 
    {
        String pfx = (prefix != null) ? prefix : "";
        if (namespace != null) {
            fNamespaces.put(pfx,namespace);
        } else {
            removeNamespace(pfx);
        }
    }
    
    public void removeNamespace(String prefix) 
    {
        String pfx = (prefix != null) ? prefix : "";
        fNamespaces.remove(pfx);
    }
    
    public URI getNamespace(String prefix) 
    {
        String pfx = (prefix != null) ? prefix : "";
        return (URI) fNamespaces.get(pfx);
    }
    
    public Map getNamespaces() 
    {
        return fNamespaces;
    }

    public ImportElement[] getImportElements()
    {
        ImportElement[] array = new ImportElement[fImportElements.size()];
        fImportElements.toArray(array);
        return array;
    }
    
    public IncludeElement[] getIncludeElements()
    {
        IncludeElement[] array = new IncludeElement[fIncludeElements.size()];
        fIncludeElements.toArray(array);
        return array;
    }

    public TypesElement getTypesElement() 
    {
        if (fTypesElement == null) {
            fTypesElement = new TypesImpl();
            fTypesElement.setParentElement(this);
        }
        return fTypesElement;    
    }
    
    public InterfaceElement[] getInterfaceElements() 
    {
        InterfaceElement[] array = new InterfaceElement[fInterfaceElements.size()];
        fInterfaceElements.toArray(array);
        return array;
    }
    
    public BindingElement[] getBindingElements() 
    {
        BindingElement[] array = new BindingElement[fBindingElements.size()];
        fBindingElements.toArray(array);
        return array;
    }
    
    public ServiceElement[] getServiceElements() 
    {
        ServiceElement[] array = new ServiceElement[fServiceElements.size()];
        fServiceElements.toArray(array);
        return array;
    }
    
    //creator methods
    
    public ImportElement addImportElement() {
        ImportElement importEl = new ImportImpl();
        fImportElements.add(importEl);
        return importEl;
    }
    
    public IncludeElement addIncludeElement() {
        IncludeElement include = new IncludeImpl();
        fIncludeElements.add(include);
        return include;
    }
    
    public InterfaceElement addInterfaceElement() {
        InterfaceElement intface = new InterfaceImpl();
        fInterfaceElements.add(intface);
        intface.setParentElement(this);

        return intface; 
    }
    
    public BindingElement addBindingElement() {
        BindingElement binding = new BindingImpl();
        fBindingElements.add(binding);
        binding.setParentElement(this);
        return binding;
    }
    
    public ServiceElement addServiceElement() {
        ServiceElement service = new ServiceImpl();
        fServiceElements.add(service);
        service.setParentElement(this);
        return service;
    }
    
    public void setExtensionRegistry(ExtensionRegistry extReg)
    {
        fExtReg = extReg;
    }
    
    public ExtensionRegistry getExtensionRegistry()
    {
        return fExtReg;
    }
    
    /*
     * @see org.apache.woden.wsdl20.xml.DescriptionElement#toComponent()
     */
    public Description toComponent()
    {
        //TODO synchronizing the Component data when Element model is modified
        if(!fComponentsInitialized) {
            initComponents();
        }
        return this;
    }
    
    /* ************************************************************
     *  Non-API implementation methods
     * ************************************************************/

    public void addElementDeclaration(ElementDeclaration elDec) 
    {
        if(elDec != null) {
            fAllElementDeclarations.add(elDec);
        }
    }

    public void addTypeDefinition(TypeDefinition typeDef) 
    {
        if(typeDef != null) {
            fAllTypeDefinitions.add(typeDef);
        }
    }
    
    private void initComponents() 
    {
        //TODO consider moving the builder logic inside this class, maybe as an inner class.
        fComponentsInitialized = true;
        new ComponentModelBuilder(this);
    }
    
    /*
     * This method returns the descriptions included by this description (using transitive closure)
     * and the descriptions for any namespaces imported directly by this description (i.e. not transitive).
     * It is a helper method for other methods that need to walk the wsdl tree to access the
     * flattened collection of wsdl components available to this description.
     * 
     * TODO imports should be pervasive, components not limited to a single location attribute.
     * TODO consider performance here (e.g. caching with flush-on-update notification) because this method
     * will be used by getters for the top-level components (interfaces, bindings, services). 
     */
    private List getNestedDescriptions()
    {
        List descs = new Vector();
        
        //includes are transitive, so navigate the include tree
        collectIncludedDescriptions(descs, this); 
        
        //imports are non-transitive, so just get the directly imported descriptions
        //and any includes within them.
        ImportElement[] imports = getImportElements();
        for(int i = 0; i < imports.length; i++)
        {
            DescriptionElement desc = imports[i].getDescriptionElement();
            if(desc != null) descs.add(desc);
            collectIncludedDescriptions(descs, desc);
        }
        
        return descs;
    }
    
    private void collectIncludedDescriptions(List descs, DescriptionElement desc)
    {
        IncludeElement[] includes = desc.getIncludeElements();
        for(int i = 0; i < includes.length; i++)
        {
            DescriptionElement includedDesc = includes[i].getDescriptionElement();
            if(includedDesc != null && !descs.contains(includedDesc)) 
            {
                descs.add(includedDesc);
            }
            collectIncludedDescriptions(descs, includedDesc);
        }
    }
    
    /**
     * Nested elements within a <code>&lt;description&gt;</code> have attributes of type
     * <i>xs:NCName</i>. The setter method for the attribute will take an NCName object as
     * the input parameter. However, to be useful, the getter method returns a QName. The
     * namespace within the QName has to be the targetNamespace of the <code>&lt;description&gt;</code>
     * element. This method provides a way to retrieve the targetNamespace of the
     * enclosing <code>&lt;description&gt;</code> element.
     * @param wElem instance of WSDLElement for which the targetNamespace is required 
     * @return the targetNamespace of the DescriptionElement that is the root element of wElem
     */
    static String getTargetNamespace(WSDLElement wElem) {
        String namespace = "";

        if (wElem instanceof NestedElement) {
            WSDLElement parent = ((NestedElement) wElem).getParentElement();
            return getTargetNamespace(parent);
        }
        if (wElem instanceof DescriptionElement) {
            URI tns = ((DescriptionElement) wElem).getTargetNamespace();
            if (tns != null) {
                namespace = tns.toString();
            }
            return namespace;
        }

        // We either have a null or a subclass of WSDLElement which isn't an instance of
        // NestedElement or DescriptionElement - and there aren't any of those
        // Return ""
        return "";
    }

    
}

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
package org.apache.woden.wsdl20.xml;

import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.woden.internal.wsdl20.DescriptionImpl;

/**
 * Test cases to check when calling XxxxElement.addYyyyElement() that:
 * a) Xxxx.getYyyyElements() returns an array containing the added YyyyElement
 * b) The YyyyElement.getParentElement() returns the instance of XxxxElement
 * 
 * Do this for all Xxxx and Yyyy
 */
public class ChildElementCreationTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ChildElementCreationTest.class);
    }

    public static Test suite() {
        return new TestSuite(ChildElementCreationTest.class);
    }
    
    
    // Elements addable to Binding
    public void testBindingFaultElement() {
        DescriptionElement descEl = new DescriptionImpl();
        BindingElement bindEl = descEl.addBindingElement();

        BindingFaultElement bindFEl = bindEl.addBindingFaultElement();
        
        assertTrue("BindingElement doesn't have correct BindingFaultElement",
                Arrays.asList(bindEl.getBindingFaultElements()).contains(bindFEl));
        assertTrue("BindingFaultElement has incorrect parent", bindFEl.getParentElement() == bindEl);
    }
    
    public void testBindingOperationElement() {
        DescriptionElement descEl = new DescriptionImpl();
        BindingElement bindEl = descEl.addBindingElement();

        BindingOperationElement bindOpEl = bindEl.addBindingOperationElement();
        
        assertTrue("BindingElement doesn't have correct BindingOperationElement",
                Arrays.asList(bindEl.getBindingOperationElements()).contains(bindOpEl));
        assertTrue("BindingOperationElement has incorrect parent", bindOpEl.getParentElement() == bindEl);
    }
    
    public void testBindingFaultReferenceElement() {
        DescriptionElement descEl = new DescriptionImpl();
        BindingElement bindEl = descEl.addBindingElement();
        BindingOperationElement bindOpEl = bindEl.addBindingOperationElement();
        BindingFaultReferenceElement bindFREl = bindOpEl.addBindingFaultReferenceElement();
        
        assertTrue("BindingOperationElement doesn't have correct BindingFaultReferenceElement",
                Arrays.asList(bindOpEl.getBindingFaultReferenceElements()).contains(bindFREl));
        assertTrue("BindingOperationElement has incorrect parent", bindOpEl.getParentElement() == bindEl);
    }

    public void testBindingMessageReferenceElement() {
        DescriptionElement descEl = new DescriptionImpl();
        BindingElement bindEl = descEl.addBindingElement();
        BindingOperationElement bindOpEl = bindEl.addBindingOperationElement();
        BindingMessageReferenceElement bindMREl = bindOpEl.addBindingMessageReferenceElement();
        
        assertTrue("BindingOperationElement doesn't have correct BindingMessageReferenceElement",
                Arrays.asList(bindOpEl.getBindingMessageReferenceElements()).contains(bindMREl));
        assertTrue("BindingOperationElement has incorrect parent", bindOpEl.getParentElement() == bindEl);
    }

    // Elements addable to Interface
    public void testInterfaceFaultElement() {
        DescriptionElement descEl = new DescriptionImpl();
        InterfaceElement interfaceEl = descEl.addInterfaceElement();

        InterfaceFaultElement interfaceFEl = interfaceEl.addInterfaceFaultElement();
        
        assertTrue("InterfaceElement doesn't have correct InterfaceFaultElement",
                Arrays.asList(interfaceEl.getInterfaceFaultElements()).contains(interfaceFEl));
        assertTrue("InterfaceFaultElement has incorrect parent", interfaceFEl.getParentElement() == interfaceEl);
    }

    public void testInterfaceOperationElement() {
        DescriptionElement descEl = new DescriptionImpl();
        InterfaceElement interfaceEl = descEl.addInterfaceElement();

        InterfaceOperationElement interfaceOpEl = interfaceEl.addInterfaceOperationElement();
        
        assertTrue("InterfaceElement doesn't have correct InterfaceOperationElement",
                Arrays.asList(interfaceEl.getInterfaceOperationElements()).contains(interfaceOpEl));
        assertTrue("InterfaceOperationElement has incorrect parent", interfaceOpEl.getParentElement() == interfaceEl);
    }
    
    public void testInterfaceFaultReferenceElement() {
        DescriptionElement descEl = new DescriptionImpl();
        InterfaceElement interfaceEl = descEl.addInterfaceElement();
        InterfaceOperationElement interfaceOpEl = interfaceEl.addInterfaceOperationElement();
        InterfaceFaultReferenceElement interfaceFREl = interfaceOpEl.addInterfaceFaultReferenceElement();
        
        assertTrue("InterfaceOperationElement doesn't have correct InterfaceFaultReferenceElement",
                Arrays.asList(interfaceOpEl.getInterfaceFaultReferenceElements()).contains(interfaceFREl));
        assertTrue("InterfaceFaultReferenceElement has incorrect parent", interfaceFREl.getParentElement() == interfaceOpEl);
    }

    public void testInterfaceMessageReferenceElement() {
        DescriptionElement descEl = new DescriptionImpl();
        InterfaceElement interfaceEl = descEl.addInterfaceElement();
        InterfaceOperationElement interfaceOpEl = interfaceEl.addInterfaceOperationElement();
        InterfaceMessageReferenceElement interfaceMREl = interfaceOpEl.addInterfaceMessageReferenceElement();
        
        assertTrue("InterfaceOperationElement doesn't have correct InterfaceMessageReferenceElement",
                Arrays.asList(interfaceOpEl.getInterfaceMessageReferenceElements()).contains(interfaceMREl));
        assertTrue("InterfaceMessageReferenceElement has incorrect parent", interfaceMREl.getParentElement() == interfaceOpEl);
    }
    
    public void testEndpointElement() {
        DescriptionElement descEl = new DescriptionImpl();
        ServiceElement serviceEl = descEl.addServiceElement();
        EndpointElement endpointEl = serviceEl.addEndpointElement();
        
        assertTrue("ServiceElement doesn't have correct EndpointElement",
                Arrays.asList(serviceEl.getEndpointElements()).contains(endpointEl));
        assertTrue("EndpointElement has incorrect parent", endpointEl.getParentElement() == serviceEl);
    }
}

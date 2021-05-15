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

package org.apache.woden.ant;

import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.woden.wsdl20.Binding;
import org.apache.woden.wsdl20.BindingFault;
import org.apache.woden.wsdl20.BindingFaultReference;
import org.apache.woden.wsdl20.BindingMessageReference;
import org.apache.woden.wsdl20.BindingOperation;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.ElementDeclaration;
import org.apache.woden.wsdl20.Endpoint;
import org.apache.woden.wsdl20.Interface;
import org.apache.woden.wsdl20.InterfaceFault;
import org.apache.woden.wsdl20.InterfaceFaultReference;
import org.apache.woden.wsdl20.InterfaceMessageReference;
import org.apache.woden.wsdl20.InterfaceOperation;
import org.apache.woden.wsdl20.Service;
import org.apache.woden.wsdl20.TypeDefinition;
import org.apache.woden.wsdl20.extensions.ComponentExtensions;
import org.apache.woden.wsdl20.extensions.InterfaceOperationExtensions;
import org.apache.woden.wsdl20.extensions.http.HTTPBindingExtensions;
import org.apache.woden.wsdl20.extensions.http.HTTPBindingFaultExtensions;
import org.apache.woden.wsdl20.extensions.http.HTTPBindingMessageReferenceExtensions;
import org.apache.woden.wsdl20.extensions.http.HTTPBindingOperationExtensions;
import org.apache.woden.wsdl20.extensions.http.HTTPEndpointExtensions;
import org.apache.woden.wsdl20.extensions.rpc.RPCInterfaceOperationExtensions;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingExtensions;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingFaultExtensions;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingFaultReferenceExtensions;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingMessageReferenceExtensions;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingOperationExtensions;

/**
 * @author Arthur Ryman (ryman@ca.ibm.com, arthur.ryman@gmail.com)
 *
 */
public class CmWriter extends NamespaceWriter {

    public static String NS = "http://www.w3.org/2002/ws/desc/wsdl/component";

    public static String PREFIX = "";

    /** XML Schema namespace */
    private static String XSD_NS = "http://www.w3.org/2001/XMLSchema";

    /** build-in simple types */
    private static String[] XSD_TYPES = { "string", "boolean", "decimal",
            "float", "double", "duration", "dateTime", "time", "date",
            "gYearMonth", "gYear", "gMonthDay", "gDay", "gMonth", "hexBinary",
            "base64Binary", "anyURI", "QName", "NOTATION", "normalizedString",
            "token", "language", "NMTOKEN", "NMTOKENS", "Name", "NCName", "ID",
            "IDREF", "IDREFS", "ENTITY", "ENTITIES", "integer",
            "nonPositiveInteger", "negativeInteger", "long", "int", "short",
            "byte", "nonNegativeInteger", "unsignedLong", "unsignedInt",
            "unsignedShort", "unsignedByte", "positiveInteger" };

    {
        Arrays.sort(XSD_TYPES);
    }

    private CmBaseWriter cmbase;

    private CmExtensionsWriter cmextensions;

    private CmRpcWriter cmrpc;

    private CmHttpWriter cmhttp;

    private CmSoapWriter cmsoap;

    public CmWriter(XMLWriter out) {

        super(out, NS, PREFIX);

        cmbase = (CmBaseWriter) out.lookup(CmBaseWriter.NS);
        cmextensions = (CmExtensionsWriter) out.lookup(CmExtensionsWriter.NS);
        cmrpc = (CmRpcWriter) out.lookup(CmRpcWriter.NS);
        cmhttp = (CmHttpWriter) out.lookup(CmHttpWriter.NS);
        cmsoap = (CmSoapWriter) out.lookup(CmSoapWriter.NS);
    }

    /**
     * Writes the component model.
     * 
     * @param component the root Description component
     */
    public void write(Description component) {

        out.xmlDeclaration("UTF-8");

        String attributes = "xmlns='" + NS + "'\n";
        attributes += " xmlns:" + CmExtensionsWriter.PREFIX + "='"
                + CmExtensionsWriter.NS + "'\n";
        attributes += " xmlns:" + CmBaseWriter.PREFIX + "='" + CmBaseWriter.NS
                + "'\n";
        attributes += " xmlns:" + CmHttpWriter.PREFIX + "='" + CmHttpWriter.NS
                + "'\n";
        attributes += " xmlns:" + CmRpcWriter.PREFIX + "='" + CmRpcWriter.NS
                + "'\n";
        attributes += " xmlns:" + CmSoapWriter.PREFIX + "='" + CmSoapWriter.NS
                + "'\n";
        attributes += CmBaseWriter.idAttribute(component);
        out.beginElement("descriptionComponent", attributes);

        URI[] extensions = { ComponentExtensions.URI_NS_EXTENSIONS,
                ComponentExtensions.URI_NS_HTTP,
                ComponentExtensions.URI_NS_RPC, ComponentExtensions.URI_NS_SOAP };

        cmbase.writeUris("extensions", extensions);
        write("interfaces", component.getInterfaces());
        write("bindings", component.getBindings());
        write("services", component.getServices());
        write("elementDeclarations", component.getElementDeclarations());
        write("typeDefinitions", component.getTypeDefinitions());

        out.endElement();
    }

    private void writeRefs(String tag, Interface[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {
                QName x1 = ((Interface) o1).getName();
                QName x2 = ((Interface) o2).getName();
                return CmBaseWriter.compareQName(x1, x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++) {
            cmbase.writeRef("interface", components[i]);
        }

        out.endElement();

    }

    private void write(String tag, Interface[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {
                QName x1 = ((Interface) o1).getName();
                QName x2 = ((Interface) o2).getName();
                return CmBaseWriter.compareQName(x1, x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++)
            write("interfaceComponent", components[i]);

        out.endElement();
    }

    private void write(String tag, Interface component) {

        out.beginElement(tag, CmBaseWriter.idAttribute(component));

        cmbase.write("name", component.getName());
        writeRefs("extendedInterfaces", component.getExtendedInterfaces());
        write("interfaceFaults", component.getInterfaceFaults());
        write("interfaceOperations", component.getInterfaceOperations());
        cmbase.features(component.getFeatures());
        cmbase.properties(component.getProperties());

        out.endElement();
    }

    private void write(String tag, InterfaceFault[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {
                QName x1 = ((InterfaceFault) o1).getName();
                QName x2 = ((InterfaceFault) o2).getName();
                return CmBaseWriter.compareQName(x1, x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++)
            write("interfaceFaultComponent", components[i]);

        out.endElement();
    }

    private void write(String tag, InterfaceFault component) {

        out.beginElement("interfaceFaultComponent", CmBaseWriter
                .idAttribute(component));

        cmbase.write("name", component.getName());
        cmbase.writeOptionalRef("elementDeclaration", component
                .getElementDeclaration());
        cmbase.features(component.getFeatures());
        cmbase.properties(component.getProperties());
        cmbase.parent(component.getParent());

        out.endElement();
    }

    private void write(String tag, InterfaceOperation[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {
                QName x1 = ((InterfaceOperation) o1).getName();
                QName x2 = ((InterfaceOperation) o2).getName();
                return CmBaseWriter.compareQName(x1, x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++)
            write("interfaceOperationComponent", components[i]);

        out.endElement();
    }

    private void write(String tag, InterfaceOperation component) {

        out.beginElement("interfaceOperationComponent", CmBaseWriter
                .idAttribute(component));

        cmbase.write("name", component.getName());
        write("messageExchangePattern", component.getMessageExchangePattern());
        write("interfaceMessageReferences", component
                .getInterfaceMessageReferences());
        write("interfaceFaultReferences", component
                .getInterfaceFaultReferences());
        cmbase.writeUris("style", component.getStyle());
        cmbase.features(component.getFeatures());
        cmbase.properties(component.getProperties());
        cmbase.parent(component.getParent());

        InterfaceOperationExtensions extensions = (InterfaceOperationExtensions) component
                .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_EXTENSIONS);
        cmextensions.wsdlInterfaceOperationExtension(extensions);

        RPCInterfaceOperationExtensions rpcExtensions = (RPCInterfaceOperationExtensions) component
                .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_RPC);
        cmrpc.rpcInterfaceOperationExtension(rpcExtensions);

        out.endElement();
    }

    private void write(String tag, InterfaceMessageReference[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {
                String x1 = ((InterfaceMessageReference) o1).getMessageLabel()
                        .toString();
                String x2 = ((InterfaceMessageReference) o2).getMessageLabel()
                        .toString();
                return x1.compareTo(x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++)
            write("interfaceMessageReferenceComponent", components[i]);

        out.endElement();
    }

    private void write(String tag, InterfaceMessageReference component) {

        out.beginElement(tag, CmBaseWriter.idAttribute(component));

        out.write("messageLabel", component.getMessageLabel().toString());
        out.write("direction", component.getDirection().toString());
        out.write("messageContentModel", component.getMessageContentModel());
        cmbase.writeOptionalRef("elementDeclaration", component
                .getElementDeclaration());
        cmbase.features(component.getFeatures());
        cmbase.properties(component.getProperties());
        cmbase.parent(component.getParent());

        out.endElement();
    }

    private void write(String tag, InterfaceFaultReference[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {

                InterfaceFaultReference i1 = (InterfaceFaultReference) o1;
                InterfaceFaultReference i2 = (InterfaceFaultReference) o2;

                InterfaceFault if1 = i1.getInterfaceFault();
                InterfaceFault if2 = i2.getInterfaceFault();

                if (if1 == if2)
                    return 0;
                if (if1 == null)
                    return -1;
                if (if2 == null)
                    return 1;

                QName q1 = if1.getName();
                QName q2 = if2.getName();

                int result = CmBaseWriter.compareQName(q1, q2);
                if (result != 0)
                    return result;

                String x1 = i1.getMessageLabel().toString();
                String x2 = i2.getMessageLabel().toString();
                return x1.compareTo(x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++)
            write("interfaceFaultReferenceComponent", components[i]);

        out.endElement();
    }

    private void write(String tag, InterfaceFaultReference component) {

        out.beginElement(tag, CmBaseWriter.idAttribute(component));

        cmbase.writeRef("interfaceFault", component.getInterfaceFault());
        out.write("messageLabel", component.getMessageLabel().toString());
        out.write("direction", component.getDirection().toString());
        cmbase.features(component.getFeatures());
        cmbase.properties(component.getProperties());
        cmbase.parent(component.getParent());

        out.endElement();
    }

    private void write(String tag, Binding[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {
                QName x1 = ((Binding) o1).getName();
                QName x2 = ((Binding) o2).getName();
                return CmBaseWriter.compareQName(x1, x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++)
            write("bindingComponent", components[i]);

        out.endElement();
    }

    private void write(String tag, Binding component) {

        out.beginElement(tag, CmBaseWriter.idAttribute(component));

        cmbase.write("name", component.getName());
        cmbase.writeOptionalRef("interface", component.getInterface());
        write("type", component.getType());
        write("bindingFaults", component.getBindingFaults());
        write("bindingOperations", component.getBindingOperations());
        cmbase.features(component.getFeatures());
        cmbase.properties(component.getProperties());

        HTTPBindingExtensions http = (HTTPBindingExtensions) component
                .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP);
        cmhttp.httpBindingExtension(http);

        SOAPBindingExtensions soap = (SOAPBindingExtensions) component
                .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        cmsoap.soapBindingExtension(soap);

        out.endElement();
    }

    private void write(String tag, BindingFault[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {

                InterfaceFault if1 = ((BindingFault) o1).getInterfaceFault();
                InterfaceFault if2 = ((BindingFault) o2).getInterfaceFault();

                if (if1 == if2)
                    return 0;
                if (if1 == null)
                    return -1;
                if (if2 == null)
                    return 1;

                QName x1 = if1.getName();
                QName x2 = if2.getName();

                return CmBaseWriter.compareQName(x1, x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++)
            write("bindingFaultComponent", components[i]);

        out.endElement();
    }

    private void write(String tag, BindingFault component) {

        out.beginElement(tag, CmBaseWriter.idAttribute(component));

        cmbase.writeRef("interfaceFault", component.getInterfaceFault());
        cmbase.features(component.getFeatures());
        cmbase.properties(component.getProperties());
        cmbase.parent(component.getParent());

        HTTPBindingFaultExtensions http = (HTTPBindingFaultExtensions) component
                .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP);
        cmhttp.httpBindingFaultExtension(http);

        SOAPBindingFaultExtensions soap = (SOAPBindingFaultExtensions) component
                .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        cmsoap.soapBindingFaultExtension(soap);

        out.endElement();
    }

    private void write(String tag, BindingOperation[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {
                QName x1 = ((BindingOperation) o1).getInterfaceOperation()
                        .getName();
                QName x2 = ((BindingOperation) o1).getInterfaceOperation()
                        .getName();
                return CmBaseWriter.compareQName(x1, x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++)
            write("bindingOperationComponent", components[i]);

        out.endElement();
    }

    private void write(String tag, BindingOperation component) {

        out.beginElement(tag, CmBaseWriter.idAttribute(component));

        cmbase
                .writeRef("interfaceOperation", component
                        .getInterfaceOperation());
        write("bindingFaultReferences", component.getBindingFaultReferences());
        write("bindingMessageReferences", component
                .getBindingMessageReferences());
        cmbase.features(component.getFeatures());
        cmbase.properties(component.getProperties());
        cmbase.parent(component.getParent());

        HTTPBindingOperationExtensions http = (HTTPBindingOperationExtensions) component
                .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP);
        cmhttp.httpBindingOperationExtension(http);

        SOAPBindingOperationExtensions soap = (SOAPBindingOperationExtensions) component
                .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        cmsoap.soapBindingOperationExtension(soap);

        out.endElement();
    }

    private void write(String tag, BindingMessageReference[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {

                InterfaceMessageReference i1 = ((BindingMessageReference) o1)
                        .getInterfaceMessageReference();
                InterfaceMessageReference i2 = ((BindingMessageReference) o2)
                        .getInterfaceMessageReference();

                String x1 = i1.getMessageLabel().toString();
                String x2 = i2.getMessageLabel().toString();
                return x1.compareTo(x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++)
            write("bindingMessageReferenceComponent", components[i]);

        out.endElement();
    }

    private void write(String tag, BindingMessageReference component) {

        out.beginElement(tag, CmBaseWriter.idAttribute(component));

        cmbase.writeRef("interfaceMessageReference", component
                .getInterfaceMessageReference());
        cmbase.features(component.getFeatures());
        cmbase.properties(component.getProperties());
        cmbase.parent(component.getParent());

        HTTPBindingMessageReferenceExtensions http = (HTTPBindingMessageReferenceExtensions) component
                .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP);
        cmhttp.httpBindingMessageReferenceExtension(http);

        SOAPBindingMessageReferenceExtensions soap = (SOAPBindingMessageReferenceExtensions) component
                .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        cmsoap.soapBindingMessageReferenceExtension(soap);

        out.endElement();
    }

    private void write(String tag, BindingFaultReference[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {

                InterfaceFaultReference i1 = ((BindingFaultReference) o1)
                        .getInterfaceFaultReference();
                InterfaceFaultReference i2 = ((BindingFaultReference) o2)
                        .getInterfaceFaultReference();

                QName q1 = i1.getInterfaceFault().getName();
                QName q2 = i2.getInterfaceFault().getName();

                int result = CmBaseWriter.compareQName(q1, q2);
                if (result != 0)
                    return result;

                String x1 = i1.getMessageLabel().toString();
                String x2 = i2.getMessageLabel().toString();
                return x1.compareTo(x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++)
            write("bindingFaultReferenceComponent", components[i]);

        out.endElement();
    }

    private void write(String tag, BindingFaultReference component) {

        out.beginElement(tag, CmBaseWriter.idAttribute(component));

        cmbase.writeRef("interfaceFaultReference", component
                .getInterfaceFaultReference());
        cmbase.features(component.getFeatures());
        cmbase.properties(component.getProperties());
        cmbase.parent(component.getParent());

        SOAPBindingFaultReferenceExtensions soap = (SOAPBindingFaultReferenceExtensions) component
                .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_SOAP);
        cmsoap.soapBindingFaultReferenceExtension(soap);

        out.endElement();
    }

    private void write(String tag, Service[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {
                QName x1 = ((Service) o1).getName();
                QName x2 = ((Service) o2).getName();
                return CmBaseWriter.compareQName(x1, x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++)
            write("serviceComponent", components[i]);

        out.endElement();
    }

    private void write(String tag, Service component) {

        out.beginElement(tag, CmBaseWriter.idAttribute(component));

        cmbase.write("name", component.getName());
        cmbase.writeRef("interface", component.getInterface());
        write("endpoints", component.getEndpoints());
        cmbase.features(component.getFeatures());
        cmbase.properties(component.getProperties());

        out.endElement();
    }

    private void write(String tag, Endpoint[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {
                String x1 = ((Endpoint) o1).getName().toString();
                String x2 = ((Endpoint) o2).getName().toString();
                return x1.compareTo(x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++) {
            write("endpointComponent", components[i]);
        }

        out.endElement();

    }

    private void write(String tag, Endpoint component) {

        out.beginElement(tag, CmBaseWriter.idAttribute(component));

        out.write("name", component.getName());
        cmbase.writeRef("binding", component.getBinding());
        cmbase.write("address", component.getAddress());
        cmbase.features(component.getFeatures());
        cmbase.properties(component.getProperties());
        cmbase.parent(component.getParent());

        HTTPEndpointExtensions http = (HTTPEndpointExtensions) component
                .getComponentExtensionsForNamespace(ComponentExtensions.URI_NS_HTTP);
        cmhttp.httpEndpointExtension(http);

        out.endElement();
    }

    private void write(String tag, ElementDeclaration[] components) {

        // filter out the XSD elements until Woden fixes the bug
        Vector filteredElements = new Vector();
        for (int i = 0; i < components.length; i++) {
            if (!XSD_NS.equals(components[i].getName().getNamespaceURI()))
                filteredElements.addElement(components[i]);
        }
        components = new ElementDeclaration[filteredElements.size()];
        filteredElements.copyInto(components);

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {
                QName x1 = ((ElementDeclaration) o1).getName();
                QName x2 = ((ElementDeclaration) o2).getName();
                return CmBaseWriter.compareQName(x1, x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++)
            write("elementDeclarationComponent", components[i]);

        out.endElement();
    }

    private void write(String tag, ElementDeclaration component) {

        out.beginElement(tag, CmBaseWriter.idAttribute(component));

        cmbase.write("name", component.getName());
        write("system", component.getSystem());

        out.endElement();
    }

    private void write(String tag, TypeDefinition[] components) {

        // filter out the XSD non-built-in types until Woden fixes the bug
        Vector filteredTypes = new Vector();
        for (int i = 0; i < components.length; i++) {
            if (!XSD_NS.equals(components[i].getName().getNamespaceURI())) {
                filteredTypes.addElement(components[i]);
            } else {
                if (Arrays.binarySearch(XSD_TYPES, components[i].getName()
                        .getLocalPart()) >= 0)
                    filteredTypes.addElement(components[i]);
            }
        }
        components = new TypeDefinition[filteredTypes.size()];
        filteredTypes.copyInto(components);

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {
                QName x1 = ((TypeDefinition) o1).getName();
                QName x2 = ((TypeDefinition) o2).getName();
                return CmBaseWriter.compareQName(x1, x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++)
            write("typeDefinitionComponent", components[i]);

        out.endElement();
    }

    private void write(String tag, TypeDefinition component) {

        out.beginElement(tag, CmBaseWriter.idAttribute(component));

        cmbase.write("name", component.getName());
        write("system", component.getSystem());

        out.endElement();
    }

}

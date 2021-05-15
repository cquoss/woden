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

import javax.xml.namespace.QName;

import org.apache.woden.wsdl20.ElementDeclaration;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingExtensions;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingFaultExtensions;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingFaultReferenceExtensions;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingMessageReferenceExtensions;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingOperationExtensions;
import org.apache.woden.wsdl20.extensions.soap.SOAPFaultCode;
import org.apache.woden.wsdl20.extensions.soap.SOAPFaultSubcodes;
import org.apache.woden.wsdl20.extensions.soap.SOAPHeaderBlock;
import org.apache.woden.wsdl20.extensions.soap.SOAPModule;

/**
 * @author Arthur Ryman (ryman@ca.ibm.com, arthur.ryman@gmail.com)
 *
 */
public class CmSoapWriter extends NamespaceWriter {

    public final static String NS = "http://www.w3.org/2002/ws/desc/wsdl/component-soap";

    public final static String PREFIX = "cmsoap";

    private CmBaseWriter cmbase;

    private CmHttpWriter cmhttp;

    /**
     * @param out
     */
    public CmSoapWriter(XMLWriter out) {

        super(out, NS, PREFIX);

        cmbase = (CmBaseWriter) out.lookup(CmBaseWriter.NS);
        cmhttp = (CmHttpWriter) out.lookup(CmHttpWriter.NS);
    }

    public void soapBindingExtension(SOAPBindingExtensions soap) {

        if (soap == null)
            return;

        out.beginElement(PREFIX + ":soapBindingExtension");
        
        write(PREFIX + ":soapMepDefault", soap.getSoapMepDefault());
        write(PREFIX + ":soapModules", soap.getSoapModules());
        write(PREFIX + ":soapUnderlyingProtocols", soap.getSoapMepDefault());
        out.write(PREFIX + ":soapVersion", soap.getSoapVersion());

        out.endElement();
    }

    public void soapBindingFaultExtension(SOAPBindingFaultExtensions soap) {

        if (soap == null)
            return;

        out.beginElement(PREFIX + ":soapBindingFaultExtension");

        write(PREFIX + ":soapFaultCode", soap.getSoapFaultCode());
        write(PREFIX + ":soapFaultSubcodes", soap.getSoapFaultSubcodes());
        write(PREFIX + ":soapHeaders", soap.getSoapHeaders());
        write(PREFIX + ":soapModules", soap.getSoapModules());

        out.endElement();
    }

    public void soapBindingOperationExtension(
            SOAPBindingOperationExtensions soap) {

        if (soap == null)
            return;

        out.beginElement(PREFIX + ":soapBindingOperationExtension");

        write(PREFIX + ":soapAction", soap.getSoapAction());
        write(PREFIX + ":soapMep", soap.getSoapMep());
        write(PREFIX + ":soapModules", soap.getSoapModules());

        out.endElement();
    }

    public void soapBindingMessageReferenceExtension(
            SOAPBindingMessageReferenceExtensions soap) {

        if (soap == null)
            return;

        out.beginElement(PREFIX + ":soapBindingMessageReferenceExtension");

        write(PREFIX + ":soapHeaders", soap.getSoapHeaders());
        write(PREFIX + ":soapModules", soap.getSoapModules());

        out.endElement();
    }

    public void soapBindingFaultReferenceExtension(
            SOAPBindingFaultReferenceExtensions soap) {

        if (soap == null)
            return;

        out.beginElement(PREFIX + ":soapBindingFaultReferenceExtension");

        write(PREFIX + ":soapModules", soap.getSoapModules());

        out.endElement();
    }

    private void write(String tag, SOAPModule[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {

                URI uri1 = ((SOAPModule) o1).getRef();
                URI uri2 = ((SOAPModule) o2).getRef();

                return uri1.compareTo(uri2);
            }
        });
        out.beginElement(tag);

        for (int i = 0; i < components.length; i++)
            write("SoapModuleComponent", components[i]);

        out.endElement();

    }

    private void write(String tag, SOAPModule component) {

        out.beginElement(tag, CmBaseWriter.idAttribute(component));

        cmbase.write(PREFIX + ":ref", component.getRef());

        out.write(PREFIX + ":required", component.isRequired().booleanValue());

        cmbase.parent(component.getParent());

        out.endElement();
    }

    private void write(String tag, SOAPFaultSubcodes soapFaultSubcodes) {

        out.beginElement(tag);
        if (soapFaultSubcodes.isQNames()) {
            out.beginElement(PREFIX + ":subcodes");
            QName[] codes = soapFaultSubcodes.getQNames();
            for (int i = 0; i < codes.length; i++) {
                cmbase.write(PREFIX + ":code", codes[i]);
            }
            out.endElement();
        }
        out.endElement();
    }

    private void write(String tag, SOAPFaultCode soapFaultCode) {

        out.beginElement(tag);
        if (soapFaultCode.isQName()) {
            cmbase.write(PREFIX + ":code", soapFaultCode.getQName());
        }
        out.endElement();
    }

    private void write(String tag, SOAPHeaderBlock[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {

                ElementDeclaration ed1 = ((SOAPHeaderBlock) o1)
                        .getElementDeclaration();
                ElementDeclaration ed2 = ((SOAPHeaderBlock) o2)
                        .getElementDeclaration();

                if (ed1 == ed2)
                    return 0;
                if (ed1 == null)
                    return -1;
                if (ed2 == null)
                    return 1;

                QName x1 = ed1.getName();
                QName x2 = ed2.getName();

                return CmBaseWriter.compareQName(x1, x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++)
            write(PREFIX + ":SoapHeaderBlockComponent", components[i]);

        out.endElement();
    }

    private void write(String tag, SOAPHeaderBlock component) {

        out.beginElement(tag, CmBaseWriter.idAttribute(component));

        cmbase.writeRef(PREFIX + ":elementDeclaration", component
                .getElementDeclaration());

        out.write(PREFIX + ":mustUnderstand", component.mustUnderstand()
                .booleanValue());

        out.write(PREFIX + ":required", component.isRequired().booleanValue());

        cmbase.parent(component.getParent());

        out.endElement();

    }
}

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

import org.apache.woden.wsdl20.Feature;
import org.apache.woden.wsdl20.Property;

public class CmBaseWriter extends NamespaceWriter {
    
    public final static String NS = "http://www.w3.org/2002/ws/desc/wsdl/component-base";
    
    public final static String PREFIX = "cmbase";
    
    // element names
    public final static String FEATURES = PREFIX + ":features";
    public final static String FEATURE_COMPONENT = PREFIX + ":featureComponent";
    public final static String LOCAL_NAME = PREFIX + ":localName";
    public final static String NAMESPACE_NAME = PREFIX + ":namespaceName";
    public final static String PARENT = PREFIX + ":parent";
    public final static String PROPERTIES = PREFIX + ":properties";
    public final static String PROPERTY_COMPONENT = PREFIX + ":propertyComponent";
    public final static String REF = PREFIX + ":ref";
    public final static String REQUIRED = PREFIX + ":required";
    public final static String URI = PREFIX + ":uri";
    public final static String VALUE = PREFIX + ":value";
    public final static String VALUE_CONSTRAINT = PREFIX + ":valueConstraint";

    public CmBaseWriter(XMLWriter out) {
        
        super(out, NS, PREFIX);
    }
    
    public void features(Feature[] features) {
        
        write(FEATURES, features);
    }
    
    public void properties(Property[] properties) {
        
        write(PROPERTIES, properties);
    }
    
    public void parent(Object parent) {
        
        writeRef(PARENT, parent);
    }

    public void write(String tag, QName qname) {

        if (qname == null)
            return;

        out.beginElement(tag);

        out.element(NAMESPACE_NAME, qname.getNamespaceURI());
        out.element(LOCAL_NAME, qname.getLocalPart());

        out.endElement();
    }

    public void writeUris(String tag, URI[] uris) {

        if (uris.length == 0)
            return;

        Arrays.sort(uris);

        out.beginElement(tag);

        for (int i = 0; i < uris.length; i++)
            write(URI, uris[i]);

        out.endElement();
    }

    public void writeOptionalRef(String tag, Object o) {

        if (o == null)
            return;

        writeRef(tag, o);
    }

    public void writeRef(String tag, Object o) {

        out.emptyElement(tag, refAttribute(o));
    }

    public static String id(Object o) {

        if (o == null) {
            return "id-null";
        }

        return "id-" + o.hashCode();
    }

    public static String idAttribute(Object o) {

        return "xml:id='" + id(o) + "'";
    }

    public static String refAttribute(Object o) {

        return "ref='" + id(o) + "'";
    }
    
    public void write(String tag, Feature[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {
                URI x1 = ((Feature) o1).getRef();
                URI x2 = ((Feature) o2).getRef();
                return x1.compareTo(x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++) {
            write(FEATURE_COMPONENT, components[i]);
        }
        out.endElement();
    }

    public void write(String tag, Feature component) {

        out.beginElement(tag, idAttribute(component));

        write(REF, component.getRef());
        out.write(REQUIRED, component.isRequired());
        writeRef(PARENT, component.getParent());

        out.endElement();
    }

    public void write(String tag, Property[] components) {

        if (components.length == 0)
            return;

        Arrays.sort(components, new Comparator() {

            public int compare(Object o1, Object o2) {
                URI x1 = ((Property) o1).getRef();
                URI x2 = ((Property) o2).getRef();
                return x1.compareTo(x2);
            }
        });

        out.beginElement(tag);

        for (int i = 0; i < components.length; i++) {
            write(PROPERTY_COMPONENT, components[i]);
        }

        out.endElement();
    }

    public void write(String tag, Property component) {

        out.beginElement(tag, idAttribute(component));

        write(REF, component.getRef());
        writeOptionalRef(VALUE_CONSTRAINT, component.getValueConstraint());
        writeAny(VALUE, component.getValue());
        writeRef(PARENT, component.getParent());

        out.endElement();
    }

    public static int compareQName(QName q1, QName q2) {

        if (q1.equals(q2))
            return 0;

        String n1 = q1.getNamespaceURI();
        String n2 = q2.getNamespaceURI();
        if (n1.equals(n2)) {
            String l1 = q1.getLocalPart();
            String l2 = q2.getLocalPart();

            return l1.compareTo(l2);
        } else {
            return n1.compareTo(n2);
        }
    }
}

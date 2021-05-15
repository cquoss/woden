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
package org.apache.woden.schema;


import java.net.URI;

import org.apache.ws.commons.schema.XmlSchema;

/**
 * This interface provides an abstract representation of an XML Schema referenced 
 * within the WSDL &lt;types&gt; element. For example, via &lt;xs:schema&gt; or 
 * &lt;xs:import&gt;.
 * It contains the namespace used as the target namespace of an inlined schema
 * or as the imported namespace of a schema import. 
 * It contains a reference to the actual schema definition, represented by
 * <code>org.apache.ws.commons.schema.XmlSchema</code>. 
 * It also indicates whether the schema is 'referenceable' by the WSDL, 
 * as defined by the schema referenceability rules in the WSDL 2.0 spec.
 * <p>
 * 
 * NOTE: non-XML type systems like DTD are not handled by this interface. They must be
 * handled by WSDL 2.0 extension mechanisms.
 * 
 * TODO initially this will be tested with XML Schema. Need to determine if it really
 * is sufficient for other xml-based schema types like Relax NG or if some type of 
 * schema extension mechanism is required. 
 * 
 * @author jkaputin@apache.org
 */
public interface Schema {
    
    /**
     * @return a URI representing the target namespace of an inline schema or the 
     * namespace attribute of a schema import.
     */
    public URI getNamespace();
    public void setNamespace(URI namespace);
    
    /**
     * @return the XmlSchema object representing the schema contents.
     */
    public XmlSchema getSchemaDefinition();
    public void setSchemaDefinition(XmlSchema schemaDef);
    
}

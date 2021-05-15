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
package org.apache.woden.wsdl20.xml;

import java.net.URI;

import org.apache.woden.schema.ImportedSchema;
import org.apache.woden.schema.InlinedSchema;
import org.apache.woden.schema.Schema;

/**
 * This interface represents the &lt;wsdl:types&gt; element. 
 * It supports parsing, creating and manipulating a &lt;types&gt; element.
 * <p>
 * The data types used in WSDL elements are typically defined within a 
 * &lt;types&gt; element using a type system such as W3C XML Schema.
 * Schemas may be imported or inlined within &lt;types&gt;.
 * A &lt;types&gt; element may contain multiple schema import elements with
 * the same namespace attribute, so the schemaLocation attribute may be used 
 * to distinguish them.
 * Likewise, it is valid to have multiple inline schemas, so the id attribute 
 * may be used to distinguish them.
 * 
 * @author jkaputin@apache.org
 */
public interface TypesElement extends DocumentableElement, NestedElement
{
    
    /**
     * Indicates the type system used within the &lt;types&gt; element. 
     * Typically the W3C XML Schema type system will be used, indicated by 
     * the namespace "http://www.w3.org/2001/XMLSchema". An alternative
     * schema-like type system is Relax NG (http://www.relaxng.org/).
     */
    public void setTypeSystem(String typeSystem);
    
    /**
     * Get the string indicating the type system used within the &lt;types&gt;
     * element.
     */
    public String getTypeSystem();
    
    /**
     * Add a Schema object for a schema inlined or imported within the &lt;types&gt; element.
     * 
     * @param schema the Schema object.
     */
    public void addSchema(Schema schema);
    
    /**
     * Delete the specified Schema object.
     */
    public void removeSchema(Schema schema);
    
    /**
     * Return the Schemas representing all inlined schemas or schema imports,
     * in the order in which they occur within the &lt;types&gt; element.
     * 
     * @return an array of Schema objects
     */
    public Schema[] getSchemas();
    
    /**
     * Return all Schemas where the specified namespace argument is either the
     * target namespace of an inlined schema or the imported namespace of a 
     * schema import. Schemas are returned in the order in which they occur 
     * within the &lt;types&gt; element.
     * <p>
     * A null namespace argument will return any inlined schemas missing their
     * target namespace attribute or any schema imports missing their namespace 
     * attribute.
     * 
     * @return the Schemas for the schema with the specified target namespace.
     */
    public Schema[] getSchemas(URI namespace);
    
    /**
     * Return all schemas inlined within the &lt;types&gt; element, in the order
     * in which they occur within &lt;types&gt;.
     * 
     * @return an array of Schema objects.
     */
    public InlinedSchema[] getInlinedSchemas();
    
    /**
     * Return all schema imports from within the &lt;types&gt; element, in the order
     * in which they occur within &lt;types&gt;.
     * 
     * @return an array of Schema objects.
     */
    public ImportedSchema[] getImportedSchemas();
    
    //TODO is there a use case to remove all schemas for a given namespace?
    //E.g.
    //public void removeSchemas(String namespace);


    //TODO methods to add/get/remove extension elements ... i.e. for other type systems

}

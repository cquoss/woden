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

/**
 * This interface represents a schema import, &lt;xs:import&gt;. It extends the Schema
 * interface, adding support for the <code>schemaLocation</code> attribute.
 * 
 * @author jkaputin@apache.org
 */
public interface ImportedSchema extends Schema {
    
    /**
     * Set the URI representing the <code>schemaLocation</code> attribute of &lt;xs:import&gt;.
     */
    public void setSchemaLocation(URI location);
    
    /**
     * @return the URI representing the <code>schemaLocation</code> attribute of &lt;xs:import&gt;.
     */
    public URI getSchemaLocation();
    
}

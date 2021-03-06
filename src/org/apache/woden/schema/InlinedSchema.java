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


/**
 * This interface represents an inlined schema, &lt;xs:schema&gt;. It extends the Schema
 * interface, adding support for the <code>id</code> attribute.
 * 
 * @author jkaputin@apache.org
 */
public interface InlinedSchema extends Schema {
    
    /**
     * Set the String representing the <code>id</code> attribute of &lt;xs:schema&gt;.
     */
    public void setId(String id);
    
    /**
     * @return a String representing the <code>id</code> attribute of &lt;xs:schema&gt;.
     */
    public String getId();
}

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
package org.apache.woden;

/**
 * Represents the location of parsing error within a XML document.
 * Based on org.xml.sax.Locator.
 * 
 * TODO decide if URI info of the document is needed, 
 * and maybe XPATH of the element or attribute in error. 
 *
 * @author kaputin
 */
public interface ErrorLocator {
    
    public String getDocumentBaseURI();
    
    public String getLocationURI();
    
    public int getLineNumber();
    
    public int getColumnNumber();

}

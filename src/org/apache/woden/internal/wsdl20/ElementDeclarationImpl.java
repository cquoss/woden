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
package org.apache.woden.internal.wsdl20;

import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.woden.wsdl20.ElementDeclaration;

/**
 * This class represents an ElementDeclaration property of the Description component.
 * It refers to a global element declaration provided by the underlying type
 * system (e.g. XML Schema) 
 * 
 * @author jkaputin@apache.org
 */
public class ElementDeclarationImpl implements ElementDeclaration {
    
    private QName  fName = null;
    private URI fSystem = null;
    private String fContentModel = null;
    private Object fContent = null;

    /* ************************************************************
     *  ElementDeclaration interface methods (the WSDL Component model)
     * ************************************************************/
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.ElementDeclaration#getName()
     */
    public QName getName() 
    {
        return fName;
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.ElementDeclaration#getSystem()
     */
    public URI getSystem() 
    {
        return fSystem;
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.ElementDeclaration#getContentModel()
     */
    public String getContentModel() 
    {
        return fContentModel;
    }
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.ElementDeclaration#getContent()
     */
    public Object getContent() 
    {
        return fContent;
    }
    
    /* ************************************************************
     *  Non-API implementation methods
     * ************************************************************/

    public void setName(QName name)
    {
        fName = name;
    }

    public void setSystem(URI typeSystemURI)
    {
        fSystem = typeSystemURI;
    }
    
    public void setContentModel(String contentModel)
    {
        fContentModel = contentModel;
    }

    public void setContent(Object elementContent)
    {
        fContent = elementContent;
    }


}

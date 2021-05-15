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
package org.apache.woden.wsdl20;

import org.apache.woden.wsdl20.enumeration.Direction;
import org.apache.woden.wsdl20.enumeration.MessageLabel;
import org.apache.woden.wsdl20.xml.InterfaceMessageReferenceElement;


/**
 * @author jkaputin@apache.org
 */
public interface InterfaceMessageReference extends NestedComponent, ConfigurableComponent {
    
    public MessageLabel getMessageLabel();
    
    public Direction getDirection();
    
    /**
     * Represents a token indicating the type of message content. 
     * '#any' means any single element, '#none' means no message content, 
     * '#other' means non-XML extension type system and '#element' means 
     * XML Schema global element definition.
     *   
     * @return string representing the type of message content
     */
    public String getMessageContentModel();
    
    public ElementDeclaration getElementDeclaration();
    
    public InterfaceMessageReferenceElement toElement();
    
}

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
package org.apache.woden.xml;

import javax.xml.namespace.QName;

/**
 * This interface represents XML attribute information items of type xs:QName.
 * If the attribute's string value cannot be converted into a QName object the
 * getContent() and getQName() methods will return null and isValid() will return
 * false.
 * 
 * @author jkaputin@apache.org
 */
public interface QNameAttr extends XMLAttr 
{
    public QName getQName();
}

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
package org.apache.woden.internal;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class is used for formatting error messages. Unformatted error messages
 * are stored in a resource bundle. Formatting involves replacing any parameters
 * in the unformatted message text with values supplied at invocation. The error
 * messages may be translated into a localized resource bundle, so a locale may be
 * specified to determine the localization required.
 * 
 * @author jkaputin@apache.org
 */
public class MessageFormatter {

    /**
     * The specified key is used to retrieve an unformatted message from a  
     * resource bundle localized for the specified locale. This text is then
     * formatted with the specified message args.
     *
     * @param locale the required locale
     * @param key the message key
     * @param args message parameter values
     * @return the formatted message text
     * 
     * @throws NullPointerException if key is null 
     * @throws MissingResourceException if no object for the given key can be found 
     * @throws ClassCastException if the object found for the given key is not a string
     * @throws IllegalArgumentException if the args don't match the message.
     */
    public String formatMessage(Locale locale, String key, Object[] args) {
        
        ResourceBundle bundle = null;
        
        if (locale == null) {
            bundle = ResourceBundle.getBundle("org.apache.woden.internal.Messages");
        } else {
            bundle = ResourceBundle.getBundle("org.apache.woden.internal.Messages",
                                              locale);
        }
        
        String unformattedMsg = bundle.getString(key);
        String formattedMsg = MessageFormat.format(unformattedMsg, args);
        
        return formattedMsg;
    }

}

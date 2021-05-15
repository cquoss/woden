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
package org.apache.woden.wsdl20.enumeration;


/**
 * This class defines the values of the {message label} property of
 * InterfaceMessageReference and InterfaceFaultReference. This property 
 * identifies the role the message plays in the message exchange pattern
 * associated with the parent InterfaceOperation.
 * The property is represented in XML by the <code>messageLabel</code>
 * attribute of the &lt;input&gt;, &lt;output&gt;, &lt;infault&gt; and
 * &lt;outfault&gt; elements.
 * <p>
 * The valid <code>messageLabel</code> values are:
 * <ul>
 * <li>"In" - represented by the constant IN
 * <li>"Out" - represented by the constant OUT
 * </ul>
 * This class uses the typesafe enum pattern. Applications should use the
 * public static final constants defined in this class to specify or to 
 * evaluate a message label.
 * <p>
 * Examples:
 * <pre>
 *     msgRef.setMessageLabel(MessageLabel.IN);
 *     if(msgRef.getMessageLabel() == MessageLabel.IN) ...
 *     if(msgRef.getMessageLabel().equals(MessageLabel.IN)) ...
 * </pre>
 * Valid MessageLabels are Singletons, so <tt>==</tt> and <tt>.equals</tt> are gauranteed
 * to return the same result.
 * <p>
 * If a <tt>messageLabel</tt> attribute in the WSDL specifies an invalid value 
 * (i.e. not represented by a constant), it may still be useful to capture this 
 * value for reporting purposes. Use the public static method <tt>invalidValue(String)</tt>
 * for this purpose. 
 * Invalid MessageLabels are not Singletons, so <tt>.equals</tt> is overridden to compare 
 * string contents for invalid MessageLabels and <tt>==</tt> is not gauranteed to return the same
 * result as <tt>.equals</tt>.
 * <p>
 * TODO consider using a singleton map of invalid MessageLabels keyed by intern() strings
 * so that '==' and '.equals' will return the same result, as they do for valid MessageLabels
 * (i.e. remove the .equals() override and use object refs instead).
 * <p> 
 * TODO if extensibility is required, chg ctor to protected
 * <p>
 * TODO confirm that 'isValid()' is needed as a public method on the API
 * 
 * @author jkaputin@apache.org
 */
public class MessageLabel 
{
    public static final MessageLabel IN = new MessageLabel("In");
    public static final MessageLabel OUT = new MessageLabel("Out");

    public static final MessageLabel invalidValue(String value) {
        return new MessageLabel(value.intern(), false);
    }
    
    private final String fValue;
    private boolean fValid = true;
    
    private MessageLabel(String value) {
        this(value, true);
    }
    
    private MessageLabel(String value, boolean valid) {
        fValue = value;
        fValid = valid;
    }
    
    public String toString() {return fValue;}
    public boolean isValid() {return fValid;}
    
    public boolean equals(MessageLabel other)
    {
        if(fValid) 
        {
            //valid MessageLabel is Singleton, so compare object refs 
            return this == other;
        } 
        else 
        {
            //invalid MessageLabel is not Singleton, so compare contents
            if(other != null) {
                return this.fValue == other.toString();
            } else {
                return false;
            }
        }
    }
    
}

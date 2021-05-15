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

import org.apache.woden.wsdl20.BindingMessageReference;
import org.apache.woden.wsdl20.BindingOperation;
import org.apache.woden.wsdl20.InterfaceMessageReference;
import org.apache.woden.wsdl20.InterfaceOperation;
import org.apache.woden.wsdl20.enumeration.Direction;
import org.apache.woden.wsdl20.enumeration.MessageLabel;
import org.apache.woden.wsdl20.xml.BindingMessageReferenceElement;

/**
 * This class represents the BindingMessageReference component of the 
 * WSDL 2.0 Component model and the &lt;input&gt; and &lt;output&gt; 
 * child elements of a WSDL binding &lt;operation&gt;. 
 * 
 * @author jkaputin@apache.org
 */
public class BindingMessageReferenceImpl extends NestedConfigurableImpl
                                         implements BindingMessageReference, 
                                                    BindingMessageReferenceElement 
{
    private Direction fDirection = null;
    private MessageLabel fMessageLabel = null;

    /* ************************************************************
     *  BindingMessageReference interface methods (i.e. WSDL Component model)
     * ************************************************************/
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.BindingMessageReference#getInterfaceMessageReference()
     * 
     * The "effective" message label of the binding message reference message must be equal to the 
     * message label of an interface message reference.
     * 
     * The WSDL 2.0 Part 1 spec says " Define the effective message label of a binding message reference 
     * element information item  to be either the actual value of the messageLabel attribute information 
     * item if it is present, or the {message label} of the unique placeholder message with {direction} equal 
     * to the message direction if the attribute information item is absent."
     * 
     * The code in this method currently just supports the first type of "effective" message label,
     * where the message label property IS present in the binding msg reference.
     * 
     * TODO effective message label based on message exchange pattern placeholder message, 
     * where the message label property IS NOT present in the binding message reference.
     * 
     */
    public InterfaceMessageReference getInterfaceMessageReference() 
    {
        InterfaceMessageReference intMsgRef = null;
        BindingOperation bindOp = (BindingOperation)getParent();
        InterfaceOperation intOp = bindOp.getInterfaceOperation();
        if(intOp != null)
        {
            //Determine the "effective" msg label for this binding msg ref.
            MessageLabel effectiveMsgLabel = null;
            if(fMessageLabel != null) 
            {
                effectiveMsgLabel = fMessageLabel;
            } 
            else 
            {
                //implement placeholder effective msg label, as per the todo comment above
            }
            
            //Now match the effective msg label against the msg label of an interface msg reference.
            if(effectiveMsgLabel != null)
            {
                InterfaceMessageReference[] intMsgRefs = intOp.getInterfaceMessageReferences();
                for(int i=0; i<intMsgRefs.length; i++)
                {
                    if( effectiveMsgLabel.equals(intMsgRefs[i].getMessageLabel()) )
                    {
                        intMsgRef = intMsgRefs[i];
                        break;
                    }
                }
            }
        }
        return intMsgRef;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.BindingMessageReference#toElement()
     */
    public BindingMessageReferenceElement toElement() {
        return this;
    }

    /* ************************************************************
     *  BindingMessageReferenceElement interface methods (the XML Element model)
     * ************************************************************/
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.BindingMessageReferenceElement#setDirection(org.apache.woden.wsdl20.enumeration.Direction)
     */
    public void setDirection(Direction dir) {
        fDirection = dir;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.BindingMessageReferenceElement#getDirection()
     */
    public Direction getDirection() {
        return fDirection;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.BindingMessageReferenceElement#setMessageLabel(org.apache.woden.wsdl20.enumeration.MessageLabel)
     */
    public void setMessageLabel(MessageLabel msgLabel) {
        fMessageLabel = msgLabel;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.BindingMessageReferenceElement#getMessageLabel()
     */
    public MessageLabel getMessageLabel() {
        return fMessageLabel;
    }

    /* ************************************************************
     *  Non-API implementation methods
     * ************************************************************/
    
}

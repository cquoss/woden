/**
 * Copyright 2006 Apache Software Foundation 
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

import org.apache.woden.ErrorReporter;
import org.apache.woden.WSDLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;


/**
 * This class provides a WSDLSource implementation that supports a
 * DOM-based representation of the WSDL source. Specifically, it
 * may accept via its <code>setSource</code> method an object of type
 * org.w3c.dom.Element, org.w3c.dom.Document or org.xml.sax.InputSource. 
 * Any other type of object passed to this method will result in a 
 * WSDLException being thrown.
 * 
 * @author John Kaputin (jkaputin@apache.org)
 */
public class DOMWSDLSource extends BaseWSDLSource {
    
    public DOMWSDLSource(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    /* (non-Javadoc)
     * @see org.apache.woden.WSDLSource#setSource(java.lang.Object)
     */
    public void setSource(Object wsdlSource) throws WSDLException {
        
        //TODO check if any other types of wsdl source should be supported
        if(wsdlSource instanceof Element ||
           wsdlSource instanceof Document ||
           wsdlSource instanceof InputSource) {
            fSource = wsdlSource;
        }
        else {
            String sourceClass = wsdlSource.getClass().getName();
            String readerClass = "DOMWSDLReader";
            String msg = fErrorReporter.getFormattedMessage(
                    "WSDL017", new Object[] {sourceClass, readerClass});
            throw new WSDLException(WSDLException.PARSER_ERROR, msg);
        }
    }

}

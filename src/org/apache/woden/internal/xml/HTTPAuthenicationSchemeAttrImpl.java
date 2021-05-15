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
package org.apache.woden.internal.xml;

import org.apache.woden.ErrorReporter;
import org.apache.woden.WSDLException;
import org.apache.woden.internal.ErrorLocatorImpl;
import org.apache.woden.wsdl20.extensions.http.HTTPAuthenticationScheme;
import org.apache.woden.xml.HTTPAuthenticationSchemeAttr;
import org.w3c.dom.Element;

/**
 * This class implements the whttp:authenticationType attribute.
 * 
 * @author Arthur Ryman (ryman@ca.ibm.com, arthur.ryman@gmail.com)
 * 
 */
public class HTTPAuthenicationSchemeAttrImpl extends XMLAttrImpl implements
		HTTPAuthenticationSchemeAttr {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.woden.internal.xml.XMLAttrImpl#convert(org.w3c.dom.Element,
	 *      java.lang.String)
	 */
	protected Object convert(Element ownerEl, String attrValue)
			throws WSDLException {

		// TODO: define correct error numbers

		HTTPAuthenticationScheme scheme = null;

		if (attrValue == null) {
			setValid(false);
			getErrorReporter().reportError(new ErrorLocatorImpl(), // TODO
					// line&col
					// nos.
					"WSDL508", new Object[] { attrValue },
					ErrorReporter.SEVERITY_ERROR);
		} else if (attrValue.equals(HTTPAuthenticationScheme.BASIC.toString())) {

			setValid(true);
			scheme = HTTPAuthenticationScheme.BASIC;
		} else if (attrValue.equals(HTTPAuthenticationScheme.DIGEST.toString())) {

			setValid(true);
			scheme = HTTPAuthenticationScheme.DIGEST;
		} else {

			setValid(false);
			getErrorReporter().reportError(new ErrorLocatorImpl(), // TODO
					// line&col
					// nos.
					"WSDL508", new Object[] { attrValue },
					ErrorReporter.SEVERITY_ERROR);
		}

		return scheme;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.woden.xml.HTTPAuthenticationSchemeAttr#getScheme()
	 */
	public HTTPAuthenticationScheme getScheme() {

		return (HTTPAuthenticationScheme) getContent();
	}

}

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
package org.apache.woden.internal.wsdl20.extensions.http;

import org.apache.woden.internal.wsdl20.extensions.ComponentExtensionsImpl;
import org.apache.woden.wsdl20.extensions.http.HTTPAuthenticationScheme;
import org.apache.woden.wsdl20.extensions.http.HTTPEndpointExtensions;
import org.apache.woden.wsdl20.xml.WSDLElement;
import org.apache.woden.xml.HTTPAuthenticationSchemeAttr;
import org.apache.woden.xml.StringAttr;

/**
 * This class defines the properties from the HTTP namespace added to the WSDL
 * <code>Endpoint</code> component as part of the HTTP binding extension
 * defined by the WSDL 2.0 spec.
 * 
 * @author Arthur Ryman (ryman@ca.ibm.com, arthur.ryman@gmail.com)
 * 
 */
public class HTTPEndpointExtensionsImpl extends ComponentExtensionsImpl
		implements HTTPEndpointExtensions {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.woden.wsdl20.extensions.http.HTTPEndpointExtensions#getHttpAuthenicationScheme()
	 */
	public HTTPAuthenticationScheme getHttpAuthenicationScheme() {

		HTTPAuthenticationSchemeAttr scheme = (HTTPAuthenticationSchemeAttr) ((WSDLElement) fParent)
				.getExtensionAttribute(HTTPConstants.Q_ATTR_AUTHENTICATION_TYPE);

		return scheme != null ? scheme.getScheme() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.woden.wsdl20.extensions.http.HTTPEndpointExtensions#getHttpAuthenticationRealm()
	 */
	public String getHttpAuthenticationRealm() {

		StringAttr realm = (StringAttr) ((WSDLElement) fParent)
				.getExtensionAttribute(HTTPConstants.Q_ATTR_AUTHENTICATION_REALM);

		return realm != null ? realm.getString() : null;
	}

}

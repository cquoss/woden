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

import java.net.URI;

import org.apache.woden.internal.wsdl20.extensions.ComponentExtensionsImpl;
import org.apache.woden.internal.wsdl20.extensions.ExtensionConstants;
import org.apache.woden.wsdl20.Binding;
import org.apache.woden.wsdl20.BindingOperation;
import org.apache.woden.wsdl20.InterfaceOperation;
import org.apache.woden.wsdl20.extensions.InterfaceOperationExtensions;
import org.apache.woden.wsdl20.extensions.http.HTTPBindingExtensions;
import org.apache.woden.wsdl20.extensions.http.HTTPBindingOperationExtensions;
import org.apache.woden.wsdl20.xml.WSDLElement;
import org.apache.woden.xml.BooleanAttr;
import org.apache.woden.xml.StringAttr;
import org.apache.woden.xml.URIAttr;

/**
 * This class defines the properties from the HTTP namespace added to the WSDL
 * <code>BindingOperation</code> component as part of the HTTP binding
 * extension defined by the WSDL 2.0 spec.
 * 
 * @author John Kaputin (jkaputin@apache.org)
 * @author Arthur Ryman (ryman@ca.ibm.com, arthur.ryman@gmail.com) - added
 *         support for {http location ignore uncited}
 */
public class HTTPBindingOperationExtensionsImpl extends ComponentExtensionsImpl
		implements HTTPBindingOperationExtensions {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.woden.wsdl20.extensions.http.HTTPBindingOperationExtensions#getHttpLocation()
	 */
	public URI getHttpLocation() {
		URIAttr httpLoc = (URIAttr) ((WSDLElement) fParent)
				.getExtensionAttribute(HTTPConstants.Q_ATTR_LOCATION);
		return httpLoc != null ? httpLoc.getURI() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.woden.wsdl20.extensions.http.HTTPBindingOperationExtensions#isHttpLocationIgnoreUncited()
	 * 
	 * The actual value of the whttp:ignoreUncited attribute information item,
	 * if present. Otherwise, "false".
	 */
	public Boolean isHttpLocationIgnoreUncited() {

		BooleanAttr ignoreUncited = (BooleanAttr) ((WSDLElement) fParent)
				.getExtensionAttribute(HTTPConstants.Q_ATTR_IGNORE_UNCITED);
		
		return ignoreUncited != null ? ignoreUncited.getBoolean()
				: new Boolean(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.woden.wsdl20.extensions.http.HTTPBindingOperationExtensions#getHttpMethod()
	 * 
	 * 1. Return whttp:method if present on the binding operation. 
     * 2. Otherwise return null
     * 
     * TODO implement steps 2,3,4 below if Part 2 spec is modified
     * to reflect ws-desc posting 30May06 about moving this default 
     * behaviour from binding rules into the component model. 
     * 2. Otherwise, return whttp:methodDefault if present on the binding.
     * 3. Otherwise, return "GET" if the {safety} extension property of 
     *    InterfaceOperation is 'true'. 
     * 4. Otherwise, return "POST"
	 */
	public String getHttpMethod() {

		// 1. try whttp:method
		StringAttr methodAttr = (StringAttr) ((WSDLElement) fParent)
				.getExtensionAttribute(HTTPConstants.Q_ATTR_METHOD);
		if (methodAttr != null) {
			return methodAttr.getString();
		}

        // 2. return null (i.e. no default specified in the spec...yet) 
        return null; //TODO remove if default behaviour below is used.
        

        /* TODO Uncomment the following code if the defaults descibed for 
         * binding rules in Part 2, 6.3.1 HTTP Method Selection, are moved
         * into the Component model (i.e. the defaults are applied in the 
         * component model, not left to the binder to enforce them.
         * See posting ws-desc 30May06.
         *
         * // 2. try whttp:methodDefault
         * Binding binding = (Binding) ((BindingOperation) fParent).getParent();
         * 
         * HTTPBindingExtensions httpBindExts = (HTTPBindingExtensions) binding
         *         .getComponentExtensionsForNamespace(HTTPConstants.NS_URI_HTTP);
         * 
         * // no need to check for a null httpBindExts because Binding has REQUIRED
         * // http extension properties
         * String methodDef = httpBindExts.getHttpMethodDefault();
         * if (methodDef != null) {
         *     return methodDef;
         * }
         *        
		 * // 3. try {safety}
		 * InterfaceOperation intOper = ((BindingOperation) fParent)
		 *         .getInterfaceOperation();
		 * if (intOper != null) {
		 *    InterfaceOperationExtensions intOperExts = (InterfaceOperationExtensions) intOper
		 *            .getComponentExtensionsForNamespace(URI
		 *                    .create(ExtensionConstants.NS_URI_WSDL_EXTENSIONS));
		 *    if (intOperExts != null && intOperExts.isSafety()) {
		 *       return HTTPConstants.METHOD_GET;
		 *    }
		 * }
         *
		 * // 4. default to POST.
		 * return HTTPConstants.METHOD_POST;
         */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.woden.wsdl20.extensions.http.HTTPBindingOperationExtensions#getHttpInputSerialization()
     * 
     * Per Part 2, sect 6.3.3, if this attribute is omitted then 
     * {http input serialization} will be application/x-www-form-urlencoded if
     * {http method} is GET or DELETE, or application/xml if {http method} has
     * any other value.
	 */
	public String getHttpInputSerialization() {
        
		StringAttr serialization = (StringAttr) ((WSDLElement) fParent)
				.getExtensionAttribute(HTTPConstants.Q_ATTR_INPUT_SERIALIZATION);
		if (serialization != null) {
			return serialization.getString();
		}

        //TODO replace determineHttpMethod() with getHttpMethod() if spec is
        //modified to define {http method} defaults in the component model
		String method = determineHttpMethod();
        
		if (method.equals(HTTPConstants.METHOD_GET)
				|| method.equals(HTTPConstants.METHOD_DELETE)) {
			return HTTPConstants.SERIAL_APP_URLENCODED;
		} else {
			// for POST, PUT or any other method type (Part 2 sect 6.3.3)
			return HTTPConstants.SERIAL_APP_XML;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.woden.wsdl20.extensions.http.HTTPBindingOperationExtensions#getHttpOutputSerialization()
     * 
     * Per Part 2, sect 6.3.3, if this attribute is omitted then 
     * {http output serialization} will be application/xml for any {http method}
     * value.
	 */
	public String getHttpOutputSerialization() {
		StringAttr serialization = (StringAttr) ((WSDLElement) fParent)
				.getExtensionAttribute(HTTPConstants.Q_ATTR_OUTPUT_SERIALIZATION);
		return serialization != null ? serialization.getString()
				: HTTPConstants.SERIAL_APP_XML;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.woden.wsdl20.extensions.http.HTTPBindingOperationExtensions#getHttpFaultSerialization()
     * 
     * Per Part 2, sect 6.4.5, if attribute omitted default to application/xml.
	 */
	public String getHttpFaultSerialization() {
		StringAttr serialization = (StringAttr) ((WSDLElement) fParent)
				.getExtensionAttribute(HTTPConstants.Q_ATTR_FAULT_SERIALIZATION);
		return serialization != null ? serialization.getString()
				: HTTPConstants.SERIAL_APP_XML;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.woden.wsdl20.extensions.http.HTTPBindingOperationExtensions#getHttpQueryParameterSeparator()
	 */
	public String getHttpQueryParameterSeparator() {
        
		StringAttr separator = (StringAttr) ((WSDLElement) fParent)
				.getExtensionAttribute(HTTPConstants.Q_ATTR_QUERY_PARAMETER_SEPARATOR);
        return separator != null ? separator.getString() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.woden.wsdl20.extensions.http.HTTPBindingOperationExtensions#getHttpTransferCodingDefault()
	 */
	public String getHttpTransferCodingDefault() {
		StringAttr tfrCodingDef = (StringAttr) ((WSDLElement) fParent)
				.getExtensionAttribute(HTTPConstants.Q_ATTR_TRANSFER_CODING_DEFAULT);
		return tfrCodingDef != null ? tfrCodingDef.getString() : null;
	}
    
    /*
     * This default behaviour for http method is referred to in the spec
     * Part 2 when determining default values for input and output 
     * serialization in the component model extensions (i.e. 6.3.3 refers
     * to 6.3.1). If spec is changed to define this default behaviour directly
     * for the {http method} property, this behaviour will move to the 
     * getHttpMethod() method and this private method can be removed.
     */
    private String determineHttpMethod() {
        
        // 1. try whttp:method
        String method = getHttpMethod();
        if(method != null) return method;
        
        // 2. try whttp:methodDefault
        Binding binding = (Binding) ((BindingOperation) fParent).getParent();
        
        HTTPBindingExtensions httpBindExts = (HTTPBindingExtensions) binding
                .getComponentExtensionsForNamespace(HTTPConstants.NS_URI_HTTP);
        
        // no need to check for a null httpBindExts because Binding has REQUIRED
        // http extension properties
        String methodDef = httpBindExts.getHttpMethodDefault();
        if (methodDef != null) return methodDef;
               
        // 3. try {safety} equals True
        InterfaceOperation intOper = ((BindingOperation) fParent)
                .getInterfaceOperation();
        if (intOper != null) {
            InterfaceOperationExtensions intOperExts = (InterfaceOperationExtensions) intOper
                    .getComponentExtensionsForNamespace(URI
                           .create(ExtensionConstants.NS_URI_WSDL_EXTENSIONS));
           if (intOperExts != null && intOperExts.isSafety()) {
              return HTTPConstants.METHOD_GET;
           }
        }
        
        // 4. default to POST.
        return HTTPConstants.METHOD_POST;
    }

}

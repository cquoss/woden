/**
 * Copyright 2005, 2006 Apache Software Foundation
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
package org.apache.woden.tests;

import javax.xml.namespace.QNameTest;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.woden.WSDLFactoryTest;
import org.apache.woden.WSDLReaderTest;
import org.apache.woden.internal.ReaderFeaturesTest;
import org.apache.woden.internal.wsdl20.validation.WSDLComponentValidatorTest;
import org.apache.woden.internal.wsdl20.validation.WSDLDocumentValidatorTest;
import org.apache.woden.wsdl20.extensions.http.HTTPBindingExtensionsTest;
import org.apache.woden.wsdl20.extensions.http.HTTPBindingFaultExtensionsTest;
import org.apache.woden.wsdl20.extensions.http.HTTPBindingMessageReferenceExtensionsTest;
import org.apache.woden.wsdl20.extensions.http.HTTPBindingOperationExtensionsTest;
import org.apache.woden.wsdl20.extensions.http.HTTPEndpointExtensionsTest;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingExtensionsTest;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingFaultExtensionsTest;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingFaultReferenceExtensionsTest;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingMessageReferenceExtensionsTest;
import org.apache.woden.wsdl20.extensions.soap.SOAPBindingOperationExtensionsTest;
import org.apache.woden.wsdl20.xml.EndpointElementTest;
import org.apache.woden.wsdl20.xml.NameAttributeTest;
import org.apache.woden.wsdl20.xml.ServiceElementTest;
import org.apache.woden.xml.IntOrTokenAttrTest;
import org.apache.woden.xml.TokenAttrTest;

public class AllWodenTestsDOM extends TestSuite
{
  /**
   * Create this test suite.
   *
   * @return This test suite.
   */
  public static Test suite()
  {
    return new AllWodenTestsDOM();
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllWodenTestsDOM.suite());
  }

  /**
   * Constructor
   */
  public AllWodenTestsDOM()
  {
    super("AllWodenTestsDOM");

    String ver = System.getProperty("java.version");
    if(ver.startsWith("1.4"))
    {
        /*
         * From Java 1.5.0 the QName class is included in the jre and loaded
         * by the bootstrap classloader. However, we are running a 1.4 jvm
         * now, so the Woden QName class will be loaded and we do want to
         * run the junit tests for this class.
         */
        addTest(QNameTest.suite());
    }

	addTest(WSDLFactoryTest.suite());
	addTest(WSDLReaderTest.suite());
	addTest(W3CTestSuiteTest.suite());
	addTestSuite(ReaderFeaturesTest.class);
	addTest(WSDLDocumentValidatorTest.suite());
	addTest(WSDLComponentValidatorTest.suite());
    addTest(ServiceElementTest.suite());
    addTest(EndpointElementTest.suite());
    addTest(NameAttributeTest.suite());
    addTest(IntOrTokenAttrTest.suite());
    addTest(TokenAttrTest.suite());
    addTest(SOAPBindingExtensionsTest.suite());
    addTest(SOAPBindingFaultExtensionsTest.suite());
    addTest(SOAPBindingOperationExtensionsTest.suite());
    addTest(SOAPBindingMessageReferenceExtensionsTest.suite());
    addTest(SOAPBindingFaultReferenceExtensionsTest.suite());
    addTest(HTTPBindingExtensionsTest.suite());
    addTest(HTTPBindingFaultExtensionsTest.suite());
    addTest(HTTPBindingOperationExtensionsTest.suite());
    addTest(HTTPBindingMessageReferenceExtensionsTest.suite());
    addTest(HTTPEndpointExtensionsTest.suite());
    //TODO in-progress 30May06 tests for BindingOpExt and BindingMsgRefExt
  }

}

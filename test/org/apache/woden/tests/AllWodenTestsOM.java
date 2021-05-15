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

import org.apache.woden.OMWSDLFactoryTest;
import org.apache.woden.OMWSDLReaderTest;
import org.apache.woden.wsdl20.xml.OMEndpointElementTest;
import org.apache.woden.wsdl20.xml.OMServiceElementTest;

public class AllWodenTestsOM extends TestSuite{

  /**
   * Create this test suite.
   *
   * @return This test suite.
   */
  public static Test suite(){
    return new AllWodenTestsOM();
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllWodenTestsOM.suite());
  }

  /**
   * Constructor
   */
  public AllWodenTestsOM(){
    super("AllWodenTestsOM");

    String ver = System.getProperty("java.version");
    if(ver.startsWith("1.4")){
        /*
         * From Java 1.5.0 the QName class is included in the jre and loaded
         * by the bootstrap classloader. However, we are running a 1.4 jvm
         * now, so the Woden QName class will be loaded and we do want to
         * run the junit tests for this class.
         */
        addTest(QNameTest.suite());
    }
    addTest(OMWSDLFactoryTest.suite());
	addTest(OMWSDLReaderTest.suite());
	addTest(OMW3CTestSuiteTest.suite());
    addTest(OMServiceElementTest.suite());
    addTest(OMEndpointElementTest.suite());
  }

}

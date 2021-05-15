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

import org.apache.woden.wsdl20.xml.ChildElementCreationTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllWodenTests extends TestSuite 
{
  /**
   * Create this test suite.
   * 
   * @return This test suite.
   */
  public static Test suite()
  {
    return new AllWodenTests();
  }
  
  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllWodenTests.suite());
  }
  
  /**
   * Constructor
   */
  public AllWodenTests()
  {
    super("AllWodenTests");
    
      addTest(AllWodenTestsDOM.suite());
      addTest(AllWodenTestsOM.suite());
      addTest(ChildElementCreationTest.suite());
    //TODO in-progress 30May06 tests for BindingOpExt and BindingMsgRefExt
  }
	
}

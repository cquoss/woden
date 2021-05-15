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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.woden.WSDLFactory;
import org.apache.woden.WSDLReader;
import org.apache.woden.wsdl20.xml.DescriptionElement;

public class OMW3CTestSuiteTest extends TestCase{

  private WSDLFactory omWSDLFactory = null;
  private WSDLReader omWSDLReader = null;
  private TestErrorHandler handler;
  private W3CFileRepository wfr = null;


  public static Test suite(){
    return new TestSuite(OMW3CTestSuiteTest.class);
  }

  protected void setUp() throws Exception{

    wfr = new W3CFileRepository("http://dev.w3.org/cvsweb/~checkout~/",
                                  System.getProperty("user.dir") + "/downloads/w3c-cache/");
    handler = new TestErrorHandler();

    try{
      omWSDLFactory = WSDLFactory.newInstance("org.apache.woden.internal.OMWSDLFactory");
      omWSDLReader = omWSDLFactory.newWSDLReader();
      omWSDLReader.setFeature(WSDLReader.FEATURE_VALIDATION, true);
    }
    catch (Exception e){
    }
  }

  protected void tearDown() throws Exception{
    handler = null;
    omWSDLFactory = null;
    omWSDLReader = null;
  }

  /**
   * BAD TEST CASES
   * All of the following test cases should report errors.
   * TODO: Add in error checks as the WSDL 2.0 validator is developed.
   */

    /**
     * Test for the test-suite/documents/bad/Chameleon-1B W3C test.
     */
    public void testChameleon1BFromOM(){
      try{
          DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/bad/Chameleon-1B/getBalance.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
          // TODO: determine the assertions that should fail for this test.
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/bad/Chameleon-2B W3C test.
     */
    public void testChameleon2BFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/bad/Chameleon-2B/getBalance.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        // TODO: determine the assertions that should fail for this test.
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/bad/Import-1B W3C test.
     */
    public void testImport1BFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/bad/Import-1B/XSDImport.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        // TODO: determine the assertions that should fail for this test.
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/bad/Import-2B W3C test.
     */
    public void testImport2BFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/bad/Import-2B/XSDImportInWSDL.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        // TODO: determine the assertions that should fail for this test.
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/bad/Import-3B W3C test.
     */
    public void testImport3BFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/bad/Import-3B/XSDImport2.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        // TODO: determine the assertions that should fail for this test.
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/bad/Interface-1B W3C test.
     */
    public void testInterface1BFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/bad/Interface-1B/Interface.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        // TODO: determine the assertions that should fail for this test.
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/bad/Interface-2B W3C test.
     */
    public void testInterface2BFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/bad/Interface-2B/Interface.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        // TODO: determine the assertions that should fail for this test.
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/bad/Interface-3B W3C test.
     */
    public void testInterface3BFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/bad/Interface-3B/Interface.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        // TODO: determine the assertions that should fail for this test.
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/bad/Service-1B W3C test.
     */
    public void testService1BFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/bad/Service-1B/Service.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        // TODO: determine the assertions that should fail for this test.
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/bad/Service-2B W3C test.
     */
    public void testService2BFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/bad/Service-2B/Service.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        // TODO: determine the assertions that should fail for this test.
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/bad/Service-12B W3C test.
     */
    public void testService12BFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/bad/Service-12B/Service.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        // TODO: determine the assertions that should fail for this test.
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/bad/Service-13B W3C test.
     */
    public void testService13BFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/bad/Service-13B/Service.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        // TODO: determine the assertions that should fail for this test.
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/bad/Service-14B W3C test.
     */
    public void testService14BFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/bad/Service-14B/Service.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        // TODO: determine the assertions that should fail for this test.
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/bad/Service-15B W3C test.
     */
    public void testService15BFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/bad/Service-15B/Service.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        // TODO: determine the assertions that should fail for this test.
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/bad/TicketAgent-1B W3C test.
     */
    public void testTicketAgent1BFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/bad/TicketAgent-1B/TicketAgent-bad.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        // TODO: determine the assertions that should fail for this test.
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * GOOD TEST CASES
     * All of the following test cases shouldn't report errors.
     * TODO: Add in error checks as the WSDL 2.0 validator is developed.
     */

    /**
     * Test for the test-suite/documents/good/Chameleon-1G W3C test.
     */
    public void testChameleon1GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/Chameleon-1G/getBalance.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good Chameleon-1G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/Chameleon-2G W3C test.
     */
    public void testChameleon2GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/Chameleon-2G/getBalance.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good Chameleon-2G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/Chameleon-3G W3C test.
     */
    public void testChameleon3GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/Chameleon-3G/getBalance.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good Chameleon-3G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/Chameleon-4G W3C test.
     */
    public void testChameleon4GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/Chameleon-4G/getBalance.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good Chameleon-4G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/CreditCardFaults-1G W3C test.
     */
    public void testCreditCardFaults1GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/CreditCardFaults-1G/use-credit-card-faults.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good CreditCardFaults-1G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/GreatH-1G W3C test.
     */
    public void testGreatH1GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/GreatH-1G/primer-hotelReservationService.wsdl"), handler);
        assertTrue("number of binding elements isn't 1", desc.getBindingElements().length == 1);
        assertTrue("interfacename is null", desc.toComponent().getBindings()[0].getInterface()!=null);

        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good GreatH-1G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/Import-1G W3C test.
     */
    public void testImport1GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/Import-1G/XSDImport.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good Import-1G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/Import-2G W3C test.
     */
    public void testImport2GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/Import-2G/XSDImport2.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good Import-2G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/ImportedWSDL-1G W3C test.
     */
    public void testImportedWSDL1GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/ImportedWSDL-1G/updateDetails.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good ImportedWSDL-1G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/Interface-1G W3C test.
     */
    public void testInterface1GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/Interface-1G/Interface.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good Interface-1G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/Interface-2G W3C test.
     */
    public void testInterface2GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/Interface-2G/Interface.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good Interface-2G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/Interface-3G W3C test.
     */
    public void testInterface3GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/Interface-3G/Interface.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good Interface-3G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/Interface-4G W3C test.
     */
    public void testInterface4GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/Interface-4G/Interface.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good Interface-4G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/Interface-5G W3C test.
     */
    public void testInterface5GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/Interface-5G/Interface.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good Interface-5G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/Interface-6G W3C test.
     */
    public void testInterface6GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/Interface-6G/Interface.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good Interface-6G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/Interface-7G W3C test.
     */
    public void testInterface7GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/Interface-7G/Interface.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good Interface-7G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/MultipleInlineSchemas-1G W3C test.
     */
    public void testMultipleInlineSchemas1GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/MultipleInlineSchemas-1G/retrieveItems.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good MultipleInlineSchemas-1G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/SchemaId-1G W3C test.
     */
    public void testSchemaId1GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/SchemaId-1G/schemaIds.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good SchemaId-1G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/SchemaLocationFragment-1G W3C test.
     */
    public void testSchemaLocationFragment1GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/SchemaLocationFragment-1G/Items.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good SchemaLocationFragment-1G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/Service-1G W3C test.
     */
    public void testService1GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/Service-1G/Service.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good Service-1G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/Service-2G W3C test.
     */
    public void testService2GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/Service-2G/Service.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good Service-2G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/Service-3G W3C test.
     */
    public void testService3GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/Service-3G/Service.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good Service-3G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/ServiceReference-1G W3C test.
     */
    public void testServiceReference1G1FromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/ServiceReference-1G/reservationList.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good ServiceReference-1G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/ServiceReference-1G W3C test.
     */
    public void testServiceReference1G2FromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/ServiceReference-1G/reservationDetails.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good ServiceReference-1G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/TicketAgent-1G W3C test.
     */
    public void testTicketAgent1GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/TicketAgent-1G/TicketAgent.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good TicketAgent-1G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/WeathSvc-1G W3C test.
     */

    ////////////////////////////////////////////////////////////////////////////////
    //FAILED : due to XmlSchemaException:"No namespace found in given ref"        //
    // This has been fixed in XMLSchema, but would have to wait for a release to  //
    // get the jars with the fix                                                  //
    ////////////////////////////////////////////////////////////////////////////////
    public void testWeathSvc1GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/WeathSvc-1G/WeathSvc.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        //TODO FIXME
        //assertFalse("The good WeathSvc-1G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/XsImport-1G W3C test.
     */
    public void testXsImport1GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/XsImport-1G/reservation.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good XsImport-1G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/XsImport-2G W3C test.
     */
    public void testXsImport2GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/XsImport-2G/reservationDetails.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good XsImport-2G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }

    /**
     * Test for the test-suite/documents/good/XsImport-3G W3C test.
     */
    public void testXsImport3GFromOM(){
      try{
        DescriptionElement desc = omWSDLReader.readWSDL(wfr.getFilePath("http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/documents/good/XsImport-3G/reservationDetails.wsdl"), handler);
        assertNotNull("DescriptionElement is null.", desc);
        assertFalse("The good XsImport-3G test returned errors. " + handler.getSummaryOfMessageKeys(), handler.messageHasBeenReported());
      }
      catch(Exception e){
        fail("Unable to read WSDL document because of " + e);
      }
    }
}

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
package org.apache.woden.internal;

import org.apache.woden.WSDLReader;

import junit.framework.TestCase;

/**
 * Unit tests for the ReaderFeatures class.
 * 
 * TODO: Add tests for all features.
 */
public class ReaderFeaturesTest extends TestCase {

	private ReaderFeatures defaultFeatures = new ReaderFeatures();
	private ReaderFeatures features = new ReaderFeatures();
	
	/**
	 * Test that the validation feature is off by default.
	 */
	public void testValidationDefault()
	{
		assertFalse("The validation feature is not off by default.", defaultFeatures.getValue(WSDLReader.FEATURE_VALIDATION));
	}
	
	/**
	 * Test that the getValue method throws an exception for invalid
	 * features.
	 */
	public void testGetValueForInvalidFeature()
	{
		try
		{
			features.getValue("http://invalidfeatureid");
			fail("An IllegalStateException was not thrown when getValue is called for an invalid feature ID.");
		}
		catch(IllegalArgumentException e)
		{
			// The successful case will reach here. Nothing to do at this point.
		}
	}
	
	/**
	 * Test that the setValue method throws an exception for invalid
	 * features.
	 */
	public void testSetValueForInvalidFeature()
	{
		try
		{
			features.setValue("http://invalidfeatureid", true);
			fail("An IllegalStateException was not thrown when setValue is called for an invalid feature ID.");
		}
		catch(IllegalArgumentException e)
		{
			// The successful case will reach here. Nothing to do at this point.
		}
	}
	
	/**
	 * Test that setting values to on or off works correctly.
	 */
	public void testSetValue()
	{
		features.setValue(WSDLReader.FEATURE_VALIDATION, true);
		assertTrue("The validation feature is not set to true.", features.getValue(WSDLReader.FEATURE_VALIDATION));
		
		features.setValue(WSDLReader.FEATURE_VALIDATION, false);
		assertFalse("The validation feature is not set to false.", features.getValue(WSDLReader.FEATURE_VALIDATION));
	}

}

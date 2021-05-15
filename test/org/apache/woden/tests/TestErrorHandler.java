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

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.woden.ErrorHandler;
import org.apache.woden.ErrorInfo;

/**
 * An error handler that will allow the test cases to get access to the
 * errors and warnings reported by the validators.
 */
public class TestErrorHandler implements ErrorHandler 
{
  public Hashtable warnings = new Hashtable();
  public int numWarnings = 0;
  public Hashtable errors = new Hashtable();
  public int numErrors = 0;
  public Hashtable fatalErrors = new Hashtable();
  public int numFatalErrors = 0;

  /**
   * Reset the handler. Remove all messages stored in the handler.
   */
  public void reset()
  {
	warnings.clear();
	numWarnings = 0;
	errors.clear();
	numErrors = 0;
	fatalErrors.clear();
	numFatalErrors = 0;
  }
  
  /**
   * Determine whether a message has been reported.
   * 
   * @return True if a message has been reported, false otherwise.
   */
  public boolean messageHasBeenReported()
  {
	if(numWarnings + numErrors + numFatalErrors == 0)
	  return false;
	return true;
  }
  
  /**
   * Get a summary of the message keys. This is used in
   * reporting the keys of messages that were reported.
   * 
   * @return A summary string of the message keys.
   */
  public String getSummaryOfMessageKeys()
  {
	StringBuffer summary = new StringBuffer();
	
	if(numFatalErrors > 0)
	{
	  summary.append("Fatal Errors: ");
	  Enumeration keys = fatalErrors.keys();
	  while(keys.hasMoreElements())
	  {
		summary.append(keys.nextElement()).append(" ");
	  }
	  summary.append("\n");
	}
	
	if(numErrors > 0)
	{
	  summary.append("Errors: ");
	  Enumeration keys = errors.keys();
	  while(keys.hasMoreElements())
	  {
		summary.append(keys.nextElement()).append(" ");
	  }
	  summary.append("\n");
	}
	
	if(numWarnings > 0)
	{
	  summary.append("Warnings: ");
	  Enumeration keys = warnings.keys();
	  while(keys.hasMoreElements())
	  {
		summary.append(keys.nextElement()).append(" ");
	  }
	}
	
	return summary.toString();
  }
  
  /* (non-Javadoc)
   * @see org.apache.woden.ErrorHandler#warning(org.apache.woden.ErrorInfo)
   */
  public void warning(ErrorInfo errorInfo)
  {
    warnings.put(errorInfo.getKey(), errorInfo);
    numWarnings++;
  }

  /* (non-Javadoc)
   * @see org.apache.woden.ErrorHandler#error(org.apache.woden.ErrorInfo)
   */
  public void error(ErrorInfo errorInfo) 
  {
    errors.put(errorInfo.getKey(), errorInfo);
    numErrors++;
  }

  /* (non-Javadoc)
   * @see org.apache.woden.ErrorHandler#fatalError(org.apache.woden.ErrorInfo)
   */
  public void fatalError(ErrorInfo errorInfo) 
  {
    fatalErrors.put(errorInfo.getKey(), errorInfo);
    numFatalErrors++;
  }
  
  

}

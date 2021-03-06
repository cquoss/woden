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
package org.apache.woden;

/**
 * This interface describes the three types of errors 
 * (warning, error and fatal error) that may be reported. 
 * Implementations may choose to override the 
 * <code>toString()</code> method inherited from java.lang.Object 
 * to concatenate this information into format suitable for
 * reporting purposes.
 *
 * @author kaputin
 */
public interface ErrorInfo {

    public ErrorLocator getErrorLocator();

    public String getKey();

    public String getMessage();
    
    public Exception getException();
}
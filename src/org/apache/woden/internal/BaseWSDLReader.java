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
package org.apache.woden.internal;

import org.apache.woden.ErrorReporter;
import org.apache.woden.WSDLException;
import org.apache.woden.WSDLFactory;
import org.apache.woden.WSDLReader;
import org.apache.woden.wsdl20.extensions.ExtensionRegistry;


/**
 * This abstract class contains properties and methods common 
 * to WSDLReader implementations.
 * 
 * <p>
 * TODO a Template Inheritance pattern that ensures WSDL validation gets invoked 
 * (if turned on by some property) after the subclass has parsed the WSDL. Note, 
 * this class is currently WSDL version-independent and XML parser-independent;
 * should try to keep it that way. 
 * 
 * @author jkaputin@apache.org
 */
public abstract class BaseWSDLReader implements WSDLReader {
    
    private WSDLFactory fFactory;    
    private String fFactoryImplName = null;
    private ErrorReporter fErrorReporter;
    private ExtensionRegistry fExtReg;
    
    protected ReaderFeatures features;

    protected BaseWSDLReader() throws WSDLException {
        fErrorReporter= new ErrorReporterImpl(); 
        features = new ReaderFeatures();
    }
    
    /**
     * @return Returns the fErrorReporter.
     */
    public ErrorReporter getErrorReporter() 
    {
        return fErrorReporter;
    }
    
    /**
     * Get the cached WSDLFactory if there is one, otherwise
     * create and cache a new one.
     * 
     * @return Returns a.
     */
    protected WSDLFactory getFactory() throws WSDLException 
    {
        if(fFactory == null) 
        {
            fFactory = (fFactoryImplName != null)
                        ? WSDLFactory.newInstance(fFactoryImplName)
                        : WSDLFactory.newInstance();
        }
        return fFactory;
    }

    /**
     * Stores the name of the WSDLFactory implementation class to be used for
     * any subsequent WSDLFactory requests, first discarding any cached factory 
     * object.
     * 
     * @param factoryImplName the WSDLFactory implementation classname
     */
    public void setFactoryImplName(String factoryImplName) {
        
        fFactory = null;
        
        fFactoryImplName = factoryImplName;
    }
    
    /**
     * @return the WSDLFactory implementation classname
     */
    public String getFactoryImplName() {
        return fFactoryImplName;
    }
    
    public void setExtensionRegistry(ExtensionRegistry extReg)
    {
        fExtReg = extReg;
    }
    
    public ExtensionRegistry getExtensionRegistry()
    {
        return fExtReg;    
    }
    
    /**
     * Set a named feature on or off with a boolean. Note, this relates to 
     * features of the Woden framework, not to WSDL-specific features such
     * as the WSDL 2.0 Feature component.
     * <p>
     * All feature names should be fully-qualified, Java package style to 
     * avoid name clashes. All names starting with org.apache.woden. are 
     * reserved for features defined by the Woden implementation. 
     * Features specific to other implementations should be fully-qualified  
     * to match the package name structure of that implementation. 
     * For example: com.abc.featureName
     * 
     * @param name the name of the feature to be set
     * @param value a boolean value where true sets the feature on, false sets it off
     * @throws IllegalArgumentException if the feature name is not recognized.
     */
    public void setFeature(String name, boolean value) 
    {
        if(name == null) 
        {
            //name must not be null
            throw new IllegalArgumentException(
                    fErrorReporter.getFormattedMessage("WSDL005", null));
        }
        try
        {
        	features.setValue(name, value);
        }
        catch(IllegalArgumentException e)
        {
        	// Feature name is not recognized, so throw an exception.
            Object[] args = new Object[] {name};
            throw new IllegalArgumentException(
                    fErrorReporter.getFormattedMessage("WSDL006", args));
        }
    }

    /**
     * Returns the on/off setting of the named feature, represented as a boolean. 
     * 
     * @param name the name of the feature to get the value of
     * @return a boolean representing the on/off state of the named feature
     * @throws IllegalArgumentException if the feature name is not recognized.
     */
    public boolean getFeature(String name)
    {
        if(name == null) 
        {
            //name must not be null
            throw new IllegalArgumentException(
                    fErrorReporter.getFormattedMessage("WSDL005", null));
        }
        
        try
        {
        	return features.getValue(name);
        }
        catch(IllegalArgumentException e)
        {
        	// Feature name is not recognized, so throw an exception.
            Object[] args = new Object[] {name};
            throw new IllegalArgumentException(
                    fErrorReporter.getFormattedMessage("WSDL006", args));
        }
    }
    
    /**
     * Set a named property to the specified object.  Note, this relates to 
     * properties of the Woden implementation, not to WSDL-specific properties
     * such as the WSDL 2.0 Property component.
     * <p>
     * All property names should be fully-qualified, Java package style to 
     * avoid name clashes. All names starting with org.apache.woden. are 
     * reserved for properties defined by the Woden implementation. 
     * Properties specific to other implementations should be fully-qualified  
     * to match the package name structure of that implementation. 
     * For example: com.abc.propertyName
     * 
     * @param name the name of the property to be set
     * @param value an Object representing the value to set the property to
     * @throws IllegalArgumentException if the property name is not recognized.
     */
    public void setProperty(String name, Object value) 
    {
        if(name == null) 
        {
            //name must not be null
            throw new IllegalArgumentException(
                    fErrorReporter.getFormattedMessage("WSDL007", null));
        }
        else if(name.equals("xyz"))
        {
            //TODO determine the required properties and
            //create an if block for each one to set the value.
        }
        else
        {
            //property name is not recognized, so throw an exception
            Object[] args = new Object[] {name};
            throw new IllegalArgumentException(
                    fErrorReporter.getFormattedMessage("WSDL008", args));
        }
    }

    /**
     * Returns the value of the named property.
     * 
     * @param name the name of the property to get the value of
     * @return an Object representing the property's value
     * @throws IllegalArgumentException if the property name is not recognized.
     */
    public Object getProperty(String name)
    {
        if(name == null) 
        {
            //name must not be null
            throw new IllegalArgumentException(
                    fErrorReporter.getFormattedMessage("WSDL007", null));
        }
        
        //Return the property's value or throw an exception if the property
        //name is not recognized
        
        if(name.equals("xyz"))
        {
            //TODO determine the required properties and
            //create an if block for each one to get the value.
            return null;
        }
        else
        {
            //property name is not recognized, so throw an exception
            Object[] args = new Object[] {name};
            throw new IllegalArgumentException(
                    fErrorReporter.getFormattedMessage("WSDL008", args));
        }
    }
    
}
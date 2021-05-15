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
package org.apache.woden.internal.wsdl20;

import java.util.List;
import java.util.Vector;

import org.apache.woden.wsdl20.ConfigurableComponent;
import org.apache.woden.wsdl20.Feature;
import org.apache.woden.wsdl20.Property;
import org.apache.woden.wsdl20.xml.ConfigurableElement;
import org.apache.woden.wsdl20.xml.FeatureElement;
import org.apache.woden.wsdl20.xml.PropertyElement;

/**
 * This abstract superclass implements support for features and
 * properties. All classes representing WSDL components that
 * can be configured with features and properties will directly 
 * or indirectly extend this abstract class.
 * 
 * @author jkaputin@apache.org
 */
public abstract class ConfigurableImpl extends DocumentableImpl 
                                       implements ConfigurableComponent,
                                                  ConfigurableElement 
{
    private List fFeatures = new Vector();
    private List fProperties = new Vector();

    /* ************************************************************
     *  API methods for Property and Feature components.
     * ************************************************************/
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.ConfigurableComponent#getFeatures()
     */
    public Feature[] getFeatures() 
    {
        Feature[] array = new Feature[fFeatures.size()];
        fFeatures.toArray(array);
        return array;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.ConfigurableComponent#getProperties()
     */
    public Property[] getProperties() 
    {
        Property[] array = new Property[fProperties.size()];
        fProperties.toArray(array);
        return array;
    }

    /* ************************************************************
     *  API methods for Property and Feature elements.
     * ************************************************************/
    
    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.ConfigurableElement#addFeatureElement()
     */
    public FeatureElement addFeatureElement() 
    {
        FeatureElement feature = new FeatureImpl();
        fFeatures.add(feature);
        feature.setParentElement(this);
        return feature;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.ConfigurableElement#getFeatureElements()
     */
    public FeatureElement[] getFeatureElements() 
    {
        FeatureElement[] array = new FeatureElement[fFeatures.size()];
        fFeatures.toArray(array);
        return array;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.ConfigurableElement#addPropertyElement(org.apache.woden.wsdl20.xml.PropertyElement)
     */
    public PropertyElement addPropertyElement() 
    {
        PropertyElement property = new PropertyImpl();
        fProperties.add(property);
        property.setParentElement(this);
        return property;
    }

    /* (non-Javadoc)
     * @see org.apache.woden.wsdl20.xml.ConfigurableElement#getPropertyElements()
     */
    public PropertyElement[] getPropertyElements() 
    {
        PropertyElement[] array = new PropertyElement[fProperties.size()];
        fProperties.toArray(array);
        return array;
    }
}

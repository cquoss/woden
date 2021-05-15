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
package org.apache.woden.wsdl20.extensions;

import java.net.URI;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.woden.ErrorReporter;
import org.apache.woden.WSDLException;
import org.apache.woden.wsdl20.xml.WSDLElement;
import org.apache.woden.xml.UnknownAttr;
import org.apache.woden.xml.XMLAttr;

/**
 * This class is used to associate serializers, deserializers, and
 * Java implementation types with extension elements.
 * It also associates Java implementation types only with extension attributes,
 * but not serializers and deserializers.
 * <p>
 * This class has been copied from WSDL4J and modified for Woden.
 * 
 * TODO update method javadocs.
 *
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 * @author jkaputin@apache.org
 */
public class ExtensionRegistry
{

  /**
   * Creates the extension registry, and sets the defaultSerializer
   * and defaultDeserializer properties to instances of an
   * UnknownExtensionSerializer, and an UnknownExtensionDeserializer,
   * respectively.
   */
  public ExtensionRegistry()
  {
    setDefaultSerializer(new UnknownExtensionSerializer());
    setDefaultDeserializer(new UnknownExtensionDeserializer());
  }

  /*
    This is a Map of Maps. The top-level Map is keyed by (Class)parentType,
    and the inner Maps are keyed by (QName)elementQN.
  */
  protected Map serializerReg = new Hashtable();
  /*
    This is a Map of Maps. The top-level Map is keyed by (Class)parentType,
    and the inner Maps are keyed by (QName)elementQN.
  */
  protected Map deserializerReg = new Hashtable();
  /*
    This is a Map of Maps. The top-level Map is keyed by (Class)parentType,
    and the inner Maps are keyed by (QName)elementQN.
  */
  protected Map extElementReg = new Hashtable();
  protected ExtensionSerializer defaultSer = null;
  protected ExtensionDeserializer defaultDeser = null;
  /*
    This is a Map of Maps. The top-level Map is keyed by (Class)parentType,
    and the inner Maps are keyed by (QName)attrName.
  */
  protected Map extAttributeReg = new Hashtable();
  
  /*
   * This is a Map of Maps. The top-level Map is keyed by (Class)parentComponent
   * and the inner Map is keyed by (URI)extensionNamespace with a value of
   * (Class)componentExtensions.
   */
  protected Map compExtReg = new Hashtable();
  
  private ErrorReporter errorReporter = null;
  
  public void setErrorReporter(ErrorReporter errRpt)
  {
      this.errorReporter = errRpt;
  }
  
  public ErrorReporter getErrorReporter()
  {
      //TODO create a newErrorReporter method on WSDLFactory, then lazy init this field.
      return this.errorReporter;
  }

  /**
   * Set the serializer to be used when none is found for an extensibility
   * element. Set this to null to have an exception thrown when
   * unexpected extensibility elements are encountered. Default value is
   * an instance of UnknownExtensionSerializer.
   *
   * @see UnknownExtensionSerializer
   */
  public void setDefaultSerializer(ExtensionSerializer defaultSer)
  {
    this.defaultSer = defaultSer;
  }

  /**
   * Get the serializer to be used when none is found for an extensibility
   * element. Default value is an instance of UnknownExtensionSerializer.
   *
   * @see UnknownExtensionSerializer
   */
  public ExtensionSerializer getDefaultSerializer()
  {
    return defaultSer;
  }

  /**
   * Set the deserializer to be used when none is found for an encountered
   * element. Set this to null to have an exception thrown when
   * unexpected extensibility elements are encountered. Default value is
   * an instance of UnknownExtensionDeserializer.
   *
   * @see UnknownExtensionDeserializer
   */
  public void setDefaultDeserializer(ExtensionDeserializer defaultDeser)
  {
    this.defaultDeser = defaultDeser;
  }

  /**
   * Get the deserializer to be used when none is found for an encountered
   * element. Default value is an instance of UnknownExtensionDeserializer.
   *
   * @see UnknownExtensionDeserializer
   */
  public ExtensionDeserializer getDefaultDeserializer()
  {
    return defaultDeser;
  }

  /**
   * Declare that the specified serializer should be used to serialize
   * all extensibility elements with a qname matching elementQN, when
   * encountered as children of the specified parentType.
   *
   * @param parentType a class object indicating where in the WSDL
   * definition this extension was encountered. For
   * example, javax.wsdl.Binding.class would be used to indicate
   * this extensibility element was found in the list of
   * extensibility elements belonging to a javax.wsdl.Binding.
   * @param elementType the qname of the extensibility element
   * @param es the extension serializer to use
   *
   * @see #querySerializer(Class, QName)
   */
  public void registerSerializer(Class parentType,
                                 QName elementType,
                                 ExtensionSerializer es)
  {
    Map innerSerializerReg = (Map)serializerReg.get(parentType);

    if (innerSerializerReg == null)
    {
      innerSerializerReg = new Hashtable();

      serializerReg.put(parentType, innerSerializerReg);
    }

    innerSerializerReg.put(elementType, es);
  }

  /**
   * Declare that the specified deserializer should be used to deserialize
   * all extensibility elements with a qname matching elementQN, when
   * encountered as immediate children of the element represented by the
   * specified parentType.
   *
   * @param parentType a class object indicating where in the WSDL
   * document this extensibility element was encountered. For
   * example, javax.wsdl.Binding.class would be used to indicate
   * this element was encountered as an immediate child of
   * a &lt;wsdl:binding&gt; element.
   * @param elementType the qname of the extensibility element
   * @param ed the extension deserializer to use
   *
   * @see #queryDeserializer(Class, QName)
   */
  public void registerDeserializer(Class parentType,
                                   QName elementType,
                                   ExtensionDeserializer ed)
  {
    Map innerDeserializerReg = (Map)deserializerReg.get(parentType);

    if (innerDeserializerReg == null)
    {
      innerDeserializerReg = new Hashtable();

      deserializerReg.put(parentType, innerDeserializerReg);
    }

    innerDeserializerReg.put(elementType, ed);
  }

  /**
   * Look up the serializer to use for the extensibility element with
   * the qname elementQN, which was encountered as a child of the
   * specified parentType.
   *
   * @param parentType a class object indicating where in the WSDL
   * definition this extension was encountered. For
   * example, javax.wsdl.Binding.class would be used to indicate
   * this extensibility element was found in the list of
   * extensibility elements belonging to a javax.wsdl.Binding.
   * @param elementType the qname of the extensibility element
   *
   * @return the extension serializer, if one was found. If none was
   * found, the behavior depends on the value of the defaultSerializer
   * property. If the defaultSerializer property is set to a non-null
   * value, that value is returned; otherwise, a WSDLException is
   * thrown.
   *
   * @see #registerSerializer(Class, QName, ExtensionSerializer)
   * @see #setDefaultSerializer(ExtensionSerializer)
   */
  public ExtensionSerializer querySerializer(Class parentType,
                                             QName elementType)
                                               throws WSDLException
  {
    Map innerSerializerReg = (Map)serializerReg.get(parentType);
    ExtensionSerializer es = null;

    if (innerSerializerReg != null)
    {
      es = (ExtensionSerializer)innerSerializerReg.get(elementType);
    }

    if (es == null)
    {
      es = defaultSer;
    }

    if (es == null)
    {
      throw new WSDLException(WSDLException.CONFIGURATION_ERROR,
                              "No ExtensionSerializer found " +
                              "to serialize a '" + elementType +
                              "' element in the context of a '" +
                              parentType.getName() + "'.");
    }

    return es;
  }

  /**
   * Look up the deserializer for the extensibility element with the
   * qname elementQN, which was encountered as an immediate child
   * of the element represented by the specified parentType.
   *
   * @param parentType a class object indicating where in the WSDL
   * document this extensibility element was encountered. For
   * example, javax.wsdl.Binding.class would be used to indicate
   * this element was encountered as an immediate child of
   * a &lt;wsdl:binding&gt; element.
   * @param elementType the qname of the extensibility element
   *
   * @return the extension deserializer, if one was found. If none was
   * found, the behavior depends on the value of the defaultDeserializer
   * property. If the defaultDeserializer property is set to a non-null
   * value, that value is returned; otherwise, a WSDLException is thrown.
   *
   * @see #registerDeserializer(Class, QName, ExtensionDeserializer)
   * @see #setDefaultDeserializer(ExtensionDeserializer)
   */
  public ExtensionDeserializer queryDeserializer(Class parentType,
                                                 QName elementType)
                                                   throws WSDLException
  {
    Map innerDeserializerReg = (Map)deserializerReg.get(parentType);
    ExtensionDeserializer ed = null;

    if (innerDeserializerReg != null)
    {
      ed = (ExtensionDeserializer)innerDeserializerReg.get(elementType);
    }

    if (ed == null)
    {
      ed = defaultDeser;
    }

    if (ed == null)
    {
      throw new WSDLException(WSDLException.CONFIGURATION_ERROR,
                              "No ExtensionDeserializer found " +
                              "to deserialize a '" + elementType +
                              "' element in the context of a '" +
                              parentType.getName() + "'.");
    }

    return ed;
  }

  /**
   * Look up the type of the extensibility element with the specified qname, which
   * was defined as a child of the element represented by the specified parent class.
   *
   * @param parentClass a class object indicating where in the WSDL
   * document this extensibility attribute was encountered. For
   * example, javax.wsdl.Binding.class would be used to indicate
   * this attribute was defined on a &lt;wsdl:binding> element.
   * @param elemQN the qname of the extensibility attribute
   *
   * @return one of the constants defined on the AttributeExtensible class
   *
   * @see #registerExtAttributeType(Class, QName, Class)
   * @see AttributeExtensible
   */
  public Class queryExtElementType(Class parentClass, QName elemQN)
  {
    Map innerExtensionAttributeReg =
      (Map)extAttributeReg.get(parentClass);
    Class elemClass = null;

    if (innerExtensionAttributeReg != null)
    {
        elemClass = (Class)innerExtensionAttributeReg.get(elemQN);
    }

    return elemClass;
  }
  
  /**
   * TODO make the return val typesafe, and create similar method for ext attrs.
   * 
   * Returns a set of QNames representing the extensibility elements
   * that are allowed as children of the specified parent type.
   * Basically, this method returns the keys associated with the set
   * of extension deserializers registered for this parent type.
   * Returns null if no extension deserializers are registered for
   * this parent type.
   */
  public Set getAllowableExtensions(Class parentType)
  {
    Map innerDeserializerReg = (Map)deserializerReg.get(parentType);

    return (innerDeserializerReg != null)
           ? innerDeserializerReg.keySet()
           : null;
  }

  /**
   * Declare that the specified extensionType is the concrete
   * class which should be used to represent extensibility elements
   * with qnames matching elementQN, that are intended to exist as
   * children of the specified parentType.
   *
   * @param parentType a class object indicating where in the WSDL
   * definition this extension would exist. For example,
   * javax.wsdl.Binding.class would be used to indicate
   * this extensibility element would be added to the list of
   * extensibility elements belonging to a javax.wsdl.Binding,
   * after being instantiated.
   * @param elementType the qname of the extensibility element
   * @param extensionType the concrete class which should be instantiated
   *
   * @see #createExtElement(Class, QName)
   */
  public void registerExtElementType(Class parentType,
                                QName elementType,
                                Class extensionType)
  {
    Map innerExtensionTypeReg = (Map)extElementReg.get(parentType);

    if (innerExtensionTypeReg == null)
    {
      innerExtensionTypeReg = new Hashtable();

      extElementReg.put(parentType, innerExtensionTypeReg);
    }

    innerExtensionTypeReg.put(elementType, extensionType);
  }

  /**
   * Create an instance of the type which was declared to be used to
   * represent extensibility elements with qnames matching elementQN,
   * when intended to exist as children of the specified parentType.
   * This method allows a user to instantiate an extensibility element
   * without having to know the implementing type.
   *
   * @param parentType a class object indicating where in the WSDL
   * definition this extension will exist. For example,
   * javax.wsdl.Binding.class would be used to indicate
   * this extensibility element is going to be added to the list of
   * extensibility elements belonging to a javax.wsdl.Binding,
   * after being instantiated.
   * @param elementType the qname of the extensibility element
   *
   * @return a new instance of the type used to represent the
   * specified extension
   *
   * @see #registerExtElementType(Class, QName, Class)
   */
  public ExtensionElement createExtElement(Class parentType,
                                              QName elementType)
                                                throws WSDLException
  {
    Map innerExtensionTypeReg = (Map)extElementReg.get(parentType);
    Class extensionType = null;

    if (innerExtensionTypeReg != null)
    {
      extensionType = (Class)innerExtensionTypeReg.get(elementType);
    }

    if (extensionType == null)
    {
      //TODO use ErrorReporter to get formatted error msg WSDL012
      throw new WSDLException(WSDLException.CONFIGURATION_ERROR,
                              "No Java extensionType found " +
                              "to represent a '" + elementType +
                              "' element in the context of a '" +
                              parentType.getName() + "'.");
    }
    else if (!(ExtensionElement.class.isAssignableFrom(extensionType)))
    {
      //TODO use ErrorReporter to get formatted error msg WSDL013
      throw new WSDLException(WSDLException.CONFIGURATION_ERROR,
                              "The Java extensionType '" +
                              extensionType.getName() + "' does " +
                              "not implement the ExtensibilityElement " +
                              "interface.");
    }

    try
    {
        ExtensionElement ee = (ExtensionElement)extensionType.newInstance();
      /*
      if (ee.getElementType() == null)
      {
        ee.setElementType(elementQN);
      }
      */
      
      return ee;
    }
    catch (Exception e)
    {
      /*
        Catches:
                 InstantiationException
                 IllegalAccessException
      */
      throw new WSDLException(WSDLException.CONFIGURATION_ERROR,
                              "Problem instantiating Java " +
                              "extensionType '" + extensionType.getName() +
                              "'.",
                              e);
    }
  }

  /**
   * Declare that the type of the specified extension attribute, when it occurs
   * as an attribute of the specified parent type, should be assumed to be
   * attrType.
   *
   * @param ownerClass a class object indicating where in the WSDL
   * document this extensibility attribute was encountered. For
   * example, javax.wsdl.Binding.class would be used to indicate
   * this attribute was defined on a &lt;wsdl:binding> element.
   * @param attrQName the qname of the extensibility attribute
   * @param attrClass one of the constants defined on the AttributeExtensible
   * class
   *
   * @see #queryExtAttributeType(Class, QName)
   * @see AttributeExtensible
   */
  public void registerExtAttributeType(Class ownerClass,
                                       QName attrQName,
                                       Class attrClass)
  {
    Map innerExtensionAttributeReg =
      (Map)extAttributeReg.get(ownerClass);

    if (innerExtensionAttributeReg == null)
    {
      innerExtensionAttributeReg = new Hashtable();

      extAttributeReg.put(ownerClass, innerExtensionAttributeReg);
    }

    innerExtensionAttributeReg.put(attrQName, attrClass);
  }

  /**
   * Look up the type of the extensibility attribute with the specified qname,
   * which was defined on an element represented by the specified parent class.
   *
   * @param parentClass a class object indicating where in the WSDL
   * document this extensibility attribute was encountered. For
   * example, javax.wsdl.Binding.class would be used to indicate
   * this attribute was defined on a &lt;wsdl:binding> element.
   * @param attrQN the qname of the extensibility attribute
   *
   * @return one of the constants defined on the AttributeExtensible class
   *
   * @see #registerExtAttributeType(Class, QName, Class)
   * @see AttributeExtensible
   */
  public Class queryExtAttributeType(Class parentClass, QName attrQN)
  {
    Map innerExtensionAttributeReg =
      (Map)extAttributeReg.get(parentClass);
    Class attrClass = null;

    if (innerExtensionAttributeReg != null)
    {
        attrClass = (Class)innerExtensionAttributeReg.get(attrQN);
    }

    return attrClass;
  }
  
  public XMLAttr createExtAttribute(Class ownerClass, QName attrQName)
                                          throws WSDLException
  {
      Map innerExtensionAttributeReg = (Map)extAttributeReg.get(ownerClass);
      Class implClass = null;
      XMLAttr attr = null;
      
      if (innerExtensionAttributeReg != null)
      {
          implClass = (Class)innerExtensionAttributeReg.get(attrQName);
      }
      
      if (implClass == null)
      {
          /*
           * Implementations MAY register a default class to handle unknown extension attributes,
           * which will be retrieved here from the Extension Registry. For example, the
           * StringAttr implementation class may be registered as the default.
           * 
           * TODO TBC, then add this info to javadoc and user docs. See sample code in PopulatedExtReg.
           */
          implClass = queryExtAttributeType(WSDLElement.class, 
                  new QName("http://ws.apache.org/woden", "DefaultAttr"));

          //If no default Java class is registered, use the UnknownAttr class.
          if(implClass == null) implClass = UnknownAttr.class;
          
           
          //Decided it is not necessary to report WSDL010 as a warning to the user, 
          //however we will keep the message code for now in case we add logging capability 
          //later and can capture it in a log (i.e. pull vs. push diagnositic information)
          
          /* TODO the following line was replaced by null in the reportError call below
           * to avoid an API build problem for Milestone 2. This must be fixed, post-M2.
           * e.g. provide some creator for error locator that hides the Impl class.
           *  
           * new ErrorLocatorImpl(),  //TODO line/col nos.
           *
          getErrorReporter().reportError( 
              null,        //see comment above                   
              "WSDL010", 
              new Object[] {attrQName.toString(), 
                            ownerClass.getName(), 
                            implClass.getName()},
              ErrorReporter.SEVERITY_WARNING);
           */
      }
      else if (!(XMLAttr.class.isAssignableFrom(implClass)))
      {
          String msg = getErrorReporter().getFormattedMessage("WSDL011", 
                           new Object[] {attrQName.toString()});    
          throw new WSDLException(WSDLException.CONFIGURATION_ERROR, "WSDL011: " + msg);
      }
      
      try {
          attr = (XMLAttr)implClass.newInstance();
          attr.setErrorReporter(getErrorReporter());
      } 
      catch (InstantiationException e) 
      {
          String msg = getErrorReporter().getFormattedMessage("WSDL009",
                           new Object[] {implClass.getName()});
          throw new WSDLException(WSDLException.CONFIGURATION_ERROR, "WSDL009: " + msg, e);
      } 
      catch (IllegalAccessException e) {
          String msg = getErrorReporter().getFormattedMessage("WSDL009",
                           new Object[] {implClass.getName()});
          throw new WSDLException(WSDLException.CONFIGURATION_ERROR, msg, e);
      }
      
      return attr;
  }
  
  /**
   * Register the Java class which will represent extensions from a specified 
   * namespace that will extend the specified WSDL component class.
   * 
   * @param parentClass the WSDL component class
   * @param extNamespace the extension namespace
   * @param compExtClass the Java class representing these extensions
   */
  public void registerComponentExtension(Class parentClass,
                                         URI extNamespace,
                                         Class compExtClass)
  {
      if(!(ComponentExtensions.class.isAssignableFrom(compExtClass)))
      {
          String msg = getErrorReporter().getFormattedMessage("WSDL016", 
                  new Object[] {compExtClass.getName()});    
          throw new IllegalArgumentException(msg);
      }
      
      Map innerCompExtReg =
          (Map)compExtReg.get(parentClass);
      
      if (innerCompExtReg == null)
      {
          innerCompExtReg = new Hashtable();
          
          compExtReg.put(parentClass, innerCompExtReg);
      }
      
      innerCompExtReg.put(extNamespace, compExtClass);
  }
  
  /**
   * Return the Java class that represents the extensions from the specified
   * namespace that extend the specified WSDL component class.
   * 
   * @param parentClass the WSDL component
   * @param extNamespace the extension namespace
   * @return the Class of the component extensions
   */
  public Class queryComponentExtension(Class parentClass, URI extNamespace)
  {
      Map innerCompExtReg =
          (Map)compExtReg.get(parentClass);
      Class compExtClass = null;
      
      if (innerCompExtReg != null)
      {
          compExtClass = (Class)innerCompExtReg.get(extNamespace);
      }
      
      return compExtClass;
  }

  /**
   * Return the extension namespaces registered for the specified WSDL Component class.
   * 
   * @param parentClass the class of WSDL component extended by these namespaces
   * @return an array of namespace URIs
   */
  public URI[] queryComponentExtensionNamespaces(Class parentClass)
  {
      Map innerCompExtReg =
          (Map)compExtReg.get(parentClass);
      
      if (innerCompExtReg != null)
      {
          Set namespaceKeys = innerCompExtReg.keySet();
          URI[] extNamespaces = new URI[namespaceKeys.size()];
          namespaceKeys.toArray(extNamespaces);
          return extNamespaces;
      }
      else
      {
          return new URI[0]; //return an empty array, rather than null
      }
      
  }
  
  /**
   * Return a ComponentExtensions object from the Java class registered for 
   * the specified extension namespace against the specified WSDL component class.
   * 
   * @param parentClass the WSDL component class.
   * @param extNamespace the extension namespace.
   * @return a <code>ComponentExtensions</code> object
   * @throws WSDLException if no Java class is registered for this namespace and WSDL component.
   */
  public ComponentExtensions createComponentExtension(Class parentClass, 
                                                      URI extNamespace)
                                                      throws WSDLException
  {
      Class compExtClass = queryComponentExtension(parentClass, extNamespace);
      
      if(compExtClass == null)
      {
          String msg = getErrorReporter().getFormattedMessage("WSDL015",
                  new Object[] {extNamespace.toString(), parentClass.getName()});
          throw new WSDLException(WSDLException.CONFIGURATION_ERROR, "WSDL015: " + msg);
      }
          
      ComponentExtensions compExt = null;
      
      try {
          compExt = (ComponentExtensions)compExtClass.newInstance();
      } 
      catch (InstantiationException e) 
      {
          e.printStackTrace();
          String msg = getErrorReporter().getFormattedMessage("WSDL009",
                  new Object[] {compExtClass.getName()});
          throw new WSDLException(WSDLException.CONFIGURATION_ERROR, "WSDL009: " + msg, e);
      } 
      catch (IllegalAccessException e) {
          String msg = getErrorReporter().getFormattedMessage("WSDL009",
                  new Object[] {compExtClass.getName()});
          throw new WSDLException(WSDLException.CONFIGURATION_ERROR, "WSDL009: " + msg, e);
      }
      
      return compExt;
  }
  
}
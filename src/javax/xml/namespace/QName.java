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
package javax.xml.namespace;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * <code>QName</code> class represents the value of a qualified name
 * as specified in <a href="http://www.w3.org/TR/xmlschema-2/#QName">XML
 * Schema Part2: Datatypes specification</a>.
 * <p>
 * The WSDL4J version of QName has been copied and updated for Apache Woden.
 * It now conforms to the version 1.1 QName described in the Javadoc at 
 * <code>
 * "http://java.sun.com/j2ee/1.4/docs/api/javax/xml/namespace/QName.html".
 * </code>
 * <p>
 * The value of a QName contains a <b>namespaceURI</b>, a <b>localPart</b> 
 * and a <b>prefix</b>.
 * The localPart provides the local part of the qualified name. The
 * namespaceURI is a URI reference identifying the namespace. The prefix 
 * corresponds to a namespace declaration 'xmlns:somePrefix' in the underlying xml. 
 *
 * Note: Some of this impl code was taken from Axis.
 * <p>
 * The constructors throw an IllegalArgumentException if the 'localPart' 
 * argument is null, but if it is the empty string ("") they just create
 * the QName object with the localPart set to the empty string.
 * The 'prefix' property defaults to the empty string for the two 
 * constructors that do not take a 'prefix' argument. 
 * The constructor that does take a 'prefix' argument will throw an 
 * IllegalArgumentException if a null value is used (i.e. the absence 
 * of any 'prefix' must be specified explicitly as the empty string "").
 * To support the deserialization of objects that were serialized with an
 * older version of QName (i.e. without a 'prefix' field), the 
 * <code>readObject</code> method will check if the 'prefix' 
 * value is null after default deserialization and if so, change it to the 
 * empty string. 
 *
 * @author axis-dev
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 * @author jkaputin@apache.org
 */
public class QName implements Serializable
{
  // Comment/shared empty string.
  private static final String emptyString = "";

  // Field namespaceURI.
  private String namespaceURI;

  // Field localPart.
  private String localPart;
  
  // Field prefix.
  private String prefix;

  private static final long serialVersionUID = -9120448754896609940L;

  /**
   * Constructor for the QName.
   * Takes a localPart and sets the namespace and prefix to 
   * the empty string "".
   *
   * @param localPart Local part of the QName
   * 
   * @throws IllegalArgumentException if localPart is null.
   */
  public QName(String localPart)
  {
      this(emptyString, localPart, emptyString);
  }

  /**
   * Constructor for the QName.
   * Takes a localPart and a namespace and sets the prefix to 
   * the empty string "". If namespace is null, it defaults to
   * the empty string.
   *
   * @param namespaceURI Namespace URI for the QName
   * @param localPart Local part of the QName.
   * 
   * @throws IllegalArgumentException if localPart is null.
   */
  public QName(String namespaceURI, String localPart)
  {
      this(namespaceURI, localPart, emptyString);
  }

  /**
   * Constructor for the QName.
   * Takes a localPart, a namespace and a prefix. If the namespace is
   * null, it defaults to the empty string "". The prefix cannot be 
   * null. If there is no prefix, an empty string "" must be used.
   *
   * @param namespaceURI Namespace URI for the QName
   * @param localPart Local part of the QName.
   * @param prefix the xmlns-declared prefix for this namespaceURI
   * 
   * @throws IllegalArgumentException if localPart is null.
   */
  public QName(String namespaceURI, String localPart, String prefix)
  {
    this.namespaceURI = (namespaceURI == null)
                        ? emptyString
                        : namespaceURI.intern();
    if(localPart != null)
    {
        this.localPart = localPart.intern();
    }
    else
    {
        throw new IllegalArgumentException("localpart cannot be null.");
    }
    if(prefix != null)
    {
        this.prefix = prefix.intern();
    }
    else
    {
        throw new IllegalArgumentException("prefix cannot be null.");
    }
  }
  
  /**
   * Gets the Namespace URI for this QName
   *
   * @return Namespace URI
   */
  public String getNamespaceURI()
  {
    return namespaceURI;
  }

  /**
   * Gets the Local part for this QName
   *
   * @return Local part
   */
  public String getLocalPart()
  {
    return localPart;
  }
  
  /**
   * Gets the prefix for this QName
   * 
   * @return prefix of this QName
   */
  public String getPrefix()
  {
      return prefix;
  }

  /**
   * Returns a string representation of this QName
   *
   * @return a string representation of the QName
   */
  public String toString()
  {
    return ((namespaceURI == emptyString)
            ? localPart
            : '{' + namespaceURI + '}' + localPart);
  }

  /**
   * Tests this QName for equality with another object.
   * <p>
   * If the given object is not a QName or is null then this method
   * returns <tt>false</tt>.
   * <p>
   * For two QNames to be considered equal requires that both
   * localPart and namespaceURI must be equal. This method uses
   * <code>String.equals</code> to check equality of localPart
   * and namespaceURI. The prefix is NOT used to determine equality.
   * Any class that extends QName is required
   * to satisfy this equality contract.
   * <p>
   * This method satisfies the general contract of the <code>Object.equals</code> method.
   *
   * @param obj the reference object with which to compare
   *
   * @return <code>true</code> if the given object is identical to this
   *      QName: <code>false</code> otherwise.
   */
  public final boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (!(obj instanceof QName))
    {
      return false;
    }

    //We use intern strings, so can use '==' instead of .equals
    //for better performance.
    
    if ((namespaceURI == ((QName)obj).namespaceURI)
        && (localPart == ((QName)obj).localPart))
    {
      return true;
    }

    return false;
  }

  /**
   * Returns a QName holding the value of the specified String.
   * <p>
   * The string must be in the form returned by the QName.toString()
   * method, i.e. "{namespaceURI}localPart", with the "{namespaceURI}"
   * part being optional.
   * If the Namespace URI .equals(""), only the local part should be 
   * provided.
   * <p>
   * The prefix value CANNOT be represented in the String and will be 
   * set to "".
   * <p>
   * This method doesn't do a full validation of the resulting QName.
   * In particular, it doesn't check that the resulting namespace URI
   * is a legal URI (per RFC 2396 and RFC 2732), nor that the resulting
   * local part is a legal NCName per the XML Namespaces specification.
   *
   * @param s the string to be parsed
   * @throws java.lang.IllegalArgumentException If the specified String
   * cannot be parsed as a QName
   * @return QName corresponding to the given String
   */
  public static QName valueOf(String s)
  {
    if (s == null)
    {
      throw new IllegalArgumentException(
                  "Invalid QName literal - null string.");
    }

    if (s.equals(""))
    {
      throw new IllegalArgumentException(
                  "Invalid QName literal - empty string.");
    }
    
    int a = s.indexOf("{");
    int b = s.lastIndexOf("{");
    int c = s.indexOf("}");
    int d = s.lastIndexOf("}");
    
    if(   a > 0                //'{' is not the first character
       || a != b               //more than one '{' exists
       || a != -1 && c == -1   //'{' not matched by a '}'
       || a == -1 && c != -1   //'}' not matched by a '{'
       || c != d               //more than one '}' exists
       || c == s.length() - 1  //namespace only, no local part
      )
    {
        throw new IllegalArgumentException(
                "Invalid QName literal '" + s + "'.");
    }
    
    //We have confirmed that if the qname-as-string contains 
    //'{' and '}' braces then they are correctly formatted.
    
    if (s.charAt(0) == '{')
    {
        //namespace and local part
        return new QName(s.substring(1, c), s.substring(c + 1));
    }
    else
    {
        //local part only
        return new QName(s);
    }
  }

  /**
   * Returns a hash code value for this QName object. The hash code
   * is based on both the localPart and namespaceURI parts of the
   * QName. This method satisfies the  general contract of the
   * <code>Object.hashCode</code> method.
   *
   * @return a hash code value for this Qname object
   */
  public final int hashCode()
  {
    return namespaceURI.hashCode() ^ localPart.hashCode();
  }

  /**
   * Sets the object variables to internal strings matching the values
   * from the input stream. If the serialized object represents an 
   * older version of QName that did not support the 'prefix' variable,
   * then 'prefix' will be set to null by <code>defaultReadObject()</code>.
   * In this case, change the 'prefix' to the empty string "".
   * 
   * @param in
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException,
                                                       ClassNotFoundException
  {
    in.defaultReadObject();

    namespaceURI = namespaceURI.intern();
    localPart    = localPart.intern();
    if(prefix == null)
    {
        //The serialized object did not have a 'prefix'.
        //i.e. it was serialized from an old version of QName.
        prefix = emptyString;
    }
    else
    {
        prefix = prefix.intern();
    }
  }
  
}
package org.apache.woden.tool.converter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Import;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.AttributeExtensible;
import javax.wsdl.extensions.ElementExtensible;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPFault;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

//We may want to remove these 2 dependencies at some point.
import com.ibm.wsdl.util.StringUtils;
import com.ibm.wsdl.util.xml.DOMUtils;

/**
 * This is a utility to convert WSDL 1.1 documents into WSDL 2.0 documents.
 *
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 */
public class Convert
{
  private static String NS_URI_WSDL_2_0_BASE =
    "http://www.w3.org/2006/01";
  private static String NS_URI_WSDL_2_0 =
    NS_URI_WSDL_2_0_BASE + "/wsdl";
  private static String NS_URI_WSDL_2_0_SOAP =
    NS_URI_WSDL_2_0 + "/soap";
  private static String NS_URI_WSDL_2_0_SOAP_1_1_HTTP =
    NS_URI_WSDL_2_0_BASE + "/soap11/bindings/HTTP";
  private static String NS_URI_WSDL_2_0_SOAP_1_2_HTTP =
    "http://www.w3.org/2003/05/soap/bindings/HTTP";
  private static String NS_URI_WSDL_1_1 =
    "http://schemas.xmlsoap.org/wsdl/";
  private static String NS_URI_WSDL_1_1_SOAP =
    "http://schemas.xmlsoap.org/wsdl/soap/";
  private static String NS_URI_WSDL_1_1_SOAP_HTTP =
    "http://schemas.xmlsoap.org/soap/http";
  private static String NS_URI_WSP =
    "http://schemas.xmlsoap.org/ws/2004/09/policy";

  private static Map patternMappings = new HashMap();
  static
  {
    patternMappings.put(OperationType.ONE_WAY, NS_URI_WSDL_2_0 + "/in-only");
    patternMappings.put(OperationType.REQUEST_RESPONSE, NS_URI_WSDL_2_0 + "/in-out");
    patternMappings.put(OperationType.SOLICIT_RESPONSE, NS_URI_WSDL_2_0 + "/out-in");
    patternMappings.put(OperationType.NOTIFICATION, NS_URI_WSDL_2_0 + "/out-only");
    /*
      For this to happen, the 1.1 document would've had to omit both the
      <input> and <output> elements (instead of including them and referring
      to childless <message>s).
     */
    patternMappings.put(null, NS_URI_WSDL_2_0 + "/in-out");
  }

  public static String COPY = "copy";
  public static String IGNORE = "ignore";

  private static Map baseExtToBehaviorMap = new HashMap();
  static
  {
    baseExtToBehaviorMap.put(Binding.class.getName() +
                             new QName(NS_URI_WSDL_1_1_SOAP, "binding"),
                             IGNORE);
    baseExtToBehaviorMap.put(BindingOperation.class.getName() +
                             new QName(NS_URI_WSDL_1_1_SOAP, "operation"),
                             IGNORE);
    baseExtToBehaviorMap.put(Port.class.getName() +
                             new QName(NS_URI_WSDL_1_1_SOAP, "address"),
                             IGNORE);

    baseExtToBehaviorMap.put(Definition.class.getName() +
                             new QName(NS_URI_WSP, "UsingPolicy"),
                             COPY);
    baseExtToBehaviorMap.put(Definition.class.getName() +
                             new QName(NS_URI_WSP, "Policy"),
                             COPY);

    // MJD - debug
    // Need to decide what to do with extension elements found within message.
//    baseExtToBehaviorMap.put(Message.class.getName() +
//                             new QName(NS_URI_WSP, "PolicyReference"),
//                             COPY);
    // MJD - debug
    baseExtToBehaviorMap.put(Operation.class.getName() +
                             new QName(NS_URI_WSP, "PolicyReference"),
                             COPY);
    baseExtToBehaviorMap.put(Binding.class.getName() +
                             new QName(NS_URI_WSP, "PolicyReference"),
                             COPY);
    baseExtToBehaviorMap.put(BindingOperation.class.getName() +
                             new QName(NS_URI_WSP, "PolicyReference"),
                             COPY);
    // MJD - debug
    // Need to decide what to do with extension elements found within bindingInput.
//    baseExtToBehaviorMap.put(BindingInput.class.getName() +
//                             new QName(NS_URI_WSP, "PolicyReference"),
//                             COPY);
    // MJD - debug
    baseExtToBehaviorMap.put(Service.class.getName() +
                             new QName(NS_URI_WSP, "PolicyReference"),
                             COPY);
    baseExtToBehaviorMap.put(Port.class.getName() +
                             new QName(NS_URI_WSP, "PolicyReference"),
                             COPY);

    // MJD - debug
    /*
      Need to decide how to handle extensibility attributes encountered within
      faults and parts.
     */
    // MJD - debug
  }

  private Map extToBehaviorMap = baseExtToBehaviorMap;

  public Map getExtToBehaviorMap()
  {
    return extToBehaviorMap;
  }

  public void setExtToBehaviorMap(Map extToBehaviorMap)
  {
    this.extToBehaviorMap = extToBehaviorMap;
  }

  public String convertDefinition(String newTargetNS,
                                  Definition def,
                                  String targetDir,
                                  boolean verbose,
                                  boolean overwrite) throws WSDLException,
                                                            IOException
  {
    return convertDefinition(newTargetNS,
                             def,
                             new HashMap(),
                             new HashMap(),
                             new HashMap(),
                             targetDir,
                             verbose,
                             overwrite);
  }

  private String convertDefinition(String newTargetNS,
                                   Definition def,
                                   Map opToQNameMap,
                                   Map faultToQNameMap,
                                   Map defToFileNameMap,
                                   String targetDir,
                                   boolean verbose,
                                   boolean overwrite) throws WSDLException,
                                                             IOException
  {
    String documentBaseURI = def.getDocumentBaseURI();
    String targetFileName = new File(documentBaseURI).getName() + "2";
    OutputStream out = Utils.getOutputStream(targetDir,
                                             targetFileName,
                                             overwrite,
                                             verbose);
    PrintWriter pw = new PrintWriter(out);
    ExtensionRegistry extReg = def.getExtensionRegistry();

    pw.print("<description");

    String origTargetNamespace = def.getTargetNamespace();
    String targetNamespace = convertNamespaceDecls(newTargetNS,
                                                   def.getNamespaces(),
                                                   pw,
                                                   def);

    convertExtensions(Definition.class,
                      def,
                      pw,
                      def);

    defToFileNameMap.put(def, targetFileName);

    convertImports(newTargetNS,
                   def.getImports(),
                   pw,
                   def,
                   origTargetNamespace,
                   targetNamespace,
                   opToQNameMap,
                   faultToQNameMap,
                   defToFileNameMap,
                   targetDir,
                   verbose,
                   overwrite);
    convertTypes(def.getTypes(), pw, def, extReg);
    convertPortTypes(def.getPortTypes(),
                     pw,
                     def,
                     targetNamespace,
                     opToQNameMap,
                     faultToQNameMap);
    convertBindings(def.getBindings(),
                    pw,
                    def,
                    origTargetNamespace,
                    targetNamespace,
                    opToQNameMap,
                    faultToQNameMap);
    convertServices(def.getServices(),
                    pw,
                    def,
                    origTargetNamespace,
                    targetNamespace);

    pw.println("</description>");
    pw.flush();
    pw.close();

    return targetFileName;
  }

  public String convertFile(String newTargetNS,
                            String fileName,
                            String targetDir,
                            boolean verbose,
                            boolean overwrite) throws WSDLException,
                                                      IOException
  {
    WSDLFactory factory = WSDLFactory.newInstance();
    WSDLReader reader = factory.newWSDLReader();

    reader.setFeature("javax.wsdl.verbose", verbose);

    Definition def = reader.readWSDL(fileName);

    return convertDefinition(newTargetNS, def, targetDir, verbose, overwrite);
  }

  private String convertNamespaceDecls(String newTargetNS,
                                       Map namespaces,
                                       PrintWriter pw,
                                       Definition def)
  {
    String targetNamespace = newTargetNS != null && !newTargetNS.equals("")
                             ? newTargetNS
                             : def.getTargetNamespace();

    if (targetNamespace == null || targetNamespace.equals(""))
    {
      throw new IllegalArgumentException("If the source WSDL v1.1 document " +
                                         "does not specify a " +
                                         "targetNamespace, you must use the " +
                                         "-targetNS command-line argument to " +
                                         "specify one.");
    }

    pw.println(" xmlns=\"" + NS_URI_WSDL_2_0 + "\"");
    pw.print("             targetNamespace=\"" + targetNamespace + "\"");

    // MJD - debug
    // These namespace declarations should be reset to their initial value.
    def.addNamespace("tns", targetNamespace);
    def.addNamespace("wsoap", NS_URI_WSDL_2_0_SOAP);
    // MJD - debug

    if (namespaces != null)
    {
      Iterator prefixes = namespaces.keySet().iterator();

      while (prefixes.hasNext())
      {
        String prefix = (String)prefixes.next();

        if (!prefix.equals("") && !prefix.equals("soap"))
        {
          String namespaceURI = def.getNamespace(prefix);

          if (!namespaceURI.equals(NS_URI_WSDL_1_1_SOAP)
              && !namespaceURI.equals(NS_URI_WSDL_1_1))
          {
            pw.print("\n             xmlns:" + prefix + "=\"" + namespaceURI +
                     "\"");
          }
        }
      }
    }

    // MJD - debug
//    pw.print("\n             " +
//             "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
//             "\n             xsi:schemaLocation=\n" +
//             "               \"" + NS_URI_WSDL_2_0 + "\n" +
//             "                http://www.w3.org/2006/01/wsdl/wsdl20.xsd\"");
    // MJD - debug

    pw.println(">");

    return targetNamespace;
  }

  private void convertExtensionAttrs(AttributeExtensible attrExtensible,
                                     PrintWriter pw,
                                     Definition def)
                                       throws WSDLException
  {
    Map extensionAttributes = attrExtensible.getExtensionAttributes();
    Iterator attrNames = extensionAttributes.keySet().iterator();

    while (attrNames.hasNext())
    {
      QName attrName = (QName)attrNames.next();
      Object attrValue = extensionAttributes.get(attrName);
      String attrStrValue = null;
      QName attrQNameValue = null;

      if (attrValue instanceof String)
      {
        attrStrValue = (String)attrValue;
      }
      else if (attrValue instanceof QName)
      {
        attrQNameValue = (QName)attrValue;
      }
      else if (attrValue instanceof List)
      {
        List attrValueList = (List)attrValue;
        int size = attrValueList.size();

        if (size > 0)
        {
          Object tempAttrVal = attrValueList.get(0);

          if (tempAttrVal instanceof String)
          {
            attrStrValue = StringUtils.getNMTokens(attrValueList);
          }
          else if (tempAttrVal instanceof QName)
          {
            StringBuffer strBuf = new StringBuffer();

            for (int i = 0; i < size; i++)
            {
              QName tempQName = (QName)attrValueList.get(i);

              strBuf.append((i > 0 ? " " : "") +
                            DOMUtils.getQualifiedValue(tempQName.getNamespaceURI(),
                                                       tempQName.getLocalPart(),
                                                       def));
            }

            attrStrValue = strBuf.toString();
          }
          else
          {
            throw new WSDLException(WSDLException.CONFIGURATION_ERROR,
                                    "Unknown type of extension attribute '" +
                                    attrName + "': " +
                                    tempAttrVal.getClass().getName());
          }
        }
        else
        {
          attrStrValue = "";
        }
      }
      else
      {
        throw new WSDLException(WSDLException.CONFIGURATION_ERROR,
                                "Unknown type of extension attribute '" +
                                attrName + "': " +
                                attrValue.getClass().getName());
      }

      if (attrQNameValue != null)
      {
        DOMUtils.printQualifiedAttribute(attrName, attrQNameValue, def, pw);
      }
      else
      {
        DOMUtils.printQualifiedAttribute(attrName, attrStrValue, def, pw);
      }
    }
  }

  private void convertExtensions(Class parentClass,
                                 ElementExtensible elExtensible,
                                 PrintWriter pw,
                                 Definition def)
                                   throws WSDLException
  {
    List extElements = elExtensible.getExtensibilityElements();

    if (extElements != null)
    {
      ExtensionRegistry extReg = def.getExtensionRegistry();
      Iterator extIterator = extElements.iterator();
      String parentClassName = parentClass.getName();

      while (extIterator.hasNext())
      {
        ExtensibilityElement extEl = (ExtensibilityElement)extIterator.next();
        QName elementType = extEl.getElementType();
        String behavior = (String)extToBehaviorMap.get(parentClassName +
                                                       elementType);

        if (behavior == null)
        {
          throw new IllegalArgumentException("Encountered unknown " +
                                             "extension element '" +
                                             elementType + "', as a child of " +
                                             "a " + parentClassName + ".");
        }
        else if (behavior.equals(COPY))
        {
          ExtensionSerializer extSer = extReg.querySerializer(parentClass,
                                                              elementType);

          extSer.marshall(parentClass,
                          elementType,
                          extEl,
                          pw,
                          def,
                          extReg);
        }
        else if (behavior.equals(IGNORE))
        {
        }
        else
        {
          throw new IllegalArgumentException("Encountered unknown " +
                                             "behavior '" + behavior +
                                             "' registered for extension " +
                                             "element '" + elementType +
                                             "', as a child of a " +
                                             parentClassName + ".");
        }
      }
    }
  }

  private void convertImports(String newTargetNS,
                              Map importMap,
                              PrintWriter pw,
                              Definition def,
                              String origTargetNamespace,
                              String targetNamespace,
                              Map opToQNameMap,
                              Map faultToQNameMap,
                              Map defToFileNameMap,
                              String targetDir,
                              boolean verbose,
                              boolean overwrite) throws WSDLException,
                                                        IOException
  {
    Iterator importListIterator = importMap.values().iterator();

    while (importListIterator.hasNext())
    {
      List importList = (List)importListIterator.next();
      Iterator importIterator = importList.iterator();

      while (importIterator.hasNext())
      {
        Import _import = (Import)importIterator.next();
        Definition importedDef = _import.getDefinition();
        //String importedNamespace = _import.getNamespaceURI();
        String importedDefTNS = null;

        if (importedDef != null)
        {
          importedDefTNS = importedDef.getTargetNamespace();

          /*
            Since an imported definition was actually resolved, we have to make
            sure that a targetNamespace was specified, and that it matches the
            value of the namespace attribute specified on the <import> element.
           */
          if (importedDefTNS == null || importedDefTNS.equals(""))
          {
            throw new IllegalArgumentException("You cannot <import> WSDL v1.1 " +
                                               "documents that do not " +
                                               "specify the targetNamespace " +
                                               "attribute on the definition " +
                                               "element.");
          }
          else if (!importedDefTNS.equals(_import.getNamespaceURI()))
          {
            throw new IllegalArgumentException("The value of the namespace " +
                                               "attribute on the <import> " +
                                               "element must match the " +
                                               "value of the targetNamespace " +
                                               "attribute specified on the " +
                                               "document being imported.");
          }
        }
        else
        {
          /*
            Since no definition was resolved, use the value from the namespace
            attribute on the <import> element.
           */
          importedDefTNS = _import.getNamespaceURI();

          if (importedDefTNS == null || importedDefTNS.equals(""))
          {
            throw new IllegalArgumentException("You cannot <import> WSDL v1.1 " +
                                               "documents wihout either " +
                                               "specifying the targetNamespace " +
                                               "attribute on the imported " +
                                               "definition element or " +
                                               "specifying the namespace " +
                                               "attribute on the <import> " +
                                               "element.");
          }
        }

        // Determine whether the -targetNS value should be propagated.
        if (newTargetNS != null && !newTargetNS.equals(""))
        {
          if (importedDefTNS.equals(origTargetNamespace))
          {
            importedDefTNS = newTargetNS;
          }
          else if (!importedDefTNS.equals(newTargetNS))
          {
            newTargetNS = null;
          }
        }

        String targetFileName = null;
        String locationURI = _import.getLocationURI();

        if (locationURI != null
            && !locationURI.equals("")
            && importedDef != null)
        {
          targetFileName = (String)defToFileNameMap.get(importedDef);

          if (targetFileName == null)
          {
            targetFileName = convertDefinition(newTargetNS,
                                               importedDef,
                                               opToQNameMap,
                                               faultToQNameMap,
                                               defToFileNameMap,
                                               targetDir,
                                               verbose,
                                               overwrite);
          }
        }

        if (importedDefTNS.equals(targetNamespace))
        {
          if (targetFileName != null)
          {
            pw.print("  <include location=\"" + targetFileName + "\"");

            // Attributes are just copied over to the new element.
            convertExtensionAttrs(_import,
                                  pw,
                                  def);

            pw.println("/>");
          }
        }
        else
        {
          pw.print("  <import namespace=\"" + importedDefTNS + "\"");

          if (targetFileName != null)
          {
            pw.print(" location=\"" + targetFileName + "\"");
          }

          // Attributes are just copied over to the new element.
          convertExtensionAttrs(_import,
                                pw,
                                def);

          pw.println("/>");
        }
      }
    }
  }

  private void convertTypes(Types types,
                            PrintWriter pw,
                            Definition def,
                            ExtensionRegistry extReg) throws WSDLException
  {
    if (types == null)
    {
      return;
    }

    List extElList = types.getExtensibilityElements();

    if (extElList != null && extElList.size() > 0)
    {
      pw.println("  <types>");

      Iterator extElIterator = extElList.iterator();

      while (extElIterator.hasNext())
      {
        ExtensibilityElement extEl = (ExtensibilityElement)extElIterator.next();
        QName elementType = extEl.getElementType();
        ExtensionSerializer extSer = extReg.querySerializer(Types.class,
                                                            extEl.getElementType());

        extSer.marshall(Types.class, elementType, extEl, pw, def, extReg);
      }

      pw.println("  </types>");
    }
  }

  private void convertPortTypes(Map portTypeMap,
                                PrintWriter pw,
                                Definition def,
                                String targetNamespace,
                                Map opToQNameMap,
                                Map faultToQNameMap)
                                  throws WSDLException
  {
    if (portTypeMap == null)
    {
      return;
    }

    Iterator portTypes = portTypeMap.values().iterator();

    while (portTypes.hasNext())
    {
      PortType portType = (PortType)portTypes.next();

      if (portType.isUndefined())
      {
        continue;
      }

      QName qname = portType.getQName();

      pw.print("  <interface name=\"" + qname.getLocalPart() + "\"");

      // Attributes are just copied over to the new element.
      convertExtensionAttrs(portType,
                            pw,
                            def);

      pw.println(">");

      StringWriter operationsSW = new StringWriter();
      PrintWriter operationsPW = new PrintWriter(operationsSW);
      StringWriter faultsSW = new StringWriter();
      PrintWriter faultsPW = new PrintWriter(faultsSW);

      convertOperations(portType.getOperations(),
                        faultsPW,
                        operationsPW,
                        def,
                        targetNamespace,
                        opToQNameMap,
                        faultToQNameMap);

      faultsPW.flush();
      pw.print(faultsSW.toString());
      operationsPW.flush();
      pw.print(operationsSW.toString());

      pw.println("  </interface>");
    }
  }

  private void convertOperations(List operations,
                                 PrintWriter faultsPW,
                                 PrintWriter operationsPW,
                                 Definition def,
                                 String targetNamespace,
                                 Map opToQNameMap,
                                 Map faultToQNameMap)
                                   throws WSDLException
  {
    if (operations != null && operations.size() > 0)
    {
      Set opNameSet = new HashSet();
      Map newFaultMap = new HashMap();
      Iterator opIterator = operations.iterator();

      while (opIterator.hasNext())
      {
        Operation op = (Operation)opIterator.next();
        String origOpName = op.getName();
        String opName = origOpName;
        int index = 1;

        while (opNameSet.contains(opName))
        {
          opName = origOpName + "_" + index++;
        }

        opNameSet.add(opName);
        opToQNameMap.put(op, new QName(targetNamespace, opName));

        OperationType opType = op.getStyle();

        operationsPW.println("    <operation name=\"" + opName + "\"");
        operationsPW.print("               pattern=\"" +
                           patternMappings.get(opType) + "\"");
        operationsPW.println(">");

        convertExtensions(Operation.class,
                          op,
                          operationsPW,
                          def);

        // MJD - debug
        // Need to make sure these are called in the proper order.
        convertInput(op.getInput(), operationsPW, def);
        convertOutput(op.getOutput(), operationsPW, def);
        convertFaults(op.getFaults(),
                      faultsPW,
                      operationsPW,
                      def,
                      targetNamespace,
                      newFaultMap,
                      faultToQNameMap);
        // MJD - debug

        operationsPW.println("    </operation>");
      }
    }
  }

  private void convertFaults(Map faultMap,
                             PrintWriter faultsPW,
                             PrintWriter operationsPW,
                             Definition def,
                             String targetNamespace,
                             Map newFaultMap,
                             Map faultToQNameMap)
                               throws WSDLException
  {
    if (faultMap == null)
    {
      return;
    }

    Iterator faults = faultMap.values().iterator();

    while (faults.hasNext())
    {
      Fault fault = (Fault)faults.next();
      String faultName = fault.getName();

      if (faultName == null || faultName.equals(""))
      {
        throw new IllegalArgumentException("WSDL v1.1 requires a name to be " +
                                           "specified for every fault element.");
      }

      Message faultMessage = fault.getMessage();

      if (faultMessage == null || faultMessage.isUndefined())
      {
        throw new IllegalArgumentException("WSDL v1.1 requires a (defined) " +
                                           "message to be specified for " +
                                           "every fault element.");
      }

      List extEls = faultMessage.getExtensibilityElements();

      if (extEls != null && extEls.size() > 0)
      {
        throw new IllegalArgumentException("Encountered extensibility " +
                                           "elements within a message " +
                                           "and cannot determine what to " +
                                           "do with them: " + faultMessage);
      }

      List faultParts = faultMessage.getOrderedParts(null);
      int size = faultParts.size();

      if (size == 1)
      {
        Part part = (Part)faultParts.get(0);
        QName elementName = part.getElementName();

        if (elementName == null)
        {
          throw new IllegalArgumentException("The conversion utility " +
                                             "currently supports only " +
                                             "doc/lit style.");
        }

        QName retrievedElName = (QName)newFaultMap.get(faultName);
        String origFaultName = faultName;
        int index = 1;

        while (retrievedElName != null
               && !retrievedElName.equals(elementName))
        {
          faultName = origFaultName + "_" + index++;
          retrievedElName = (QName)newFaultMap.get(faultName);
        }

        if (retrievedElName == null)
        {
          faultsPW.print("    <fault name=\"" + faultName + "\"");

          DOMUtils.printQualifiedAttribute("element",
                                           elementName,
                                           def,
                                           faultsPW);

          faultsPW.println("/>");

          newFaultMap.put(faultName, elementName);
        }

        QName refQName = new QName(targetNamespace, faultName);

        operationsPW.print("      <outfault");

        DOMUtils.printQualifiedAttribute("ref",
                                         refQName,
                                         def,
                                         operationsPW);

        operationsPW.println(" messageLabel=\"Out\"/>");

        faultToQNameMap.put(fault, refQName);
      }
      else
      {
        throw new IllegalArgumentException("Encountered a fault " +
                                           "message with a number of " +
                                           "parts other than 1: " +
                                           faultParts);
      }
    }
  }

  private void convertInput(Input input,
                            PrintWriter pw,
                            Definition def) throws WSDLException
  {
    if (input != null)
    {
      Message inputMsg = input.getMessage();

      if (inputMsg != null)
      {
        List extEls = inputMsg.getExtensibilityElements();

        if (extEls != null && extEls.size() > 0)
        {
          throw new IllegalArgumentException("Encountered extensibility " +
                                             "elements within a message " +
                                             "and cannot determine what to " +
                                             "do with them: " + inputMsg);
        }

        List orderedInputParts = inputMsg.getOrderedParts(null);
        int size = orderedInputParts.size();

        if (size == 1)
        {
          Part part = (Part)orderedInputParts.get(0);
          QName elementName = part.getElementName();
//          String messageLabel = part.getName();
          String messageLabel = "In";

          pw.print("      <input messageLabel=\"" + messageLabel + "\"");

          DOMUtils.printQualifiedAttribute("element",
                                           elementName,
                                           def,
                                           pw);

          // Attributes are just copied over to the new element.
          convertExtensionAttrs(input,
                                pw,
                                def);

          pw.println("/>");
        }
        else if (size == 0)
        {
          pw.print("      <input messageLabel=\"In\" element=\"#none\"");

          // Attributes are just copied over to the new element.
          convertExtensionAttrs(input,
                                pw,
                                def);

          pw.println("/>");
        }
        else
        {
          throw new IllegalArgumentException("Encountered an input " +
                                             "message with a number of " +
                                             "parts other than 0 or 1: " +
                                             orderedInputParts);
        }
      }
    }
  }

  private void convertOutput(Output output,
                             PrintWriter pw,
                             Definition def) throws WSDLException
  {
    if (output != null)
    {
      Message outputMsg = output.getMessage();

      if (outputMsg != null)
      {
        List extEls = outputMsg.getExtensibilityElements();

        if (extEls != null && extEls.size() > 0)
        {
          throw new IllegalArgumentException("Encountered extensibility " +
                                             "elements within a message " +
                                             "and cannot determine what to " +
                                             "do with them: " + outputMsg);
        }

        List orderedOutputParts = outputMsg.getOrderedParts(null);
        int size = orderedOutputParts.size();

        if (size == 1)
        {
          Part part = (Part)orderedOutputParts.get(0);
          QName elementName = part.getElementName();
//          String messageLabel = part.getName();
          String messageLabel = "Out";

          pw.print("      <output messageLabel=\"" + messageLabel + "\"");

          DOMUtils.printQualifiedAttribute("element",
                                           elementName,
                                           def,
                                           pw);

          // Attributes are just copied over to the new element.
          convertExtensionAttrs(output,
                                pw,
                                def);

          pw.println("/>");
        }
        else if (size == 0)
        {
          pw.print("      <output messageLabel=\"Out\" element=\"#none\"");

          // Attributes are just copied over to the new element.
          convertExtensionAttrs(output,
                                pw,
                                def);

          pw.println("/>");
        }
        else
        {
          throw new IllegalArgumentException("Encountered an output " +
                                             "message with a number of " +
                                             "parts other than 0 or 1: " +
                                             orderedOutputParts);
        }
      }
    }
  }

  private void convertBindings(Map bindingMap,
                               PrintWriter pw,
                               Definition def,
                               String origTargetNamespace,
                               String targetNamespace,
                               Map opToQNameMap,
                               Map faultToQNameMap)
                                 throws WSDLException
  {
    if (bindingMap == null)
    {
      return;
    }

    Iterator bindings = bindingMap.values().iterator();

    while (bindings.hasNext())
    {
      Binding binding = (Binding)bindings.next();

      if (binding.isUndefined())
      {
        continue;
      }

      QName qname = binding.getQName();
      PortType portType = binding.getPortType();

      // Only convert the binding if the original portType can be resolved.
      if (portType.isUndefined())
      {
        continue;
      }

      QName portTypeQName = null;
      Set faultSet = new HashSet();
      String type = null;
      // Need to support both 1.1 and 1.2.
      String soapVersion = "1.1";
      String soapProtocol = null;
      List extEls = binding.getExtensibilityElements();

      if (extEls.size() > 0)
      {
        ExtensibilityElement extEl = (ExtensibilityElement)extEls.get(0);

        if (extEl instanceof SOAPBinding)
        {
          SOAPBinding soapBinding = (SOAPBinding)extEl;

          type = NS_URI_WSDL_2_0_SOAP;

          String transportURI = soapBinding.getTransportURI();

          if (NS_URI_WSDL_1_1_SOAP_HTTP.equals(transportURI))
          {
            soapProtocol = NS_URI_WSDL_2_0_SOAP_1_1_HTTP;
          }
        }
      }

      pw.print("  <binding name=\"" + qname.getLocalPart() + "\"");

      if (portType != null)
      {
        portTypeQName = portType.getQName();

        if (portTypeQName != null)
        {
          String portTypeNS = portTypeQName.getNamespaceURI();

          if (!portTypeNS.equals(targetNamespace)
              && portTypeNS.equals(origTargetNamespace))
          {
            portTypeQName = new QName(targetNamespace,
                                      portTypeQName.getLocalPart());
          }

          pw.print("\n          ");

          DOMUtils.printQualifiedAttribute("interface",
                                           portTypeQName,
                                           def,
                                           pw);
        }
      }

      if (type != null)
      {
        pw.print("\n           type=\"" + type + "\"");
      }

      if (soapVersion != null)
      {
        pw.print("\n          ");

        DOMUtils.printQualifiedAttribute(new QName(NS_URI_WSDL_2_0_SOAP,
                                                   "version"),
                                         soapVersion,
                                         def,
                                         pw);
      }

      if (soapProtocol != null)
      {
        pw.print("\n          ");

        DOMUtils.printQualifiedAttribute(new QName(NS_URI_WSDL_2_0_SOAP,
                                                   "protocol"),
                                         soapProtocol,
                                         def,
                                         pw);
      }

      pw.println(">");

      convertExtensions(Binding.class,
                        binding,
                        pw,
                        def);

      List operations = binding.getBindingOperations();
      StringWriter operationsSW = new StringWriter();
      PrintWriter operationsPW = new PrintWriter(operationsSW);
      Iterator opIterator = operations.iterator();

      while (opIterator.hasNext())
      {
        BindingOperation bindingOp = (BindingOperation)opIterator.next();
        Operation op = bindingOp.getOperation();
        Map bindingFaultMap = bindingOp.getBindingFaults();

        if (bindingFaultMap != null)
        {
          Iterator bindingFaults = bindingFaultMap.values().iterator();

          while (bindingFaults.hasNext())
          {
            BindingFault bindingFault = (BindingFault)bindingFaults.next();
            String faultName = bindingFault.getName();

            if (faultName == null)
            {
              List extElList = bindingFault.getExtensibilityElements();

              if (extElList != null)
              {
                Iterator extIterator = extElList.iterator();

                while (extIterator.hasNext())
                {
                  ExtensibilityElement extEl =
                    (ExtensibilityElement)extIterator.next();

                  if (extEl instanceof SOAPFault)
                  {
                    SOAPFault soapFault = (SOAPFault)extEl;

                    faultName = soapFault.getName();
                  }
                }
              }
            }

            if (faultName == null)
            {
              throw new IllegalArgumentException("WSDL v1.1 requires a name " +
                                                 "to be specified for " +
                                                 "every wsdl:fault and " +
                                                 "soap:fault element.");
            }

            Fault fault = op.getFault(faultName);

            if (fault == null)
            {
              throw new IllegalArgumentException("Unable to resolve " +
                                                 "referenced fault '" +
                                                 faultName + "'.");
            }

            QName faultRefType = (QName)faultToQNameMap.get(fault);

            if (faultRefType == null)
            {
              throw new IllegalArgumentException("Unable to resolve " +
                                                 "referenced fault '" +
                                                 faultName + "'.");
            }
            else
            {
              faultSet.add(faultRefType);
            }
          }
        }

        QName refType = (QName)opToQNameMap.get(op);

        if (refType == null)
        {
          throw new IllegalArgumentException("Unable to resolve referenced " +
                                             "operation '" + op.getName() +
                                             "'.");
        }

        String soapActionURI = null;
        List extElList = bindingOp.getExtensibilityElements();

        if (extElList != null)
        {
          Iterator extIterator = extElList.iterator();

          while (extIterator.hasNext())
          {
            ExtensibilityElement extEl =
              (ExtensibilityElement)extIterator.next();

            if (extEl instanceof SOAPOperation)
            {
              SOAPOperation soapOp = (SOAPOperation)extEl;

              soapActionURI = soapOp.getSoapActionURI();
            }
          }
        }

        operationsPW.print("    <operation");

        DOMUtils.printQualifiedAttribute("ref",
                                         refType,
                                         def,
                                         operationsPW);

        if (soapActionURI != null)
        {
          DOMUtils.printQualifiedAttribute(new QName(NS_URI_WSDL_2_0_SOAP,
                                                     "action"),
                                           soapActionURI,
                                           def,
                                           operationsPW);
        }

        operationsPW.println(">");

        convertExtensions(BindingOperation.class,
                          bindingOp,
                          operationsPW,
                          def);

        operationsPW.println("    </operation>");
      }

      Iterator faultIterator = faultSet.iterator();

      while (faultIterator.hasNext())
      {
        QName faultRefType = (QName)faultIterator.next();

        pw.print("    <fault");

        DOMUtils.printQualifiedAttribute("ref",
                                         faultRefType,
                                         def,
                                         pw);

        pw.println("/>");
      }

      operationsPW.flush();
      pw.print(operationsSW.toString());
      pw.println("  </binding>");
    }
  }

  private void convertServices(Map serviceMap,
                               PrintWriter pw,
                               Definition def,
                               String origTargetNamespace,
                               String targetNamespace)
                                 throws WSDLException
  {
    if (serviceMap == null)
    {
      return;
    }

    Set serviceNameSet = new HashSet();
    Iterator services = serviceMap.values().iterator();

    while (services.hasNext())
    {
      Service service = (Service)services.next();
      QName serviceQName = service.getQName();
      String origServiceName = null;

      if (serviceQName != null)
      {
        origServiceName = serviceQName.getLocalPart();
      }

      if (origServiceName == null || origServiceName.equals(""))
      {
        throw new IllegalArgumentException("WSDL v1.1 requires a name to be " +
                                           "specified for every service " +
                                           "element.");
      }

      Map portMap = service.getPorts();

      if (portMap == null || portMap.size() == 0)
      {
        // If there are no 1.1 <port>s, we can't define any 2.0 <endpoint>s.
        return;
      }

      Map portTypeToPortsMap = new HashMap();
      Iterator ports = portMap.values().iterator();

      while (ports.hasNext())
      {
        Port port = (Port)ports.next();
        PortType servicePortType = getServicePortType(port);

        // Only convert the port if the original portType can be resolved.
        if (!servicePortType.isUndefined())
        {
          List portList = (List)portTypeToPortsMap.get(servicePortType);

          if (portList == null)
          {
            portList = new Vector();
            portTypeToPortsMap.put(servicePortType, portList);
          }

          portList.add(port);
        }
      }

      String serviceName = origServiceName;
      Iterator uniquePortTypes = portTypeToPortsMap.keySet().iterator();
      int index = 0;

      while (uniquePortTypes.hasNext())
      {
        if (index > 0 || serviceNameSet.contains(serviceName))
        {
          serviceName = origServiceName + "_" + index++;

          while (serviceNameSet.contains(serviceName))
          {
            serviceName = origServiceName + "_" + index++;
          }
        }

        serviceNameSet.add(serviceName);

        PortType servicePortType = (PortType)uniquePortTypes.next();
        QName serviceInterfaceType = getServiceInterfaceType(origServiceName,
                                                             servicePortType,
                                                             origTargetNamespace,
                                                             targetNamespace);
        List portList = (List)portTypeToPortsMap.get(servicePortType);

        pw.println("  <service name=\"" + serviceName + "\"");
        pw.print("          ");

        DOMUtils.printQualifiedAttribute("interface",
                                         serviceInterfaceType,
                                         def,
                                         pw);

        pw.println(">");

        convertExtensions(Service.class,
                          service,
                          pw,
                          def);

        convertPorts(portList, pw, def, origTargetNamespace, targetNamespace);

        pw.println("  </service>");

        if (index == 0)
        {
          index++;
        }
      }
    }
  }

  private PortType getServicePortType(Port port)
  {
    Binding binding = port.getBinding();
    PortType servicePortType = null;

    if (binding != null)
    {
      servicePortType = binding.getPortType();
    }

    return servicePortType;
  }

  private QName getServiceInterfaceType(String origServiceName,
                                        PortType servicePortType,
                                        String origTargetNamespace,
                                        String targetNamespace)
  {
    QName serviceInterfaceType = null;

    if (servicePortType != null)
    {
      serviceInterfaceType = servicePortType.getQName();
    }

    if (serviceInterfaceType == null)
    {
      /*
        If we can't determine the type of the 1.1 <portType>, we can't define
        the 2.0 <service>.
       */
      throw new IllegalArgumentException("Unable to determine the portType " +
                                         "referenced by service '" +
                                         origServiceName + "'.");
    }
    else
    {
      String serviceInterfaceNS = serviceInterfaceType.getNamespaceURI();

      if (!serviceInterfaceNS.equals(targetNamespace)
          && serviceInterfaceNS.equals(origTargetNamespace))
      {
        serviceInterfaceType = new QName(targetNamespace,
                                         serviceInterfaceType.getLocalPart());
      }
    }

    return serviceInterfaceType;
  }

  private void convertPorts(List portList,
                            PrintWriter pw,
                            Definition def,
                            String origTargetNamespace,
                            String targetNamespace)
                              throws WSDLException
  {
    Iterator ports = portList.iterator();

    while (ports.hasNext())
    {
      Port port = (Port)ports.next();
      String portName = port.getName();
      Binding binding = port.getBinding();
      QName bindingQName = null;

      if (binding != null)
      {
        bindingQName = binding.getQName();
      }

      if (bindingQName == null)
      {
        /*
          If we can't determine the type of the 1.1 <binding>, we can't define
          the 2.0 <endpoint>.
         */
        throw new IllegalArgumentException("Unable to determine the type of " +
                                           "the binding referenced by port '" +
                                           port.getName() + "'.");
      }
      else
      {
        String bindingNS = bindingQName.getNamespaceURI();

        if (!bindingNS.equals(targetNamespace)
            && bindingNS.equals(origTargetNamespace))
        {
          bindingQName = new QName(targetNamespace,
                                   bindingQName.getLocalPart());
        }
      }

      String address = null;
      List extEls = port.getExtensibilityElements();

      if (extEls.size() > 0)
      {
        ExtensibilityElement extEl = (ExtensibilityElement)extEls.get(0);

        if (extEl instanceof SOAPAddress)
        {
          SOAPAddress soapAddress = (SOAPAddress)extEl;

          address = soapAddress.getLocationURI();
        }
      }

      pw.println("    <endpoint name=\"" + portName + "\"");
      pw.print("             ");

      DOMUtils.printQualifiedAttribute("binding",
                                       bindingQName,
                                       def,
                                       pw);

      if (address != null)
      {
        pw.print("\n              address=\"" + address + "\"");
      }

      pw.println(">");

      convertExtensions(Port.class,
                        port,
                        pw,
                        def);

      pw.println("    </endpoint>");
    }
  }

  private static void printUsage(String errorMessage)
  {
    System.err.println("Error: " + errorMessage + "\n\n" +
                       "Usage:\n\n" +
                       "  java " + Convert.class.getName() +
                       " [args]\n\n" +
                       "    args:\n\n" +
                       "      -wsdl         file-or-URL-of-wsdl1.1-document\n" +
                       "      [-targetNS    new-target-namespace]\n" +
                       "      [-dir        targetDir]   default: .\n" +
                       "      [-verbose     (on|off)]   default: on\n" +
                       "      [-overwrite   (on|off)]   default: off" +
                       "   (Overwrite existing files?)");

    System.exit(1);
  }

  /**
   * The main entry-point for the conversion utility. The output is sent to
   * standard out.
   *<code>
   *<pre>Usage:</pre>
   *<p>
   *<pre>  java org.apache.woden.tool.converter.Convert -wsdl wsdl1.1FileName</pre>
   */
  public static void main(String[] argv) throws Exception
  {
    long startTime = System.currentTimeMillis();

    if (argv.length % 2 != 0)
    {
      printUsage("Incorrect number of arguments.");
    }

    String wsdlDoc = null;
    String newTargetNS = null;
    String targetDir = ".";
    boolean verbose = true;
    boolean overwrite = false;

    for (int i = 0; i < argv.length; i += 2)
    {
      String option = argv[i];
      String value = argv[i + 1];

      if (option.equals("-wsdl"))
      {
        wsdlDoc = value;
      }
      else if (option.equals("-targetNS"))
      {
        newTargetNS = value;
      }
      else if (option.equals("-dir"))
      {
        targetDir = value;
      }
      else if (option.equals("-verbose"))
      {
        verbose = value.equals("on");
      }
      else if (option.equals("-overwrite"))
      {
        overwrite = value.equals("on");
      }
      else
      {
        printUsage("Unrecognized argument '" + option + "'.");
      }
    }

    if (wsdlDoc != null)
    {
      Convert convert = new Convert();

      convert.convertFile(newTargetNS,
                          wsdlDoc,
                          targetDir,
                          verbose,
                          overwrite);
    }
    else
    {
      printUsage("No WSDL 1.1 document was specified (use the '-wsdl' " +
                 "argument.)");
    }

    long endTime = System.currentTimeMillis();

    if (verbose)
    {
      System.out.println("Done.\n" +
                         "Elapsed time: " + (endTime - startTime) + "ms");
    }
  }
}


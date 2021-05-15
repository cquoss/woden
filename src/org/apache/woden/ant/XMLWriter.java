package org.apache.woden.ant;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Stack;

import org.apache.woden.types.NCName;

/**
 * The <code>XMLWriter</code> class writes XML files.
 * 
 * @author Arthur Ryman (ryman@ca.ibm.com, arthur.ryman@gmail.com)
 *
 */
public class XMLWriter {
    
    private HashMap registry = new HashMap();

    private PrintWriter out;

    private int indentation = 2;

    private Stack tags = new Stack();

    /**
     * Constructs an XML writer.
     * 
     * @param out the output writer
     */
    public XMLWriter(PrintWriter out) {
        this.out = out;
    }

    /**
     * Closes the output writer.
     */
    public void close() {

        if (out != null) {

            out.close();
        }
    }

    /**
     * Writes the XML declaration.
     * 
     * @param encoding the character encoding or null
     */
    public void xmlDeclaration(String encoding) {

        if (out == null)
            return;

        if (encoding == null) {
            out.println("<?xml version='1.0'?>");
        } else {
            out.println("<?xml version='1.0' encoding='" + encoding + "'?>");
        }
    }

    /**
     * Writes the begin tag for an element.
     * 
     * @param tag the tag
     */
    public void beginElement(String tag) {

        if (out == null)
            return;

        beginElement(tag, "");
    }

    /**
     * Writes the begin tag for an element with attributes.
     * 
     * @param tag the tag
     * @param attributes the attributes
     */
    public void beginElement(String tag, String attributes) {

        if (out == null)
            return;

        indent();
        if (attributes.length() == 0) {
            out.println("<" + tag + ">");

        } else {
            out.println("<" + tag + " " + attributes + ">");
        }
        tags.push(tag);
    }

    /**
     * Writes the end tag for the current element.
     */
    public void endElement() {

        if (out == null)
            return;

        String tag = (String) tags.pop();
        indent();
        out.println("</" + tag + ">");
    }

    /**
     * Write the end tag for all open elements. Call this to produce a well-formed document when execution ends prematurely.
     */
    public void endAllElements() {

        while (!tags.empty()) {

            endElement();
        }
    }

    /**
     * Writes an element with text content.
     * 
     * @param tag the tag
     * @param content the content
     */
    public void element(String tag, String content) {

        if (out == null)
            return;

        indent();
        out.println("<" + tag + ">" + escape(content) + "</" + tag + ">");
    }

    /**
     * Writes an empty element with attributes.
     * 
     * @param tag the tag
     * @param attributes the attributes
     */
    public void emptyElement(String tag, String attributes) {

        if (out == null)
            return;

        indent();
        out.println("<" + tag + " " + attributes + "/>");
    }

    private void indent() {

        if (out == null)
            return;

        for (int i = 0; i < tags.size(); i++) {
            for (int j = 0; j < indentation; j++) {
                out.write(' ');
            }
        }
    }

    /**
     * Writes an NCName element if non-null.
     * 
     * @param tag the tag
     * @param ncname the NCName object or null
     */
    public void write(String tag, NCName ncname) {

        if (ncname == null)
            return;

        element(tag, ncname.toString());
    }

    /**
     * Writes a String element if non-null.
     * 
     * @param tag the tag
     * @param content the String object or null
     */
    public void write(String tag, String content) {

        if (content == null)
            return;

        element(tag, content);
    }

    /**
     * Writes a Boolean element if non-null.
     * 
     * @param tag the tag
     * @param o the Boolean object or null
     */
    public void write(String tag, Boolean o) {

        if (o == null)
            return;

        write(tag, o.booleanValue());
    }

    /**
     * Writes a boolean element.
     * 
     * @param tag the tag
     * @param value the boolean value
     */
    public void write(String tag, boolean value) {

        element(tag, Boolean.toString(value));
    }

    /**
     * Writes an int element.
     * 
     * @param tag the tag
     * @param value the int value
     */
    public void write(String tag, int value) {

        element(tag, Integer.toString(value));
    }

    /**
     * Replaces the special XML characters a text string with their predefined entities.
     * 
     * @param x the text
     * @return the escaped text
     */
    public static String escape(String x) {

        if (x == null)
            return null;

        StringBuffer y = new StringBuffer();

        for (int i = 0; i < x.length(); i++) {

            char c = x.charAt(i);
            switch (c) {
            case '&':
                y.append("&amp;");
                break;

            case '<':
                y.append("&lt;");
                break;

            case '>':
                y.append("&gt;");
                break;

            case '"':
                y.append("&quot;");
                break;

            case '\'':
                y.append("&apos;");
                break;

            default:
                y.append(c);
            }
        }

        return y.toString();
    }
    
    /**
     * Registers a namespace writer for this XML writer.
     * 
     * @param writer the namespace writer
     */
    public void register(NamespaceWriter writer) {
        
        registry.put(writer.getNamespace(), writer);
    }
    
    /**
     * Looks up a namespace writer for this XML writer.
     * 
     * @param namespace the namespace to look up
     * @return the namespace writer
     */
    public NamespaceWriter lookup(String namespace) {
        
        return (NamespaceWriter) registry.get(namespace);
    }
}

package org.apache.woden.ant;

import java.net.URI;

/**
 * This is the abstract base class for classes that write elements and types from an XML namespace;
 * 
 * @author Arthur Ryman (ryman@ca.ibm.com, arthur.ryman@gmail.com)
 *
 */
public abstract class NamespaceWriter {
    
    protected XMLWriter out;
    
    private String namespace;
    
    private String prefix;
    
    public NamespaceWriter(XMLWriter out, String namespace, String prefix) {
        
        this.out = out;
        this.namespace = namespace;
        this.prefix = prefix;
        
        out.register(this);
    }
    
    public String getNamespace() {
        
        return namespace;
    }
    
    public String getPrefix() {
        
        return prefix;
    }
    
    public void write(String tag, URI uri) {

        if (uri == null)
            return;

        out.element(tag, uri.toString());
    }

    public void writeAny(String tag, Object o) {

        if (o == null)
            return;

        // TODO: write element content correctly
        out.element(tag, o.toString());
    }
}
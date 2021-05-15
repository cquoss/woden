package org.apache.woden.ant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Vector;

import org.apache.woden.ErrorHandler;
import org.apache.woden.ErrorInfo;
import org.apache.woden.ErrorLocator;

/**
 * The <code>Report</code> class writes an XML file that reports the results of validating a set of WSDL files.
 * 
 * @author Arthur Ryman (ryman@ca.ibm.com, arthur.ryman@gmail.com)
 *
 */
public class Report extends XMLWriter implements ErrorHandler {
    
    private final String NS = "http://www.w3.org/2006/06/wsdl/ValidationReport";

    private final String REPORT = "report";

    private final String WSDL = "wsdl";

    private final String URI = "uri";
    
    private final String SUCCESS = "success";

    // ErrorHandler elements

    private final String WARNING = "warning";

    private final String ERROR = "error";

    private final String FATAL_ERROR = "fatalError";

    // ErrorInfo elements

    private final String ERROR_LOCATOR = "errorLocator";

    private final String KEY = "key";

    private final String MESSAGE = "message";

    private final String EXCEPTION = "exception";

    // ErrorLocator elements

    private final String DOCUMENT_BASE_URI = "documentBaseUri";

    private final String LOCATION_URI = "locationUri";

    private final String LINE_NUMBER = "lineNumber";

    private final String COLUMN_NUMBER = "columnNumber";

    // Exception element

    private final String DETAIL_MESSAGE = "detailMessage";
    
    
    // error handler data
    private boolean success = true;
    private Vector errorInfos = null;
    private Vector severities = null;

    /**
     * Creates a report writer and writes the document root.
     * 
     * @param report the reort file
     * @return the report writer
     */
    public static Report openReport(File report) {

        if (report == null) {

            return new Report(null);
        }

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(report);
        } catch (FileNotFoundException e) {

            e.printStackTrace();

            return new Report(null);
        }

        PrintWriter reportOut = new PrintWriter(fos);
        Report reportWriter = new Report(reportOut);
        reportWriter.xmlDeclaration("UTF-8");
        reportWriter.beginReport();

        return reportWriter;
    }

    /**
     * Ends the report and closes the output writer.
     */
    public void closeReport() {

        endReport();
        close();
    }

    /**
     * Constructs a <code>Report</code> writer.
     * 
     * @param out the output writer or null
     */
    public Report(PrintWriter out) {
        super(out);
    }

    public void beginReport() {
        beginElement(REPORT, "xmlns='" + NS + "'");
    }

    public void endReport() {
        endAllElements();
    }

    public void beginWsdl(String uri) {
        
        success = true;
        errorInfos = new Vector();
        severities = new Vector();
        
        beginElement(WSDL);

        element(URI, uri);
    }

    public void endWsdl() {
        
        write(SUCCESS, success);
        
        for(int i = 0; i < errorInfos.size(); i ++) {
            
            ErrorInfo errorInfo = (ErrorInfo) errorInfos.elementAt(i);
            String severity = (String) severities.elementAt(i);
            
            write(severity, errorInfo);
        }
        
        endElement();
        
        success = true;
        errorInfos = null;
        severities = null;
    }

    public void warning(ErrorInfo errorInfo) {
        
        errorInfos.add(errorInfo);
        severities.add(WARNING);
    }

    public void error(ErrorInfo errorInfo) {

        success = false;
        errorInfos.add(errorInfo);
        severities.add(ERROR);
    }

    public void fatalError(ErrorInfo errorInfo) {

        success = false;
        errorInfos.add(errorInfo);
        severities.add(FATAL_ERROR);
    }

    public void write(String tag, ErrorInfo errorInfo) {

        if (errorInfo == null)
            return;

        beginElement(tag);

        write(ERROR_LOCATOR, errorInfo.getErrorLocator());
        element(KEY, errorInfo.getKey());
        element(MESSAGE, errorInfo.getMessage());
        write(EXCEPTION, errorInfo.getException());

        endElement();
    }

    public void write(String tag, ErrorLocator errorLocator) {

        if (errorLocator == null)
            return;

        beginElement(tag);

        write(DOCUMENT_BASE_URI, errorLocator.getDocumentBaseURI());
        write(LOCATION_URI, errorLocator.getLocationURI());
        write(LINE_NUMBER, errorLocator.getLineNumber());
        write(COLUMN_NUMBER, errorLocator.getColumnNumber());

        endElement();
    }

    public void write(String tag, Exception e) {

        if (e == null)
            return;

        beginElement(tag);

        write(DETAIL_MESSAGE, e.getMessage());

        endElement();
    }
}

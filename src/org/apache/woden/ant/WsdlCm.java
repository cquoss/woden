/**
 * Copyright 2006 Apache Software Foundation 
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

package org.apache.woden.ant;

/**
 * This class writes a Woden WSDL 2.0 component model instance in the W3C
 * interchange format which is an XML vocabulary for serializing component model
 * instances in a canonical way so that different implementations can be easily
 * compared for interoperability.
 * 
 * The latest schema for this format is available at the following URL:
 * 
 * http://dev.w3.org/cvsweb/~checkout~/2002/ws/desc/test-suite/interchange/wsdlcm.xsd?content-type=text/xml
 * 
 * @author Arthur Ryman (ryman@ca.ibm.com, arthur-ryman@gmail.com)
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.apache.woden.WSDLException;
import org.apache.woden.WSDLFactory;
import org.apache.woden.WSDLReader;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.xml.DescriptionElement;

/**
 * The <code>WsdlCm</code> class writes the component model in the W3C interchange format.
 * 
 * @author Arthur Ryman (ryman@ca.ibm.com, arthur-ryman@gmail.com)
 *
 */
public class WsdlCm extends XMLWriter {

    private CmWriter cm;

    /**
     * Constructs a component model writer.
     * 
     * @param out the output writer for the component model
     */
    public WsdlCm(PrintWriter out) {
        super(out);

        // create the writes for each namespace
        new CmBaseWriter(this);
        new CmExtensionsWriter(this);
        new CmRpcWriter(this);
        new CmHttpWriter(this);
        new CmSoapWriter(this);
        cm = new CmWriter(this);
    }

    /**
     * Writes the Description component in the component model interchange format.
     * 
     * @param descComp the Description component
     */
    public void write(Description descComp) {

        cm.write(descComp);
    }

    /**
     * Tests the component model interchange format writer.
     * 
     * TODO Move this method into a tests package.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // simple program to read a WSDL 2.0 file and then poke at the component
        // model.

        String wsdlLoc = "file:///D:/workspaces/WSD2/woden/ant-test/test.wsdl";
        String wsdlCmLoc = "D:\\workspaces\\WSD2\\woden\\ant-test\\test.xml";
        String reportLoc = "D:\\workspaces\\WSD2\\woden\\ant-test\\report.xml";

        if (args.length > 0) {
            wsdlLoc = args[0];
        }

        if (args.length > 1) {
            wsdlCmLoc = args[1];
        }

        if (args.length > 2) {
            reportLoc = args[2];
        }

        System.out.println("Starting: " + wsdlLoc);

        Report reportWriter = Report.openReport(new File(reportLoc));
        reportWriter.beginWsdl(wsdlLoc);

        try {

            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();

            // <-- enable WSDL 2.0 validation (optional)
            reader.setFeature(WSDLReader.FEATURE_VALIDATION, true);

            // <-- the <description> element
            DescriptionElement desc = reader.readWSDL(wsdlLoc, reportWriter);

            // <-- the Description component
            Description descComp = desc.toComponent();

            File xml = new File(wsdlCmLoc);
            FileOutputStream fos = new FileOutputStream(xml);
            PrintWriter out = new PrintWriter(fos);
            WsdlCm wsdlCm = new WsdlCm(out);

            wsdlCm.write(descComp);

            out.flush();

        } catch (WSDLException e) {

            e.printStackTrace();
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        reportWriter.closeReport();

        System.out.println("Finished.");
    }
}

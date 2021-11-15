package it.kdm.orchestratore.utils;


import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class  BasicXSL {


    // This method applies the xslFilename to inFilename and writes
    // the output to outFilename.
    public static byte[] xsl(InputStream inFilename, InputStream xslFilename) throws  Exception{
        try {
            // Create transformer factory
            TransformerFactory factory = TransformerFactory.newInstance();

            // Use the factory to create a template containing the xsl file
            Templates template = factory.newTemplates(new StreamSource(xslFilename));

            // Use the template to create a transformer
            Transformer xformer = template.newTransformer();

            // Prepare the input and output files
            Source source = new StreamSource(inFilename);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(outputStream);
            // Apply the xsl file to the source file and write the result
            // to the output file
            xformer.transform(source, result);
            return outputStream.toByteArray();
        }  catch (TransformerConfigurationException e) {
            // An error occurred in the XSL file
            throw e;
        } catch (TransformerException e) {
            // An error occurred while applying the XSL file
            // Get location of error in input file
            SourceLocator locator = e.getLocator();
            int col = locator.getColumnNumber();
            int line = locator.getLineNumber();
            String publicId = locator.getPublicId();
            String systemId = locator.getSystemId();
            throw e;
        }
    }
}

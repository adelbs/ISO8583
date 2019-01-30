package org.adelbs.iso8583.xml;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLUtils {
	
	public XMLUtils() {	}
	
	/**
	 * Convert a XML String into {@link Document}
	 * @param xml
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static Document convertXMLToDOM(final String xml) throws SAXException, IOException, ParserConfigurationException{
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
	    final InputSource is = new InputSource(new StringReader(sanitizeXML(xml)));
		return builder.parse(is);
	}
	
	/**
	 * Remove all unnecessary white spaces, tabs and line breaks from XML string.
	 * TODO: Would be better to use a XSD with setIgnoringElementContentWhitespace attribute true
	 * 
	 * @param xml
	 * @return
	 */
	public static String sanitizeXML(String xml) {
		return xml.replace("\n", "").replace("\r", "").replace("\t", "").replaceAll(">\\s+<", "><").trim();
	}

}

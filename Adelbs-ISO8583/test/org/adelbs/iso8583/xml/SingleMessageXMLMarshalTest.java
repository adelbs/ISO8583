package org.adelbs.iso8583.xml;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.adelbs.iso8583.constants.DelimiterEnum;
import org.adelbs.iso8583.gui.ISOConfigGuiConverter;
import org.adelbs.iso8583.vo.ISOConfigVO;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This TestCase is the most simple case of a ISO XML message config, with a single message
 * without Fields
 */
public class SingleMessageXMLMarshalTest {
	
	protected static ISOConfigMarshaller ISOCONFIG_MARSHALLER;
	protected static DocumentBuilder DOCBUILDER;
	protected Node rootNode;

	
	@Before
	public void setUp() throws Exception {
		createTestTreeNode();
	}
	
	@BeforeClass
	public static void setUpClass() throws ParserConfigurationException, ISOConfigMarshallerException{
		ISOCONFIG_MARSHALLER =  ISOConfigMarshaller.creatMarshaller();
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DOCBUILDER=factory.newDocumentBuilder();  
	}
	
	/**
	 * Creates a test TreeNode to be used inside this TestCase. Should be overridden when subclasses
	 * to change the behavior/structure of the tree node.
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ISOConfigMarshallerException 
	 */
	void createTestTreeNode() throws ParserConfigurationException, SAXException, IOException, ISOConfigMarshallerException{
		this.rootNode = buildDOMDocFromTreeNode(TreeNodeTestFactory.createSingleMessageWithoutFieldsTestTreeNode());
	}
	
	@Test
	public void testRootElementParse() throws ParserConfigurationException, SAXException, IOException{
		assertTrue("First element must be a ISO8583", "ISO8583".equalsIgnoreCase(rootNode.getNodeName()));
		
        final String delimiterAttribute = rootNode.getAttributes().getNamedItem("delimiter").getFirstChild().getNodeValue();
        assertTrue("Delimiter attribute should be length 2", "LENGTH2_DELIMITER_BEG".equalsIgnoreCase(delimiterAttribute));
	}
	
	@Test
	public void testQuantityOfMessages(){
		final NodeList messageNodes = rootNode.getChildNodes();
		assertEquals("We have only one Message", 1, messageNodes.getLength());
	}
	
	@Test
	public void testMessagesParse(){
		final Node messageNode = getMessageNode();
		assertTrue("Tag element shoudl be called Message, and its independent of the Case", "MESSAGE".equalsIgnoreCase(messageNode.getNodeName()));
		assertEquals("Type attribute must be presented and must have a proper value", "0200", this.getAttributeFromNode(messageNode, "type"));	
	}
	
	@Test
	public void testFieldDetph(){
		final Node messageNode = getMessageNode();
		assertEquals("This Message do not have any fields. The Depth should be zero", 0, messageNode.getChildNodes().getLength());	
	}

	/**
	 * @return
	 */
	protected Node getMessageNode() {
		final NodeList messageNodes = rootNode.getChildNodes();
		final Node messageNode = messageNodes.item(0);
		return messageNode;
	}
	
	/**
	 * Extract the value of an attribute
	 * @param node
	 * @return
	 */
	protected String getAttributeFromNode(final Node node, final String attribute) {
		return node.getAttributes().getNamedItem(attribute).getFirstChild().getNodeValue();
	}
	
	/**
	 * Build a DOM document from a Tree Node
	 * @return
	 * @throws ISOConfigMarshallerException 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	protected Node buildDOMDocFromTreeNode(final DefaultMutableTreeNode treeNode) throws ISOConfigMarshallerException, SAXException, IOException {
		final ISOConfigVO isoConfigVO = ISOConfigGuiConverter.revert(treeNode);
		isoConfigVO.setDelimiter(DelimiterEnum.getDelimiter(""));
		
		final String xmlConfig = ISOCONFIG_MARSHALLER.marshal(isoConfigVO);
		final String isoConfigXML = sanitizeXML(xmlConfig);
		final Document doc = DOCBUILDER.parse(new InputSource(new StringReader(isoConfigXML)));
        return doc.getFirstChild();
	}
	
	
	
	/**
	 * Remove all unnecessary white spaces, tabs and line breaks from XML string.
	 * TODO: Would be better to use a XSD with setIgnoringElementContentWhitespace attribute true
	 * 
	 * @param isoConfigXML
	 * @return
	 */
	private static String sanitizeXML(String isoConfigXML) {
		return XMLUtils.sanitizeXML(isoConfigXML);
	}

	
	@After
	public void tearDown() throws Exception {
	}

}

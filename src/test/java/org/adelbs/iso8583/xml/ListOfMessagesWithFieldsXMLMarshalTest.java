package org.adelbs.iso8583.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This TestCase test if ISO XML message config, with a more than one message
 * with fields, is parsed correctly.
 */
public class ListOfMessagesWithFieldsXMLMarshalTest extends SingleMessageWithFieldsXMLMarshalTest{
	
	@Override
	void createTestTreeNode() throws ParserConfigurationException, SAXException, IOException, ISOConfigMarshallerException{
		this.rootNode = buildDOMDocFromTreeNode(TreeNodeTestFactory.createListOfMessagesWithFieldsTestTreeNode());
	}
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Test @Override
	public void testQuantityOfMessages() {
		final NodeList messageNodes = rootNode.getChildNodes();
		assertEquals("We have two Messages", 2, messageNodes.getLength());
	}
	
	@Test @Override
	public void testMessagesParse(){
		super.testMessagesParse();
		
		final Node messageNode = getMessageNode().getNextSibling();
		assertTrue("Tag element shoudl be called Message, and its independent of the Case", "MESSAGE".equalsIgnoreCase(messageNode.getNodeName()));
		assertEquals("Type attribute must be presented and must have a proper value", "0210", this.getAttributeFromNode(messageNode, "type"));	
	}
	
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

}

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
 * This TestCase test if ISO XML message config, with a single message
 * has fields, is parsed correctly
 */
public class SingleMessageWithFieldsXMLMarshalTest extends SingleMessageXMLMarshalTest{
	
	@Override
	void createTestTreeNode() throws ParserConfigurationException, SAXException, IOException, ISOConfigMarshallerException{
		this.rootNode = buildDOMDocFromTreeNode(TreeNodeTestFactory.createSingleMessageWithFieldsTestTreeNode());
	}
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Test @Override
	public void testFieldDetph(){
		final NodeList childNodes = this.getMessageNode().getChildNodes();
		final Node firstField = getMessageNode().getFirstChild();
		final Node secondField = firstField.getNextSibling();
		assertTrue("Message should have Fields, but the depth should be 1", childNodes.getLength() > 0 && 
				firstField.getChildNodes().getLength() == 0 && 
				secondField.getChildNodes().getLength() == 0);	
	}
	
	@Test
	public void testFieldsParse(){
		final Node firstField = getMessageNode().getFirstChild();
		final Node secondField = firstField.getNextSibling();
		
		assertTrue("The First level child of a Message should be called FIELD", "FIELD".equalsIgnoreCase(firstField.getNodeName()) && "FIELD".equalsIgnoreCase(firstField.getNodeName()));
		
		assertTrue("fieldNode001".equalsIgnoreCase(this.getAttributeFromNode(firstField, "name")));
		assertEquals("1", this.getAttributeFromNode(firstField, "bitnum"));
		
		assertTrue("fieldNode002".equalsIgnoreCase(this.getAttributeFromNode(secondField, "name")));
		assertEquals("2", this.getAttributeFromNode(secondField, "bitnum"));
	}
	
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

}

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
 * Test Complex case of ISO Config Xml structure, where we have a combination of messages, fields and subfields
 */
public class ListOfMessagesWithSubFieldsXMLMarshalTest extends ListOfMessagesWithFieldsXMLMarshalTest{
	@Override
	void createTestTreeNode() throws ParserConfigurationException, SAXException, IOException, ISOConfigMarshallerException{
		this.rootNode = buildDOMDocFromTreeNode(TreeNodeTestFactory.createListOfMessagesWithSubFieldTestTreeNode());
	}
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	private int calculateTreeDepth(final Node node){
		final NodeList childNodes = node.getChildNodes();
		int depth = 1;
		int iterationDetph = 0;
		for(int i=0; i<childNodes.getLength(); i++){
			final Node subNode = childNodes.item(i);
			final int subNodeDepth = calculateTreeDepth(subNode);
			iterationDetph = subNodeDepth > iterationDetph ? subNodeDepth : iterationDetph;
		}
		return depth + iterationDetph;
	}
	
	@Test @Override 
	public void testFieldDetph() {
		final int fieldTreeDepth = calculateTreeDepth(rootNode);
		assertEquals("This ISO Config should have detpth 5", 5, fieldTreeDepth);	
	}
	
	@Test
	public void testSubFieldsParse(){
		final Node fieldMessage = getMessageNode().getFirstChild();
		final Node subFieldNode = fieldMessage.getFirstChild();
		final Node leafFieldNode = subFieldNode.getFirstChild();
		
		assertTrue("Subfields must be also called Field, ignore case", "FIELD".equalsIgnoreCase(subFieldNode.getNodeName()) && 
				"FIELD".equalsIgnoreCase(leafFieldNode.getNodeName()));
		
		
		assertTrue(this.getAttributeFromNode(subFieldNode, "name").startsWith("fieldNode001."));
		assertEquals("1", this.getAttributeFromNode(subFieldNode, "bitnum"));
		
		assertTrue(this.getAttributeFromNode(leafFieldNode, "name").startsWith("fieldNode001.1."));
		assertEquals("1", this.getAttributeFromNode(subFieldNode, "bitnum"));
	}
	
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

}

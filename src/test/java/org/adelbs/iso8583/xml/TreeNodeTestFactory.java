package org.adelbs.iso8583.xml;

import javax.swing.tree.DefaultMutableTreeNode;

import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.constants.TypeLengthEnum;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.MessageVO;

/**
 * Package factory to help creating clean TreeNode instances to 
 * be used as input on our Tests
 */
final class TreeNodeTestFactory {
	private TreeNodeTestFactory(){}
	
	
	private static DefaultMutableTreeNode createFieldNode(final String fieldName, final int bitNum) {
		return new DefaultMutableTreeNode(new FieldVO(null, fieldName, "", bitNum, TypeEnum.ALPHANUMERIC, TypeLengthEnum.FIXED, 1, EncodingEnum.UTF8, ""));
	}
	
	private static DefaultMutableTreeNode createSubFieldNode(final String fieldName, final int bitNum) {
		return new DefaultMutableTreeNode(new FieldVO(null, "", fieldName, bitNum, TypeEnum.ALPHANUMERIC, TypeLengthEnum.FIXED, 1, EncodingEnum.UTF8, ""));
	}

	private static DefaultMutableTreeNode createMessageNode(final String messageCode) {
		final MessageVO messageVO = new MessageVO(messageCode, EncodingEnum.UTF8, EncodingEnum.UTF8);
		return new DefaultMutableTreeNode(messageVO);
	}
	
	/**
	 * Create Test {@link DefaultMutableTreeNode} message without any Fields
	 */
	public static DefaultMutableTreeNode createSingleMessageWithoutFieldsTestTreeNode() {
		//Root Node
		final DefaultMutableTreeNode nodeTree = new DefaultMutableTreeNode("ISO8583");
		
		//Add First Message 0200;
		final DefaultMutableTreeNode messageNode = createMessageNode("0200");
		nodeTree.add(messageNode);
				
		return nodeTree;
	}
	
	/**
	 * Creates a single Test Message with two Fields 
	 * @return
	 */
	public static DefaultMutableTreeNode createSingleMessageWithFieldsTestTreeNode() {
		//Root Node
		DefaultMutableTreeNode nodeTree = createSingleMessageWithoutFieldsTestTreeNode();		
		final DefaultMutableTreeNode messageNode = (DefaultMutableTreeNode) nodeTree.getFirstChild();
		
		DefaultMutableTreeNode fieldNode;
		
		//Add First Level Fields
		fieldNode =  createFieldNode("fieldNode001", 1);
		messageNode.add(fieldNode);

		fieldNode =  createFieldNode("fieldNode002", 2);
		messageNode.add(fieldNode);
				
		return nodeTree;
	}
	
	/**
	 * Creates a dual test message with Fields
	 * @return
	 */
	public static DefaultMutableTreeNode createListOfMessagesWithFieldsTestTreeNode() {
		//First pack of Message
		DefaultMutableTreeNode nodeTree = createSingleMessageWithFieldsTestTreeNode();
				
		//Add Second Message 0210
		final DefaultMutableTreeNode messageNode =  createMessageNode("0210");
		final DefaultMutableTreeNode fieldNode =  createFieldNode("fieldNode003", 1);
		messageNode.add(fieldNode);
		nodeTree.add(messageNode);
				
		return nodeTree;
	}

	
	/**
	 * Creates a list of Messages where We have a sample of a Field with two level deep subfield list
	 * @return
	 */
	public static DefaultMutableTreeNode createListOfMessagesWithSubFieldTestTreeNode() {
		//Root Node
		DefaultMutableTreeNode nodeTree = createListOfMessagesWithFieldsTestTreeNode();
		
		//Message 0200;
		DefaultMutableTreeNode messageNode = (DefaultMutableTreeNode) nodeTree.getFirstChild();
		
		//First Level Field
		DefaultMutableTreeNode fieldNode =  (DefaultMutableTreeNode) messageNode.getFirstChild();
		
		//Add Second Level of Fields
		DefaultMutableTreeNode subFieldNode = createSubFieldNode("fieldNode001.1",1);
		fieldNode.add(subFieldNode);
		
		//Add Third Level Field
		for(int i=1; i< 4; i++){
			subFieldNode.add(createSubFieldNode("fieldNode001.1."+i, i));
		}
		
		subFieldNode = createSubFieldNode("fieldNode001.2", 2);
		fieldNode.add(subFieldNode);
		
		return nodeTree;
	}

}

package org.adelbs.iso8583.gui;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.ISOConfigVO;
import org.adelbs.iso8583.vo.MessageVO;

/**
 * Class responsible to convert ISO {@link DefaultMutableTreeNode} into {@link ISOConfigVO} and vice-versa
 * 
 * TODO: Search for places where repeated conventions happens and substitute by methods provided by this class
 */
public class ISOConfigGuiConverter {

	ISOConfigGuiConverter() {}
	
	//TODO: Must be implemented
	public  static DefaultMutableTreeNode convert(final ISOConfigVO isoConfigVO){
		return null;
	}

	/**
	 * Helper method to revert a ISOConfig {@link DefaultMutableTreeNode} into a {@link ISOConfigVO} object
	 * @param isoConfigNode {@link DefaultMutableTreeNode} structured as a ISOConfig 
	 * @return ISOConfigVO object populated with its messages and fields. Delimiter is not set by default
	 */
	@SuppressWarnings("unchecked")
	public static ISOConfigVO revert(DefaultMutableTreeNode isoConfigNode){	
		final ISOConfigVO isoConfigVO = new ISOConfigVO();
		isoConfigVO.addAllMessages(revertIntoAListOfMessages(isoConfigNode.children()));
		return isoConfigVO;
	}
	
	/**
	 * Convert a list of messages nodes into a list of {@link MessageVO}
	 * @param messageNodes Enumeration of Tree Nodes with Message data
	 * @return List of {@link MessageVO}
	 */
	private static List<MessageVO> revertIntoAListOfMessages(final Enumeration<DefaultMutableTreeNode> messageNodes){	
		final ArrayList<MessageVO> messagesList = new ArrayList<MessageVO>();
		while (messageNodes.hasMoreElements()) {
			final DefaultMutableTreeNode messageUINode = messageNodes.nextElement();
			messagesList.add(revertIntoAMessage(messageUINode));
		}
		return messagesList;
	}
		
	
	/**
	 * Convert a single message tree node into a {MessageVO} object
	 * @param node
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static MessageVO revertIntoAMessage(final DefaultMutableTreeNode node){
		final MessageVO messageVo = (MessageVO) node.getUserObject();
		//Ignore The actual state of the message field list. It will be updated by the tree node structure
		messageVo.getFieldList().clear();
		messageVo.getFieldList().addAll(revertIntoAListOfFields(node.children()));
		return messageVo;
	}
	
	/**
	 * Convert a enumeration of UI Field nodes into a list of {@link FieldVO} 
	 * @param fieldNodes Ordered list of fields to be parsed
	 * @param isSubField check whether these fields are a first level fields, or its children of a Field Node
	 * @return List of FieldVO
	 */
	private static List<FieldVO> revertIntoAListOfFields(final Enumeration<DefaultMutableTreeNode> fieldNodes){
		final ArrayList<FieldVO> fieldsList = new ArrayList<FieldVO>();
		while (fieldNodes.hasMoreElements()) {
			final DefaultMutableTreeNode node = fieldNodes.nextElement();
			fieldsList.add(revertIntoAField(node));
		}
		return fieldsList;
	}
	
	/**
	 * Convert a single Field UI Node into {@link FieldVO}. Field name depends on the level of the field.
	 * The method also search for child field nodes.
	 * 
	 * @param fieldNode Field UI node to be parsed into XML
	 * @return converted FieldVo with children
	 */
	@SuppressWarnings("unchecked")
	private static FieldVO revertIntoAField(final DefaultMutableTreeNode fieldNode){
		final FieldVO fieldVo = (FieldVO) fieldNode.getUserObject();
		
		//Ignore The actual state of the Field list. It will be updated by the tree node structure
		fieldVo.getFieldList().clear();
		if (fieldNode.getChildCount() > 0) {
			fieldVo.getFieldList().addAll(revertIntoAListOfFields(fieldNode.children()));
		}
		return fieldVo;
	}
}

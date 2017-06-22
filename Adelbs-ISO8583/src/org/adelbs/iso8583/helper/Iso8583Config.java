package org.adelbs.iso8583.helper;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.adelbs.iso8583.constants.DelimiterEnum;
import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.constants.NodeValidationError;
import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.constants.TypeLengthEnum;
import org.adelbs.iso8583.gui.PnlMain;
import org.adelbs.iso8583.gui.xmlEditor.XmlTextPane;
import org.adelbs.iso8583.protocol.ISO8583Delimiter;
import org.adelbs.iso8583.util.ISOUtils;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.GenericIsoVO;
import org.adelbs.iso8583.vo.MessageVO;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import groovy.util.Eval;

public class Iso8583Config {
	
	private DefaultMutableTreeNode configTreeNode;
	private XmlTextPane xmlText = new XmlTextPane();
	
	private DelimiterEnum isoDelimiter;
	
	//Arquivo de configuração carregado
	private String xmlFilePath = null;
	
	public Iso8583Config(String fileName) {
		this();
		openFile(null, fileName);
		parseXmlToConfig(null);
	}
	
	public Iso8583Config() {
		isoDelimiter = DelimiterEnum.getDelimiter("");
		configTreeNode = new DefaultMutableTreeNode("ISO8583");
	}
	
	public DefaultMutableTreeNode getConfigTreeNode() {
		return configTreeNode;
	}
	
	public XmlTextPane getXmlText() {
		return xmlText;
	}
	
	public DefaultMutableTreeNode addType() {
		MessageVO parseVO = new MessageVO("0000", EncodingEnum.UTF8, EncodingEnum.UTF8);
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(parseVO);
		configTreeNode.add(newNode);
		return newNode;
	}

	public DefaultMutableTreeNode addField(PnlMain pnlMain, Object node) {
		DefaultMutableTreeNode newNode = null;
		
		boolean add = ((DefaultMutableTreeNode) node).getUserObject() instanceof MessageVO;
		add = add || (((DefaultMutableTreeNode) node).getUserObject() instanceof FieldVO && 
				!(((DefaultMutableTreeNode) ((DefaultMutableTreeNode) node).getParent()).getUserObject() instanceof FieldVO));
		
		if (add) {
			newNode = new DefaultMutableTreeNode(new FieldVO(pnlMain, "NewField", "", 2, TypeEnum.ALPHANUMERIC, TypeLengthEnum.FIXED, 1, EncodingEnum.UTF8, ""));
			((DefaultMutableTreeNode) node).add(newNode);
		}
		
		return newNode;
	}
	
	@SuppressWarnings("unchecked")
	public void updateSumField(Object node) {
		Enumeration<DefaultMutableTreeNode> enu = ((DefaultMutableTreeNode) node).children();
		FieldVO fieldVo = (FieldVO) ((DefaultMutableTreeNode) node).getUserObject();
		
		String name = fieldVo.getName();
		int sum = 0;
		int count = 1;
		
		while (enu.hasMoreElements()) {
			fieldVo = (FieldVO) enu.nextElement().getUserObject();
			fieldVo.setName(name + count);
			//fieldVo.setBitNum(count);
			
			sum += fieldVo.getLength();
			count++;
		}
		
		fieldVo = (FieldVO) ((DefaultMutableTreeNode) node).getUserObject();
		
		if (count > 1)
			fieldVo.setLength(sum);
	}
	
	@SuppressWarnings("unchecked")
	public void parseConfigToXML() {
		MessageVO messageVo;
		FieldVO fieldVo;

		DefaultMutableTreeNode node;
		
		StringBuilder xmlISO = new StringBuilder();
		
		xmlISO.append("<?xml version=\"1.0\" ?>\n\n");
		xmlISO.append("<iso8583 delimiter=\"").append(isoDelimiter.getValue()).append("\">");
		
		//Capturando os MessageVO
		Enumeration<DefaultMutableTreeNode> enuParse = configTreeNode.children();
		Enumeration<DefaultMutableTreeNode> enuFields;
		Enumeration<DefaultMutableTreeNode> enuSubFields;
		
		while (enuParse.hasMoreElements()) {
			node = enuParse.nextElement();
			messageVo = (MessageVO) node.getUserObject();
			xmlISO.append("\n\n\t<message type=\"").append(messageVo.getType()).
					append("\" header-encoding=\"").append(messageVo.getHeaderEncoding().toPlainString()).
					append("\" bitmap-encoding=\"").append(messageVo.getBitmatEncoding().toPlainString()).append("\">");
			
			//Capturando os FieldVO
			enuFields = node.children();
			while (enuFields.hasMoreElements()) {
				node = enuFields.nextElement();
				fieldVo = (FieldVO) node.getUserObject();
				
				appendFieldVO(xmlISO, fieldVo, (node.getChildCount() > 0), false);
				
				if (node.getChildCount() > 0) {
					
					xmlISO.append(">");
					
					//Capturando os FieldVO filhos
					enuSubFields = node.children();
					while (enuSubFields.hasMoreElements()) {
						node = enuSubFields.nextElement();
						fieldVo = (FieldVO) node.getUserObject();
						
						appendFieldVO(xmlISO, fieldVo, false, true);
					}
					xmlISO.append("\n\t\t</field>");
				}
					
			}
			
			xmlISO.append("\n\t</message>");
		}

		xmlISO.append("\n</iso8583>");
		
		xmlText.setText(xmlISO.toString());
	}
	
	private void appendFieldVO(StringBuilder xmlISO, FieldVO fieldVo, boolean hasChild, boolean subField) {
		xmlISO.append("\n").append(subField ? "\t\t\t" : "\t\t").append("<field ").
			append("name=\"").append((subField ? fieldVo.getSubFieldName() : fieldVo.getName())).append("\" ").		
			append("bitnum=\"").append(fieldVo.getBitNum()).append("\" ").
			append("condition=\"").append(fieldVo.getDynaCondition()).append("\" ").
			append("length-type=\"").append(fieldVo.getTypeLength().toPlainString()).append("\" ").
			append("length=\"").append(fieldVo.getLength()).append("\" ").
			append("type=\"").append(fieldVo.getType()).append("\" ").
			append("encoding=\"").append(fieldVo.getEncoding().toPlainString()).append("\" ");
		
		if (!hasChild) xmlISO.append("/>");
	}
	
	public void parseXmlToConfig(PnlMain pnlMain) {
		try {
			if (!xmlText.getText().trim().equals("")) {
				configTreeNode.removeAllChildren();
				
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
			    InputSource is = new InputSource(new StringReader(xmlText.getText()));
				
				Document document = builder.parse(is);
				
				DefaultMutableTreeNode lastParseNode;
				
				setDelimiterEnum(DelimiterEnum.getDelimiter(document.getDocumentElement().getAttribute("delimiter")));
				
				NodeList nodeList = document.getDocumentElement().getChildNodes();
				Node node;
				
				for (int i = 0; i < nodeList.getLength(); i++) {
					node = nodeList.item(i);
					
					if ("message".equalsIgnoreCase(node.getNodeName())) {
						lastParseNode = addType();
						
						((MessageVO) lastParseNode.getUserObject()).setType(ISOUtils.getAttr(node, "type", "0000"));;
						((MessageVO) lastParseNode.getUserObject()).setHeaderEncoding(EncodingEnum.getEncoding(ISOUtils.getAttr(node, "header-encoding", "")));
						((MessageVO) lastParseNode.getUserObject()).setBitmatEncoding(EncodingEnum.getEncoding(ISOUtils.getAttr(node, "bitmap-encoding", "")));
						
						addFieldsToTree(pnlMain, node, lastParseNode);
					}
				}
			}
		}
		catch (Exception x) {
			x.printStackTrace();
			JOptionPane.showMessageDialog(pnlMain, "Invalid XML! See the log file.\n\n" + x.getMessage());
		}
	}

	public MessageVO getMessageVOAtTree(String type) {
		
		DefaultMutableTreeNode messageNode = null;
		MessageVO messageVO;
		MessageVO newMessageVO = null;
		FieldVO fieldVO;
		FieldVO newFieldVO;
		FieldVO newSubfieldVO;
		
		for (int i = 0; i < configTreeNode.getChildCount(); i++) {
			messageNode = (DefaultMutableTreeNode) configTreeNode.getChildAt(i);
			messageVO = (MessageVO) messageNode.getUserObject();
			if (messageVO.getType().equals(type)) {
				newMessageVO = messageVO.getInstanceCopy();
				break;
			}
		}
		
		if (newMessageVO != null) {
			newMessageVO.setFieldList(new ArrayList<FieldVO>());
			for (int i = 0; i < messageNode.getChildCount(); i++) {
				fieldVO = (FieldVO) ((DefaultMutableTreeNode) messageNode.getChildAt(i)).getUserObject();
				newFieldVO = fieldVO.getInstanceCopy();
				
				newMessageVO.getFieldList().add(newFieldVO);
				
				newFieldVO.setFieldList(new ArrayList<FieldVO>());
				for (int j = 0; j < messageNode.getChildAt(i).getChildCount(); j++) {
					fieldVO = (FieldVO) ((DefaultMutableTreeNode) messageNode.getChildAt(i).getChildAt(j)).getUserObject();	
					newSubfieldVO = fieldVO.getInstanceCopy();

					newFieldVO.getFieldList().add(newSubfieldVO);
				}
			}
		}
		
		return newMessageVO;
	}
	
	private void addFieldsToTree(PnlMain pnlMain, Node node, DefaultMutableTreeNode lastParseNode) {
		NodeList fieldList = node.getChildNodes();
		NodeList subFieldList;

		DefaultMutableTreeNode lastFieldNode;
		DefaultMutableTreeNode newNode;
		FieldVO fieldVo;

		for (int j = 0; j < fieldList.getLength(); j++) {
			node = fieldList.item(j);
			
			if ("field".equalsIgnoreCase(node.getNodeName())) {
				newNode = addField(pnlMain, lastParseNode);
				fieldVo = (FieldVO) newNode.getUserObject();
				populateField(node, fieldVo, newNode, lastParseNode, false);
				
				subFieldList = node.getChildNodes();
				if (subFieldList.getLength() > 1) {
					lastFieldNode = newNode;
					for (int w = 0; w < subFieldList.getLength(); w++) {
						node = subFieldList.item(w);
						
						if ("field".equalsIgnoreCase(node.getNodeName())) {
							newNode = addField(pnlMain, lastFieldNode);
							fieldVo = (FieldVO) newNode.getUserObject();
							populateField(node, fieldVo, newNode, lastParseNode, true);
						}
					}
					updateSumField(lastFieldNode);
				}
			}
		}
	}
	
	private void populateField(Node node, FieldVO fieldVo, DefaultMutableTreeNode newNode, DefaultMutableTreeNode lastParseNode, boolean isSubfield) {

		fieldVo.setName(ISOUtils.getAttr(node, "name", ""));
		
		if (isSubfield)
			fieldVo.setSubFieldName(ISOUtils.getAttr(node, "name", ""));

		fieldVo.setBitNum(Integer.parseInt(ISOUtils.getAttr(node, "bitnum", "0")));
		fieldVo.setType(TypeEnum.getType(ISOUtils.getAttr(node, "type", "")));
		fieldVo.setTypeLength(TypeLengthEnum.getTypeLength(ISOUtils.getAttr(node, "length-type", "")));
		fieldVo.setLength(Integer.parseInt(ISOUtils.getAttr(node, "length", "0")));
		fieldVo.setEncoding(EncodingEnum.getEncoding(ISOUtils.getAttr(node, "encoding", "")));
		fieldVo.setDynaCondition(ISOUtils.getAttr(node, "condition", ""));
	}
	
	public void openFile(Component mainFrame, String file) {
		
		this.xmlFilePath = file;
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    xmlText.setText(sb.toString());
		} 
		catch (Exception x) {
			this.xmlFilePath = null;
			x.printStackTrace();
			
			if (mainFrame != null)
				JOptionPane.showMessageDialog(mainFrame, "Unable to read the file. See the log.\n\n"+ x.getMessage());
		}
		finally {
			if (br != null) {
				try {
					br.close();
				}
				catch (Exception x) {
					x.printStackTrace();
				}
			}
		}
	}
	
	public boolean newFile(Component mainFrame) {
		
		boolean result = false;
		
		if (JOptionPane.showConfirmDialog(mainFrame, "Are you sure? All changes will be lost.", "New File", JOptionPane.YES_NO_OPTION) == 0) {
			xmlText.setText("<j8583-config></j8583-config>");
			result = true;
		}
		
		return result;
	}
	
	public void saveFile(Component mainFrame, String filePath) {
		
		BufferedWriter output = null;
		
		try {
			if (!filePath.equals("")) {
				this.xmlFilePath = filePath;
				File file = new File(filePath);
	
				if (!file.exists()) {
					file.createNewFile();
				}
				
	            output = new BufferedWriter(new FileWriter(file));
	            output.write(xmlText.getText());
	            
				JOptionPane.showMessageDialog(mainFrame, "File saved successfully!");
			}
			else {
				this.xmlFilePath = null;
			}
		} 
		catch (Exception x) {
			this.xmlFilePath = null;
			x.printStackTrace();
			JOptionPane.showMessageDialog(mainFrame, "Error saving the XML file. See the log.\n\n"+ x.getMessage());
		}
		finally {
			if ( output != null ) {
				try {
					output.close();
				}
				catch (Exception x) {
					x.printStackTrace();
				}
			}
		}
	}

	public void validateAllNodes() {
		validateAllNodes(configTreeNode);
	}
	
	public void validateAllNodes(DefaultMutableTreeNode rootNode) {

		DefaultMutableTreeNode node;
		
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			node = (DefaultMutableTreeNode) rootNode.getChildAt(i);
			if (node.getChildCount() > 0)
				validateAllNodes(node);
		}
		
		validateNode((GenericIsoVO) rootNode.getUserObject(), (DefaultMutableTreeNode) rootNode.getParent());
	}
	
	public boolean validateNode(GenericIsoVO isoVO, DefaultMutableTreeNode selectedNodeParent) {
		if (isoVO instanceof FieldVO) {
			if (selectedNodeParent == null && ((FieldVO) isoVO).getBitNum().intValue() == 1)
				isoVO.addValidationError(NodeValidationError.RESERVED_BIT_NUMBER);
			else
				isoVO.removeValidationError(NodeValidationError.RESERVED_BIT_NUMBER);
				
			if (!"".equals(validateCondition((FieldVO) isoVO)))
				isoVO.addValidationError(NodeValidationError.INVALID_CONDITION);
			else
				isoVO.removeValidationError(NodeValidationError.INVALID_CONDITION);
			
			if (((FieldVO) isoVO).getBitNum().intValue() <= 0)
				isoVO.addValidationError(NodeValidationError.INVALID_BIT_NUMBER);
			else
				isoVO.removeValidationError(NodeValidationError.INVALID_BIT_NUMBER);
			
			if (selectedNodeParent != null) {
				FieldVO fieldVO1;
				FieldVO fieldVO2;
				for (int i = 0; i < selectedNodeParent.getChildCount(); i++) {
					fieldVO1 = (FieldVO) ((DefaultMutableTreeNode) selectedNodeParent.getChildAt(i)).getUserObject();
					fieldVO1.removeValidationError(NodeValidationError.DUPLICATED_BIT);
					
					for (int j = 0; j < selectedNodeParent.getChildCount(); j++) {
						fieldVO2 = (FieldVO) ((DefaultMutableTreeNode) selectedNodeParent.getChildAt(j)).getUserObject();
						if (!fieldVO1.equals(fieldVO2) && fieldVO1.getBitNum().intValue() == fieldVO2.getBitNum().intValue()) {
							
							boolean isDynamic1 = !fieldVO1.getDynaCondition().equals("") && !fieldVO1.getDynaCondition().equals("true");
							boolean isDynamic2 = !fieldVO2.getDynaCondition().equals("") && !fieldVO2.getDynaCondition().equals("true");
							
							if (!isDynamic1)
								fieldVO1.addValidationError(NodeValidationError.DUPLICATED_BIT);
							
							if (!isDynamic2)
								fieldVO2.addValidationError(NodeValidationError.DUPLICATED_BIT);

							break;
						}
					}
				}
			}
		}
		else if (isoVO instanceof MessageVO) {
			if (selectedNodeParent != null) {
				MessageVO messageVO1;
				MessageVO messageVO2;
				for (int i = 0; i < selectedNodeParent.getChildCount(); i++) {
					messageVO1 = (MessageVO) ((DefaultMutableTreeNode) selectedNodeParent.getChildAt(i)).getUserObject();
					messageVO1.removeValidationError(NodeValidationError.DUPLICATED_MESSAGE_TYPE);
					
					for (int j = 0; j < selectedNodeParent.getChildCount(); j++) {
						messageVO2 = (MessageVO) ((DefaultMutableTreeNode) selectedNodeParent.getChildAt(j)).getUserObject();
						if (!messageVO1.equals(messageVO2) && messageVO1.getType().equals(messageVO2.getType())) {
							messageVO1.addValidationError(NodeValidationError.DUPLICATED_MESSAGE_TYPE);
							messageVO2.addValidationError(NodeValidationError.DUPLICATED_MESSAGE_TYPE);
							break;
						}
					}
				}
			}
		}
		
		if (selectedNodeParent != null && !(selectedNodeParent.getUserObject() instanceof String)) {
			if (selectedNodeParent.getParent() != null && ((DefaultMutableTreeNode) selectedNodeParent.getParent()).getUserObject() instanceof MessageVO) {
				if (!isoVO.isValid()) {
					((GenericIsoVO) selectedNodeParent.getUserObject()).addValidationError(NodeValidationError.CHILD_ERRORS);
					((GenericIsoVO) ((DefaultMutableTreeNode) selectedNodeParent.getParent()).getUserObject()).addValidationError(NodeValidationError.CHILD_ERRORS);
				}
				else {
					((GenericIsoVO) selectedNodeParent.getUserObject()).removeValidationError(NodeValidationError.CHILD_ERRORS);
					((GenericIsoVO) ((DefaultMutableTreeNode) selectedNodeParent.getParent()).getUserObject()).removeValidationError(NodeValidationError.CHILD_ERRORS);
				}
			}
			else {
				if (!isoVO.isValid()) {
					((GenericIsoVO) selectedNodeParent.getUserObject()).addValidationError(NodeValidationError.CHILD_ERRORS);
				}
				else {
					((GenericIsoVO) selectedNodeParent.getUserObject()).removeValidationError(NodeValidationError.CHILD_ERRORS);
				}
				
			}
		}
		
		return isoVO.isValid();
	}
	
	public String validateCondition(FieldVO fieldVO) {
		String resultMessage = "";
		
		try {
			String condition = "Object[] BIT = new Object[255];\n";
			condition = condition + fieldVO.getDynaCondition();
			Eval.me(condition);
			
			if (condition.indexOf("BIT[" + fieldVO.getBitNum() + "]") > -1)
				throw new Exception("You cannot look for the same bit value.");
		}
		catch (Exception x) {
			resultMessage = x.getMessage();
		}
		
		return resultMessage;
	}
	
	public String getXmlFilePath() {
		return xmlFilePath;
	}
	
	public DelimiterEnum getDelimiterEnum() {
		return isoDelimiter;
	}
	
	public void setDelimiterEnum(DelimiterEnum isoDelimiter) {
		this.isoDelimiter = isoDelimiter;
	}
	
	public ISO8583Delimiter getDelimiter() {
		return isoDelimiter.getDelimiter();
	}
	
	public MessageVO findMessageVOByPayload(byte[] payload) {
		MessageVO result = null;
		for (int i = 0; i < configTreeNode.getChildCount(); i++) {
			result = (MessageVO) ((DefaultMutableTreeNode) configTreeNode.getChildAt(i)).getUserObject();
			if (result.getType().equals(result.getHeaderEncoding().convert(ISOUtils.subArray(payload, 0, 4))))
				break;
			else
				result = null;
		}
		
		return result;
	}
}

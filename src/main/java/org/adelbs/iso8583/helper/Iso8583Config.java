package org.adelbs.iso8583.helper;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.adelbs.iso8583.constants.DelimiterEnum;
import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.constants.TypeLengthEnum;
import org.adelbs.iso8583.exception.OutOfBoundsException;
import org.adelbs.iso8583.gui.ISOConfigGuiConverter;
import org.adelbs.iso8583.gui.PnlMain;
import org.adelbs.iso8583.gui.xmlEditor.XmlTextPane;
import org.adelbs.iso8583.protocol.ISO8583Delimiter;
import org.adelbs.iso8583.util.ISOUtils;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.GenericIsoVO;
import org.adelbs.iso8583.vo.ISOConfigVO;
import org.adelbs.iso8583.vo.MessageVO;
import org.adelbs.iso8583.xml.ISOConfigMarshaller;
import org.adelbs.iso8583.xml.ISOConfigMarshallerException;
import org.adelbs.iso8583.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import groovy.util.Eval;

public class Iso8583Config {
	
	private static final String XML_FIELD_NODENAME = "field";
	private DefaultMutableTreeNode configTreeNode;
	private XmlTextPane xmlText = new XmlTextPane();
	
    private DelimiterEnum isoDelimiter;
    private EncodingEnum headerEncoding;
    private Integer headerSize;
    
    private boolean TPDU;
	
	//Arquivo de configuracao carregado
	private String xmlFilePath = null;
	
	public Iso8583Config(String fileName) {
		this();
		openFile(null, fileName);
		parseXmlToConfig(null);
	}
	
	public Iso8583Config() {
        isoDelimiter = DelimiterEnum.getDelimiter("");
        headerEncoding = EncodingEnum.getEncoding("");
        headerSize = 0;
        TPDU=false;
		configTreeNode = new DefaultMutableTreeNode("ISO8583");
	}
	
	public DefaultMutableTreeNode getConfigTreeNode() {
		return configTreeNode;
	}
	
	public XmlTextPane getXmlText() {
		return xmlText;
	}
	
	public DefaultMutableTreeNode addType() {
		MessageVO parseVO = new MessageVO("0000", EncodingEnum.UTF8);
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(parseVO);
		configTreeNode.add(newNode);
		return newNode;
	}

	public DefaultMutableTreeNode addField(PnlMain pnlMain, Object node) {
		DefaultMutableTreeNode newNode = null;
		
		if (isAMessageNode(node) || isAFieldNode(node)) {
			newNode = new DefaultMutableTreeNode(new FieldVO(pnlMain, "NewField", "", 2, TypeEnum.ALPHANUMERIC, TypeLengthEnum.FIXED, 1, EncodingEnum.UTF8, ""));
			((DefaultMutableTreeNode) node).add(newNode);
		}
		
		return newNode;
	}

	/**
	 * Check whether this node object is or isn't a {@link FieldVO} element;
	 * @param node {@link GenericIsoVO} object
	 * @return True if its a FieldVO, False otherwise.
	 */
	private boolean isAFieldNode(final Object node) {
		return ((DefaultMutableTreeNode) node).getUserObject() instanceof FieldVO;
	}

	/**
	 * Check whether this node object is or isn't a {@link MessageVO} element;
	 * @param node {@link GenericIsoVO} object
	 * @return True if its a MessageVO, False otherwise.
	 */
	private boolean isAMessageNode(final Object node) {
		return ((DefaultMutableTreeNode) node).getUserObject() instanceof MessageVO;
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
			
			sum += fieldVo.getLength();
			count++;
		}
		
		fieldVo = (FieldVO) ((DefaultMutableTreeNode) node).getUserObject();
		
		if (count > 1)
			fieldVo.setLength(sum);
	}
	
	public void parseConfigToXML() throws ISOConfigMarshallerException {
		final ISOConfigMarshaller xmlParser = ISOConfigMarshaller.creatMarshaller();
		final ISOConfigVO isoConfigVO = ISOConfigGuiConverter.revert(configTreeNode);
        isoConfigVO.setDelimiter(isoDelimiter);
        isoConfigVO.setHeaderEncoding(headerEncoding);
        isoConfigVO.setHeaderSize(headerSize);
        isoConfigVO.setTPDU(TPDU);
		xmlText.setText(xmlParser.marshal(isoConfigVO));
	}
	
	//TODO: Could be substituted by a ISOConfigMarshaller.unmarshal method
	public void parseXmlToConfig(PnlMain pnlMain) {
		try {
			if (!xmlText.getText().trim().equals("")) {
				configTreeNode.removeAllChildren();
				
				Document document = XMLUtils.convertXMLToDOM(xmlText.getText());
				
				DefaultMutableTreeNode lastParseNode;
				
                setDelimiterEnum(DelimiterEnum.getDelimiter(document.getDocumentElement().getAttribute("delimiter")));
                setHeaderEncoding(EncodingEnum.getEncoding(document.getDocumentElement().getAttribute("headerEncoding")));
                
                try {
                    setHeaderSize(Integer.parseInt(document.getDocumentElement().getAttribute("headerSize")));
                }
                catch (Exception x) {
                    setHeaderSize(0);
                }
                
                try {
                    setTPDU(Boolean.parseBoolean(document.getDocumentElement().getAttribute("tpdu")));
                    
                }
                catch (Exception x) {
                	setTPDU(false);
                }
				
				NodeList nodeList = document.getDocumentElement().getChildNodes();
				Node node;
				
				for (int i = 0; i < nodeList.getLength(); i++) {
					node = nodeList.item(i);
					
					if ("message".equalsIgnoreCase(node.getNodeName())) {
						lastParseNode = addType();
						
						((MessageVO) lastParseNode.getUserObject()).setType(ISOUtils.getAttr(node, "type", "0000"));;
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
		MessageVO newMessageVO = null;
		final ISOConfigVO isoConfigVO = ISOConfigGuiConverter.revert(configTreeNode);
		final List<MessageVO> messages = isoConfigVO.getMessageList();
		for (MessageVO messageVO : messages) {
			if (messageVO.getType().equalsIgnoreCase(type)) {
                newMessageVO = messageVO.getInstanceCopy();
                
                newMessageVO.setHeaderEncoding(getHeaderEncoding());
                newMessageVO.setHeaderSize(getHeaderSize());   
                
				break;
			}
		}		
		return newMessageVO;
	}

	private void addFieldsToTree(PnlMain pnlMain, Node domNode, DefaultMutableTreeNode lastParseUINode) {
		NodeList fielNodedList = domNode.getChildNodes();
		for (int j = 0; j < fielNodedList.getLength(); j++) {
			domNode = fielNodedList.item(j);
			addFieldToTree(pnlMain, domNode, lastParseUINode, false);
		}
	}
	
	/**
	 * Add a single Message Field, and its children fields, to the panel tree. 
	 * Only tags {@link XML_FIELD_NODENAME} are considered message fields. All other tags will be disconsidered.
	 * 
	 * @param pnlMain parent panel element.
	 * @param domNode root DOM node with the XML structure of the field we are adding to the panel.
	 * @param lastParseUINode last added UI node element.
	 * @param isSubField indicates that this field is a inner field of a "<i>pre-created</i>" field, enabling nested fields.
	 */
	private void addFieldToTree(final PnlMain pnlMain, Node domNode, DefaultMutableTreeNode lastParseUINode, boolean isSubField){	
		if (XML_FIELD_NODENAME.equalsIgnoreCase(domNode.getNodeName())) {
			DefaultMutableTreeNode newUINode = addField(pnlMain, lastParseUINode);
			FieldVO fieldVo = (FieldVO) newUINode.getUserObject();
			populateField(domNode, fieldVo, newUINode, lastParseUINode, isSubField);
			
			NodeList subFieldNodeList = domNode.getChildNodes();
			if (subFieldNodeList.getLength() > 0) {
				for (int i = 0; i < subFieldNodeList.getLength(); i++) {
					addFieldToTree(pnlMain, subFieldNodeList.item(i), newUINode, true);
				}
				updateSumField(newUINode);
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
    
    public ISO8583Delimiter getDelimiter() {
		return isoDelimiter.getDelimiter();
	}

    public EncodingEnum getHeaderEncoding() {
        return headerEncoding;
    }

    public Integer getHeaderSize() {
        return headerSize;
    }

	public boolean getTPDU() {
		return TPDU;
	}

	public void setDelimiterEnum(DelimiterEnum isoDelimiter) {
		this.isoDelimiter = isoDelimiter;
	}
    
    public void setHeaderEncoding(EncodingEnum headerEncoding) {
        this.headerEncoding = headerEncoding;
    }

    public void setHeaderSize(Integer headerSize) {
        this.headerSize = headerSize;
    }

	public void setTPDU(boolean TPDU) {
		this.TPDU=TPDU;
	}
	
	/**
	 * It returns the MessageVO from the loaded tree, according to what comes from the payload
	 */
	public MessageVO findMessageVOByPayload(byte[] payload) {
		MessageVO result = null;
		try {
			int messageTypeSize = (headerEncoding == EncodingEnum.BCD) ? 2 : 4;
            int calculatedHeaderSize = (headerEncoding == EncodingEnum.BCD) ? (headerSize / 2) : headerSize;
            
            calculatedHeaderSize+=getIfTpdu(payload) ? 10 : 0; //por conta do tpdu
            
            String messageType = headerEncoding.convert(ISOUtils.subArray(payload, calculatedHeaderSize, (calculatedHeaderSize + messageTypeSize)));
            
			for (int i = 0; i < configTreeNode.getChildCount(); i++) {
                result = (MessageVO) ((DefaultMutableTreeNode) configTreeNode.getChildAt(i)).getUserObject();
                
				if (result.getType().equals(messageType))
					break;
				else
					result = null;
			}
		}
		catch (OutOfBoundsException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private boolean getIfTpdu(byte[] payload) {
		//Validate if ther is TPDU in payload between size and message type
		
		//TODO: improve this verification
		try {
			if(headerEncoding.convert(ISOUtils.subArray(payload,0,1)).equals("0") ) { //validate if the 0 of message type is right after the message size byte
				return false;
			}
			else {
				return true;
			}
		}
		catch(Exception ex) {
			return false;
		}
	}
}

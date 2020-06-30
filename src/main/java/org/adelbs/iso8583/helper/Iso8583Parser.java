package org.adelbs.iso8583.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.exception.OutOfBoundsException;
import org.adelbs.iso8583.exception.ParseException;
import org.adelbs.iso8583.helper.listoperations.FieldListMerge;
import org.adelbs.iso8583.helper.listoperations.StructurePriorityMerge;
import org.adelbs.iso8583.helper.listoperations.ValuePriorityMerge;
import org.adelbs.iso8583.protocol.ISOMessage;
import org.adelbs.iso8583.util.ISOUtils;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.ISOTestVO;
import org.adelbs.iso8583.vo.MessageVO;
import org.adelbs.iso8583.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Iso8583Parser {

	private MessageVO messageVO;
	private Iso8583Config2 isoConfig;

	public Iso8583Parser(String configFilePath) {
		this.isoConfig = new Iso8583Config2(configFilePath);
	}

	public Iso8583Parser(Iso8583Config2 isoConfig) {
		this.isoConfig = isoConfig;
	}
	
	public byte[] parseXmlToBytes(String xml) throws ParseException {
		ISOTestVO isoTest = getISOTestVOFromXML(xml);
		if (isoTest != null) this.isoConfig = new Iso8583Config2(isoTest.getConfigFile());
		
		ISOMessage isoMessage = new ISOMessage(updateMessageValuesFromXML(xml));
		return this.isoConfig.getDelimiter().preparePayload(isoMessage, null);
	}
	
	public String parseBytesToXml(byte[] bytes) throws ParseException, OutOfBoundsException {
		
        int messageTypeSize = (this.isoConfig.getHeaderEncoding() == EncodingEnum.BCD) ? 2 : 4;
        int calculatedHeaderSize = (this.isoConfig.getHeaderEncoding() == EncodingEnum.BCD) ? (this.isoConfig.getHeaderSize() / 2) : this.isoConfig.getHeaderSize();
        String messageType = this.isoConfig.getHeaderEncoding().convert(ISOUtils.subArray(bytes, calculatedHeaderSize, (calculatedHeaderSize + messageTypeSize)));
        
		try {
			ISOMessage isoMessage = new ISOMessage(bytes, this.isoConfig.getMessageByType(messageType));
			this.messageVO = isoMessage.getMessageVO();
		}
		catch (ParseException x) {
			x.printStackTrace();
			throw x;
		}

		return getXML();
	}

	public MessageVO parseXmlToMessageVO(String xml) throws ParseException {
		return buildMessageStructureFromXML(xml);
	}
	
	
	//PAYLOADCONFIG INICIO **************************************************************************************************************
	
	private String getXML() {
		StringBuilder xmlMessage = new StringBuilder();
		
		xmlMessage.append("<?xml version=\"1.0\" ?>\n\n ");
		xmlMessage.append("<document>\n\n");

		if (messageVO != null) {
			xmlMessage.append("<message type=\"").append(messageVO.getType()).append("\" header=\"").append(messageVO.getHeader()).append("\">");
			
			for (FieldVO fieldVO : messageVO.getFieldList())
				xmlMessage.append(fieldVO.getXML());
				
			xmlMessage.append("\n</message>");
		}
		
		xmlMessage.append("\n\n</document>");

		return xmlMessage.toString();
	}
	
	private static NodeList convertToDOMNodes(final String xml) throws ParserConfigurationException, SAXException, IOException {
		final Document document = XMLUtils.convertXMLToDOM(xml);
		return document.getDocumentElement().getChildNodes();
	}
	
	private MessageVO buildMessageStructureFromXML(final String xml) throws ParseException{
		return buildMessagesFromXML(xml, new ValuePriorityMerge());
	}
	
	private MessageVO updateMessageValuesFromXML(final String xml) throws ParseException{
		return buildMessagesFromXML(xml, new StructurePriorityMerge());
	}
	
	private MessageVO buildMessagesFromXML(final String xml, final FieldListMerge fieldListMege) throws ParseException{
		try {
			MessageVO newMessageVO = null;
			final NodeList nodeList = convertToDOMNodes(xml);
			for (int i = 0; i < nodeList.getLength(); i++) {
				final Node node = nodeList.item(i);
				if ("message".equalsIgnoreCase(node.getNodeName())) {
					
                    newMessageVO = this.isoConfig.getMessageByType(ISOUtils.getAttr(node, "type", "")).getInstanceCopy();
                    
                    String headerValue = ISOUtils.getAttr(node, "header", "");
                    if (headerValue != null) {
                        if (headerValue.length() > this.isoConfig.getHeaderSize()) {
                            newMessageVO.setHeader(headerValue.substring(0, this.isoConfig.getHeaderSize()));
                        }
                        else {
                            newMessageVO.setHeader(headerValue);
                        }
                    }

                    newMessageVO.setHeaderEncoding(this.isoConfig.getHeaderEncoding());
                    newMessageVO.setHeaderSize(this.isoConfig.getHeaderSize());

                    String tpduValue = ISOUtils.getAttr(node, "tpdu", "");
                    if (tpduValue != null) {
                        if (tpduValue.length() > 10) {
                            newMessageVO.setTPDUValue(tpduValue.substring(0, 10));
                        }
                        else {
                            newMessageVO.setTPDUValue(tpduValue);
                        }
                    }
                    
					final ArrayList<FieldVO> messageFieldList = newMessageVO.getFieldList();
					final ArrayList<FieldVO> xmlFieldList = getFieldsFromXML(node.getChildNodes(), messageFieldList);
					final List<FieldVO> newFieldList = fieldListMege.merge(messageFieldList, xmlFieldList);
					newMessageVO.setFieldList((ArrayList<FieldVO>)newFieldList);
					break;
				}
			}
			return newMessageVO;
		}
		catch(SAXException | IOException | ParserConfigurationException e){
			throw new ParseException(e.getMessage());
		}
	}
	
	private ISOTestVO getISOTestVOFromXML(String xml) throws ParseException {
		ISOTestVO testVO = null;
		
		try {
			if (xml.indexOf("test-iso") > -1) {
				NodeList nodeList = convertToDOMNodes(xml);
				
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					
					if ("test-iso".equalsIgnoreCase(node.getNodeName()))
						testVO = new ISOTestVO(ISOUtils.getAttr(node, "config-file", ""));
				}
			}
		}
		catch (Exception x) {
			throw new ParseException("Error parsing the XML.\n\n" + x.getMessage());
		}
		
		return testVO;
	}
	
	private ArrayList<FieldVO> getFieldsFromXML(NodeList fieldNodeList, ArrayList<FieldVO> treeFieldList) throws ParseException {
		ArrayList<FieldVO> fieldList = new ArrayList<FieldVO>();
		
		int bitNum = 0;
		FieldVO fieldFound;
		FieldVO newFieldVO;
		Node node;
		
		for (int j = 0; j < fieldNodeList.getLength(); j++) {
			node = fieldNodeList.item(j);
			
			if ("bit".equalsIgnoreCase(node.getNodeName())) {
				bitNum = Integer.parseInt(ISOUtils.getAttr(node, "num", ""));
				fieldFound = null;
				
				if (treeFieldList == null || treeFieldList.size() == 0)
					throw new ParseException("It was not possible to parse the XML. At the XML it appears to have children, but there is not children at the ISO structure.");

				for (FieldVO field : treeFieldList) {
					if (field.getBitNum().intValue() == bitNum) {
						fieldFound = field;
						break;
					}
				}
				
				if (fieldFound == null){
					throw new ParseException("It was not possible to parse the XML. The bit number was not found at the ISO structure.");
				}
					
				newFieldVO = fieldFound.getInstanceCopy();
				
				newFieldVO.setPresent(true);
				newFieldVO.setTlvType(ISOUtils.getAttr(node, "tag", ""));
				newFieldVO.setTlvLength(ISOUtils.getAttr(node, "length", ""));
				newFieldVO.setValue(ISOUtils.getAttr(node, "value", ""));
				
				if (node.getChildNodes().getLength() > 0){
					newFieldVO.setFieldList(getFieldsFromXML(node.getChildNodes(), newFieldVO.getFieldList()));
				}
					
				fieldList.add(newFieldVO);
			}
		}
		
		return fieldList;
	}
	
	//PAYLOADCONFIG FIM **************************************************************************************************************
}

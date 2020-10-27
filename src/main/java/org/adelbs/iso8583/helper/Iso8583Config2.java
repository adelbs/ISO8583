package org.adelbs.iso8583.helper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import org.adelbs.iso8583.constants.DelimiterEnum;
import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.constants.TypeLengthEnum;
import org.adelbs.iso8583.protocol.ISO8583Delimiter;
import org.adelbs.iso8583.util.ISOUtils;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.GenericIsoVO;
import org.adelbs.iso8583.vo.MessageVO;
import org.adelbs.iso8583.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Iso8583Config2 {

	private HashMap<String, MessageVO> messageMap;
	
	private String xmlText = "";
	
    private DelimiterEnum isoDelimiter;
    private EncodingEnum headerEncoding;
    private Integer headerSize;
    
    private boolean TPDU=false;
    private boolean StxEtx=false;
	
    public Iso8583Config2(String configFilePath) {
    	openFile(configFilePath);
    }
    
	private void updateSumField(FieldVO fieldVo) {
		String name = fieldVo.getName();
		int sum = 0;
		int count = 1;
		
		for (int i = 0; i < fieldVo.getFieldList().size(); i++) {
			fieldVo.getFieldList().get(i).setName(name + count);
			sum += fieldVo.getFieldList().get(i).getLength();
			count++;
		}
		
		if (count > 1)
			fieldVo.setLength(sum);
	}
	
	private void parseXmlToConfig() {
		try {
			if (!xmlText.trim().equals("")) {
				
				messageMap.clear();
				
				Document document = XMLUtils.convertXMLToDOM(xmlText);
				
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
				
                try {
                    setStxEtx(Boolean.parseBoolean(document.getDocumentElement().getAttribute("stxetx")));
                    
                }
                catch (Exception x) {
                	System.out.println("(error) on setting stxetx");
                	setStxEtx(false);
                }
				
				NodeList nodeList = document.getDocumentElement().getChildNodes();
				Node node;
				
				for (int i = 0; i < nodeList.getLength(); i++) {
					node = nodeList.item(i);
					
					if ("message".equalsIgnoreCase(node.getNodeName())) {
						
						MessageVO messageVO = new MessageVO(
								ISOUtils.getAttr(node, "type", "0000"), 
								EncodingEnum.getEncoding(ISOUtils.getAttr(node, "bitmap-encoding", "")));
						
	                    messageVO.setHeaderEncoding(getHeaderEncoding());
	                    messageVO.setHeaderSize(getHeaderSize());
	                    
						addFieldsToMessageVO(node, messageVO);
						
						messageMap.put(messageVO.getType(), messageVO);
					}
				}
			}
		}
		catch (Exception x) {
			x.printStackTrace();
		}
	}

	private void addFieldsToMessageVO(Node domNode, MessageVO messageVO) {
		NodeList fielNodedList = domNode.getChildNodes();
		for (int j = 0; j < fielNodedList.getLength(); j++) {
			domNode = fielNodedList.item(j);
			addFieldToVO(domNode, messageVO, false);
		}
	}
	
	private void addFieldToVO(Node domNode, GenericIsoVO vo, boolean isSubField) {
		if ("field".equalsIgnoreCase(domNode.getNodeName())) {
			
			FieldVO fieldVo = new FieldVO(null, "NewField", "", 2, TypeEnum.ALPHANUMERIC, TypeLengthEnum.FIXED, 1, EncodingEnum.UTF8, "");
			populateField(domNode, fieldVo, isSubField);
			
			NodeList subFieldNodeList = domNode.getChildNodes();
			if (subFieldNodeList.getLength() > 0) {
				for (int i = 0; i < subFieldNodeList.getLength(); i++) {
					addFieldToVO(subFieldNodeList.item(i), fieldVo, true);
				}
				
				updateSumField(fieldVo);
			}
			
			vo.getFieldList().add(fieldVo);
		}
	}
	
	private void populateField(Node node, FieldVO fieldVo, boolean isSubfield) {
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
	
	private void openFile(String file) {
        isoDelimiter = DelimiterEnum.LENGTH2_DELIMITER_BEG;
        headerEncoding = EncodingEnum.UTF8;
        headerSize = 0;
        
		messageMap = new HashMap<String, MessageVO>();
		
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
		    xmlText = sb.toString();
		} 
		catch (Exception x) {
			x.printStackTrace();
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
		
		parseXmlToConfig();
	}
    
	public EncodingEnum getHeaderEncoding() {
        return headerEncoding;
    }

	public Integer getHeaderSize() {
        return headerSize;
    }
	
	public boolean getTPDU() {
		return this.TPDU;
	}

	public boolean getStxEtx() {
		return this.StxEtx;
	}
	
	private void setDelimiterEnum(DelimiterEnum isoDelimiter) {
		this.isoDelimiter = isoDelimiter;
	}
    
	private void setHeaderEncoding(EncodingEnum headerEncoding) {
        this.headerEncoding = headerEncoding;
    }

	private void setHeaderSize(Integer headerSize) {
        this.headerSize = headerSize;
    }

	public void setTPDU(boolean TPDU) {
		this.TPDU=TPDU;
	}
	
	public void setStxEtx(boolean StxEtx) {
		this.StxEtx=StxEtx;
	}

	public ISO8583Delimiter getDelimiter() {
		return isoDelimiter.getDelimiter();
	}

	public MessageVO getMessageByType(String type) {
		return messageMap.get(type);
	}
}

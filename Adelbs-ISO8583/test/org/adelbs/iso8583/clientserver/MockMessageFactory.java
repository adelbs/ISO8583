package org.adelbs.iso8583.clientserver;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.constants.TypeLengthEnum;
import org.adelbs.iso8583.helper.Iso8583Config;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.MessageVO;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class MockMessageFactory {
	
	private MockMessageFactory(){}
	
	public static MessageVO createMockMessage(final String messageType, final Iso8583Config isoConfig) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException{
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		final Document configDoc = builder.parse(isoConfig.getXmlFilePath());
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("/iso8583/message[@type='"+messageType+"']");
		NodeList nl = (NodeList) expr.evaluate(configDoc, XPathConstants.NODESET);
		
		if(nl.getLength() > 0){
			final Node messageNode = nl.item(0);
			
			final MessageVO message = new MessageVO();
			message.setType(messageType);
			final String bitmapEncode = messageNode.getAttributes().getNamedItem("bitmap-encoding").getNodeValue();
			message.setBitmatEncoding(EncodingEnum.getEncoding(bitmapEncode));
			final String headerEncoding = messageNode.getAttributes().getNamedItem("header-encoding").getNodeValue();
			message.setHeaderEncoding(EncodingEnum.getEncoding(headerEncoding));
			
			final NodeList fieldNodes = messageNode.getChildNodes();
			for(int i=0; i<fieldNodes.getLength();i++){
				final Node fieldNode = fieldNodes.item(i);
				final String name = fieldNode.getAttributes().getNamedItem("name").getNodeValue();
				final String bitnum = fieldNode.getAttributes().getNamedItem("bitnum").getNodeValue();
				final String condition = fieldNode.getAttributes().getNamedItem("condition").getNodeValue();
				final String lenghtType = fieldNode.getAttributes().getNamedItem("length-type").getNodeValue();
				final String length = fieldNode.getAttributes().getNamedItem("length").getNodeValue();
				final String type = fieldNode.getAttributes().getNamedItem("type").getNodeValue();
				final String encoding = fieldNode.getAttributes().getNamedItem("encoding").getNodeValue();
				
				final FieldVO fieldVO = new FieldVO();
				fieldVO.setName(name);
				fieldVO.setBitNum(Integer.parseInt(bitnum));
				fieldVO.setDynaCondition(condition);
				fieldVO.setLength(Integer.parseInt(length));
				fieldVO.setType(TypeEnum.valueOf(type));
				fieldVO.setEncoding(EncodingEnum.getEncoding(encoding));
				fieldVO.setTypeLength(TypeLengthEnum.getTypeLength(lenghtType));
				fieldVO.setPresent(true);
				
				message.getFieldList().add(fieldVO);
			}
			
			return message;
		}
		
		throw new IllegalArgumentException("No message created");
	}
}

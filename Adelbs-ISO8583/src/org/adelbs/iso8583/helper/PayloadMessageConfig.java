package org.adelbs.iso8583.helper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.exception.ParseException;
import org.adelbs.iso8583.gui.PnlGuiPayload;
import org.adelbs.iso8583.gui.PnlMain;
import org.adelbs.iso8583.protocol.ISOMessage;
import org.adelbs.iso8583.util.ISOUtils;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.ISOTestVO;
import org.adelbs.iso8583.vo.MessageVO;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class PayloadMessageConfig {

	private MessageVO messageVO;
	
	private ArrayList<GuiPayloadField> fieldList;

	private int numLines;
	private JPanel pnlFields;

	private ISOMessage isoMessage;
	
	//runtime
	private Iso8583Config isoConfig;
	private ISOTestVO isoTest;
	private String runTimeXML;
	
	public PayloadMessageConfig(String xml) throws ParseException {
		this.runTimeXML = xml;
		this.isoTest = getISOTestVOFromXML(xml);
		this.isoConfig = new Iso8583Config(isoTest.getConfigFile());
		this.pnlFields = new JPanel();
	}
	
	public PayloadMessageConfig(Iso8583Config isoConfig) {
		this.pnlFields = new JPanel();
		this.isoConfig = isoConfig;
	}
	
	public PayloadMessageConfig(Iso8583Config isoConfig, JPanel pnlFields) {
		this.pnlFields = pnlFields;
		this.isoConfig = isoConfig;
	}

	public void setMessageVO(String messageType) {
		setMessageVO(isoConfig.getMessageVOAtTree(messageType));
	}
	
	public void setMessageVO(MessageVO messageVO) {
		if (messageVO != null) {
			this.messageVO = messageVO.getInstanceCopy();
			this.messageVO.setFieldList(new ArrayList<FieldVO>());
			
			pnlFields.removeAll();
			numLines = 0;
			fieldList = new ArrayList<GuiPayloadField>();
			
			GuiPayloadField payloadField;
			
			for (FieldVO fieldVO : messageVO.getFieldList()) {
				payloadField = addLine(fieldVO);
				payloadField.setValues(fieldVO);
				for (FieldVO subFieldVO : fieldVO.getFieldList()) {
					payloadField = addSubline(subFieldVO);
					payloadField.setValues(subFieldVO);
				}
			}
		}
	}

	public String getXML() {
		return getXML(null);
	}
	
	public String getXML(PnlMain pnlMain) {
		StringBuilder xmlMessage = new StringBuilder();
		
		xmlMessage.append("<?xml version=\"1.0\" ?>\n\n ");
		xmlMessage.append("<document>\n\n");
		
		if (pnlMain != null)
			isoTest = new ISOTestVO(pnlMain.getTxtFilePath().getText());

		if (isoTest != null)
			xmlMessage.append(isoTest.toXML(false));

		if (messageVO != null) {
			xmlMessage.append("<message type=\"").append(messageVO.getType()).append("\">");
			
			for (GuiPayloadField payloadField : fieldList)
				xmlMessage.append(payloadField.getXML());
				
			xmlMessage.append("\n</message>");
		}
		
		xmlMessage.append("\n\n</document>");

		return xmlMessage.toString();
	}
	
	public MessageVO getMessageVO() throws ParseException {
		MessageVO messageVO = getMessageVOFromXML(runTimeXML);
		setMessageVO(messageVO);
		return messageVO;
	}
	
	public MessageVO getMessageVOFromXML(String xml) throws ParseException {
		MessageVO newMessageVO = null;
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
		    InputSource is = new InputSource(new StringReader(xml));
			
			Document document = builder.parse(is);
			NodeList nodeList = document.getDocumentElement().getChildNodes();
			Node node;

			ArrayList<FieldVO> fieldsFromXML = new ArrayList<FieldVO>();
			ArrayList<FieldVO> newFieldList = new ArrayList<FieldVO>();
			FieldVO newFieldVO;
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				node = nodeList.item(i);
				
				if ("message".equalsIgnoreCase(node.getNodeName())) {

					newMessageVO = isoConfig.getMessageVOAtTree(ISOUtils.getAttr(node, "type", "")).getInstanceCopy();
					fieldsFromXML = getFieldsFromXML(node.getChildNodes(), newMessageVO.getFieldList());
					
					//Carregando o valor dos campos
					for (FieldVO fieldVO : newMessageVO.getFieldList()) {
						newFieldVO = fieldVO;
						for (FieldVO fieldXMLVO : fieldsFromXML) {
							if (fieldVO.getBitNum().intValue() == fieldXMLVO.getBitNum().intValue()) {
								newFieldVO = fieldXMLVO;
							}
						}
						
						newFieldList.add(newFieldVO);
					}
					
					newMessageVO.setFieldList(newFieldList);
					
					break;
				}
			}
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		
		return newMessageVO;
	}
	
	public int getNumLines() {
		return numLines;
	}
	
	private GuiPayloadField addLine(FieldVO fieldVO) {
		GuiPayloadField newPayloadField = new GuiPayloadField(fieldVO, null);
		fieldList.add(newPayloadField);
		return newPayloadField;
	}

	private GuiPayloadField addSubline(FieldVO fieldVO) {
		GuiPayloadField newPayloadField = null;
		if (fieldList.size() > 0)
			newPayloadField = fieldList.get(fieldList.size() - 1).addSubline(fieldVO, fieldList.get(fieldList.size() - 1).getFieldVO());
		return newPayloadField;
	}

	public void setISOTestVO(ISOTestVO isoTestVO) {
		this.isoTest = isoTestVO;
	}

	public ISOTestVO getISOTestVO() {
		return isoTest;
	}
	
	public ISOTestVO getISOTestVOFromXML(String xml) throws ParseException {
		ISOTestVO testVO = new ISOTestVO("");
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
		    InputSource is = new InputSource(new StringReader(xml));
		    
			Document document = builder.parse(is);
			NodeList nodeList = document.getDocumentElement().getChildNodes();
			Node node;
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				node = nodeList.item(i);
				
				if ("test-iso".equalsIgnoreCase(node.getNodeName()))
					testVO.setConfigFile(ISOUtils.getAttr(node, "config-file", ""));
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
				
				if (fieldFound == null)
					throw new ParseException("It was not possible to parse the XML. The bit number was not found at the ISO structure.");

				newFieldVO = fieldFound.getInstanceCopy();
				
				newFieldVO.setPresent(true);
				newFieldVO.setTlvType(ISOUtils.getAttr(node, "tag", ""));
				newFieldVO.setTlvLength(ISOUtils.getAttr(node, "length", ""));
				newFieldVO.setValue(ISOUtils.getAttr(node, "value", ""));
				
				if (node.getChildNodes().getLength() > 0)
					newFieldVO.setFieldList(getFieldsFromXML(node.getChildNodes(), newFieldVO.getFieldList()));
				
				fieldList.add(newFieldVO);
			}
		}
		
		return fieldList;
	}
	
	public void updateFromPayload(byte[] bytes) throws ParseException {
		setMessageVO(isoConfig.findMessageVOByPayload(bytes).getType());
		updateFromPayload(null, bytes);
	}
	
	public void updateFromMessageVO() throws ParseException {
		isoMessage = new ISOMessage(messageVO);
	}
	
	public void updateFromPayload(PnlMain pnlMain, byte[] bytes) throws ParseException {
		try {
			isoMessage = new ISOMessage(bytes, messageVO);
			setMessageVO(isoMessage.getMessageVO());
		}
		catch (ParseException x) {
			x.printStackTrace();
			if (pnlMain != null)
				JOptionPane.showMessageDialog(pnlMain, "It was not possible to parse this payload. Certify that the message structure was not changed.\n" + x.getMessage());
			else
				throw x;
		}
	}
	
	public ISOMessage getIsoMessage() {
		return isoMessage;
	}
	
	public void setReadOnly() {
		for (GuiPayloadField field : fieldList) {
			field.setReadOnly();
			for (GuiPayloadField field2 : field.subfieldList) {
				field2.setReadOnly();	
			}
		}
	}
	
	private class GuiPayloadField {
		
		boolean isSubfield;
		private int lineNum;
		private FieldVO fieldVO;
		private FieldVO superFieldVO;
		
		private JCheckBox ckBox;
		private JTextField txtType;
		private JTextField txtLength;
		private JTextField txtValue;
		
		private JLabel lblFieldNum;
		private JLabel lblFieldName;
		private JLabel lblType;
		private JLabel lblDynamic;
		
		private ArrayList<GuiPayloadField> subfieldList;

		private KeyListener saveFieldPayloadAction = new KeyListener() {
			public void keyTyped(KeyEvent e) { }
			public void keyReleased(KeyEvent e) {
				saveFieldValue();
			}
			public void keyPressed(KeyEvent e) { }
		};

		private GuiPayloadField(FieldVO fieldVO, FieldVO superfieldVO) {

			this.isSubfield = (superfieldVO != null);
			this.superFieldVO = superfieldVO;
			this.fieldVO = fieldVO.getInstanceCopy();
			this.fieldVO.setFieldList(new ArrayList<FieldVO>());
			
			if (isSubfield)
				superFieldVO.getFieldList().add(this.fieldVO);
			else
				messageVO.getFieldList().add(this.fieldVO);
			
			ckBox = new JCheckBox();
			txtType = new JTextField();
			txtLength = new JTextField();
			txtValue = new JTextField();
			subfieldList = new ArrayList<GuiPayloadField>();
			
			lblFieldNum = new JLabel(fieldVO.getBitNum().toString());
			lblFieldName = new JLabel(fieldVO.getName());
			lblType = new JLabel(fieldVO.getType().toString());
			lblDynamic = new JLabel();
			lblDynamic.setIcon(new ImageIcon(PnlGuiPayload.class.getResource("/org/adelbs/iso8583/resource/search.png")));
			lblDynamic.setToolTipText(fieldVO.getDynaCondition());

			lineNum = numLines;
			numLines++;
			
			ckBox.setBounds(10, 10 + (lineNum * 25), 22, 22);
			lblFieldNum.setBounds(40, 10 + (lineNum * 25), 50, 22);
			lblFieldName.setBounds(80, 10 + (lineNum * 25), 100, 22);
			lblType.setBounds(470, 10 + (lineNum * 25), 100, 22);
			lblDynamic.setBounds(600, 10 + (lineNum * 25), 50, 22);
			
			if (fieldVO.getType() == TypeEnum.ALPHANUMERIC) {
				txtValue.setBounds(190, 10 + (lineNum * 25), 260, 22);
			}
			else if (fieldVO.getType() == TypeEnum.TLV) {
				txtType.setBounds(190, 10 + (lineNum * 25), 80, 22);
				txtLength.setBounds(280, 10 + (lineNum * 25), 80, 22);
				txtValue.setBounds(370, 10 + (lineNum * 25), 80, 22);
				
				pnlFields.add(txtType);
				pnlFields.add(txtLength);
			}
			
			if (!isSubfield)
				pnlFields.add(ckBox);
				
			pnlFields.add(lblFieldNum);
			pnlFields.add(lblFieldName);
			pnlFields.add(txtValue);
			pnlFields.add(lblType);

			txtType.addKeyListener(saveFieldPayloadAction);
			txtLength.addKeyListener(saveFieldPayloadAction);
			txtValue.addKeyListener(saveFieldPayloadAction);
			
			if (!fieldVO.getDynaCondition().equals("") && !fieldVO.getDynaCondition().equals("true"))
				pnlFields.add(lblDynamic);
			
			if (fieldVO.getDynaCondition().equals("true")) {
				ckBox.setSelected(true);
				ckBox.setEnabled(false);
				ckBoxClick(ckBox);
				
				setEnabled(true);
			}
			else {
				ckBox.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						ckBoxClick((JCheckBox) e.getSource());
						saveFieldValue();
					}
				});
				
				setEnabled(false);
			}
		}
		
		private void setValues(FieldVO fromFieldVO) {
			
			if (superFieldVO != null)
				ckBox.setSelected(superFieldVO.isPresent() || superFieldVO.getDynaCondition().equals("true"));
			else
				ckBox.setSelected(fromFieldVO.isPresent() || fromFieldVO.getDynaCondition().equals("true"));
			
			ckBoxClick(ckBox);

			if (ckBox.isSelected()) {
				txtType.setText(fromFieldVO.getTlvType());
				txtLength.setText(fromFieldVO.getTlvLength());
				txtValue.setText(fromFieldVO.getValue());
			}
			else {
				txtType.setText("");
				txtLength.setText("");
				txtValue.setText("");
			}
			
			updateFieldVO();
		}
		
		private void saveFieldValue() {
			fieldVO.setPresent(ckBox.isSelected());
			fieldVO.setTlvType(txtType.getText());
			fieldVO.setTlvLength(txtLength.getText());
			fieldVO.setValue(txtValue.getText());
		}
		
		private void ckBoxClick(JCheckBox ckBox) {
			setEnabled(ckBox.isSelected());
			txtValue.setText("");
			for (GuiPayloadField subfield : subfieldList) {
				subfield.setEnabled(ckBox.isSelected());
				subfield.txtValue.setText("");
			}
		}
		
		private GuiPayloadField addSubline(FieldVO fieldVO, FieldVO superfieldVO) {
			GuiPayloadField newPayloadField;
			if (superfieldVO != null && superfieldVO.getType() != TypeEnum.TLV) {
				pnlFields.remove(txtValue);
				pnlFields.remove(lblType);
			}
			
			newPayloadField = new GuiPayloadField(fieldVO, superfieldVO);
			subfieldList.add(newPayloadField);
			return newPayloadField;
		}
		
		private void setReadOnly() {
			setEnabled(false);
			ckBox.setEnabled(false);
			
			txtType.setEnabled(true);
			txtLength.setEnabled(true);
			txtValue.setEnabled(true);
			
			txtType.setEditable(false);
			txtLength.setEditable(false);
			txtValue.setEditable(false);
		}
		
		private void setEnabled(boolean enabled) {
			txtType.setEnabled(enabled);
			txtLength.setEnabled(enabled);
			txtValue.setEnabled(enabled);
			lblFieldNum.setEnabled(enabled);
			lblFieldName.setEnabled(enabled);
			lblType.setEnabled(enabled);
			lblDynamic.setEnabled(enabled);
		}
		
		private String getType() {
			return txtType.getText();
		}
		
		private String getLength() {
			return txtLength.getText();
		}
		
		private String getValue() {
			return txtValue.getText();
		}
			
		private StringBuilder getXML() {
			StringBuilder xmlField = new StringBuilder();
			String tabs = isSubfield ? "\t\t" : "\t";
			
			if (isSubfield || ckBox.isSelected()) {
				boolean hasSubfield = false;
				
				xmlField.append("\n").append(tabs).append("<bit num=\"").append(fieldVO.getBitNum()).append("\"");

				if (fieldVO.getType() == TypeEnum.TLV) {
					xmlField.append(" tag=\"").append(getType()).
							append("\" length=\"").append(getLength()).append("\"").
							append(" value=\"").append(getValue()).append("\"");
				}

				for (GuiPayloadField subfield : subfieldList) {
					if (!hasSubfield) xmlField.append(">");
					xmlField.append(subfield.getXML());
					hasSubfield = true;
				}
				
				if (hasSubfield) 
					xmlField.append("\n\t</bit>");
				else {
					if (fieldVO.getType() != TypeEnum.TLV)
						xmlField.append(" value=\"").append(getValue()).append("\"/>");
					else
						xmlField.append("/>");
				}
			}
			
			return xmlField;
		}

		private void updateFieldVO() {
			if (ckBox.isSelected()) {
				fieldVO.setPresent(true);
				fieldVO.setTlvType(txtType.getText());
				fieldVO.setTlvLength(txtLength.getText());
				fieldVO.setValue(txtValue.getText());
			}
			else {
				fieldVO.setPresent(false);
				fieldVO.setTlvType("");
				fieldVO.setTlvLength("");
				fieldVO.setValue("");
			}
		}
		
		private FieldVO getFieldVO() {
			updateFieldVO();
			return fieldVO;
		}
	}
	
	public Iso8583Config getIsoConfig() {
		return isoConfig;
	}
}

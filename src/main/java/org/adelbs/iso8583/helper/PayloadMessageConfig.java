package org.adelbs.iso8583.helper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.parsers.ParserConfigurationException;

import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.exception.ParseException;
import org.adelbs.iso8583.gui.PnlGuiPayload;
import org.adelbs.iso8583.gui.PnlMain;
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
		updateFromXML(xml);
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
			numLines = 0;
			pnlFields.removeAll();
			fieldList = new ArrayList<GuiPayloadField>();
			
			
			//Add Field lists of this message
			for (FieldVO fieldVO : messageVO.getFieldList()) {
				GuiPayloadField newPayloadField = new GuiPayloadField(fieldVO, null, "");
				fieldList.add(newPayloadField);
				newPayloadField.setValues(fieldVO);
				setSubFieldsVO(fieldVO.getFieldList(), newPayloadField, "");
			}
		}
	}
	
	/**
	 * Iteratively adds all fields and subfields to the Panel
	 * @param fields List of {@link FieldVO) to be added to the Panel
	 * @param payloadField GUI panel that will receive all lines
	 */
	protected void setSubFieldsVO(final List<FieldVO> fields, final GuiPayloadField parentPlayloadField, String superFieldBitNum){
		for (final FieldVO fieldVO : fields) {
			GuiPayloadField innerFieldPayload = parentPlayloadField.addSubline(fieldVO, parentPlayloadField.getFieldVO(), superFieldBitNum +"["+ parentPlayloadField.getFieldVO().getBitNum() +"]");
			innerFieldPayload.setValues(fieldVO);
			setSubFieldsVO(fieldVO.getFieldList(), innerFieldPayload, superFieldBitNum +"["+ parentPlayloadField.getFieldVO().getBitNum() +"]");
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
			xmlMessage.append("<message type=\"").append(messageVO.getType()).append("\" header=\"").append(messageVO.getHeader()).append("\"").append(xmlIfTpdu()).append(">");
			
			for (GuiPayloadField payloadField : fieldList)
				xmlMessage.append(payloadField.getXML(1));
				
			xmlMessage.append("\n</message>");
		}
		
		xmlMessage.append("\n\n</document>");

		return xmlMessage.toString();
	}
	
	private String xmlIfTpdu() {
		StringBuilder sbResult = new StringBuilder();
		
		if(!messageVO.getTPDUValue().isEmpty()) {
			sbResult.append(" tpdu=\"").append(messageVO.getTPDUValue()).append("\"");
		} 
		
		return sbResult.toString();
	}
	
	public MessageVO getMessageVO() throws ParseException {
		MessageVO messageVO = updateMessageValuesFromXML(runTimeXML);
		setMessageVO(messageVO);
		return messageVO;
	}
    
    public void setHeaderValue(String headerValue) {
        
        this.messageVO.setHeaderEncoding(isoConfig.getHeaderEncoding());
        this.messageVO.setHeaderSize(isoConfig.getHeaderSize());
        
        if (headerValue != null) {
            if (headerValue.length() > isoConfig.getHeaderSize()) {
                this.messageVO.setHeader(headerValue.substring(0, isoConfig.getHeaderSize()));
            }
            else {
                this.messageVO.setHeader(headerValue);
            }
        }
    }
    
    /**/
    //public void setTPDU(String tpdu) throws ParseException {
    public void setTPDUValue(String tpdu) {
        
        if (tpdu != null) {
            if (tpdu.length() != 10) {
            	this.messageVO.setTPDUValue(this.messageVO.getTPDUValue());
                //throw new ParseException("Invalid TPDU. This need to be a 10 bytes sized value.");
            }
            else {
                this.messageVO.setTPDUValue(tpdu);
            }
        }
    }
    /**/
		
	private static NodeList convertToDOMNodes(final String xml) throws ParserConfigurationException, SAXException, IOException {
		final Document document = XMLUtils.convertXMLToDOM(xml);
		return document.getDocumentElement().getChildNodes();
	}
	
	/**
	 * Creates a {@link MessageVO} where its list of Fields will be created from the XML:
	 * structure o fields, types, values, etc, will be create based on the <bit> tags
	 * 
	 * @param xml Message XML from Request/Response panel
	 * @return {@link MessageVO} updated with a list of Field from the XML panel
	 * @throws ParseException
	 */
	public MessageVO buildMessageStructureFromXML(final String xml) throws ParseException{
		return buildMessagesFromXML(xml, new ValuePriorityMerge());
	}
	
	/**
	 * Creates a {@link MessageVO} where its list of Fields will be update with values from the XML panel.
	 * But will maintain it's original structure of fields, even if it's not defined at the XML structure.
	 * 
	 * @param xml Message XML from Request/Response panel
	 * @return {@link MessageVO} with it's original field list, but with values updated from the xml
	 * @throws ParseException
	 */
	public MessageVO updateMessageValuesFromXML(final String xml) throws ParseException{
		return buildMessagesFromXML(xml, new StructurePriorityMerge());
	}
	
	public MessageVO buildMessagesFromXML(final String xml, final FieldListMerge fieldListMege) throws ParseException{
		try{
			MessageVO newMessageVO = null;
			final NodeList nodeList = convertToDOMNodes(xml);
			for (int i = 0; i < nodeList.getLength(); i++) {
				final Node node = nodeList.item(i);
				if ("message".equalsIgnoreCase(node.getNodeName())) {
                    newMessageVO = isoConfig.getMessageVOAtTree(ISOUtils.getAttr(node, "type", "")).getInstanceCopy();
                    
                    String headerValue = ISOUtils.getAttr(node, "header", "");
                    if (headerValue != null) {
                        if (headerValue.length() > isoConfig.getHeaderSize()) {
                            newMessageVO.setHeader(headerValue.substring(0, isoConfig.getHeaderSize()));
                        }
                        else {
                            newMessageVO.setHeader(headerValue);
                        }
                    }

                    newMessageVO.setHeaderEncoding(isoConfig.getHeaderEncoding());
                    newMessageVO.setHeaderSize(isoConfig.getHeaderSize());

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
	
	public int getNumLines() {
		return numLines;
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
			NodeList nodeList = convertToDOMNodes(xml);
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				
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
	
	public void updateFromXML(String xml) throws ParseException {
		this.runTimeXML = xml;
		this.isoTest = getISOTestVOFromXML(xml);
		this.isoConfig = new Iso8583Config(isoTest.getConfigFile());
		this.pnlFields = new JPanel();
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
		setFieldListsReadOnly(fieldList);
	}
	
	public static void setFieldListsReadOnly(final List<GuiPayloadField> fieldList){
		fieldList.forEach(field->{
			field.setReadOnly();
			setFieldListsReadOnly(field.subfieldList);
		});
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

		private GuiPayloadField(FieldVO fieldVO, FieldVO superfieldVO, String superFieldBitNum) {

			this.isSubfield = (superfieldVO != null);
			this.superFieldVO = superfieldVO;
			this.fieldVO = fieldVO.getInstanceCopy();
			this.fieldVO.setFieldList(new ArrayList<FieldVO>());
			
			if (isSubfield) {
				superFieldVO.getFieldList().add(this.fieldVO);
            }
            else {
				messageVO.getFieldList().add(this.fieldVO);
			}
			
			ckBox = new JCheckBox();
			txtType = new JTextField();
			txtLength = new JTextField();
			txtValue = new JTextField();
			subfieldList = new ArrayList<GuiPayloadField>();
			
			lblFieldNum = new JLabel(superFieldBitNum +"["+ fieldVO.getBitNum().toString() +"]");
			lblFieldName = new JLabel(fieldVO.getName());
			lblType = new JLabel(fieldVO.getType().toString());
			lblDynamic = new JLabel();
			lblDynamic.setIcon(new ImageIcon(PnlGuiPayload.class.getResource("/img/search.png")));
			lblDynamic.setToolTipText(fieldVO.getDynaCondition());

			lineNum = numLines;
			numLines++;
			
			ckBox.setBounds(10, 10 + (lineNum * 25), 22, 22);
			lblFieldNum.setBounds(40, 10 + (lineNum * 25), 60, 22);
			lblFieldName.setBounds(100, 10 + (lineNum * 25), 100, 22);
			lblType.setBounds(490, 10 + (lineNum * 25), 100, 22);
			lblDynamic.setBounds(620, 10 + (lineNum * 25), 50, 22);
			
			if (fieldVO.getType() == TypeEnum.ALPHANUMERIC) {
				txtValue.setBounds(210, 10 + (lineNum * 25), 260, 22);
			}
			else if (fieldVO.getType() == TypeEnum.TLV) {
				txtType.setBounds(210, 10 + (lineNum * 25), 80, 22);
				txtLength.setBounds(300, 10 + (lineNum * 25), 80, 22);
				txtValue.setBounds(390, 10 + (lineNum * 25), 80, 22);
				
				pnlFields.add(txtType);
				pnlFields.add(txtLength);
			}
			
			pnlFields.add(ckBox);
			pnlFields.add(lblFieldNum);
			pnlFields.add(lblFieldName);
			
			//Remove TypeLabel and TextField when we have SubFields. 
			//Parent field has its value created by its subfields
			if(fieldVO.getFieldList().size() == 0 || fieldVO.getType() == TypeEnum.TLV){
				pnlFields.add(txtValue);
				pnlFields.add(lblType);
			}
			
			txtType.addKeyListener(saveFieldPayloadAction);
			txtLength.addKeyListener(saveFieldPayloadAction);
			txtValue.addKeyListener(saveFieldPayloadAction);
			
			if (!fieldVO.getDynaCondition().equals("") && !fieldVO.getDynaCondition().equals("true"))
				pnlFields.add(lblDynamic);
			
			if (fieldVO.getDynaCondition().equals("true")) {
				ckBox.setSelected(true);
				ckBox.setEnabled(false);
				ckBoxClick(ckBox);
				
				setEnabled(true, false);
			}
			else {
				ckBox.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						ckBoxClick((JCheckBox) e.getSource());
						saveFieldValue();
					}
				});
				
				setEnabled(false, isSubfield);
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
			setEnabled(ckBox.isSelected(), false);
			txtValue.setText("");
			setEnableStatusForFields(subfieldList, ckBox.isSelected());
		}
		
		/**
		 * Enable/Disable the fields, and subsfields, depending on the checkbox 
		 * @param subFields list of fields to be enabled/disabled
		 * @param enabledValue boolean that represent whether the checkbox is checked or not.
		 */
		private void setEnableStatusForFields(final List<GuiPayloadField> subFields, final boolean enabledValue){
			subFields.forEach(subField->{
				subField.setEnabled(enabledValue, true);
				subField.txtValue.setText("");
				setEnableStatusForFields(subField.subfieldList, enabledValue);
			});
		}
		
		private GuiPayloadField addSubline(FieldVO fieldVO, FieldVO superfieldVO, String superFieldBitNum) {
			GuiPayloadField newPayloadField = new GuiPayloadField(fieldVO, superfieldVO, superFieldBitNum);
			subfieldList.add(newPayloadField);
			return newPayloadField;
		}
		
		private void setReadOnly() {
			setEnabled(false, false);
			ckBox.setEnabled(false);
			
			txtType.setEnabled(true);
			txtLength.setEnabled(true);
			txtValue.setEnabled(true);
			
			txtType.setEditable(false);
			txtLength.setEditable(false);
			txtValue.setEditable(false);
		}
		
		private void setEnabled(boolean enabled, boolean enableCkBox) {
			txtType.setEnabled(enabled);
			txtLength.setEnabled(enabled);
			txtValue.setEnabled(enabled);
			lblFieldNum.setEnabled(enabled);
			lblFieldName.setEnabled(enabled);
			lblType.setEnabled(enabled);
			lblDynamic.setEnabled(enabled);
			if (enableCkBox) {
				ckBox.setEnabled(enabled);
				ckBox.setSelected(enabled);
			}
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
			
		private StringBuilder getXML(int depth) {
			StringBuilder xmlField = new StringBuilder();

			String tabs = "";
			for (int i = 0; i < depth; i++) tabs += "\t";
			
			if (ckBox.isSelected()) {
				xmlField.append("\n").append(tabs).append("<bit num=\"").append(fieldVO.getBitNum()).append("\"");

				if (fieldVO.getType() == TypeEnum.TLV) {
					xmlField.append(" tag=\"").append(getType()).
							append("\" length=\"").append(getLength()).append("\"").
							append(" value=\"").append(getValue()).append("\"");
				}
				
				if (subfieldList.size() > 0){
					xmlField.append(">");
				}
				
				subfieldList.forEach(subfield -> {
					xmlField.append(subfield.getXML(depth + 1));
				});

				
				if (subfieldList.size() > 0) {
					xmlField.append("\n"+ tabs +"</bit>");
				}
				else if (fieldVO.getType() != TypeEnum.TLV) {
					xmlField.append(" value=\"").append(getValue()).append("\"/>");
				}
				else {
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
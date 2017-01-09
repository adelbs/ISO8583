package org.adelbs.iso8583.helper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.gui.FrmMain;
import org.adelbs.iso8583.gui.PnlGuiPayload;
import org.adelbs.iso8583.gui.xmlEditor.XmlTextPane;
import org.adelbs.iso8583.protocol.ISOMessage;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.MessageVO;

public class GuiPayloadMessageHelper {

	private MessageVO messageVO;
	
	private ArrayList<GuiPayloadField> fieldList;

	private int numLines;
	private JPanel pnlFields;
	private XmlTextPane xmlText;

	private ISOMessage isoMessage;
	
	public GuiPayloadMessageHelper(MessageVO messageVO, JPanel pnlFields, XmlTextPane xmlText) {
		this.pnlFields = pnlFields;
		this.xmlText = xmlText;
		this.messageVO = new MessageVO(messageVO.getType(), messageVO.getBitmatEncoding(), messageVO.getHeaderEncoding());
		
		pnlFields.removeAll();
		numLines = 0;
		fieldList = new ArrayList<GuiPayloadField>();
	}
	
	public MessageVO getMessageVO() {
		return messageVO;
	}

	public ArrayList<GuiPayloadField> getFieldList() {
		return fieldList;
	}

	public void addLine(FieldVO fieldVO) {
		fieldList.add(new GuiPayloadField(fieldVO, null));
	}

	public void addSubline(FieldVO fieldVO) {
		if (fieldList.size() > 0)
			fieldList.get(fieldList.size() - 1).addSubline(fieldVO, fieldList.get(fieldList.size() - 1).getFieldVO());
	}

	public void updateGUIfromXML() {
		//TODO
	}
	
	public void updateFromPayload(byte[] bytes) {
		try {
			isoMessage = new ISOMessage(bytes, messageVO);
		}
		catch (Exception x) {
			JOptionPane.showMessageDialog(FrmMain.getInstance(), "It was not possible to parse this payload. Certify that the message structure was not changed.\n" + x.getMessage());
		}
		
		for (GuiPayloadField guiPayloadField : fieldList) {
			if (isoMessage.getBit(guiPayloadField.getFieldVO().getBitNum()) != null) {
				guiPayloadField.setSelected();
				guiPayloadField.getFieldVO().setPresent(true);
				guiPayloadField.setTlvType(isoMessage.getBit(guiPayloadField.getFieldVO().getBitNum()).getTlvType());
				guiPayloadField.setTlvLength(isoMessage.getBit(guiPayloadField.getFieldVO().getBitNum()).getTlvLength());
				guiPayloadField.setText(isoMessage.getBit(guiPayloadField.getFieldVO().getBitNum()).getValue());
				for (int i = 0; i < guiPayloadField.subfieldList.size(); i++) {
					guiPayloadField.subfieldList.get(i).setTlvType(isoMessage.getBit(guiPayloadField.getFieldVO().getBitNum()).getFieldList().get(i).getTlvType());
					guiPayloadField.subfieldList.get(i).setTlvLength(isoMessage.getBit(guiPayloadField.getFieldVO().getBitNum()).getFieldList().get(i).getTlvLength());
					guiPayloadField.subfieldList.get(i).setText(isoMessage.getBit(guiPayloadField.getFieldVO().getBitNum()).getFieldList().get(i).getValue());
				}
			}
		}
		
	}
	
	public void updateXMLfromGUI() {
		StringBuilder xmlMessage = new StringBuilder();
		
		xmlMessage.append("<?xml version=\"1.0\" ?>\n\n");
		xmlMessage.append("<message type=\"").append(messageVO.getType()).append("\">");
		
		for (GuiPayloadField payloadField : fieldList)
			xmlMessage.append(payloadField.getXML());
			
		xmlMessage.append("\n</message>");
		xmlText.setText(xmlMessage.toString());
	}
	
	public void updateRawMessage(JTextArea txtRawMessage) {
		try {
			isoMessage = new ISOMessage(messageVO);
			txtRawMessage.setText(isoMessage.getVisualPayload());
		}
		catch (Exception x) {
			JOptionPane.showMessageDialog(FrmMain.getInstance(), "Error building the raw message.\n" + x.getMessage());
		}
	}
	
	public ISOMessage getIsoMessage() {
		return isoMessage;
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
			this.fieldVO = new FieldVO(fieldVO.getName(), fieldVO.getSubFieldName(), fieldVO.getBitNum(), fieldVO.getType(), fieldVO.getTypeLength(), 
					fieldVO.getLength(), fieldVO.getEncoding(), fieldVO.getDynaCondition());
			
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
			lblDynamic.setIcon(new ImageIcon(PnlGuiPayload.class.getResource("/resource/search.png")));
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
		
		private void saveFieldValue() {
			fieldVO.setPresent(ckBox.isSelected());
			fieldVO.setTlvType(txtType.getText());
			fieldVO.setTlvLength(txtLength.getText());
			fieldVO.setValue(txtValue.getText());
		}
		
		private void setSelected() {
			ckBox.setSelected(true);
			ckBoxClick(ckBox);
		}
		
		private void ckBoxClick(JCheckBox ckBox) {
			setEnabled(ckBox.isSelected());
			setText("");
			for (GuiPayloadField subfield : subfieldList) {
				subfield.setEnabled(ckBox.isSelected());
				subfield.setText("");
			}
		}
		
		private void addSubline(FieldVO fieldVO, FieldVO superfieldVO) {
			if (superfieldVO != null && superfieldVO.getType() != TypeEnum.TLV) {
				pnlFields.remove(txtValue);
				pnlFields.remove(lblType);
			}
			
			subfieldList.add(new GuiPayloadField(fieldVO, superfieldVO));
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
		
		private void setTlvType(String value) {
			txtType.setText(value);
		}
		
		private void setTlvLength(String value) {
			txtLength.setText(value);
		}
		
		private void setText(String text) {
			txtValue.setText(text);
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
				
				for (GuiPayloadField subfield : subfieldList) {
					if (!hasSubfield) xmlField.append(">");
					xmlField.append(subfield.getXML());
					hasSubfield = true;
				}
				
				if (hasSubfield) 
					xmlField.append("\n\t</bit>");
				else {
					if (fieldVO.getType() == TypeEnum.TLV) {
						xmlField.append(" type=\"").append(getType()).
								append("\" length=\"").append(getLength()).append("\"");
					}
					xmlField.append(" value=\"").append(getValue()).append("\"/>");
				}
			}
			
			return xmlField;
		}
		
		private FieldVO getFieldVO() {
			return fieldVO;
		}
	}
}

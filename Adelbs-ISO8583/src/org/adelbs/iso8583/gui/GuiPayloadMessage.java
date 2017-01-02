package org.adelbs.iso8583.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.gui.xmlEditor.XmlTextPane;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.MessageVO;

public class GuiPayloadMessage {

	private MessageVO messageVO;
	
	private ArrayList<GuiPayloadField> fieldList;

	private int numLines;
	private JPanel pnlFields;
	private XmlTextPane xmlText;
	
	public GuiPayloadMessage(MessageVO messageVO, JPanel pnlFields, XmlTextPane xmlText) {
		this.pnlFields = pnlFields;
		this.xmlText = xmlText;
		this.messageVO = messageVO;
		
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
		fieldList.add(new GuiPayloadField(fieldVO, false));
	}

	public void addSubline(FieldVO fieldVO) {
		if (fieldList.size() > 0)
			fieldList.get(fieldList.size() - 1).addSubline(fieldVO);
	}

	public void updateXML() {
		StringBuilder xmlMessage = new StringBuilder();
		
		xmlMessage.append("<?xml version=\"1.0\" ?>\n\n");
		xmlMessage.append("<message type=\"").append(messageVO.getType()).append("\">");
		
		for (GuiPayloadField payloadField : fieldList)
			xmlMessage.append(payloadField.getXML());
			
		xmlMessage.append("\n</message>");
		xmlText.setText(xmlMessage.toString());
	}
	
	private class GuiPayloadField {
		
		boolean isSubfield;
		private int lineNum;
		private FieldVO fieldVO;
		
		private JCheckBox ckBox;
		private JTextField txtType;
		private JTextField txtLength;
		private JTextField txtValue;
		
		private JLabel lblFieldNum;
		private JLabel lblFieldName;
		private JLabel lblType;
		private JLabel lblDynamic;
		
		private ArrayList<GuiPayloadField> subfieldList;

		private GuiPayloadField(FieldVO fieldVO, boolean isSubfield) {
			this.fieldVO = fieldVO;
			this.isSubfield = isSubfield;
			
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
					}
				});
				
				setEnabled(false);
			}
		}
		
		private void ckBoxClick(JCheckBox ckBox) {
			setEnabled(ckBox.isSelected());
			setText("");
			for (GuiPayloadField subfield : subfieldList) {
				subfield.setEnabled(ckBox.isSelected());
				subfield.setText("");
			}
		}
		
		private void addSubline(FieldVO fieldVO) {
			pnlFields.remove(txtValue);
			pnlFields.remove(lblType);
			subfieldList.add(new GuiPayloadField(fieldVO, true));
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
	}
}

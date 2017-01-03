package org.adelbs.iso8583.gui;

import java.awt.event.KeyListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.vo.MessageVO;

public class PnlMessageProperties extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JLabel lblMsgType = new JLabel("Message Type");
	private JTextField txtMsgType = new JTextField();
	private JLabel lblHeaderEncoding = new JLabel("Encoding");
	private JComboBox<EncodingEnum> cmbHeaderEncoding = new JComboBox<EncodingEnum>();
	
	public PnlMessageProperties(KeyListener saveKeyListener) {
		setLayout(null);
		setBorder(new TitledBorder(null, "Message Properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		lblMsgType.setBounds(12, 27, 88, 16);
		txtMsgType.setColumns(10);
		txtMsgType.setBounds(101, 24, 70, 22);
		txtMsgType.addKeyListener(saveKeyListener);
		
		lblHeaderEncoding.setBounds(200, 27, 88, 16);
		cmbHeaderEncoding.setBounds(262, 24, 116, 22);
		cmbHeaderEncoding.setModel(new DefaultComboBoxModel<EncodingEnum>(new EncodingEnum[] {
				EncodingEnum.UTF8, EncodingEnum.EBCDIC, 
				EncodingEnum.ISO88591, EncodingEnum.BINARY}));
		
		add(lblMsgType);
		add(txtMsgType);
		add(lblHeaderEncoding);
		add(cmbHeaderEncoding);
		
	}
	
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		lblMsgType.setEnabled(value);
		txtMsgType.setEnabled(value);
		lblHeaderEncoding.setEnabled(value);
		cmbHeaderEncoding.setEnabled(value);
	}

	public void save(MessageVO messageVo) {
		if (txtMsgType.getText() == null || txtMsgType.getText().trim().equals(""))
			messageVo.setType("0000");
		else
			messageVo.setType(txtMsgType.getText().trim().replaceAll(" ", ""));
		
		messageVo.setBitmatEncoding((EncodingEnum) cmbHeaderEncoding.getSelectedItem());
	}

	public void load(MessageVO messageVo) {
		if (messageVo != null) {
			txtMsgType.setText(messageVo.getType());
			cmbHeaderEncoding.setSelectedItem(messageVo.getBitmatEncoding());
		}
	}
	
	public void clear() {
		txtMsgType.setText("");
		cmbHeaderEncoding.setSelectedIndex(0);
	}

}

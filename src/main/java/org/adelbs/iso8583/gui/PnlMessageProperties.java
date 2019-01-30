package org.adelbs.iso8583.gui;

import java.awt.event.KeyListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.vo.MessageVO;

public class PnlMessageProperties extends JPanel {

	private static final long serialVersionUID = 2L;
	
	private JLabel lblMsgType = new JLabel("Message Type");
	private JTextField txtMsgType = new JTextField();
	private JLabel lblHeaderEncoding = new JLabel("Header Encoding");	
	private JLabel lblBitmapEncoding = new JLabel("Bitmap Encoding");
	private JComboBox<EncodingEnum> cmbHeaderEncoding = new JComboBox<EncodingEnum>();
	private JComboBox<EncodingEnum> cmbBitmapEncoding = new JComboBox<EncodingEnum>();
	
	public PnlMessageProperties(KeyListener saveKeyListener) {
		setLayout(null);
		setBorder(new TitledBorder(null, "Message Properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		lblMsgType.setHorizontalAlignment(SwingConstants.RIGHT);

		lblMsgType.setBounds(12, 27, 102, 16);
		txtMsgType.setColumns(10);
		txtMsgType.setBounds(126, 24, 70, 22);
		txtMsgType.addKeyListener(saveKeyListener);
		lblHeaderEncoding.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lblHeaderEncoding.setBounds(12, 56, 102, 16);
		cmbHeaderEncoding.setBounds(126, 53, 92, 22);
		EncodingEnum.addCmbItemList(cmbHeaderEncoding);
		lblBitmapEncoding.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lblBitmapEncoding.setBounds(230, 56, 102, 16);
		cmbBitmapEncoding.setBounds(344, 53, 92, 22);
		EncodingEnum.addCmbItemList(cmbBitmapEncoding);
		
		add(lblMsgType);
		add(txtMsgType);
		add(lblHeaderEncoding);
		add(cmbHeaderEncoding);
		add(lblBitmapEncoding);
		add(cmbBitmapEncoding);
		
		setEnabled(false);
	}
	
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		lblMsgType.setEnabled(value);
		txtMsgType.setEnabled(value);
		lblHeaderEncoding.setEnabled(value);
		cmbHeaderEncoding.setEnabled(value);
		lblBitmapEncoding.setEnabled(value);
		cmbBitmapEncoding.setEnabled(value);
	}

	public void save(MessageVO messageVo) {
		if (txtMsgType.getText() == null || txtMsgType.getText().trim().equals(""))
			messageVo.setType("0000");
		else
			messageVo.setType(txtMsgType.getText().trim().replaceAll(" ", ""));
		
		messageVo.setBitmatEncoding((EncodingEnum) cmbBitmapEncoding.getSelectedItem());
		messageVo.setHeaderEncoding((EncodingEnum) cmbHeaderEncoding.getSelectedItem());
	}

	public void load(MessageVO messageVo) {
		if (messageVo != null) {
			txtMsgType.setText(messageVo.getType());
			cmbBitmapEncoding.setSelectedItem(messageVo.getBitmatEncoding());
			cmbHeaderEncoding.setSelectedItem(messageVo.getHeaderEncoding());
		}
	}
	
	public void clear() {
		txtMsgType.setText("");
		cmbHeaderEncoding.setSelectedIndex(0);
	}

}

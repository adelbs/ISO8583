package org.adelbs.iso8583.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.constants.TypeLengthEnum;
import org.adelbs.iso8583.vo.FieldVO;

public class PnlFieldProperties extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JLabel lblName = new JLabel("Name");
	private JTextField txtName = new JTextField();
	private JLabel lblSubfield = new JLabel("Subfield Name");
	private JTextField txtSubField = new JTextField();
	private JLabel lblNum = new JLabel("Bit Num");
	private JTextField txtNum = new JTextField();
	private JLabel lblType = new JLabel("Type");
	private JComboBox<TypeEnum> cmbType = new JComboBox<TypeEnum>();
	private JLabel lblLenght = new JLabel("Lenght");
	private JTextField txtLength = new JTextField();
	private JLabel lblEncoding = new JLabel("Encoding");
	private JComboBox<EncodingEnum> cmbEncoding = new JComboBox<EncodingEnum>();
	private JCheckBox chckbxMandatory = new JCheckBox("Mandatory");
	private JLabel lblLenValue = new JLabel("Length value");
	private JComboBox<TypeLengthEnum> cmbLength = new JComboBox<TypeLengthEnum>();

	public PnlFieldProperties(KeyListener saveKeyListener) {

		setLayout(null);
		setBorder(new TitledBorder(null, "Field Properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		lblName.setBounds(12, 27, 83, 16);
		lblName.setHorizontalAlignment(SwingConstants.RIGHT);
		txtName.setColumns(10);
		txtName.addKeyListener(saveKeyListener);
		
		lblSubfield.setBounds(10, 59, 83, 16);
		lblSubfield.setHorizontalAlignment(SwingConstants.RIGHT);
		txtSubField.setColumns(10);
		txtSubField.addKeyListener(saveKeyListener);

		lblNum.setBounds(12, 92, 83, 16);
		lblNum.setHorizontalAlignment(SwingConstants.RIGHT);
		txtNum.setBounds(101, 89, 70, 22);
		txtNum.setColumns(10);
		txtNum.addKeyListener(saveKeyListener);
		
		lblType.setBounds(28, 122, 61, 16);
		lblType.setHorizontalAlignment(SwingConstants.RIGHT);
		cmbType.setBounds(101, 121, 116, 22);
		cmbType.setModel(new DefaultComboBoxModel<TypeEnum>(new TypeEnum[] {TypeEnum.ALPHANUMERIC, TypeEnum.TLV}));
		
		lblLenght.setBounds(12, 155, 83, 16);
		lblLenght.setHorizontalAlignment(SwingConstants.RIGHT);
		cmbLength.setBounds(101, 152, 116, 22);
		cmbLength.setModel(new DefaultComboBoxModel<TypeLengthEnum>(new TypeLengthEnum[] {TypeLengthEnum.FIXED, TypeLengthEnum.NVAR}));
		
		cmbLength.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cmbLengthClick();
			}
		});
		
		lblLenValue.setHorizontalAlignment(SwingConstants.RIGHT);
		lblLenValue.setBounds(235, 156, 83, 16);
		txtLength.setBounds(322, 153, 51, 22);
		txtLength.setColumns(10);
		
		lblEncoding.setBounds(257, 123, 61, 16);
		lblEncoding.setHorizontalAlignment(SwingConstants.RIGHT);
		cmbEncoding.setBounds(322, 122, 116, 22);
		cmbEncoding.setModel(new DefaultComboBoxModel<EncodingEnum>(new EncodingEnum[] {
				EncodingEnum.UTF8, EncodingEnum.EBCDIC, 
				EncodingEnum.ISO88591, EncodingEnum.BINARY}));
		
		chckbxMandatory.setBounds(179, 88, 113, 25);
		
		chckbxMandatory.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FrmMain.getInstance().getPnlGuiConfig().getPnlFieldCondition().setMandatory(chckbxMandatory.isSelected());
			}
		});
		
		add(lblName);
		add(txtName);
		add(lblSubfield);
		add(txtSubField);
		add(lblNum);
		add(txtNum);
		add(lblType);
		add(cmbType);
		add(lblLenght);
		add(txtLength);
		add(lblEncoding);
		add(cmbEncoding);
		add(chckbxMandatory);
		add(cmbLength);
		add(lblLenValue);
		
		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentResized(ComponentEvent e) {
				txtName.setBounds(101, 24, getWidth() - 120, 22);
				txtSubField.setBounds(101, 56, getWidth() - 120, 22);
			}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		}); 
	}
	
	private void cmbLengthClick() {
		if (cmbLength.getSelectedItem() == TypeLengthEnum.NVAR)
			lblLenValue.setText("Num of Bits");
		else 
			lblLenValue.setText("Length value");
	}
	
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		lblName.setEnabled(value);
		txtName.setEnabled(value);
		lblSubfield.setEnabled(false);
		txtSubField.setEnabled(false);
		lblNum.setEnabled(value);
		txtNum.setEnabled(value);
		lblType.setEnabled(value);
		cmbType.setEnabled(value);
		lblLenght.setEnabled(value);
		lblLenValue.setEnabled(value);
		txtLength.setEnabled(value);
		cmbLength.setEnabled(value);
		lblEncoding.setEnabled(value);
		cmbEncoding.setEnabled(value);
		chckbxMandatory.setEnabled(value);
		
		lblNum.setText("Bit Num");
	}
	
	public void disableSuperField() {
		lblSubfield.setEnabled(false);
		txtSubField.setEnabled(false);
		lblLenght.setEnabled(false);
		cmbLength.setEnabled(false);
		lblLenValue.setEnabled(false);
		txtLength.setEnabled(false);
		
		lblNum.setText("Bit Num");
	}

	public void disableSubField() {
		lblName.setEnabled(false);
		txtName.setEnabled(false);
		chckbxMandatory.setSelected(false);
		chckbxMandatory.setEnabled(false);
		
		lblSubfield.setEnabled(true);
		txtSubField.setEnabled(true);
		lblLenght.setEnabled(true);
		cmbLength.setEnabled(true);
		lblLenValue.setEnabled(true);
		txtLength.setEnabled(true);

		lblNum.setText("Order");
	}
	
	public void save(FieldVO fieldVo) {
		
		if (txtName.getText() == null || txtName.getText().trim().equals(""))
			fieldVo.setName("NewField");
		else
			fieldVo.setName(txtName.getText().trim().replaceAll(" ", ""));

		if (txtSubField.getText() == null || txtSubField.getText().trim().equals(""))
			fieldVo.setSubFieldName("");
		else
			fieldVo.setSubFieldName(txtSubField.getText().trim().replaceAll(" ", ""));
		
		try {
			fieldVo.setBitNum(Integer.parseInt(txtNum.getText()));
		}
		catch (Exception x) {
			fieldVo.setBitNum(1);
		}

		fieldVo.setType((TypeEnum) cmbType.getSelectedItem());
		
		if (txtLength.getText() == null || txtLength.getText().trim().equals(""))
			fieldVo.setLength(1);
		else
			fieldVo.setLength(Integer.parseInt(txtLength.getText()));

		fieldVo.setTypeLength((TypeLengthEnum) cmbLength.getSelectedItem());
		fieldVo.setEncoding((EncodingEnum) cmbEncoding.getSelectedItem());
	}

	public void load(FieldVO fieldVo) {
		if (fieldVo != null) {
			txtName.setText(fieldVo.getName());
			txtSubField.setText(fieldVo.getSubFieldName());
			txtNum.setText(String.valueOf(fieldVo.getBitNum()));
			cmbType.setSelectedItem(fieldVo.getType());
			cmbLength.setSelectedItem(fieldVo.getTypeLength());
			txtLength.setText(String.valueOf(fieldVo.getLength()));
			cmbEncoding.setSelectedItem(fieldVo.getEncoding());
			chckbxMandatory.setSelected(fieldVo.getDynaCondition().equals("true"));
			
			cmbLengthClick();
		}
	}

	public void clear() {
		txtName.setText("");
		txtNum.setText("");
		cmbType.setSelectedIndex(0);
		txtLength.setText("");
		cmbEncoding.setSelectedIndex(0);
		chckbxMandatory.setSelected(false);
		cmbLength.setSelectedIndex(0);
	}
	
	public JCheckBox getChckbxMandatory() {
		return chckbxMandatory;
	}
}

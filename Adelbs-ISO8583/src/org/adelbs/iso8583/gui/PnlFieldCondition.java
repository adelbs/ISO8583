package org.adelbs.iso8583.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import org.adelbs.iso8583.constants.OperatorEnum;
import org.adelbs.iso8583.helper.Iso8583Helper;
import org.adelbs.iso8583.vo.CmbItemVO;
import org.adelbs.iso8583.vo.FieldVO;

public class PnlFieldCondition extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JCheckBox ckDynamic = new JCheckBox("Dynamic");
	private JComboBox<CmbItemVO> cmbDynaBit = new JComboBox<CmbItemVO>();
	private JComboBox<CmbItemVO> cmbDynaOperator = new JComboBox<CmbItemVO>();
	private JLabel lblDynaValue = new JLabel("Value");
	private JTextField txtDynaValue = new JTextField();
	private JButton btnDynaAdd = new JButton("");
	private JButton btnDynaValidate = new JButton("");
	private JTextArea txtDynaCondition = new JTextArea();
	private JScrollPane scrDynaCondition = new JScrollPane();

	public PnlFieldCondition() {
		
		setLayout(null);
		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Condition", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		//SINCRONIZAR VALORES ABAIXO COM O RESIZE
		scrDynaCondition.setBounds(15, 100, getWidth() - 55, getHeight() - 109);
		btnDynaValidate.setBounds(getWidth() - 35, getHeight() - 35, 25, 25);
		//***************************************************************************

		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentResized(ComponentEvent e) {
				scrDynaCondition.setBounds(15, 100, getWidth() - 55, getHeight() - 109);
				btnDynaValidate.setBounds(getWidth() - 35, getHeight() - 35, 25, 25);
			}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		}); 
		
		ckDynamic.setBounds(8, 0, 77, 25);

		cmbDynaOperator.addItem(new CmbItemVO("-- LOGIC OPERATION --", "-- LOGIC OPERATION --"));
		cmbDynaOperator.addItem(new CmbItemVO("EQUAL (" + OperatorEnum.EQUAL.toString() + ")", OperatorEnum.EQUAL.toString()));
		cmbDynaOperator.addItem(new CmbItemVO("DIFFERENT (" + OperatorEnum.DIFFERENT.toString() + ")", OperatorEnum.DIFFERENT.toString()));
		cmbDynaOperator.addItem(new CmbItemVO("GRATER THAN (" + OperatorEnum.GRATER_THAN.toString() + ")", OperatorEnum.GRATER_THAN.toString()));
		cmbDynaOperator.addItem(new CmbItemVO("SMALLER THAN (" + OperatorEnum.SMALLER_THAN.toString() + ")", OperatorEnum.SMALLER_THAN.toString()));
		cmbDynaOperator.addItem(new CmbItemVO(" ( ", OperatorEnum.OPEN_PAR.toString()));
		cmbDynaOperator.addItem(new CmbItemVO(" ) ", OperatorEnum.CLOSE_PAR.toString()));
		cmbDynaOperator.addItem(new CmbItemVO("PLUS (" + OperatorEnum.PLUS.toString() + ")", OperatorEnum.PLUS.toString()));
		cmbDynaOperator.addItem(new CmbItemVO("MINUS (" + OperatorEnum.MINUS.toString() + ")", OperatorEnum.MINUS.toString()));
		cmbDynaOperator.addItem(new CmbItemVO("AND (" + OperatorEnum.AND.toString() + ")", OperatorEnum.AND.toString()));
		cmbDynaOperator.addItem(new CmbItemVO("OR (" + OperatorEnum.OR.toString() + ")", OperatorEnum.OR.toString()));
		
		cmbDynaBit.setBounds(58, 36, 116, 22);
		cmbDynaOperator.setBounds(186, 36, 158, 22);
		lblDynaValue.setBounds(15, 71, 40, 16);
		txtDynaValue.setBounds(58, 68, 245, 22);
		txtDynaValue.setColumns(10);

		btnDynaAdd.setIcon(new ImageIcon(PnlGuiConfig.class.getResource("/resource/enter.png")));
		btnDynaAdd.setBounds(305, 68, 40, 22);
		txtDynaCondition.setLineWrap(true);
		txtDynaCondition.setFont(new Font("Monospaced", Font.BOLD, 18));
		scrDynaCondition.setViewportView(txtDynaCondition);
		btnDynaValidate.setIcon(new ImageIcon(PnlGuiConfig.class.getResource("/resource/validate.png")));
		
		cmbDynaOperator.addActionListener(new AddLogicActionListener(cmbDynaOperator));
		btnDynaAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!txtDynaCondition.getText().trim().equals("")) {
					txtDynaCondition.setText(txtDynaCondition.getText() + " " + txtDynaValue.getText());
					txtDynaValue.setText("");
					
					FrmMain.getInstance().getPnlGuiConfig().save();
					FrmMain.getInstance().getPnlGuiConfig().getTree().updateUI();
				}
			}
		});
		
		btnDynaValidate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FrmMain.getInstance().getPnlGuiConfig().save();
				String validationError = Iso8583Helper.getInstance().validateCondition((FieldVO) FrmMain.getInstance().getPnlGuiConfig().getSelectedNode().getUserObject());
				if (!"".equals(validationError))
					JOptionPane.showMessageDialog(FrmMain.getInstance(), "Invalid expression!\n\n" + validationError);
				else
					JOptionPane.showMessageDialog(FrmMain.getInstance(), "The expression looks good!");
			}
		});
		
		ckDynamic.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ckDynamicClick();
			}
		});
		
		add(ckDynamic);
		add(cmbDynaBit);
		add(cmbDynaOperator);
		add(lblDynaValue);
		add(txtDynaValue);
		add(btnDynaAdd);
		add(scrDynaCondition);
		add(btnDynaValidate);

	}
	
	private void ckDynamicClick() {
		if (ckDynamic.isSelected()) {
			setEnabled(true);
			ckDynamic.setEnabled(!txtDynaCondition.getText().equals("true"));
			
			FieldVO fieldVO = null;
			cmbDynaBit.removeAllItems();
			cmbDynaBit.addItem(new CmbItemVO("-- BIT --", "-- BIT --"));
			for (int i = 0; i < FrmMain.getInstance().getPnlGuiConfig().getSelectedNodeParent().getChildCount(); i++) {
				fieldVO = (FieldVO) ((DefaultMutableTreeNode) FrmMain.getInstance().getPnlGuiConfig().getSelectedNodeParent().getChildAt(i)).getUserObject();
				cmbDynaBit.addItem(new CmbItemVO("BIT[" + fieldVO.getBitNum() + "] " + fieldVO.getName(), "BIT[" + fieldVO.getBitNum() + "]"));
			}
			
			cmbDynaBit.addActionListener(new AddLogicActionListener(cmbDynaBit));
		}
		else {
			setEnabled(false);
			ckDynamic.setEnabled(!txtDynaCondition.getText().equals("true"));
			clear();
		}
	}

	public void disableSubField() {
		ckDynamic.setSelected(false);
		ckDynamic.setEnabled(false);
	}

	public void setMandatory(boolean value) {
		if (value) {
			txtDynaCondition.setText("true");
			ckDynamic.setSelected(false);
			ckDynamic.setEnabled(false);
		}
		else {
			txtDynaCondition.setText("");
			ckDynamic.setEnabled(true);
		}
		
		ckDynamicClick();
	}
	
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		
		ckDynamic.setEnabled(value);
		cmbDynaBit.setEnabled(value);
		cmbDynaOperator.setEnabled(value);
		lblDynaValue.setEnabled(value);
		txtDynaValue.setEnabled(value);
		btnDynaAdd.setEnabled(value);
		txtDynaCondition.setEnabled(value);
		btnDynaValidate.setEnabled(value);
	}
	
	public void save(FieldVO fieldVo) {
		fieldVo.setDynaCondition(txtDynaCondition.getText().trim());
	}
	
	public void load(FieldVO fieldVo) {
		if (fieldVo != null) {
			cmbDynaOperator.setSelectedItem(fieldVo.getDynaCondition());
			txtDynaCondition.setText(fieldVo.getDynaCondition());
			ckDynamic.setSelected(fieldVo.getDynaCondition() != null && !fieldVo.getDynaCondition().trim().equals("") && !fieldVo.getDynaCondition().trim().equals("true"));
		}
	}
	
	public void clear() {
		ckDynamic.setSelected(false);
		cmbDynaBit.removeAllItems();
		cmbDynaOperator.setSelectedIndex(0);
		txtDynaValue.setText("");
		txtDynaCondition.setText(FrmMain.getInstance().getPnlGuiConfig().getPnlFieldProperties().getChckbxMandatory().isSelected() ? "true" : "");
	}

	private class AddLogicActionListener implements ActionListener {
		
		private JComboBox<CmbItemVO> cmbObject;
		
		AddLogicActionListener(JComboBox<CmbItemVO> cmbObject) {
			this.cmbObject = cmbObject;
		}
		
		public void actionPerformed(ActionEvent e) {
			if (cmbObject.getSelectedIndex() > 0) {
				txtDynaCondition.setText(txtDynaCondition.getText() + " " + ((CmbItemVO) cmbObject.getSelectedItem()).getValue());
				cmbObject.setSelectedIndex(0);
				
				FrmMain.getInstance().getPnlGuiConfig().save();
				FrmMain.getInstance().getPnlGuiConfig().getTree().updateUI();
			}
		}
	}
}

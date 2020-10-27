package org.adelbs.iso8583.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import org.adelbs.iso8583.vo.CmbItemVO;
import org.adelbs.iso8583.vo.FieldVO;

public class PnlFieldCondition extends JPanel {

	private static final long serialVersionUID = 2L;
	
	private JComboBox<CmbItemVO> cmbDynaBit = new JComboBox<CmbItemVO>();
	private JComboBox<CmbItemVO> cmbDynaOperator = new JComboBox<CmbItemVO>();
	private JLabel lblDynaValue = new JLabel("Value");
	private JTextField txtDynaValue = new JTextField();
	private JButton btnDynaAdd = new JButton("");
	private JButton btnDynaValidate = new JButton("");
	private JTextArea txtDynaCondition = new JTextArea();
	private JScrollPane scrDynaCondition = new JScrollPane();

	private PnlMain pnlMain;
	
	public PnlFieldCondition(final PnlMain pnlMain) {
		
		this.pnlMain = pnlMain;
		
		setLayout(null);
		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Condition", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		//SINCRONIZAR VALORES ABAIXO COM O RESIZE
		scrDynaCondition.setBounds(15, 100, 395, 100);
		btnDynaValidate.setBounds(415, 174, 25, 25);
		//***************************************************************************

		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentResized(ComponentEvent e) {
				scrDynaCondition.setBounds(15, 100, getWidth() - 55, ((getHeight() - 120) <= 100 ? 100 : getHeight() - 120));
				btnDynaValidate.setBounds(getWidth() - 35, ((getHeight() - 46) <= 174 ? 174 : getHeight() - 46), 25, 25);
			}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		}); 
		
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

		btnDynaAdd.setIcon(new ImageIcon(PnlGuiConfig.class.getResource("/img/enter.png")));
		btnDynaAdd.setBounds(305, 68, 40, 22);
		txtDynaCondition.setLineWrap(true);
		txtDynaCondition.setFont(new Font("Monospaced", Font.BOLD, 18));
		scrDynaCondition.setViewportView(txtDynaCondition);
		btnDynaValidate.setIcon(new ImageIcon(PnlGuiConfig.class.getResource("/img/validate.png")));
		
		cmbDynaOperator.addActionListener(new AddLogicActionListener(cmbDynaOperator));
		btnDynaAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!txtDynaCondition.getText().trim().equals("")) {
					txtDynaCondition.setText(txtDynaCondition.getText() + " " + txtDynaValue.getText());
					txtDynaValue.setText("");
					
					pnlMain.getPnlGuiConfig().save(pnlMain);
					pnlMain.getPnlGuiConfig().getTree().updateUI();
				}
			}
		});
		
		btnDynaValidate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pnlMain.getPnlGuiConfig().save(pnlMain);
				String validationError = pnlMain.getIso8583Config().validateCondition((FieldVO) pnlMain.getPnlGuiConfig().getSelectedNode().getUserObject());
				if (!"".equals(validationError))
					JOptionPane.showMessageDialog(pnlMain.getPnlGuiConfig(), "Invalid expression!\n\n" + validationError);
				else
					JOptionPane.showMessageDialog(pnlMain.getPnlGuiConfig(), "The expression looks good!");
			}
		});
		
		add(cmbDynaBit);
		add(cmbDynaOperator);
		add(lblDynaValue);
		add(txtDynaValue);
		add(btnDynaAdd);
		add(scrDynaCondition);
		add(btnDynaValidate);

	}
	
	public void loadDynamicFields() {
		FieldVO fieldVO = null;
		FieldVO selectedFieldVO = (FieldVO) ((DefaultMutableTreeNode) pnlMain.getPnlGuiConfig().getSelectedNode()).getUserObject();
		cmbDynaBit.removeAllItems();
		cmbDynaBit.addItem(new CmbItemVO("-- BIT --", "-- BIT --"));
		for (int i = 0; i < pnlMain.getPnlGuiConfig().getSelectedNodeParent().getChildCount(); i++) {
			fieldVO = (FieldVO) ((DefaultMutableTreeNode) pnlMain.getPnlGuiConfig().getSelectedNodeParent().getChildAt(i)).getUserObject();
			if (fieldVO.getBitNum().intValue() < selectedFieldVO.getBitNum().intValue())
				cmbDynaBit.addItem(new CmbItemVO("BIT[" + fieldVO.getBitNum() + "] " + fieldVO.getName(), "BIT[" + fieldVO.getBitNum() + "]"));
		}
		
		cmbDynaBit.addActionListener(new AddLogicActionListener(cmbDynaBit));
	}

	public void setMandatory(boolean value) {
		if (value) {
			txtDynaCondition.setText("return true;");
		}
		else {
			txtDynaCondition.setText("");
		}
	}
	
	public void setIgnored(boolean value) {
		txtDynaCondition.setText(value ? "return ignore();" : "");
	}
	
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		
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
		}
	}
	
	public void clear() {
		cmbDynaBit.removeAllItems();
		cmbDynaOperator.setSelectedIndex(0);
		txtDynaValue.setText("");
		txtDynaCondition.setText("");
		
		final boolean fieldIsMandatory = pnlMain.getPnlGuiConfig().getPnlFieldProperties().getChckbxMandatory().isSelected();
		final boolean fieldIsIgnored = pnlMain.getPnlGuiConfig().getPnlFieldProperties().getChckbxIgnored().isSelected();
		if (fieldIsMandatory) {
			txtDynaCondition.setText("return true;");
		}
		else if (fieldIsIgnored) {
			txtDynaCondition.setText("return ignore();");
		}
		
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
				
				pnlMain.getPnlGuiConfig().save(pnlMain);
				pnlMain.getPnlGuiConfig().getTree().updateUI();
			}
		}
	}
}

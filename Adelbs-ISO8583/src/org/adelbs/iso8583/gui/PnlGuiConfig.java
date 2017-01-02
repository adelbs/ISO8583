package org.adelbs.iso8583.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultComboBoxModel;
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
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.constants.OperatorEnum;
import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.constants.TypeLengthEnum;
import org.adelbs.iso8583.helper.Iso8583Helper;
import org.adelbs.iso8583.helper.SortTreeHelper;
import org.adelbs.iso8583.vo.CmbItemVO;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.MessageVO;
import javax.swing.SwingConstants;

public class PnlGuiConfig extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	private JLabel lblExpand = new JLabel("[Expand All]");
	private JLabel lblCollapse = new JLabel("[Collapse All]");
	private static final JCheckBox chckbxShowBitNum = new JCheckBox("Show bit #");
	
	//Painel Propriedades da mensagem ************************
	private JPanel pnlMessageProp = new JPanel();
	private JLabel lblMsgType = new JLabel("Message Type");
	private JTextField txtMsgType = new JTextField();
	private JLabel lblHeaderEncoding = new JLabel("Encoding");
	private JComboBox<EncodingEnum> cmbHeaderEncoding = new JComboBox<EncodingEnum>();
	
	//Painel Propriedades do campo **************************
	private JPanel pnlProperties = new JPanel();
	
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
	
	//Painel Dynamic ********************************************
	private JPanel pnlDynamic = new JPanel();
	private JCheckBox ckDynamic = new JCheckBox("Dynamic");
	private JComboBox<CmbItemVO> cmbDynaBit = new JComboBox<CmbItemVO>();
	private JComboBox<CmbItemVO> cmbDynaOperator = new JComboBox<CmbItemVO>();
	private JLabel lblDynaValue = new JLabel("Value");
	private JTextField txtDynaValue = new JTextField();
	private JButton btnDynaAdd = new JButton("");
	private JButton btnDynaValidate = new JButton("");
	private JTextArea txtDynaCondition = new JTextArea();
	private JScrollPane scrDynaCondition = new JScrollPane();
	
	//Tree
	private JTree treeTypes;
	private JScrollPane scrTreeTypes = new JScrollPane();
	
	//Botões
	private JButton btnNew = new JButton();
	private JButton btnNewField = new JButton();
	private JButton btnRemove = new JButton();
	
	//Campo para auxiliar no manuseio da arvore
	private DefaultMutableTreeNode selectedNode;
	private DefaultMutableTreeNode selectedNodeParent;

	private SaveKeyListener saveKeyListener = new SaveKeyListener();
	
	public PnlGuiConfig(final PnlMain pnlMain) {
		setLocation(-225, -183);
		setLayout(null);
		
		//######### Apenas para visualizacao no WindowBuilder *********************************
		scrTreeTypes.setBounds(12, 20, 246, 350); //FAKE!!!!!
		pnlMessageProp.setBounds(271, 12, 450, 60); //FAKE!!!!
		pnlProperties.setBounds(271, pnlMessageProp.getHeight() + pnlMessageProp.getY() + 10, 450, 185); //FAKE!!!!!
		pnlDynamic.setBounds(270, pnlProperties.getHeight() + pnlProperties.getY() + 10, 450, 200); //FAKE!!!!!
		
		//SINCRONIZAR VALORES ABAIXO COM O RESIZE
		btnNew.setBounds(12, scrTreeTypes.getY() + scrTreeTypes.getHeight() + 10, 246, 32);
		btnNewField.setBounds(12, scrTreeTypes.getY() + scrTreeTypes.getHeight() + 50, 115, 32);
		btnRemove.setBounds(143, scrTreeTypes.getY() + scrTreeTypes.getHeight() + 50, 115, 32);
		txtName.setBounds(101, 24, pnlProperties.getWidth() - 110, 22);
		txtSubField.setBounds(101, 56, pnlProperties.getWidth() - 110, 22);
		scrDynaCondition.setBounds(15, 100, pnlDynamic.getWidth() - 55, pnlDynamic.getHeight() - 109);
		btnDynaValidate.setBounds(pnlDynamic.getWidth() - 35, pnlDynamic.getHeight() - 35, 25, 25);
		//***************************************************************************
		
		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentResized(ComponentEvent e) {
				//Arvore
				scrTreeTypes.setBounds(12, 20, 246, getHeight() - 115);
				
				//Botoes abaixo da arvore
				btnNew.setBounds(12, scrTreeTypes.getY() + scrTreeTypes.getHeight() + 10, 246, 32);
				btnNewField.setBounds(12, scrTreeTypes.getY() + scrTreeTypes.getHeight() + 50, 115, 32);
				btnRemove.setBounds(143, scrTreeTypes.getY() + scrTreeTypes.getHeight() + 50, 115, 32);
				
				//Paineis de propriedades
				pnlMessageProp.setBounds(271, 12, getWidth() - 280, 60);
				
				pnlProperties.setBounds(271, pnlMessageProp.getHeight() + pnlMessageProp.getY() + 10, getWidth() - 280, 185);
				pnlDynamic.setBounds(270, pnlProperties.getHeight() + pnlProperties.getY() + 10, getWidth() - 280, getHeight() - pnlProperties.getHeight() - 103);
				
				//Campos
				txtName.setBounds(101, 24, pnlProperties.getWidth() - 110, 22);
				txtSubField.setBounds(101, 56, pnlProperties.getWidth() - 110, 22);
				scrDynaCondition.setBounds(15, 100, pnlDynamic.getWidth() - 55, pnlDynamic.getHeight() - 109);
				btnDynaValidate.setBounds(pnlDynamic.getWidth() - 35, pnlDynamic.getHeight() - 35, 25, 25);
			}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		}); 
		
		//Configurando botões
		btnNew.setIcon(new ImageIcon(PnlGuiConfig.class.getResource("/resource/addType.png")));
		btnNew.setText("Add Message Type");
		btnNew.setToolTipText("Add Message Type");
		btnNewField.setIcon(new ImageIcon(PnlGuiConfig.class.getResource("/resource/addField.png")));
		btnNewField.setText("Add Field");
		btnNewField.setToolTipText("Add Field");
		btnRemove.setIcon(new ImageIcon(PnlGuiConfig.class.getResource("/resource/remove.png")));
		btnRemove.setText("Remove");
		btnRemove.setToolTipText("Remove");
		
		btnNewField.setEnabled(false);
		btnRemove.setEnabled(false);
		
		add(btnNew);
		add(btnNewField);
		add(btnRemove);

		//Ações dos botões
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
				DefaultMutableTreeNode newNode = Iso8583Helper.getInstance().addType();
				treeTypes.setSelectionPath(new TreePath(newNode.getPath()));
				
				save();
				treeTypes.updateUI();
				treeTypes.expandRow(0);
			}
		});
		btnNewField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
				DefaultMutableTreeNode newNode = Iso8583Helper.getInstance().addField(treeTypes.getLastSelectedPathComponent());

				if (selectedNode.getUserObject() instanceof FieldVO) {
					disableSuperField();
					Iso8583Helper.getInstance().updateSumField(treeTypes.getLastSelectedPathComponent());
					loadFieldValues();
				}
				
				treeTypes.setSelectionPath(new TreePath(newNode.getPath()));
				
				save();
				treeTypes.updateUI();
			}
		});
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
				Iso8583Helper.getInstance().removeNode(pnlMain, treeTypes.getLastSelectedPathComponent());
				treeTypes.updateUI();
				treeTypes.expandRow(0);

				btnNewField.setEnabled(false);
				btnRemove.setEnabled(false);
				enableMessageProp(false);
				enableProperties(false);
				enableDynamic(false, true);
				clearMessageProp();
				clearProperties();
				clearDynamic();
			}
		});
		
		//Configurando paineis
		pnlMessageProp.setLayout(null);
		pnlMessageProp.setBorder(new TitledBorder(null, "Message Properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlProperties.setLayout(null);
		pnlProperties.setBorder(new TitledBorder(null, "Field Properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlDynamic.setLayout(null);
		pnlDynamic.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Condition", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		add(pnlMessageProp);
		add(pnlProperties);
		add(pnlDynamic);

		//Painel Message type **********************************************************************************************************
		
		lblMsgType.setBounds(12, 27, 88, 16);
		txtMsgType.setColumns(10);
		txtMsgType.setBounds(101, 24, 70, 22);
		txtMsgType.addKeyListener(saveKeyListener);
		
		lblHeaderEncoding.setBounds(200, 27, 88, 16);
		cmbHeaderEncoding.setBounds(262, 24, 116, 22);
		cmbHeaderEncoding.setModel(new DefaultComboBoxModel<EncodingEnum>(new EncodingEnum[] {
				EncodingEnum.UTF8, EncodingEnum.EBCDIC, 
				EncodingEnum.ISO88591, EncodingEnum.BINARY}));
		
		pnlMessageProp.add(lblMsgType);
		pnlMessageProp.add(txtMsgType);
		pnlMessageProp.add(lblHeaderEncoding);
		pnlMessageProp.add(cmbHeaderEncoding);
		
		//Painel proprerties **************************************************************************************
		lblName.setBounds(12, 27, 83, 16);
		lblName.setHorizontalAlignment(SwingConstants.RIGHT);
		txtName.setColumns(10);
		txtName.addKeyListener(saveKeyListener);
		
		lblSubfield.setBounds(10, 59, 83, 16);
		lblSubfield.setHorizontalAlignment(SwingConstants.RIGHT);
		txtSubField.setColumns(10);
		txtSubField.addKeyListener(saveKeyListener);
		lblSubfield.setEnabled(false);
		txtSubField.setEnabled(false);

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
		
		pnlProperties.add(lblName);
		pnlProperties.add(txtName);
		pnlProperties.add(lblSubfield);
		pnlProperties.add(txtSubField);
		pnlProperties.add(lblNum);
		pnlProperties.add(txtNum);
		pnlProperties.add(lblType);
		pnlProperties.add(cmbType);
		pnlProperties.add(lblLenght);
		pnlProperties.add(txtLength);
		pnlProperties.add(lblEncoding);
		pnlProperties.add(cmbEncoding);
		pnlProperties.add(chckbxMandatory);
		pnlProperties.add(cmbLength);
		pnlProperties.add(lblLenValue);
		
		//Painel Dynamic **********************************************************************************************************
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
					
					save();
					treeTypes.updateUI();
				}
			}
		});
		
		btnDynaValidate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
				String validationError = Iso8583Helper.getInstance().validateCondition((FieldVO) selectedNode.getUserObject());
				if (!"".equals(validationError))
					JOptionPane.showMessageDialog(pnlMain, "Invalid expression!\n\n" + validationError);
				else
					JOptionPane.showMessageDialog(pnlMain, "The expression looks good!");
			}
		});
		
		pnlDynamic.add(ckDynamic);
		pnlDynamic.add(cmbDynaBit);
		pnlDynamic.add(cmbDynaOperator);
		pnlDynamic.add(lblDynaValue);
		pnlDynamic.add(txtDynaValue);
		pnlDynamic.add(btnDynaAdd);
		pnlDynamic.add(scrDynaCondition);
		pnlDynamic.add(btnDynaValidate);
		
		//TabbedPane e Tree
		treeTypes = new JTree(Iso8583Helper.getInstance().getConfigTreeNode());
		treeTypes.setCellRenderer(new ISOTreeRenderer());
		treeTypes.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		//Ao selecionar um no da arvore
		treeTypes.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				save();
				
				selectedNode = (DefaultMutableTreeNode) treeTypes.getLastSelectedPathComponent();
				selectedNodeParent = (selectedNode == null || selectedNode.getParent() == null ? null : (DefaultMutableTreeNode) selectedNode.getParent());
				
				ckDynamic.setEnabled(false);
				clearMessageProp();
				clearProperties();
				clearDynamic();
				
				if (selectedNode != null) {
					loadFieldValues();
					
					//Habilitando os campos
					if (selectedNode.getUserObject() instanceof FieldVO) {
						enableMessageProp(false);
						enableProperties(true);
						enableDynamic(true, true);
						ckDynamicClick();
						disableSuperField();
					}
					else {
						enableMessageProp(!(selectedNode.getUserObject() instanceof String));
						enableProperties(false);
						enableDynamic(false, true);
					}
	
					boolean isParentField = (selectedNodeParent != null && (selectedNodeParent.getUserObject() instanceof FieldVO));
					lblSubfield.setEnabled(isParentField);
					txtSubField.setEnabled(isParentField);
	
					//Habilitando os botões
					if (selectedNode.getUserObject() instanceof MessageVO) {
						btnNewField.setEnabled(true);
						btnRemove.setEnabled(true);
					}
					else if (selectedNode.getUserObject() instanceof FieldVO) {
						btnRemove.setEnabled(true);
						
						if (selectedNodeParent.getUserObject() instanceof FieldVO) 
							btnNewField.setEnabled(false);
						else 
							btnNewField.setEnabled(true);
					}
					else {
						btnNewField.setEnabled(false);
						btnRemove.setEnabled(false);
					}
				}
			}
		});

		add(scrTreeTypes);

		lblCollapse.setForeground(SystemColor.textHighlight);
		lblCollapse.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		add(lblCollapse);
		
		lblExpand.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblExpand.setForeground(new Color(51, 153, 255));
		add(lblExpand);
		
		lblCollapse.setBounds(12, 0, 81, 20);
		lblExpand.setBounds(95, 0, 75, 20);

		lblCollapse.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {
				treeTypes.updateUI();
				for (int i = treeTypes.getRowCount() - 1; i > 0; i--)
					treeTypes.collapseRow(i);
			}
		});

		lblExpand.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {
				treeTypes.updateUI();
				for (int i = 1; i < treeTypes.getRowCount(); i++)
					treeTypes.expandRow(i);
			}
		});
		
		chckbxShowBitNum.setBounds(171, -2, 91, 22);
		chckbxShowBitNum.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				treeTypes.updateUI();
			}
		});
		add(chckbxShowBitNum);
		
		scrTreeTypes.setViewportView(treeTypes);
		
		enableProperties(false);
		enableDynamic(false, true);
		
		chckbxMandatory.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chckbxMandatory.isSelected()) {
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
		});
		
		ckDynamic.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ckDynamicClick();
			}
		});
	}
	
	public void save() {
		try {
			if (selectedNode != null) {
				if (selectedNode.getUserObject() instanceof MessageVO) {
					Iso8583Helper.getInstance().saveMessage((MessageVO) selectedNode.getUserObject(), txtMsgType.getText(), (EncodingEnum) cmbHeaderEncoding.getSelectedItem());
					Iso8583Helper.getInstance().validateNode((MessageVO) selectedNode.getUserObject(), selectedNodeParent);
				}
				else if (selectedNode.getUserObject() instanceof FieldVO) {
					Iso8583Helper.getInstance().saveField((FieldVO) selectedNode.getUserObject(), txtName.getText(), 
							txtSubField.getText(),txtNum.getText(), (TypeEnum) cmbType.getSelectedItem(), (TypeLengthEnum) cmbLength.getSelectedItem(), txtLength.getText(), 
							(EncodingEnum) cmbEncoding.getSelectedItem(), txtDynaCondition.getText());
					
					if (selectedNodeParent.getUserObject() instanceof FieldVO)
						Iso8583Helper.getInstance().updateSumField(selectedNodeParent);
					else
						Iso8583Helper.getInstance().updateSumField(selectedNode);
				
					Iso8583Helper.getInstance().validateNode((FieldVO) selectedNode.getUserObject(), selectedNodeParent);
				}
				
				SortTreeHelper.sortTree(selectedNodeParent, treeTypes);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void loadFieldValues() {
		MessageVO messageVo = null;
		
		if (selectedNode.getUserObject() instanceof FieldVO) {
			FieldVO fieldVo = (FieldVO) selectedNode.getUserObject();
			txtName.setText(fieldVo.getName());
			txtSubField.setText(fieldVo.getSubFieldName());
			txtNum.setText(String.valueOf(fieldVo.getBitNum()));
			cmbType.setSelectedItem(fieldVo.getType());
			cmbLength.setSelectedItem(fieldVo.getTypeLength());
			txtLength.setText(String.valueOf(fieldVo.getLength()));
			cmbEncoding.setSelectedItem(fieldVo.getEncoding());
			chckbxMandatory.setSelected(fieldVo.getDynaCondition().equals("true"));
			
			cmbDynaOperator.setSelectedItem(fieldVo.getDynaCondition());
			txtDynaCondition.setText(fieldVo.getDynaCondition());
			ckDynamic.setSelected(fieldVo.getDynaCondition() != null && !fieldVo.getDynaCondition().trim().equals("") && !fieldVo.getDynaCondition().trim().equals("true"));
			
			cmbLengthClick();
		}

		if (selectedNode.getUserObject() instanceof String)
			messageVo = null;
		else if (selectedNode.getUserObject() instanceof MessageVO)
			messageVo = (MessageVO) selectedNode.getUserObject();
		else if (((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject() instanceof MessageVO)
			messageVo = (MessageVO) ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject();
		else if (((DefaultMutableTreeNode) selectedNode.getParent().getParent()).getUserObject() instanceof MessageVO)
			messageVo = (MessageVO) ((DefaultMutableTreeNode) selectedNode.getParent().getParent()).getUserObject();
		
		if (messageVo != null) {
			txtMsgType.setText(messageVo.getType());
			cmbHeaderEncoding.setSelectedItem(messageVo.getBitmatEncoding());
		}
		
	}

	private void enableMessageProp(boolean value) {
		pnlMessageProp.setEnabled(value);
		lblMsgType.setEnabled(value);
		txtMsgType.setEnabled(value);
		lblHeaderEncoding.setEnabled(value);
		cmbHeaderEncoding.setEnabled(value);
	}
	
	private void enableProperties(boolean value) {
		pnlProperties.setEnabled(value);
		lblName.setEnabled(value);
		txtName.setEnabled(value);
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
	}
	
	private void enableDynamic(boolean value, boolean full) {
		ckDynamic.setEnabled(!txtDynaCondition.getText().equals("true"));
		ckDynamic.setEnabled(full ? value : ckDynamic.isEnabled());
		
		cmbDynaBit.setEnabled(value);
		cmbDynaOperator.setEnabled(value);
		lblDynaValue.setEnabled(value);
		txtDynaValue.setEnabled(value);
		btnDynaAdd.setEnabled(value);
		txtDynaCondition.setEnabled(value);
		btnDynaValidate.setEnabled(value);
	}

	private void disableSuperField() {
		if (selectedNode.getChildCount() > 0) {
			cmbType.setSelectedIndex(0);
			lblType.setEnabled(false);
			cmbType.setEnabled(false);
			lblLenght.setEnabled(false);
			lblLenValue.setEnabled(false);
			txtLength.setEnabled(false);
			cmbLength.setEnabled(false);
			lblEncoding.setEnabled(false);
			cmbEncoding.setEnabled(false);
			
			lblNum.setText("Bit Num");
		}
		
		if (selectedNodeParent.getUserObject() instanceof FieldVO) {
			lblName.setEnabled(false);
			txtName.setEnabled(false);
			chckbxMandatory.setSelected(false);
			chckbxMandatory.setEnabled(false);
			ckDynamic.setSelected(false);
			ckDynamic.setEnabled(false);
			lblNum.setText("Order");
		}
	}
	
	private void clearMessageProp() {
		txtMsgType.setText("");
		cmbHeaderEncoding.setSelectedIndex(0);
	}
	
	private void clearProperties() {
		txtName.setText("");
		txtNum.setText("");
		cmbType.setSelectedIndex(0);
		txtLength.setText("");
		cmbEncoding.setSelectedIndex(0);
		chckbxMandatory.setSelected(false);
		cmbLength.setSelectedIndex(0);
	}
	
	private void ckDynamicClick() {
		if (ckDynamic.isSelected()) {
			enableDynamic(true, false);
			
			FieldVO fieldVO = null;
			cmbDynaBit.removeAllItems();
			cmbDynaBit.addItem(new CmbItemVO("-- BIT --", "-- BIT --"));
			for (int i = 0; i < selectedNodeParent.getChildCount(); i++) {
				fieldVO = (FieldVO) ((DefaultMutableTreeNode) selectedNodeParent.getChildAt(i)).getUserObject();
				cmbDynaBit.addItem(new CmbItemVO("BIT[" + fieldVO.getBitNum() + "] " + fieldVO.getName(), "BIT[" + fieldVO.getBitNum() + "]"));
			}
			
			cmbDynaBit.addActionListener(new AddLogicActionListener(cmbDynaBit));
		}
		else {
			enableDynamic(false, false);
			clearDynamic();
		}
	}
	
	private void cmbLengthClick() {
		if (cmbLength.getSelectedItem() == TypeLengthEnum.NVAR)
			lblLenValue.setText("Num of Bits");
		else 
			lblLenValue.setText("Length value");
	}
	
	private void clearDynamic() {
		ckDynamic.setSelected(false);
		cmbDynaBit.removeAllItems();
		cmbDynaOperator.setSelectedIndex(0);
		txtDynaValue.setText("");
		txtDynaCondition.setText(chckbxMandatory.isSelected() ? "true" : "");
	}
	
	public void updateTree() {
		clearMessageProp();
		clearProperties();
		clearDynamic();
		enableMessageProp(false);
		enableProperties(false);
		enableDynamic(false, true);
		
		treeTypes.updateUI();
	}
	
	public void expandAllNodes() {
		expandAllNodes(treeTypes, 0, treeTypes.getRowCount());
	}
	
	private void expandAllNodes(JTree tree, int startingIndex, int rowCount){
	    for (int i=startingIndex;i<rowCount;++i) {
	        tree.expandRow(i);
	    }

	    if (tree.getRowCount()!=rowCount) {
	        expandAllNodes(tree, rowCount, tree.getRowCount());
	    }
	}

	public JTree getTree() {
		return treeTypes;
	}
	
	public static boolean isShowBitNum() {
		return chckbxShowBitNum.isSelected();
	}
	
	private class SaveKeyListener implements KeyListener {
		public void keyTyped(KeyEvent e) { }
		public void keyReleased(KeyEvent e) {
			save();
			treeTypes.updateUI();
		}
		public void keyPressed(KeyEvent e) { }
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
				
				save();
				treeTypes.updateUI();
			}
		}
	}
}
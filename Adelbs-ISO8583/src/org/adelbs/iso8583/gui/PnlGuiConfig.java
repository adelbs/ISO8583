package org.adelbs.iso8583.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.adelbs.iso8583.helper.Iso8583Helper;
import org.adelbs.iso8583.helper.SortTreeHelper;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.GenericIsoVO;
import org.adelbs.iso8583.vo.MessageVO;

public class PnlGuiConfig extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	private JLabel lblExpand = new JLabel("[Expand All]");
	private JLabel lblCollapse = new JLabel("[Collapse All]");
	private JCheckBox chckbxShowBitNum = new JCheckBox("Show bit #");
	
	private PnlMessageProperties pnlMessageProperties;
	private PnlFieldProperties pnlFieldProperties;
	private PnlFieldCondition pnlFieldCondition;
	
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
	
	public PnlGuiConfig() {
		setLocation(-225, -183);
		setLayout(null);
		
		pnlMessageProperties = new PnlMessageProperties(saveKeyListener);
		pnlFieldProperties = new PnlFieldProperties(saveKeyListener);
		pnlFieldCondition = new PnlFieldCondition();
		
		//######### Apenas para visualizacao no WindowBuilder *********************************
		scrTreeTypes.setBounds(12, 20, 246, 350); //FAKE!!!!!
		pnlMessageProperties.setBounds(271, 12, 450, 60); //FAKE!!!!
		pnlFieldProperties.setBounds(271, pnlMessageProperties.getHeight() + pnlMessageProperties.getY() + 10, 450, 185); //FAKE!!!!!
		pnlFieldCondition.setBounds(270, pnlFieldProperties.getHeight() + pnlFieldProperties.getY() + 10, 450, 200); //FAKE!!!!!
		
		//SINCRONIZAR VALORES ABAIXO COM O RESIZE
		btnNew.setBounds(12, scrTreeTypes.getY() + scrTreeTypes.getHeight() + 10, 246, 32);
		btnNewField.setBounds(12, scrTreeTypes.getY() + scrTreeTypes.getHeight() + 50, 115, 32);
		btnRemove.setBounds(143, scrTreeTypes.getY() + scrTreeTypes.getHeight() + 50, 115, 32);
		pnlFieldProperties.setBounds(101, 24, pnlFieldProperties.getWidth() - 110, 22);
		pnlFieldProperties.setBounds(101, 56, pnlFieldProperties.getWidth() - 110, 22);
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
				pnlMessageProperties.setBounds(271, 12, getWidth() - 295, 90);
				pnlFieldProperties.setBounds(271, pnlMessageProperties.getHeight() + pnlMessageProperties.getY() + 10, getWidth() - 295, 185);
				pnlFieldCondition.setBounds(270, pnlFieldProperties.getHeight() + pnlFieldProperties.getY() + 10, getWidth() - 295, 
						getHeight() - pnlMessageProperties.getHeight() - pnlFieldProperties.getHeight() - 43);
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
					checkFieldsEnablement();
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
				removeNode(treeTypes.getLastSelectedPathComponent());
				treeTypes.updateUI();
				treeTypes.expandRow(0);

				btnNewField.setEnabled(false);
				btnRemove.setEnabled(false);
				
				pnlMessageProperties.setEnabled(false);
				pnlFieldProperties.setEnabled(false);
				pnlFieldCondition.setEnabled(false);
				pnlMessageProperties.clear();
				pnlFieldProperties.clear();
				pnlFieldCondition.clear();
			}
		});
		
		add(pnlMessageProperties);
		add(pnlFieldProperties);
		add(pnlFieldCondition);

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
				
				pnlMessageProperties.clear();
				pnlFieldProperties.clear();
				pnlFieldCondition.clear();
				
				if (selectedNode != null) {
					loadFieldValues();
					
					//Habilitando os campos
					if (selectedNode.getUserObject() instanceof FieldVO) {
						pnlMessageProperties.setEnabled(false);
						pnlFieldProperties.setEnabled(true);
						pnlFieldCondition.setEnabled(true);
						checkFieldsEnablement();
					}
					else {
						pnlMessageProperties.setEnabled(!(selectedNode.getUserObject() instanceof String));
						pnlFieldProperties.setEnabled(false);
						pnlFieldCondition.setEnabled(false);
					}
	
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
		
		pnlFieldProperties.setEnabled(false);
		pnlFieldCondition.setEnabled(false);
	}
	
	public void save() {
		try {
			if (selectedNode != null) {
				if (selectedNode.getUserObject() instanceof MessageVO) {
					pnlMessageProperties.save((MessageVO) selectedNode.getUserObject());
				}
				else if (selectedNode.getUserObject() instanceof FieldVO) {
					pnlFieldProperties.save((FieldVO) selectedNode.getUserObject());
					pnlFieldCondition.save((FieldVO) selectedNode.getUserObject());
					
					if (selectedNodeParent.getUserObject() instanceof FieldVO)
						Iso8583Helper.getInstance().updateSumField(selectedNodeParent);
					else
						Iso8583Helper.getInstance().updateSumField(selectedNode);
				
				}
				
				if (!(selectedNode.getUserObject() instanceof String))
					Iso8583Helper.getInstance().validateNode((GenericIsoVO) selectedNode.getUserObject(), selectedNodeParent);
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
			
			pnlFieldProperties.load(fieldVo);
			pnlFieldCondition.load(fieldVo);
		}

		if (selectedNode.getUserObject() instanceof String)
			messageVo = null;
		else if (selectedNode.getUserObject() instanceof MessageVO)
			messageVo = (MessageVO) selectedNode.getUserObject();
		else if (((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject() instanceof MessageVO)
			messageVo = (MessageVO) ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject();
		else if (((DefaultMutableTreeNode) selectedNode.getParent().getParent()).getUserObject() instanceof MessageVO)
			messageVo = (MessageVO) ((DefaultMutableTreeNode) selectedNode.getParent().getParent()).getUserObject();
		
		pnlMessageProperties.load(messageVo);
	}

	private void checkFieldsEnablement() {
		if (selectedNode.getChildCount() > 0) {
			pnlFieldProperties.disableSuperField();
			pnlFieldCondition.ckDynamicClick();
		}
		else if (selectedNodeParent.getUserObject() instanceof FieldVO) {
			pnlFieldProperties.disableSubField();
			pnlFieldCondition.setEnabled(false);
		}
		else {
			pnlFieldCondition.ckDynamicClick();
		}
	}
	
	public void updateTree() {
		pnlMessageProperties.clear();
		pnlFieldProperties.clear();
		pnlFieldCondition.clear();
		pnlMessageProperties.setEnabled(false);
		pnlFieldProperties.setEnabled(false);
		pnlFieldCondition.setEnabled(false);
		
		treeTypes.updateUI();
	}
	
	public void expandAllNodes() {
		expandAllNodes(treeTypes, 0, treeTypes.getRowCount());
	}
	
	private void expandAllNodes(JTree tree, int startingIndex, int rowCount){
	    for (int i=startingIndex;i<rowCount;++i)
	        tree.expandRow(i);

	    if (tree.getRowCount()!=rowCount)
	        expandAllNodes(tree, rowCount, tree.getRowCount());
	}

	public JTree getTree() {
		return treeTypes;
	}

	public DefaultMutableTreeNode getSelectedNode() {
		return selectedNode;
	}
	
	public DefaultMutableTreeNode getSelectedNodeParent() {
		return selectedNodeParent;
	}
	
	public PnlMessageProperties getPnlMessageProperties() {
		return pnlMessageProperties;
	}
	
	public PnlFieldProperties getPnlFieldProperties() {
		return pnlFieldProperties;
	}
	
	public PnlFieldCondition getPnlFieldCondition() {
		return pnlFieldCondition;
	}
	
	public boolean isShowBitNum() {
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

	private void removeNode(Object node) {
		if (node != null && !((DefaultMutableTreeNode) node).isRoot()) {
			if (JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this Object?", "Remove Object", JOptionPane.YES_NO_OPTION) == 0)
				((DefaultMutableTreeNode) node).removeFromParent();
		}
	}

}
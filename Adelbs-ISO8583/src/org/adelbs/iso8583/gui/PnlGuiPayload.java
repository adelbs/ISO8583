package org.adelbs.iso8583.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;

import org.adelbs.iso8583.gui.xmlEditor.XmlTextPane;
import org.adelbs.iso8583.helper.GuiPayloadMessageHelper;
import org.adelbs.iso8583.helper.Iso8583Helper;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.GenericIsoVO;
import org.adelbs.iso8583.vo.MessageVO;

public class PnlGuiPayload extends JPanel {

	private static final long serialVersionUID = 1L;

	private GuiPayloadMessageHelper guiPayloadMessageHelper;
	
	private JLabel lblMessageType = new JLabel("Message Type");
	private JComboBox<MessageVO> cmbMessageType = new JComboBox<MessageVO>();
	private JButton btnUpdate = new JButton();
	private JButton btnSendResponse = new JButton("Send Response");
	private JButton btnOpenPayload = new JButton("Open Payload");
	private JButton btnSavePayload = new JButton("Save Payload");
	
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	
	private JPanel pnlFormattedFields = new JPanel();
	private JPanel pnlFields = new JPanel();
	private JLabel lblBit = new JLabel("Bit #");
	private JLabel lblFieldName = new JLabel("Field Name");
	private JLabel lblFieldValue = new JLabel("Field Value");
	private JLabel lblType = new JLabel("Type");
	private JLabel lblDynamic = new JLabel("Dynamic");
	private JScrollPane scrFields = new JScrollPane();

	private JPanel pnlXML = new JPanel();
	private JScrollPane scrXML = new JScrollPane();
	private XmlTextPane xmlText = new XmlTextPane();
	
	private JPanel pnlRawMessage = new JPanel();
	private JScrollPane scrRawMessage = new JScrollPane();
	private JTextArea txtRawMessage = new JTextArea();
	
	public PnlGuiPayload(boolean server, boolean request) {
		setLayout(null);
		
		lblMessageType.setBounds(12, 13, 90, 16);
		cmbMessageType.setBounds(114, 10, 178, 22);
		btnUpdate.setBounds(295, 10, 22, 22);
		btnUpdate.setIcon(new ImageIcon(PnlGuiPayload.class.getResource("/resource/update.png")));
		btnSendResponse.setIcon(new ImageIcon(PnlGuiPayload.class.getResource("/resource/enter.png")));
		btnOpenPayload.setIcon(new ImageIcon(PnlGuiPayload.class.getResource("/resource/openFile.png")));
		btnSavePayload.setIcon(new ImageIcon(PnlGuiPayload.class.getResource("/resource/saveFile.png")));

		tabbedPane.setEnabled(false);
		if (server) {
			enablePnl(false);
			if (request) {
				add(btnSavePayload);
			}
			else {
				add(lblMessageType);
				add(cmbMessageType);
				add(btnUpdate);
				add(btnOpenPayload);
				add(btnSendResponse);
			}
		}
		
		if (!server) {
			if (request) {
				add(lblMessageType);
				add(cmbMessageType);
				add(btnUpdate);
				add(btnOpenPayload);
				add(btnSavePayload);
			}
			else {
				add(btnSavePayload);
				enablePnl(false);
			}
		}
		
		btnUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateCmbMessage();
			}
		});
		
		cmbMessageType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cmbClick();
			}
		});
		
		//######### Apenas para visualizacao no WindowBuilder *********************************
		
		btnOpenPayload.setBounds(430, 9, 143, 25);
		btnSavePayload.setBounds(585, 9, 143, 25);
		tabbedPane.setBounds(12, 42, 716, 516);
		
		//Formatted fields
		scrFields.setBounds(12, 32, 681, 432);
		
		//***************************************************************************
		
		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentResized(ComponentEvent e) {
				
				btnSendResponse.setBounds(getWidth() - 160, 9, 143, 25);
				btnOpenPayload.setBounds(getWidth() - 310, 9, 143, 25);
				btnSavePayload.setBounds(getWidth() - 160, 9, 143, 25);
				tabbedPane.setBounds(12, 42, getWidth() - 25, getHeight() - 55);
				
				//Formatted fields
				scrFields.setBounds(0, 32, tabbedPane.getWidth() - 5, tabbedPane.getHeight() - 60);
			}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		}); 

		add(tabbedPane);
		
		//Formatted fields ***************************************************************************
		tabbedPane.addTab("Formatted Fields", pnlFormattedFields);
		pnlFormattedFields.setLayout(null);
		
		lblBit.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblBit.setBounds(30, 10, 40, 16);
		pnlFormattedFields.add(lblBit);
		
		lblFieldName.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblFieldName.setBounds(80, 10, 88, 16);
		pnlFormattedFields.add(lblFieldName);
		
		lblFieldValue.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblFieldValue.setBounds(190, 10, 77, 16);
		pnlFormattedFields.add(lblFieldValue);
		
		lblType.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblType.setBounds(470, 10, 56, 16);
		pnlFormattedFields.add(lblType);

		lblDynamic.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblDynamic.setBounds(580, 10, 70, 16);
		pnlFormattedFields.add(lblDynamic);
		
		pnlFormattedFields.add(scrFields);
		
		scrFields.setViewportView(pnlFields);
		pnlFields.setLayout(null);

		//XML *************************************************************************************
		pnlXML.setLayout(new BorderLayout(0, 0));
		scrXML.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrXML.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrXML.setViewportView(xmlText);
		pnlXML.add(scrXML);
		tabbedPane.addTab("XML", pnlXML);
		
		tabbedPane.addTab("Raw message", pnlRawMessage);
		scrRawMessage.setViewportView(txtRawMessage);
		pnlRawMessage.setLayout(new BorderLayout(0, 0));
		pnlRawMessage.add(scrRawMessage);
		
		btnSavePayload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (guiPayloadMessageHelper.getIsoMessage() == null) {
					JOptionPane.showMessageDialog(FrmMain.getInstance(), "There is no data to save.");
				}
				else {
					String filePath;
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setAcceptAllFileFilterUsed(false);
					fileChooser.setFileFilter(new FileNameExtensionFilter("ISO-8583 files (*.iso8583)", "iso8583"));
					if (fileChooser.showSaveDialog(FrmMain.getInstance()) == JFileChooser.APPROVE_OPTION) {
						filePath = fileChooser.getSelectedFile().getAbsolutePath();
						if (filePath.indexOf(".iso8583") < 0) filePath = filePath + ".iso8583";
						
						FileOutputStream fos = null;
						try {
							if (filePath != null && !filePath.equals("")) {
								File file = new File(filePath);
								if (!file.exists())
									file.createNewFile();
								
								fos = new FileOutputStream(filePath);
								fos.write(guiPayloadMessageHelper.getIsoMessage().getPayload());
								
								JOptionPane.showMessageDialog(FrmMain.getInstance(), "Payload saved!");
							}
						} 
						catch (Exception x) {
							x.printStackTrace();
							JOptionPane.showMessageDialog(FrmMain.getInstance(), "Error saving the Payload file. See the log.\n\n"+ x.getMessage());
						}
						finally {
							if (fos != null) {
								try {
									fos.close();
								}
								catch (Exception x) {
									x.printStackTrace();
								}
							}
						}
					}
				}
			}
		});
		
		btnOpenPayload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser file = new JFileChooser();
				file.setAcceptAllFileFilterUsed(false);
				file.setFileFilter(new FileNameExtensionFilter("ISO-8583 files (*.iso8583)", "iso8583"));
				
				if (file.showOpenDialog(FrmMain.getInstance()) == JFileChooser.APPROVE_OPTION) {
					updateCmbMessage();
					
					try {
					    Path path = Paths.get(file.getSelectedFile().getAbsolutePath());
					    byte[] data = Files.readAllBytes(path);
					    
						if (data.length > 20) {
							String messageType = new String(new byte[]{data[0], data[1], data[2], data[3]});
							MessageVO payloadMessageVO = null;
							
							for (int i = 0; i < cmbMessageType.getItemCount(); i++) {
								if (((MessageVO) cmbMessageType.getItemAt(i)).getType().equals(messageType)) {
									payloadMessageVO = (MessageVO) cmbMessageType.getItemAt(i);
									break;
								}
							}
							
							if (payloadMessageVO == null) {
								JOptionPane.showMessageDialog(FrmMain.getInstance(), "It was not possible to parse this payload. Certify that the message structure was not changed.");
							}
							else {
								cmbMessageType.setSelectedItem(payloadMessageVO);
								guiPayloadMessageHelper.updateFromPayload(data);
							}
						}
						else {
							JOptionPane.showMessageDialog(FrmMain.getInstance(), "Invalid file.");							
						}
					} 
					catch (Exception x) {
						JOptionPane.showMessageDialog(FrmMain.getInstance(), "It was not possible to read the file. See the log.\n\n"+ x.getMessage());
					}
				}
			}
		});
		
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (tabbedPane.getSelectedIndex() == 0)
					guiPayloadMessageHelper.updateGUIfromXML();
				else if (tabbedPane.getSelectedIndex() == 1)
					guiPayloadMessageHelper.updateXMLfromGUI();
				
				guiPayloadMessageHelper.updateRawMessage(txtRawMessage);
			}
		});
	}

	private void cmbClick() {
		tabbedPane.setEnabled(false);
		tabbedPane.setSelectedIndex(0);
		pnlFields.removeAll();
		
		if (cmbMessageType.getSelectedItem() != null) {
			if (!((GenericIsoVO) cmbMessageType.getSelectedItem()).isValid()) {
				JOptionPane.showMessageDialog(this, "There are errors at the selected message type. Please verify it.");
			}
			else {
				tabbedPane.setEnabled(true);
				
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) Iso8583Helper.getInstance().getConfigTreeNode().getChildAt(cmbMessageType.getSelectedIndex());
				guiPayloadMessageHelper = new GuiPayloadMessageHelper((MessageVO) cmbMessageType.getSelectedItem(), pnlFields, xmlText);
				
				FieldVO fieldVO;
				int line = 0;
				for (int i = 0; i < treeNode.getChildCount(); i++) {
				
					fieldVO = (FieldVO) ((DefaultMutableTreeNode) treeNode.getChildAt(i)).getUserObject();
					guiPayloadMessageHelper.addLine(fieldVO);
					line++;
					
					for (int j = 0; j < treeNode.getChildAt(i).getChildCount(); j++) {
						fieldVO = (FieldVO) ((DefaultMutableTreeNode) treeNode.getChildAt(i).getChildAt(j)).getUserObject();	
						guiPayloadMessageHelper.addSubline(fieldVO);
						line++;
					}
				}
				
				scrFields.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				scrFields.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				pnlFields.setPreferredSize(new Dimension(500, (line * 25) + 20));
			}
		}
	}
	
	public void enablePnl(boolean value) {
		lblMessageType.setEnabled(value);
		cmbMessageType.setEnabled(value);
		btnUpdate.setEnabled(value);
		btnSendResponse.setEnabled(value);
		btnOpenPayload.setEnabled(value);
		btnSavePayload.setEnabled(value);
		
		tabbedPane.setEnabled(value);
	}
	
	private void updateCmbMessage() {
		cmbMessageType.removeAllItems();
		int totalMessages = Iso8583Helper.getInstance().getConfigTreeNode().getChildCount();
		
		DefaultMutableTreeNode treeNode;
		MessageVO messageVO;
		for (int messageIndex = 0; messageIndex < totalMessages; messageIndex++) {
			treeNode = (DefaultMutableTreeNode) Iso8583Helper.getInstance().getConfigTreeNode().getChildAt(messageIndex);
			if (treeNode.getUserObject() instanceof MessageVO) {
				messageVO = (MessageVO) treeNode.getUserObject();
				cmbMessageType.addItem(messageVO);
			}
		}
	}

}

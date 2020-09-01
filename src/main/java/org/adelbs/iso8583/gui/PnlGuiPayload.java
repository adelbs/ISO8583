package org.adelbs.iso8583.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;

import org.adelbs.iso8583.gui.xmlEditor.XmlTextPane;
import org.adelbs.iso8583.helper.Iso8583Config;
import org.adelbs.iso8583.helper.PayloadMessageConfig;
import org.adelbs.iso8583.util.Out;
import org.adelbs.iso8583.vo.MessageVO;

public class PnlGuiPayload extends JPanel {

	private static final long serialVersionUID = 2L;

	private PayloadMessageConfig payloadMessageConfig;
	
	private JLabel lblMessageType = new JLabel("Message Type");
	private JComboBox<MessageVO> cmbMessageType = new JComboBox<MessageVO>();
	private JButton btnUpdate = new JButton();
	private JButton btnSendRequest = new JButton("Send");
	private JButton btnSendResponse = new JButton("Send Response");
	private JButton btnOpenPayload = new JButton("Open Payload");
	private JButton btnSavePayload = new JButton("Save Payload");
	private JButton btnNextPayload = new JButton("Next Payload");
	
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	
	private JPanel pnlFormattedFields = new JPanel();
	private JPanel pnlFields = new JPanel();
    private JLabel lblHeader = new JLabel("Header:");
    private JTextField txtHeader = new JTextField();
    
    /**/
    private JLabel lblTPDU = new JLabel("TPDU:");
    private JTextField txtTPDU = new JTextField();
    /**/
    
    private JLabel lblBit = new JLabel("Bit #");
	private JLabel lblFieldName = new JLabel("Field Name");
	private JLabel lblFieldValue = new JLabel("Field Value");
	private JLabel lblType = new JLabel("Type");
	private JLabel lblDynamic = new JLabel("Dynamic");
	private JScrollPane scrFields = new JScrollPane();

	private JPanel pnlXML = new JPanel();
	private JScrollPane scrXML = new JScrollPane();
	private XmlTextPane xmlText = new XmlTextPane();
	
	public PnlGuiPayload(final PnlMain pnlMain, boolean server, boolean request) {
		setLayout(null);
		
		payloadMessageConfig = new PayloadMessageConfig(pnlMain.getIso8583Config(), pnlFields);
		
		lblMessageType.setBounds(12, 13, 90, 16);
		cmbMessageType.setBounds(114, 10, 178, 22);
		btnUpdate.setBounds(295, 10, 22, 22);
		btnUpdate.setIcon(new ImageIcon(PnlGuiPayload.class.getResource("/org/adelbs/iso8583/resource/update.png")));
		btnSendRequest.setIcon(new ImageIcon(PnlGuiPayload.class.getResource("/org/adelbs/iso8583/resource/enter.png")));
		btnSendResponse.setIcon(new ImageIcon(PnlGuiPayload.class.getResource("/org/adelbs/iso8583/resource/enter.png")));
		btnOpenPayload.setIcon(new ImageIcon(PnlGuiPayload.class.getResource("/org/adelbs/iso8583/resource/openFile.png")));
		btnSavePayload.setIcon(new ImageIcon(PnlGuiPayload.class.getResource("/org/adelbs/iso8583/resource/saveFile.png")));
		btnNextPayload.setIcon(new ImageIcon(PnlGuiPayload.class.getResource("/org/adelbs/iso8583/resource/update.png")));
        
        txtHeader.setEnabled(false);
		tabbedPane.setEnabled(false);
		if (server) {
			enablePnl(false);
			if (request) {
				add(lblMessageType);
				add(cmbMessageType);
				add(btnNextPayload);
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
				add(btnSendRequest);
				add(btnOpenPayload);
				add(btnSavePayload);
			}
			else {
				add(lblMessageType);
				add(cmbMessageType);
				add(btnNextPayload);
				add(btnSavePayload);
				enablePnl(false);
			}
		}
		
		btnUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateCmbMessage(pnlMain.getIso8583Config(), null);
			}
		});
		
		cmbMessageType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cmbClick(pnlMain);
			}
		});
		
		//######### Apenas para visualizacao no WindowBuilder *********************************
		
		btnOpenPayload.setBounds(430, 9, 143, 25);
		btnNextPayload.setBounds(430, 9, 143, 25);
		btnSavePayload.setBounds(585, 9, 143, 25);
		tabbedPane.setBounds(12, 42, 716, 516);
		
		//Formatted fields
		scrFields.setBounds(12, 67, 681, 467);
		
		//***************************************************************************
		
		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentResized(ComponentEvent e) {
				
				btnSendRequest.setBounds(getWidth() - 402, 9, 85, 25);
				btnSendResponse.setBounds(getWidth() - 160, 9, 143, 25);
				btnOpenPayload.setBounds(getWidth() - 310, 9, 143, 25);
				btnNextPayload.setBounds(getWidth() - 310, 9, 143, 25);
				btnSavePayload.setBounds(getWidth() - 160, 9, 143, 25);
				tabbedPane.setBounds(12, 42, getWidth() - 25, getHeight() - 55);
				
                //Formatted fields
                //32
				scrFields.setBounds(0, 67, tabbedPane.getWidth() - 5, tabbedPane.getHeight() - 95);
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
        
        lblHeader.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblHeader.setBounds(30, 10, 60, 16);
        
        txtHeader.setBounds(100, 10, 370, 20);
        txtHeader.addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e) { }
            public void keyReleased(KeyEvent e) {
                payloadMessageConfig.setHeaderValue(txtHeader.getText());
            }
            public void keyPressed(KeyEvent e) { }
        });
        
        if(payloadMessageConfig.getIsoConfig().getHeaderSize()>0) {
        	pnlFormattedFields.add(lblHeader);
            pnlFormattedFields.add(txtHeader);
        }
        
        /**/
        
        lblTPDU.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblTPDU.setBounds(500, 10, 50, 16);
        
        txtTPDU.setBounds(560, 10, 100, 20);
        txtTPDU.addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e) { }
            public void keyReleased(KeyEvent e) {
                try {
                	payloadMessageConfig.setTPDUValue(txtTPDU.getText());
                }
                catch(Exception ex) {
                	JOptionPane.showMessageDialog(pnlMain, ex.getMessage());
                }
            }
            public void keyPressed(KeyEvent e) { }
        });
        
        if(payloadMessageConfig.getIsoConfig().getTPDU()){
        	pnlFormattedFields.add(lblTPDU);
        	pnlFormattedFields.add(txtTPDU);
        }   	
        /**/

		lblBit.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblBit.setBounds(30, 45, 40, 16);
		pnlFormattedFields.add(lblBit);
		
		lblFieldName.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblFieldName.setBounds(100, 45, 88, 16);
		pnlFormattedFields.add(lblFieldName);
		
		lblFieldValue.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblFieldValue.setBounds(210, 45, 77, 16);
		pnlFormattedFields.add(lblFieldValue);
		
		lblType.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblType.setBounds(490, 45, 56, 16);
		pnlFormattedFields.add(lblType);

		lblDynamic.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblDynamic.setBounds(600, 45, 70, 16);
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
		
		btnSavePayload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (payloadMessageConfig.getIsoMessage() == null) {
					JOptionPane.showMessageDialog(pnlMain, "There is no data to save.");
				}
				else {
					String filePath;
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setAcceptAllFileFilterUsed(false);
					fileChooser.setFileFilter(new FileNameExtensionFilter("ISO-8583 files (*.iso8583)", "iso8583"));
					
					if (pnlMain.getLastCurrentDirectory() != null) 
						fileChooser.setCurrentDirectory(pnlMain.getLastCurrentDirectory());
					
					if (fileChooser.showSaveDialog(pnlMain) == JFileChooser.APPROVE_OPTION) {
						pnlMain.setLastCurrentDirectory(fileChooser.getSelectedFile());
						filePath = fileChooser.getSelectedFile().getAbsolutePath();
						if (filePath.indexOf(".iso8583") < 0) filePath = filePath + ".iso8583";
						
						FileOutputStream fos = null;
						try {
							if (filePath != null && !filePath.equals("")) {
								File file = new File(filePath);
								if (!file.exists())
									file.createNewFile();
								
								fos = new FileOutputStream(filePath);
								fos.write(payloadMessageConfig.getIsoMessage().getPayload());
								
								JOptionPane.showMessageDialog(pnlMain, "Payload saved!");
							}
						} 
						catch (Exception x) {
							x.printStackTrace();
							JOptionPane.showMessageDialog(pnlMain, "Error saving the Payload file. See the log.\n\n"+ x.getMessage());
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
				
				if (pnlMain.getLastCurrentDirectory() != null) 
					file.setCurrentDirectory(pnlMain.getLastCurrentDirectory());
				
				if (file.showOpenDialog(pnlMain) == JFileChooser.APPROVE_OPTION) {
					pnlMain.setLastCurrentDirectory(file.getSelectedFile());
					updateCmbMessage(pnlMain.getIso8583Config(), null);
					
					try {
					    Path path = Paths.get(file.getSelectedFile().getAbsolutePath());
					    byte[] data = Files.readAllBytes(path);
					    
						if (data.length > 20) {
							MessageVO payloadMessageVO = pnlMain.getIso8583Config().findMessageVOByPayload(data);
							
							if (payloadMessageVO == null) {
								JOptionPane.showMessageDialog(pnlMain, "It was not possible to parse this payload. Certify that the message structure was not changed.");
							}
							else {
								cmbMessageType.setSelectedItem(payloadMessageVO);
								payloadMessageConfig.updateFromPayload(pnlMain, data);
								updateScrFields();
								updateTextValues(payloadMessageVO);
							}
						}
						else {
							JOptionPane.showMessageDialog(pnlMain, "Invalid file.");							
						}
					} 
					catch (Exception x) {
						JOptionPane.showMessageDialog(pnlMain, "It was not possible to read the file. See the log.\n\n"+ x.getMessage());
					}
				}
			}
		});
		
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				try {
					if (tabbedPane.getSelectedIndex() == 0){
                        MessageVO messageVO;

						if (server && !request || !server && request) {
                            messageVO = payloadMessageConfig.updateMessageValuesFromXML(xmlText.getText());
                            payloadMessageConfig.setMessageVO(messageVO); 

                        }
                        else {
                            messageVO = payloadMessageConfig.buildMessageStructureFromXML(xmlText.getText());
                            payloadMessageConfig.setMessageVO(messageVO);

							setReadOnly();
                        }

						updateTextValues(messageVO);
                    }
                    else if (tabbedPane.getSelectedIndex() == 1){
						xmlText.setText(payloadMessageConfig.getXML(pnlMain));
					}
				}
				catch (Exception x) {
					JOptionPane.showMessageDialog(pnlMain, x.getMessage());
				}
			}
		});
		
		xmlText.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				checkAdvancedTag();
			}
			@Override
			public void keyPressed(KeyEvent e) {}
		});
	}
	
	public void checkAdvancedTag() {
		if (xmlText.getText().indexOf("<%") > -1 || xmlText.getText().indexOf("%>") > -1) {
            enablePnl(false);
			
            tabbedPane.setEnabled(true);
			tabbedPane.setEnabledAt(0, false);
			tabbedPane.setEnabledAt(1, true);
		}
		else {
			enablePnl(true);
			tabbedPane.setEnabledAt(0, true);
		}
	}
	
	public void cmbClick(PnlMain pnlMain) {
		
		tabbedPane.setEnabledAt(0, true);
		
		tabbedPane.setEnabled(false);
        tabbedPane.setSelectedIndex(0);
        txtHeader.setText("");
        setTPDUValue();
		pnlFields.removeAll();
		
		if (cmbMessageType.getSelectedItem() != null) {
			tabbedPane.setEnabled(true);
            txtHeader.setEnabled(true);
            
			payloadMessageConfig.setMessageVO(pnlMain.getIso8583Config().getMessageVOAtTree(((MessageVO) cmbMessageType.getSelectedItem()).getType()));
			updateScrFields();
		}
	}
	
	public void setTPDUResponseValue(String TPDUValue, JTextField txtTPDU) {
        String TPDUResponseValue="";
        if (TPDUValue!=null)
	    	if(!TPDUValue.isEmpty()){
	    		TPDUResponseValue=TPDUValue.substring(0,2).concat(TPDUValue.substring(6,10)).concat(TPDUValue.substring(2,6));
	    		txtTPDU.setText(TPDUResponseValue);
	            try{
	            	payloadMessageConfig.setTPDUValue(TPDUResponseValue);
	            }
	            catch(Exception ex) {
	            	Out.log("setTPDUResponseValue",ex.getMessage());
	            }
	    	}
    }
	
	public void setTPDUValue() {
		//verificar se vem do request ou do response
		//se vier do response, pegar o tpdu do request tratado
		System.out.println("Checking TPDU Value");
		/*
		System.out.println("Alimentando valor do TPDU");
		System.out.println("this.listenerList.toString()");
		System.out.println(this.listenerList.toString());
		System.out.println("this.tabbedPane.getName()");
		System.out.println(this.tabbedPane.getName());
		System.out.println("this.tabbedPane.getParent().getName()");
		System.out.println(this.tabbedPane.getParent().getName());
		*/
	}

	public void updateTextValues(MessageVO messageVO) {
		txtHeader.setText(messageVO.getHeader());
        txtTPDU.setText(messageVO.getTPDUValue());
	}
	
	public void updateScrFields() {
		scrFields.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrFields.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		pnlFields.setPreferredSize(new Dimension(500, (payloadMessageConfig.getNumLines() * 25) + 20));
		scrFields.getVerticalScrollBar().setUnitIncrement(10);
		
		if(payloadMessageConfig.getIsoConfig().getTPDU()){
        	pnlFormattedFields.add(lblTPDU);
        	pnlFormattedFields.add(txtTPDU);
        }
	}
	
	public void setReadOnly() {
		enablePnl(true);
		payloadMessageConfig.setReadOnly();
		cmbMessageType.setEnabled(false);
		setEnableXmlPanel(false);
	}
	
	public void enablePnl(boolean value) {
		lblMessageType.setEnabled(value);
		cmbMessageType.setEnabled(value);
		btnUpdate.setEnabled(value);
		btnSendResponse.setEnabled(value);
		btnOpenPayload.setEnabled(value);
		btnSavePayload.setEnabled(value);
		
        tabbedPane.setEnabled(value);
        txtHeader.setEnabled(value);
        txtTPDU.setEditable(value);
	}
	
	private void setEnableXmlPanel(boolean value){
		pnlXML.setEnabled(value);
		scrXML.setEnabled(value);
		xmlText.setEnabled(value);
	}
	
	/**
	 * Removes all items of cmb messages and adds all items again, according to the tree.
	 * In addition it sets the selected item according to the msgType parameter.
	 * @param isoConfig
	 * @param msgType
	 */
	public void updateCmbMessage(Iso8583Config isoConfig, String msgType) {
		cmbMessageType.removeAllItems();
		int totalMessages = isoConfig.getConfigTreeNode().getChildCount();
		
		DefaultMutableTreeNode treeNode;
		MessageVO messageVO;
		MessageVO selectedMessageVO = null;
		for (int messageIndex = 0; messageIndex < totalMessages; messageIndex++) {
			treeNode = (DefaultMutableTreeNode) isoConfig.getConfigTreeNode().getChildAt(messageIndex);
			if (treeNode.getUserObject() instanceof MessageVO) {
				messageVO = (MessageVO) treeNode.getUserObject();
				cmbMessageType.addItem(messageVO);
				
				if (msgType != null && messageVO.getType().equals(msgType)) selectedMessageVO = messageVO;
			}
		}
		
		if (selectedMessageVO != null) cmbMessageType.setSelectedItem(selectedMessageVO);
	}
	
	public JComboBox<MessageVO> getCmbMessageType() {
		return cmbMessageType;
	}
	
	public PayloadMessageConfig getPayloadMessageConfig() {
		return payloadMessageConfig;
	}

	public JButton getBtnNextPayload() {
		return btnNextPayload;
	}
	
	public JButton getBtnSendRequest() {
		return btnSendRequest;
	}
	
	public JButton getBtnSendResponse() {
		return btnSendResponse;
	}
	
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
	
	public XmlTextPane getXmlText() {
		return xmlText;
	}
	
	public JTextField getTxtTPDU() {
		return txtTPDU;
	}
	
	public String getTPDUValue() {
		return txtTPDU.getText();
	}
}

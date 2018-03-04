package org.adelbs.iso8583.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.adelbs.iso8583.clientserver.CallbackAction;
import org.adelbs.iso8583.clientserver.ISOConnection;
import org.adelbs.iso8583.exception.ConnectionException;
import org.adelbs.iso8583.exception.ParseException;
import org.adelbs.iso8583.vo.ISOTestVO;


public class PnlGuiMessages extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JLabel lblHost;
	private JLabel lblPort = new JLabel("Port");
	private JLabel lblTimeout = new JLabel("Timeout (sec)");
	private JTextField txtHost = new JTextField("localhost");
	private JTextField txtPort = new JTextField("9980");
	private JTextField txtTimeout = new JTextField("520");
	private JCheckBox ckRequestSync = new JCheckBox("Synchronized request");
	private JCheckBox ckResponseSync = new JCheckBox("Synchronized response");
	private JButton btnConnect;
	
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private JPanel pnlConnection = new JPanel();
	private PnlGuiPayload pnlRequest;
	private PnlGuiPayload pnlResponse;
	private JPanel pnlConsole = new JPanel();
	private JTextArea txtConsole = new JTextArea();
	private JScrollPane scrConsole = new JScrollPane();
	
	private boolean isServer;
	private ISOConnection isoClient = null;
	private ISOConnection isoServer = null;
	
	public PnlGuiMessages(final PnlMain pnlMain, boolean server) {
		setLayout(null);
		
		isServer = server;
		
		lblHost = new JLabel(server ? "Host (bind)" : "Host");
		btnConnect = new JButton("Connect");
		pnlRequest = new PnlGuiPayload(pnlMain, server, true);
		pnlResponse = new PnlGuiPayload(pnlMain, server, false);
		
		pnlRequest.getBtnSendRequest().setEnabled(false);
		
		lblHost.setBounds(12, 43, 78, 16);
		txtHost.setBounds(80, 40, 226, 22);
		txtHost.setColumns(10);

		lblPort.setBounds(318, 43, 37, 16);
		txtPort.setBounds(348, 40, 50, 22);
		txtPort.setColumns(10);
		btnConnect.setBounds(411, 40, 91, 25);
		
		lblTimeout.setBounds(259, 72, 100, 16);
		txtTimeout.setBounds(348, 69, 50, 22);
		txtTimeout.setColumns(10);
		
		ckRequestSync.setBounds(80, 72, 170, 22);
		ckResponseSync.setBounds(80, 99, 170, 22);
		
		if (!server) {
			ckRequestSync.setSelected(true);
			ckResponseSync.setSelected(true);
		}
		
		pnlConnection.setLayout(null);
		pnlConnection.add(lblHost);
		pnlConnection.add(txtHost);
		pnlConnection.add(lblPort);
		pnlConnection.add(txtPort);
		pnlConnection.add(lblTimeout);
		pnlConnection.add(txtTimeout);
		pnlConnection.add(btnConnect);
		
		if (!server) {
			pnlConnection.add(ckRequestSync);
			pnlConnection.add(ckResponseSync);
		}
		
		add(tabbedPane);
		tabbedPane.addTab("Connection", null, pnlConnection, null);
		tabbedPane.addTab("Request", null, pnlRequest, null);
		tabbedPane.addTab("Response", null, pnlResponse, null);
		tabbedPane.addTab("Console", null, pnlConsole, null);
		pnlConsole.setLayout(new BorderLayout(0, 0));
		
		txtConsole.setEditable(false);
		pnlConsole.add(scrConsole, BorderLayout.CENTER);
		scrConsole.setViewportView(txtConsole);
		
		
		//######### Apenas para visualizacao no WindowBuilder *********************************
		
		tabbedPane.setBounds(12, 45, 845, 567);
		
		//***************************************************************************
		
		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentResized(ComponentEvent e) {
				
				tabbedPane.setBounds(10, 10, getWidth() - 35, getHeight() - 20);
				
			}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		}); 
		
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				tabbedPane.setTitleAt(tabbedPane.getTabCount() - 1, "<html>Console</html>");
			}
		});
		
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					connectAction(pnlMain);
					
				} 
				catch (Exception x) {
					x.printStackTrace();
					JOptionPane.showMessageDialog(pnlMain, "Error trying to connect\n" + x.getMessage());
				}
			}
		});
		
		pnlRequest.getBtnSendRequest().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					sendMessageAction(pnlMain);
				} 
				catch (Exception x) {
					x.printStackTrace();
					JOptionPane.showMessageDialog(pnlMain, "Error trying to send the message\n" + x.getMessage());
				}
			}
		});
	}

	private void connectAction(final PnlMain pnlMain) throws ParseException, NumberFormatException, IOException, ConnectionException {
		if (isServer) {
			if (isoServer == null) {
				try{
					isoServer = new ISOConnection(true, txtHost.getText(), Integer.parseInt(txtPort.getText()), Integer.parseInt(txtTimeout.getText()));
					isoServer.setIsoConfig(pnlMain.getIso8583Config());
					isoServer.setCallback(new Callback(pnlMain, true));
					isoServer.connect();
					System.out.println("Server connected.");
					
				}catch(Exception ex){
					isoServer=null;
					throw ex;
				}
			}
			
			if (pnlResponse.getBtnSendResponse().getActionListeners().length > 0) 
				pnlResponse.getBtnSendResponse().removeActionListener(pnlResponse.getBtnSendResponse().getActionListeners()[0]);
			pnlResponse.getBtnSendResponse().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					pnlResponse.setReadOnly();
					pnlResponse.enablePnl(false);
					pnlResponse.updateRawMessage(pnlMain);
					
					try {
						isoServer.sendBytes(
								pnlMain.getIso8583Config().getDelimiter().preparePayload(pnlResponse.getPayloadMessageConfig().getIsoMessage(), pnlMain.getIso8583Config()), 
								ckRequestSync.isSelected(), ckResponseSync.isSelected());
					}
					catch (Exception x) {
						x.printStackTrace();
						JOptionPane.showMessageDialog(pnlMain, "Error trying to send the bytes\n" + x.getMessage());
					}
				}
			});
		}
		else {
			if (isoClient == null) {
				try{
					isoClient = new ISOConnection(false, txtHost.getText(), Integer.parseInt(txtPort.getText()), Integer.parseInt(txtTimeout.getText()));
					isoClient.setIsoConfig(pnlMain.getIso8583Config());
					isoClient.setCallback(new Callback(pnlMain, false));
					isoClient.connect();
					System.out.println("Client connected.");
					//TODO: Send handshake message to start the communication
					sendHandshake();
				}catch(Exception ex){
					isoClient=null;
					throw ex;
				}
			}
		}
		
		txtHost.setEnabled(false);
		txtPort.setEnabled(false);
		txtTimeout.setEnabled(false);
		btnConnect.setEnabled(false);
		pnlRequest.getBtnSendRequest().setEnabled(true);
	}
	
	private void sendHandshake(){
		//TODO: Implement a communication between Client and Server to start the messaging
		/*try{
			isoClient.sendBytes(
					"ping".getBytes(),  
					ckRequestSync.isSelected(), ckResponseSync.isSelected());
		}
		catch(Exception  ex){
			ex.printStackTrace();
		}*/
	}
	
	private void sendMessageAction(final PnlMain pnlMain) throws ParseException, NumberFormatException, IOException, InterruptedException {
		pnlRequest.updateRawMessage(pnlMain);
		
		if (pnlRequest.getTabbedPane().getSelectedIndex() == 1)
			pnlRequest.getPayloadMessageConfig().setMessageVO(pnlRequest.getPayloadMessageConfig().getMessageVOFromXML(pnlRequest.getXmlText().getText()));
		
		isoClient.sendBytes(
				pnlMain.getIso8583Config().getDelimiter().preparePayload(pnlRequest.getPayloadMessageConfig().getIsoMessage(), pnlMain.getIso8583Config()), 
				ckRequestSync.isSelected(), ckResponseSync.isSelected());
	}

	public PnlGuiPayload getPnlRequest() {
		return pnlRequest;
	}

	public JTextField getTxtHost() {
		return txtHost;
	}
	
	public JTextField getTxtPort() {
		return txtPort;
	}
	
	private class Callback extends CallbackAction {
		
		private boolean isRequest = false;
		
		private PnlMain pnlMain;
		
		private Callback(PnlMain pnlMain, boolean isRequest) {
			this.pnlMain = pnlMain;
			this.isRequest = isRequest;
		}
		
		public void dataReceived(byte[] data) throws ParseException {
			PnlGuiPayload pnlGui;
			
			if (isRequest) {
				pnlGui = pnlRequest;
				pnlResponse.enablePnl(true);
			}
			else {
				pnlGui = pnlResponse;
			}
			
			pnlGui.updateCmbMessage(pnlMain.getIso8583Config(), pnlMain.getIso8583Config().findMessageVOByPayload(data).getType());
			pnlGui.getPayloadMessageConfig().updateFromPayload(pnlMain, data);
			pnlGui.setReadOnly();
			
		}

		public void log(String log) {
			tabbedPane.setTitleAt(tabbedPane.getTabCount() - 1, "<html><i>*Console</i></html>");
			txtConsole.setText(txtConsole.getText() + "\n" + log);
		}

		public void end() {
			txtHost.setEnabled(true);
			txtPort.setEnabled(true);
			txtTimeout.setEnabled(true);
			btnConnect.setEnabled(true);
			pnlRequest.getBtnSendRequest().setEnabled(false);
		}
	}
	
	public void setXmlRequest(PnlMain pnlMain, String xml) throws ParseException {
		
		ISOTestVO testVO = pnlRequest.getPayloadMessageConfig().getISOTestVOFromXML(xml);

		//Carregando arquivo de configuracao
		if (pnlMain.getTxtFilePath().getText() != "" && !testVO.getConfigFile().equals(pnlMain.getTxtFilePath().getText())) {
			pnlMain.getTxtFilePath().setText(testVO.getConfigFile());
			pnlMain.getIso8583Config().openFile(pnlMain, pnlMain.getTxtFilePath().getText());
			pnlMain.getIso8583Config().parseXmlToConfig(pnlMain);
			pnlMain.getPnlGuiConfig().updateTree();
			pnlMain.getPnlGuiConfig().expandAllNodes();
			pnlMain.getPnlGuiConfig().updateTree();
		}

		ckRequestSync.setSelected(testVO.isRequestSync());
		ckResponseSync.setSelected(testVO.isResponseSync());
		
		pnlRequest.getTabbedPane().setSelectedIndex(1);
		pnlRequest.getXmlText().setText(xml);
		pnlRequest.checkAdvancedTag();
	}

	public String getXmlRequest(PnlMain pnlMain) {
		return pnlRequest.getPayloadMessageConfig().getXML(pnlMain);
	}
	
	public String getXmlResponse(PnlMain pnlMain) {
		return pnlResponse.getPayloadMessageConfig().getXML(pnlMain);
	}
	
	public JPanel getConnectionPanel() {
		return pnlConnection;
	}
	
	public JCheckBox getCkRequestSync() {
		return ckRequestSync;
	}
	
	public JCheckBox getCkResponseSync() {
		return ckResponseSync;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
	
}

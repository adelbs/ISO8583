package org.adelbs.iso8583.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
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
import org.adelbs.iso8583.clientserver.SocketPayload;
import org.adelbs.iso8583.exception.ConnectionException;
import org.adelbs.iso8583.exception.ParseException;
import org.adelbs.iso8583.helper.Iso8583Config;
import org.adelbs.iso8583.protocol.ISOMessage;
import org.adelbs.iso8583.vo.ISOTestVO;


public class PnlGuiMessages extends JPanel {

	private static final long serialVersionUID = 2L;
	
	private JLabel lblHost;
	private JLabel lblPort = new JLabel("Port");
	private JLabel lblTimeout = new JLabel("Timeout (sec)");
	private JTextField txtHost = new JTextField("localhost");
	private JTextField txtPort = new JTextField("9980");
	private JTextField txtTimeout = new JTextField("520");
	private JButton btnConnect;
	private JButton btnDisconnect;
	
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private JPanel pnlConnection = new JPanel();
	private PnlGuiPayload pnlRequest;
	private PnlGuiPayload pnlResponse;
	private JPanel pnlConsole = new JPanel();
	private JTextArea txtConsole = new JTextArea();
	private JScrollPane scrConsole = new JScrollPane();
	
	private boolean isServer;
	private ISOConnection isoConnClient = null;
	private ISOConnection isoConnServer = null;
	private Callback callBackServer;
	
	public PnlGuiMessages(final PnlMain pnlMain, boolean server) {
		setLayout(null);
		
		isServer = server;
		
		lblHost = new JLabel(server ? "Host (bind)" : "Host");
		btnConnect = new JButton("Connect");
		btnDisconnect = new JButton("Close");
		pnlRequest = new PnlGuiPayload(pnlMain, server, true);
		pnlResponse = new PnlGuiPayload(pnlMain, server, false);
		
		pnlResponse.getBtnNextPayload().setEnabled(false);
		pnlRequest.getBtnNextPayload().setEnabled(false);
		pnlRequest.getBtnSendRequest().setEnabled(false);
		
		lblHost.setBounds(12, 43, 78, 16);
		txtHost.setBounds(80, 40, 226, 22);
		txtHost.setColumns(10);

		lblPort.setBounds(318, 43, 37, 16);
		txtPort.setBounds(348, 40, 50, 22);
		txtPort.setColumns(10);
		btnConnect.setBounds(411, 40, 91, 25);
		btnDisconnect.setBounds(411, 70, 91, 25);
		
		lblTimeout.setBounds(259, 72, 100, 16);
		txtTimeout.setBounds(348, 69, 50, 22);
		txtTimeout.setColumns(10);
		
		pnlConnection.setLayout(null);
		pnlConnection.add(lblHost);
		pnlConnection.add(txtHost);
		pnlConnection.add(lblPort);
		pnlConnection.add(txtPort);
		pnlConnection.add(lblTimeout);
		pnlConnection.add(txtTimeout);
		pnlConnection.add(btnConnect);
		pnlConnection.add(btnDisconnect);
		
		btnDisconnect.setEnabled(false);

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
				try {
					//tabbedPane.setTitleAt(tabbedPane.getTabCount() - 1, "<html>Console</html>");
					switch(tabbedPane.getSelectedIndex()) {
					case(0): //Connection
						
						break;
					
					case(1): //Request
						
						break;
					
					case(2): //Response
						pnlResponse.setTPDUResponseValue(pnlRequest.getTPDUValue(), pnlResponse.getTxtTPDU());
						break;
					
					case(3): //Console
						
						break;
					}
					
				}

				catch (Exception x) {
					JOptionPane.showMessageDialog(pnlMain, x.getMessage());
				}
			}
		});
		
		

		btnDisconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					disconnectAction();
				} 
				catch (Exception x) {
					x.printStackTrace();
					JOptionPane.showMessageDialog(pnlMain, "Error trying to connect\n" + x.getMessage());
				}
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
		
		pnlRequest.getBtnNextPayload().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					isoConnServer.processNextPayload(String.valueOf(Thread.currentThread().getId()), false, 0);
				} 
				catch (Exception x) {
					x.printStackTrace();
					JOptionPane.showMessageDialog(pnlMain, "Error trying to retrieve the next payload\n" + x.getMessage());
				}
			}
		});

		pnlResponse.getBtnNextPayload().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					isoConnClient.processNextPayload(String.valueOf(Thread.currentThread().getId()), false, 0);
				} 
				catch (Exception x) {
					x.printStackTrace();
					JOptionPane.showMessageDialog(pnlMain, "Error trying to retrieve the next payload\n" + x.getMessage());
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

	private void disconnectAction() throws IOException, ParseException, InterruptedException {
		if (isServer) {
			if (isoConnServer != null) {
				isoConnServer.endConnection(String.valueOf(Thread.currentThread().getId()));
				isoConnServer = null;
			}
			
			if (pnlResponse.getBtnSendResponse().getActionListeners().length > 0) 
				pnlResponse.getBtnSendResponse().removeActionListener(pnlResponse.getBtnSendResponse().getActionListeners()[0]);
		}
		else {
			if (isoConnClient != null) {
				isoConnClient.endConnection(String.valueOf(Thread.currentThread().getId()));
				isoConnClient = null;
			}
		}
		
		txtHost.setEnabled(true);
		txtPort.setEnabled(true);
		txtTimeout.setEnabled(true);
		btnConnect.setEnabled(true);
		btnDisconnect.setEnabled(false);
		pnlResponse.getBtnNextPayload().setEnabled(false);
		pnlRequest.getBtnNextPayload().setEnabled(false);
		pnlRequest.getBtnSendRequest().setEnabled(false);
	}
	
	private void connectAction(final PnlMain pnlMain) throws ParseException, NumberFormatException, IOException, ConnectionException, InterruptedException {
		if (isServer) {
			if (isoConnServer != null)
				disconnectAction();
			
			try {
				callBackServer = new Callback(pnlMain, true);
				
				isoConnServer = new ISOConnection(true, txtHost.getText(), Integer.parseInt(txtPort.getText()), Integer.parseInt(txtTimeout.getText()));
				isoConnServer.setIsoConfig(pnlMain.getIso8583Config());
				isoConnServer.putCallback(String.valueOf(Thread.currentThread().getId()), callBackServer);
				isoConnServer.connect(String.valueOf(Thread.currentThread().getId()));
				System.out.println("Server connected.");
				
			}
			catch(Exception ex) {
				isoConnServer = null;
				throw ex;
			}
			
			if (pnlResponse.getBtnSendResponse().getActionListeners().length > 0) 
				pnlResponse.getBtnSendResponse().removeActionListener(pnlResponse.getBtnSendResponse().getActionListeners()[0]);
			
			pnlResponse.getBtnSendResponse().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					pnlResponse.setReadOnly();
					pnlResponse.enablePnl(false);
					
					try {
						pnlResponse.getPayloadMessageConfig().updateFromMessageVO();
						
						isoConnServer.send(
								new SocketPayload(pnlMain.getIso8583Config().getDelimiter().preparePayload(
										pnlResponse.getPayloadMessageConfig().getIsoMessage(), 
										pnlMain.getIso8583Config()), callBackServer.getSocketToRespond()));
					}
					catch (Exception x) {
						x.printStackTrace();
						JOptionPane.showMessageDialog(pnlMain, "Error trying to send the bytes\n" + x.getMessage());
					}
				}
			});
		}
		else {
			if (isoConnClient != null)
				disconnectAction();
			
			try{
				isoConnClient = new ISOConnection(false, txtHost.getText(), Integer.parseInt(txtPort.getText()), Integer.parseInt(txtTimeout.getText()));
				isoConnClient.setIsoConfig(pnlMain.getIso8583Config());
				isoConnClient.putCallback(String.valueOf(Thread.currentThread().getId()), new Callback(pnlMain, false));
				isoConnClient.connect(String.valueOf(Thread.currentThread().getId()));
				System.out.println("Client connected.");
			}
			catch(Exception ex) {
				isoConnClient = null;
				throw ex;
			}
		}
		
		txtHost.setEnabled(false);
		txtPort.setEnabled(false);
		txtTimeout.setEnabled(false);
		btnConnect.setEnabled(false);
		btnDisconnect.setEnabled(true);
		pnlResponse.getBtnNextPayload().setEnabled(true);
		pnlRequest.getBtnNextPayload().setEnabled(true);
		pnlRequest.getBtnSendRequest().setEnabled(true);
	}
	
	private void sendMessageAction(final PnlMain pnlMain) throws ParseException, NumberFormatException, IOException, InterruptedException {
		pnlRequest.getPayloadMessageConfig().updateFromMessageVO();
		
		if (pnlRequest.getTabbedPane().getSelectedIndex() == 1){
			pnlRequest.getPayloadMessageConfig().setMessageVO(pnlRequest.getPayloadMessageConfig().updateMessageValuesFromXML(pnlRequest.getXmlText().getText()));
		}
		
		final ISOMessage requestMessage = pnlRequest.getPayloadMessageConfig().getIsoMessage();
		final Iso8583Config isoConfig = pnlMain.getIso8583Config();
		final byte[] preparedPayload = isoConfig.getDelimiter().preparePayload(requestMessage, isoConfig);
		isoConnClient.send(new SocketPayload(preparedPayload, isoConnClient.getClientSocket()));
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
		private Socket socketToRespnd;
		
		private Callback(PnlMain pnlMain, boolean isRequest) {
			this.pnlMain = pnlMain;
			this.isRequest = isRequest;
		}
		
		public void dataReceived(SocketPayload payload) throws ParseException {
			PnlGuiPayload pnlGui;
			
			if (isRequest) {
				pnlGui = pnlRequest;
				pnlResponse.enablePnl(true);
			}
			else {
				pnlGui = pnlResponse;
			}
			
			pnlGui.updateCmbMessage(pnlMain.getIso8583Config(), pnlMain.getIso8583Config().findMessageVOByPayload(payload.getData()).getType());
			pnlGui.getPayloadMessageConfig().updateFromPayload(pnlMain, payload.getData());
			pnlGui.setReadOnly();
			
			socketToRespnd = payload.getSocket();
		}

		public void log(String log) {
			//tabbedPane.setTitleAt(tabbedPane.getTabCount() - 1, "<html><i>*Console</i></html>");
			txtConsole.setText(txtConsole.getText() + "\n" + log);
		}

		public void end() {
			txtHost.setEnabled(true);
			txtPort.setEnabled(true);
			txtTimeout.setEnabled(true);
			btnConnect.setEnabled(true);
			btnDisconnect.setEnabled(false);
			pnlResponse.getBtnNextPayload().setEnabled(false);
			pnlRequest.getBtnNextPayload().setEnabled(false);
			pnlRequest.getBtnSendRequest().setEnabled(false);
		}
		
		public Socket getSocketToRespond() {
			return socketToRespnd;
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
	
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
	
}

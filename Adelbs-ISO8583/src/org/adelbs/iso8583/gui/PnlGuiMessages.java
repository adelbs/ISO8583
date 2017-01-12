package org.adelbs.iso8583.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.adelbs.iso8583.clientserver.CallbackAction;
import org.adelbs.iso8583.clientserver.ISOClient;
import org.adelbs.iso8583.clientserver.ISOServer;
import org.adelbs.iso8583.util.ISOUtils;
import org.adelbs.iso8583.vo.ISOTestVO;
import org.adelbs.iso8583.vo.MessageVO;

import groovyjarjarcommonscli.ParseException;

public class PnlGuiMessages extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JLabel lblHost;
	private JLabel lblPort = new JLabel("Port");
	private JTextField txtHost = new JTextField();
	private JTextField txtPort = new JTextField();
	private JButton btnConnect;

	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private PnlGuiPayload pnlRequest;
	private PnlGuiPayload pnlResponse;
	private JPanel pnlConsole = new JPanel();
	private JTextArea txtConsole = new JTextArea();
	private JScrollPane scrConsole = new JScrollPane();
	
	private boolean isServer;
	
	public PnlGuiMessages(final PnlMain pnlMain, boolean server) {
		setLayout(null);
		
		isServer = server;
		
		lblHost = new JLabel(server ? "Host (bind)" : "Host");
		btnConnect = new JButton(server ? "Open connection" : "Connect and send message");
		pnlRequest = new PnlGuiPayload(pnlMain, server, true);
		pnlResponse = new PnlGuiPayload(pnlMain, server, false);
		
		lblHost.setBounds(12, 16, 78, 16);
		txtHost.setBounds(80, 13, 226, 22);
		txtHost.setColumns(10);

		lblPort.setBounds(318, 16, 37, 16);
		txtPort.setBounds(349, 13, 50, 22);
		txtPort.setColumns(10);
		btnConnect.setBounds(411, 12, 220, 25);

		add(lblHost);
		add(txtHost);
		add(lblPort);
		add(txtPort);
		add(btnConnect);
		add(tabbedPane);
		
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
				
				tabbedPane.setBounds(12, 45, getWidth() - 25, getHeight() - 55);
				
			}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		}); 
		
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				tabbedPane.setTitleAt(2, "<html>Console</html>");
			}
		});
		
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientServerAction(pnlMain);
			}
		});
	}
	
	private void clientServerAction(final PnlMain pnlMain) {
		pnlRequest.updateRawMessage(pnlMain);
		
		if (isServer) {
			final ISOServer server = new ISOServer(txtHost.getText(), Integer.parseInt(txtPort.getText()), new Callback(pnlMain, true));
			
			if (pnlResponse.getBtnSendResponse().getActionListeners().length > 0) 
				pnlResponse.getBtnSendResponse().removeActionListener(pnlResponse.getBtnSendResponse().getActionListeners()[0]);
			pnlResponse.getBtnSendResponse().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					pnlResponse.setReadOnly();
					pnlResponse.enablePnl(false);
					pnlResponse.updateRawMessage(pnlMain);
					byte[] data = ISOUtils.mergeArray(pnlResponse.getMessageHelper().getIsoMessage().getMessageSize(4).getBytes(), pnlResponse.getMessageHelper().getIsoMessage().getPayload());
					server.setResponsePayload(data);
				}
			});
			
			server.start();
		}
		else {
			byte[] data = ISOUtils.mergeArray(pnlRequest.getMessageHelper().getIsoMessage().getMessageSize(4).getBytes(), pnlRequest.getMessageHelper().getIsoMessage().getPayload());
			ISOClient client = new ISOClient(txtHost.getText(), Integer.parseInt(txtPort.getText()), data, new Callback(pnlMain, false));
			client.start();
		}
		
		txtHost.setEnabled(false);
		txtPort.setEnabled(false);
		btnConnect.setEnabled(false);
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
		
		public void dataReceived(byte[] data) throws Exception {
			if (isRequest) {
				pnlRequest.updateCmbMessage(pnlMain.getIsoHelper(), new String(ISOUtils.subArray(data, 0, 4)));
				pnlRequest.getMessageHelper().updateFromPayload(pnlMain, data);
				pnlRequest.setReadOnly();
				pnlResponse.enablePnl(true);
			}
			else {
				pnlResponse.updateCmbMessage(pnlMain.getIsoHelper(), new String(ISOUtils.subArray(data, 0, 4)));
				pnlResponse.getMessageHelper().updateFromPayload(pnlMain, data);
				pnlResponse.setReadOnly();
			}
		}

		public void log(String log) {
			tabbedPane.setTitleAt(2, "<html><i>*Console</i></html>");
			txtConsole.setText(txtConsole.getText() + "\n" + log);
		}

		public void end() {
			txtHost.setEnabled(true);
			txtPort.setEnabled(true);
			btnConnect.setEnabled(true);
		}
	}
	
	public void setXmlRequest(PnlMain pnlMain, String xml) throws ParseException {
		ISOTestVO testVO = pnlRequest.getMessageHelper().getISOTestVOFromXML(xml);

		//Carregando arquivo de configuracao
		if (pnlMain.getTxtFilePath().getText() != "" && !testVO.getConfigFile().equals(pnlMain.getTxtFilePath().getText())) {
			pnlMain.getTxtFilePath().setText(testVO.getConfigFile());
			pnlMain.getIsoHelper().openFile(pnlMain, pnlMain.getTxtFilePath().getText());
			pnlMain.getIsoHelper().parseXmlToConfig(pnlMain);
			pnlMain.getPnlGuiConfig().updateTree();
			pnlMain.getPnlGuiConfig().expandAllNodes();
			pnlMain.getPnlGuiConfig().updateTree();
		}

		txtHost.setText(testVO.getHost());
		txtPort.setText(testVO.getPort());
		
		MessageVO messageVO = pnlRequest.getMessageHelper().getMessageVOFromXML(xml);
		pnlRequest.updateCmbMessage(pnlMain.getIsoHelper(), messageVO.getType());
		pnlRequest.getMessageHelper().setMessageVO(messageVO);;
	}
	
	public String getXmlRequest(PnlMain pnlMain) {
		return pnlRequest.getMessageHelper().getXML(pnlMain);
	}
	
	public String getXmlResponse(PnlMain pnlMain) {
		return pnlResponse.getMessageHelper().getXML(pnlMain);
	}
}

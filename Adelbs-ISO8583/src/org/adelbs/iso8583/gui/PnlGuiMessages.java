package org.adelbs.iso8583.gui;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
	
	public PnlGuiMessages(boolean server) {
		setLayout(null);
		
		lblHost = new JLabel(server ? "Host (bind)" : "Host");
		btnConnect = new JButton(server ? "Open connection" : "Connect and send message");
		pnlRequest = new PnlGuiPayload(server, true);
		pnlResponse = new PnlGuiPayload(server, false);
		
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
	}
}

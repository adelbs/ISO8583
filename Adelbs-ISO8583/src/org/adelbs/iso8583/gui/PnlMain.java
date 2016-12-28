package org.adelbs.iso8583.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.adelbs.iso8583.helper.Iso8583Helper;

public class PnlMain extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
	
	//Abas
	private PnlGuiConfig pnlGuiConfig;
	private PnlGuiMessages pnlGuiMessagesClient;
	private PnlGuiMessages pnlGuiMessagesServer;
	private PnlXmlConfig pnlXmlConfig;
	private JTextField txtFilePath;
	private JButton btnSave;
	private JButton btnOpen;
	private JButton btnNew;
	
	public PnlMain() {
		setLayout(null);
		pnlGuiConfig = new PnlGuiConfig(this);
		pnlGuiMessagesClient = new PnlGuiMessages(false);
		pnlGuiMessagesServer = new PnlGuiMessages(true);
		pnlXmlConfig = new PnlXmlConfig(this);
		
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				parseXML();
			}
		});

		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentResized(ComponentEvent e) {
				tabbedPane.setBounds(-2, 25, getWidth(), getHeight() - 25);
				btnSave.setBounds(getWidth() - 35, 0, 33, 25);
				btnOpen.setBounds(getWidth() - 70, 0, 33, 25);
				btnNew.setBounds(getWidth() - 105, 0, 33, 25);
				txtFilePath.setBounds(3, 1, getWidth() - 112, 22);
			}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		}); 
		
		//*** Adicionando as Abas ***
		add(tabbedPane);
		tabbedPane.addTab("ISO Configure", null, pnlGuiConfig, null);
		tabbedPane.addTab("Test ISO Messages (Client)", null, pnlGuiMessagesClient, null);
		tabbedPane.addTab("Test ISO Messages (Server)", null, pnlGuiMessagesServer, null);
		tabbedPane.addTab("XML", null, pnlXmlConfig, null);
		
		txtFilePath = new JTextField(Iso8583Helper.getInstance().getXmlFilePath());
		txtFilePath.setEditable(false);
		add(txtFilePath);
		txtFilePath.setColumns(10);
		
		btnOpen = new JButton("");
		btnOpen.setToolTipText("Open");
		btnOpen.setIcon(new ImageIcon(FrmMain.class.getResource("/resource/openFile.png")));
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		add(btnOpen);
		
		btnSave = new JButton("");
		btnSave.setToolTipText("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		btnSave.setIcon(new ImageIcon(FrmMain.class.getResource("/resource/saveFile.png")));
		add(btnSave);
		
		btnNew = new JButton("");
		btnNew.setToolTipText("New");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newFile();
			}
		});
		btnNew.setIcon(new ImageIcon(FrmMain.class.getResource("/resource/newFile.png")));
		add(btnNew);
	}

	private void parseXML() {
		pnlGuiConfig.save();
		if (tabbedPane.getSelectedIndex() == 0)
			Iso8583Helper.getInstance().parseXmlToConfig(this);
		else 
			Iso8583Helper.getInstance().parseConfigToXML();
		
		pnlGuiConfig.updateTree();
		pnlGuiConfig.expandAllNodes();
	}
	
	private void newFile() {
		if (Iso8583Helper.getInstance().newFile(this)) {
			txtFilePath.setText("");
			Iso8583Helper.getInstance().parseXmlToConfig(this);
			
			pnlGuiConfig.updateTree();
		}
	}
	
	private void openFile() {
		JFileChooser file = new JFileChooser();
		file.setAcceptAllFileFilterUsed(false);
		file.setFileFilter(new FileNameExtensionFilter("xml files (*.xml)", "xml"));
		
		if (!txtFilePath.getText().equals("")) 
			file.setCurrentDirectory(new File(txtFilePath.getText()));
		
		if (file.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			txtFilePath.setText(file.getSelectedFile().getAbsolutePath());
			Iso8583Helper.getInstance().openFile(this, txtFilePath.getText());
			Iso8583Helper.getInstance().parseXmlToConfig(this);
			
			pnlGuiConfig.updateTree();
			pnlGuiConfig.expandAllNodes();
		}
	}
	
	public boolean save() {
		
		boolean fileSaved = false;
		
		Iso8583Helper.getInstance().parseXmlToConfig(this);
		if (txtFilePath.getText().equals("")) {
			JFileChooser file = new JFileChooser();
			file.setAcceptAllFileFilterUsed(false);
			file.setFileFilter(new FileNameExtensionFilter("xml files (*.xml)", "xml"));
			if (file.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				txtFilePath.setText(file.getSelectedFile().getAbsolutePath());
				if (txtFilePath.getText().indexOf(".xml") < 0) txtFilePath.setText(txtFilePath.getText() +".xml");
			}
		}

		Iso8583Helper.getInstance().saveFile(this, txtFilePath.getText());
		fileSaved = (Iso8583Helper.getInstance().getXmlFilePath() != null);
		
		if (!fileSaved)
			JOptionPane.showMessageDialog(this, "You must inform a file to save.");
	
		return fileSaved;
	}

}

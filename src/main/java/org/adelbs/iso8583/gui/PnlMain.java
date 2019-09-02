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

import org.adelbs.iso8583.helper.Iso8583Config;
import org.adelbs.iso8583.xml.ISOConfigMarshallerException;

public class PnlMain extends JPanel {

	private static final long serialVersionUID = 2L;
	
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
	
	//Tabs
	private PnlGuiConfig pnlGuiConfig;
	private PnlGuiMessages pnlGuiMessagesClient;
	private PnlGuiMessages pnlGuiMessagesServer;
	private PnlXmlConfig pnlXmlConfig;
	private JTextField txtFilePath;
	private JButton btnSave;
	private JButton btnOpen;
	private JButton btnNew;

	private Iso8583Config isoConfig;
	
	private File lastCurrentDirectory = null;
	
	public PnlMain() {
		
		isoConfig = new Iso8583Config();
		
		//Painel principal
		setLayout(null);
		pnlGuiConfig = new PnlGuiConfig(this);
		pnlGuiMessagesClient = new PnlGuiMessages(this, false);
		pnlGuiMessagesServer = new PnlGuiMessages(this, true);
		pnlXmlConfig = new PnlXmlConfig(this);
		
		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentResized(ComponentEvent e) {
				tabbedPane.setBounds(-2, 25, getWidth(), getHeight() - 82);
				btnSave.setBounds(getWidth() - 52, 0, 33, 25);
				btnOpen.setBounds(getWidth() - 87, 0, 33, 25);
				btnNew.setBounds(getWidth() - 122, 0, 33, 25);
				txtFilePath.setBounds(3, 1, getWidth() - 129, 22);
			}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		}); 
		
		//*** Adicionando as Abas ***
		add(tabbedPane);
		tabbedPane.addTab("ISO-8583 Request", null, pnlGuiMessagesClient, null);
		tabbedPane.addTab("ISO-8583 Server", null, pnlGuiMessagesServer, null);
		tabbedPane.addTab("ISO Configure", null, pnlGuiConfig, null);
		tabbedPane.addTab("XML", null, pnlXmlConfig, null);
		
		txtFilePath = new JTextField(isoConfig.getXmlFilePath());
		txtFilePath.setEditable(false);
		add(txtFilePath);
		txtFilePath.setColumns(10);
		
		btnOpen = new JButton("");
		btnOpen.setToolTipText("Open");
		btnOpen.setIcon(new ImageIcon(PnlMain.class.getResource("/org/adelbs/iso8583/resource/openFile.png")));
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
		btnSave.setIcon(new ImageIcon(PnlMain.class.getResource("/org/adelbs/iso8583/resource/saveFile.png")));
		add(btnSave);
		
		btnNew = new JButton("");
		btnNew.setToolTipText("New");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newFile();
			}
		});
		btnNew.setIcon(new ImageIcon(PnlMain.class.getResource("/org/adelbs/iso8583/resource/newFile.png")));
		add(btnNew);
		
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				parseXML();
			}
		});
	}
	
	private void parseXML() {	
		pnlGuiConfig.save(this);
		
		try {
			if (tabbedPane.getSelectedComponent().equals(pnlGuiConfig)) {
				isoConfig.parseXmlToConfig(this);
			}
			else {
				isoConfig.parseConfigToXML();
			}
		}
		catch (ISOConfigMarshallerException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "An error ocurred while creating the XML.\n\n" + e.getMessage());
		}
		
		pnlGuiConfig.updateTree();
		pnlGuiConfig.expandAllNodes();
	}
	
	private void newFile() {
		if (isoConfig.newFile(this)) {
			txtFilePath.setText("");
			isoConfig.parseXmlToConfig(this);
			
			pnlGuiConfig.updateTree();
		}
	}
	
	private void openFile() {
		JFileChooser file = new JFileChooser();
		file.setAcceptAllFileFilterUsed(false);
		file.setFileFilter(new FileNameExtensionFilter("xml files (*.xml)", "xml"));
		
		if (lastCurrentDirectory != null) 
			file.setCurrentDirectory(lastCurrentDirectory);
		
		if (file.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			txtFilePath.setText(file.getSelectedFile().getAbsolutePath());
			lastCurrentDirectory = file.getSelectedFile();			

			openXML();
		}
	}
	
	public void openXML(){
		if(!txtFilePath.getText().equals("")){
			isoConfig.openFile(this, txtFilePath.getText());
			isoConfig.parseXmlToConfig(this);
			
			pnlGuiConfig.updateTree();
			pnlGuiConfig.expandAllNodes();
			
            pnlGuiConfig.updateTree();
		}
	}
	
	private boolean save() {
		boolean fileSaved = false;
		
		try{
			if (tabbedPane.getComponent(tabbedPane.getSelectedIndex()) instanceof PnlGuiConfig){
				pnlGuiConfig.save(this);
				isoConfig.parseConfigToXML();
			}else{
				isoConfig.parseXmlToConfig(this);
			}
		}catch(ISOConfigMarshallerException e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "An error ocurred while creating the XML.\n\n" + e.getMessage());
		}
		
		if (txtFilePath.getText().equals("")) {
			JFileChooser file = new JFileChooser();
			file.setAcceptAllFileFilterUsed(false);
			file.setFileFilter(new FileNameExtensionFilter("xml files (*.xml)", "xml"));
			
			if (lastCurrentDirectory != null) 
				file.setCurrentDirectory(lastCurrentDirectory);
			
			if (file.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				lastCurrentDirectory = file.getSelectedFile();
				txtFilePath.setText(file.getSelectedFile().getAbsolutePath());
				if (txtFilePath.getText().indexOf(".xml") < 0) txtFilePath.setText(txtFilePath.getText() +".xml");
			}
		}

		isoConfig.saveFile(this, txtFilePath.getText());
		fileSaved = (isoConfig.getXmlFilePath() != null);
		
		if (!fileSaved)
			JOptionPane.showMessageDialog(this, "You must inform a file to save.");
	
		return fileSaved;
	}
	
	public PnlGuiConfig getPnlGuiConfig() {
		return pnlGuiConfig;
	}

	public Iso8583Config getIso8583Config() {
		return isoConfig;
	}
	
	public JTextField getTxtFilePath() {
		return txtFilePath;
	}
	
	public PnlGuiMessages getPnlGuiMessagesClient() {
		return pnlGuiMessagesClient;
	}

	public File getLastCurrentDirectory() {
		return lastCurrentDirectory;
	}

	public void setLastCurrentDirectory(File lastCurrentDirectory) {
		this.lastCurrentDirectory = lastCurrentDirectory;
	}
	
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
}

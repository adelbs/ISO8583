package org.adelbs.iso8583.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.adelbs.iso8583.helper.Iso8583Helper;

public class FrmMain extends JFrame {

	private static final long serialVersionUID = 1L;
	private static FrmMain instance;
	
	private static int MIN_WIDTH = 755;
	private static int MIN_HEIGHT = 570;

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

	
	public static FrmMain getInstance() {
		if (instance == null)
			instance = new FrmMain();
		return instance;
	}
	
	private FrmMain() {
		addWindowListener(new WindowListener() {
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowOpened(WindowEvent e) {}
		});
		
		//*** Configurações da janela principal ***
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setIconImage(Toolkit.getDefaultToolkit().getImage(FrmMain.class.getResource("/resource/package.png")));
		setTitle("Adelbs-ISO8583 v."+ Iso8583Helper.VERSION);
		
		//Tamanho e posição
		setBounds(0, 0, MIN_WIDTH, MIN_HEIGHT);
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		int w = getSize().width;
		int h = getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;
		setLocation(x, y);

		//Painel principal
		setLayout(null);
		pnlGuiConfig = new PnlGuiConfig();
		pnlGuiMessagesClient = new PnlGuiMessages(false);
		pnlGuiMessagesServer = new PnlGuiMessages(true);
		pnlXmlConfig = new PnlXmlConfig();
		
		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentResized(ComponentEvent e) {
				tabbedPane.setBounds(-2, 25, getWidth(), getHeight() - 72);
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
		
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				parseXML();
			}
		});
		
		//Exibindo a tela
		setVisible(true);
	}
	
	private void parseXML() {
		pnlGuiConfig.save();
		if (tabbedPane.getSelectedIndex() == 0)
			Iso8583Helper.getInstance().parseXmlToConfig();
		else 
			Iso8583Helper.getInstance().parseConfigToXML();
		
		pnlGuiConfig.updateTree();
		pnlGuiConfig.expandAllNodes();
	}
	
	private void newFile() {
		if (Iso8583Helper.getInstance().newFile(this)) {
			txtFilePath.setText("");
			Iso8583Helper.getInstance().parseXmlToConfig();
			
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
			Iso8583Helper.getInstance().parseXmlToConfig();
			

			pnlGuiConfig.updateTree();
			pnlGuiConfig.expandAllNodes();
			
		//	Iso8583Helper.getInstance().validateAllNodes();
			
			pnlGuiConfig.updateTree();
		}
	}
	
	private boolean save() {
		
		boolean fileSaved = false;
		
		if (tabbedPane.getSelectedIndex() == 0)
			Iso8583Helper.getInstance().parseConfigToXML();
		else 
			Iso8583Helper.getInstance().parseXmlToConfig();
		
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
	
	public PnlGuiConfig getPnlGuiConfig() {
		return pnlGuiConfig;
	}
}

package org.adelbs.iso8583.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.border.LineBorder;

import org.adelbs.iso8583.helper.Iso8583Helper;

public class FrmMain extends JFrame {

	private static final long serialVersionUID = 1L;
	private PnlMain pnlMain = new PnlMain();
	
	private static int MIN_WIDTH = 755;
	private static int MIN_HEIGHT = 540;
	
	public FrmMain() {
		this.addWindowListener(new WindowListener() {
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
		getContentPane().setLayout(new BorderLayout(0, 0));
		pnlMain.setBorder(new LineBorder(new Color(0, 0, 0)));

		//Painel principal
		getContentPane().add(pnlMain);
		
		//Exibindo a tela
		setVisible(true);
	}
}

package org.adelbs.iso8583;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.adelbs.iso8583.gui.PnlGuiConfig;
import org.adelbs.iso8583.gui.PnlMain;

public final class Main extends JFrame {

	private static final long serialVersionUID = 2L;
	
	private static int MIN_WIDTH = 770;
	private static int MIN_HEIGHT = 570;

	private PnlMain pnlMain;

	public static void main(String[] args) {
		new Main();
	}
	
	private Main() {
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
		setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/org/adelbs/iso8583/resource/package.png")));
		setTitle("Adelbs-ISO8583 v.1.0");
		
		//Tamanho e posição
		setBounds(0, 0, MIN_WIDTH, MIN_HEIGHT);
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		int w = getSize().width;
		int h = getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;
		setLocation(x, y);

		//Painel principal
		pnlMain = new PnlMain();
		pnlMain.setBounds(0, 0, getWidth(), getHeight());
		getContentPane().setLayout(null);
		getContentPane().add(pnlMain);
		
		getContentPane().addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentResized(ComponentEvent e) {
				pnlMain.setBounds(0, 0, getWidth(), getHeight());
			}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		}); 

		
		//Exibindo a tela
		setVisible(true);
	}
	
	public PnlGuiConfig getPnlGuiConfig() {
		return pnlMain.getPnlGuiConfig();
	}
}
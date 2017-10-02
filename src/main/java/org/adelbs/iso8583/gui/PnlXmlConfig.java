package org.adelbs.iso8583.gui;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class PnlXmlConfig extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private JScrollPane scrollPane = new JScrollPane();
	
	public PnlXmlConfig(PnlMain pnlMain) {
		
		setLayout(null);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		scrollPane.setViewportView(pnlMain.getIso8583Config().getXmlText());

		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentResized(ComponentEvent e) {
				scrollPane.setBounds(0, 0, getWidth() - 15, getHeight());
			}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		}); 
		
		add(scrollPane);
	}
}

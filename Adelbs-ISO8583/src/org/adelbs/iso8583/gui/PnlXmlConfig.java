package org.adelbs.iso8583.gui;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.adelbs.iso8583.helper.Iso8583Helper;

public class PnlXmlConfig extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private JScrollPane scrollPane = new JScrollPane();
	
	public PnlXmlConfig() {
		
		setLayout(null);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		scrollPane.setViewportView(Iso8583Helper.getInstance().getXmlText());

		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentResized(ComponentEvent e) {
				scrollPane.setBounds(0, 0, getWidth(), getHeight());
			}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		}); 
		
		add(scrollPane);
	}
}

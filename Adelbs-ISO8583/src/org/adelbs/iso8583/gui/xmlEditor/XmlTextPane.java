package org.adelbs.iso8583.gui.xmlEditor;

import java.awt.Font;

import javax.swing.JTextPane;

public class XmlTextPane extends JTextPane {
	 
    private static final long serialVersionUID = 6270183148379328085L;
 
    public XmlTextPane() {
         
        // Set editor kit
    	this.setEditorKitForContentType("text/xml", new XmlEditorKit());
        this.setContentType("text/xml");
        
        this.setFont(new Font("Arial", Font.PLAIN, 14));
    }
     
}
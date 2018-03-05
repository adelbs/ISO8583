package org.adelbs.iso8583.gui.xmlEditor;

import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

public class XmlEditorKit extends StyledEditorKit {
	 
    private static final long serialVersionUID = 2969169649596107758L;
    private ViewFactory xmlViewFactory;
 
    public XmlEditorKit() {
        xmlViewFactory = new XmlViewFactory();
    }
     
    @Override
    public ViewFactory getViewFactory() {
        return xmlViewFactory;
    }
 
    @Override
    public String getContentType() {
        return "text/xml";
    }
}
package org.adelbs.iso8583.gui.xmlEditor;

import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import javax.swing.text.Element;

public class XmlViewFactory extends Object implements ViewFactory {
	 

	@Override
	public View create(Element elem) {
		return new XmlView(elem);
	}
 
}
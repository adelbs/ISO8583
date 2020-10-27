package org.adelbs.iso8583.gui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.adelbs.iso8583.vo.GenericIsoVO;
import org.adelbs.iso8583.vo.MessageVO;

public class ISOTreeRenderer extends DefaultTreeCellRenderer  {

	private static final long serialVersionUID = 2L;

	private static final Icon isoIcon = new ImageIcon(PnlGuiConfig.class.getResource("/img/isoIcon.png"));
	private static final Icon validMessage = new ImageIcon(PnlGuiConfig.class.getResource("/img/validMessage.png"));
	private static final Icon validField = new ImageIcon(PnlGuiConfig.class.getResource("/img/validField.png"));
	
	@Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

		setOpenIcon(isoIcon);
		setClosedIcon(isoIcon);
		setLeafIcon(isoIcon);
		
		setFont(new Font("Arial", Font.PLAIN, 14));
		
		if (!(((DefaultMutableTreeNode) value).getUserObject() instanceof String)) {
			GenericIsoVO isoVO = (GenericIsoVO) ((DefaultMutableTreeNode) value).getUserObject();
			
			if (isoVO instanceof MessageVO)
				setIcon(validMessage);
			else
				setIcon(validField);
        }

		return this;
    }
	
}

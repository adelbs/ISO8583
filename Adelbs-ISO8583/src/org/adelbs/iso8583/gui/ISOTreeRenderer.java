package org.adelbs.iso8583.gui;

import java.awt.Color;
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

	private static final long serialVersionUID = 1L;

	private static final Icon isoIcon = new ImageIcon(PnlGuiConfig.class.getResource("/org/adelbs/iso8583/resource/isoIcon.png"));
	private static final Icon validMessage = new ImageIcon(PnlGuiConfig.class.getResource("/org/adelbs/iso8583/resource/validMessage.png"));
	private static final Icon invalidMessage = new ImageIcon(PnlGuiConfig.class.getResource("/org/adelbs/iso8583/resource/invalidMessage.png"));
	private static final Icon validField = new ImageIcon(PnlGuiConfig.class.getResource("/org/adelbs/iso8583/resource/validField.png"));
	private static final Icon invalidField = new ImageIcon(PnlGuiConfig.class.getResource("/org/adelbs/iso8583/resource/invalidField.png"));
	
	@Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

		setOpenIcon(isoIcon);
		setClosedIcon(isoIcon);
		setLeafIcon(isoIcon);
		
		setFont(new Font("Arial", Font.PLAIN, 14));
		
		if (!(((DefaultMutableTreeNode) value).getUserObject() instanceof String)) {
			GenericIsoVO isoVO = (GenericIsoVO) ((DefaultMutableTreeNode) value).getUserObject();
			
			if (!isoVO.isValid()) {
				setForeground(Color.RED);
				
				if (isoVO instanceof MessageVO)
					setIcon(invalidMessage);
				else
					setIcon(invalidField);
			}
			else{
				if (isoVO instanceof MessageVO)
					setIcon(validMessage);
				else
					setIcon(validField);
			}
        }

		return this;
    }
	
}

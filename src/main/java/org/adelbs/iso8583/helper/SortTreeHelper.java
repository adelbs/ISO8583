package org.adelbs.iso8583.helper;

import java.util.Comparator;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.adelbs.iso8583.gui.PnlGuiConfig;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.MessageVO;

public class SortTreeHelper {

	private static boolean updateTree = false;
	
	@SuppressWarnings("rawtypes")
	public static void sortTree(PnlGuiConfig pnlGuiConfig, DefaultMutableTreeNode root, JTree treeTypes) {
		if (root != null) {
			Enumeration e = root.depthFirstEnumeration();
			while (e.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
				if (!node.isLeaf()) {
					sort(node);   //selection sort
				}
			}
			
			//Atualizando a arvore
			if (updateTree) {
				TreePath treePath = treeTypes.getSelectionPath();
				DefaultTreeModel model = (DefaultTreeModel) treeTypes.getModel();
				model.reload();
				treeTypes.setSelectionPath(treePath);
				updateTree = false;
			}

			pnlGuiConfig.getPnlFieldCondition().ckDynamicClick();
		}
	}
	
	private static Comparator< DefaultMutableTreeNode> tnc = new Comparator< DefaultMutableTreeNode>() {
		@Override public int compare(DefaultMutableTreeNode a, DefaultMutableTreeNode b) {
			if (a.getUserObject() instanceof FieldVO) {
				Integer sa = ((FieldVO) a.getUserObject()).getBitNum();
				Integer sb = ((FieldVO) b.getUserObject()).getBitNum();
				return sa.compareTo(sb);
			}
			else if (a.getUserObject() instanceof MessageVO) {
				String sa = ((MessageVO) a.getUserObject()).getType();
				String sb = ((MessageVO) b.getUserObject()).getType();
				return sa.compareToIgnoreCase(sb);
			}
			else
				return -1;
		}
	};

	//selection sort
	public static void sort(DefaultMutableTreeNode parent) {
		int n = parent.getChildCount();
		for (int i = 0; i < n - 1; i++) {
			int min = i;
			for (int j = i + 1; j < n; j++) {
				if (tnc.compare((DefaultMutableTreeNode) parent.getChildAt(min), (DefaultMutableTreeNode) parent.getChildAt(j)) > 0) {
					min = j;
				}
			}
			if (i != min) {
				MutableTreeNode a = (MutableTreeNode) parent.getChildAt(i);
				MutableTreeNode b = (MutableTreeNode) parent.getChildAt(min);
				parent.insert(b, i);
				parent.insert(a, min);
				
				updateTree = true;
			}
		}
	}
}

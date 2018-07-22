/*
 * Copyright (c) 2010 MIT Strategic Engineering Research Group
 * 
 * This file is part of SpaceNet 2.5r2.
 * 
 * SpaceNet 2.5r2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SpaceNet 2.5r2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SpaceNet 2.5r2.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.mit.spacenet.gui.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import edu.mit.spacenet.gui.component.CheckBoxNode.CheckBoxNodeComponent;

/**
 * A tree representation of check box nodes.
 * 
 * @author Paul Grogan
 */
public class CheckBoxTree extends JTree {
	private static final long serialVersionUID = 1702544297754862171L;
	
	/**
	 * The constructor.
	 * 
	 * @param model the check box tree model
	 */
	public CheckBoxTree(CheckBoxTreeModel model) {
		super(model);
        setCellRenderer(new DefaultTreeCellRenderer() {
			private static final long serialVersionUID = -1994646294945996581L;
			public Component getTreeCellRendererComponent(JTree tree, Object value,
		            boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
				if(value instanceof CheckBoxNode) {
					CheckBoxNodeComponent component = ((CheckBoxNode)value).getCheckBox();
					if(selected) {
						component.getLabel().setBackground(UIManager.getColor("Tree.selectionBackground"));
						component.getLabel().setForeground(UIManager.getColor("Tree.selectionForeground"));
						component.getLabel().setBorder(BorderFactory.createLineBorder(UIManager.getColor("Tree.selectionBorderColor"),1));
					} else {
						component.getLabel().setBackground(UIManager.getColor("Tree.textBackground"));
						component.getLabel().setForeground(UIManager.getColor("Tree.textForeground"));
						component.getLabel().setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
					}
					return component;
				} else {
					return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
				}
			}
        });

        setCellEditor(new DefaultTreeCellEditor(this, (DefaultTreeCellRenderer)getCellRenderer()) {
        	public boolean isCellEditable(EventObject event) {
                boolean returnValue = false;
                if (event instanceof MouseEvent) {
                    MouseEvent mouseEvent = (MouseEvent) event;
                    TreePath path = tree.getPathForLocation(mouseEvent.getX(),  mouseEvent.getY());
                    if (path != null) {
                    	Object node = path.getLastPathComponent();
                        if ((node != null) && (node instanceof CheckBoxNode)) {
                            returnValue = true;
                        }
                    }
                }
                return returnValue;
            }
        	public Component getTreeCellEditorComponent(JTree tree, Object value,
                    boolean selected, boolean expanded, boolean leaf, int row) {
        		Component editor =  renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);
        		if(editor instanceof CheckBoxNodeComponent) {
        			final CheckBoxNode node = (CheckBoxNode)value;
	        		ItemListener itemListener = new ItemListener() {
	                     public void itemStateChanged(ItemEvent itemEvent) {
                        	 getModel().nodeChanged(node);
	                         if (itemEvent.getStateChange()==ItemEvent.SELECTED) {
	                        	 getModel().checkChildren(node);
	                         } else {
	                        	 getModel().uncheckChildren(node);
	                         }
	                     }
	                 };
	                ((CheckBoxNodeComponent)editor).getCheckBox().addItemListener(itemListener);
        		}
        		return editor;
        	}
        });
        
        setEditable(true);
	}
	
	/**
	 * Expands all nodes.
	 */
	public void expandAll() {
		for(int i=0; i<getRowCount(); i++) {
			expandRow(i);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JTree#getModel()
	 */
	@Override
	public CheckBoxTreeModel getModel() {
		return (CheckBoxTreeModel)super.getModel();
	}
    
    /**
     * The Class CheckBoxNodeRenderer.
     */
    class CheckBoxNodeRenderer implements TreeCellRenderer {
        private CheckBoxNodeComponent valueRenderer;
        private DefaultTreeCellRenderer rootRenderer = new DefaultTreeCellRenderer();
        private Color textForeground, textBackground;

        protected CheckBoxNodeComponent getValueRenderer() {
            return valueRenderer;
        }
        
        /**
         * Instantiates a new check box node renderer.
         */
        public CheckBoxNodeRenderer() {
            textForeground = UIManager.getColor("Tree.textForeground");
            textBackground = UIManager.getColor("Tree.textBackground");
        }
        
        /* (non-Javadoc)
         * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
         */
        public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            if(value instanceof CheckBoxNode) {
                valueRenderer = ((CheckBoxNode)value).getCheckBox();
                valueRenderer.setForeground(textForeground);
                valueRenderer.setBackground(textBackground);
                return valueRenderer;
            } else {
                return rootRenderer.getTreeCellRendererComponent(tree,
                    value, selected, expanded, leaf, row, hasFocus);
            }
        }
    }
}

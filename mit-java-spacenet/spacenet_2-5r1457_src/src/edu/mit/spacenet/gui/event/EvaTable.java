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
package edu.mit.spacenet.gui.event;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import edu.mit.spacenet.domain.element.CrewMember;
import edu.mit.spacenet.domain.element.ElementType;
import edu.mit.spacenet.domain.element.I_State;

/**
 * A table for managing EVAs and explorations by selecting crew and EVA states.
 * 
 * @author Paul Grogan
 */
public class EvaTable extends JTable {
	private static final long serialVersionUID = 764356022370707951L;
	private EvaTableModel model;
	
	/**
	 * Instantiates a new eva table.
	 */
	public EvaTable() {
		model = new EvaTableModel();
		setModel(model);
		
		getColumnModel().getColumn(0).setHeaderValue("EVA?");
		getColumnModel().getColumn(0).setMaxWidth(50);
		getColumnModel().getColumn(1).setHeaderValue("Crew Member");
		getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(value instanceof CrewMember)
                	setIcon(ElementType.CREW_MEMBER.getIconType().getIcon());
                return this;
			}
		});
		getColumnModel().getColumn(2).setHeaderValue("EVA State");
		getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(value!=null && value instanceof I_State)
                	setIcon(((I_State)value).getStateType().getIcon());
                else 
                	setIcon(null);
                return this;
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JTable#getModel()
	 */
	public EvaTableModel getModel() {
		return model;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JTable#getCellEditor(int, int)
	 */
	public TableCellEditor getCellEditor(int row, int column) {
		if(column==2) {
			JComboBox stateCombo = new JComboBox();
			stateCombo.setRenderer(new DefaultListCellRenderer() {
				private static final long serialVersionUID = -2255885956722142642L;
				public Component getListCellRendererComponent(JList list, Object value, 
						int index, boolean isSelected, boolean cellHasFocus) {
					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if(value instanceof I_State)
						setIcon(((I_State)value).getStateType().getIcon());
					return this;
				}
			});
			for(I_State state : ((CrewMember)getValueAt(row, 1)).getStates()) {
				stateCombo.addItem(state);
	    	}
	    	return new DefaultCellEditor(stateCombo);
		} else return super.getCellEditor(row, column);
	}
	
	/**
	 * The Class EvaTableModel.
	 */
	public class EvaTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -6033117922087345936L;
		private SortedMap<CrewMember, I_State> data;
		private Map<CrewMember, Boolean> visibility;
		
		/**
		 * Instantiates a new eva table model.
		 */
		public EvaTableModel() {
			data = new TreeMap<CrewMember, I_State>();
			visibility = new HashMap<CrewMember, Boolean>();
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getColumnCount() {
			return 3;
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			return data.size();
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int row, int col) {
			if(col==0) return visibility.get(getValueAt(row,1));
			else if(col==1) return data.keySet().toArray()[row];
			else {
				if(visibility.get(getValueAt(row,1)))
					return data.get(getValueAt(row,1));
				else return null;
			}
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		public Class<?> getColumnClass(int col) {
			if(col==0) return Boolean.class;
			else if(col==1) return Object.class;
			else return Object.class;
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int row, int col) {
			if(col==0) return true;
			else if(col==1) return false;
			else return (Boolean)getValueAt(row, 0);
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
		 */
		public void setValueAt(Object value, int row, int col) {
			if(col==0) {
				if((Boolean)value) {
					I_State evaState = ((CrewMember)getValueAt(row,1)).getCurrentState();
					for(I_State s : ((CrewMember)getValueAt(row,1)).getStates()) {
						if(s.getName().toLowerCase().contains("eva")) {
							evaState = s;
							break;
						}
					}
					data.put((CrewMember)getValueAt(row,1),evaState);	
				}
				visibility.put((CrewMember)getValueAt(row,1), (Boolean)value);
			}
			else data.put((CrewMember)getValueAt(row,1),(I_State)value);
			fireTableRowsUpdated(row, row);
		}
		
		/**
		 * Put.
		 * 
		 * @param c the c
		 * @param s the s
		 * @param isVisible the is visible
		 */
		public void put(CrewMember c, I_State s, boolean isVisible) {
			data.put(c, s);
			visibility.put(c, isVisible);
			fireTableDataChanged();
		}
		
		/**
		 * Clear.
		 */
		public void clear() {
			data.clear();
			visibility.clear();
			fireTableDataChanged();
		}
		
		/**
		 * Gets the data.
		 * 
		 * @return the data
		 */
		public SortedMap<CrewMember, I_State> getData() {
			TreeMap<CrewMember, I_State> retData = new TreeMap<CrewMember, I_State>();
			for(CrewMember c : data.keySet()) {
				if(visibility.get(c)) {
					retData.put(c, data.get(c));
				}
			}
			return retData;
		}
	}
}

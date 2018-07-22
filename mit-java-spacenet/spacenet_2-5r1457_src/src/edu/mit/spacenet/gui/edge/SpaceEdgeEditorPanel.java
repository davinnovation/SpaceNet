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
package edu.mit.spacenet.gui.edge;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.mit.spacenet.domain.network.edge.Burn;
import edu.mit.spacenet.domain.network.edge.SpaceEdge;
import edu.mit.spacenet.domain.network.node.Node;
import edu.mit.spacenet.gui.component.UnitsWrapper;
import edu.mit.spacenet.gui.renderer.BurnListCellRenderer;
import edu.mit.spacenet.gui.renderer.NodeListCellRenderer;
import edu.mit.spacenet.util.GlobalParameters;

/**
 * A panel used to edit space edges.
 * 
 * @author Paul Grogan, ptgrogan@mit.edu
 * @author Ivo Ferreira
 */
public class SpaceEdgeEditorPanel extends AbstractEdgeEditorPanel {
	private static final long serialVersionUID = -2875579231969740002L;
	
	private SpaceEdge edge;
	
	private JComboBox originCombo, destinationCombo;
	private JTextField nameText;
	private SpinnerNumberModel durationModel;
	private JSpinner durationSpinner;
	private JTextArea descriptionText;
	private DefaultListModel burnListModel;
	private JList burnList;
	private JButton addBurnButton, editBurnButton, removeBurnButton, 
		moveBurnUpButton, moveBurnDownButton;
	
	/**
	 * Instantiates a new space edge editor panel.
	 * 
	 * @param edge the edge
	 */
	public SpaceEdgeEditorPanel(EdgeEditorDialog dialog, SpaceEdge edge) {
		super(dialog);
		this.edge = edge;
		buildPanel();
		initialize();
	}
	
	/**
	 * Builds the panel.
	 */
	public void buildPanel(){
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2,2,2,2);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.anchor = GridBagConstraints.LINE_END;
		c.fill = GridBagConstraints.NONE;
		add(new JLabel("Name: "), c);
		
		c.gridx++;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		nameText= new JTextField();
		add(nameText, c);

		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.fill = GridBagConstraints.NONE;
		add(new JLabel("Origin: "), c);
		
		c.gridx++;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.NONE;
		originCombo = new JComboBox();
		originCombo.setRenderer(new NodeListCellRenderer());
		for(Node t : getDialog().getDialog().getDataSource().getNodeLibrary()) 
			originCombo.addItem(t);
		originCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED) {
				edge.setOrigin((Node)e.getItem());}}
		});
		add(originCombo, c);
		originCombo.setToolTipText("Origin node of trajectory");
		
		c.gridx++;
		c.weightx = 0;
		c.anchor = GridBagConstraints.LINE_END;
		c.fill = GridBagConstraints.NONE;
		add(new JLabel("Destination: "), c);
		
		c.gridx++;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.NONE;
		destinationCombo = new JComboBox();
		destinationCombo.setRenderer(new NodeListCellRenderer());
		for(Node t : getDialog().getDialog().getDataSource().getNodeLibrary()) 
			destinationCombo.addItem(t);
		destinationCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED) {
				edge.setDestination((Node)e.getItem());}}
		});
		add(destinationCombo, c);
		destinationCombo.setToolTipText("Destination node of trajectory");
		
		c.gridy++;
		c.gridx = 0;
		c.weightx = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.fill = GridBagConstraints.NONE;
		add(new JLabel("Duration:"), c);
		
		c.gridx++;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.NONE;
		durationModel = new SpinnerNumberModel(0,0,Double.MAX_VALUE, GlobalParameters.getTimePrecision());
		durationSpinner = new JSpinner(durationModel);
		durationSpinner.setPreferredSize(new Dimension(75,20));
		add(new UnitsWrapper(durationSpinner, "days"), c);
		durationSpinner.setToolTipText("Total duration for the trajectory between origin and destination [days]");
		
		c.gridy++;
		c.gridx = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		c.fill = GridBagConstraints.NONE;
		add(new JLabel("Burns: "), c);
		
		c.gridx++;
		c.weighty = 1;
		c.weightx = 1;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		burnListModel = new DefaultListModel();
		burnList = new JList(burnListModel);
		burnList.setCellRenderer(new BurnListCellRenderer());
		burnList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				updateButtons();
			}
		});
		JScrollPane burnScroll = new JScrollPane(burnList);
		burnScroll.setPreferredSize(new Dimension(200,75));
		add(burnScroll, c);
		
		c.gridy++;
		c.weighty = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.NONE;
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
		buttonPanel.setOpaque(false);
		addBurnButton = new JButton("Add", new ImageIcon(getClass().getClassLoader().getResource("icons/add.png")));
		addBurnButton.setOpaque(false);
		addBurnButton.setMargin(new Insets(3,3,3,3));
		addBurnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BurnEditorDialog.createAndShowGUI(getThis(), new Burn());				
			}
		});
		buttonPanel.add(addBurnButton);
		editBurnButton = new JButton("Edit", new ImageIcon(getClass().getClassLoader().getResource("icons/cog.png")));
		editBurnButton.setOpaque(false);
		editBurnButton.setMargin(new Insets(3,3,3,3));
		editBurnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BurnEditorDialog.createAndShowGUI(getThis(), (Burn)burnListModel.getElementAt(burnList.getSelectedIndex()));
			}
		});
		buttonPanel.add(editBurnButton);
		removeBurnButton = new JButton("Delete", new ImageIcon(getClass().getClassLoader().getResource("icons/delete.png")));
		removeBurnButton.setOpaque(false);
		removeBurnButton.setMargin(new Insets(3,3,3,3));
		removeBurnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i : burnList.getSelectedIndices()){
					burnListModel.remove(i);
				}
				burnList.clearSelection();
				burnList.repaint();
			}}
		);
		buttonPanel.add(removeBurnButton);
		moveBurnUpButton = new JButton("Up");
		moveBurnUpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Burn burn = (Burn)burnList.getSelectedValue();
				burnListModel.set(burnList.getSelectedIndex(), burnListModel.get(burnList.getSelectedIndex()-1));
				burnListModel.set(burnList.getSelectedIndex()-1, burn);
				burnList.setSelectedIndex(burnList.getSelectedIndex()-1);
				burnList.repaint();
			}}
		);
		moveBurnUpButton.setEnabled(false);
		buttonPanel.add(moveBurnUpButton);
		moveBurnDownButton = new JButton("Down");
		moveBurnDownButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Burn burn = (Burn)burnList.getSelectedValue();
				burnListModel.set(burnList.getSelectedIndex(), burnListModel.get(burnList.getSelectedIndex()+1));
				burnListModel.set(burnList.getSelectedIndex()+1, burn);
				burnList.setSelectedIndex(burnList.getSelectedIndex()+1);
				burnList.repaint();
			}}
		);
		moveBurnDownButton.setEnabled(false);
		buttonPanel.add(moveBurnDownButton);
		
		add(buttonPanel, c);

		c.gridy++;
		c.gridx = 0;
		c.weightx = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		c.fill = GridBagConstraints.NONE;
		add(new JLabel("Description: "), c);
		
		c.gridx++;
		c.weighty = 1;
		c.weightx = 1;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		descriptionText = new JTextArea(5,5);
		descriptionText.setLineWrap(true);
		descriptionText.setWrapStyleWord(true);
		descriptionText.setFont(new Font("Sans-Serif", Font.PLAIN, 11));
		add(new JScrollPane(descriptionText), c);
	}
	
	/**
	 * Initializes the components.
	 */
	private void initialize() {
		if(edge!= null){
			originCombo.setSelectedItem(edge.getOrigin());
			destinationCombo.setSelectedItem(edge.getDestination());
			nameText.setText(edge.getName());
			durationModel.setValue(edge.getDuration());
			descriptionText.setText(edge.getDescription());
			burnListModel.clear();
			for(Burn burn : edge.getBurns()) {
				burnListModel.addElement(burn);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		super.paint(g);
		burnList.repaint();
		updateButtons();
	}
	
	/**
	 * Gets the burn list model.
	 * 
	 * @return the burn list model
	 */
	public DefaultListModel getBurnListModel() {
		return burnListModel;
	}
	
	/**
	 * Gets the burn list.
	 * 
	 * @return the burn list
	 */
	public JList getBurnList() {
		return burnList;
	}
	
	/**
	 * Update buttons.
	 */
	private void updateButtons() {
		editBurnButton.setEnabled(burnList.getSelectedIndices().length==1);
		removeBurnButton.setEnabled(burnList.getSelectedIndices().length>0);
		moveBurnUpButton.setEnabled(burnList.getSelectedIndices().length==1
				&& burnListModel.size()>1
				&& burnList.getSelectedIndex()>0
				&& ((Burn)burnListModel.get(burnList.getSelectedIndex())).getTime()
				== ((Burn)burnListModel.get(burnList.getSelectedIndex()-1)).getTime());
		moveBurnDownButton.setEnabled(burnList.getSelectedIndices().length>0
				&& burnListModel.size()>1
				&& burnList.getSelectedIndex()<burnListModel.size()-1
				&& ((Burn)burnListModel.get(burnList.getSelectedIndex())).getTime()
				== ((Burn)burnListModel.get(burnList.getSelectedIndex()+1)).getTime());
	}
	
	/**
	 * Gets this.
	 * 
	 * @return this
	 */
	private SpaceEdgeEditorPanel getThis() {
		return this;
	}
	
	/**
	 * Gets the edge duration.
	 * 
	 * @return the edge duration
	 */
	public double getEdgeDuration() {
		return durationModel.getNumber().doubleValue();
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.editor.gui.edge.AbstractEdgeEditorPanel#getEdge()
	 */
	public SpaceEdge getEdge() {
		return edge;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.editor.gui.edge.AbstractEdgeEditorPanel#isEdgeValid()
	 */
	public boolean isEdgeValid() {
		if(nameText.getText().length()==0){
			JOptionPane.showMessageDialog(getDialog(), 
					"Please enter an edge name.",
					"SpaceNet Warning",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if(originCombo.getSelectedItem()==null){
			JOptionPane.showMessageDialog(getDialog(), 
					"Please select an origin node.",
					"SpaceNet Warning",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if(destinationCombo.getSelectedItem()==null){
			JOptionPane.showMessageDialog(getDialog(), 
					"Please select a destination node.",
					"SpaceNet Warning",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if(originCombo.getSelectedItem()==destinationCombo.getSelectedItem()){
			JOptionPane.showMessageDialog(getDialog(), 
					"Origin node cannot also be the destination node.",
					"SpaceNet Warning",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if(burnListModel.size()==0){
			JOptionPane.showMessageDialog(getDialog(), 
					"Space edge must have at least one burn.",
					"SpaceNet Warning",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see edu.mit.spacenet.editor.gui.edge.AbstractEdgePanelEditor#saveEdge()
	 */
	public void saveEdge() {
		edge.setName(nameText.getText());
		edge.setOrigin((Node)originCombo.getSelectedItem());
		edge.setDestination((Node)destinationCombo.getSelectedItem());
		edge.setDuration(durationModel.getNumber().doubleValue());
		edge.getBurns().clear();
		for(int i=0; i<burnListModel.size(); i++) {
			edge.getBurns().add((Burn)burnListModel.getElementAt(i));
		}
		edge.setDescription(descriptionText.getText());
	}
}

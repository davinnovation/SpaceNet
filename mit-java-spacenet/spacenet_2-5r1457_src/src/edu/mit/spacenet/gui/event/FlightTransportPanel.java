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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import edu.mit.spacenet.domain.I_Container;
import edu.mit.spacenet.domain.element.I_Element;
import edu.mit.spacenet.domain.network.edge.Edge;
import edu.mit.spacenet.domain.network.edge.FlightEdge;
import edu.mit.spacenet.gui.SpaceNetFrame;
import edu.mit.spacenet.gui.component.CheckBoxTree;
import edu.mit.spacenet.gui.component.CheckBoxTreeModel;
import edu.mit.spacenet.gui.component.ContainerComboBox;
import edu.mit.spacenet.simulator.event.FlightTransport;

/**
 * A panel for viewing and editing a flight transport.
 * 
 * @author Paul Grogan
 */
public class FlightTransportPanel extends AbstractEventPanel {
	private static final long serialVersionUID = 769918023169742283L;
	
	private FlightTransport event;
	
	private JComboBox ddlFlight;
	private JProgressBar cargoCapacity, crewCapacity;
	
	private JLabel lblDestination, lblDuration;
	private CheckBoxTreeModel elementModel;
	private CheckBoxTree elementTree;
	
	/**
	 * Instantiates a new flight transport panel.
	 * 
	 * @param eventDialog the event dialog
	 * @param event the event
	 */
	public FlightTransportPanel(EventDialog eventDialog, FlightTransport event) {
		super(eventDialog, event);
		this.event = event;
		buildPanel();
		initialize();
	}
	private void buildPanel() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2,2,2,2);
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Flight: "), c);
		c.gridy = 2;
		add(new JLabel("Capacity: "), c);
		c.gridx = 2;
		c.anchor = GridBagConstraints.LINE_START;
		add(new JLabel("(Cargo)"), c);
		c.gridy = 3;
		add(new JLabel("(Crew)"), c);
		c.gridy = 4;
		c.gridx = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		add(new JLabel("Elements: "), c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		ddlFlight = new ContainerComboBox();
		add(ddlFlight, c);
		c.gridy = 1;
		JPanel flightPanel = new JPanel();
		flightPanel.setLayout(new BoxLayout(flightPanel, BoxLayout.PAGE_AXIS));
		lblDestination = new JLabel();
		flightPanel.add(lblDestination);
		lblDuration = new JLabel();
		flightPanel.add(lblDuration);
		add(flightPanel, c);
		c.gridx = 1;
		c.gridy = 2;
		cargoCapacity = new JProgressBar(0, 100);
		cargoCapacity.setStringPainted(true);
		add(cargoCapacity, c);
		c.gridy = 3;
		crewCapacity = new JProgressBar(0, 100);
		crewCapacity.setStringPainted(true);
		add(crewCapacity, c);

		c.gridx = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.gridy = 4;
		c.weightx = 1;
		c.weighty = 1;
		elementModel = new CheckBoxTreeModel();
		elementTree = new CheckBoxTree(elementModel);
		elementTree.setRootVisible(false);
		elementTree.setShowsRootHandles(true);
		JScrollPane elementScroll = new JScrollPane(elementTree);
		elementScroll.setPreferredSize(new Dimension(200,50));
		add(elementScroll, c);
	}
	private void initialize() {
		ddlFlight.addItem(event.getEdge());
		ddlFlight.setSelectedItem(event.getEdge());
		ddlFlight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateCapacities();
			}
		});
		
		elementModel = new CheckBoxTreeModel(getEventDialog().getSimNode());
		elementTree.setModel(elementModel);
		elementModel.setCheckedElements(event.getElements());
		elementModel.addTreeModelListener(new TreeModelListener() {
			public void treeNodesChanged(TreeModelEvent e) { 
				updateCapacities(); 
			}
			public void treeNodesInserted(TreeModelEvent e) { }
			public void treeNodesRemoved(TreeModelEvent e) { }
			public void treeStructureChanged(TreeModelEvent e) { }
		});
		elementTree.expandAll();
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.gui.event.AbstractEventPanel#getEvent()
	 */
	@Override
	public FlightTransport getEvent() {
		return event;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.gui.event.AbstractEventPanel#saveEvent()
	 */
	@Override
	public void saveEvent() {
		event.setEdge((FlightEdge)ddlFlight.getSelectedItem());
		
		event.getElements().clear();
		for(I_Element element : elementModel.getTopCheckedElements()) {
			event.getElements().add(getEventDialog().getElement(element.getUid()));
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.gui.event.AbstractEventPanel#updateView()
	 */
	@Override
	public void updateView() {
		FlightEdge flight = (FlightEdge)ddlFlight.getSelectedItem();
		ActionListener flightListener = ddlFlight.getActionListeners()[0];
		ddlFlight.removeActionListener(flightListener);
		ddlFlight.removeAllItems();
		for(Edge e : SpaceNetFrame.getInstance().getScenarioPanel().getScenario().getNetwork().getEdges()) {
			if(e.getOrigin().equals(getEventDialog().getNode())
					&& e instanceof FlightEdge) {
				ddlFlight.addItem(e);
				if(e.equals(flight)) {
					ddlFlight.setSelectedItem(e);
				}
			}
		}
		lblDestination.setText("Destination: " + 
				((FlightEdge)ddlFlight.getSelectedItem()).getDestination());
		lblDuration.setText("Duration: " + 
				((FlightEdge)ddlFlight.getSelectedItem()).getDuration() + " days");
		ddlFlight.addActionListener(flightListener);
		
		Set<I_Element> checkedElements = elementModel.getTopCheckedElements();
		elementModel = new CheckBoxTreeModel(getEventDialog().getSimNode());
		elementTree.setModel(elementModel);
		elementModel.setCheckedElements(checkedElements);
		elementModel.addTreeModelListener(new TreeModelListener() {
			public void treeNodesChanged(TreeModelEvent e) { 
				updateCapacities(); 
			}
			public void treeNodesInserted(TreeModelEvent e) { }
			public void treeNodesRemoved(TreeModelEvent e) { }
			public void treeStructureChanged(TreeModelEvent e) { }
		});
		elementTree.expandAll();
		updateCapacities();
	}
	private void updateCapacities() {
		double mass = 0;
		double crew = 0;
		DecimalFormat format = new DecimalFormat("0.0");
		
		boolean hasErrors = false;
		
		for(I_Element e : elementModel.getTopCheckedElements()) {
			if(e instanceof I_Container) {
				mass += ((I_Container)e).getCargoMass();
				crew += ((I_Container)e).getCrewSize();
			}
		}
		FlightEdge flight = (FlightEdge)(ddlFlight).getSelectedItem();
		if(flight != null) {
			if(mass >  flight.getMaxCargoMass()) {
				hasErrors = true;
				cargoCapacity.setForeground(new Color(153, 0, 0));
			} else {
				cargoCapacity.setForeground(new Color(0, 153, 0));
			}
			
			if(flight.getMaxCargoMass()==0) {
				cargoCapacity.setValue(100);
			} else {
				cargoCapacity.setValue((int)(100*mass/flight.getMaxCargoMass()));
			}
			cargoCapacity.setString(mass + " / " + flight.getMaxCargoMass() + " kg");
			
			if(crew > flight.getMaxCrewSize())  {
				hasErrors = true;
				crewCapacity.setForeground(new Color(153, 0, 0));
			} else {
				crewCapacity.setForeground(new Color(0, 153, 0));
			}
			if(flight.getMaxCrewSize()==0) {
				crewCapacity.setValue(100);
			} else {
				crewCapacity.setValue((int)(100*crew/flight.getMaxCrewSize()));
			}
			crewCapacity.setString((int)crew + " / " + flight.getMaxCrewSize() + " crew");
		} else {
			cargoCapacity.setForeground(Color.WHITE);
			cargoCapacity.setValue(0);
			cargoCapacity.setString(format.format(mass) + " kg");
			crewCapacity.setForeground(Color.WHITE);
			crewCapacity.setValue(0);
			crewCapacity.setString((int)crew + " crew");
		}
		getEventDialog().setOkButtonEnabled(!hasErrors);
	}
}

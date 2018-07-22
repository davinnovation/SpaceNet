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
package edu.mit.spacenet.simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.mit.spacenet.domain.element.I_Carrier;
import edu.mit.spacenet.domain.element.I_Element;
import edu.mit.spacenet.domain.element.PartApplication;
import edu.mit.spacenet.domain.network.edge.Edge;
import edu.mit.spacenet.domain.network.node.Body;
import edu.mit.spacenet.domain.network.node.Node;
import edu.mit.spacenet.domain.network.node.SurfaceNode;
import edu.mit.spacenet.domain.resource.Demand;
import edu.mit.spacenet.domain.resource.DemandSet;
import edu.mit.spacenet.domain.resource.Item;
import edu.mit.spacenet.scenario.Mission;
import edu.mit.spacenet.scenario.RepairItem;
import edu.mit.spacenet.scenario.Scenario;
import edu.mit.spacenet.scenario.SupplyEdge;
import edu.mit.spacenet.scenario.SupplyEdge.SupplyPoint;
import edu.mit.spacenet.simulator.event.I_Transport;
import edu.mit.spacenet.simulator.event.SurfaceTransport;
import edu.mit.spacenet.util.DateFunctions;
import edu.mit.spacenet.util.SerializeUtil;

/**
 * A simulator that logs detailed demand information.
 * 
 * @author Paul Grogan
 */
public class DemandSimulator extends AbstractSimulator {
	private Map<Integer, ArrayList<RepairItem>> sortedRepairItems;
	private Map<Integer, ArrayList<RepairItem>> unsortedRepairItems;
	
	private SortedSet<SupplyEdge> supplyEdges;
	private SortedSet<SupplyPoint> supplyPoints;
	private SortedMap<SupplyPoint, DemandSet> aggregatedNodeDemands;
	private SortedMap<SupplyEdge, DemandSet> aggregatedEdgeDemands;
	
	/**
	 * The constructor sets the scenario and initializes the data structures.
	 * 
	 * @param scenario the scenario
	 */
	public DemandSimulator(Scenario scenario) {
		super(scenario);

		sortedRepairItems = new HashMap<Integer, ArrayList<RepairItem>>();
		unsortedRepairItems = new HashMap<Integer, ArrayList<RepairItem>>();

		supplyEdges = new TreeSet<SupplyEdge>();
		supplyPoints = new TreeSet<SupplyPoint>();
		aggregatedNodeDemands = new TreeMap<SupplyPoint, DemandSet>();
		aggregatedEdgeDemands = new TreeMap<SupplyEdge, DemandSet>();
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.simulator.I_Simulator#simulate()
	 */
	public void simulate() {
		initializeSimulation();

		sortedRepairItems.clear();
		unsortedRepairItems.clear();
		
		for(Mission mission : getScenario().getMissionList()) {
			sortedRepairItems.put(getScenario().getMissionList().indexOf(mission), new ArrayList<RepairItem>());
			unsortedRepairItems.put(getScenario().getMissionList().indexOf(mission), new ArrayList<RepairItem>());
		}

		supplyEdges.clear();
		supplyPoints.clear();
		aggregatedNodeDemands.clear();
		aggregatedEdgeDemands.clear();
		
		// simulate events, serializing and saving after each time step
		while(getEvents().peek() != null) {
			getNextEvent();
			
			// find supply edges and supply points
			if(event instanceof I_Transport) {
				HashSet<I_Carrier> carriers = new HashSet<I_Carrier>();
				for(I_Element element : ((I_Transport)event).getElements()) {
					if(element instanceof I_Carrier) {
						carriers.add((I_Carrier)SerializeUtil.deepClone(element));
					}
				}
				
				boolean isReversed = false;
				if(event instanceof SurfaceTransport) 
					isReversed = ((SurfaceTransport)event).isReversed();
				
				SupplyEdge edge = new SupplyEdge(((I_Transport)event).getEdge(), 
						isReversed,
						((I_Transport)event).getTime(), 
						((I_Transport)event).getTime() + ((I_Transport)event).getDuration(), 
						carriers);
				supplyEdges.add(edge);
				aggregatedEdgeDemands.put(edge, new DemandSet());
				supplyPoints.add(edge.getPoint());
				aggregatedNodeDemands.put(edge.getPoint(), new DemandSet());
			}
			
			handleDemands();
			
			executeEvent();
		}
		tabulateRepairableDemands();
		aggregateDemands();
	}
	private void tabulateRepairableDemands() {
		double lastEndTime = 0;
		for(Mission mission : getScenario().getMissionList()) {
			if(mission.isCrewed()) {
				int index = getScenario().getMissionList().indexOf(mission);
				double startTime = lastEndTime;
				double endTime = DateFunctions.getDaysBetween(getScenario().getStartDate(), mission.getStartDate()) + mission.getDuration();
				lastEndTime = endTime;
								
				for(SimDemand simDemand : getUnsatisfiedDemands()) {
					if(simDemand.getTime() > startTime 
							&& simDemand.getTime() <= endTime
							&& simDemand.getElement() != null) {
						for(Demand demand : simDemand.getDemands()) {
							if(demand.getResource() instanceof Item) {
								for(PartApplication app : simDemand.getElement().getParts()) {
									if(app.getPart().equals(demand.getResource())
											&& app.getMeanTimeToRepair() > 0) {
										RepairItem i = new RepairItem(demand, simDemand.getElement());
										sortedRepairItems.get(index).add(i);
										break;
									}
								}
							}
						}
					}
				}				

				for(RepairItem i : sortedRepairItems.get(index)) {
					unsortedRepairItems.get(index).add(i);
				}
				
				Collections.sort(sortedRepairItems.get(index));
			}
		}
	}
	private void aggregateDemands() {
		for(SimDemand demand : getUnsatisfiedDemands()) {
			if(demand.getLocation() instanceof Node) {
				SupplyPoint point = null;
				for(SupplyPoint p : supplyPoints) {
					if(p.getNode().equals(demand.getLocation())
							&& p.getTime() <= demand.getTime()
							&& (point==null || p.getTime() > point.getTime())) {
						point = p;
					}
				}
				if(point==null 
						&& demand.getLocation() instanceof SurfaceNode 
						&& ((SurfaceNode)demand.getLocation()).getBody()==Body.EARTH) {
					// ignore demands at earth surface nodes
				} else if(point==null) {
					System.out.println("No supply point found to satisfy demands! " + demand.getTime() + " " + demand.getDemands());
				} else {
					aggregatedNodeDemands.get(point).addAll(demand.getDemands());
				}
			} else if(demand.getLocation() instanceof Edge) {
				SupplyEdge edge = null;
				for(SupplyEdge e : supplyEdges) {
					if(e.getEdge().equals((demand.getLocation()))
							&& demand.getTime() >= e.getStartTime()
							&& demand.getTime() <= e.getEndTime()) {
						edge = e;
					}
				}
				if(edge==null) {
					System.out.println("No supply edge found to satisfy demands! " + demand.getTime() + " " + demand.getDemands());
				} else {
					aggregatedEdgeDemands.get(edge).addAll(demand.getDemands());
				}
			}
		}
	}
	
	/**
	 * Gets the repairable items, unsorted (in order of occurrence in simulation).
	 * 
	 * @return the set of repairable items
	 */
	public Map<Integer, ArrayList<RepairItem>> getUnsortedRepairItems() {
		return unsortedRepairItems;
	}
	
	/**
	 * Gets the repairable items, sorted by decreasing value.
	 * 
	 * @return the set of repairable items
	 */
	public Map<Integer, ArrayList<RepairItem>> getSortedRepairItems() {
		return sortedRepairItems;
	}
	
	/**
	 * Gets the set of supply edges.
	 * 
	 * @return the set supply edges
	 */
	public SortedSet<SupplyEdge> getSupplyEdges() {
		return supplyEdges;
	}
	
	/**
	 * Gets the set of supply points.
	 * 
	 * @return the set of supply points
	 */
	public SortedSet<SupplyPoint> getSupplyPoints() {
		return supplyPoints;
	}
	
	/**
	 * Gets the demands aggregated to supply points (nodes).
	 * 
	 * @return the aggregated set of demands
	 */
	public SortedMap<SupplyPoint, DemandSet> getAggregatedNodeDemands() {
		return aggregatedNodeDemands;
	}
	
	/**
	 * Gets the demands aggregated to supply edges (edges).
	 * 
	 * @return the aggregated set of demands
	 */
	public SortedMap<SupplyEdge, DemandSet> getAggregatedEdgeDemands() {
		return aggregatedEdgeDemands;
	}
}

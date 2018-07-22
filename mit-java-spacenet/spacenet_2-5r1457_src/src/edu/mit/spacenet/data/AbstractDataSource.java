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
package edu.mit.spacenet.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.mit.spacenet.domain.ClassOfSupply;
import edu.mit.spacenet.domain.model.I_DemandModel;
import edu.mit.spacenet.domain.network.edge.Edge;
import edu.mit.spacenet.domain.network.node.Node;
import edu.mit.spacenet.domain.resource.GenericResource;
import edu.mit.spacenet.domain.resource.I_Item;
import edu.mit.spacenet.domain.resource.I_Resource;
import edu.mit.spacenet.domain.resource.Resource;

/**
 * An abstract implementation of the data source interface.
 * 
 * @author Paul Grogan
 */
public abstract class AbstractDataSource implements I_DataSource {
	protected List<Node> nodeLibrary;
	protected List<Edge> edgeLibrary;
	protected List<Edge> burnLibrary;
	protected List<I_Resource> resourceTypeLibrary;
	protected List<ElementPreview> elementPreviewLibrary;
	protected List<I_DemandModel> demandModelLibrary;
	private Date lastLoadDate;
	
	/**
	 * Instantiates a new abstract data source.
	 */
	public AbstractDataSource() {
		nodeLibrary = new ArrayList<Node>();
		edgeLibrary = new ArrayList<Edge>();
		resourceTypeLibrary = new ArrayList<I_Resource>();
		elementPreviewLibrary = new ArrayList<ElementPreview>();
		demandModelLibrary = new ArrayList<I_DemandModel>();
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.scenario.data.I_DataSource#getNodeLibrary()
	 */
	public final List<Node> getNodeLibrary() {
		return nodeLibrary;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.scenario.data.I_DataSource#getEdgeLibrary()
	 */
	public final List<Edge> getEdgeLibrary() {
		return edgeLibrary;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.scenario.data.I_DataSource#getEdgeLibrary()
	 */
	public final List<Edge> getBurnLibrary() {
		return burnLibrary;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.scenario.data.I_DataSource#getResourceLibrary()
	 */
	public final List<I_Resource> getResourceLibrary() {
		return resourceTypeLibrary;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.scenario.data.I_DataSource#getContinuousResourceLibrary()
	 */
	public final List<Resource> getContinuousResourceLibrary() {
		ArrayList<Resource> list = new ArrayList<Resource>();
		for(I_Resource r : resourceTypeLibrary) {
			if(r.getClass().equals(Resource.class)) {
				list.add((Resource)r);
			}
		}
		return list;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.scenario.data.I_DataSource#getItemLibrary()
	 */
	public final List<I_Item> getItemLibrary() {
		ArrayList<I_Item> list = new ArrayList<I_Item>();
		for(I_Resource r : resourceTypeLibrary) {
			if(r instanceof I_Item) {
				list.add((I_Item)r);
			}
		}
		return list;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.scenario.data.I_DataSource#getElementPreviewLibrary()
	 */
	public final List<ElementPreview> getElementPreviewLibrary() {
		return elementPreviewLibrary;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.scenario.data.I_DataSource#getDemandModelLibrary()
	 */
	public final List<I_DemandModel> getDemandModelLibrary() {
		return demandModelLibrary;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.scenario.data.I_DataSource#print()
	 */
	public void print() {
		System.out.println("Node Library:");
		for(Node n : nodeLibrary) {
			System.out.println("\t" + n.getTid() + ": " + n.getName() + " (" + n.getClass().getSimpleName() + ") ");
		}
		System.out.println("Edge Library:");
		for(Edge e : edgeLibrary) {
			System.out.println("\t" + e.getTid() + ": " + e.getName() + " (" + e.getClass().getSimpleName() + ") ");
		}
		System.out.println("Resource Library:");
		for(I_Resource r : resourceTypeLibrary) {
			System.out.println("\t" + r.getTid() + ": " + r.getName() + " (COS " + r.getClassOfSupply().getId() + ") ");
		}
		System.out.println("Element Library:");
		for(ElementPreview o : elementPreviewLibrary) {
			System.out.println("\t" + o.ID + ": " + o.NAME + " (" + o.TYPE + ") ");
		}
		System.out.println("Demand Model Library:");
		for(I_DemandModel o : demandModelLibrary) {
			System.out.println("\t" + o.getTid() + ": " + o.getName() + " (" + o.getDemandModelType() + ") ");
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.scenario.data.I_DataSource#loadLibraries(boolean)
	 */
	public final void loadLibraries(boolean updateNodes, 
			boolean updateEdges, 
			boolean updateResources) throws Exception {
		lastLoadDate = new Date();
		if(updateNodes) loadNodeLibrary();
		if(updateEdges) loadEdgeLibrary();
		if(updateResources) loadResourceLibrary();
		loadElementLibrary();
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.scenario.data.I_DataSource#loadLibraries()
	 */
	public final void loadLibraries() throws Exception {
		lastLoadDate = new Date();
		loadNodeLibrary();
		loadEdgeLibrary();
		loadResourceLibrary();
		loadElementLibrary();
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.scenario.data.I_DataSource#loadResource(int)
	 */
	public final I_Resource loadResource(int tid) {
		if(tid < 0) {
			return new GenericResource(ClassOfSupply.getInstance(-tid));
		}
		for(I_Resource resource : resourceTypeLibrary) {
			if(resource.getTid() == tid) return resource;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.data.I_DataSource#loadEdge(int)
	 */
	public final Edge loadEdge(int tid) {
		for(Edge edge : edgeLibrary) {
			if(edge.getTid()==tid) return edge;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.mit.spacenet.data.I_DataSource#loadNode(int)
	 */
	public final Node loadNode(int tid) {
		for(Node node : nodeLibrary) {
			if(node.getTid()==tid) return node;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.data.I_DataSource#loadElementPreview(int)
	 */
	public final ElementPreview loadElementPreview(int tid) {
		for(ElementPreview preview : elementPreviewLibrary) {
			if(preview.ID==tid) return preview;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.scenario.data.I_DataSource#getLastLoadDate()
	 */
	public final Date getLastLoadDate() {
		return lastLoadDate;
	}
}

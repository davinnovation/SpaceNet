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
package edu.mit.spacenet.domain.element;

import java.util.SortedSet;
import java.util.TreeSet;

import edu.mit.spacenet.domain.ClassOfSupply;
import edu.mit.spacenet.domain.Environment;
import edu.mit.spacenet.domain.I_Container;
import edu.mit.spacenet.domain.resource.DemandSet;
import edu.mit.spacenet.simulator.I_Simulator;
import edu.mit.spacenet.util.GlobalParameters;

/**
 * An element with the capability to contain crew and cargo up to capacity
 * constraints.
 * 
 * @author Paul Grogan
 */
public class Carrier extends Element implements I_Carrier {
	private SortedSet<I_Element> contents;
	private double maxCargoMass;
	private double maxCargoVolume;
	private Environment cargoEnvironment;
	private int maxCrewSize;
	
	/**
	 * The default constructor that initializes the contents, crew, and cargo
	 * structures.
	 */
	public Carrier() {
		super();
		setCargoEnvironment(Environment.UNPRESSURIZED);
		contents = new TreeSet<I_Element>();
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.I_Carrier#getCargoEnvironment()
	 */
	public Environment getCargoEnvironment() {
		return cargoEnvironment;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.I_Carrier#setCargoEnvironment(edu.mit.spacenet.domain.Environment)
	 */
	public void setCargoEnvironment(Environment cargoEnvironment) {
		this.cargoEnvironment = cargoEnvironment;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.I_Carrier#getMaxCargoMass()
	 */
	public double getMaxCargoMass() {
		return GlobalParameters.getRoundedMass(maxCargoMass);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.I_Carrier#setMaxCargoMass(double)
	 */
	public void setMaxCargoMass(double maxCargoMass) {
		this.maxCargoMass = maxCargoMass;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.I_Container#getCargoMass()
	 */
	public double getCargoMass() {
		double mass = 0;
		for(I_Element i : contents) {
			if(!(i instanceof CrewMember)) {
				mass += i.getTotalMass();
			}
		}
		return mass;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.I_Carrier#getMaxCargoVolume()
	 */
	public double getMaxCargoVolume() {
		return GlobalParameters.getRoundedVolume(maxCargoVolume);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.I_Carrier#setMaxCargoVolume(double)
	 */
	public void setMaxCargoVolume(double maxCargoVolume) {
		this.maxCargoVolume = maxCargoVolume;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.I_Container#getCargoVolume()
	 */
	public double getCargoVolume() {
		double volume = 0;
		for(I_Element e : contents) {
			if(!(e instanceof CrewMember)) {
				volume += e.getVolume();
			}
		}
		return GlobalParameters.getRoundedVolume(volume);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.I_Container#getCrewSize()
	 */
	public int getCrewSize() {
		int crew = 0;
		for(I_Element e : contents) {
			if(e instanceof CrewMember) {
				crew++;
			}
		}
		return crew;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.I_Container#getTotalCrewSize()
	 */
	public int getTotalCrewSize() {
		int crew = 0;
		for(I_Element e : contents) {
			if(e instanceof CrewMember) {
				crew++;
			} else if(e instanceof I_Carrier) {
				crew+=((I_Carrier)e).getTotalCrewSize();
			}
		}
		return crew;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.I_Carrier#getMaxCrewSize()
	 */
	public int getMaxCrewSize() {
		return maxCrewSize;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.I_Carrier#setMaxCrewSize(int)
	 */
	public void setMaxCrewSize(int maxCrewSize) {
		this.maxCrewSize = maxCrewSize;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.I_Container#canAdd(edu.mit.spacenet.domain.element.I_Element)
	 */
	public boolean canAdd(I_Element element) {
		if(element instanceof CrewMember) {
			if(contents.contains(element)) return true;
			else if(getCrewSize() < maxCrewSize) return true;
			else return false;
		} else {
			if(contents.contains(element)) {
				return true;
			} else if(getCargoMass() + element.getTotalMass() - getMaxCargoMass() > GlobalParameters.getMassPrecision()/2d ) {
				return false; // mass constrained
			} else if(GlobalParameters.isVolumeConstrained()
						&& getCargoVolume() + element.getVolume() - getMaxCargoVolume() > GlobalParameters.getVolumePrecision()/2d) {
					return false; // volume constrained
			} else if(GlobalParameters.isEnvironmentConstrained()
					&& (element.getEnvironment().equals(Environment.PRESSURIZED)
					&& getCargoEnvironment().equals(Environment.UNPRESSURIZED))) {
				return false; // environment constrained
			} else return getContainer()==null?true:getContainer().canAdd(element.getTotalMass());
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.I_Container#canAdd(double)
	 */
	public boolean canAdd(double addedMass) {
		if(getCargoMass() + addedMass - getMaxCargoMass() > GlobalParameters.getMassPrecision()/2d ) 
			return false;
		else return true;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.I_Container#add(edu.mit.spacenet.domain.element.I_Element)
	 */
	public boolean add(I_Element element) {
		if(contents.contains(element)) return true;
		else if(canAdd(element)) {
			if(element.getContainer()!=null) element.getContainer().remove(element);
			element.setContainer(this);
			contents.add(element);
			return true;
		} else return false;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.I_Container#remove(edu.mit.spacenet.domain.element.I_Element)
	 */
	public boolean remove(I_Element element) {
		if(contents.contains(element)) {
			element.setContainer(null);
			contents.remove(element);
			return true;
		} else return false;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.I_Container#getContents()
	 */
	public SortedSet<I_Element> getContents() {
		return contents;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.I_Container#getCompleteContents()
	 */
	public SortedSet<I_Element> getCompleteContents() {
		SortedSet<I_Element> elements = new TreeSet<I_Element>();
		for(I_Element element : contents) recursiveAdd(elements, element);
		return elements;
	}
	private void recursiveAdd(SortedSet<I_Element> elements, I_Element element) {
		elements.add(element);
		if(element instanceof I_Container) {
			for(I_Element child : ((I_Container)element).getContents()) {
				recursiveAdd(elements, child);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.Element#getTotalMass()
	 */
	@Override
	public double getTotalMass() {
		double mass = 0;
		for(I_Element e : contents) {
			mass += e.getTotalMass();
		}
		return GlobalParameters.getRoundedMass(super.getTotalMass() + mass);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.Element#getTotalMass(edu.mit.spacenet.domain.ClassOfSupply)
	 */
	@Override
	public double getTotalMass(ClassOfSupply cos) {
		double amount = super.getTotalMass(cos);
		for(I_Element e : getContents()) {
			amount+=e.getTotalMass(cos);
		}
		return GlobalParameters.getRoundedMass(amount);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.DomainType#print(int)
	 */
	@Override
	public void print(int tabOrder) {
		super.print(tabOrder);
		for(I_Element e : contents) {
			e.print(tabOrder+1);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.Element#satisfyDemands(edu.mit.spacenet.domain.resource.DemandSet, edu.mit.spacenet.simulator.I_Simulator)
	 */
	@Override
	public void satisfyDemands(DemandSet demands, I_Simulator simulator) {
		super.satisfyDemands(demands, simulator);
		for(I_Element i : contents) i.satisfyDemands(demands, simulator);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.Element#getElementType()
	 */
	@Override
	public ElementType getElementType() {
		return ElementType.CARRIER;
	}
}
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

import edu.mit.spacenet.util.GlobalParameters;

/**
 * A generic type of resource container where the element's mass and volume are
 * functions of the contents' mass and volume via mass and volume packing
 * factors. Instead of specifying maximum cargo masses and volumes, you instead
 * specify maximum masses and volumes for the entire container.
 * 
 * @author Paul Grogan
 */
public class GenericResourceContainer extends ResourceContainer {
	private double massPackingFactor, volumePackingFactor;
	private double maxMass, maxVolume;
	
	/**
	 * Instantiates a new generic resource container.
	 */
	public GenericResourceContainer() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.Element#getVolume()
	 */
	@Override
	public double getVolume() {
		return GlobalParameters.getRoundedVolume(super.getVolume() + volumePackingFactor*getCargoVolume());
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.Element#getMass()
	 */
	@Override
	public double getMass() {
		return GlobalParameters.getRoundedMass(super.getMass() + massPackingFactor*getCargoMass());
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.ResourceContainer#getMaxCargoMass()
	 */
	@Override
	public double getMaxCargoMass() {
		return GlobalParameters.getRoundedMass((maxMass-getMass())/(massPackingFactor+1));
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.ResourceContainer#setMaxCargoMass(double)
	 */
	@Override
	public void setMaxCargoMass(double maxCargoMass) {
		maxMass = maxCargoMass*(massPackingFactor+1)+getMass();
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.ResourceContainer#getMaxCargoVolume()
	 */
	@Override
	public double getMaxCargoVolume() {
		return GlobalParameters.getRoundedVolume((maxVolume-getVolume())/(volumePackingFactor+1));
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.domain.element.ResourceContainer#setMaxCargoVolume(double)
	 */
	@Override
	public void setMaxCargoVolume(double maxCargoVolume) {
		maxVolume = maxCargoVolume*(volumePackingFactor+1)+getVolume();
	}
	
	/**
	 * Gets the mass packing factor.
	 * 
	 * @return the mass packing factor
	 */
	public double getMassPackingFactor() {
		return massPackingFactor;
	}
	
	/**
	 * Sets the mass packing factor.
	 * 
	 * @param massPackingFactor the mass packing factor
	 */
	public void setMassPackingFactor(double massPackingFactor) {
		this.massPackingFactor = massPackingFactor;
	}
	
	/**
	 * Gets the volume packing factor.
	 * 
	 * @return the volume packing factor
	 */
	public double getVolumePackingFactor() {
		return volumePackingFactor;
	}
	
	/**
	 * Sets the volume packing factor.
	 * 
	 * @param volumePackingFactor the volume packing factor
	 */
	public void setVolumePackingFactor(double volumePackingFactor) {
		this.volumePackingFactor = volumePackingFactor;
	}
	
	/**
	 * Gets the maximum container mass.
	 * 
	 * @return the maximum mass (kilograms)
	 */
	public double getMaxMass() {
		return GlobalParameters.getRoundedMass(maxMass);
	}
	
	/**
	 * Sets the maximum container mass.
	 * 
	 * @param maxMass the maximum mass (kilograms)
	 */
	public void setMaxMass(double maxMass) {
		this.maxMass = maxMass;
	}
	
	/**
	 * Gets the maximum container volume.
	 * 
	 * @return the maximum container volume (cubic meters)
	 */
	public double getMaxVolume() {
		return GlobalParameters.getRoundedVolume(maxVolume);
	}
	
	/**
	 * Sets the maximum container volume.
	 * 
	 * @param maxVolume the maximum container volume (cubic meters)
	 */
	public void setMaxVolume(double maxVolume) {
		this.maxVolume = maxVolume;
	}
}
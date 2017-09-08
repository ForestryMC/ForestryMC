/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.climate;

import java.util.Collection;

import forestry.api.climate.IClimateState;

/**
 * @since 5.3.4
 */
public interface IClimateSourceContainer {

	/**
	 * Add a climate source to this container.
	 */
	void addClimateSource(IClimateSource source);

	/**
	 * Remove a climate source from this container.
	 */
	void removeClimateSource(IClimateSource source);

	/**
	 * @return All climate sources of this container.
	 */
	Collection<IClimateSource> getClimateSources();

	/**
	 * The climate of the {@link IClimateContainer} that results out of the {@link IClimateState} modifications of the {@link IClimateSource}s can not be higher than this bound.
	 */
	IClimateState getBoundaryUp();

	/**
	 * The climate of the {@link IClimateContainer} that results out of the {@link IClimateState} modifications of the {@link IClimateSource}s can not be lower than this bound.
	 */
	IClimateState getBoundaryDown();

	double getSizeModifier();

	/**
	 * Calculates the up and down boundary and the size modifier.
	 */
	void recalculateBoundaries();

	/**
	 * @return True if the container allows the {@link IClimateSource}s to work.
	 */
	boolean canWork();

}

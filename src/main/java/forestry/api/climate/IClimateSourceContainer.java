/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import java.util.Collection;

/**
 * @since 5.3.4
 *
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
	
	/**
	 * Has to be called if any range of a {@link IClimateSource} changes. It calculates the range of all {@link IClimateSource}s together.
	 */
	void recalculateBoundaries(double sizeModifier);
	
}

/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import javax.annotation.Nullable;

/**
 * A climate source is stored in a {@link IClimateSourceOwner}. It is used by the {@link IClimateSourceContainer}s to change the climate of a {@link IClimateContainer}. 
 * One {@link IClimateSource} can only be used by one {@link IClimateSourceContainer} at the same time.
 * <p>
 * In forestry it is used in a part of a {@link IClimateModifier}.
 */
public interface IClimateSource {
	
	/**
	 * The owner of this source.
	 */
	@Nullable
	IClimateSourceOwner getOwner();
	
	/**
	 * The range of this source.
	 */
	float getBoundaryModifier(ClimateType type, boolean boundaryUp);
	
	/**
	 * @return true if this source affects this sourceType of climate.
	 */
	boolean affectClimateType(ClimateType type);
	
	/**
	 * @param state the {@link IClimateState} that the source has to work on.
	 * @param target the by the {@link IClimateContainer} targeted {@link IClimateState}.
	 * 
	 */
	ClimateChange work(IClimateState state, ImmutableClimateState target);
	
	/**
	 * @return true if source has changed the climate at the last work cicle.
	 */
	boolean isActive();
	
	/**
	 * Called if the source is added to a {@link IClimateContainer}.
	 */
	void onAdded(IClimateContainer container);
	
	/**
	 * Called if the source is removed form a {@link IClimateContainer}.
	 */
	void onRemoved(IClimateContainer container);

}

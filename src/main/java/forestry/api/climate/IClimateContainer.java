/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import javax.annotation.Nullable;
import java.util.Collection;

import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;

public interface IClimateContainer extends INbtReadable, INbtWritable, IClimateSourceContainer {
	
	/**
	 * @return The parent of this container.
	 */
	IClimateHousing getParent();
	
	/**
	 * @return The current climate state.
	 */
	IClimateState getState();
	
	World getWorld();
	
	/**
	 * Update the climate in a region.
	 */
	void updateClimate(int ticks);
	
	/**
	 * Add a listener to this container.
	 */
	void addListaner(IClimateContainerListener listener);
	
	/**
	 * Remove a listener to this container.
	 */
	void removeListener(IClimateContainerListener listener);
	
	/**
	 * All listeners of this container.
	 */
	Collection<IClimateContainerListener> getListeners();
	
	/**
	 * @return the state that the container targets to get his climate state to.
	 */
	ImmutableClimateState getTargetedState();
	
	void setTargetedState(ImmutableClimateState state);
	
	@Nullable
	@SideOnly(Side.CLIENT)
	IClimateTable getTable(IClimateTableHelper helper, ClimateType type, boolean withTitle);

}

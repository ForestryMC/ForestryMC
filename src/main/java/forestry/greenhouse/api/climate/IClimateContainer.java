/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.climate;

import java.util.List;

import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;
import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.api.greenhouse.IClimateHousing;

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
	 * @return only not present if the controller never was assembled.
	 */
	IClimateState getTargetedState();

	void setTargetedState(IClimateState state);

	@SideOnly(Side.CLIENT)
	void addModifierInformation(IClimateModifier modifier, ClimateType type, List<String> lines);

}

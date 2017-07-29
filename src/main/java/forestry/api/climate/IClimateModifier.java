/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This modifier is used by {@link IClimateContainer}s.
 */
public interface IClimateModifier {
	
	/**
	 * Is called every 20 ticks by the {@link IClimateContainer} to change its climate state.
	 * 
	 * @param container the climate container
	 * @param newState the new climate state
	 * @param oldState the climate static of the previous modification.
	 * @param data A {@link NBTTagCompound} that can be used to save custom data or to send it to the client to show it in the {@link IClimateTable}.
	 * @return a modify newState or the newState.
	 */
	IClimateState modifyTarget(IClimateContainer container, IClimateState newState, ImmutableClimateState oldState, NBTTagCompound data);
	
	@SideOnly(Side.CLIENT)
	void addTableEntries(IClimateContainer container, IClimateState climateState, NBTTagCompound data, ClimateType tableType, IClimateTable table);
	
	/**
	 * @return The priority of this modifier. The modifier with the highest priority is called first and the modifier with the lowest at last.
	 */
	int getPriority();
	
}

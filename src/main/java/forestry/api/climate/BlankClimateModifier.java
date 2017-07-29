/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlankClimateModifier implements IClimateModifier {
	@Override
	public IClimateState modifyTarget(IClimateContainer container, IClimateState newState, ImmutableClimateState oldState, NBTTagCompound data) {
		return newState;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addTableEntries(IClimateContainer container, IClimateState climateState, NBTTagCompound data, ClimateType tableType, IClimateTable table) {
	}
	
	@Override
	public int getPriority() {
		return 0;
	}
}

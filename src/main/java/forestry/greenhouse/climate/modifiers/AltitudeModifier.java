/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.greenhouse.climate.modifiers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.BlankClimateModifier;
import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateContainer;
import forestry.api.climate.IClimateData;
import forestry.api.climate.IClimateHousing;
import forestry.api.climate.IClimateState;
import forestry.api.climate.ImmutableClimateState;
import forestry.core.utils.Translator;

public class AltitudeModifier extends BlankClimateModifier {

	private static final float TEMPERATURE_CHANGE = 0.01F;

	@Override
	public IClimateState modifyTarget(IClimateContainer container, IClimateState newState, ImmutableClimateState oldState, NBTTagCompound data) {
		World world = container.getWorld();
		IClimateHousing parent = container.getParent();
		if (world.provider.isSurfaceWorld()) {
			BlockPos position = parent.getCoordinates();
			float modifier = (64 - position.getY()) / 64F;
			float altitudeChange = modifier * TEMPERATURE_CHANGE;
			data.setFloat("altitudeChange", altitudeChange);
			newState = newState.addTemperature(altitudeChange);
		}
		return newState;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addData(IClimateContainer container, IClimateState climateState, NBTTagCompound nbtData, IClimateData data) {
		data.addData(ClimateType.TEMPERATURE, Translator.translateToLocal("for.gui.modifier.altitude"), nbtData.getFloat("altitudeChange"));
	}

}

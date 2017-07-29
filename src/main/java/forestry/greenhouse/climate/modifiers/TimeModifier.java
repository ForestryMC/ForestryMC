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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.BlankClimateModifier;
import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateContainer;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateTable;
import forestry.api.climate.ImmutableClimateState;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;

public class TimeModifier extends BlankClimateModifier {

	private static final float TEMPERATURE_CHANGE = 0.10F;

	public static float calculateLightRatio(World world) {
		int lightValue = EnumSkyBlock.SKY.defaultLightValue - world.getSkylightSubtracted();
		float sunAngle = world.getCelestialAngleRadians(1.0F);

		if (sunAngle < (float) Math.PI) {
			sunAngle -= sunAngle * 0.2F;
		} else {
			sunAngle += (((float) Math.PI * 2F) - sunAngle) * 0.2F;
		}

		lightValue = Math.round(lightValue * MathHelper.cos(sunAngle));

		lightValue = MathHelper.clamp(lightValue, 0, 15);
		return lightValue / 15f;
	}

	@Override
	public IClimateState modifyTarget(IClimateContainer container, IClimateState newState, ImmutableClimateState oldState, NBTTagCompound data) {
		World world = container.getWorld();
		float lightRatio = calculateLightRatio(world);
		float temperatureChange = lightRatio * TEMPERATURE_CHANGE;
		data.setFloat("timeTemperatureChange", temperatureChange);
		return newState.addTemperature(temperatureChange);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addTableEntries(IClimateContainer container, IClimateState climateState, NBTTagCompound data, ClimateType tableType, IClimateTable table) {
		if (tableType == ClimateType.TEMPERATURE) {
			table.addValueEntry(Translator.translateToLocal("for.gui.modifier.time"), StringUtil.floatAsPercent(data.getFloat("timeTemperatureChange")));
		}
	}

}

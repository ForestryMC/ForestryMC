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

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;
import forestry.core.utils.StringUtil;
import forestry.core.translation.Translator;
import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.api.climate.IClimateModifier;

public class TimeModifier implements IClimateModifier {

	private static final float TEMPERATURE_CHANGE = 0.05F;

	private static float calculateLightRatio(World world) {
		int lightValue = EnumSkyBlock.SKY.defaultLightValue - world.getSkylightSubtracted();
		float sunAngle = world.getCelestialAngleRadians(1.0F);

		if (!world.isDaytime()) {
			lightValue = EnumSkyBlock.SKY.defaultLightValue - lightValue;
		}

		if (sunAngle < (float) Math.PI) {
			sunAngle -= sunAngle * 0.2F;
		} else {
			sunAngle += (((float) Math.PI * 2F) - sunAngle) * 0.2F;
		}

		lightValue = Math.round(lightValue * MathHelper.cos(sunAngle));

		lightValue = MathHelper.clamp(lightValue, -15, 15);
		return lightValue / 15f;
	}

	@Override
	public IClimateState modifyTarget(IClimateContainer container, IClimateState newState, IClimateState oldState, NBTTagCompound data) {
		World world = container.getWorld();
		float lightRatio = calculateLightRatio(world);
		float temperatureChange = lightRatio * TEMPERATURE_CHANGE;
		data.setFloat("timeTemperatureChange", temperatureChange);
		return newState.addTemperature(temperatureChange);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(IClimateContainer container, NBTTagCompound nbtData, ClimateType type, List<String> lines) {
		lines.add(Translator.translateToLocalFormatted("for.gui.modifier.time", StringUtil.floatAsPercent(nbtData.getFloat("timeTemperatureChange"))));
	}

	@Override
	public boolean canModify(ClimateType type) {
		return type == ClimateType.TEMPERATURE;
	}

	@Override
	public String getName() {
		return Translator.translateToLocal("for.gui.modifier.time.title");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		return new ItemStack(Items.CLOCK);
	}
}

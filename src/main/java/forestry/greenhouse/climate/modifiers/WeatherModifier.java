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

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;
import forestry.core.render.TextureManagerForestry;
import forestry.core.utils.StringUtil;
import forestry.core.translation.Translator;
import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.api.climate.IClimateModifier;

public class WeatherModifier implements IClimateModifier {

	private static final int RAIN_TIME = 600;
	private static final float RAIN_HUMIDITY_CHANGE = 0.15F;
	private static final float RAIN_TEMPERATURE_CHANGE = -0.10F;
	private static final int SNOW_MODIFIER = 2;

	@Override
	public IClimateState modifyTarget(IClimateContainer container, IClimateState newState, IClimateState oldState, NBTTagCompound data) {
		World world = container.getWorld();
		IClimateState defaultClimate = container.getParent().getDefaultClimate();
		int rainTime = data.getInteger("raintime");
		if (world.isRaining()) {
			if (rainTime < RAIN_TIME) {
				rainTime++;
			}
		} else {
			if (rainTime > 0) {
				rainTime--;
			}
		}
		if (rainTime > 0) {
			float rainModifier = (float) rainTime / (float) RAIN_TIME;
			float temperatureChange = rainModifier * RAIN_TEMPERATURE_CHANGE;
			if (defaultClimate.getTemperature() <= 0.35F) {
				temperatureChange *= SNOW_MODIFIER;
			}
			float humidityChange = rainModifier * RAIN_HUMIDITY_CHANGE;
			data.setFloat("rainTemperatureChange", temperatureChange);
			data.setFloat("rainHumidityChange", humidityChange);
			newState = newState.addHumidity(humidityChange);
			newState = newState.addTemperature(temperatureChange);
		}
		data.setInteger("raintime", rainTime);
		return newState;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(IClimateContainer container, NBTTagCompound nbtData, ClimateType type, List<String> lines) {
		if (type == ClimateType.HUMIDITY) {
			lines.add(Translator.translateToLocalFormatted("for.gui.modifier.rain", StringUtil.floatAsPercent(nbtData.getFloat("rainHumidityChange"))));
		} else {
			lines.add(Translator.translateToLocalFormatted("for.gui.modifier.rain", StringUtil.floatAsPercent(nbtData.getFloat("rainTemperatureChange"))));
		}
	}

	@Override
	public boolean canModify(ClimateType type) {
		return true;
	}

	@Override
	public String getName() {
		return Translator.translateToLocal("for.gui.modifier.weather.title");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getIcon() {
		return TextureManagerForestry.getInstance().getDefault("modifiers/rain");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTextureMap() {
		return TextureManagerForestry.LOCATION_FORESTRY_TEXTURE;
	}

}

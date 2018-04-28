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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;
import forestry.api.greenhouse.IClimateHousing;
import forestry.core.render.TextureManagerForestry;
import forestry.core.utils.StringUtil;
import forestry.api.core.Translator;
import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.api.climate.IClimateModifier;

public class AltitudeModifier implements IClimateModifier {

	private static final float TEMPERATURE_CHANGE = 0.01F;

	@Override
	public IClimateState modifyTarget(IClimateContainer container, IClimateState newState, IClimateState oldState, NBTTagCompound data) {
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
	public void addInformation(IClimateContainer container, NBTTagCompound nbtData, ClimateType type, List<String> lines) {
		lines.add(Translator.translateToLocalFormatted("for.gui.modifier.altitude", StringUtil.floatAsPercent(nbtData.getFloat("altitudeChange"))));
	}

	@Override
	public boolean canModify(ClimateType type) {
		return type == ClimateType.TEMPERATURE;
	}

	@Override
	public String getName() {
		return Translator.translateToLocal("for.gui.modifier.altitude.title");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getIcon() {
		return TextureManagerForestry.getInstance().getDefault("habitats/hills");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTextureMap() {
		return TextureManagerForestry.LOCATION_FORESTRY_TEXTURE;
	}
}

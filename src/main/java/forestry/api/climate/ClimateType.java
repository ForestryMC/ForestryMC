/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import java.util.Locale;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ForestryAPI;

public enum ClimateType {
	TEMPERATURE, HUMIDITY;
	
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getSprite() {
		return ForestryAPI.textureManager.getDefault("misc/" + getName());
	}
	
	public String getName(){
		return name().toLowerCase(Locale.ENGLISH);
	}

}

/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Get the instance from {@link ForestryAPI#textureManager}.
 */
@SideOnly(Side.CLIENT)
public interface ITextureManager {

	/**
	 * Location of the Forestry Gui Texture Map.
	 * Used for binding with {@link TextureManager#bindTexture(ResourceLocation)}
	 */
	ResourceLocation getGuiTextureMap();

	/**
	 * Get a texture atlas sprite that has been registered by Forestry, for Forestry's Gui Texture Map.
	 */
	TextureAtlasSprite getDefault(String ident);

	/**
	 * Register a sprite with Forestry's Gui Texture Map.
	 */
	TextureAtlasSprite registerGuiSprite(ResourceLocation location);
}

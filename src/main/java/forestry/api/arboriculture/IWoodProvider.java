/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ITextureManager;

public interface IWoodProvider {
	@SideOnly(Side.CLIENT)
	void registerSprites(Item item, ITextureManager manager);

	/**
	 * @return The texture sprite of the wood.
	 */
	@SideOnly(Side.CLIENT)
	TextureAtlasSprite getSprite(boolean isTop);

	/**
	 * @return A stack of wood from the tree to craft wood pile's or other things.
	 */
	ItemStack getWoodStack();
	
	/**
	 * The amount of charcoal dropped from a charcoal pile of this wood.
	 * @deprecated Will be removed in 1.13
	 */
	@Deprecated
	int getCarbonization();

	/**
	 * The chance that a pile has to drop a extra charcoal
	 * @deprecated Will be removed in 1.13
	 */
	@Deprecated
	float getCharcoalChance(int numberOfCharcoal);
}

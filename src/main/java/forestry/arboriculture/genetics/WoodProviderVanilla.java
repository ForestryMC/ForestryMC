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
package forestry.arboriculture.genetics;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IWoodProvider;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.core.ITextureManager;

@Deprecated
public class WoodProviderVanilla implements IWoodProvider {

	private final EnumVanillaWoodType woodType;
	private TextureAtlasSprite woodTop;
	private TextureAtlasSprite woodBark;

	public WoodProviderVanilla(EnumVanillaWoodType woodType) {
		this.woodType = woodType;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerSprites(Item item, ITextureManager manager) {
		String name;
		switch (woodType) {
			case OAK:
				name = "oak";
				break;
			case SPRUCE:
				name = "spruce";
				break;
			case BIRCH:
				name = "birch";
				break;
			case JUNGLE:
				name = "jungle";
				break;
			case ACACIA:
				name = "acacia";
				break;
			case DARK_OAK:
				name = "big_oak";
				break;
			default:
				return;
		}

		TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
		woodTop = textureMap.registerSprite(new ResourceLocation("minecraft", "blocks/log_" + name + "_top"));
		woodBark = textureMap.registerSprite(new ResourceLocation("minecraft", "blocks/log_" + name));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getSprite(boolean isTop) {
		if (isTop) {
			return woodTop;
		} else {
			return woodBark;
		}
	}

	@Override
	public ItemStack getWoodStack() {
		return TreeManager.woodAccess.getStack(woodType, WoodBlockKind.LOG, false);
	}

}

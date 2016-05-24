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

import forestry.api.arboriculture.IWoodProvider;
import forestry.api.core.ITextureManager;
import forestry.core.render.TextureManager;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class WoodProviderVanilla implements IWoodProvider {

	private final int vanillaMap;
	private  TextureAtlasSprite woodTop;
	private  TextureAtlasSprite woodBark;

	public WoodProviderVanilla(int vanillaMap) {
		this.vanillaMap = vanillaMap;
	}
	
	@Override
	public void registerSprites(Item item, ITextureManager manager) {
		String name = "";
		switch (vanillaMap) {
		case 0:
			name = "oak";
			break;
		case 1:
			name = "spruce";
			break;
		case 2:
			name = "birch";
			break;
		case 3:
			name = "jungle";
			break;
		case 4:
			name = "acacia";
			break;
		case 5:
			name = "big_oak";
			break;
	}
		woodTop = TextureManager.registerSprite(new ResourceLocation("minecraft:blocks/log_" + name + "_top"));
		woodBark =  TextureManager.registerSprite(new ResourceLocation("minecraft:blocks/log_" + name));
	}

	@Override
	public TextureAtlasSprite getSprite(boolean isTop) {
		if(isTop){
			return woodTop;
		}else{
			return woodBark;
		}
	}
	
	@Override
	public ItemStack getWoodStack() {
		Block block;
		if(vanillaMap < 4){
			block = Blocks.LOG;
		}else{
			block = Blocks.LOG2;
		}
		return new ItemStack(block, 1, vanillaMap >= 4? vanillaMap - 4 : vanillaMap);
	}
	
}

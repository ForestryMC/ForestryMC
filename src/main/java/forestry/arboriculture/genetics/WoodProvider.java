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

import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.IWoodProvider;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.ITextureManager;
import forestry.core.render.ForestryResource;
import forestry.core.render.TextureManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class WoodProvider implements IWoodProvider {

	private final String name;
	private final EnumWoodType woodType;
	private  TextureAtlasSprite woodTop;
	private  TextureAtlasSprite woodBark;

	public WoodProvider(String name, EnumWoodType woodType) {
		this.name = name;
		this.woodType = woodType;
	}
	
	@Override
	public void registerSprites(Item item, ITextureManager manager) {
		woodTop = TextureManager.registerSprite(new ForestryResource("blocks/wood/heart." + name));
		woodBark = TextureManager.registerSprite(new ForestryResource("blocks/wood/bark." + name));
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
		return TreeManager.woodAccess.getLog(woodType, false);
	}

}

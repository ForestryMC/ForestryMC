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
package forestry.arboriculture.models;

import net.minecraft.util.ResourceLocation;

import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IGermlingModelProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.genetics.EnumGermlingType;

public class ModelProviderGermlingVanilla implements IGermlingModelProvider {

	private final EnumVanillaWoodType woodType;
	private final ILeafSpriteProvider leafSpriteProvider;

	public ModelProviderGermlingVanilla(EnumVanillaWoodType woodType, ILeafSpriteProvider leafSpriteProvider) {
		this.woodType = woodType;
		this.leafSpriteProvider = leafSpriteProvider;
	}

	@Override
	public int getSpriteColor(EnumGermlingType type, int renderPass) {
		if (type == EnumGermlingType.POLLEN) {
			return leafSpriteProvider.getColor(false);
		} else {
			return 0xFFFFFF;
		}
	}

	@Override
	public ResourceLocation getItemModel() {
		switch (woodType) {
			case SPRUCE:
				return new ResourceLocation("minecraft", "spruce_sapling");
			case BIRCH:
				return new ResourceLocation("minecraft", "birch_sapling");
			case JUNGLE:
				return new ResourceLocation("minecraft", "jungle_sapling");
			case ACACIA:
				return new ResourceLocation("minecraft", "acacia_sapling");
			case DARK_OAK:
				return new ResourceLocation("minecraft", "dark_oak_sapling");
			default:
				return new ResourceLocation("minecraft", "oak_sapling");
		}
	}

	@Override
	public ResourceLocation getBlockModel() {
		switch (woodType) {
			case SPRUCE:
				return new ResourceLocation("minecraft", "block/spruce_sapling");
			case BIRCH:
				return new ResourceLocation("minecraft", "block/birch_sapling");
			case JUNGLE:
				return new ResourceLocation("minecraft", "block/jungle_sapling");
			case ACACIA:
				return new ResourceLocation("minecraft", "block/acacia_sapling");
			case DARK_OAK:
				return new ResourceLocation("minecraft", "block/dark_oak_sapling");
			default:
				return new ResourceLocation("minecraft", "block/oak_sapling");
		}
	}
}

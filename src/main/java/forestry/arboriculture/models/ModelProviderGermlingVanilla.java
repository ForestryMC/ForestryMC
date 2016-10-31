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

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IGermlingModelProvider;
import forestry.api.core.IModelManager;

public class ModelProviderGermlingVanilla implements IGermlingModelProvider {

	private final EnumVanillaWoodType woodType;

	private ModelResourceLocation germlingModel;
	private ModelResourceLocation pollenModel;
	private ModelResourceLocation charcoalModel;

	public ModelProviderGermlingVanilla(EnumVanillaWoodType woodType) {
		this.woodType = woodType;
	}

	@Override
	public void registerModels(Item item, IModelManager manager, EnumGermlingType type) {
		if(type == EnumGermlingType.SAPLING){
			switch (woodType) {
				case OAK:
					germlingModel = manager.getModelLocation("minecraft", "oak_sapling");
					break;
				case SPRUCE:
					germlingModel = manager.getModelLocation("minecraft", "spruce_sapling");
					break;
				case BIRCH:
					germlingModel = manager.getModelLocation("minecraft", "birch_sapling");
					break;
				case JUNGLE:
					germlingModel = manager.getModelLocation("minecraft", "jungle_sapling");
					break;
				case ACACIA:
					germlingModel = manager.getModelLocation("minecraft", "acacia_sapling");
					break;
				case DARK_OAK:
					germlingModel = manager.getModelLocation("minecraft", "dark_oak_sapling");
					break;
			}
		}else if(type == EnumGermlingType.POLLEN){
			pollenModel = manager.getModelLocation("pollen");
		}
	}

	@Nonnull
	@Override
	public ModelResourceLocation getModel(EnumGermlingType type) {
		if(type == EnumGermlingType.POLLEN){
			return pollenModel;
		}else{
			return germlingModel;
		}
	}

	@Override
	public int getSpriteColor(int renderPass) {
		return 0xFFFFFF;
	}
}

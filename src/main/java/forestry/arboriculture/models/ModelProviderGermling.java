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

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IGermlingModelProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.core.IModelManager;
import forestry.core.utils.StringUtil;

public class ModelProviderGermling implements IGermlingModelProvider {

	private final String name;
	private final ILeafSpriteProvider leafSpriteProvider;
	@SideOnly(Side.CLIENT)
	private ModelResourceLocation germlingModel;
	@SideOnly(Side.CLIENT)
	private ModelResourceLocation pollenModel;

	public ModelProviderGermling(String uid, ILeafSpriteProvider leafSpriteProvider) {
		String modelName = uid.substring("forestry.".length());
		this.name = StringUtil.camelCaseToUnderscores(modelName);
		this.leafSpriteProvider = leafSpriteProvider;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(Item item, IModelManager manager, EnumGermlingType type) {
		if (type == EnumGermlingType.SAPLING) {
			germlingModel = manager.getModelLocation("germlings/sapling." + name);
			ModelBakery.registerItemVariants(item, new ResourceLocation("forestry:germlings/sapling." + name));
		} else if (type == EnumGermlingType.POLLEN) {
			pollenModel = manager.getModelLocation("pollen");
			ModelBakery.registerItemVariants(item, new ResourceLocation("forestry:pollen"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModel(EnumGermlingType type) {
		if (type == EnumGermlingType.POLLEN) {
			return pollenModel;
		} else {
			return germlingModel;
		}
	}

	@Override
	public int getSpriteColor(EnumGermlingType type, int renderPass) {
		if (type == EnumGermlingType.POLLEN) {
			return leafSpriteProvider.getColor(false);
		} else {
			return 0xFFFFFF;
		}
	}
}

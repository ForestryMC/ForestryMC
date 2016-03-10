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
package forestry.arboriculture.render;

import javax.annotation.Nonnull;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IGermlingModelProvider;
import forestry.api.core.IModelManager;

public class ModelProviderGermling implements IGermlingModelProvider {

	private final String name;

	private ModelResourceLocation model;
	private ModelResourceLocation pollenModel;

	public ModelProviderGermling(String uid) {
		this.name = uid.substring("forestry.".length());
	}

	@Override
	public void registerModels(Item item, IModelManager manager) {
		model = manager.getModelLocation("germlings/sapling." + name);
		manager.registerVariant(item, new ResourceLocation("forestry:germlings/sapling." + name));
		pollenModel = manager.getModelLocation("pollen");
		manager.registerVariant(item, new ResourceLocation("forestry:pollen"));
	}

	@Nonnull
	@Override
	public ModelResourceLocation getModel(EnumGermlingType type) {
		if (type == EnumGermlingType.POLLEN) {
			return pollenModel;
		}
		return model;
	}
}

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

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IGermlingModelProvider;
import forestry.api.core.IModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class GermlingVanillaModelProvider implements IGermlingModelProvider {

	private final int vanillaMap;

	private ModelResourceLocation model;
	private ModelResourceLocation pollenModel;

	public GermlingVanillaModelProvider(int vanillaMap) {
		this.vanillaMap = vanillaMap;
	}

	@Override
	public void registerModels(IModelManager manager) {
		switch (vanillaMap) {
		case 0:
			model = manager.getModelLocation("minecraft", "oak_sapling");
			break;
		case 1:
			model = manager.getModelLocation("minecraft", "spruce_sapling");
			break;
		case 2:
			model = manager.getModelLocation("minecraft", "birch_sapling");
			break;
		case 3:
			model = manager.getModelLocation("minecraft", "jungle_sapling");
			break;
		case 4:
			model = manager.getModelLocation("minecraft", "dark_oak_sapling");
			break;
		case 5:
			model = manager.getModelLocation("minecraft", "acacia_sapling");
			break;
		}
		pollenModel = manager.getModelLocation("pollen");
	}

	@Override
	public ModelResourceLocation getModel(EnumGermlingType type) {
		if (type == EnumGermlingType.POLLEN) {
			return pollenModel;
		}
		return model;
	}
}

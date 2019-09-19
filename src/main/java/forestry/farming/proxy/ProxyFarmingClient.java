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
package forestry.farming.proxy;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.models.ClientManager;
import forestry.farming.features.FarmingBlocks;
import forestry.farming.models.ModelFarmBlock;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ProxyFarmingClient extends ProxyFarming {

	@Override
	public void initializeModels() {
		ClientManager.getInstance().registerModel(new ModelFarmBlock(), FarmingBlocks.FARM);
		/*for(FeatureBlock<BlockFarm, BlockItem> feature : FarmingBlocks.FARM.getFeatures()) {
			ModelManager.getInstance().registerModel(new ModelFarmBlock(), feature.block(), feature.item());
			ModelManager.getInstance().registerCustomBlockModel(new BlockModelEntry(new ModelResourceLocation("forestry:ffarm"),
				new ModelResourceLocation("forestry:ffarm", "inventory"), new ModelFarmBlock(),
				block));
		}*/
	}
}

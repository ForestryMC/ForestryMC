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
package forestry.arboriculture.proxy;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.ColorizerFoliage;

import forestry.arboriculture.PluginArboriculture;
import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.render.ModelDecorativeLeaves;
import forestry.arboriculture.render.ModelLeaves;
import forestry.core.models.BlockModelIndex;
import forestry.core.proxy.Proxies;

public class ProxyArboricultureClient extends ProxyArboriculture {
	@Override
	public void initializeModels() {
		{
			ModelResourceLocation blockModelLocation = new ModelResourceLocation("forestry:leaves");
			ModelResourceLocation itemModelLocation = new ModelResourceLocation("forestry:leaves", "inventory");
			BlockModelIndex blockModelIndex = new BlockModelIndex(blockModelLocation, itemModelLocation, new ModelLeaves(), PluginArboriculture.blocks.leaves);
			Proxies.render.registerBlockModel(blockModelIndex);
		}

		for (BlockDecorativeLeaves leaves : PluginArboriculture.blocks.leavesDecorative) {
			String resourceName = "forestry:leaves.decorative." + leaves.getBlockNumber();
			ModelResourceLocation blockModelLocation = new ModelResourceLocation(resourceName);
			ModelResourceLocation itemModeLocation = new ModelResourceLocation(resourceName, "inventory");
			BlockModelIndex blockModelIndex = new BlockModelIndex(blockModelLocation, itemModeLocation, new ModelDecorativeLeaves(), leaves);
			Proxies.render.registerBlockModel(blockModelIndex);
		}
	}

	@Override
	public int getFoliageColorBasic() {
		return ColorizerFoliage.getFoliageColorBasic();
	}

	@Override
	public int getFoliageColorBirch() {
		return ColorizerFoliage.getFoliageColorBirch();
	}

	@Override
	public int getFoliageColorPine() {
		return ColorizerFoliage.getFoliageColorPine();
	}
}

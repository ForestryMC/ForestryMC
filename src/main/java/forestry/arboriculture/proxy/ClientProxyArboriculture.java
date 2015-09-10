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

import forestry.arboriculture.render.LeavesRenderingHandler;
import forestry.arboriculture.render.SaplingRenderHandler;
import forestry.core.config.ForestryBlock;
import forestry.core.proxy.Proxies;
import forestry.core.render.BlockModelIndex;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.ColorizerFoliage;

public class ClientProxyArboriculture extends ProxyArboriculture {
	
	@Override
	public void initializeRendering() {
		Proxies.render.registerBlockModel(new BlockModelIndex(new ModelResourceLocation("forestry:leaves"), new ModelResourceLocation("forestry:leaves"), new LeavesRenderingHandler(), ForestryBlock.leaves.block()));
		Proxies.render.registerBlockModel(new BlockModelIndex(new ModelResourceLocation("forestry:saplings"), new ModelResourceLocation("forestry:saplings"), new SaplingRenderHandler(), ForestryBlock.saplingGE.block()));
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

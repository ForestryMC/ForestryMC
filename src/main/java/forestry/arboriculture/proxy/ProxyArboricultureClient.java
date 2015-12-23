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
import forestry.arboriculture.render.LeavesRenderingHandler;
import forestry.core.proxy.Proxies;
import forestry.core.render.model.BlockModelIndex;
import forestry.plugins.PluginArboriculture;

public class ProxyArboricultureClient extends ProxyArboriculture {
	@Override
	public void initializeRendering() {
		Proxies.render.registerBlockModel(new BlockModelIndex(new ModelResourceLocation("forestry:leaves"),
				new ModelResourceLocation("forestry:leaves", "inventory"), new LeavesRenderingHandler(),
				PluginArboriculture.blocks.leaves));
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

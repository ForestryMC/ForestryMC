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

import net.minecraft.world.ColorizerFoliage;

public class ClientProxyArboriculture extends ProxyArboriculture {
	@Override
	public void initializeRendering() {
		//PluginArboriculture.modelIdSaplings = RenderingRegistry.getNextAvailableRenderId();
		//PluginArboriculture.modelIdLeaves = RenderingRegistry.getNextAvailableRenderId();
		//PluginArboriculture.modelIdFences = RenderingRegistry.getNextAvailableRenderId();
		//PluginArboriculture.modelIdPods = RenderingRegistry.getNextAvailableRenderId();

		//RenderingRegistry.registerBlockHandler(new SaplingRenderHandler());
		//RenderingRegistry.registerBlockHandler(new LeavesRenderingHandler());

		//MinecraftForgeClient.registerItemRenderer(ForestryBlock.leaves.item(), new LeavesRenderingHandler());
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

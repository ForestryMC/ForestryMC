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

import forestry.core.proxy.Proxies;
import forestry.core.render.model.BlockModelIndex;
import forestry.farming.render.FarmBlockRenderer;
import forestry.plugins.PluginFarming;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class ProxyFarmingClient extends ProxyFarming {

	@Override
	public void initializeRendering() {
		Proxies.render.registerBlockModel(new BlockModelIndex(new ModelResourceLocation("forestry:ffarm"),
				new ModelResourceLocation("forestry:ffarm", "inventory"), new FarmBlockRenderer(),
				PluginFarming.blocks.farm));
	}
}

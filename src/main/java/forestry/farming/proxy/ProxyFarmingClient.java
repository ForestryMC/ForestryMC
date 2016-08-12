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

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import forestry.core.models.BlockModelEntry;
import forestry.core.proxy.Proxies;
import forestry.farming.PluginFarming;
import forestry.farming.models.ModelFarmBlock;

public class ProxyFarmingClient extends ProxyFarming {

	@Override
	public void initializeModels() {
		Proxies.render.registerBlockModel(new BlockModelEntry(new ModelResourceLocation("forestry:ffarm"),
				new ModelResourceLocation("forestry:ffarm", "inventory"), new ModelFarmBlock(),
				PluginFarming.blocks.farm));
	}
}

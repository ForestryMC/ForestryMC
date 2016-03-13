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
package forestry.greenhouse.proxy;

import net.minecraft.block.Block;
import net.minecraft.client.resources.model.ModelResourceLocation;
import forestry.core.models.BlockModelIndex;
import forestry.core.proxy.Proxies;
import forestry.greenhouse.PluginGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.models.ModelGreenhouse;

public class ProxyGreenhouseClient extends ProxyGreenhouse {

	@Override
	public void initializeModels() {
		for(BlockGreenhouseType greenhouseType : BlockGreenhouseType.VALUES){
			if(greenhouseType == BlockGreenhouseType.DOOR) {
				return;
			}
			Proxies.render.registerBlockModel(new BlockModelIndex(new ModelResourceLocation("forestry:greenhouse." + greenhouseType),
				new ModelResourceLocation("forestry:greenhouse", "inventory"), new ModelGreenhouse(),
				Block.getBlockFromItem(PluginGreenhouse.blocks.getGreenhouseBlock(greenhouseType).getItem())));
		}
	}
}

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
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.minecraftforge.client.model.animation.AnimationTESR;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import forestry.core.models.BlockModelEntry;
import forestry.core.proxy.Proxies;
import forestry.greenhouse.PluginGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.models.ModelGreenhouse;
import forestry.greenhouse.tiles.TileGreenhouseSprinkler;

public class ProxyGreenhouseClient extends ProxyGreenhouse {

	@Override
	public void initializeModels() {
		for (BlockGreenhouseType greenhouseType : BlockGreenhouseType.VALUES) {
			if (greenhouseType == BlockGreenhouseType.DOOR) {
				continue;
			} else if (greenhouseType == BlockGreenhouseType.SPRINKLER) {
				ClientRegistry.bindTileEntitySpecialRenderer(TileGreenhouseSprinkler.class, new AnimationTESR<>());
				continue;
			}

			Block greenhouseBlock = PluginGreenhouse.blocks.getGreenhouseBlock(greenhouseType);
			if (greenhouseBlock != null) {
				ModelResourceLocation blockModelLocation = new ModelResourceLocation("forestry:greenhouse." + greenhouseType);
				ModelResourceLocation itemModelLocation = new ModelResourceLocation("forestry:greenhouse", "inventory");
				BlockModelEntry blockModelIndex = new BlockModelEntry(blockModelLocation, itemModelLocation, new ModelGreenhouse(), greenhouseBlock);
				Proxies.render.registerBlockModel(blockModelIndex);
			}
		}
	}
}

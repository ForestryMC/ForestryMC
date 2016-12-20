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

import com.google.common.base.Preconditions;
import forestry.api.core.ForestryAPI;
import forestry.core.models.BlockModelEntry;
import forestry.core.proxy.Proxies;
import forestry.greenhouse.PluginGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.blocks.BlockRegistryGreenhouse;
import forestry.greenhouse.models.ModelGreenhouse;
import forestry.greenhouse.tiles.TileGreenhouseSprinkler;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.animation.AnimationTESR;
import net.minecraftforge.fml.client.registry.ClientRegistry;

@SuppressWarnings("unused")
public class ProxyGreenhouseClient extends ProxyGreenhouse {

	@Override
	public void initializeModels() {
		BlockRegistryGreenhouse blocks = PluginGreenhouse.blocks;
		Preconditions.checkState(blocks != null);

		for (BlockGreenhouseType greenhouseType : BlockGreenhouseType.VALUES) {
			Block greenhouseBlock = blocks.getGreenhouseBlock(greenhouseType);

			if (greenhouseType == BlockGreenhouseType.DOOR) {
				Proxies.render.registerBlockModel(new BlockModelEntry(new ModelResourceLocation("forestry:greenhouse." + greenhouseType, "camouflage"),
						null, new ModelGreenhouse(),
						greenhouseBlock, false));
				continue;
			} else if (greenhouseType == BlockGreenhouseType.SPRINKLER) {
				ClientRegistry.bindTileEntitySpecialRenderer(TileGreenhouseSprinkler.class, new AnimationTESR<>());
				continue;
			} else if (greenhouseType == BlockGreenhouseType.BUTTERFLY_HATCH) {
				if (!ForestryAPI.enabledPlugins.contains(ForestryPluginUids.LEPIDOPTEROLOGY)) {
					continue;
				}
			} else if (greenhouseType == BlockGreenhouseType.WINDOW || greenhouseType == BlockGreenhouseType.WINDOW_UP) {
				continue;
			}

			if (greenhouseBlock != null) {
				ModelResourceLocation blockModelLocation = new ModelResourceLocation("forestry:greenhouse." + greenhouseType);
				ModelResourceLocation itemModelLocation = new ModelResourceLocation("forestry:greenhouse", "inventory");
				BlockModelEntry blockModelIndex = new BlockModelEntry(blockModelLocation, itemModelLocation, new ModelGreenhouse(), greenhouseBlock);
				Proxies.render.registerBlockModel(blockModelIndex);
			}
		}
	}
}

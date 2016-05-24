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

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.world.ColorizerFoliage;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import forestry.api.arboriculture.EnumPileType;
import forestry.arboriculture.PluginArboriculture;
import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.models.ModelDecorativeLeaves;
import forestry.arboriculture.models.ModelLeaves;
import forestry.arboriculture.models.ModelWoodPile;
import forestry.arboriculture.render.CharcoalPileRenderer;
import forestry.arboriculture.tiles.TilePile;
import forestry.core.models.BlockModelIndex;
import forestry.core.proxy.Proxies;

@SuppressWarnings("unused")
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
		
		{
			ModelResourceLocation blockModelLocation = new ModelResourceLocation("forestry:pile", "type=wood");
			ModelResourceLocation itemModelLocation = new ModelResourceLocation("forestry:woodPile", "inventory");
			BlockModelIndex blockModelIndex = new BlockModelIndex(blockModelLocation, itemModelLocation, new ModelWoodPile(), PluginArboriculture.blocks.piles.get(EnumPileType.WOOD));
			Proxies.render.registerBlockModel(blockModelIndex);
		}
		ClientRegistry.bindTileEntitySpecialRenderer(TilePile.class, new CharcoalPileRenderer());
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

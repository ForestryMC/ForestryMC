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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.arboriculture.PluginArboriculture;
import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.blocks.property.PropertyTreeType;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.models.ModelDecorativeLeaves;
import forestry.arboriculture.models.ModelLeaves;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.models.BlockModelIndex;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileUtil;

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

		Minecraft minecraft = Minecraft.getMinecraft();
		BlockColors blockColors = minecraft.getBlockColors();

		blockColors.registerBlockColorHandler(new LeavesBlockColor(), PluginArboriculture.blocks.leaves);

		for (BlockDecorativeLeaves leaves : PluginArboriculture.blocks.leavesDecorative) {
			blockColors.registerBlockColorHandler(new DecorativeLeavesBlockColor(leaves.getVariant()), leaves);
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

	private static class LeavesBlockColor implements IBlockColor {

		@Override
		public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
			TileLeaves leaves = TileUtil.getTile(worldIn, pos, TileLeaves.class);
			if (leaves == null) {
				return PluginArboriculture.proxy.getFoliageColorBasic();
			}

			if (tintIndex == 0) {
				EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
				return leaves.getFoliageColour(thePlayer);
			} else {
				return leaves.getFruitColour();
			}
		}
	}

	private static class DecorativeLeavesBlockColor implements IBlockColor {
		private final PropertyTreeType variant;

		public DecorativeLeavesBlockColor(PropertyTreeType variant) {
			this.variant = variant;
		}

		@Override
		public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
			TreeDefinition treeDefinition = state.getValue(variant);
			if (treeDefinition == null) {
				return PluginArboriculture.proxy.getFoliageColorBasic();
			}

			ITreeGenome genome = treeDefinition.getGenome();
			if (tintIndex == 0) {
				return genome.getPrimary().getLeafSpriteProvider().getColor(false);
			} else {
				IFruitProvider fruitProvider = genome.getFruitProvider();
				return fruitProvider.getDecorativeColor();
			}
		}
	}
}

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
package forestry.apiculture.proxy;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.apiculture.PluginApiculture;
import forestry.apiculture.genetics.BeeGenome;
import forestry.apiculture.tiles.TileCandle;
import forestry.core.tiles.TileUtil;

public class ProxyApicultureClient extends ProxyApiculture {

	@Override
	public void initializeRendering() {
		/*if (PluginApiculture.fancyRenderedBees) {
			RenderingRegistry.registerEntityRenderingHandler(EntityBee.class, new RenderBeeEntity());

			MinecraftForgeClient.registerItemRenderer(PluginApiculture.items.beeDroneGE, new RenderBeeItem());
			MinecraftForgeClient.registerItemRenderer(PluginApiculture.items.beePrincessGE, new RenderBeeItem());
			MinecraftForgeClient.registerItemRenderer(PluginApiculture.items.beeQueenGE, new RenderBeeItem());
		}*/

		Minecraft minecraft = Minecraft.getMinecraft();
		ItemColors itemColors = minecraft.getItemColors();
		itemColors.registerItemColorHandler(new BeeItemColor(),
				PluginApiculture.items.beeQueenGE,
				PluginApiculture.items.beeDroneGE,
				PluginApiculture.items.beeLarvaeGE,
				PluginApiculture.items.beePrincessGE
		);

		BlockColors blockColors = minecraft.getBlockColors();
		blockColors.registerBlockColorHandler(new CandleBlockColor(),
				PluginApiculture.blocks.candle,
				PluginApiculture.blocks.stump
		);
	}

	private static class BeeItemColor implements IItemColor {
		@Override
		public int getColorFromItemstack(ItemStack itemstack, int tintIndex) {
			if (!itemstack.hasTagCompound()) {
				if (tintIndex == 1) {
					return 0xffdc16;
				} else {
					return 0xffffff;
				}
			}

			IAlleleBeeSpecies species = BeeGenome.getSpecies(itemstack);
			if (species instanceof IAlleleBeeSpecies) {
				return species.getSpriteColour(tintIndex);
			} else {
				return 0xffffff;
			}
		}
	}

	private static class CandleBlockColor implements IBlockColor {
		@Override
		public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
			TileCandle tileCandle = TileUtil.getTile(worldIn, pos, TileCandle.class);
			if (tileCandle != null) {
				return tileCandle.getColour();
			}
			return 0xffffff;
		}
	}
}

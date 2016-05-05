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
package forestry.arboriculture.items;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.arboriculture.PluginArboriculture;
import forestry.arboriculture.blocks.BlockForestryLeaves;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.items.ItemBlockForestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Translator;

public class ItemBlockLeaves extends ItemBlockForestry<BlockForestryLeaves> implements IItemColor {

	public ItemBlockLeaves(Block block) {
		super(block);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		if (!itemstack.hasTagCompound()) {
			return Translator.translateToLocal("trees.grammar.leaves.type");
		}

		TileLeaves tileLeaves = new TileLeaves();
		tileLeaves.readFromNBT(itemstack.getTagCompound());

		String unlocalizedName = tileLeaves.getUnlocalizedName();
		return getDisplayName(unlocalizedName);
	}

	public static String getDisplayName(String unlocalizedSpeciesName) {
		String customTreeKey = "for.trees.custom.leaves." + unlocalizedSpeciesName.replace("for.trees.species.", "");
		if (Translator.canTranslateToLocal(customTreeKey)) {
			return Translator.translateToLocal(customTreeKey);
		}

		String grammar = Translator.translateToLocal("for.trees.grammar.leaves");
		String localizedName = Translator.translateToLocal(unlocalizedSpeciesName);

		String leaves = Translator.translateToLocal("for.trees.grammar.leaves.type");
		return grammar.replaceAll("%SPECIES", localizedName).replaceAll("%TYPE", leaves);
	}

	@Override
	public int getColorFromItemstack(ItemStack itemStack, int renderPass) {
		if (!itemStack.hasTagCompound()) {
			return PluginArboriculture.proxy.getFoliageColorBasic();
		}

		TileLeaves tileLeaves = new TileLeaves();
		tileLeaves.readFromNBT(itemStack.getTagCompound());
		
		if (renderPass == 0) {
			EntityPlayer player = Proxies.common.getPlayer();
			return tileLeaves.getFoliageColour(player);
		} else {
			return tileLeaves.getFruitColour();
		}
	}
	
	@Override
	public boolean placeBlockAt(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		return false;
	}

}

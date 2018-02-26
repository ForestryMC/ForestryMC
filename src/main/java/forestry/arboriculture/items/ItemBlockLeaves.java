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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.blocks.BlockAbstractLeaves;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.items.IColoredItem;
import forestry.core.items.ItemBlockForestry;
import forestry.core.utils.Translator;

public class ItemBlockLeaves extends ItemBlockForestry<BlockAbstractLeaves> implements IColoredItem {

	public ItemBlockLeaves(BlockAbstractLeaves block) {
		super(block);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		if (itemstack.getTagCompound() == null) {
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
	@SideOnly(Side.CLIENT)
	public int getColorFromItemstack(ItemStack itemStack, int renderPass) {
		if (itemStack.getTagCompound() == null) {
			return ModuleArboriculture.proxy.getFoliageColorBasic();
		}

		TileLeaves tileLeaves = new TileLeaves();
		tileLeaves.readFromNBT(itemStack.getTagCompound());

		if (renderPass == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
			return tileLeaves.getFruitColour();
		} else {
			EntityPlayer player = Minecraft.getMinecraft().player;
			return tileLeaves.getFoliageColour(player);
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		return false;
	}

}

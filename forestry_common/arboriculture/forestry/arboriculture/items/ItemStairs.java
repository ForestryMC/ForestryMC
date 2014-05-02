/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import forestry.arboriculture.WoodType;
import forestry.arboriculture.gadgets.TileStairs;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.items.ItemForestryBlock;
import forestry.core.utils.StringUtil;

public class ItemStairs extends ItemForestryBlock {

	public ItemStairs(Block block) {
		super(block);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,
			int metadata) {

		WoodType type = WoodType.getFromCompound(stack.getTagCompound());
		return placeStairs(world, player, stack, type, x, y, z, metadata);

	}

	private boolean placeStairs(World world, EntityPlayer player, ItemStack stack, WoodType type, int x, int y, int z, int metadata) {

		boolean placed = world.setBlock(x, y, z, ForestryBlock.stairs, metadata, Defaults.FLAG_BLOCK_SYNCH);
		if (!placed)
			return false;

		Block block = world.getBlock(x, y, z);

		if (block != ForestryBlock.stairs)
			return false;

		block.onBlockPlacedBy(world, x, y, z, player, stack);
		block.onPostBlockPlaced(world, x, y, z, metadata);

		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileStairs)) {
			world.setBlockToAir(x, y, z);
			return false;
		}

		((TileStairs) tile).setType(type);
		return true;
	}

	private String getWoodNameIS(WoodType type) {
		return StringUtil.localize("wood." + type.ordinal());
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {

		WoodType type = WoodType.getFromCompound(itemstack.getTagCompound());

		return getWoodNameIS(type) + " " + StringUtil.localize(getUnlocalizedName());
	}
}

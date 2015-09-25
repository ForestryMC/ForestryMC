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

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodType;
import forestry.arboriculture.tiles.TileWood;
import forestry.core.config.Constants;
import forestry.core.items.ItemBlockForestry;
import forestry.core.utils.StringUtil;

public class ItemBlockWood extends ItemBlockForestry {

	public ItemBlockWood(Block block) {
		super(block);
	}

	public static boolean placeWood(ItemStack stack, @Nullable EntityPlayer player, World world, int x, int y, int z, int metadata) {
		WoodType woodType = WoodType.getFromCompound(stack.getTagCompound());
		Block block = Block.getBlockFromItem(stack.getItem());

		boolean placed = world.setBlock(x, y, z, block, metadata, Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
		if (!placed) {
			return false;
		}

		Block worldBlock = world.getBlock(x, y, z);
		if (!Block.isEqualTo(block, worldBlock)) {
			return false;
		}

		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileWood)) {
			world.setBlockToAir(x, y, z);
			return false;
		}

		if (player != null) {
			worldBlock.onBlockPlacedBy(world, x, y, z, player, stack);
			worldBlock.onPostBlockPlaced(world, x, y, z, metadata);
		}

		((TileWood) tile).setWoodType(woodType);
		return true;
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		return placeWood(stack, player, world, x, y, z, metadata);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		Block block = getBlock();
		if (!(block instanceof IWoodTyped)) {
			return super.getItemStackDisplayName(itemstack);
		}

		WoodType woodType = getWood(itemstack);
		if (woodType == null) {
			return super.getItemStackDisplayName(itemstack);
		}

		IWoodTyped wood = (IWoodTyped) block;
		String blockKind = wood.getBlockKind();

		String displayName;
		String customUnlocalizedName = blockKind + "." + woodType.ordinal() + ".name";
		if (StringUtil.canTranslateTile(customUnlocalizedName)) {
			displayName = StringUtil.localizeTile(customUnlocalizedName);
		} else {
			String woodGrammar = StringUtil.localize(blockKind + ".grammar");
			String woodTypeName = StringUtil.localize("trees.woodType." + woodType);

			displayName = woodGrammar.replaceAll("%TYPE", woodTypeName);
		}

		if (wood.isFireproof()) {
			displayName = StringUtil.localizeAndFormatRaw("tile.for.fireproof", displayName);
		}

		return displayName;
	}

	private static WoodType getWood(ItemStack itemStack) {
		return WoodType.getFromCompound(itemStack.getTagCompound());
	}
}

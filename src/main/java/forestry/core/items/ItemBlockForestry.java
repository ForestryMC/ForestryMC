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
package forestry.core.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;

public class ItemBlockForestry extends ItemBlock {

	public ItemBlockForestry(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	protected Block getBlock() {
		return field_150939_a;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int meta) {
		return this.getBlock().getIcon(1, meta);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return getBlock().getUnlocalizedName() + "." + itemstack.getItemDamage();
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		boolean placed = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);

		TileForestry tile = TileUtil.getTile(world, x, y, z, TileForestry.class);

		if (tile != null) {
			if (stack.getItem() instanceof ItemBlockNBT && stack.hasTagCompound()) {
				tile.readFromNBT(stack.getTagCompound());
				tile.xCoord = x;
				tile.yCoord = y;
				tile.zCoord = z;
			}

			tile.rotateAfterPlacement(player, side);
		}

		return placed;
	}
}

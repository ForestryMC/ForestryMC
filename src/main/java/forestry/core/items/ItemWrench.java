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
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//@Optional.Interface(iface = "buildcraft.api.tools.IToolWrench", modid = "BuildCraftAPI|tools")
public class ItemWrench extends ItemForestry {//implements IToolWrench {

	public ItemWrench() {
		setHarvestLevel("wrench", 0);
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		Block block = world.getBlockState(pos).getBlock();
		if (block.rotateBlock(world, pos, side)) {
			player.swingArm(hand);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	/*
	@Override
	public boolean canWrench(EntityPlayer player, BlockPos pos) {
		return true;
	}

	@Override
	public void wrenchUsed(EntityPlayer player, BlockPos pos) {
	}

	@Override
	public boolean canWrench(EntityPlayer entityPlayer, Entity entity) {
		return true;
	}

	@Override
	public void wrenchUsed(EntityPlayer entityPlayer, Entity entity) {
	}
	*/
}

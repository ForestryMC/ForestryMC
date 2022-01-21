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

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.ToolType;

import forestry.core.ItemGroupForestry;

public class ItemWrench extends ItemForestry {

	public ItemWrench() {
		super((new Item.Properties())
				.addToolType(ToolType.get("wrench"), 0).tab(ItemGroupForestry.tabForestry));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level worldIn = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.FAIL;
		}
		Direction facing = context.getClickedFace();
		InteractionHand hand = context.getHand();

		BlockState state = worldIn.getBlockState(pos);
		Block block = state.getBlock();
		BlockState rotatedState = block.rotate(state, worldIn, pos, Rotation.CLOCKWISE_90);
		if (rotatedState != state) {    //TODO - how to rotate based on a direction, might need helper method
			player.swing(hand);
			worldIn.setBlock(pos, rotatedState, 2);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	//	@Override
	//	public boolean canWrench(PlayerEntity player, Hand hand, ItemStack wrench, RayTraceResult rayTrace) {
	//		return true;
	//	}
	//
	//	@Override
	//	public void wrenchUsed(PlayerEntity player, Hand hand, ItemStack wrench, RayTraceResult rayTrace) {
	//	}
}

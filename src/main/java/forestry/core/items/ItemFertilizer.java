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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemFertilizer extends ItemForestry {

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		World worldIn = context.getWorld();
		Hand hand = context.getHand();
		BlockPos pos = context.getPos();
		Direction facing = context.getFace();
		ItemStack heldItem = player.getHeldItem(hand);
		if (!player.canPlayerEdit(pos.offset(facing), facing, heldItem)) {
			return ActionResultType.FAIL;
		}

		if (BoneMealItem.applyBonemeal(heldItem, worldIn, pos, player)) {
			if (!worldIn.isRemote) {
				worldIn.playEvent(2005, pos, 0);
			}

			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}
}

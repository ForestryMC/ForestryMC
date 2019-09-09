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

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import forestry.core.ItemGroupForestry;

public class ItemAssemblyKit extends ItemForestry {
	private final ItemStack assembled;

	public ItemAssemblyKit(ItemStack assembled) {
		super((new Item.Properties())
			.maxStackSize(24)
			.group(ItemGroupForestry.tabForestry));
		this.assembled = assembled;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack heldItem = playerIn.getHeldItem(handIn);
		if (!worldIn.isRemote) {
			heldItem.shrink(1);
			ItemEntity entity = new ItemEntity(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, assembled.copy());
			worldIn.addEntity(entity);
		}
		return ActionResult.newResult(ActionResultType.SUCCESS, heldItem);
	}
}

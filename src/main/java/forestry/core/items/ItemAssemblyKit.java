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

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

import forestry.core.ItemGroupForestry;

public class ItemAssemblyKit extends ItemForestry {
	private final ItemStack assembled;

	public ItemAssemblyKit(ItemStack assembled) {
		super((new Item.Properties())
				.stacksTo(24)
				.tab(ItemGroupForestry.tabForestry));
		this.assembled = assembled;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack heldItem = playerIn.getItemInHand(handIn);
		if (!worldIn.isClientSide) {
			heldItem.shrink(1);
			ItemEntity entity = new ItemEntity(worldIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), assembled.copy());
			worldIn.addFreshEntity(entity);
		}
		return InteractionResultHolder.success(heldItem);
	}
}

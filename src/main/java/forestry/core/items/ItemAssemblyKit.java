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

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemAssemblyKit extends ItemForestry {
	private final ItemStack assembled;

	public ItemAssemblyKit(ItemStack assembled) {
		maxStackSize = 24;
		this.assembled = assembled;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack heldItem = playerIn.getHeldItem(handIn);
		if (!worldIn.isRemote) {
			heldItem.shrink(1);
			EntityItem entity = new EntityItem(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, assembled.copy());
			worldIn.spawnEntity(entity);
		}
		return ActionResult.newResult(EnumActionResult.SUCCESS, heldItem);
	}
}

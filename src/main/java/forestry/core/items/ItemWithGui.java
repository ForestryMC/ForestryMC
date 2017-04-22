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

import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.GuiHandler;
import forestry.core.gui.IGuiHandlerItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public abstract class ItemWithGui extends ItemForestry implements IGuiHandlerItem {
	public ItemWithGui() {
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (!worldIn.isRemote) {
			openGui(playerIn);
		}

		ItemStack stack = playerIn.getHeldItem(handIn);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	protected void openGui(EntityPlayer entityplayer) {
		GuiHandler.openGui(entityplayer, this);
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack itemstack, EntityPlayer player) {
		if (itemstack != null &&
				player instanceof EntityPlayerMP &&
				player.openContainer instanceof ContainerItemInventory) {
			player.closeScreen();
		}

		return super.onDroppedByPlayer(itemstack, player);
	}
}

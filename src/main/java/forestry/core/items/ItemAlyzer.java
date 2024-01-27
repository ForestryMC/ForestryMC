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

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import forestry.api.core.ItemGroups;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.inventory.ItemInventoryAlyzer;


public class ItemAlyzer extends ItemWithGui {
	public ItemAlyzer() {
		super((new Item.Properties())
				.tab(ItemGroups.tabApiculture)
				.stacksTo(1));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag advanced) {
		super.appendHoverText(stack, world, tooltip, advanced);
		int charges = 0;
		CompoundTag compound = stack.getTag();
		if (compound != null) {
			charges = compound.getInt("Charges");
		}
		tooltip.add(Component.translatable(stack.getDescriptionId() + ".charges", charges).withStyle(ChatFormatting.GOLD));
	}

	@Override
	public AbstractContainerMenu getContainer(int windowId, Player player, ItemStack heldItem) {
		return new ContainerAlyzer(windowId, new ItemInventoryAlyzer(player, heldItem), player);
	}
}

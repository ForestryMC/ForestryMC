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

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.fml.network.NetworkHooks;

import forestry.api.core.ItemGroups;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.inventory.ItemInventoryAlyzer;


public class ItemAlyzer extends ItemWithGui {
	public ItemAlyzer() {
		super((new Item.Properties())
			.group(ItemGroups.tabApiculture)
			.maxStackSize(1));
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
		super.addInformation(stack, world, tooltip, advanced);
		int charges = 0;
		CompoundNBT compound = stack.getTag();
		if (compound != null) {
			charges = compound.getInt("Charges");
		}
		tooltip.add(new TranslationTextComponent(stack.getTranslationKey() + ".charges", charges).setStyle((new Style()).setColor(TextFormatting.GOLD)));
	}

	@Override
	public void openGui(ServerPlayerEntity player, ItemStack stack) {
		NetworkHooks.openGui(player, new ContainerProvider(stack), p -> p.writeBoolean(player.getActiveHand() == Hand.MAIN_HAND));
	}

	@Override
	public Container getContainer(int windowId, PlayerEntity player, ItemStack heldItem) {
		return new ContainerAlyzer(windowId, new ItemInventoryAlyzer(player, heldItem), player);
	}

	//TODO see if this can be deduped. Given we pass in the held item etc.
	public static class ContainerProvider implements INamedContainerProvider {

		private ItemStack heldItem;

		public ContainerProvider(ItemStack heldItem) {
			this.heldItem = heldItem;
		}

		@Override
		public ITextComponent getDisplayName() {
			return new StringTextComponent("ITEM_GUI_TITLE");    //TODO needs to be overriden individually
		}

		@Nullable
		@Override
		public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
			return new ContainerAlyzer(windowId, new ItemInventoryAlyzer(playerEntity, heldItem), playerEntity);
		}
	}
}

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
package forestry.mail.items;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.IItemSubtype;
import forestry.api.mail.ILetter;
import forestry.core.ItemGroupForestry;
import forestry.core.items.ItemWithGui;
import forestry.mail.Letter;
import forestry.mail.gui.ContainerLetter;
import forestry.mail.inventory.ItemInventoryLetter;

public class ItemLetter extends ItemWithGui {

	public enum State implements IItemSubtype {
		FRESH, STAMPED, OPENED, EMPTIED;

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}

	public enum Size implements IItemSubtype {
		EMPTY, SMALL, BIG;

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}

	private Size size;
	private State state;

	public ItemLetter(Size size, State state) {
		super((new Item.Properties())
				.tab(ItemGroupForestry.tabForestry)
				.stacksTo(64));
		this.size = size;
		this.state = state;
	}

	public Size getSize() {
		return size;
	}

	public State getState() {
		return state;
	}

	@Override
	public String getDescriptionId() {
		return "item.forestry.letter";
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack heldItem = playerIn.getItemInHand(handIn);
		if (heldItem.getCount() == 1) {
			return super.use(worldIn, playerIn, handIn);
		} else {
			playerIn.sendMessage(new TranslatableComponent("for.chat.mail.wrongstacksize"), Util.NIL_UUID);
			return InteractionResultHolder.fail(heldItem);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack itemstack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);

		CompoundTag compoundNBT = itemstack.getTag();
		if (compoundNBT == null) {
			list.add(new TextComponent("<")
					.append(new TranslatableComponent("for.gui.blank").append(">"))
					.withStyle(ChatFormatting.GRAY));
			return;
		}

		ILetter letter = new Letter(compoundNBT);
		letter.addTooltip(list);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> stacks) {
		if (allowdedIn(group) && state == State.FRESH && size == Size.EMPTY) {
			stacks.add(new ItemStack(this));
		}
	}

	@Nullable
	@Override
	public AbstractContainerMenu getContainer(int windowId, Player player, ItemStack heldItem) {
		return new ContainerLetter(windowId, player, new ItemInventoryLetter(player, heldItem));
	}
}

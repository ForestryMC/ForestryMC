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
package forestry.storage.items;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.IItemHandler;

import forestry.api.core.ItemGroups;
import forestry.api.storage.BackpackStowEvent;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.inventory.ItemHandlerInventoryManipulator;
import forestry.core.inventory.ItemInventory;
import forestry.core.inventory.StandardStackFilters;
import forestry.core.items.ItemWithGui;
import forestry.core.items.definitions.IColoredItem;
import forestry.core.network.PacketBufferForestry;
import forestry.core.tiles.TileUtil;
import forestry.storage.BackpackMode;
import forestry.storage.gui.ContainerBackpack;
import forestry.storage.inventory.ItemInventoryBackpack;

public class ItemBackpack extends ItemWithGui implements IColoredItem {
	private final IBackpackDefinition definition;
	private final EnumBackpackType type;

	public ItemBackpack(IBackpackDefinition definition, EnumBackpackType type) {
		this(definition, type, ItemGroups.tabStorage);
	}

	public ItemBackpack(IBackpackDefinition definition, EnumBackpackType type, CreativeModeTab tab) {
		super((new Item.Properties()).tab(tab));
		Preconditions.checkNotNull(definition, "Backpack must have a backpack definition.");
		Preconditions.checkNotNull(type, "Backpack must have a backpack type.");

		this.definition = definition;
		this.type = type;
	}

	public IBackpackDefinition getDefinition() {
		return definition;
	}

	@Override
	protected void writeContainerData(ServerPlayer player, ItemStack stack, PacketBufferForestry buffer) {
		buffer.writeEnum(type == EnumBackpackType.WOVEN ? ContainerBackpack.Size.T2 : ContainerBackpack.Size.DEFAULT, ContainerBackpack.Size.values());
		buffer.writeItem(stack);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		if (!playerIn.isShiftKeyDown()) {
			return super.use(worldIn, playerIn, handIn);
		} else {
			ItemStack heldItem = playerIn.getItemInHand(handIn);
			switchMode(heldItem);
			return InteractionResultHolder.success(heldItem);
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		if (getInventoryHit(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace()) != null) {
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		Player player = context.getPlayer();
		// We only do this when shift is clicked
		if (player.isShiftKeyDown()) {
			ItemStack heldItem = player.getItemInHand(context.getHand());
			return evaluateTileHit(heldItem, player, context.getLevel(), context.getClickedPos(), context.getClickedFace()) ? InteractionResult.PASS : InteractionResult.FAIL;
		}
		return super.onItemUseFirst(stack, context);
	}

	public static void tryStowing(Player player, ItemStack backpackStack, ItemStack stack) {
		if (getMode(backpackStack) == BackpackMode.LOCKED) {
			return;
		}

		ItemBackpack backpack = (ItemBackpack) backpackStack.getItem();
		ItemInventory inventory = new ItemInventoryBackpack(player, backpack.getBackpackSize(), backpackStack);

		if (MinecraftForge.EVENT_BUS.post(new BackpackStowEvent(player, backpack.getDefinition(), inventory, stack))) {
			return;
		}
		if (stack.isEmpty()) {
			return;
		}

		IItemHandler itemHandler = inventory.getItemHandler();
		ItemHandlerInventoryManipulator manipulator = new ItemHandlerInventoryManipulator(itemHandler);
		ItemStack remainder = manipulator.addStack(stack);

		stack.setCount(remainder == null ? 0 : remainder.getCount());
	}

	private static void switchMode(ItemStack itemstack) {
		BackpackMode mode = getMode(itemstack);
		int nextMode = mode.ordinal() + 1;
		if (!Config.enableBackpackResupply && nextMode == BackpackMode.RESUPPLY.ordinal()) {
			nextMode++;
		}
		nextMode %= BackpackMode.values().length;
		itemstack.setDamageValue(nextMode);
	}

	@Nullable
	private static IItemHandler getInventoryHit(Level world, BlockPos pos, Direction side) {
		BlockEntity targeted = TileUtil.getTile(world, pos);
		return TileUtil.getInventoryFromTile(targeted, side);
	}

	private boolean evaluateTileHit(ItemStack stack, Player player, Level world, BlockPos pos, Direction side) {

		// Shift right-clicking on an inventory tile will attempt to transfer
		// items contained in the backpack
		IItemHandler inventory = getInventoryHit(world, pos, side);
		// Process only inventories
		if (inventory != null) {

			// Must have inventory slots
			if (inventory.getSlots() <= 0) {
				return true;
			}

			if (!world.isClientSide) {
				// Create our own backpack inventory
				ItemInventoryBackpack backpackInventory = new ItemInventoryBackpack(player, getBackpackSize(), stack);

				BackpackMode mode = getMode(stack);
				if (mode == BackpackMode.RECEIVE) {
					receiveFromChest(backpackInventory, inventory);
				} else {
					transferToChest(backpackInventory, inventory);
				}
			}

			return true;
		}

		return false;
	}

	private static void transferToChest(ItemInventoryBackpack backpackInventory, IItemHandler target) {
		ItemHandlerInventoryManipulator manipulator = new ItemHandlerInventoryManipulator(backpackInventory.getItemHandler());
		manipulator.transferStacks(target, StandardStackFilters.ALL);
	}

	private void receiveFromChest(ItemInventoryBackpack backpackInventory, IItemHandler target) {
		ItemHandlerInventoryManipulator manipulator = new ItemHandlerInventoryManipulator(target);
		manipulator.transferStacks(backpackInventory.getItemHandler(), definition.getFilter());
	}

	public int getBackpackSize() {
		return getSlotsForType(type);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack itemstack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);

		int occupied = ItemInventory.getOccupiedSlotCount(itemstack);

		BackpackMode mode = getMode(itemstack);
		String infoKey = mode.getUnlocalizedInfo();
		if (infoKey != null) {
			list.add(Component.translatable(infoKey).withStyle(ChatFormatting.GRAY));
		}
		list.add(Component.translatable("for.gui.slots", String.valueOf(occupied), String.valueOf(getBackpackSize())).withStyle(ChatFormatting.GRAY));
	}

	@Override
	public Component getName(ItemStack itemstack) {
		return definition.getName(itemstack);
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {

		if (j == 0) {
			return definition.getPrimaryColour();
		} else if (j == 1) {
			return definition.getSecondaryColour();
		} else {
			return 0xffffff;
		}
	}

	private static int getSlotsForType(EnumBackpackType type) {
		return switch (type) {
			case NATURALIST -> Constants.SLOTS_BACKPACK_APIARIST;
			case WOVEN -> Constants.SLOTS_BACKPACK_WOVEN;
			case NORMAL -> Constants.SLOTS_BACKPACK_DEFAULT;
		};
	}

	public static BackpackMode getMode(ItemStack backpack) {
		Preconditions.checkArgument(backpack.getItem() instanceof ItemBackpack, "Item must be a backpack");

		int meta = backpack.getDamageValue();

		if (meta >= 3) {
			return BackpackMode.RESUPPLY;
		} else if (meta >= 2) {
			return BackpackMode.RECEIVE;
		} else if (meta >= 1) {
			return BackpackMode.LOCKED;
		} else {
			return BackpackMode.NEUTRAL;
		}
	}

	public static EnumBackpackType getType(ItemStack backpack) {
		Preconditions.checkArgument(backpack.getItem() instanceof ItemBackpack, "Item must be a backpack");
		return ((ItemBackpack) backpack.getItem()).type;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		Item oldItem = oldStack.getItem();
		Item newItem = newStack.getItem();
		return oldItem != newItem || getMode(oldStack) != getMode(newStack);
	}

	@Override
	@Nullable
	public AbstractContainerMenu getContainer(int windowId, Player player, ItemStack heldItem) {
		Item item = heldItem.getItem();
		if (!(item instanceof ItemBackpack backpack)) {
			return null;
		}
		return switch (backpack.type) {
			case NORMAL -> new ContainerBackpack(windowId, player, ContainerBackpack.Size.DEFAULT, heldItem);
			case WOVEN -> new ContainerBackpack(windowId, player, ContainerBackpack.Size.T2, heldItem);
			default -> null;
		};
	}
}

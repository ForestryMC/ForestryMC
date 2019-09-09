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

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.IItemHandler;

import net.minecraftforge.fml.network.NetworkHooks;

import forestry.api.storage.BackpackStowEvent;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.core.ItemGroupForestry;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.inventory.ItemHandlerInventoryManipulator;
import forestry.core.inventory.ItemInventory;
import forestry.core.inventory.StandardStackFilters;
import forestry.core.items.IColoredItem;
import forestry.core.items.ItemWithGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.tiles.TileUtil;
import forestry.storage.BackpackMode;
import forestry.storage.gui.ContainerBackpack;
import forestry.storage.inventory.ItemInventoryBackpack;

public class ItemBackpack extends ItemWithGui implements IColoredItem {
	private final IBackpackDefinition definition;
	private final EnumBackpackType type;

	public ItemBackpack(IBackpackDefinition definition, EnumBackpackType type) {
		this(definition, type, ItemGroupForestry.tabForestry);
	}

	public ItemBackpack(IBackpackDefinition definition, EnumBackpackType type, ItemGroup tab) {
		super((new Item.Properties()).group(tab));
		Preconditions.checkNotNull(definition, "Backpack must have a backpack definition.");
		Preconditions.checkNotNull(type, "Backpack must have a backpack type.");

		this.definition = definition;
		this.type = type;
		addPropertyOverride(new ResourceLocation("mode"), (itemStack, world, livingEntity) -> {
			return getMode(itemStack).ordinal();
		});
	}

	public IBackpackDefinition getDefinition() {
		return definition;
	}

	@Override
	protected void openGui(ServerPlayerEntity playerEntity, ItemStack stack) {
		NetworkHooks.openGui(playerEntity, new ContainerProvider(stack), b -> {
			PacketBufferForestry p = new PacketBufferForestry(b);
			p.writeEnum(type == EnumBackpackType.WOVEN ? ContainerBackpack.Size.T2 : ContainerBackpack.Size.DEFAULT, ContainerBackpack.Size.values());
			p.writeItemStack(stack);
		});
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (!playerIn.isSneaking()) {
			return super.onItemRightClick(worldIn, playerIn, handIn);
		} else {
			ItemStack heldItem = playerIn.getHeldItem(handIn);
			switchMode(heldItem);
			return ActionResult.newResult(ActionResultType.SUCCESS, heldItem);
		}
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext ctx) {
		if (getInventoryHit(ctx.getWorld(), ctx.getPos(), ctx.getFace()) != null) {
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		// We only do this when shift is clicked
		if (player.isSneaking()) {
			ItemStack heldItem = player.getHeldItem(context.getHand());
			return evaluateTileHit(heldItem, player, context.getWorld(), context.getPos(), context.getFace()) ? ActionResultType.PASS : ActionResultType.FAIL;
		}
		return super.onItemUseFirst(stack, context);
	}

	public static void tryStowing(PlayerEntity player, ItemStack backpackStack, ItemStack stack) {
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
		itemstack.setDamage(nextMode);
	}

	@Nullable
	private static IItemHandler getInventoryHit(World world, BlockPos pos, Direction side) {
		TileEntity targeted = TileUtil.getTile(world, pos);
		return TileUtil.getInventoryFromTile(targeted, side);
	}

	private boolean evaluateTileHit(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side) {

		// Shift right-clicking on an inventory tile will attempt to transfer
		// items contained in the backpack
		IItemHandler inventory = getInventoryHit(world, pos, side);
		// Process only inventories
		if (inventory != null) {

			// Must have inventory slots
			if (inventory.getSlots() <= 0) {
				return true;
			}

			if (!world.isRemote) {
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
	public void addInformation(ItemStack itemstack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.addInformation(itemstack, world, list, flag);

		int occupied = ItemInventory.getOccupiedSlotCount(itemstack);

		BackpackMode mode = getMode(itemstack);
		String infoKey = mode.getUnlocalizedInfo();
		if (infoKey != null) {
			list.add(new TranslationTextComponent(infoKey).applyTextStyle(TextFormatting.GRAY));
		}
		list.add(new TranslationTextComponent("for.gui.slots", String.valueOf(occupied), String.valueOf(getBackpackSize())).applyTextStyle(TextFormatting.GRAY));
	}

	@Override
	public ITextComponent getDisplayName(ItemStack itemstack) {
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
		switch (type) {
			case NATURALIST:
				return Constants.SLOTS_BACKPACK_APIARIST;
			case WOVEN:
				return Constants.SLOTS_BACKPACK_WOVEN;
			case NORMAL:
			default:
				return Constants.SLOTS_BACKPACK_DEFAULT;
		}
	}

	public static BackpackMode getMode(ItemStack backpack) {
		Preconditions.checkArgument(backpack.getItem() instanceof ItemBackpack, "Item must be a backpack");

		int meta = backpack.getDamage();

		if (meta >= 3) {
			return BackpackMode.RESUPPLY;
		} else if (meta >= 2) {
			return BackpackMode.RECEIVE;
		} else if (meta >= 1) {
			return BackpackMode.LOCKED;
		} else {
			return BackpackMode.NORMAL;
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		Item oldItem = oldStack.getItem();
		Item newItem = newStack.getItem();
		return oldItem != newItem || getMode(oldStack) != getMode(newStack);
	}

	@Override
	@Nullable
	public Container getContainer(int windowId, PlayerEntity player, ItemStack heldItem) {
		Item item = heldItem.getItem();
		if (!(item instanceof ItemBackpack)) {
			return null;    //TODO OK?
		}
		ItemBackpack backpack = (ItemBackpack) item;
		EnumBackpackType type = backpack.type;
		switch (type) {
			case NORMAL:
				return new ContainerBackpack(windowId, player, ContainerBackpack.Size.DEFAULT, heldItem);
			case WOVEN:
				return new ContainerBackpack(windowId, player, ContainerBackpack.Size.T2, heldItem);
			default:
				return null;
		}
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
			Item item = heldItem.getItem();
			if (!(item instanceof ItemBackpack)) {
				return null;
			}
			ItemBackpack backpack = (ItemBackpack) item;
			return backpack.getContainer(windowId, playerEntity, heldItem);
		}
	}
}

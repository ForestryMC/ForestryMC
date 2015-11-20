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

import java.util.List;
import java.util.Locale;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.storage.BackpackStowEvent;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.gui.GuiHandler;
import forestry.core.inventory.ItemInventory;
import forestry.core.inventory.wrappers.IInvSlot;
import forestry.core.inventory.wrappers.InventoryIterator;
import forestry.core.items.ItemWithGui;
import forestry.core.render.TextureManager;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.StringUtil;
import forestry.storage.BackpackMode;
import forestry.storage.gui.ContainerBackpack;
import forestry.storage.gui.GuiBackpack;
import forestry.storage.gui.GuiBackpackT2;
import forestry.storage.inventory.ItemInventoryBackpack;

public class ItemBackpack extends ItemWithGui {
	private final IBackpackDefinition definition;
	private final EnumBackpackType type;

	public ItemBackpack(IBackpackDefinition definition, EnumBackpackType type) {
		this.definition = definition;
		this.type = type;
	}

	public IBackpackDefinition getDefinition() {
		return definition;
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	protected void openGui(EntityPlayer entityplayer) {
		GuiHandler.openGui(entityplayer, this, (short) type.ordinal());
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			if (!player.isSneaking()) {
				openGui(player);
			} else {
				switchMode(itemstack);
			}
		}

		return itemstack;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		return getInventoryHit(world, x, y, z, side) != null;
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

		if (world.isRemote) {
			return false;
		}

		// We only do this when shift is clicked
		if (!player.isSneaking()) {
			return false;
		}

		return evaluateTileHit(itemstack, player, world, x, y, z, side);
	}

	public static ItemStack tryStowing(EntityPlayer player, ItemStack backpackStack, ItemStack stack) {

		ItemBackpack backpack = ((ItemBackpack) backpackStack.getItem());
		ItemInventory inventory = new ItemInventoryBackpack(player, backpack.getBackpackSize(), backpackStack);
		if (backpackStack.getItemDamage() == 1) {
			return stack;
		}

		Event event = new BackpackStowEvent(player, backpack.getDefinition(), inventory, stack);
		MinecraftForge.EVENT_BUS.post(event);
		if (stack.stackSize <= 0) {
			return null;
		}
		if (event.isCanceled()) {
			return stack;
		}

		ItemStack remainder = InventoryUtil.moveItemStack(stack, inventory);
		stack.stackSize = remainder == null ? 0 : remainder.stackSize;

		return null;
	}

	private static void switchMode(ItemStack itemstack) {
		BackpackMode mode = getMode(itemstack);
		int nextMode = mode.ordinal() + 1;
		if (!Config.enableBackpackResupply && nextMode == BackpackMode.RESUPPLY.ordinal()) {
			nextMode++;
		}
		nextMode %= BackpackMode.values().length;
		itemstack.setItemDamage(nextMode);
	}

	private static IInventory getInventoryHit(World world, int x, int y, int z, int side) {
		TileEntity targeted = world.getTileEntity(x, y, z);
		return TileUtil.getInventoryFromTile(targeted, ForgeDirection.getOrientation(side));
	}

	private boolean evaluateTileHit(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {

		// Shift right-clicking on an inventory tile will attempt to transfer
		// items contained in the backpack
		IInventory inventory = getInventoryHit(world, x, y, z, side);
		// Process only inventories
		if (inventory != null) {

			// Must have inventory slots
			if (inventory.getSizeInventory() <= 0) {
				return true;
			}

			// Create our own backpack inventory
			ItemInventoryBackpack backpackInventory = new ItemInventoryBackpack(player, getBackpackSize(), stack);

			BackpackMode mode = getMode(stack);
			if (mode == BackpackMode.RECEIVE) {
				tryChestReceive(backpackInventory, inventory);
			} else {
				tryChestTransfer(backpackInventory, inventory);
			}

			return true;
		}

		return false;
	}

	private static void tryChestTransfer(ItemInventoryBackpack backpackInventory, IInventory target) {

		for (IInvSlot slot : InventoryIterator.getIterable(backpackInventory)) {
			ItemStack packStack = slot.getStackInSlot();
			if (packStack == null) {
				continue;
			}

			ItemStack remaining = InventoryUtil.moveItemStack(packStack, target);
			slot.setStackInSlot(remaining);
		}
	}

	private void tryChestReceive(ItemInventoryBackpack backpackInventory, IInventory target) {

		for (IInvSlot slot : InventoryIterator.getIterable(target)) {
			ItemStack targetStack = slot.getStackInSlot();
			if (targetStack == null) {
				continue;
			}

			if (!definition.isValidItem(targetStack)) {
				continue;
			}

			ItemStack remaining = InventoryUtil.moveItemStack(targetStack, backpackInventory);
			slot.setStackInSlot(remaining);
		}
	}

	public int getBackpackSize() {
		return getSlotsForType(type);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		int occupied = ItemInventory.getOccupiedSlotCount(itemstack);

		BackpackMode mode = getMode(itemstack);
		if (mode == BackpackMode.LOCKED) {
			list.add(StringUtil.localize("storage.backpack.mode.locked"));
		} else if (mode == BackpackMode.RECEIVE) {
			list.add(StringUtil.localize("storage.backpack.mode.receiving"));
		} else if (mode == BackpackMode.RESUPPLY) {
			list.add(StringUtil.localize("storage.backpack.mode.resupply"));
		}
		list.add(StringUtil.localize("gui.slots").replaceAll("%USED", String.valueOf(occupied)).replaceAll("%SIZE", String.valueOf(getBackpackSize())));

	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		return definition.getName(itemstack);
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		icons = new IIcon[6];

		EnumBackpackType t = type == EnumBackpackType.APIARIST ? EnumBackpackType.T1 : type;
		String typeTag = "backpacks/" + t.toString().toLowerCase(Locale.ENGLISH);

		icons[0] = TextureManager.registerTex(register, typeTag + ".cloth");
		icons[1] = TextureManager.registerTex(register, typeTag + ".outline");
		icons[2] = TextureManager.registerTex(register, "backpacks/neutral");
		icons[3] = TextureManager.registerTex(register, "backpacks/locked");
		icons[4] = TextureManager.registerTex(register, "backpacks/receive");
		icons[5] = TextureManager.registerTex(register, "backpacks/resupply");
	}

	// Return true to enable color overlay - client side only
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getRenderPasses(int metadata) {
		return 3;
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

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamageForRenderPass(int i, int j) {
		if (j == 0) {
			return icons[0];
		}
		if (j == 1) {
			return icons[1];
		}

		if (i > 2) {
			return icons[5];
		} else if (i > 1) {
			return icons[4];
		} else if (i > 0) {
			return icons[3];
		} else {
			return icons[2];
		}
	}

	private static int getSlotsForType(EnumBackpackType type) {
		switch (type) {
			case APIARIST:
				return Constants.SLOTS_BACKPACK_APIARIST;
			case T2:
				return Constants.SLOTS_BACKPACK_T2;
			case T1:
			default:
				return Constants.SLOTS_BACKPACK_DEFAULT;
		}
	}

	public static BackpackMode getMode(ItemStack backpack) {
		int meta = backpack.getItemDamage();

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
	public Object getGui(EntityPlayer player, ItemStack heldItem, int data) {
		if (data > EnumBackpackType.values().length) {
			return null;
		}
		EnumBackpackType type = EnumBackpackType.values()[data];
		switch (type) {
			case T1:
				return new GuiBackpack(new ContainerBackpack(player, ContainerBackpack.Size.DEFAULT, heldItem));
			case T2:
				return new GuiBackpackT2(new ContainerBackpack(player, ContainerBackpack.Size.T2, heldItem));
		}
		return null;
	}

	@Override
	public Object getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		if (data > EnumBackpackType.values().length) {
			return null;
		}
		EnumBackpackType type = EnumBackpackType.values()[data];
		switch (type) {
			case T1:
				return new ContainerBackpack(player, ContainerBackpack.Size.DEFAULT, heldItem);
			case T2:
				return new ContainerBackpack(player, ContainerBackpack.Size.T2, heldItem);
		}
		return null;
	}
}

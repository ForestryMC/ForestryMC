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

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.core.ForestryAPI;
import forestry.api.storage.BackpackStowEvent;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.core.config.Defaults;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.wrappers.IInvSlot;
import forestry.core.inventory.wrappers.InventoryIterator;
import forestry.core.items.ItemInventoried;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.ItemInventory;
import forestry.core.utils.StringUtil;
import forestry.storage.BackpackMode;
import java.util.EnumSet;

public class ItemBackpack extends ItemInventoried {

	private final IBackpackDefinition info;
	private final EnumBackpackType type;

	public ItemBackpack(IBackpackDefinition info, EnumBackpackType type) {
		super();
		this.info = info;
		this.type = type;
		setMaxStackSize(1);
	}

	public IBackpackDefinition getDefinition() {
		return info;
	}

	/**
	 * @return true if the item's stackTagCompound needs to be synchronized over
	 * SMP.
	 */
	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {

		if (!Proxies.common.isSimulating(world))
			return itemstack;

		if (!player.isSneaking())
			openGui(player, itemstack);
		else
			switchMode(itemstack);
		return itemstack;

	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (getInventoryHit(world, x, y, z, side) != null)
			return true;
		else
			return false;
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

		if (!Proxies.common.isSimulating(world))
			return false;

		// We only do this when shift is clicked
		if (!player.isSneaking())
			return false;

		return evaluateTileHit(itemstack, player, world, x, y, z, side, hitX, hitY, hitZ);
	}

	public ItemStack tryStowing(EntityPlayer player, ItemStack backpackStack, ItemStack stack) {

		ItemBackpack backpack = ((ItemBackpack) backpackStack.getItem());
		ItemInventory inventory = new ItemInventory(ItemBackpack.class, backpack.getBackpackSize(), backpackStack);
		if (backpackStack.getItemDamage() == 1)
			return stack;

		Event event = new BackpackStowEvent(player, backpack.getDefinition(), inventory, stack);
		MinecraftForge.EVENT_BUS.post(event);
		if (stack.stackSize <= 0)
			return null;
		if (event.isCanceled())
			return stack;

		ItemStack remainder = InvTools.moveItemStack(stack, inventory);
		stack.stackSize = remainder == null ? 0 : remainder.stackSize;

		inventory.save();
		return null;
	}

	private void switchMode(ItemStack itemstack) {
		BackpackMode mode = getMode(itemstack);
		if (mode == BackpackMode.RESUPPLY)
			itemstack.setItemDamage(0);
		else if (mode == BackpackMode.RECEIVE)
			itemstack.setItemDamage(3);
		else if (mode == BackpackMode.LOCKED)
			itemstack.setItemDamage(2);
		else
			itemstack.setItemDamage(1);
	}

	private IInventory getInventoryHit(World world, int x, int y, int z, int side) {
		TileEntity targeted = world.getTileEntity(x, y, z);
		return InvTools.getInventoryFromTile(targeted, ForgeDirection.getOrientation(side));
	}

	private boolean evaluateTileHit(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

		// Shift right-clicking on an inventory tile will attempt to transfer
		// items contained in the backpack
		IInventory inventory = getInventoryHit(world, x, y, z, side);
		// Process only inventories
		if (inventory != null) {

			// Must have inventory slots
			if (inventory.getSizeInventory() <= 0)
				return true;

			// Create our own backpack inventory
			ItemInventory backpackInventory = new ItemInventory(ItemBackpack.class, getBackpackSize(), stack);

			if (stack.getItemDamage() == 2)
				tryChestReceive(player, backpackInventory, inventory);
			else
				tryChestTransfer(backpackInventory, inventory);

			backpackInventory.save();

			return true;
		}

		return false;
	}

	private void tryChestTransfer(ItemInventory backpackInventory, IInventory target) {

		for (IInvSlot slot : InventoryIterator.getIterable(backpackInventory)) {
			ItemStack packStack = slot.getStackInSlot();
			if (packStack == null)
				continue;

			ItemStack remaining = InvTools.moveItemStack(packStack, target);
			slot.setStackInSlot(remaining);
		}
	}

	private void tryChestReceive(EntityPlayer player, ItemInventory backpackInventory, IInventory target) {

		for (IInvSlot slot : InventoryIterator.getIterable(target)) {
			ItemStack targetStack = slot.getStackInSlot();
			if (targetStack == null)
				continue;

			if (!info.isValidItem(player, targetStack))
				continue;

			ItemStack remaining = InvTools.moveItemStack(targetStack, backpackInventory);
			slot.setStackInSlot(remaining);
		}

	}

	public void openGui(EntityPlayer entityplayer, ItemStack itemstack) {
		if (getBackpackSize() == Defaults.SLOTS_BACKPACK_DEFAULT)
			entityplayer.openGui(ForestryAPI.instance, GuiId.BackpackGUI.ordinal(), entityplayer.worldObj, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);
		else if (getBackpackSize() == Defaults.SLOTS_BACKPACK_T2)
			entityplayer.openGui(ForestryAPI.instance, GuiId.BackpackT2GUI.ordinal(), entityplayer.worldObj, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);
	}

	public boolean isBackpack(ItemStack stack) {
		if (stack == null)
			return false;

		return stack.getItem() == this;
	}

	public int getBackpackSize() {
		return getSlotsForType(type);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		ItemInventory inventory = new ItemInventory(ItemBackpack.class, getBackpackSize(), itemstack);

		// HashMap<ItemStack, Integer> contents = new HashMap<ItemStack,
		// Integer>();
		int occupied = 0;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) == null)
				continue;
			if (inventory.getStackInSlot(i).stackSize <= 0)
				continue;

			// Count the slot as occupied
			occupied++;
		}

		BackpackMode mode = getMode(itemstack);
		if (mode == BackpackMode.LOCKED)
			list.add(StringUtil.localize("storage.backpack.mode.locked"));
		else if (mode == BackpackMode.RECEIVE)
			list.add(StringUtil.localize("storage.backpack.mode.receiving"));
		else if (mode == BackpackMode.RESUPPLY)
			list.add(StringUtil.localize("storage.backpack.mode.resupply"));
		list.add(occupied + "/" + getBackpackSize() + " " + StringUtil.localize("gui.slots"));

	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		try {
			return info.getName(itemstack);
		} catch (Error er) {
			return info.getName();
		}
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

		icons[0] = TextureManager.getInstance().registerTex(register, typeTag + ".cloth");
		icons[1] = TextureManager.getInstance().registerTex(register, typeTag + ".outline");
		icons[2] = TextureManager.getInstance().registerTex(register, "backpacks/neutral");
		icons[3] = TextureManager.getInstance().registerTex(register, "backpacks/locked");
		icons[4] = TextureManager.getInstance().registerTex(register, "backpacks/receive");
		icons[5] = TextureManager.getInstance().registerTex(register, "backpacks/resupply");
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

		if (j == 0)
			return info.getPrimaryColour();
		else if (j == 1)
			return info.getSecondaryColour();
		else
			return 0xffffff;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamageForRenderPass(int i, int j) {
		if (j == 0)
			return icons[0];
		if (j == 1)
			return icons[1];

		if (i > 2)
			return icons[5];
		else if (i > 1)
			return icons[4];
		else if (i > 0)
			return icons[3];
		else
			return icons[2];
	}

	public static int getSlotsForType(EnumBackpackType type) {
		switch (type) {
		case APIARIST:
			return Defaults.SLOTS_BACKPACK_APIARIST;
		case T2:
			return Defaults.SLOTS_BACKPACK_T2;
		case T1:
		default:
			return Defaults.SLOTS_BACKPACK_DEFAULT;
		}
	}

	public static BackpackMode getMode(ItemStack backpack) {
		int meta = backpack.getItemDamage();

		if (meta >= 3)
			return BackpackMode.RESUPPLY;
		else if (meta >= 2)
			return BackpackMode.RECEIVE;
		else if (meta >= 1)
			return BackpackMode.LOCKED;
		else
			return BackpackMode.NORMAL;
	}
}

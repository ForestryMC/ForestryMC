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

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IModelManager;
import forestry.api.storage.BackpackStowEvent;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.ItemInventory;
import forestry.core.inventory.ItemInventoryBackpack;
import forestry.core.inventory.wrappers.IInvSlot;
import forestry.core.inventory.wrappers.InventoryIterator;
import forestry.core.items.ItemInventoried;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;
import forestry.storage.BackpackMode;

public class ItemBackpack extends ItemInventoried {

	private final IBackpackDefinition definition;
	private final EnumBackpackType type;

	public ItemBackpack(IBackpackDefinition definition, EnumBackpackType type) {
		super();
		this.definition = definition;
		this.type = type;
		setMaxStackSize(1);
	}

	public IBackpackDefinition getDefinition() {
		return definition;
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

		if (!Proxies.common.isSimulating(world)) {
			return itemstack;
		}

		if (!player.isSneaking()) {
			openGui(player);
		} else {
			switchMode(itemstack);
		}
		return itemstack;

	}
	
	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		return getInventoryHit(world, pos, side) != null;
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!Proxies.common.isSimulating(world)) {
			return false;
		}

		// We only do this when shift is clicked
		if (!player.isSneaking()) {
			return false;
		}

		return evaluateTileHit(itemstack, player, world, pos, side);
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

		ItemStack remainder = InvTools.moveItemStack(stack, inventory);
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

	private static IInventory getInventoryHit(World world, BlockPos pos, EnumFacing side) {
		TileEntity targeted = world.getTileEntity(pos);
		return InvTools.getInventoryFromTile(targeted, side);
	}

	private boolean evaluateTileHit(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {

		// Shift right-clicking on an inventory tile will attempt to transfer
		// items contained in the backpack
		IInventory inventory = getInventoryHit(world, pos, side);
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

			ItemStack remaining = InvTools.moveItemStack(packStack, target);
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

			ItemStack remaining = InvTools.moveItemStack(targetStack, backpackInventory);
			slot.setStackInSlot(remaining);
		}

	}

	protected void openGui(EntityPlayer entityplayer) {
		if (getBackpackSize() == Defaults.SLOTS_BACKPACK_DEFAULT) {
			entityplayer.openGui(ForestryAPI.instance, GuiId.BackpackGUI.ordinal(), entityplayer.worldObj, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);
		} else if (getBackpackSize() == Defaults.SLOTS_BACKPACK_T2) {
			entityplayer.openGui(ForestryAPI.instance, GuiId.BackpackT2GUI.ordinal(), entityplayer.worldObj, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);
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
	private ModelResourceLocation[] models;
	
	@SideOnly(Side.CLIENT)
	public static int i = 0;
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		EnumBackpackType t = type == EnumBackpackType.APIARIST ? EnumBackpackType.T1 : type;
		String typeTag = "backpacks/" + t.toString().toLowerCase(Locale.ENGLISH);
		models = new ModelResourceLocation[4];
		models[0] = new ModelResourceLocation("forestry:" + typeTag + "_neutral" , "inventory");
		models[1] = new ModelResourceLocation("forestry:" + typeTag + "_locked" , "inventory");
		models[2] = new ModelResourceLocation("forestry:" + typeTag + "_receive", "inventory");
		models[3] = new ModelResourceLocation("forestry:" + typeTag + "_resupply", "inventory");
		if(i == 0 && (type == EnumBackpackType.T1 || type == EnumBackpackType.APIARIST) || i == 1 && type == EnumBackpackType.T2)
		{
			manager.registerVariant(item, "forestry:" + typeTag + "_neutral");
			manager.registerVariant(item, "forestry:" + typeTag + "_locked");
			manager.registerVariant(item, "forestry:" + typeTag + "_receive");
			manager.registerVariant(item, "forestry:" + typeTag + "_resupply");
			i++;
		}
		manager.registerItemModel(item, new BackpackMeshDefinition());
	}
	
	@SideOnly(Side.CLIENT)
	private class BackpackMeshDefinition implements ItemMeshDefinition{

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			EnumBackpackType t = type == EnumBackpackType.APIARIST ? EnumBackpackType.T1 : type;
			return models[stack.getItemDamage()];
		}
		
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
}

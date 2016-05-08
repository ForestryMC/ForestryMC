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
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

import forestry.api.core.IModelManager;
import forestry.api.storage.BackpackStowEvent;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.gui.GuiHandler;
import forestry.core.inventory.ItemInventory;
import forestry.core.inventory.filters.StandardStackFilters;
import forestry.core.inventory.manipulators.ItemHandlerInventoryManipulator;
import forestry.core.items.ItemWithGui;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.Translator;
import forestry.storage.BackpackMode;
import forestry.storage.gui.ContainerBackpack;
import forestry.storage.gui.GuiBackpack;
import forestry.storage.gui.GuiBackpackT2;
import forestry.storage.inventory.ItemInventoryBackpack;

public class ItemBackpack extends ItemWithGui implements IItemColor {
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
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (!playerIn.isSneaking()) {
			return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
		} else {
			switchMode(itemStackIn);
			return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
		}
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (getInventoryHit(worldIn, pos, facing) != null) {
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		// We only do this when shift is clicked
		if (!player.isSneaking()) {
			return EnumActionResult.FAIL;
		}

		return evaluateTileHit(stack, player, world, pos, side) ? EnumActionResult.PASS : EnumActionResult.FAIL;
	}

	public static void tryStowing(EntityPlayer player, ItemStack backpackStack, ItemStack stack) {
		if (getMode(backpackStack) == BackpackMode.LOCKED) {
			return;
		}

		ItemBackpack backpack = (ItemBackpack) backpackStack.getItem();
		ItemInventory inventory = new ItemInventoryBackpack(player, backpack.getBackpackSize(), backpackStack);

		if (MinecraftForge.EVENT_BUS.post(new BackpackStowEvent(player, backpack.getDefinition(), inventory, stack))) {
			return;
		}
		if (stack.stackSize <= 0) {
			return;
		}

		IItemHandler itemHandler = inventory.getItemHandler();
		ItemHandlerInventoryManipulator manipulator = new ItemHandlerInventoryManipulator(itemHandler);
		ItemStack remainder = manipulator.addStack(stack);

		stack.stackSize = remainder == null ? 0 : remainder.stackSize;
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

	private static IItemHandler getInventoryHit(World world, BlockPos pos, EnumFacing side) {
		TileEntity targeted = world.getTileEntity(pos);
		return TileUtil.getInventoryFromTile(targeted, side);
	}

	private boolean evaluateTileHit(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {

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
		manipulator.transferStacks(backpackInventory.getItemHandler(), definition);
	}

	public int getBackpackSize() {
		return getSlotsForType(type);
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List<String> list, boolean flag) {
		int occupied = ItemInventory.getOccupiedSlotCount(itemstack);

		BackpackMode mode = getMode(itemstack);
		String infoKey = mode.getUnlocalizedInfo();
		if (infoKey != null) {
			list.add(Translator.translateToLocal(infoKey));
		}
		list.add(Translator.translateToLocal("for.gui.slots").replaceAll("%USED", String.valueOf(occupied)).replaceAll("%SIZE", String.valueOf(getBackpackSize())));
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		return definition.getName(itemstack);
	}

	/* MODELS */
	@SideOnly(Side.CLIENT)
	private ModelResourceLocation[] models;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		EnumBackpackType t = type == EnumBackpackType.NATURALIST ? EnumBackpackType.NORMAL : type;
		String typeTag = "backpacks/" + t.toString().toLowerCase(Locale.ENGLISH);
		models = new ModelResourceLocation[4];
		models[0] = new ModelResourceLocation("forestry:" + typeTag + "_neutral", "inventory");
		models[1] = new ModelResourceLocation("forestry:" + typeTag + "_locked", "inventory");
		models[2] = new ModelResourceLocation("forestry:" + typeTag + "_receive", "inventory");
		models[3] = new ModelResourceLocation("forestry:" + typeTag + "_resupply", "inventory");
		manager.registerVariant(item, new ResourceLocation("forestry:" + typeTag + "_neutral"));
		manager.registerVariant(item, new ResourceLocation("forestry:" + typeTag + "_locked"));
		manager.registerVariant(item, new ResourceLocation("forestry:" + typeTag + "_receive"));
		manager.registerVariant(item, new ResourceLocation("forestry:" + typeTag + "_resupply"));
		manager.registerItemModel(item, new BackpackMeshDefinition());
	}

	@SideOnly(Side.CLIENT)
	private class BackpackMeshDefinition implements ItemMeshDefinition {

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			BackpackMode mode = getMode(stack);
			return models[mode.ordinal()];
		}

	}

	@Override
	public int getColorFromItemstack(ItemStack itemstack, int j) {

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
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return getMode(oldStack) != getMode(newStack);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public Object getGui(EntityPlayer player, ItemStack heldItem, int data) {
		if (data > EnumBackpackType.values().length) {
			return null;
		}
		EnumBackpackType type = EnumBackpackType.values()[data];
		switch (type) {
			case NORMAL:
				return new GuiBackpack(new ContainerBackpack(player, ContainerBackpack.Size.DEFAULT, heldItem));
			case WOVEN:
				return new GuiBackpackT2(new ContainerBackpack(player, ContainerBackpack.Size.T2, heldItem));
		}
		return null;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public Object getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		if (data > EnumBackpackType.values().length) {
			return null;
		}
		EnumBackpackType type = EnumBackpackType.values()[data];
		switch (type) {
			case NORMAL:
				return new ContainerBackpack(player, ContainerBackpack.Size.DEFAULT, heldItem);
			case WOVEN:
				return new ContainerBackpack(player, ContainerBackpack.Size.T2, heldItem);
		}
		return null;
	}
}

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
import java.util.Locale;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
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
import net.minecraftforge.items.IItemHandler;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IModelManager;
import forestry.api.storage.BackpackStowEvent;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.gui.GuiHandler;
import forestry.core.inventory.ItemHandlerInventoryManipulator;
import forestry.core.inventory.ItemInventory;
import forestry.core.inventory.StandardStackFilters;
import forestry.core.items.IColoredItem;
import forestry.core.items.ItemWithGui;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.Translator;
import forestry.storage.BackpackMode;
import forestry.storage.gui.ContainerBackpack;
import forestry.storage.gui.GuiBackpack;
import forestry.storage.gui.GuiBackpackT2;
import forestry.storage.inventory.ItemInventoryBackpack;

public class ItemBackpack extends ItemWithGui implements IColoredItem {
	private final IBackpackDefinition definition;
	private final EnumBackpackType type;

	public ItemBackpack(IBackpackDefinition definition, EnumBackpackType type) {
		Preconditions.checkNotNull(definition, "Backpack must have a backpack definition.");
		Preconditions.checkNotNull(type, "Backpack must have a backpack type.");

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
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (!playerIn.isSneaking()) {
			return super.onItemRightClick(worldIn, playerIn, handIn);
		} else {
			ItemStack heldItem = playerIn.getHeldItem(handIn);
			switchMode(heldItem);
			return ActionResult.newResult(EnumActionResult.SUCCESS, heldItem);
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (getInventoryHit(worldIn, pos, facing) != null) {
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		// We only do this when shift is clicked
		if (player.isSneaking()) {
			ItemStack heldItem = player.getHeldItem(hand);
			return evaluateTileHit(heldItem, player, world, pos, side) ? EnumActionResult.PASS : EnumActionResult.FAIL;
		}
		return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
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
		itemstack.setItemDamage(nextMode);
	}

	@Nullable
	private static IItemHandler getInventoryHit(World world, BlockPos pos, EnumFacing side) {
		TileEntity targeted = TileUtil.getTile(world, pos);
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
		manipulator.transferStacks(backpackInventory.getItemHandler(), definition.getFilter());
	}

	public int getBackpackSize() {
		return getSlotsForType(type);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		super.addInformation(itemstack, world, list, flag);

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
		ModelBakery.registerItemVariants(item, new ResourceLocation("forestry:" + typeTag + "_neutral"));
		ModelBakery.registerItemVariants(item, new ResourceLocation("forestry:" + typeTag + "_locked"));
		ModelBakery.registerItemVariants(item, new ResourceLocation("forestry:" + typeTag + "_receive"));
		ModelBakery.registerItemVariants(item, new ResourceLocation("forestry:" + typeTag + "_resupply"));
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
		Preconditions.checkArgument(backpack.getItem() instanceof ItemBackpack, "Item must be a backpack");

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
		Item oldItem = oldStack.getItem();
		Item newItem = newStack.getItem();
		return oldItem != newItem || getMode(oldStack) != getMode(newStack);
	}

	@Override
	@Nullable
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, ItemStack heldItem, int data) {
		if (data > EnumBackpackType.values().length) {
			return null;
		}
		EnumBackpackType type = EnumBackpackType.values()[data];
		switch (type) {
			case NORMAL:
				return new GuiBackpack(new ContainerBackpack(player, ContainerBackpack.Size.DEFAULT, heldItem));
			case WOVEN:
				return new GuiBackpackT2(new ContainerBackpack(player, ContainerBackpack.Size.T2, heldItem));
			default:
				return null;
		}
	}

	@Override
	@Nullable
	public Container getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		if (data > EnumBackpackType.values().length) {
			return null;
		}
		EnumBackpackType type = EnumBackpackType.values()[data];
		switch (type) {
			case NORMAL:
				return new ContainerBackpack(player, ContainerBackpack.Size.DEFAULT, heldItem);
			case WOVEN:
				return new ContainerBackpack(player, ContainerBackpack.Size.T2, heldItem);
			default:
				return null;
		}
	}
}

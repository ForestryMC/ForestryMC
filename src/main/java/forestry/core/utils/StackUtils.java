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
package forestry.core.utils;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.oredict.OreDictionary;

import forestry.core.config.Defaults;
import forestry.core.inventory.InvTools;

public class StackUtils {

	public static final ItemStack[] EMPTY_STACK_ARRAY = new ItemStack[0];

	/**
	 * Compares item id, damage and NBT. Accepts wildcard damage.
	 */
	public static boolean isIdenticalItem(ItemStack lhs, ItemStack rhs) {
		if (lhs == null || rhs == null) {
			return false;
		}

		if (lhs.getItem() != rhs.getItem()) {
			return false;
		}

		if (lhs.getItemDamage() != Defaults.WILDCARD) {
			if (lhs.getItemDamage() != rhs.getItemDamage()) {
				return false;
			}
		}

		return ItemStack.areItemStackTagsEqual(lhs, rhs);
	}

	public static boolean stowInInventory(ItemStack itemstack, IInventory inventory, boolean doAdd) {
		return stowInInventory(itemstack, inventory, doAdd, 0, inventory.getSizeInventory());
	}

	public static boolean stowInInventory(ItemStack itemstack, IInventory inventory, boolean doAdd, int slot1, int count) {

		boolean added = false;

		for (int i = slot1; i < slot1 + count; i++) {
			ItemStack inventoryStack = inventory.getStackInSlot(i);

			// Grab those free slots
			if (inventoryStack == null) {
				if (doAdd) {
					inventory.setInventorySlotContents(i, itemstack.copy());
					itemstack.stackSize = 0;
				}
				return true;
			}

			// Already full
			if (inventoryStack.stackSize >= inventoryStack.getMaxStackSize()) {
				continue;
			}

			// Not same type
			if (!inventoryStack.isItemEqual(itemstack)) {
				continue;
			}
			if (!ItemStack.areItemStackTagsEqual(inventoryStack, itemstack)) {
				continue;
			}

			int space = inventoryStack.getMaxStackSize() - inventoryStack.stackSize;

			// Enough space to add all
			if (space > itemstack.stackSize) {
				if (doAdd) {
					inventoryStack.stackSize += itemstack.stackSize;
					itemstack.stackSize = 0;
				}
				return true;
				// Only part can be added
			} else {
				if (doAdd) {
					inventoryStack.stackSize = inventoryStack.getMaxStackSize();
					itemstack.stackSize -= space;
				}
				added = true;
			}

		}

		return added;

	}

	public static int addToInventory(ItemStack itemstack, IInventory inventory, boolean doAdd, int slot1, int count) {

		int added = 0;

		for (int i = slot1; i < slot1 + count; i++) {
			ItemStack inventoryStack = inventory.getStackInSlot(i);

			// Grab those free slots
			if (inventoryStack == null) {
				if (doAdd) {
					inventory.setInventorySlotContents(i, itemstack.copy());
				}
				return itemstack.stackSize;
			}

			// Already full
			if (inventoryStack.stackSize >= inventoryStack.getMaxStackSize()) {
				continue;
			}

			// Not same type
			if (!inventoryStack.isItemEqual(itemstack)) {
				continue;
			}

			int space = inventoryStack.getMaxStackSize() - inventoryStack.stackSize;

			// Enough space to add all
			if (space > itemstack.stackSize - added) {
				if (doAdd) {
					inventoryStack.stackSize += itemstack.stackSize;
				}
				return itemstack.stackSize;
				// Only part can be added
			} else {
				if (doAdd) {
					inventoryStack.stackSize = inventoryStack.getMaxStackSize();
				}
				added += space;
			}

		}

		return added;

	}

	/**
	 * Merges the giving stack into the receiving stack as far as possible
	 */
	public static void mergeStacks(ItemStack giver, ItemStack receptor) {
		if (receptor.stackSize >= 64) {
			return;
		}

		if (!receptor.isItemEqual(giver)) {
			return;
		}

		if (giver.stackSize <= (receptor.getMaxStackSize() - receptor.stackSize)) {
			receptor.stackSize += giver.stackSize;
			giver.stackSize = 0;
			return;
		}

		ItemStack temp = giver.splitStack(receptor.getMaxStackSize() - receptor.stackSize);
		receptor.stackSize += temp.stackSize;
		temp.stackSize = 0;
	}

	public static boolean freeSpaceInStack(ItemStack stack, int maxSize) {
		if (stack == null) {
			return true;
		}

		if (stack.stackSize >= maxSize) {
			return false;
		}

		return true;
	}

	/**
	 * Creates a split stack of the specified amount, preserving NBT data,
	 * without decreasing the source stack.
	 */
	public static ItemStack createSplitStack(ItemStack stack, int amount) {
		ItemStack split = new ItemStack(stack.getItem(), amount, stack.getItemDamage());
		if (stack.getTagCompound() != null) {
			NBTTagCompound nbttagcompound = (NBTTagCompound) stack.getTagCompound().copy();
			split.setTagCompound(nbttagcompound);
		}
		return split;
	}

	public static ItemStack[] condenseStacks(ItemStack[] stacks) {
		return condenseStacks(stacks, -1, false);
	}

	/**
	 * @param maxCountedPerStack The maximum stacksize counted in a single stack. -1 for unlimited.
	 */
	public static ItemStack[] condenseStacks(ItemStack[] stacks, int maxCountedPerStack, boolean craftingEquivalency) {
		ArrayList<ItemStack> condensed = new ArrayList<ItemStack>();

		for (ItemStack stack : stacks) {
			if (stack == null) {
				continue;
			}
			if (stack.stackSize <= 0) {
				continue;
			}

			boolean matched = false;
			for (ItemStack cached : condensed) {
				if (cached.isItemEqual(stack)
						|| (craftingEquivalency && isCraftingEquivalent(cached, stack, true, false))) {
					cached.stackSize += maxCountedPerStack > 0 && stack.stackSize > maxCountedPerStack ? maxCountedPerStack : stack.stackSize;
					matched = true;
				}
			}

			if (!matched) {
				ItemStack cached = stack.copy();
				if (maxCountedPerStack > 0) {
					cached.stackSize = maxCountedPerStack;
				}
				condensed.add(cached);
			}

		}

		return condensed.toArray(new ItemStack[condensed.size()]);
	}

	public static boolean containsItemStack(Iterable<ItemStack> list, ItemStack itemStack) {
		for (ItemStack listStack : list) {
			if (isIdenticalItem(listStack, itemStack)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Counts how many full sets are contained in the passed stock
	 */
	public static int containsSets(ItemStack[] set, ItemStack[] stock) {
		return containsSets(set, stock, false, false);
	}

	/**
	 * Counts how many full sets are contained in the passed stock
	 */
	public static int containsSets(ItemStack[] set, ItemStack[] stock, boolean oreDictionary, boolean craftingTools) {
		int count = 0;

		ItemStack[] condensedRequired = StackUtils.condenseStacks(set, -1, oreDictionary);
		ItemStack[] condensedOffered = StackUtils.condenseStacks(stock, -1, oreDictionary);

		for (ItemStack req : condensedRequired) {

			boolean matched = false;
			for (ItemStack offer : condensedOffered) {

				if (isCraftingEquivalent(req, offer, oreDictionary, craftingTools)) {
					matched = true;

					int stackCount = (int) Math.floor(offer.stackSize / req.stackSize);
					if (stackCount <= 0) {
						return 0;
					} else if (count == 0) {
						count = stackCount;
					} else if (count > stackCount) {
						count = stackCount;
					}
				}
			}
			if (!matched) {
				return 0;
			}
		}

		return count;
	}

	/**
	 * Compare two item stacks for crafting equivalency without oreDictionary or craftingTools
	 */
	public static boolean isCraftingEquivalent(ItemStack base, ItemStack comparison) {
		if (base == null || comparison == null) {
			return false;
		}

		if (base.getItem() != comparison.getItem()) {
			return false;
		}

		if (base.getItemDamage() != Defaults.WILDCARD) {
			if (base.getItemDamage() != comparison.getItemDamage()) {
				return false;
			}
		}

		// When the base stackTagCompound is null or empty, treat it as a wildcard for crafting
		if (base.getTagCompound() == null || base.getTagCompound().hasNoTags()) {
			return true;
		} else {
			return ItemStack.areItemStackTagsEqual(base, comparison);
		}
	}

	/**
	 * Compare two item stacks for crafting equivalency.
	 */
	public static boolean isCraftingEquivalent(ItemStack base, ItemStack comparison, boolean oreDictionary, boolean craftingTools) {
		if (isCraftingEquivalent(base, comparison)) {
			return true;
		}

		if (base == null || comparison == null) {
			return false;
		}

		if (oreDictionary) {
			int[] idsBase = OreDictionary.getOreIDs(base);
			for (int idBase : idsBase) {
				for (ItemStack itemstack : OreDictionary.getOres(OreDictionary.getOreName(idBase))) {
					if (comparison.getItem() == itemstack.getItem() && (itemstack.getItemDamage() == OreDictionary.WILDCARD_VALUE || comparison.getItemDamage() == itemstack.getItemDamage())) {
						return true;
					}
				}
			}
		}

		if (craftingTools) {
			return isThisCraftingTool(base, comparison);
		}

		return false;
	}

	public static boolean isCraftingTool(ItemStack itemstack) {
		return itemstack.getItem().hasContainerItem(itemstack)
				&& itemstack.getItem().isDamageable()
				&& !itemstack.getItem().doesContainerItemLeaveCraftingGrid(itemstack);
	}

	public static boolean isThisCraftingTool(ItemStack phantom, ItemStack actual) {
		return isCraftingTool(phantom) && phantom.getItem() == actual.getItem();
	}

	public static void stowContainerItem(ItemStack itemstack, IInventory stowing, int slotIndex, EntityPlayer player) {
		if (!itemstack.getItem().hasContainerItem(itemstack)) {
			return;
		}

		ItemStack container = itemstack.getItem().getContainerItem(itemstack);

		if (container.isItemStackDamageable() && container.getItemDamage() > container.getMaxDamage()) {
			if (player != null) {
				MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, container));
			}
			container = null;
		}

		if (container != null) {

			if (itemstack.getItem().doesContainerItemLeaveCraftingGrid(itemstack)) {
				if (!InvTools.tryAddStack(stowing, container, true)) {
					if (player != null && !player.inventory.addItemStackToInventory(container)) {
						player.dropPlayerItemWithRandomChoice(container, true);
					}
				}
			} else {
				if (!InvTools.tryAddStack(stowing, container, slotIndex, 1, true)) {
					if (!InvTools.tryAddStack(stowing, container, true) && player != null) {
						player.dropPlayerItemWithRandomChoice(container, true);
					}
				}
			}
		}
	}

	public static void dropItemStackAsEntity(ItemStack items, World world, BlockPos pos) {
		if (items.stackSize <= 0) {
			return;
		}

		float f1 = 0.7F;
		double d = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
		double d1 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
		double d2 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
		EntityItem entityitem = new EntityItem(world, pos.getX() + d, pos.getY() + d1, pos.getZ() + d2, items);
		entityitem.setPickupDelay(10);

		world.spawnEntityInWorld(entityitem);

	}

	public static ItemStack copyWithRandomSize(ItemStack template, int max, Random rand) {
		int size = rand.nextInt(max);
		ItemStack created = template.copy();
		created.stackSize = size <= 0 ? 1 : size > created.getMaxStackSize() ? created.getMaxStackSize() : size;
		return created;
	}

	public static ItemStack consumeItem(ItemStack stack) {
		if (stack.stackSize == 1) {
			if (stack.getItem().hasContainerItem(stack)) {
				return stack.getItem().getContainerItem(stack);
			} else {
				return null;
			}
		} else {
			stack.splitStack(1);

			return stack;
		}
	}

	public static Block getBlock(ItemStack stack) {
		Item item = stack.getItem();

		if (item instanceof ItemBlock) {
			return ((ItemBlock) item).block;
		} else {
			return null;
		}
	}

	public static boolean equals(Block block, ItemStack stack) {
		return block == getBlock(stack);
	}

	public static boolean equals(Block block, int meta, ItemStack stack) {
		return block == getBlock(stack) && meta == stack.getItemDamage();
	}
}

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
package forestry.factory.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import forestry.core.gui.ContainerTile;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotCraftMatrix;
import forestry.core.gui.slots.SlotCrafter;
import forestry.core.interfaces.IContainerCrafting;
import forestry.core.network.PacketGuiSelect;
import forestry.core.network.PacketId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;
import forestry.factory.gadgets.TileWorktable;
import forestry.factory.network.PacketWorktableMemoryUpdate;
import forestry.factory.recipes.RecipeMemory;

public class ContainerWorktable extends ContainerTile<TileWorktable> implements IContainerCrafting, IGuiSelectable {

	private final InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	private long lastUpdate;

	public ContainerWorktable(EntityPlayer player, TileWorktable tile) {
		super(tile, player.inventory, 8, 136);

		IInventory craftingInventory = tile.getCraftingInventory();
		IInventory internalInventory = tile.getInternalInventory();

		// Internal inventory
		for (int i = 0; i < 2; i++) {
			for (int k = 0; k < 9; k++) {
				addSlotToContainer(new Slot(internalInventory, TileWorktable.SLOT_INVENTORY_1 + k + i * 9, 8 + k * 18, 90 + i * 18));
			}
		}

		// Crafting matrix
		for (int l = 0; l < 3; l++) {
			for (int k1 = 0; k1 < 3; k1++) {
				addSlotToContainer(new SlotCraftMatrix(this, craftingInventory, k1 + l * 3, 11 + k1 * 18, 20 + l * 18));
			}
		}

		// CraftResult display
		addSlotToContainer(new SlotCrafter(player, craftingInventory, tile, TileWorktable.SLOT_CRAFTING_RESULT, 77, 38));

		// Update crafting matrix with current contents of tileentity.
		updateMatrix();
		updateRecipe();
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (lastUpdate == tile.getMemory().getLastUpdate()) {
			return;
		}

		lastUpdate = tile.getMemory().getLastUpdate();

		PacketWorktableMemoryUpdate packet = new PacketWorktableMemoryUpdate(tile);
		sendPacketToCrafters(packet);
	}

	@Override
	public void onCraftMatrixChanged(IInventory iinventory, int slot) {
		if (slot >= craftMatrix.getSizeInventory()) {
			return;
		}

		ItemStack stack = iinventory.getStackInSlot(slot);
		ItemStack currentStack = craftMatrix.getStackInSlot(slot);

		if (!StackUtils.isIdenticalItem(stack, currentStack)) {
			craftMatrix.setInventorySlotContents(slot, stack);
			updateRecipe();
		}
	}

	private void updateMatrix() {
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			craftMatrix.setInventorySlotContents(i, tile.getCraftingInventory().getStackInSlot(i));
		}
	}

	private void updateRecipe() {
		tile.setRecipe(craftMatrix);
	}

	public static void clearRecipe() {
		sendRecipeClick(0, RecipeMemory.capacity);
	}

	public static void sendRecipeClick(int mouseButton, int recipeIndex) {
		PacketGuiSelect packet = new PacketGuiSelect(PacketId.GUI_SELECTION_CHANGE, mouseButton, recipeIndex);
		Proxies.net.sendToServer(packet);
	}

	@Override
	public void handleSelectionChange(EntityPlayer player, PacketGuiSelect packet) {
		if (packet.getPrimaryIndex() > 0) {
			tile.getMemory().toggleLock(player.worldObj, packet.getSecondaryIndex());
		} else {
			tile.chooseRecipe(packet.getSecondaryIndex());
			updateMatrix();
			updateRecipe();
		}
	}

	@Override
	public void setSelection(PacketGuiSelect packet) {
	}
}

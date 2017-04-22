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
package forestry.core.inventory;

import com.google.common.collect.ImmutableSet;
import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.IErrorSource;
import forestry.api.core.IErrorState;
import forestry.core.circuits.CircuitRecipe;
import forestry.core.circuits.CircuitRegistry;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.circuits.SolderManager;
import forestry.core.errors.EnumErrorCode;
import forestry.core.utils.datastructures.RevolvingList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemInventorySolderingIron extends ItemInventory implements IErrorSource {

	private final RevolvingList<ICircuitLayout> layouts = new RevolvingList<>(ChipsetManager.circuitRegistry.getRegisteredLayouts().values());

	private static final short inputCircuitBoardSlot = 0;
	private static final short finishedCircuitBoardSlot = 1;
	private static final short ingredientSlot1 = 2;
	private static final short ingredientSlotCount = 4;

	public ItemInventorySolderingIron(EntityPlayer player, ItemStack itemStack) {
		super(player, 6, itemStack);

		layouts.setCurrent(ChipsetManager.circuitRegistry.getDefaultLayout());
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	public ICircuitLayout getLayout() {
		return layouts.getCurrent();
	}

	public void setLayout(ICircuitLayout layout) {
		layouts.setCurrent(layout);
	}

	public void advanceLayout() {
		layouts.rotateRight();
	}

	public void regressLayout() {
		layouts.rotateLeft();
	}

	private ICircuit[] getCircuits(boolean doConsume) {

		ICircuit[] circuits = new ICircuit[ingredientSlotCount];

		for (short i = 0; i < ingredientSlotCount; i++) {
			ItemStack ingredient = getStackInSlot(ingredientSlot1 + i);
			if (!ingredient.isEmpty()) {
				CircuitRecipe recipe = SolderManager.getMatchingRecipe(layouts.getCurrent(), ingredient);
				if (recipe != null) {
					if (doConsume) {
						decrStackSize(ingredientSlot1 + i, recipe.getResource().getCount());
					}
					circuits[i] = recipe.getCircuit();
				}
			}
		}

		return circuits;
	}

	@Override
	public void onSlotClick(int slotIndex, EntityPlayer player) {
		if (layouts.getCurrent() == CircuitRegistry.DUMMY_LAYOUT) {
			return;
		}

		ItemStack inputCircuitBoard = getStackInSlot(inputCircuitBoardSlot);

		if (inputCircuitBoard.isEmpty() || inputCircuitBoard.getCount() > 1) {
			return;
		}
		if (!getStackInSlot(finishedCircuitBoardSlot).isEmpty()) {
			return;
		}

		// Need a chipset item
		if (!ChipsetManager.circuitRegistry.isChipset(inputCircuitBoard)) {
			return;
		}

		// Illegal type
		if (inputCircuitBoard.getItemDamage() < 0 || inputCircuitBoard.getItemDamage() >= EnumCircuitBoardType.values().length) {
			return;
		}

		EnumCircuitBoardType type = EnumCircuitBoardType.values()[inputCircuitBoard.getItemDamage()];
		if (getCircuitCount() != type.getSockets()) {
			return;
		}

		ICircuit[] circuits = getCircuits(true);

		ItemStack outputCircuitBoard = ItemCircuitBoard.createCircuitboard(type, layouts.getCurrent(), circuits);

		setInventorySlotContents(finishedCircuitBoardSlot, outputCircuitBoard);
		setInventorySlotContents(inputCircuitBoardSlot, ItemStack.EMPTY);
	}

	private int getCircuitCount() {
		ICircuit[] circuits = getCircuits(false);
		int count = 0;
		for (ICircuit circuit : circuits) {
			if (circuit != null) {
				count++;
			}
		}
		return count;
	}

	@Override
	public ImmutableSet<IErrorState> getErrorStates() {
		ImmutableSet.Builder<IErrorState> errorStates = ImmutableSet.builder();

		if (layouts.getCurrent() == CircuitRegistry.DUMMY_LAYOUT) {
			errorStates.add(EnumErrorCode.NO_CIRCUIT_LAYOUT);
		}

		ItemStack blankCircuitBoard = getStackInSlot(inputCircuitBoardSlot);

		if (blankCircuitBoard.isEmpty()) {
			errorStates.add(EnumErrorCode.NO_CIRCUIT_BOARD);
		} else {
			EnumCircuitBoardType type = EnumCircuitBoardType.values()[blankCircuitBoard.getItemDamage()];

			int circuitCount = 0;
			for (short i = 0; i < type.getSockets(); i++) {
				if (!getStackInSlot(ingredientSlot1 + i).isEmpty()) {
					circuitCount++;
				}
			}

			if (circuitCount != type.getSockets()) {
				errorStates.add(EnumErrorCode.CIRCUIT_MISMATCH);
			} else {
				int count = getCircuitCount();
				if (count != type.getSockets()) {
					errorStates.add(EnumErrorCode.NO_CIRCUIT_LAYOUT);
				}
			}
		}

		return errorStates.build();
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return false;
		}

		Item item = itemStack.getItem();
		if (slotIndex == inputCircuitBoardSlot) {
			return item instanceof ItemCircuitBoard;
		} else if (slotIndex >= ingredientSlot1 && slotIndex < ingredientSlot1 + ingredientSlotCount) {
			CircuitRecipe recipe = SolderManager.getMatchingRecipe(layouts.getCurrent(), itemStack);
			return recipe != null;
		}
		return false;
	}
}

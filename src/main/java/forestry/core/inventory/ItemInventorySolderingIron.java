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

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.IErrorSource;
import forestry.api.core.IErrorState;
import forestry.api.recipes.ISolderRecipe;
import forestry.core.circuits.CircuitRegistry;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.errors.EnumErrorCode;
import forestry.core.utils.datastructures.RevolvingList;

import java.util.Optional;

public class ItemInventorySolderingIron extends ItemInventory implements IErrorSource {

	private final RevolvingList<ICircuitLayout> layouts = new RevolvingList<>(ChipsetManager.circuitRegistry.getRegisteredLayouts().values());

	private static final short inputCircuitBoardSlot = 0;
	private static final short finishedCircuitBoardSlot = 1;
	private static final short ingredientSlot1 = 2;
	private static final short ingredientSlotCount = 4;

	public ItemInventorySolderingIron(Player player, ItemStack itemStack) {
		super(player, 6, itemStack);

		layouts.setCurrent(ChipsetManager.circuitRegistry.getDefaultLayout());
	}

	@Override
	public int getMaxStackSize() {
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
			ItemStack ingredient = getItem(ingredientSlot1 + i);
			if (!ingredient.isEmpty()) {
				Optional<ISolderRecipe> optionalRecipe = ChipsetManager.solderManager.getMatchingRecipe(player.level.getRecipeManager(), layouts.getCurrent(), ingredient);
				if (optionalRecipe.isPresent()) {
					ISolderRecipe recipe = optionalRecipe.get();
					if (doConsume) {
						removeItem(ingredientSlot1 + i, recipe.getResource().getCount());
					}
					circuits[i] = recipe.getCircuit();
				}
			}
		}

		return circuits;
	}

	@Override
	public void onSlotClick(int slotIndex, Player player) {
		if (layouts.getCurrent() == CircuitRegistry.DUMMY_LAYOUT) {
			return;
		}

		ItemStack inputCircuitBoard = getItem(inputCircuitBoardSlot);

		if (inputCircuitBoard.isEmpty() || inputCircuitBoard.getCount() > 1) {
			return;
		}
		if (!getItem(finishedCircuitBoardSlot).isEmpty()) {
			return;
		}

		// Need a chipset item
		if (!ChipsetManager.circuitRegistry.isChipset(inputCircuitBoard)) {
			return;
		}

		Item item = inputCircuitBoard.getItem();
		if (!(item instanceof ItemCircuitBoard circuitBoard)) {
			return;
		}

		EnumCircuitBoardType type = circuitBoard.getType();
		if (getCircuitCount() != type.getSockets()) {
			return;
		}

		ICircuit[] circuits = getCircuits(true);

		ItemStack outputCircuitBoard = ItemCircuitBoard.createCircuitboard(type, layouts.getCurrent(), circuits);

		setItem(finishedCircuitBoardSlot, outputCircuitBoard);
		setItem(inputCircuitBoardSlot, ItemStack.EMPTY);
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

		ItemStack blankCircuitBoard = getItem(inputCircuitBoardSlot);

		if (blankCircuitBoard.isEmpty()) {
			errorStates.add(EnumErrorCode.NO_CIRCUIT_BOARD);
		} else {
			Item item = blankCircuitBoard.getItem();
			if (!(item instanceof ItemCircuitBoard)) {
				return errorStates.build();
			}
			EnumCircuitBoardType type = ((ItemCircuitBoard) item).getType();

			int circuitCount = 0;
			for (short i = 0; i < type.getSockets(); i++) {
				if (!getItem(ingredientSlot1 + i).isEmpty()) {
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
			Optional<ISolderRecipe> recipe = ChipsetManager.solderManager.getMatchingRecipe(player.level.getRecipeManager(), layouts.getCurrent(), itemStack);
			return recipe.isPresent();
		}
		return false;
	}
}

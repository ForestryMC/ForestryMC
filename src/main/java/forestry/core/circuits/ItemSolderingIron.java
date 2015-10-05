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
package forestry.core.circuits;

import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.circuits.ISolderManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorSource;
import forestry.api.core.IErrorState;
import forestry.core.EnumErrorCode;
import forestry.core.inventory.ItemInventory;
import forestry.core.items.ItemForestry;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.RevolvingList;

public class ItemSolderingIron extends ItemForestry implements ISolderingIron {

	public static class CircuitRecipe {

		private final ICircuitLayout layout;
		private final ItemStack resource;
		public final ICircuit circuit;

		public CircuitRecipe(ICircuitLayout layout, ItemStack resource, ICircuit circuit) {
			this.resource = resource;
			this.layout = layout;
			this.circuit = circuit;
		}

		public boolean matches(ICircuitLayout layout, ItemStack itemstack) {
			if (!this.layout.getUID().equals(layout.getUID())) {
				return false;
			}

			return itemstack.isItemEqual(resource);
		}
	}

	public static class SolderManager implements ISolderManager {

		public static final ArrayList<CircuitRecipe> recipes = new ArrayList<CircuitRecipe>();

		@Override
		public void addRecipe(ICircuitLayout layout, ItemStack resource, ICircuit circuit) {
			if (layout == null) {
				throw new IllegalArgumentException("layout may not be null");
			}
			if (resource == null) {
				throw new IllegalArgumentException("resource may not be null");
			}
			if (circuit == null) {
				throw new IllegalArgumentException("circuit may not be null");
			}
			recipes.add(new CircuitRecipe(layout, resource, circuit));
		}

		public static ICircuit getCircuit(ICircuitLayout layout, ItemStack resource) {
			CircuitRecipe circuitRecipe = getMatchingRecipe(layout, resource);
			if (circuitRecipe == null) {
				return null;
			}
			return circuitRecipe.circuit;
		}

		public static CircuitRecipe getMatchingRecipe(ICircuitLayout layout, ItemStack resource) {
			if (layout == null || resource == null) {
				return null;
			}

			for (CircuitRecipe recipe : recipes) {
				if (recipe.matches(layout, resource)) {
					return recipe;
				}
			}

			return null;
		}
	}

	// / INVENTORY MANAGMENT
	public static class SolderingInventory extends ItemInventory implements IErrorSource {

		private final RevolvingList<ICircuitLayout> layouts = new RevolvingList<ICircuitLayout>(ChipsetManager.circuitRegistry.getRegisteredLayouts().values());

		private static final short blankSlot = 0;
		private static final short finishedSlot = 1;
		private static final short ingredientSlot1 = 2;
		private static final short ingredientSlotCount = 4;

		public SolderingInventory(EntityPlayer player, ItemStack itemStack) {
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

		public void setLayout(String uid) {
			layouts.setCurrent(ChipsetManager.circuitRegistry.getLayout(uid));
		}

		public void advanceLayout() {
			layouts.rotateRight();
		}

		public void regressLayout() {
			layouts.rotateLeft();
		}

		private Collection<ICircuit> getCircuits(EnumCircuitBoardType type, boolean doConsume) {

			ArrayList<ICircuit> circuits = new ArrayList<ICircuit>();

			for (short i = 0; i < type.sockets; i++) {
				ItemStack ingredient = getStackInSlot(ingredientSlot1 + i);
				if (ingredient == null) {
					continue;
				}

				CircuitRecipe recipe = SolderManager.getMatchingRecipe(layouts.getCurrent(), ingredient);
				if (recipe == null) {
					continue;
				}

				// / Make sure we don't exceed this circuits limit per chipset
				if (getCount(recipe.circuit, circuits) >= recipe.circuit.getLimit()) {
					continue;
				}

				if (doConsume) {
					decrStackSize(ingredientSlot1 + i, recipe.resource.stackSize);
				}
				circuits.add(recipe.circuit);
			}

			return circuits;
		}

		@Override
		public void onSlotClick(EntityPlayer player) {
			if (layouts.getCurrent() == CircuitRegistry.DUMMY_LAYOUT) {
				return;
			}

			ItemStack blank = getStackInSlot(blankSlot);
			// Requires blank slot
			if (blank == null) {
				return;
			}
			if (blank.stackSize > 1) {
				return;
			}
			if (getStackInSlot(finishedSlot) != null) {
				return;
			}

			// Need a chipset item
			if (!ChipsetManager.circuitRegistry.isChipset(blank)) {
				return;
			}

			// Illegal type
			if (blank.getItemDamage() < 0 || blank.getItemDamage() >= EnumCircuitBoardType.values().length) {
				return;
			}

			EnumCircuitBoardType type = EnumCircuitBoardType.values()[blank.getItemDamage()];
			Collection<ICircuit> circuits = getCircuits(type, false);

			if (circuits.size() != type.sockets) {
				return;
			}

			circuits = getCircuits(type, true);

			ICircuit[] circuitsArray = circuits.toArray(new ICircuit[circuits.size()]);
			ItemStack circuitBoard = ItemCircuitBoard.createCircuitboard(type, layouts.getCurrent(), circuitsArray);

			setInventorySlotContents(finishedSlot, circuitBoard);
			setInventorySlotContents(blankSlot, null);
		}

		public static int getCount(ICircuit circuit, ArrayList<ICircuit> circuits) {
			int count = 0;
			for (ICircuit other : circuits) {
				if (other.getUID().equals(circuit.getUID())) {
					count++;
				}
			}
			return count;
		}

		@Override
		public ImmutableSet<IErrorState> getErrorStates() {
			ImmutableSet.Builder<IErrorState> errorStates = ImmutableSet.builder();

			if (layouts.getCurrent() == CircuitRegistry.DUMMY_LAYOUT) {
				errorStates.add(EnumErrorCode.NOCIRCUITLAYOUT);
			}

			ItemStack blankCircuitBoard = getStackInSlot(blankSlot);

			if (blankCircuitBoard == null) {
				errorStates.add(EnumErrorCode.NOCIRCUITBOARD);
			} else {
				EnumCircuitBoardType type = EnumCircuitBoardType.values()[blankCircuitBoard.getItemDamage()];

				int circuitCount = 0;
				for (short i = 0; i < type.sockets; i++) {
					if (getStackInSlot(ingredientSlot1 + i) != null) {
						circuitCount++;
					}
				}

				if (circuitCount != type.sockets) {
					errorStates.add(EnumErrorCode.CIRCUITMISMATCH);
				} else {
					Collection<ICircuit> circuits = getCircuits(type, false);
					if (circuits.size() != type.sockets) {
						errorStates.add(EnumErrorCode.NOCIRCUITLAYOUT);
					}
				}
			}

			return errorStates.build();
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (itemStack == null) {
				return false;
			}

			Item item = itemStack.getItem();
			if (slotIndex == blankSlot) {
				return item instanceof ItemCircuitBoard;
			} else if (slotIndex >= ingredientSlot1 && slotIndex < ingredientSlot1 + ingredientSlotCount) {
				CircuitRecipe recipe = SolderManager.getMatchingRecipe(layouts.getCurrent(), itemStack);
				return recipe != null;
			}
			return false;
		}
	}

	public ItemSolderingIron() {
		super();
		setMaxStackSize(1);
		setMaxDamage(5);
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (Proxies.common.isSimulating(world)) {
			entityplayer.openGui(ForestryAPI.instance, GuiId.SolderingIronGUI.ordinal(), world, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);
		}

		return itemstack;
	}
}

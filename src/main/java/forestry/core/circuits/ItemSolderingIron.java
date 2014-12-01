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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.circuits.ISolderManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.EnumErrorCode;
import forestry.core.interfaces.IErrorSource;
import forestry.core.items.ItemForestry;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.inventory.ItemInventory;

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
			if (!this.layout.getUID().equals(layout.getUID()))
				return false;

			return itemstack.isItemEqual(resource);
		}
	}

	public static class SolderManager implements ISolderManager {
		public static final ArrayList<CircuitRecipe> recipes = new ArrayList<CircuitRecipe>();

		@Override
		public void addRecipe(ICircuitLayout layout, ItemStack resource, ICircuit circuit) {
			if (layout == null)
				throw new IllegalArgumentException("layout may not be null");
			if (resource == null)
				throw new IllegalArgumentException("resource may not be null");
			if (circuit == null)
				throw new IllegalArgumentException("circuit may not be null");
			recipes.add(new CircuitRecipe(layout, resource, circuit));
		}

		public static CircuitRecipe getMatchingRecipe(ICircuitLayout layout, ItemStack resource) {
			if (layout == null || resource == null)
				return null;

			for (CircuitRecipe recipe : recipes)
				if (recipe.matches(layout, resource))
					return recipe;

			return null;
		}
	}

	// / INVENTORY MANAGMENT
	public static class SolderingInventory extends ItemInventory implements IErrorSource {

		private ICircuitLayout layout;
		private EnumErrorCode errorState;

		private final short blankSlot = 0;
		private final short finishedSlot = 1;
		private final short ingredientSlot1 = 2;

		public SolderingInventory() {
			super(ItemSolderingIron.class, 6);
			layout = ChipsetManager.circuitRegistry.getDefaultLayout();
		}

		public SolderingInventory(ItemStack itemstack) {
			super(ItemSolderingIron.class, 6, itemstack);
			layout = ChipsetManager.circuitRegistry.getDefaultLayout();
		}

		public ICircuitLayout getLayout() {
			return this.layout;
		}

		public void setLayout(String uid) {
			this.layout = ChipsetManager.circuitRegistry.getLayout(uid);
		}

		public void advanceLayout() {
			Iterator<Entry<String, ICircuitLayout>> it = ChipsetManager.circuitRegistry.getRegisteredLayouts().entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, ICircuitLayout> entry = it.next();
				if (entry.getKey().equals(layout.getUID())) {

					if (it.hasNext())
						layout = it.next().getValue();
					else
						layout = ChipsetManager.circuitRegistry.getRegisteredLayouts().entrySet().iterator().next().getValue();

					break;
				}
			}
		}

		public void regressLayout() {
			Iterator<Entry<String, ICircuitLayout>> it = ChipsetManager.circuitRegistry.getRegisteredLayouts().entrySet().iterator();

			ICircuitLayout previous = null;
			while (it.hasNext()) {
				Entry<String, ICircuitLayout> entry = it.next();
				if (entry.getKey().equals(layout.getUID())) {

					if (previous != null)
						layout = previous;
					else
						while (it.hasNext())
							layout = it.next().getValue();

					break;
				}

				previous = entry.getValue();
			}
		}

		private Collection<ICircuit> getCircuits(EnumCircuitBoardType type, boolean doConsume) {

			ArrayList<ICircuit> circuits = new ArrayList<ICircuit>();

			for (short i = 0; i < type.sockets; i++) {
				ItemStack ingredient = inventoryStacks[ingredientSlot1 + i];
				if (ingredient == null)
					continue;

				CircuitRecipe recipe = SolderManager.getMatchingRecipe(layout, ingredient);
				if (recipe == null)
					continue;

				// / Make sure we don't exceed this circuits limit per chipset
				if (getCount(recipe.circuit, circuits) >= recipe.circuit.getLimit())
					continue;

				if (doConsume)
					decrStackSize(ingredientSlot1 + i, recipe.resource.stackSize);
				circuits.add(recipe.circuit);
			}

			return circuits;
		}

		public void trySolder() {

			// Requires blank slot
			if (inventoryStacks[blankSlot] == null)
				return;
			if (inventoryStacks[blankSlot].stackSize > 1)
				return;
			if (inventoryStacks[finishedSlot] != null)
				return;

			ItemStack blank = inventoryStacks[blankSlot];
			// Need a chipset item
			if (!ChipsetManager.circuitRegistry.isChipset(blank))
				return;

			// Illegal type
			if (blank.getItemDamage() < 0 || blank.getItemDamage() >= EnumCircuitBoardType.values().length)
				return;

			EnumCircuitBoardType type = EnumCircuitBoardType.values()[blank.getItemDamage()];
			Collection<ICircuit> circuits = getCircuits(type, false);

			if (circuits.size() <= 0)
				return;
			else if (circuits.size() != type.sockets) {
				errorState = EnumErrorCode.CIRCUITMISMATCH;
				return;
			}

			circuits = getCircuits(type, true);
			inventoryStacks[finishedSlot] = ItemCircuitBoard.createCircuitboard(type, layout, circuits.toArray(new ICircuit[circuits.size()]));
			inventoryStacks[blankSlot] = null;
		}

		public int getCount(ICircuit circuit, ArrayList<ICircuit> circuits) {
			int count = 0;
			for (ICircuit other : circuits)
				if (other.getUID().equals(circuit.getUID()))
					count++;
			return count;
		}

		@Override
		public void readFromNBT(NBTTagCompound nbttagcompound) {
		}

		@Override
		public void writeToNBT(NBTTagCompound nbttagcompound) {
		}

		@Override
		public void markDirty() {
			errorState = EnumErrorCode.OK;
			trySolder();
		}

		@Override
		public boolean throwsErrors() {
			return true;
		}

		@Override
		public EnumErrorCode getErrorState() {
			if (inventoryStacks[blankSlot] == null)
				return EnumErrorCode.NOCIRCUITBOARD;
			if (inventoryStacks[blankSlot].stackSize > 1)
				return EnumErrorCode.WRONGSTACKSIZE;
			if (errorState != EnumErrorCode.OK)
				return errorState;

			return EnumErrorCode.OK;
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
		if (Proxies.common.isSimulating(world))
			entityplayer.openGui(ForestryAPI.instance, GuiId.SolderingIronGUI.ordinal(), world, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);

		return itemstack;
	}
}

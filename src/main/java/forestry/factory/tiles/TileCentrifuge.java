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
package forestry.factory.tiles;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitSocketType;
import forestry.api.core.IErrorLogic;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.core.circuits.ISocketable;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.tiles.TilePowered;
import forestry.core.utils.InventoryUtil;
import forestry.factory.gui.ContainerCentrifuge;
import forestry.factory.gui.GuiCentrifuge;
import forestry.factory.inventory.InventoryCentrifuge;
import forestry.factory.recipes.CentrifugeRecipeManager;
import forestry.factory.triggers.FactoryTriggers;

import buildcraft.api.statements.ITriggerExternal;

public class TileCentrifuge extends TilePowered implements ISocketable, ISidedInventory {
	private static final int TICKS_PER_RECIPE_TIME = 1;
	private static final int ENERGY_PER_WORK_CYCLE = 3200;
	private static final int ENERGY_PER_RECIPE_TIME = ENERGY_PER_WORK_CYCLE / 20;

	private final InventoryAdapter sockets = new InventoryAdapter(1, "sockets");

	private ICentrifugeRecipe currentRecipe;

	private final Stack<ItemStack> pendingProducts = new Stack<>();

	public TileCentrifuge() {
		super("centrifuge", 800, Constants.MACHINE_MAX_ENERGY);
		setInternalInventory(new InventoryCentrifuge(this));
	}

	/* LOADING & SAVING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		sockets.writeToNBT(nbttagcompound);

		NBTTagList nbttaglist = new NBTTagList();
		ItemStack[] offspring = pendingProducts.toArray(new ItemStack[pendingProducts.size()]);
		for (int i = 0; i < offspring.length; i++) {
			if (offspring[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				offspring[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbttagcompound.setTag("PendingProducts", nbttaglist);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		NBTTagList nbttaglist = nbttagcompound.getTagList("PendingProducts", 10);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			pendingProducts.add(ItemStack.loadItemStackFromNBT(nbttagcompound1));
		}
		sockets.readFromNBT(nbttagcompound);

		ItemStack chip = sockets.getStackInSlot(0);
		if (chip != null) {
			ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(chip);
			if (chipset != null) {
				chipset.onLoad(this);
			}
		}
	}

	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		super.writeGuiData(data);
		sockets.writeData(data);
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		super.readGuiData(data);
		sockets.readData(data);
	}

	@Override
	public boolean workCycle() {

		// If we add pending products, we skip to the next work cycle.
		if (tryAddPending()) {
			return false;
		}

		if (!pendingProducts.isEmpty()) {
			return false;
		}

		if (currentRecipe == null) {
			return false;
		}

		// We are done, add products to queue
		Collection<ItemStack> products = currentRecipe.getProducts(worldObj.rand);
		pendingProducts.addAll(products);

		getInternalInventory().decrStackSize(InventoryCentrifuge.SLOT_RESOURCE, 1);

		tryAddPending();
		return true;
	}

	private void checkRecipe() {
		ItemStack resource = getStackInSlot(InventoryCentrifuge.SLOT_RESOURCE);
		ICentrifugeRecipe matchingRecipe = CentrifugeRecipeManager.findMatchingRecipe(resource);

		if (currentRecipe != matchingRecipe) {
			currentRecipe = matchingRecipe;
			if (currentRecipe != null) {
				int recipeTime = currentRecipe.getProcessingTime();
				setTicksPerWorkCycle(recipeTime * TICKS_PER_RECIPE_TIME);
				setEnergyPerWorkCycle(recipeTime * ENERGY_PER_RECIPE_TIME);
			}
		}
	}

	private boolean tryAddPending() {
		if (pendingProducts.isEmpty()) {
			return false;
		}

		ItemStack next = pendingProducts.peek();

		boolean added = InventoryUtil.tryAddStack(this, next, InventoryCentrifuge.SLOT_PRODUCT_1, InventoryCentrifuge.SLOT_PRODUCT_COUNT, true);

		if (added) {
			pendingProducts.pop();
		}

		getErrorLogic().setCondition(!added, EnumErrorCode.NO_SPACE_INVENTORY);
		return added;
	}

	@Override
	public boolean hasResourcesMin(float percentage) {
		IInventoryAdapter inventory = getInternalInventory();
		if (inventory.getStackInSlot(InventoryCentrifuge.SLOT_RESOURCE) == null) {
			return false;
		}

		return ((float) inventory.getStackInSlot(InventoryCentrifuge.SLOT_RESOURCE).stackSize / (float) inventory.getStackInSlot(InventoryCentrifuge.SLOT_RESOURCE).getMaxStackSize()) > percentage;
	}

	@Override
	public boolean hasWork() {
		checkRecipe();

		boolean hasResource = getStackInSlot(InventoryCentrifuge.SLOT_RESOURCE) != null;

		IErrorLogic errorLogic = getErrorLogic();
		errorLogic.setCondition(!hasResource, EnumErrorCode.NO_RESOURCE);

		return hasResource;
	}

	/* ITRIGGERPROVIDER */
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		LinkedList<ITriggerExternal> res = new LinkedList<>();
		res.add(FactoryTriggers.lowResource25);
		res.add(FactoryTriggers.lowResource10);
		return res;
	}

	/* ISocketable */
	@Override
	public int getSocketCount() {
		return sockets.getSizeInventory();
	}

	@Override
	public ItemStack getSocket(int slot) {
		return sockets.getStackInSlot(slot);
	}

	@Override
	public void setSocket(int slot, ItemStack stack) {

		if (stack != null && !ChipsetManager.circuitRegistry.isChipset(stack)) {
			return;
		}

		// Dispose correctly of old chipsets
		if (sockets.getStackInSlot(slot) != null) {
			if (ChipsetManager.circuitRegistry.isChipset(sockets.getStackInSlot(slot))) {
				ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(sockets.getStackInSlot(slot));
				if (chipset != null) {
					chipset.onRemoval(this);
				}
			}
		}

		sockets.setInventorySlotContents(slot, stack);
		if (stack == null) {
			return;
		}

		ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(stack);
		if (chipset != null) {
			chipset.onInsertion(this);
		}
	}

	@Override
	public ICircuitSocketType getSocketType() {
		return CircuitSocketType.MACHINE;
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiCentrifuge(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerCentrifuge(player.inventory, this);
	}
}

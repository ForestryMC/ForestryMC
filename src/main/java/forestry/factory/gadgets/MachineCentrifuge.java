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
package forestry.factory.gadgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;

import forestry.api.core.ForestryAPI;
import forestry.api.recipes.ICentrifugeManager;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileBase;
import forestry.core.gadgets.TilePowered;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.GuiId;
import forestry.factory.triggers.FactoryTriggers;

import buildcraft.api.statements.ITriggerExternal;

public class MachineCentrifuge extends TilePowered implements ISidedInventory {

	/* CONSTANTS */
	public static final int SLOT_RESOURCE = 0;
	public static final int SLOT_PRODUCT_1 = 1;

	/* RECIPE MANAGMENT */
	public static class Recipe {

		public final int timePerItem;
		public final ItemStack resource;
		public final HashMap<ItemStack, Integer> products;

		public Recipe(int timePerItem, ItemStack resource, HashMap<ItemStack, Integer> products) {
			this.timePerItem = timePerItem;
			this.resource = resource;
			this.products = products;

			for (ItemStack item : products.keySet()) {
				if (item == null)
					throw new IllegalArgumentException("Tried to register a null product of " + resource);
			}
		}

		public boolean matches(ItemStack res) {
			if (res == null && resource == null)
				return true;
			else if (res == null || resource == null)
				return false;
			else
				return resource.isItemEqual(res);
		}
	}

	public static class RecipeManager implements ICentrifugeManager {

		public static final ArrayList<MachineCentrifuge.Recipe> recipes = new ArrayList<MachineCentrifuge.Recipe>();

		@Override
		public void addRecipe(int timePerItem, ItemStack resource, HashMap<ItemStack, Integer> products) {
			recipes.add(new Recipe(timePerItem, resource, products));
		}

		@Override
		public void addRecipe(int timePerItem, ItemStack resource, ItemStack[] produce, int[] chances) {
			HashMap<ItemStack, Integer> products = new HashMap<ItemStack, Integer>();

			int i = 0;
			for (ItemStack prod : produce) {
				products.put(prod, chances[i]);
				i++;
			}

			addRecipe(timePerItem, resource, products);
		}

		@Override
		public void addRecipe(int timePerItem, ItemStack resource, ItemStack primary, ItemStack secondary, int chance) {
			HashMap<ItemStack, Integer> products = new HashMap<ItemStack, Integer>();
			products.put(primary, 100);
			if (secondary != null)
				products.put(secondary, chance);
			addRecipe(timePerItem, resource, products);
		}

		@Override
		public void addRecipe(int timePerItem, ItemStack resource, ItemStack primary) {
			HashMap<ItemStack, Integer> products = new HashMap<ItemStack, Integer>();
			products.put(primary, 100);
			addRecipe(timePerItem, resource, products);
		}

		public static Recipe findMatchingRecipe(ItemStack item) {
			for (Recipe recipe : recipes) {
				if (recipe.matches(item))
					return recipe;
			}
			return null;
		}

		@Override
		public Map<Object[], Object[]> getRecipes() {
			HashMap<Object[], Object[]> recipeList = new HashMap<Object[], Object[]>();

			for (Recipe recipe : recipes) {
				Set<ItemStack> productsKeys = recipe.products.keySet();
				recipeList.put(new Object[]{recipe.resource}, productsKeys.toArray(new ItemStack[productsKeys.size()]));
			}

			return recipeList;
		}
	}

	/* MEMBER */
	public MachineCentrifuge.Recipe currentRecipe;

	private final Stack<ItemStack> pendingProducts = new Stack<ItemStack>();
	private int productionTime;
	private int timePerItem;

	public MachineCentrifuge() {
		super(800, 40, Defaults.MACHINE_MAX_ENERGY);
		setInternalInventory(new TileInventoryAdapter(this, 10, "Items"));
		setHints(Config.hints.get("centrifuge"));
	}

	@Override
	public String getInventoryName() {
		return getUnlocalizedName();
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.CentrifugeGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	/* LOADING & SAVING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("ProductionTime", productionTime);
		nbttagcompound.setInteger("TimePerItem", timePerItem);

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

		productionTime = nbttagcompound.getInteger("ProductionTime");
		timePerItem = nbttagcompound.getInteger("TimePerItem");

		NBTTagList nbttaglist = nbttagcompound.getTagList("PendingProducts", 10);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			pendingProducts.add(ItemStack.loadItemStackFromNBT(nbttagcompound1));
		}

		checkRecipe();
	}

	@Override
	public void updateServerSide() {

		if (worldObj.getTotalWorldTime() % 20 * 10 != 0)
			return;

		// Check and reset recipe if necessary
		checkRecipe();
		if (getErrorState() == EnumErrorCode.NORECIPE && currentRecipe != null)
			setErrorState(EnumErrorCode.OK);

		if (energyManager.getTotalEnergyStored() == 0) {
			setErrorState(EnumErrorCode.NOPOWER);
		}
	}

	@Override
	public boolean workCycle() {

		checkRecipe();

		// If we add pending products, we skip to the next work cycle.
		if (tryAddPending())
			return false;

		if (!pendingProducts.isEmpty())
			return false;

		// Continue work if nothing needs to be added
		if (productionTime <= 0)
			return false;

		if (currentRecipe == null)
			return false;

		productionTime--;
		// Still not done, return
		if (productionTime > 0) {
			setErrorState(EnumErrorCode.OK);
			return true;
		}

		// We are done, add products to queue
		for (Map.Entry<ItemStack, Integer> entry : currentRecipe.products.entrySet()) {
			if (entry.getValue() >= 100)
				pendingProducts.push(entry.getKey().copy());
			else if (worldObj.rand.nextInt(100) < entry.getValue())
				pendingProducts.push(entry.getKey().copy());
		}

		getInternalInventory().decrStackSize(SLOT_RESOURCE, 1);
		checkRecipe();
		resetRecipe();

		tryAddPending();
		return true;
	}

	public void checkRecipe() {
		Recipe sameRec = RecipeManager.findMatchingRecipe(getInternalInventory().getStackInSlot(SLOT_RESOURCE));

		if (sameRec == null)
			setErrorState(EnumErrorCode.NORECIPE);

		if (currentRecipe != sameRec) {
			currentRecipe = sameRec;
			resetRecipe();
		}
	}

	private void resetRecipe() {
		if (currentRecipe == null) {
			productionTime = 0;
			timePerItem = 0;
			return;
		}

		productionTime = currentRecipe.timePerItem;
		timePerItem = currentRecipe.timePerItem;
	}

	private boolean tryAddPending() {
		if (pendingProducts.isEmpty())
			return false;

		ItemStack next = pendingProducts.peek();
		if (addProduct(next, true)) {
			pendingProducts.pop();
			return true;
		}

		setErrorState(EnumErrorCode.NOSPACE);
		return false;
	}

	private boolean addProduct(ItemStack product, boolean all) {
		return getInternalInventory().tryAddStack(product, 1, getSizeInventory() - 1, all);
	}

	@Override
	public boolean isWorking() {
		return currentRecipe != null;
	}

	@Override
	public boolean hasResourcesMin(float percentage) {
		TileInventoryAdapter inventory = getInternalInventory();
		if (inventory.getStackInSlot(SLOT_RESOURCE) == null)
			return false;

		return ((float) inventory.getStackInSlot(SLOT_RESOURCE).stackSize / (float) inventory.getStackInSlot(SLOT_RESOURCE).getMaxStackSize()) > percentage;
	}

	@Override
	public boolean hasWork() {
		return currentRecipe != null;
	}

	public int getProgressScaled(int i) {
		if (timePerItem == 0)
			return i;

		return (productionTime * i) / timePerItem;
	}

	/* GUI */
	public void getGUINetworkData(int i, int j) {
		switch (i) {
		case 0:
			productionTime = j;
			break;
		case 1:
			timePerItem = j;
			break;
		}
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, productionTime);
		iCrafting.sendProgressBarUpdate(container, 1, timePerItem);
	}

	/* IINVENTORY */

	@Override
	public int getSizeInventory() {
		return getInternalInventory().getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return getInternalInventory().getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return getInternalInventory().decrStackSize(i, j);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		getInternalInventory().setInventorySlotContents(i, itemstack);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getInternalInventory().getStackInSlotOnClosing(slot);
	}

	@Override
	public int getInventoryStackLimit() {
		return getInternalInventory().getInventoryStackLimit();
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return getInternalInventory().isUseableByPlayer(player);
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
		if (!getInternalInventory().isItemValidForSlot(slotIndex, itemstack))
			return false;
		return slotIndex == SLOT_RESOURCE && RecipeManager.findMatchingRecipe(itemstack) != null;
	}

	/* ISIDEDINVENTORY */
	@Override
	public boolean canInsertItem(int slotIndex, ItemStack itemstack, int side) {
		return isItemValidForSlot(slotIndex, itemstack);
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
		if (!getInternalInventory().canExtractItem(slotIndex, itemstack, side))
			return false;
		return slotIndex >= SLOT_PRODUCT_1 && slotIndex < SLOT_PRODUCT_1 + 9;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return getInternalInventory().getAccessibleSlotsFromSide(side);
	}

	/* ITRIGGERPROVIDER */
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		LinkedList<ITriggerExternal> res = new LinkedList<ITriggerExternal>();
		res.add(FactoryTriggers.lowResource25);
		res.add(FactoryTriggers.lowResource10);
		return res;
	}
}

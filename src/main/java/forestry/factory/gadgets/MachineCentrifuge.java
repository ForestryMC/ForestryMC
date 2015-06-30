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

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileBase;
import forestry.core.gadgets.TilePowered;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.GuiId;
import forestry.core.utils.GuiUtil;
import forestry.core.utils.StackUtils;
import forestry.factory.triggers.FactoryTriggers;

import buildcraft.api.statements.ITriggerExternal;

public class MachineCentrifuge extends TilePowered implements ISidedInventory {

	/* CONSTANTS */
	public static final int SLOT_RESOURCE = 0;
	public static final int SLOT_PRODUCT_1 = 1;
	public static final int SLOT_PRODUCT_COUNT = 9;

	/* MEMBER */
	public ICentrifugeRecipe currentRecipe;

	private final Stack<ItemStack> pendingProducts = new Stack<ItemStack>();
	private int productionTime;
	private int timePerItem;

	public MachineCentrifuge() {
		super(800, 40, Defaults.MACHINE_MAX_ENERGY);
		setInternalInventory(new CentrifugeInventoryAdapter(this));
		setHints(Config.hints.get("centrifuge"));
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
		super.updateServerSide();

		if (!updateOnInterval(20)) {
			return;
		}

		// Check and reset recipe if necessary
		checkRecipe();
	}

	@Override
	public boolean workCycle() {

		checkRecipe();

		// If we add pending products, we skip to the next work cycle.
		if (tryAddPending()) {
			return false;
		}

		if (!pendingProducts.isEmpty()) {
			return false;
		}

		// Continue work if nothing needs to be added
		if (productionTime <= 0) {
			return false;
		}

		if (currentRecipe == null) {
			return false;
		}

		productionTime--;
		// Still not done, return
		if (productionTime > 0) {
			return true;
		}

		// We are done, add products to queue
		Collection<ItemStack> products = currentRecipe.getProducts(worldObj.rand);
		pendingProducts.addAll(products);

		getInternalInventory().decrStackSize(SLOT_RESOURCE, 1);
		checkRecipe();
		resetRecipeTimes();

		tryAddPending();
		return true;
	}

	public void checkRecipe() {
		ICentrifugeRecipe machingRecipe = RecipeManager.findMatchingRecipe(getInternalInventory().getStackInSlot(SLOT_RESOURCE));

		if (currentRecipe != machingRecipe) {
			currentRecipe = machingRecipe;
			resetRecipeTimes();
		}

		getErrorLogic().setCondition(currentRecipe == null, EnumErrorCode.NORECIPE);
	}

	private void resetRecipeTimes() {
		if (currentRecipe == null) {
			productionTime = 0;
			timePerItem = 0;
			return;
		}

		productionTime = currentRecipe.getProcessingTime();
		timePerItem = currentRecipe.getProcessingTime();
	}

	private boolean tryAddPending() {
		if (pendingProducts.isEmpty()) {
			return false;
		}

		ItemStack next = pendingProducts.peek();

		boolean added = InvTools.tryAddStack(this, next, SLOT_PRODUCT_1, SLOT_PRODUCT_COUNT, true);

		if (added) {
			pendingProducts.pop();
		}

		getErrorLogic().setCondition(!added, EnumErrorCode.NOSPACE);
		return added;
	}

	@Override
	public boolean isWorking() {
		return currentRecipe != null;
	}

	@Override
	public boolean hasResourcesMin(float percentage) {
		IInventoryAdapter inventory = getInternalInventory();
		if (inventory.getStackInSlot(SLOT_RESOURCE) == null) {
			return false;
		}

		return ((float) inventory.getStackInSlot(SLOT_RESOURCE).stackSize / (float) inventory.getStackInSlot(SLOT_RESOURCE).getMaxStackSize()) > percentage;
	}

	@Override
	public boolean hasWork() {
		return currentRecipe != null;
	}

	public int getProgressScaled(int i) {
		if (timePerItem == 0) {
			return i;
		}

		return ((timePerItem - productionTime) * i) / timePerItem;
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

	/* ITRIGGERPROVIDER */
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		LinkedList<ITriggerExternal> res = new LinkedList<ITriggerExternal>();
		res.add(FactoryTriggers.lowResource25);
		res.add(FactoryTriggers.lowResource10);
		return res;
	}

	private static class CentrifugeInventoryAdapter extends TileInventoryAdapter<MachineCentrifuge> {
		public CentrifugeInventoryAdapter(MachineCentrifuge centrifuge) {
			super(centrifuge, 10, "Items");
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			return slotIndex == SLOT_RESOURCE && RecipeManager.findMatchingRecipe(itemStack) != null;
		}

		@Override
		public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
			return GuiUtil.isIndexInRange(slotIndex, SLOT_PRODUCT_1, SLOT_PRODUCT_COUNT);
		}
	}

	/* RECIPE MANAGEMENT */
	public static class CentrifugeRecipe implements ICentrifugeRecipe {

		private final int processingTime;
		private final ItemStack input;
		private final Map<ItemStack, Float> outputs;

		public CentrifugeRecipe(int processingTime, ItemStack input, Map<ItemStack, Float> outputs) {
			this.processingTime = processingTime;
			this.input = input;
			this.outputs = outputs;

			for (ItemStack item : outputs.keySet()) {
				if (item == null) {
					throw new IllegalArgumentException("Tried to register a null product of " + input);
				}
			}
		}

		@Override
		public ItemStack getInput() {
			return input;
		}

		@Override
		public int getProcessingTime() {
			return processingTime;
		}

		@Override
		public Collection<ItemStack> getProducts(Random random) {
			List<ItemStack> products = new ArrayList<ItemStack>();

			for (Map.Entry<ItemStack, Float> entry : this.outputs.entrySet()) {
				float probability = entry.getValue();

				if (probability >= 1.0) {
					products.add(entry.getKey().copy());
				} else if (random.nextFloat() < probability) {
					products.add(entry.getKey().copy());
				}
			}

			return products;
		}

		@Override
		public Map<ItemStack, Float> getAllProducts() {
			return ImmutableMap.copyOf(outputs);
		}
	}

	public static class RecipeManager implements ICentrifugeManager {

		public static final List<ICentrifugeRecipe> recipes = new ArrayList<ICentrifugeRecipe>();

		@Override
		public void addRecipe(ICentrifugeRecipe recipe) {
			recipes.add(recipe);
		}

		@Override
		public void addRecipe(int timePerItem, ItemStack resource, Map<ItemStack, Float> products) {
			ICentrifugeRecipe recipe = new CentrifugeRecipe(timePerItem, resource, products);
			addRecipe(recipe);
		}

		public static ICentrifugeRecipe findMatchingRecipe(ItemStack item) {
			for (ICentrifugeRecipe recipe : recipes) {
				ItemStack recipeInput = recipe.getInput();
				if (StackUtils.isCraftingEquivalent(recipeInput, item)) {
					return recipe;
				}
			}
			return null;
		}

		@Override
		public Map<Object[], Object[]> getRecipes() {
			HashMap<Object[], Object[]> recipeList = new HashMap<Object[], Object[]>();

			for (ICentrifugeRecipe recipe : recipes) {
				Set<ItemStack> productsKeys = recipe.getAllProducts().keySet();
				recipeList.put(new Object[]{recipe.getInput()}, productsKeys.toArray(new ItemStack[productsKeys.size()]));
			}

			return recipeList;
		}
	}

}

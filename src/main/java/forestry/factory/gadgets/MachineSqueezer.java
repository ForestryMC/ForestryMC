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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.api.recipes.ISqueezerManager;
import forestry.core.EnumErrorCode;
import forestry.core.circuits.ISocketable;
import forestry.core.circuits.ISpeedUpgradable;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.gadgets.TilePowered;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;

public class MachineSqueezer extends TilePowered implements ISocketable, ISidedInventory, ILiquidTankContainer, ISpeedUpgradable {

	private static final int TICKS_PER_RECIPE_TIME = 4;
	private static final int ENERGY_PER_RECIPE_TIME = 200;

	private final InventoryAdapter sockets = new InventoryAdapter(1, "sockets");

	/* MEMBER */
	private final TankManager tankManager;
	private final FilteredTank productTank;
	private final SqueezerInventory inventory;

	private Recipe currentRecipe;

	public MachineSqueezer() {
		super(1100, 4000, 2000);
		this.inventory = new SqueezerInventory(this);
		setInternalInventory(this.inventory);
		setHints(Config.hints.get("squeezer"));
		this.productTank = new FilteredTank(Defaults.PROCESSOR_TANK_CAPACITY, RecipeManager.recipeFluids);
		this.productTank.tankMode = StandardTank.TankMode.OUTPUT;
		this.tankManager = new TankManager(productTank);
	}

	@Override
	public void openGui(EntityPlayer player) {
		player.openGui(ForestryAPI.instance, GuiId.SqueezerGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	/* LOADING & SAVING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		tankManager.writeTanksToNBT(nbttagcompound);
		sockets.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		tankManager.readTanksFromNBT(nbttagcompound);
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
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		tankManager.writePacketData(data);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		tankManager.readPacketData(data);
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

	// / WORKING
	@Override
	public void updateServerSide() {
		super.updateServerSide();

		if (updateOnInterval(20)) {
			inventory.fillContainers(productTank.getFluid(), tankManager);
		}
	}

	@Override
	public boolean workCycle() {
		EntityPlayer player = Proxies.common.getPlayer(worldObj, getAccessHandler().getOwner());
		if (!inventory.removeResources(currentRecipe.resources, player)) {
			return false;
		}

		FluidStack resultFluid = currentRecipe.liquid;
		productTank.fill(resultFluid, true);

		if (currentRecipe.remnants != null && worldObj.rand.nextInt(100) < currentRecipe.chance) {
			ItemStack remnant = currentRecipe.remnants.copy();
			inventory.addRemnant(remnant, true);
		}

		return true;
	}

	private boolean checkRecipe() {
		ItemStack[] resources = inventory.getResources();
		Recipe matchingRecipe = RecipeManager.findMatchingRecipe(resources);

		if (currentRecipe != matchingRecipe) {
			currentRecipe = matchingRecipe;
			if (currentRecipe != null) {
				int recipeTime = currentRecipe.timePerItem;
				setTicksPerWorkCycle(recipeTime * TICKS_PER_RECIPE_TIME);
				setEnergyPerWorkCycle(recipeTime * ENERGY_PER_RECIPE_TIME);
			}
		}

		getErrorLogic().setCondition(currentRecipe == null, EnumErrorCode.NORECIPE);
		return currentRecipe != null;
	}

	@Override
	public boolean hasWork() {
		checkRecipe();

		boolean hasResources = inventory.hasResources();
		boolean hasRecipe = true;
		boolean canFill = true;
		boolean canAdd = true;

		if (hasResources) {
			hasRecipe = (currentRecipe != null);
			if (hasRecipe) {
				FluidStack resultFluid = currentRecipe.liquid;
				canFill = (productTank.fill(resultFluid, false) == resultFluid.amount);

				if (currentRecipe.remnants != null) {
					canAdd = inventory.addRemnant(currentRecipe.remnants, false);
				}
			}
		}

		IErrorLogic errorLogic = getErrorLogic();
		errorLogic.setCondition(!hasResources, EnumErrorCode.NORESOURCE);
		errorLogic.setCondition(!hasRecipe, EnumErrorCode.NORECIPE);
		errorLogic.setCondition(!canFill, EnumErrorCode.NOSPACETANK);
		errorLogic.setCondition(!canAdd, EnumErrorCode.NOSPACE);

		return hasResources && hasRecipe && canFill && canAdd;
	}

	public int getResourceScaled(int i) {
		return (productTank.getFluidAmount() * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public EnumTankLevel getSecondaryLevel() {
		return Utils.rateTankLevel(getResourceScaled(100));
	}

	/* ILIQUIDCONTAINER IMPLEMENTATION */
	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public void getGUINetworkData(int messageId, int data) {

	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {

	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tankManager.fill(from, resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return tankManager.drain(from, resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tankManager.drain(from, maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return tankManager.canFill(from, fluid);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return tankManager.canDrain(from, fluid);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return tankManager.getTankInfo(from);
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

	public static class SqueezerInventory extends TileInventoryAdapter<MachineSqueezer> {
		public static final short SLOT_RESOURCE_1 = 0;
		public static final short SLOTS_RESOURCE_COUNT = 9;
		public static final short SLOT_REMNANT = 9;
		public static final short SLOT_REMNANT_COUNT = 1;
		public static final short SLOT_CAN_INPUT = 10;
		public static final short SLOT_CAN_OUTPUT = 11;

		public SqueezerInventory(MachineSqueezer squeezer) {
			super(squeezer, 12, "Items");
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (slotIndex == SLOT_CAN_INPUT) {
				return FluidHelper.isEmptyContainer(itemStack);
			}

			if (slotIndex >= SLOT_RESOURCE_1 && slotIndex < SLOT_RESOURCE_1 + SLOTS_RESOURCE_COUNT) {
				if (FluidHelper.isEmptyContainer(itemStack)) {
					return false;
				}

				if (RecipeManager.canUse(itemStack)) {
					return true;
				}
			}

			return false;
		}

		@Override
		public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
			return slotIndex == SLOT_REMNANT || slotIndex == SLOT_CAN_OUTPUT;
		}

		public boolean hasResources() {
			return !InvTools.isEmpty(this, SLOT_RESOURCE_1, SLOTS_RESOURCE_COUNT);
		}

		public ItemStack[] getResources() {
			return InvTools.getStacks(this, SLOT_RESOURCE_1, SLOTS_RESOURCE_COUNT);
		}

		public boolean removeResources(ItemStack[] stacks, EntityPlayer player) {
			return InvTools.removeSets(this, 1, stacks, SLOT_RESOURCE_1, SLOTS_RESOURCE_COUNT, player, false, true);
		}

		public boolean addRemnant(ItemStack remnant, boolean doAdd) {
			return InvTools.tryAddStack(this, remnant, SLOT_REMNANT, SLOT_REMNANT_COUNT, true, doAdd);
		}

		public void fillContainers(FluidStack fluidStack, TankManager tankManager) {
			if (getStackInSlot(SLOT_CAN_INPUT) == null || fluidStack == null) {
				return;
			}
			FluidHelper.fillContainers(tankManager, this, SLOT_CAN_INPUT, SLOT_CAN_OUTPUT, fluidStack.getFluid());
		}
	}

	/* RECIPE MANAGMENT */
	public static class Recipe {

		public final int timePerItem;
		public final ItemStack[] resources;
		public final FluidStack liquid;
		public final ItemStack remnants;
		public final int chance;

		public Recipe(int timePerItem, ItemStack[] resources, FluidStack liquid, ItemStack remnants, int chance) {
			this.timePerItem = timePerItem;
			this.resources = resources;
			this.liquid = liquid;
			this.remnants = remnants;
			this.chance = chance;
		}

		public boolean matches(ItemStack[] res) {
			return StackUtils.containsSets(resources, res, true, false) > 0;
		}
	}

	public static class RecipeManager implements ISqueezerManager {

		public static final ArrayList<MachineSqueezer.Recipe> recipes = new ArrayList<MachineSqueezer.Recipe>();
		public static final HashSet<Fluid> recipeFluids = new HashSet<Fluid>();
		public static final HashSet<ItemStack> recipeInputs = new HashSet<ItemStack>();

		@Override
		public void addRecipe(int timePerItem, ItemStack[] resources, FluidStack liquid, ItemStack remnants, int chance) {
			recipes.add(new MachineSqueezer.Recipe(timePerItem, resources, liquid, remnants, chance));
			if (liquid != null) {
				recipeFluids.add(liquid.getFluid());
			}
			if (resources != null) {
				recipeInputs.addAll(Arrays.asList(resources));
			}
		}

		@Override
		public void addRecipe(int timePerItem, ItemStack[] resources, FluidStack liquid) {
			addRecipe(timePerItem, resources, liquid, null, 0);
		}

		public static Recipe findMatchingRecipe(ItemStack[] items) {
			for (Recipe recipe : recipes) {
				if (recipe.matches(items)) {
					return recipe;
				}
			}

			return null;
		}

		public static boolean canUse(ItemStack itemStack) {
			if (recipeInputs.contains(itemStack)) {
				return true;
			}
			for (ItemStack recipeInput : recipeInputs) {
				if (StackUtils.isCraftingEquivalent(recipeInput, itemStack)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public Map<Object[], Object[]> getRecipes() {
			HashMap<Object[], Object[]> recipeList = new HashMap<Object[], Object[]>();

			for (Recipe recipe : recipes) {
				recipeList.put(recipe.resources, new Object[]{recipe.remnants, recipe.liquid});
			}

			return recipeList;
		}
	}

}

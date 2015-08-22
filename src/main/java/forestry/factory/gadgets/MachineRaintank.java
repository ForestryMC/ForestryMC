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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import forestry.api.core.BiomeHelper;
import forestry.api.core.ForestryAPI;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.gadgets.TileBase;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.GuiId;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;

public class MachineRaintank extends TileBase implements ISidedInventory, ILiquidTankContainer {

	/* CONSTANTS */
	public static final short SLOT_RESOURCE = 0;
	public static final short SLOT_PRODUCT = 1;
	private static final FluidStack STACK_WATER = Fluids.WATER.getFluid(Defaults.RAINTANK_AMOUNT_PER_UPDATE);

	/* MEMBER */
	private final FilteredTank resourceTank;
	private final TankManager tankManager;
	private boolean isValidBiome = true;
	private int fillingTime;
	private ItemStack usedEmpty;

	public MachineRaintank() {
		setInternalInventory(new TileInventoryAdapter(this, 3, "Items") {
			@Override
			public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
				if (slotIndex == SLOT_RESOURCE) {
					return FluidHelper.isFillableContainer(itemStack, Fluids.WATER.getFluid(1000));
				}
				return false;
			}

			@Override
			public boolean canExtractItem(int slotIndex, ItemStack itemstack, EnumFacing side) {
				return slotIndex == SLOT_PRODUCT;
			}
		});
		setHints(Config.hints.get("raintank"));

		resourceTank = new FilteredTank(Defaults.RAINTANK_TANK_CAPACITY, FluidRegistry.WATER);
		tankManager = new TankManager(resourceTank);
	}

	@Override
	public void validate() {
		// Raintanks in desert and snow biomes are useless
		if (worldObj != null) {
			BiomeGenBase biome = Utils.getBiomeAt(worldObj, pos);
			if (!BiomeHelper.canRainOrSnow(biome)) {
				setErrorState(EnumErrorCode.INVALIDBIOME);
				isValidBiome = false;
			}
		}

		super.validate();
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.RaintankGUI.ordinal(), player.worldObj, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setBoolean("IsValidBiome", isValidBiome);

		tankManager.writeTanksToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		isValidBiome = nbttagcompound.getBoolean("IsValidBiome");

		tankManager.readTanksFromNBT(nbttagcompound);
	}

	@Override
	public void updateServerSide() {

		if (!updateOnInterval(20)) {
			return;
		}

		if (!isValidBiome) {
			setErrorState(EnumErrorCode.INVALIDBIOME);
		} else if (!worldObj.canBlockSeeSky(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()))) {
			setErrorState(EnumErrorCode.NOSKY);
		} else if (!worldObj.isRaining()) {
			setErrorState(EnumErrorCode.NOTRAINING);
		} else {
			resourceTank.fill(STACK_WATER, true);
			setErrorState(EnumErrorCode.OK);
		}
		
		IInventoryAdapter inventory = getInternalInventory();
		if (!StackUtils.isIdenticalItem(usedEmpty, inventory.getStackInSlot(SLOT_RESOURCE))) {
			fillingTime = 0;
			usedEmpty = null;
		}

		if (usedEmpty == null) {
			usedEmpty = inventory.getStackInSlot(SLOT_RESOURCE);
		}

		if (!isFilling()) {
			tryToStartFillling();
		} else {
			fillingTime--;
			if (fillingTime <= 0 && FluidHelper.fillContainers(tankManager, inventory, SLOT_RESOURCE, SLOT_PRODUCT, Fluids.WATER.getFluid())) {
				fillingTime = 0;
			}
		}
	}

	public boolean isFilling() {
		return fillingTime > 0;
	}

	private void tryToStartFillling() {
		// Nothing to do if no empty cans are available
		if (!FluidHelper.fillContainers(tankManager, getInternalInventory(), SLOT_RESOURCE, SLOT_PRODUCT, Fluids.WATER.getFluid(), false)) {
			return;
		}

		fillingTime = Defaults.RAINTANK_FILLING_TIME;
	}

	public int getFillProgressScaled(int i) {
		return (fillingTime * i) / Defaults.RAINTANK_FILLING_TIME;
	}

	/* SMP GUI */
	@Override
	public void getGUINetworkData(int i, int j) {
		i -= tankManager.maxMessageId() + 1;
		switch (i) {
			case 0:
				fillingTime = j;
				break;
		}
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int i = tankManager.maxMessageId() + 1;
		iCrafting.sendProgressBarUpdate(container, i, fillingTime);
	}

	// / ILIQUIDCONTAINER IMPLEMENTATION
	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		return tankManager.fill(from, resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		return tankManager.drain(from, resource, doDrain);
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		return tankManager.drain(from, maxDrain, doDrain);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return tankManager.canFill(from, fluid);
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return tankManager.canDrain(from, fluid);
	}

	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return tankManager.getTankInfo(from);
	}

}

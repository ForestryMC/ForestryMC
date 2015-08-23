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
package forestry.apiculture.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.core.ForestryAPI;
import forestry.core.config.Defaults;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.GuiId;

public class TileAlvearyHygroregulator extends TileAlveary implements IInventory, ILiquidTankContainer {

	/* CONSTANTS */
	public static final int BLOCK_META = 5;

	/* RECIPE MANAGMENT */
	private static class HygroregulatorRecipe {

		public final FluidStack liquid;
		public final int transferTime;
		public final float humidChange;
		public final float tempChange;

		public HygroregulatorRecipe(FluidStack liquid, int transferTime, float humidChange, float tempChange) {
			this.liquid = liquid;
			this.transferTime = transferTime;
			this.humidChange = humidChange;
			this.tempChange = tempChange;
		}
	}

	public static final short SLOT_INPUT = 0;

	private final HygroregulatorRecipe[] recipes;

	/* MEMBERS */
	private final TankManager tankManager;
	private final FilteredTank liquidTank;

	private HygroregulatorRecipe currentRecipe;
	private int transferTime;

	public TileAlvearyHygroregulator() {
		super(BLOCK_META);

		setInternalInventory(new TileInventoryAdapter(this, 1, "CanInv") {
			@Override
			public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
				if (slotIndex == SLOT_INPUT) {
					Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
					return liquidTank.accepts(fluid);
				}
				return false;
			}
		});

		Fluid water = Fluids.WATER.getFluid();
		Fluid lava = Fluids.LAVA.getFluid();
		Fluid liquidIce = Fluids.ICE.getFluid();

		liquidTank = new FilteredTank(Defaults.PROCESSOR_TANK_CAPACITY, water, lava, liquidIce);
		tankManager = new TankManager(liquidTank);

		recipes = new HygroregulatorRecipe[]{new HygroregulatorRecipe(new FluidStack(water, 1), 1, 0.01f, -0.005f),
				new HygroregulatorRecipe(new FluidStack(lava, 1), 10, -0.01f, +0.005f),
				new HygroregulatorRecipe(new FluidStack(liquidIce, 1), 10, 0.02f, -0.01f)};
	}

	@Override
	public void openGui(EntityPlayer player) {
		player.openGui(ForestryAPI.instance, GuiId.HygroregulatorGUI.ordinal(), worldObj, pos.getX(), pos.getY(), pos.getZ());
	}

	/* UPDATING */
	private HygroregulatorRecipe getRecipe(FluidStack liquid) {
		HygroregulatorRecipe recipe = null;
		for (HygroregulatorRecipe rec : recipes) {
			if (rec.liquid.isFluidEqual(liquid)) {
				recipe = rec;
				break;
			}
		}
		return recipe;
	}

	@Override
	protected void updateServerSide() {
		super.updateServerSide();

		if (transferTime <= 0 && liquidTank.getFluidAmount() > 0) {
			currentRecipe = getRecipe(liquidTank.getFluid());

			if (currentRecipe != null) {
				liquidTank.drain(currentRecipe.liquid.amount, true);
				transferTime = currentRecipe.transferTime;
			}
		}

		if (transferTime > 0) {

			transferTime--;
			if (currentRecipe != null) {
				IAlvearyComponent component = (IAlvearyComponent) this.getCentralTE();
				if (component != null) {
					component.addHumidityChange(currentRecipe.humidChange, 0.0f, 1.0f);
					component.addTemperatureChange(currentRecipe.tempChange, 0.0f, 2.0f);
				}
			} else {
				transferTime = 0;
			}
		}

		if (!updateOnInterval(20)) {
			return;
		}

		IInventoryAdapter canInventory = getInternalInventory();

		// Check if we have suitable items waiting in the item slot
		if (canInventory.getStackInSlot(0) != null) {
			FluidHelper.drainContainers(tankManager, canInventory, 0);
		}

	}

	@Override
	public boolean hasFunction() {
		return true;
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		tankManager.readTanksFromNBT(nbttagcompound);

		transferTime = nbttagcompound.getInteger("TransferTime");

		if (nbttagcompound.hasKey("CurrentLiquid")) {
			FluidStack liquid = FluidStack.loadFluidStackFromNBT(nbttagcompound.getCompoundTag("CurrentLiquid"));
			currentRecipe = getRecipe(liquid);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		tankManager.writeTanksToNBT(nbttagcompound);

		nbttagcompound.setInteger("TransferTime", transferTime);
		if (currentRecipe != null) {
			NBTTagCompound subcompound = new NBTTagCompound();
			currentRecipe.liquid.writeToNBT(subcompound);
			nbttagcompound.setTag("CurrentLiquid", subcompound);
		}

	}

	/* TEXTURES */
	@Override
	public int getIcon(int side, int metadata) {
		return BlockAlveary.ALVEARY_HYGRO;
	}

	/* ILIQUIDTANKCONTAINER */
	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

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
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return tankManager.getTankInfo(from);
	}

	@Override
	public void getGUINetworkData(int messageId, int data) {
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
	}
}

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
package forestry.apiculture.multiblock;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import forestry.api.core.IClimateControlled;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.apiculture.blocks.BlockAlveary;
import forestry.apiculture.gui.ContainerAlvearyHygroregulator;
import forestry.apiculture.gui.GuiAlvearyHygroregulator;
import forestry.apiculture.inventory.InventoryHygroregulator;
import forestry.core.config.Constants;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.tiles.ILiquidTankTile;

public class TileAlvearyHygroregulator extends TileAlveary implements IInventory, ILiquidTankTile, IFluidHandler, IAlvearyComponent.Climatiser {
	private final HygroregulatorRecipe[] recipes;

	private final TankManager tankManager;
	private final FilteredTank liquidTank;
	private final IInventoryAdapter inventory;

	private HygroregulatorRecipe currentRecipe;
	private int transferTime;

	public TileAlvearyHygroregulator() {
		super(BlockAlveary.Type.HYGRO);

		this.inventory = new InventoryHygroregulator(this);

		Fluid water = Fluids.WATER.getFluid();
		Fluid lava = Fluids.LAVA.getFluid();
		Fluid liquidIce = Fluids.ICE.getFluid();

		this.liquidTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, water, lava, liquidIce);
		this.tankManager = new TankManager(this, liquidTank);

		this.recipes = new HygroregulatorRecipe[]{new HygroregulatorRecipe(new FluidStack(water, 1), 1, 0.01f, -0.005f),
				new HygroregulatorRecipe(new FluidStack(lava, 1), 10, -0.01f, +0.005f),
				new HygroregulatorRecipe(new FluidStack(liquidIce, 1), 10, 0.02f, -0.01f)};
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return inventory;
	}

	@Override
	public boolean allowsAutomation() {
		return true;
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
	public void changeClimate(int tickCount, IClimateControlled climateControlled) {
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
				climateControlled.addHumidityChange(currentRecipe.humidChange, 0.0f, 1.0f);
				climateControlled.addTemperatureChange(currentRecipe.tempChange, 0.0f, 2.0f);
			} else {
				transferTime = 0;
			}
		}

		if (tickCount % 20 == 0) {
			// Check if we have suitable items waiting in the item slot
			FluidHelper.drainContainers(tankManager, this, 0);
		}
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		tankManager.readFromNBT(nbttagcompound);

		transferTime = nbttagcompound.getInteger("TransferTime");

		if (nbttagcompound.hasKey("CurrentLiquid")) {
			FluidStack liquid = FluidStack.loadFluidStackFromNBT(nbttagcompound.getCompoundTag("CurrentLiquid"));
			currentRecipe = getRecipe(liquid);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		tankManager.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("TransferTime", transferTime);
		if (currentRecipe != null) {
			NBTTagCompound subcompound = new NBTTagCompound();
			currentRecipe.liquid.writeToNBT(subcompound);
			nbttagcompound.setTag("CurrentLiquid", subcompound);
		}
	}

	/* TEXTURES */
	@Override
	public int getIcon(int side) {
		return BlockAlveary.ALVEARY_HYGRO;
	}

	/* ILIQUIDTANKCONTAINER */
	@Override
	public TankManager getTankManager() {
		return tankManager;
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

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiAlvearyHygroregulator(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerAlvearyHygroregulator(player.inventory, this);
	}

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
}

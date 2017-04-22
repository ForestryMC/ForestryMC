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

import javax.annotation.Nullable;

import forestry.api.climate.IClimateControlled;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.apiculture.blocks.BlockAlvearyType;
import forestry.apiculture.gui.ContainerAlvearyHygroregulator;
import forestry.apiculture.gui.GuiAlvearyHygroregulator;
import forestry.apiculture.inventory.InventoryHygroregulator;
import forestry.core.config.Constants;
import forestry.core.fluids.FilteredTank;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.tiles.ILiquidTankTile;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileAlvearyHygroregulator extends TileAlveary implements IInventory, ILiquidTankTile, IAlvearyComponent.Climatiser {
	private final HygroregulatorRecipe[] recipes;

	private final TankManager tankManager;
	private final FilteredTank liquidTank;
	private final IInventoryAdapter inventory;

	@Nullable
	private HygroregulatorRecipe currentRecipe;
	private int transferTime;

	public TileAlvearyHygroregulator() {
		super(BlockAlvearyType.HYGRO);

		this.inventory = new InventoryHygroregulator(this);

		Fluid water = FluidRegistry.WATER;
		Fluid lava = FluidRegistry.LAVA;
		Fluid liquidIce = Fluids.ICE.getFluid();

		this.liquidTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY).setFilters(water, lava, liquidIce);

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
	@Nullable
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
		if (transferTime <= 0) {
			FluidStack fluid = liquidTank.getFluid();
			if (fluid != null && fluid.amount > 0) {
				currentRecipe = getRecipe(fluid);

				if (currentRecipe != null) {
					liquidTank.drainInternal(currentRecipe.liquid.amount, true);
					transferTime = currentRecipe.transferTime;
				}
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);
		tankManager.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("TransferTime", transferTime);
		if (currentRecipe != null) {
			NBTTagCompound subcompound = new NBTTagCompound();
			currentRecipe.liquid.writeToNBT(subcompound);
			nbttagcompound.setTag("CurrentLiquid", subcompound);
		}
		return nbttagcompound;
	}

	/* ILIQUIDTANKCONTAINER */

	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ||
				super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (super.hasCapability(capability, facing)) {
			return super.getCapability(capability, facing);
		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tankManager);
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, int data) {
		return new GuiAlvearyHygroregulator(player.inventory, this);
	}

	@Override
	public Container getContainer(EntityPlayer player, int data) {
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

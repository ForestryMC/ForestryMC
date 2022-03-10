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
package forestry.climatology.tiles;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;

import forestry.api.recipes.IHygroregulatorManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import forestry.api.climate.ClimateCapabilities;
import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateHousing;
import forestry.api.climate.IClimateManipulator;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateTransformer;
import forestry.api.climate.IClimatised;
import forestry.api.core.IErrorLogic;
import forestry.api.recipes.IHygroregulatorRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.climatology.features.ClimatologyTiles;
import forestry.climatology.gui.ContainerHabitatFormer;
import forestry.climatology.inventory.InventoryHabitatFormer;
import forestry.core.climate.ClimateTransformer;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FilteredTank;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.ITankManager;
import forestry.core.fluids.TankManager;
import forestry.core.network.PacketBufferForestry;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.energy.EnergyManager;

public class TileHabitatFormer extends TilePowered implements IClimateHousing, IClimatised, ILiquidTankTile {
	private static final String TRANSFORMER_KEY = "Transformer";

	//The logic that handles the climate  changes.
	private final ClimateTransformer transformer;

	private final FilteredTank resourceTank;
	private final TankManager tankManager;

	public TileHabitatFormer(BlockPos pos, BlockState state) {
		super(ClimatologyTiles.HABITAT_FORMER.tileType(), pos, state, 1200, 10000);
		this.transformer = new ClimateTransformer(this);
		setInternalInventory(new InventoryHabitatFormer(this));
		resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY).setFilters(() -> RecipeManagers.hygroregulatorManager.getRecipeFluids(level.getRecipeManager()));
		tankManager = new TankManager(this, resourceTank);
		setTicksPerWorkCycle(10);
		setEnergyPerWorkCycle(0);
	}

	@Override
	public ITankManager getTankManager() {
		return tankManager;
	}

	@Override
	public void onRemoval() {
		transformer.removeTransformer();
	}

	@Override
	protected void updateServerSide() {
		super.updateServerSide();
		transformer.update();
		if (updateOnInterval(20)) {
			// Check if we have suitable items waiting in the item slot
			FluidHelper.drainContainers(tankManager, this, 0);
		}
	}

	@Override
	public boolean hasWork() {
		return true;
	}

	@Nullable
	private FluidStack cachedStack = null;

	@Override
	protected boolean workCycle() {
		IErrorLogic errorLogic = getErrorLogic();
		IClimateState currentState = transformer.getCurrent();
		IClimateState changedState = transformer.getTarget().subtract(currentState);
		IClimateState difference = getClimateDifference();
		cachedStack = null;
		if (difference.getHumidity() != 0.0F) {
			updateHumidity(errorLogic, changedState);
		}
		if (difference.getTemperature() != 0.0F) {
			updateTemperature(errorLogic, changedState);
		}
		return true;
	}

	private void updateHumidity(IErrorLogic errorLogic, IClimateState changedState) {
		IClimateManipulator manipulator = transformer.createManipulator(ClimateType.HUMIDITY).build();
		if (manipulator.canAdd()) {
			errorLogic.setCondition(false, EnumErrorCode.WRONG_RESOURCE);
			int currentCost = getFluidCost(changedState);
			if (!resourceTank.drain(currentCost, IFluidHandler.FluidAction.SIMULATE).isEmpty()) {
				IClimateState simulatedState = /*changedState.add(ClimateType.HUMIDITY, climateChange)*/
						changedState.toImmutable().add(manipulator.addChange(true));
				int fluidCost = getFluidCost(simulatedState);
				if (!resourceTank.drain(fluidCost, IFluidHandler.FluidAction.SIMULATE).isEmpty()) {
					cachedStack = resourceTank.drain(fluidCost, IFluidHandler.FluidAction.EXECUTE);
					manipulator.addChange(false);
				} else {
					cachedStack = resourceTank.drain(currentCost, IFluidHandler.FluidAction.EXECUTE);
				}
				errorLogic.setCondition(false, EnumErrorCode.NO_RESOURCE_LIQUID);
			} else {

				manipulator.removeChange(false);
				errorLogic.setCondition(true, EnumErrorCode.NO_RESOURCE_LIQUID);
			}
		} else {
			if (resourceTank.isEmpty()) {
				errorLogic.setCondition(true, EnumErrorCode.NO_RESOURCE_LIQUID);
			} else {
				errorLogic.setCondition(true, EnumErrorCode.WRONG_RESOURCE);
				errorLogic.setCondition(false, EnumErrorCode.NO_RESOURCE_LIQUID);
			}
		}
		manipulator.finish();
	}

	private void updateTemperature(IErrorLogic errorLogic, IClimateState changedState) {
		IClimateManipulator manipulator = transformer.createManipulator(ClimateType.TEMPERATURE).setAllowBackwards().build();
		EnergyManager energyManager = getEnergyManager();
		int currentCost = getEnergyCost(changedState);
		if (energyManager.extractEnergy(currentCost, true) > 0) {
			IClimateState simulatedState = manipulator.addChange(true);
			int energyCost = getEnergyCost(simulatedState);
			if (energyManager.extractEnergy(energyCost, true) > 0) {
				energyManager.extractEnergy(energyCost, false);
				manipulator.addChange(false);
			} else {
				energyManager.extractEnergy(currentCost, false);
			}
			errorLogic.setCondition(false, EnumErrorCode.NO_POWER);
		} else {
			manipulator.removeChange(false);
			errorLogic.setCondition(true, EnumErrorCode.NO_POWER);
		}
		manipulator.finish();
	}

	private int getFluidCost(IClimateState state) {
		FluidStack fluid = resourceTank.getFluid();
		return RecipeManagers.hygroregulatorManager.findMatchingRecipe(null, fluid)
				.map(recipe -> getEnergyCost(state) * recipe.getResource().getAmount())
				.orElse(0);
	}

	private int getEnergyCost(IClimateState state) {
		return Math.round((1.0F + Mth.abs(state.getTemperature())) * transformer.getCostModifier());
	}

	@Override
	public float getChangeForState(ClimateType type, IClimateManipulator manipulator) {
		IHygroregulatorManager manager = RecipeManagers.hygroregulatorManager;

		if (type == ClimateType.HUMIDITY) {
			FluidStack fluid = resourceTank.getFluid();
			return manager.findMatchingRecipe(null, fluid)
					.map(IHygroregulatorRecipe::getHumidChange)
					.map(humidChange -> humidChange / transformer.getSpeedModifier())
					.orElse(0f);
		}

		if (cachedStack != null) {
			return manager.findMatchingRecipe(null, cachedStack)
					.map(IHygroregulatorRecipe::getTempChange)
					.map(tempChange -> (0.05F + Math.abs(tempChange)) * 0.5F / transformer.getSpeedModifier())
					.orElse(0f);
		}
		return 0;
	}

	private IClimateState getClimateDifference() {
		IClimateState defaultState = transformer.getDefault();
		IClimateState targetedState = transformer.getTarget();
		return targetedState.subtract(defaultState);
	}

	@Override
	public void markNetworkUpdate() {
		setNeedsNetworkUpdate();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerHabitatFormer(windowId, inv, this);
	}

	@Override
	public Biome getBiome() {
		Level level = Objects.requireNonNull(this.level);
		return level.getBiome(getBlockPos()).value();
	}

	@Override
	public float getExactTemperature() {
		return transformer.getCurrent().getTemperature();
	}

	@Override
	public float getExactHumidity() {
		return transformer.getCurrent().getHumidity();
	}

	/* Methods - Implement IClimateHousing */
	@Override
	public IClimateTransformer getTransformer() {
		return transformer;
	}

	/* Methods - Implement IStreamableGui */
	@Override
	public void writeGuiData(PacketBufferForestry data) {
		super.writeGuiData(data);
		transformer.writeData(data);
	}

	@Override
	public void readGuiData(PacketBufferForestry data) throws IOException {
		super.readGuiData(data);
		transformer.readData(data);
	}

	/* Methods - SAVING & LOADING */
	@Override
	public void saveAdditional(CompoundTag data) {
		super.saveAdditional(data);

		tankManager.write(data);

		data.put(TRANSFORMER_KEY, transformer.write(new CompoundTag()));
	}

	@Override
	public void load(CompoundTag data) {
		super.load(data);

		tankManager.read(data);

		if (data.contains(TRANSFORMER_KEY)) {
			CompoundTag nbtTag = data.getCompound(TRANSFORMER_KEY);
			transformer.read(nbtTag);
		}
	}

	/* Network */
	@Override
	public void writeData(PacketBufferForestry data) {
		super.writeData(data);
		tankManager.writeData(data);
		transformer.writeData(data);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void readData(PacketBufferForestry data) throws IOException {
		super.readData(data);
		tankManager.readData(data);
		transformer.readData(data);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> tankManager).cast();
		}
		if (capability == ClimateCapabilities.CLIMATE_TRANSFORMER) {
			return LazyOptional.of(() -> transformer).cast();
		}
		return super.getCapability(capability, facing);
	}
}

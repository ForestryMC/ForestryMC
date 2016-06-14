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
package forestry.factory.multiblock;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.api.multiblock.IDistillVatComponent;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.recipes.IStillRecipe;
import forestry.core.access.EnumAccess;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.multiblock.MultiblockValidationException;
import forestry.core.multiblock.RectangularMultiblockControllerBase;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.IPowerHandler;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.OreDictUtil;
import forestry.core.utils.Translator;
import forestry.energy.EnergyManager;
import forestry.factory.inventory.InventoryDistillVat;
import forestry.factory.recipes.StillRecipeManager;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;

public class DistillVatController extends RectangularMultiblockControllerBase implements IDistillVatControllerInternal, ILiquidTankTile, IEnergyReceiver, IEnergyHandler, IPowerHandler, IErrorLogicSource {

	private final InventoryDistillVat inventory;
	private static final int WORK_TICK_INTERVAL = 5; // one Forestry work tick happens every WORK_TICK_INTERVAL game ticks
	private static final int MB_MULTIPLIER = 6 ; //# multiplier comparison to still
	private static final int ENERGY_PER_RECIPE_TIME = 200 * MB_MULTIPLIER;
	private static final int WORK_CYCLES = 4;

	private int workCounter;
	private int ticksPerWorkCycle;
	private int energyPerWorkCycle;
	private int noPowerTime = 0;
	protected float speedMultiplier = 1.0f;
	protected float powerMultiplier = 1.0f;

	private final FilteredTank resourceTank;
	private final FilteredTank productTank;
	private final TankManager tankManager;

	private IStillRecipe currentRecipe;
	private FluidStack bufferedLiquid;

	private final EnergyManager energyManager;

	// PARTS
	private final Set<IDistillVatComponent.Active> activeComponents = new HashSet<>();

	public DistillVatController(World world) {
		super(world, DistillVatMultiblockSizeLimits.instance);
		energyManager = new EnergyManager(3000, 8000 * (MB_MULTIPLIER));
		energyManager.setReceiveOnly();
		this.ticksPerWorkCycle = 4;

		this.inventory = new InventoryDistillVat(this);

		resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY * MB_MULTIPLIER, true, false);
		resourceTank.setFilters(StillRecipeManager.recipeFluidInputs);

		productTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY * MB_MULTIPLIER, false, true);
		productTank.setFilters(StillRecipeManager.recipeFluidOutputs);

		tankManager = new TankManager(this, resourceTank, productTank);

	}

	@Nonnull
	@Override
	public IInventoryAdapter getInternalInventory() {
		if (isAssembled()) {
			return inventory;
		} else {
			return FakeInventoryAdapter.instance();
		}
	}

	public TankManager getTankManager() {
		return tankManager;
	}

	public EnergyManager getEnergyManager() {
		return energyManager;
	}

	@Override
	public void onAttachedPartWithMultiblockData(IMultiblockComponent part, NBTTagCompound data) {
		this.readFromNBT(data);
	}
	@Override
	protected void onBlockAdded(IMultiblockComponent newPart) {
	}
	@Override
	protected void onBlockRemoved(IMultiblockComponent oldPart) {

	}

	@Override
	protected void isMachineWhole() throws MultiblockValidationException {
		super.isMachineWhole();

		final BlockPos maximumCoord = getMaximumCoord();
		final BlockPos minimumCoord = getMinimumCoord();

		// check the the bottom is glass blocks

		final int glassY = minimumCoord.getY() - 1;
		for (int glassX = minimumCoord.getX(); glassX <= maximumCoord.getX(); glassX++) {
			for (int glassZ = minimumCoord.getZ(); glassZ <= maximumCoord.getZ(); glassZ++) {
				BlockPos pos = new BlockPos(glassX, glassY, glassZ);
				IBlockState state = worldObj.getBlockState(pos);
				Block block = state.getBlock();
				if (!BlockUtil.isGlassBlock(state, block,worldObj, pos)) {
					throw new MultiblockValidationException(Translator.translateToLocal("for.multiblock.distillvat.error.needGlass"));
				}

			}
		}

		// check that there is space all around the alveary entrances
	}

	@Override
	protected void isGoodForExteriorLevel(IMultiblockComponent part, int level) throws MultiblockValidationException {
		if (level == 2 && !(part instanceof TileDistillVatPlain)) {
			throw new MultiblockValidationException(Translator.translateToLocal("for.multiblock.distillvat.error.needPlainOnTop"));
		}
	}

	@Override
	protected void isGoodForInterior(IMultiblockComponent part) throws MultiblockValidationException {
		if (!(part instanceof TileDistillVatPlain)) {
			throw new MultiblockValidationException(Translator.translateToLocal("for.multiblock.distillvat.error.needPlainInterior"));
		}
	}

	@Override
	protected void onAssimilate(IMultiblockControllerInternal assimilated) {

	}

	@Override
	public void onAssimilated(IMultiblockControllerInternal assimilator) {

	}

	@Override
	protected boolean updateServer(int tickCount) {
		boolean didwork = false;
		if (updateOnInterval(20)) {
			FluidHelper.drainContainers(tankManager, this, InventoryDistillVat.SLOT_CAN);

			FluidStack fluidStack = productTank.getFluid();
			if (fluidStack != null) {
			FluidHelper.fillContainers(tankManager, this, InventoryDistillVat.SLOT_RESOURCE, InventoryDistillVat.SLOT_PRODUCT, fluidStack.getFluid(), true);
			}
		}


		if (energyManager.getTotalEnergyStored() <= 0) {
			return didwork;
		}


		if (!updateOnInterval(WORK_TICK_INTERVAL)) {
			return didwork;
		}

		IErrorLogic errorLogic = getErrorLogic();

		boolean disabled = worldObj.isBlockIndirectlyGettingPowered(this.getCoordinates()) > 0;
		errorLogic.setCondition(disabled, EnumErrorCode.DISABLED_BY_REDSTONE);
		if (disabled) {
			return didwork;
		}

		if (!hasWork()) {
			return didwork;
		}

		if (workCounter < ticksPerWorkCycle) {
			int energyPerWorkCycle = getEnergyPerWorkCycle();
			boolean consumedEnergy = energyManager.consumeEnergyToDoWork(ticksPerWorkCycle, energyPerWorkCycle);
			if (consumedEnergy) {
				errorLogic.setCondition(false, EnumErrorCode.NO_POWER);
				workCounter++;
				noPowerTime = 0;
			} else {
				noPowerTime++;
				if (noPowerTime > 4) {
					errorLogic.setCondition(true, EnumErrorCode.NO_POWER);
				}
			}
		}
		if (workCounter >= ticksPerWorkCycle) {
			if (workCycle()) {
				workCounter = 0;
			}
		}

		return true;
	}

	public boolean workCycle() {

		int cycles = currentRecipe.getCyclesPerUnit();
		FluidStack output = currentRecipe.getOutput();
		FluidStack product = new FluidStack(output, (output.amount * MB_MULTIPLIER) * cycles);
		productTank.fillInternal(product, true);
		bufferedLiquid = null;

		return true;
	}

	private void checkRecipe() {
		FluidStack recipeLiquid = bufferedLiquid != null ? bufferedLiquid : resourceTank.getFluid();

		if (!StillRecipeManager.matches(currentRecipe, recipeLiquid)) {
			currentRecipe = StillRecipeManager.findMatchingRecipe(recipeLiquid);
			int recipeTime = currentRecipe == null ? 0 : currentRecipe.getCyclesPerUnit();
			setEnergyPerWorkCycle(ENERGY_PER_RECIPE_TIME * recipeTime);
			setTicksPerWorkCycle(recipeTime);
		}
	}

	public boolean hasWork() {
		checkRecipe();

		boolean hasRecipe = currentRecipe != null;
		boolean hasTankSpace = true;
		boolean hasLiquidResource = true;

		if (hasRecipe) {
			FluidStack fluidStack = currentRecipe.getOutput();
			hasTankSpace = productTank.fillInternal(fluidStack, false) == fluidStack.amount;
			if (bufferedLiquid == null) {
				int cycles = currentRecipe.getCyclesPerUnit();
				FluidStack input = currentRecipe.getInput();
				int drainAmount = cycles * (input.amount * MB_MULTIPLIER);
				FluidStack drained = resourceTank.drain(drainAmount, false);
				hasLiquidResource = drained != null && drained.amount == drainAmount;
				if (hasLiquidResource) {
					bufferedLiquid = new FluidStack(input, drainAmount);
					resourceTank.drain(drainAmount, true);
				}
			}
		}

		IErrorLogic errorLogic = getErrorLogic();
		errorLogic.setCondition(!hasRecipe, EnumErrorCode.NO_RECIPE);
		errorLogic.setCondition(!hasTankSpace, EnumErrorCode.NO_SPACE_TANK);
		errorLogic.setCondition(!hasLiquidResource, EnumErrorCode.NO_RESOURCE_LIQUID);

		return hasRecipe && hasLiquidResource && hasTankSpace;
	}

	@Override
	protected void updateClient(int tickCount) {
		for (IDistillVatComponent.Active activeComponent : activeComponents) {
			activeComponent.updateClient(tickCount);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);

		tankManager.writeToNBT(data);
		energyManager.writeToNBT(data);

		if (bufferedLiquid != null) {
			NBTTagCompound buffer = new NBTTagCompound();
			bufferedLiquid.writeToNBT(buffer);
			data.setTag("Buffer", buffer);
		}

		inventory.writeToNBT(data);
		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		tankManager.readFromNBT(data);
		energyManager.readFromNBT(data);
		if (data.hasKey("Buffer")) {
			NBTTagCompound buffer = data.getCompoundTag("Buffer");
			bufferedLiquid = FluidStack.loadFluidStackFromNBT(buffer);
		}

		inventory.readFromNBT(data);
	}

	@Override
	public void formatDescriptionPacket(NBTTagCompound data) {
		writeToNBT(data);
	}

	@Override
	public void decodeDescriptionPacket(NBTTagCompound data) {
		readFromNBT(data);
	}

	/* IActivatable */

	@Override
	public BlockPos getCoordinates() {
		BlockPos coord = getCenterCoord();
		return coord.add(0, +1, 0);
	}

	@Override
	public void onSwitchAccess(EnumAccess oldAccess, EnumAccess newAccess) {
		if (oldAccess == EnumAccess.SHARED || newAccess == EnumAccess.SHARED) {
			// pipes connected to this need to update
			for (IMultiblockComponent part : connectedParts) {
				if (part instanceof TileEntity) {
					TileEntity tile = (TileEntity) part;
					worldObj.notifyBlockOfStateChange(tile.getPos(), tile.getBlockType());
				}
			}
			markDirty();
		}
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromValue(getExactTemperature());
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}

	@Override
	public float getExactTemperature() {
		BlockPos coords = getReferenceCoord();
		return ForestryAPI.climateManager.getTemperature(getWorldObj(), coords);
	}

	@Override
	public float getExactHumidity() {
		BlockPos coords = getReferenceCoord();
		return ForestryAPI.climateManager.getHumidity(getWorldObj(), coords);
	}

	@Override
	public String getUnlocalizedType() {
		return "for.multiblock.distillvat.type";
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return energyManager.receiveEnergy(from, maxReceive, simulate);
	}
	@Override
	public int getEnergyStored(EnumFacing from) {
		return energyManager.getEnergyStored(from);
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return energyManager.getMaxEnergyStored(from);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return energyManager.canConnectEnergy(from);
	}

	/* GUI */
	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(workCounter);
		tankManager.writeData(data);
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		workCounter = data.readVarInt();
		tankManager.readData(data);
	}

	@Override
	public int getWorkCounter() {
		return workCounter;
	}

	public int getEnergyPerWorkCycle() {
		return Math.round(energyPerWorkCycle * powerMultiplier);
	}

	public void setTicksPerWorkCycle(int ticksPerWorkCycle) {
		this.ticksPerWorkCycle = ticksPerWorkCycle;
		workCounter = 0;
	}

	public int getTicksPerWorkCycle() {
		if (worldObj.isRemote) {
			return ticksPerWorkCycle;
		}
		return Math.round(ticksPerWorkCycle / speedMultiplier);
	}

	public void setEnergyPerWorkCycle(int energyPerWorkCycle) {
		this.energyPerWorkCycle = EnergyManager.scaleForDifficulty(energyPerWorkCycle);
	}

	@Override
	public int getProgressScaled(int i) {
		int ticksPerWorkCycle = getTicksPerWorkCycle();
		if (ticksPerWorkCycle == 0) {
			return 0;
		}
		return workCounter * i / ticksPerWorkCycle;
	}


}

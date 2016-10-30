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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.EnumMap;

import forestry.api.core.IErrorLogic;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.FluidHelper.FillStatus;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.watchers.ISlotPickupWatcher;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.factory.gui.ContainerBottler;
import forestry.factory.gui.GuiBottler;
import forestry.factory.inventory.InventoryBottler;
import forestry.factory.recipes.BottlerRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileBottler extends TilePowered implements ISidedInventory, ILiquidTankTile, ISlotPickupWatcher {
	private static final int TICKS_PER_RECIPE_TIME = 5;
	private static final int ENERGY_PER_RECIPE_TIME = 1000;

	private final StandardTank resourceTank;
	private final TankManager tankManager;

	private EnumMap<EnumFacing, Boolean> canDump;
	private boolean dumpingFluid = false;
	
	private BottlerRecipe currentRecipe;
	@SideOnly(Side.CLIENT)
	public boolean isFillRecipe;

	public TileBottler() {
		super("bottler", 1100, 4000);

		setInternalInventory(new InventoryBottler(this));

		resourceTank = new StandardTank(Constants.PROCESSOR_TANK_CAPACITY);
		tankManager = new TankManager(this, resourceTank);
		
		canDump = new EnumMap(EnumFacing.class);
	}

	/* SAVING & LOADING */
	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);
		tankManager.writeToNBT(nbttagcompound);
		return nbttagcompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		tankManager.readFromNBT(nbttagcompound);
		checkEmptyRecipe();
		checkFillRecipe();
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		tankManager.writeData(data);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		tankManager.readData(data);
	}

	@Override
	public void updateServerSide() {
		super.updateServerSide();

		if (updateOnInterval(20)) {
			boolean movedStack = false;
			ItemStack leftProcessingStack = getStackInSlot(InventoryBottler.SLOT_LEFT_PROCESSING);
			ItemStack rightProcessingStack = getStackInSlot(InventoryBottler.SLOT_RIGHT_PROCESSING);
			if(leftProcessingStack == null){
				ItemStack inputStack = getStackInSlot(InventoryBottler.SLOT_INPUT_FULL_CONTAINER);
				if(inputStack != null){
					leftProcessingStack = inputStack.copy();
					leftProcessingStack.stackSize = 1;
					setInventorySlotContents(InventoryBottler.SLOT_LEFT_PROCESSING, leftProcessingStack);
					decrStackSize(InventoryBottler.SLOT_INPUT_FULL_CONTAINER, 1);
					movedStack = true;
				}
			}
			if(rightProcessingStack == null){
				ItemStack inputStack = getStackInSlot(InventoryBottler.SLOT_INPUT_EMPTY_CONTAINER);
				if(inputStack != null){
					rightProcessingStack = inputStack.copy();
					rightProcessingStack.stackSize = 1;
					setInventorySlotContents(InventoryBottler.SLOT_RIGHT_PROCESSING, rightProcessingStack);
					decrStackSize(InventoryBottler.SLOT_INPUT_EMPTY_CONTAINER, 1);
					movedStack = true;
				}
			}
		}

		if (canDump.isEmpty()) {
			for(EnumFacing facing : EnumFacing.VALUES){
				canDump.put(facing, FluidHelper.canAcceptFluid(worldObj, pos.offset(facing), facing.getOpposite(), tankManager.getFluid(0)));
			}
		}

		if (canDump()) {
			if (dumpingFluid || updateOnInterval(20)) {
				dumpingFluid = dumpFluid();
			}
		}
	}
	
	private boolean canDump() {
		for(EnumFacing facing : EnumFacing.VALUES){
			if(canDump.get(facing)){
				return true;
			}
		}
		return false;
	}

	private boolean dumpFluid() {
		if (!resourceTank.isEmpty()) {
			for(EnumFacing facing : EnumFacing.VALUES){
				if(canDump.get(facing)){
					IFluidHandler fluidDestination = FluidUtil.getFluidHandler(worldObj, pos.offset(facing), facing.getOpposite());
					if (fluidDestination != null) {
						if (FluidUtil.tryFluidTransfer(fluidDestination, tankManager, Fluid.BUCKET_VOLUME / 20, true) != null) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean workCycle() {
		FluidHelper.FillStatus status;
		if(currentRecipe.fillRecipe){
			status = FluidHelper.fillContainers(tankManager, this, InventoryBottler.SLOT_RIGHT_PROCESSING, InventoryBottler.SLOT_OUTPUT_FULL_CONTAINER, currentRecipe.fluid.getFluid(), true);
		}else{
			status = FluidHelper.drainContainers(tankManager, this, InventoryBottler.SLOT_LEFT_PROCESSING, InventoryBottler.SLOT_OUTPUT_EMPTY_CONTAINER);
		}
		if(status == FluidHelper.FillStatus.SUCCESS){
			currentRecipe = null;
			return true;
		}
		return false;
	}
	
	@Override
	public void onNeighborTileChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborTileChange(world, pos, neighbor);

		for(EnumFacing facing : EnumFacing.VALUES){
			canDump.put(facing, FluidHelper.canAcceptFluid(worldObj, pos.offset(facing), facing.getOpposite(), tankManager.getFluid(0)));
		}
	}
	
	private void checkFillRecipe(){
		ItemStack emptyCan = getStackInSlot(InventoryBottler.SLOT_RIGHT_PROCESSING);
		if(emptyCan != null){
			FluidStack resource = resourceTank.getFluid();
			if (resource == null) {
				return;
			}
			//Fill Container
			if (currentRecipe == null || !currentRecipe.matchEmpty(emptyCan, resource)) {
				currentRecipe = BottlerRecipe.createEmpty(resource.getFluid(), emptyCan);
				if (currentRecipe != null) {
					float viscosityMultiplier = resource.getFluid().getViscosity(resource) / 1000.0f;
					viscosityMultiplier = (viscosityMultiplier - 1f) / 20f + 1f; // scale down the effect

					int fillAmount = Math.min(currentRecipe.fluid.amount, resource.amount);
					float fillTime = fillAmount / (float) Fluid.BUCKET_VOLUME;
					fillTime *= viscosityMultiplier;

					setTicksPerWorkCycle(Math.round(fillTime * TICKS_PER_RECIPE_TIME));
					setEnergyPerWorkCycle(Math.round(fillTime * ENERGY_PER_RECIPE_TIME));
				}
			}
		}
	}
	
	private void checkEmptyRecipe(){
		ItemStack filledCan = getStackInSlot(InventoryBottler.SLOT_LEFT_PROCESSING);
		if(filledCan != null){
			//Empty Container
			if (currentRecipe == null || !currentRecipe.matchFilled(filledCan) && !currentRecipe.fillRecipe) {
				currentRecipe = BottlerRecipe.createFilled(filledCan);
				if (currentRecipe != null) {
					FluidStack resource = currentRecipe.fluid;
					float viscosityMultiplier = resource.getFluid().getViscosity(resource) / 1000.0f;
					viscosityMultiplier = (viscosityMultiplier - 1f) / 20f + 1f; // scale down the effect

					int fillAmount = Math.min(currentRecipe.fluid.amount, resource.amount);
					float fillTime = fillAmount / (float) Fluid.BUCKET_VOLUME;
					fillTime *= viscosityMultiplier;

					setTicksPerWorkCycle(Math.round(fillTime * TICKS_PER_RECIPE_TIME));
					setEnergyPerWorkCycle(0);
				}
			}
		}
	}
	
	@Override
	public void onPickupFromSlot(int slotIndex, EntityPlayer player) {
		if(slotIndex == InventoryBottler.SLOT_LEFT_PROCESSING){
			setTicksPerWorkCycle(0);
		}else if(slotIndex == InventoryBottler.SLOT_RIGHT_PROCESSING){
			setTicksPerWorkCycle(0);
		}
	}
	
	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		super.readGuiData(data);
		isFillRecipe = data.readBoolean();
	}
	
	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		super.writeGuiData(data);
		if(currentRecipe == null){
			data.writeBoolean(false);
		}else{
			data.writeBoolean(currentRecipe.fillRecipe);
		}
	}

	@Override
	public boolean hasResourcesMin(float percentage) {
		IInventoryAdapter inventory = getInternalInventory();
		ItemStack emptyCan = inventory.getStackInSlot(InventoryBottler.SLOT_RIGHT_PROCESSING);
		if (emptyCan == null) {
			return false;
		}

		return (float) emptyCan.stackSize / (float) emptyCan.getMaxStackSize() > percentage;
	}

	@Override
	public boolean hasWork() {
		FluidHelper.FillStatus emptyStatus;
		FluidHelper.FillStatus fillStatus;
		IErrorLogic errorLogic = getErrorLogic();
		errorLogic.clearErrors();
		
		checkEmptyRecipe();
		if (currentRecipe != null) {
			IFluidTankProperties properties = tankManager.getTankProperties()[0];
			if(properties != null){
				if(properties.getContents() != null && properties.getContents().amount >= properties.getCapacity()){
					emptyStatus = FillStatus.NO_SPACE_FLUID;
				}else{
					emptyStatus = FillStatus.SUCCESS;
				}
			}else{
				emptyStatus = FillStatus.SUCCESS;
			}
		}else{
			emptyStatus = null;
		}
		if(emptyStatus == null || emptyStatus != FillStatus.SUCCESS){
			checkFillRecipe();
			if (currentRecipe == null) {
				fillStatus = FluidHelper.FillStatus.NO_FLUID;
			}else{
				fillStatus = FluidHelper.fillContainers(tankManager, this, InventoryBottler.SLOT_RIGHT_PROCESSING, InventoryBottler.SLOT_OUTPUT_FULL_CONTAINER, currentRecipe.fluid.getFluid(), false);
			}
		}else{
			return true;
		}
		
		if(fillStatus == FillStatus.SUCCESS){
			return true;
		}

		errorLogic.setCondition(fillStatus == FluidHelper.FillStatus.NO_FLUID, EnumErrorCode.NO_RESOURCE_LIQUID);
		errorLogic.setCondition(fillStatus == FluidHelper.FillStatus.NO_SPACE, EnumErrorCode.NO_SPACE_INVENTORY);
		errorLogic.setCondition(emptyStatus == FluidHelper.FillStatus.NO_SPACE_FLUID, EnumErrorCode.NO_SPACE_TANK);
		if(emptyStatus == FillStatus.INVALID_INPUT || fillStatus == FillStatus.INVALID_INPUT || errorLogic.hasErrors()){
			return false;
		}
		return true;
	}

	@Override
	public TankRenderInfo getResourceTankInfo() {
		return new TankRenderInfo(resourceTank);
	}

	/* ILIQUIDCONTAINER */
	@Nonnull
	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nonnull
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tankManager);
		}
		return super.getCapability(capability, facing);
	}

	/* ITRIGGERPROVIDER */
	// TODO: BuildCraft for 1.9
//	@Optional.Method(modid = "BuildCraftAPI|statements")
//	@Override
//	public Collection<ITriggerExternal> getExternalTriggers(EnumFacing side, TileEntity tile) {
//		LinkedList<ITriggerExternal> res = new LinkedList<>();
//		res.add(FactoryTriggers.lowResource25);
//		res.add(FactoryTriggers.lowResource10);
//		return res;
//	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiBottler(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerBottler(player.inventory, this);
	}
}

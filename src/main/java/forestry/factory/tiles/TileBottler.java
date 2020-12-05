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

import forestry.api.core.IErrorLogic;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.FluidHelper.FillStatus;
import forestry.core.fluids.StandardTank;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.watchers.ISlotPickupWatcher;
import forestry.core.network.PacketBufferForestry;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.factory.features.FactoryTiles;
import forestry.factory.gui.ContainerBottler;
import forestry.factory.inventory.InventoryBottler;
import forestry.factory.recipes.BottlerRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.EnumMap;
//import forestry.factory.triggers.FactoryTriggers;

//import buildcraft.api.statements.ITriggerExternal;

public class TileBottler extends TilePowered implements ISidedInventory, ILiquidTankTile, ISlotPickupWatcher {
    private static final int TICKS_PER_RECIPE_TIME = 5;
    private static final int ENERGY_PER_RECIPE_TIME = 1000;

    private final StandardTank resourceTank;
    private final TankManager tankManager;

    private final EnumMap<Direction, Boolean> canDump;
    private boolean dumpingFluid = false;
    @Nullable
    private BottlerRecipe currentRecipe;
    @OnlyIn(Dist.CLIENT)
    public boolean isFillRecipe;

    public TileBottler() {
        super(FactoryTiles.BOTTLER.tileType(), 1100, 4000);

        setInternalInventory(new InventoryBottler(this));

        resourceTank = new StandardTank(Constants.PROCESSOR_TANK_CAPACITY);
        tankManager = new TankManager(this, resourceTank);

        canDump = new EnumMap<>(Direction.class);
    }

    /* SAVING & LOADING */

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound = super.write(compound);
        tankManager.write(compound);
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        tankManager.read(compound);
        checkEmptyRecipe();
        checkFillRecipe();
    }

    @Override
    public void writeData(PacketBufferForestry data) {
        super.writeData(data);
        tankManager.writeData(data);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void readData(PacketBufferForestry data) throws IOException {
        super.readData(data);
        tankManager.readData(data);
    }

    @Override
    public void updateServerSide() {
        super.updateServerSide();

        if (updateOnInterval(20)) {
            ItemStack leftProcessingStack = getStackInSlot(InventoryBottler.SLOT_EMPTYING_PROCESSING);
            ItemStack rightProcessingStack = getStackInSlot(InventoryBottler.SLOT_FILLING_PROCESSING);
            if (leftProcessingStack.isEmpty()) {
                ItemStack inputStack = getStackInSlot(InventoryBottler.SLOT_INPUT_FULL_CONTAINER);
                if (!inputStack.isEmpty()) {
                    leftProcessingStack = decrStackSize(InventoryBottler.SLOT_INPUT_FULL_CONTAINER, 1);
                    setInventorySlotContents(InventoryBottler.SLOT_EMPTYING_PROCESSING, leftProcessingStack);
                }
            }
            if (rightProcessingStack.isEmpty()) {
                ItemStack inputStack = getStackInSlot(InventoryBottler.SLOT_INPUT_EMPTY_CONTAINER);
                if (!inputStack.isEmpty()) {
                    rightProcessingStack = decrStackSize(InventoryBottler.SLOT_INPUT_EMPTY_CONTAINER, 1);
                    setInventorySlotContents(InventoryBottler.SLOT_FILLING_PROCESSING, rightProcessingStack);
                }
            }
        }

        if (canDump()) {
            if (dumpingFluid || updateOnInterval(20)) {
                dumpingFluid = dumpFluid();
            }
        }
    }

    private boolean canDump() {
        FluidStack fluid = tankManager.getFluid(0);
        if (fluid != null) {
            if (canDump.isEmpty()) {
                for (Direction facing : Direction.VALUES) {
                    canDump.put(
                            facing,
                            FluidHelper.canAcceptFluid(world, pos.offset(facing), facing.getOpposite(), fluid)
                    );
                }
            }

            for (Direction facing : Direction.VALUES) {
                if (canDump.get(facing)) {
                    return true;
                }
            }
        }
        return false;
    }

    //TODO - a bit ugly atm. Are the new checks worth the perf with the new interface? Can this be written better?
    //Is there a race condition here?
    private boolean dumpFluid() {
        if (!resourceTank.isEmpty()) {
            for (Direction facing : Direction.VALUES) {
                if (canDump.get(facing)) {
                    LazyOptional<IFluidHandler> fluidDestination = FluidUtil.getFluidHandler(
                            world,
                            pos.offset(facing),
                            facing.getOpposite()
                    );

                    if (fluidDestination.isPresent()) {
                        fluidDestination.ifPresent(f -> FluidUtil.tryFluidTransfer(
                                f,
                                tankManager,
                                FluidAttributes.BUCKET_VOLUME / 20,
                                true
                        ));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean workCycle() {
        FluidHelper.FillStatus status;
        if (currentRecipe != null) {
            if (currentRecipe.fillRecipe) {
                status = FluidHelper.fillContainers(
                        tankManager,
                        this,
                        InventoryBottler.SLOT_FILLING_PROCESSING,
                        InventoryBottler.SLOT_OUTPUT_FULL_CONTAINER,
                        currentRecipe.fluid.getFluid(),
                        true
                );
            } else {
                status = FluidHelper.drainContainers(
                        tankManager,
                        this,
                        InventoryBottler.SLOT_EMPTYING_PROCESSING,
                        InventoryBottler.SLOT_OUTPUT_EMPTY_CONTAINER,
                        true
                );
            }
        } else {
            return true;
        }

        if (status == FluidHelper.FillStatus.SUCCESS) {
            currentRecipe = null;
            return true;
        }
        return false;
    }

    @Override
    public void onNeighborTileChange(World world, BlockPos pos, BlockPos neighbor) {
        super.onNeighborTileChange(world, pos, neighbor);

        canDump.clear();
    }

    private void checkFillRecipe() {
        ItemStack emptyCan = getStackInSlot(InventoryBottler.SLOT_FILLING_PROCESSING);
        if (!emptyCan.isEmpty()) {
            FluidStack resource = resourceTank.getFluid();
            if (resource.isEmpty()) {
                return;
            }
            //Fill Container
            if (currentRecipe == null || !currentRecipe.matchEmpty(emptyCan, resource)) {
                currentRecipe = BottlerRecipe.createFillingRecipe(resource.getFluid(), emptyCan);
                if (currentRecipe != null) {
                    float viscosityMultiplier = resource.getFluid().getAttributes().getViscosity(resource) / 1000.0f;
                    viscosityMultiplier = (viscosityMultiplier - 1f) / 20f + 1f; // scale down the effect

                    int fillAmount = Math.min(currentRecipe.fluid.getAmount(), resource.getAmount());
                    float fillTime = fillAmount / (float) FluidAttributes.BUCKET_VOLUME;
                    fillTime *= viscosityMultiplier;

                    setTicksPerWorkCycle(Math.round(fillTime * TICKS_PER_RECIPE_TIME));
                    setEnergyPerWorkCycle(Math.round(fillTime * ENERGY_PER_RECIPE_TIME));
                }
            }
        }
    }

    private void checkEmptyRecipe() {
        ItemStack filledCan = getStackInSlot(InventoryBottler.SLOT_EMPTYING_PROCESSING);
        if (!filledCan.isEmpty()) {
            //Empty Container
            if (currentRecipe == null || !currentRecipe.matchFilled(filledCan) && !currentRecipe.fillRecipe) {
                currentRecipe = BottlerRecipe.createEmptyingRecipe(filledCan);
                if (currentRecipe != null) {
                    FluidStack resource = currentRecipe.fluid;
                    float viscosityMultiplier = resource.getFluid().getAttributes().getViscosity(resource) / 1000.0f;
                    viscosityMultiplier = (viscosityMultiplier - 1f) / 20f + 1f; // scale down the effect

                    int fillAmount = Math.min(currentRecipe.fluid.getAmount(), resource.getAmount());
                    float fillTime = fillAmount / (float) FluidAttributes.BUCKET_VOLUME;
                    fillTime *= viscosityMultiplier;

                    setTicksPerWorkCycle(Math.round(fillTime * TICKS_PER_RECIPE_TIME));
                    setEnergyPerWorkCycle(0);
                }
            }
        }
    }

    @Override
    public void onTake(int slotIndex, PlayerEntity player) {
        if (slotIndex == InventoryBottler.SLOT_EMPTYING_PROCESSING) {
            if (currentRecipe != null && !currentRecipe.fillRecipe) {
                currentRecipe = null;
                setTicksPerWorkCycle(0);
            }
        } else if (slotIndex == InventoryBottler.SLOT_FILLING_PROCESSING) {
            if (currentRecipe != null && currentRecipe.fillRecipe) {
                currentRecipe = null;
                setTicksPerWorkCycle(0);
            }
        }
    }

    @Override
    public void writeGuiData(PacketBufferForestry data) {
        super.writeGuiData(data);
        if (currentRecipe == null) {
            data.writeBoolean(false);
        } else {
            data.writeBoolean(currentRecipe.fillRecipe);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void readGuiData(PacketBufferForestry data) throws IOException {
        super.readGuiData(data);
        isFillRecipe = data.readBoolean();
    }

    @Override
    public boolean hasResourcesMin(float percentage) {
        IInventoryAdapter inventory = getInternalInventory();
        ItemStack emptyCan = inventory.getStackInSlot(InventoryBottler.SLOT_FILLING_PROCESSING);
        if (emptyCan.isEmpty()) {
            return false;
        }

        return (float) emptyCan.getCount() / (float) emptyCan.getMaxStackSize() > percentage;
    }

    @Override
    public boolean hasWork() {
        FluidHelper.FillStatus emptyStatus;
        FluidHelper.FillStatus fillStatus;
        IErrorLogic errorLogic = getErrorLogic();
        errorLogic.clearErrors();

        checkEmptyRecipe();
        if (currentRecipe != null) {
            IFluidTank tank = tankManager.getTank(0);
            if (tank != null) {
                emptyStatus = FluidHelper.drainContainers(
                        tankManager,
                        this,
                        InventoryBottler.SLOT_EMPTYING_PROCESSING,
                        InventoryBottler.SLOT_OUTPUT_EMPTY_CONTAINER,
                        false
                );
            } else {
                emptyStatus = FillStatus.SUCCESS;
            }
        } else {
            emptyStatus = null;
        }
        if (emptyStatus != FillStatus.SUCCESS) {
            checkFillRecipe();
            if (currentRecipe == null) {
                return false;
            } else {
                fillStatus = FluidHelper.fillContainers(
                        tankManager,
                        this,
                        InventoryBottler.SLOT_FILLING_PROCESSING,
                        InventoryBottler.SLOT_OUTPUT_FULL_CONTAINER,
                        currentRecipe.fluid.getFluid(),
                        false
                );
            }
        } else {
            return true;
        }

        if (fillStatus == FillStatus.SUCCESS) {
            return true;
        }

        errorLogic.setCondition(fillStatus == FluidHelper.FillStatus.NO_FLUID, EnumErrorCode.NO_RESOURCE_LIQUID);
        errorLogic.setCondition(fillStatus == FluidHelper.FillStatus.NO_SPACE, EnumErrorCode.NO_SPACE_INVENTORY);
        errorLogic.setCondition(emptyStatus == FluidHelper.FillStatus.NO_SPACE_FLUID, EnumErrorCode.NO_SPACE_TANK);
        if (emptyStatus == FillStatus.INVALID_INPUT || fillStatus == FillStatus.INVALID_INPUT ||
            errorLogic.hasErrors()) {
            currentRecipe = null;
            return false;
        }
        return true;
    }

    @Override
    public TankRenderInfo getResourceTankInfo() {
        return new TankRenderInfo(resourceTank);
    }

    /* ILIQUIDCONTAINER */

    @Override
    public TankManager getTankManager() {
        return tankManager;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> tankManager).cast();
        }
        return super.getCapability(capability, facing);
    }

    /* ITRIGGERPROVIDER */
    //	@Optional.Method(modid = Constants.BCLIB_MOD_ID)
    //	@Override
    //	public void addExternalTriggers(Collection<ITriggerExternal> triggers, @Nonnull Direction side, TileEntity tile) {
    //		super.addExternalTriggers(triggers, side, tile);
    //		triggers.add(FactoryTriggers.lowResource25);
    //		triggers.add(FactoryTriggers.lowResource10);
    //	}

    @Override
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
        return new ContainerBottler(windowId, player.inventory, this);
    }
}

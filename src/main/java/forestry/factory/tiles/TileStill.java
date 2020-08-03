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

import com.google.common.base.Preconditions;
import forestry.api.core.IErrorLogic;
import forestry.api.recipes.IStillRecipe;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FilteredTank;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.network.PacketBufferForestry;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.factory.features.FactoryTiles;
import forestry.factory.gui.ContainerStill;
import forestry.factory.inventory.InventoryStill;
import forestry.factory.recipes.StillRecipeManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.io.IOException;

public class TileStill extends TilePowered implements ISidedInventory, ILiquidTankTile {
    private static final int ENERGY_PER_RECIPE_TIME = 200;

    private final FilteredTank resourceTank;
    private final FilteredTank productTank;
    private final TankManager tankManager;

    @Nullable
    private IStillRecipe currentRecipe;
    private FluidStack bufferedLiquid;

    public TileStill() {
        super(FactoryTiles.STILL.tileType(), 1100, 8000);
        setInternalInventory(new InventoryStill(this));
        resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, true, false);
        resourceTank.setFilters(StillRecipeManager.recipeFluidInputs);

        productTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, false, true);
        productTank.setFilters(StillRecipeManager.recipeFluidOutputs);

        tankManager = new TankManager(this, resourceTank, productTank);

        bufferedLiquid = FluidStack.EMPTY;
    }

    @Override
    public CompoundNBT write(CompoundNBT compoundNBT) {
        compoundNBT = super.write(compoundNBT);
        tankManager.write(compoundNBT);

        if (!bufferedLiquid.isEmpty()) {
            CompoundNBT buffer = new CompoundNBT();
            bufferedLiquid.writeToNBT(buffer);
            compoundNBT.put("Buffer", buffer);
        }
        return compoundNBT;
    }

    @Override
    public void read(BlockState state, CompoundNBT compoundNBT) {
        super.read(state, compoundNBT);
        tankManager.read(compoundNBT);

        if (compoundNBT.contains("Buffer")) {
            CompoundNBT buffer = compoundNBT.getCompound("Buffer");
            bufferedLiquid = FluidStack.loadFluidStackFromNBT(buffer);
        }
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
            FluidHelper.drainContainers(tankManager, this, InventoryStill.SLOT_CAN);

            FluidStack fluidStack = productTank.getFluid();
            if (!fluidStack.isEmpty()) {
                FluidHelper.fillContainers(tankManager, this, InventoryStill.SLOT_RESOURCE, InventoryStill.SLOT_PRODUCT, fluidStack.getFluid(), true);
            }
        }
    }

    @Override
    public boolean workCycle() {
        Preconditions.checkNotNull(currentRecipe);
        int cycles = currentRecipe.getCyclesPerUnit();
        FluidStack output = currentRecipe.getOutput();

        FluidStack product = new FluidStack(output, output.getAmount() * cycles);
        productTank.fillInternal(product, IFluidHandler.FluidAction.EXECUTE);

        bufferedLiquid = FluidStack.EMPTY;

        return true;
    }

    private void checkRecipe() {
        FluidStack recipeLiquid = !bufferedLiquid.isEmpty() ? bufferedLiquid : resourceTank.getFluid();

        if (!StillRecipeManager.matches(currentRecipe, recipeLiquid)) {
            currentRecipe = StillRecipeManager.findMatchingRecipe(recipeLiquid);

            int recipeTime = currentRecipe == null ? 0 : currentRecipe.getCyclesPerUnit();
            setEnergyPerWorkCycle(ENERGY_PER_RECIPE_TIME * recipeTime);
            setTicksPerWorkCycle(recipeTime);
        }
    }

    @Override
    public boolean hasWork() {
        checkRecipe();

        boolean hasRecipe = currentRecipe != null;
        boolean hasTankSpace = true;
        boolean hasLiquidResource = true;

        if (hasRecipe) {
            FluidStack fluidStack = currentRecipe.getOutput();
            hasTankSpace = productTank.fillInternal(fluidStack, IFluidHandler.FluidAction.SIMULATE) == fluidStack.getAmount();
            if (bufferedLiquid.isEmpty()) {
                int cycles = currentRecipe.getCyclesPerUnit();
                FluidStack input = currentRecipe.getInput();
                int drainAmount = cycles * input.getAmount();
                FluidStack drained = resourceTank.drain(drainAmount, IFluidHandler.FluidAction.SIMULATE);
                hasLiquidResource = !drained.isEmpty() && drained.getAmount() == drainAmount;
                if (hasLiquidResource) {
                    bufferedLiquid = new FluidStack(input, drainAmount);
                    resourceTank.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
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
    public TankRenderInfo getResourceTankInfo() {
        return new TankRenderInfo(resourceTank);
    }

    @Override
    public TankRenderInfo getProductTankInfo() {
        return new TankRenderInfo(productTank);
    }


    @Override
    public TankManager getTankManager() {
        return tankManager;
    }


    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> tankManager).cast();    //TODO - still unsure if this is the correct pattern for caps
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
        return new ContainerStill(windowId, player.inventory, this);
    }

}

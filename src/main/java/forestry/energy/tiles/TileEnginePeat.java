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
package forestry.energy.tiles;

import forestry.api.fuels.FuelManager;
import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.features.CoreItems;
import forestry.core.inventory.AdjacentInventoryCache;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.network.PacketBufferForestry;
import forestry.core.tiles.TemperatureState;
import forestry.core.utils.InventoryUtil;
import forestry.energy.features.EnergyTiles;
import forestry.energy.gui.ContainerEnginePeat;
import forestry.energy.inventory.InventoryEnginePeat;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.io.IOException;
import java.util.Collection;

//import net.minecraftforge.fml.common.Optional;

//import buildcraft.api.statements.ITriggerExternal;

public class TileEnginePeat extends TileEngine implements ISidedInventory {
    private ItemStack fuel = ItemStack.EMPTY;
    private int burnTime;
    private int totalBurnTime;
    private int ashProduction;
    private final int ashForItem;
    private final AdjacentInventoryCache inventoryCache = new AdjacentInventoryCache(this, getTileCache());

    public TileEnginePeat() {
        super(EnergyTiles.PEAT_ENGINE.tileType(), "engine.copper", Constants.ENGINE_COPPER_HEAT_MAX, 200000);

        ashForItem = Constants.ENGINE_COPPER_ASH_FOR_ITEM;
        setInternalInventory(new InventoryEnginePeat(this));
    }

    private int getFuelSlot() {
        IInventoryAdapter inventory = getInternalInventory();
        if (inventory.getStackInSlot(InventoryEnginePeat.SLOT_FUEL).isEmpty()) {
            return -1;
        }

        if (determineFuelValue(inventory.getStackInSlot(InventoryEnginePeat.SLOT_FUEL)) > 0) {
            return InventoryEnginePeat.SLOT_FUEL;
        }

        return -1;
    }

    private int getFreeWasteSlot() {
        IInventoryAdapter inventory = getInternalInventory();
        for (int i = InventoryEnginePeat.SLOT_WASTE_1; i <= InventoryEnginePeat.SLOT_WASTE_COUNT; i++) {
            ItemStack waste = inventory.getStackInSlot(i);
            if (waste.isEmpty()) {
                return i;
            }

            if (!CoreItems.ASH.itemEqual(waste)) {
                continue;
            }

            if (waste.getCount() < waste.getMaxStackSize()) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void updateServerSide() {
        super.updateServerSide();

        if (!updateOnInterval(40)) {
            return;
        }

        dumpStash();

        int fuelSlot = getFuelSlot();
        boolean hasFuel = fuelSlot >= 0 && determineBurnDuration(getInternalInventory().getStackInSlot(fuelSlot)) > 0;
        getErrorLogic().setCondition(!hasFuel, EnumErrorCode.NO_FUEL);
    }

    @Override
    public void burn() {

        currentOutput = 0;

        if (burnTime > 0) {
            burnTime--;
            addAsh(1);

            if (isRedstoneActivated()) {
                currentOutput = determineFuelValue(fuel);
                energyManager.generateEnergy(currentOutput);
                world.updateComparatorOutputLevel(pos, getBlockState().getBlock());    //TODO - I thuink
            }
        } else if (isRedstoneActivated()) {
            int fuelSlot = getFuelSlot();
            int wasteSlot = getFreeWasteSlot();

            if (fuelSlot >= 0 && wasteSlot >= 0) {
                IInventoryAdapter inventory = getInternalInventory();
                ItemStack fuelStack = inventory.getStackInSlot(fuelSlot);
                burnTime = totalBurnTime = determineBurnDuration(fuelStack);
                if (burnTime > 0 && !fuelStack.isEmpty()) {
                    fuel = fuelStack.copy();
                    decrStackSize(fuelSlot, 1);
                }
            }
        }
    }

    @Override
    public int dissipateHeat() {
        if (heat <= 0) {
            return 0;
        }

        int loss = 0;

        if (!isBurning()) {
            loss += 1;
        }

        TemperatureState tempState = getTemperatureState();
        if (tempState == TemperatureState.OVERHEATING || tempState == TemperatureState.OPERATING_TEMPERATURE) {
            loss += 1;
        }

        heat -= loss;
        return loss;
    }

    @Override
    public int generateHeat() {

        int heatToAdd = 0;

        if (isBurning()) {
            heatToAdd++;
            if ((double) energyManager.getEnergyStored() / (double) energyManager.getMaxEnergyStored() > 0.5) {
                heatToAdd++;
            }
        }

        addHeat(heatToAdd);
        return heatToAdd;
    }

    private void addAsh(int amount) {

        ashProduction += amount;
        if (ashProduction < ashForItem) {
            return;
        }

        // If we have reached the necessary amount, we need to add ash
        int wasteSlot = getFreeWasteSlot();
        if (wasteSlot >= 0) {
            IInventoryAdapter inventory = getInternalInventory();
            ItemStack wasteStack = inventory.getStackInSlot(wasteSlot);
            if (wasteStack.isEmpty()) {
                inventory.setInventorySlotContents(wasteSlot, CoreItems.ASH.stack());
            } else {
                wasteStack.grow(1);
            }
        }
        // Reset
        ashProduction = 0;
        // try to dump stash
        dumpStash();
    }

    /**
     * Returns the fuel value (power per cycle) an item of the passed ItemStack provides
     */
    private static int determineFuelValue(ItemStack fuel) {
        if (FuelManager.copperEngineFuel.containsKey(fuel)) {
            return FuelManager.copperEngineFuel.get(fuel).getPowerPerCycle();
        } else {
            return 0;
        }
    }

    /**
     * Returns the fuel value (power per cycle) an item of the passed ItemStack provides
     */
    private static int determineBurnDuration(ItemStack fuel) {
        if (FuelManager.copperEngineFuel.containsKey(fuel)) {
            return FuelManager.copperEngineFuel.get(fuel).getBurnDuration();
        } else {
            return 0;
        }
    }

    /* AUTO-EJECTING */
    private IInventory getWasteInventory() {
        return new InventoryMapper(this, InventoryEnginePeat.SLOT_WASTE_1, InventoryEnginePeat.SLOT_WASTE_COUNT);
    }

    private void dumpStash() {
        IInventory wasteInventory = getWasteInventory();

        IItemHandler wasteItemHandler = new InvWrapper(wasteInventory);

        if (!InventoryUtil.moveOneItemToPipe(wasteItemHandler, getTileCache())) {
            Direction powerSide = world.getBlockState(getPos()).get(BlockBase.FACING);
            Collection<IItemHandler> inventories = inventoryCache.getAdjacentInventoriesOtherThan(powerSide);
            InventoryUtil.moveItemStack(wasteItemHandler, inventories);
        }
    }

    // / STATE INFORMATION
    @Override
    public boolean isBurning() {
        return mayBurn() && burnTime > 0;
    }

    @Override
    public int getBurnTimeRemainingScaled(int i) {
        if (totalBurnTime == 0) {
            return 0;
        }

        return burnTime * i / totalBurnTime;
    }

    @Override
    public boolean hasFuelMin(float percentage) {
        int fuelSlot = this.getFuelSlot();
        if (fuelSlot < 0) {
            return false;
        }

        IInventoryAdapter inventory = getInternalInventory();
        return (float) inventory.getStackInSlot(fuelSlot).getCount() /
               (float) inventory.getStackInSlot(fuelSlot).getMaxStackSize() > percentage;
    }

    // / LOADING AND SAVING
    @Override
    public void read(BlockState state, CompoundNBT compoundNBT) {
        super.read(state, compoundNBT);

        if (compoundNBT.contains("EngineFuelItemStack")) {
            CompoundNBT fuelItemNbt = compoundNBT.getCompound("EngineFuelItemStack");
            fuel = ItemStack.read(fuelItemNbt);
        }

        burnTime = compoundNBT.getInt("EngineBurnTime");
        totalBurnTime = compoundNBT.getInt("EngineTotalTime");
        if (compoundNBT.contains("AshProduction")) {
            ashProduction = compoundNBT.getInt("AshProduction");
        }
    }


    @Override
    public CompoundNBT write(CompoundNBT compoundNBT) {
        compoundNBT = super.write(compoundNBT);

        if (!fuel.isEmpty()) {
            compoundNBT.put("EngineFuelItemStack", fuel.serializeNBT());
        }

        compoundNBT.putInt("EngineBurnTime", burnTime);
        compoundNBT.putInt("EngineTotalTime", totalBurnTime);
        compoundNBT.putInt("AshProduction", ashProduction);
        return compoundNBT;
    }

    @Override
    public void writeGuiData(PacketBufferForestry data) {
        super.writeGuiData(data);
        data.writeInt(burnTime);
        data.writeInt(totalBurnTime);
    }

    @Override
    public void readGuiData(PacketBufferForestry data) throws IOException {
        super.readGuiData(data);
        burnTime = data.readInt();
        totalBurnTime = data.readInt();
    }

    /* ITriggerProvider */
    //	@Optional.Method(modid = Constants.BCLIB_MOD_ID)
    //	@Override
    //	public void addExternalTriggers(Collection<ITriggerExternal> triggers, @Nonnull Direction side, TileEntity tile) {
    //		super.addExternalTriggers(triggers, side, tile);
    //		triggers.add(FactoryTriggers.lowFuel25);
    //		triggers.add(FactoryTriggers.lowFuel10);
    //	}

    @Override
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
        return new ContainerEnginePeat(windowId, player.inventory, this);
    }
}

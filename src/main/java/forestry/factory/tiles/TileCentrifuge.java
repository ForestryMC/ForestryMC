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

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitSocketType;
import forestry.api.core.IErrorLogic;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.core.circuits.ISocketable;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.network.PacketBufferForestry;
import forestry.core.tiles.IItemStackDisplay;
import forestry.core.tiles.TilePowered;
import forestry.core.utils.InventoryUtil;
import forestry.factory.features.FactoryTiles;
import forestry.factory.gui.ContainerCentrifuge;
import forestry.factory.inventory.InventoryCentrifuge;
import forestry.factory.recipes.CentrifugeRecipeManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Stack;

//import forestry.factory.triggers.FactoryTriggers;
//
//import buildcraft.api.statements.ITriggerExternal;

public class TileCentrifuge extends TilePowered implements ISocketable, ISidedInventory, IItemStackDisplay {
    private static final int TICKS_PER_RECIPE_TIME = 1;
    private static final int ENERGY_PER_WORK_CYCLE = 3200;
    private static final int ENERGY_PER_RECIPE_TIME = ENERGY_PER_WORK_CYCLE / 20;

    private final InventoryAdapter sockets = new InventoryAdapter(1, "sockets");
    private final CraftResultInventory craftPreviewInventory;
    @Nullable
    private ICentrifugeRecipe currentRecipe;

    private final Stack<ItemStack> pendingProducts = new Stack<>();

    public TileCentrifuge() {
        super(FactoryTiles.CENTRIFUGE.tileType(), 800, Constants.MACHINE_MAX_ENERGY);
        setInternalInventory(new InventoryCentrifuge(this));
        craftPreviewInventory = new CraftResultInventory();
    }

    /* LOADING & SAVING */

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound = super.write(compound);

        sockets.write(compound);

        ListNBT nbttaglist = new ListNBT();
        ItemStack[] offspring = pendingProducts.toArray(new ItemStack[0]);
        for (int i = 0; i < offspring.length; i++) {
            if (offspring[i] != null) {
                CompoundNBT products = new CompoundNBT();
                products.putByte("Slot", (byte) i);
                offspring[i].write(products);
                nbttaglist.add(products);
            }
        }

        compound.put("PendingProducts", nbttaglist);
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);

        ListNBT nbttaglist = compound.getList("PendingProducts", 10);
        for (int i = 0; i < nbttaglist.size(); i++) {
            CompoundNBT CompoundNBT1 = nbttaglist.getCompound(i);
            pendingProducts.add(ItemStack.read(CompoundNBT1));
        }

        sockets.read(compound);

        ItemStack chip = sockets.getStackInSlot(0);
        if (!chip.isEmpty()) {
            ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitBoard(chip);
            if (chipset != null) {
                chipset.onLoad(this);
            }
        }
    }

    @Override
    public void writeGuiData(PacketBufferForestry data) {
        super.writeGuiData(data);
        sockets.writeData(data);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void readGuiData(PacketBufferForestry data) throws IOException {
        super.readGuiData(data);
        sockets.readData(data);
    }

    @Override
    public boolean workCycle() {
        if (tryAddPending()) {
            return true;
        }

        if (!pendingProducts.isEmpty()) {
            craftPreviewInventory.setInventorySlotContents(0, ItemStack.EMPTY);
            return false;
        }

        if (currentRecipe == null) {
            return false;
        }

        // We are done, add products to queue
        Collection<ItemStack> products = currentRecipe.getProducts(world.rand);
        pendingProducts.addAll(products);

        //Add Item to preview slot.
        ItemStack previewStack = getInternalInventory().getStackInSlot(InventoryCentrifuge.SLOT_RESOURCE).copy();
        previewStack.setCount(1);
        craftPreviewInventory.setInventorySlotContents(0, previewStack);

        getInternalInventory().decrStackSize(InventoryCentrifuge.SLOT_RESOURCE, 1);
        return true;
    }

    private void checkRecipe() {
        ItemStack resource = getStackInSlot(InventoryCentrifuge.SLOT_RESOURCE);
        ICentrifugeRecipe matchingRecipe = CentrifugeRecipeManager.findMatchingRecipe(resource);

        if (currentRecipe != matchingRecipe) {
            currentRecipe = matchingRecipe;
            if (currentRecipe != null) {
                int recipeTime = currentRecipe.getProcessingTime();
                setTicksPerWorkCycle(recipeTime * TICKS_PER_RECIPE_TIME);
                setEnergyPerWorkCycle(recipeTime * ENERGY_PER_RECIPE_TIME);
            }
        }
    }

    private boolean tryAddPending() {
        if (pendingProducts.isEmpty()) {
            return false;
        }

        ItemStack next = pendingProducts.peek();

        boolean added = InventoryUtil.tryAddStack(
                this,
                next,
                InventoryCentrifuge.SLOT_PRODUCT_1,
                InventoryCentrifuge.SLOT_PRODUCT_COUNT,
                true
        );

        if (added) {
            pendingProducts.pop();
            if (pendingProducts.isEmpty()) {
                craftPreviewInventory.setInventorySlotContents(0, ItemStack.EMPTY);
            }
        }

        getErrorLogic().setCondition(!added, EnumErrorCode.NO_SPACE_INVENTORY);
        return added;
    }

    @Override
    public boolean hasResourcesMin(float percentage) {
        IInventoryAdapter inventory = getInternalInventory();
        if (inventory.getStackInSlot(InventoryCentrifuge.SLOT_RESOURCE).isEmpty()) {
            return false;
        }

        return (float) inventory.getStackInSlot(InventoryCentrifuge.SLOT_RESOURCE)
                .getCount() / (float) inventory.getStackInSlot(InventoryCentrifuge.SLOT_RESOURCE)
                .getMaxStackSize() > percentage;
    }

    @Override
    public boolean hasWork() {
        if (!pendingProducts.isEmpty()) {
            return true;
        }

        checkRecipe();

        boolean hasResource = !getStackInSlot(InventoryCentrifuge.SLOT_RESOURCE).isEmpty();

        IErrorLogic errorLogic = getErrorLogic();
        errorLogic.setCondition(!hasResource, EnumErrorCode.NO_RESOURCE);

        return hasResource;
    }

    //TODO - buildcraft
    //	/* ITRIGGERPROVIDER */
    //	@Optional.Method(modid = Constants.BCLIB_MOD_ID)
    //	@Override
    //	public void addExternalTriggers(Collection<ITriggerExternal> triggers, @Nonnull Direction side, TileEntity tile) {
    //		super.addExternalTriggers(triggers, side, tile);
    //		triggers.add(FactoryTriggers.lowResource25);
    //		triggers.add(FactoryTriggers.lowResource10);
    //	}

    /* ISocketable */
    @Override
    public int getSocketCount() {
        return sockets.getSizeInventory();
    }

    @Override
    public ItemStack getSocket(int slot) {
        return sockets.getStackInSlot(slot);
    }

    @Override
    public void setSocket(int slot, ItemStack stack) {
        if (!stack.isEmpty() && !ChipsetManager.circuitRegistry.isChipset(stack)) {
            return;
        }

        // Dispose correctly of old chipsets
        if (!sockets.getStackInSlot(slot).isEmpty()) {
            if (ChipsetManager.circuitRegistry.isChipset(sockets.getStackInSlot(slot))) {
                ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitBoard(sockets.getStackInSlot(slot));
                if (chipset != null) {
                    chipset.onRemoval(this);
                }
            }
        }

        sockets.setInventorySlotContents(slot, stack);
        if (stack.isEmpty()) {
            return;
        }

        ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitBoard(stack);
        if (chipset != null) {
            chipset.onInsertion(this);
        }
    }

    @Override
    public ICircuitSocketType getSocketType() {
        return CircuitSocketType.MACHINE;
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
        return new ContainerCentrifuge(windowId, player.inventory, this);
    }

    public IInventory getCraftPreviewInventory() {
        return craftPreviewInventory;
    }

    @Override
    public void handleItemStackForDisplay(ItemStack itemStack) {
        craftPreviewInventory.setInventorySlotContents(0, itemStack);
    }
}

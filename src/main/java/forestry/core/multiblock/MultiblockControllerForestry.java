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
package forestry.core.multiblock;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.mojang.authlib.GameProfile;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.api.core.ILocatable;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.OwnerHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public abstract class MultiblockControllerForestry extends MultiblockControllerBase implements ISidedInventory, IOwnedTile, IErrorLogicSource, ILocatable {
    private final OwnerHandler ownerHandler;
    private final IErrorLogic errorLogic;

    protected MultiblockControllerForestry(World world) {
        super(world);

        this.ownerHandler = new OwnerHandler();
        this.errorLogic = ForestryAPI.errorStateRegistry.createErrorLogic();
    }

    @Override
    public IOwnerHandler getOwnerHandler() {
        return ownerHandler;
    }

    @Override
    public IErrorLogic getErrorLogic() {
        return errorLogic;
    }

    @Override
    public World getWorldObj() {
        return world;
    }

    @Override
    protected void onMachineAssembled() {
        super.onMachineAssembled();

        if (world.isRemote) {
            return;
        }

        // Figure out who owns the multiblock, by majority

        Multiset<GameProfile> owners = HashMultiset.create();
        for (IMultiblockComponent part : connectedParts) {
            GameProfile owner = part.getOwner();
            if (owner != null) {
                owners.add(owner);
            }
        }

        GameProfile owner = null;
        int max = 0;
        for (Multiset.Entry<GameProfile> entry : owners.entrySet()) {
            int count = entry.getCount();
            if (count > max) {
                max = count;
                owner = entry.getElement();
            }
        }

        if (owner != null) {
            getOwnerHandler().setOwner(owner);
        }
    }

    /* INbtWritable */
    @Override
    public CompoundNBT write(CompoundNBT data) {
        ownerHandler.write(data);
        return data;
    }

    @Override
    public void read(CompoundNBT data) {
        ownerHandler.read(data);
    }

    /* INVENTORY */

    public IInventoryAdapter getInternalInventory() {
        return FakeInventoryAdapter.instance();
    }

    @Override
    public void markDirty() {
        getInternalInventory().markDirty();
    }

    @Override
    public final int getSizeInventory() {
        return getInternalInventory().getSizeInventory();
    }

    @Override
    public final ItemStack getStackInSlot(int slotIndex) {
        return getInternalInventory().getStackInSlot(slotIndex);
    }

    @Override
    public final ItemStack decrStackSize(int slotIndex, int amount) {
        return getInternalInventory().decrStackSize(slotIndex, amount);
    }

    @Override
    public ItemStack removeStackFromSlot(int slotIndex) {
        return getInternalInventory().removeStackFromSlot(slotIndex);
    }

    @Override
    public final void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
        getInternalInventory().setInventorySlotContents(slotIndex, itemstack);
    }

    @Override
    public final int getInventoryStackLimit() {
        return getInternalInventory().getInventoryStackLimit();
    }

    @Override
    public final void openInventory(PlayerEntity player) {
        getInternalInventory().openInventory(player);
    }

    @Override
    public final void closeInventory(PlayerEntity player) {
        getInternalInventory().closeInventory(player);
    }

    //TODO inventory title
    //	@Override
    //	public String getName() {
    //		return getInternalInventory().getName();
    //	}
    //
    //	@Override
    //	public ITextComponent getDisplayName() {
    //		return getInternalInventory().getDisplayName();
    //	}

    @Override
    public final boolean isUsableByPlayer(PlayerEntity player) {
        return getInternalInventory().isUsableByPlayer(player);
    }

    //TODO inventory title
    //	@Override
    //	public boolean hasCustomName() {
    //		return getInternalInventory().hasCustomName();
    //	}

    @Override
    public final boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
        return getInternalInventory().isItemValidForSlot(slotIndex, itemStack);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return getInternalInventory().getSlotsForFace(side);
    }

    @Override
    public final boolean canInsertItem(int slotIndex, ItemStack itemStack, Direction side) {
        return getInternalInventory().canInsertItem(slotIndex, itemStack, side);
    }

    @Override
    public final boolean canExtractItem(int slotIndex, ItemStack itemStack, Direction side) {
        return getInternalInventory().canExtractItem(slotIndex, itemStack, side);
    }

    //TODO inventory field
    //	@Override
    //	public int getField(int id) {
    //		return getInternalInventory().getField(id);
    //	}
    //
    //	@Override
    //	public int getFieldCount() {
    //		return getInternalInventory().getFieldCount();
    //	}
    //
    //	@Override
    //	public void setField(int id, int value) {
    //		getInternalInventory().setField(id, value);
    //	}

    @Override
    public void clear() {
        getInternalInventory().clear();
    }
}

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
package forestry.farming.tiles;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.util.Direction;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import forestry.api.multiblock.IFarmComponent;
import forestry.core.inventory.AdjacentInventoryCache;
import forestry.core.tiles.AdjacentTileCache;
import forestry.core.utils.InventoryUtil;
import forestry.farming.features.FarmingTiles;

//import net.minecraftforge.fml.common.Optional;

//import buildcraft.api.statements.IStatementContainer;
//import buildcraft.api.statements.ITriggerExternal;
//import buildcraft.api.statements.ITriggerInternal;
//import buildcraft.api.statements.ITriggerInternalSided;
//import buildcraft.api.statements.ITriggerProvider;
//
//@Optional.Interface(iface = "buildcraft.api.statements.ITriggerProvider", modid = Constants.BCLIB_MOD_ID)
public class TileFarmHatch extends TileFarm implements ISidedInventory, IFarmComponent.Active {//}, ITriggerProvider {

    private static final Direction[] dumpDirections = new Direction[]{Direction.DOWN};

    private final AdjacentTileCache tileCache;
    private final AdjacentInventoryCache inventoryCache;

    public TileFarmHatch() {
        super(FarmingTiles.HATCH.tileType());
        this.tileCache = new AdjacentTileCache(this);
        this.inventoryCache = new AdjacentInventoryCache(this, tileCache, tile -> !(tile instanceof TileFarm) && tile.getPos().getY() < getPos().getY());
    }

    @Override
    public boolean allowsAutomation() {
        return true;
    }

    @Override
    public void updateServer(int tickCount) {
        if (tickCount % 40 == 0) {
            IInventory productInventory = getMultiblockLogic().getController().getFarmInventory().getProductInventory();
            IItemHandler productItemHandler = new InvWrapper(productInventory);

            if (!InventoryUtil.moveOneItemToPipe(productItemHandler, tileCache, dumpDirections)) {
                InventoryUtil.moveItemStack(productItemHandler, inventoryCache.getAdjacentInventories());
            }
        }
    }

    @Override
    public void updateClient(int tickCount) {

    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            SidedInvWrapper sidedInvWrapper = new SidedInvWrapper(this, facing);
            return LazyOptional.of(() -> sidedInvWrapper).cast();
        }
        return super.getCapability(capability, facing);
    }

    //	@Optional.Method(modid = Constants.BCLIB_MOD_ID)
    //	@Override
    //	public void addInternalTriggers(Collection<ITriggerInternal> triggers, IStatementContainer container) {
    //	}
    //
    //	@Optional.Method(modid = Constants.BCLIB_MOD_ID)
    //	@Override
    //	public void addInternalSidedTriggers(Collection<ITriggerInternalSided> triggers, IStatementContainer container, @Nonnull Direction side) {
    //	}
    //
    //	/* ITRIGGERPROVIDER */
    //	@Optional.Method(modid = Constants.BCLIB_MOD_ID)
    //	@Override
    //	public void addExternalTriggers(Collection<ITriggerExternal> triggers, @Nonnull Direction side, TileEntity tile) {
    //		if (getMultiblockLogic().isConnected()) {
    //			triggers.addAll(FarmingTriggers.allExternalTriggers);
    //		}
    //	}
}

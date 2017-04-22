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

import forestry.api.multiblock.IFarmComponent;
import forestry.core.inventory.AdjacentInventoryCache;
import forestry.core.tiles.AdjacentTileCache;
import forestry.core.utils.InventoryUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

// TODO: Buildcraft for 1.9
//@Optional.Interface(iface = "buildcraft.api.statements.ITriggerProvider", modid = "BuildCraftAPI|statements")
public class TileFarmHatch extends TileFarm implements ISidedInventory, IFarmComponent.Active {

	private static final EnumFacing[] dumpDirections = new EnumFacing[]{EnumFacing.DOWN};

	private final AdjacentTileCache tileCache;
	private final AdjacentInventoryCache inventoryCache;

	public TileFarmHatch() {
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
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			SidedInvWrapper sidedInvWrapper = new SidedInvWrapper(this, facing);
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(sidedInvWrapper);
		}
		return super.getCapability(capability, facing);
	}

	// TODO: Buildcraft for 1.9
//	@Optional.Method(modid = "BuildCraftAPI|statements")
//	@Override
//	public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
//		return Collections.emptyList();
//	}
//
//	/* ITRIGGERPROVIDER */
//	@Optional.Method(modid = "BuildCraftAPI|statements")
//	@Override
//	public Collection<ITriggerExternal> getExternalTriggers(EnumFacing side, TileEntity tile) {
//		if (!getMultiblockLogic().isConnected()) {
//			return Collections.emptyList();
//		}
//
//		return FarmingTriggers.allExternalTriggers;
//	}

}

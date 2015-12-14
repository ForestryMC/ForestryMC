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

import java.util.Collection;
import java.util.Collections;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;

import forestry.api.multiblock.IFarmComponent;
import forestry.core.inventory.AdjacentInventoryCache;
import forestry.core.tiles.AdjacentTileCache;
import forestry.core.utils.InventoryUtil;
import forestry.farming.triggers.FarmingTriggers;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerProvider;

@Optional.Interface(iface = "buildcraft.api.statements.ITriggerProvider", modid = "BuildCraftAPI|statements")
public class TileFarmHatch extends TileFarm implements ISidedInventory, IFarmComponent.Active, ITriggerProvider {

	private static final ForgeDirection[] dumpDirections = new ForgeDirection[]{ForgeDirection.DOWN};

	private final AdjacentTileCache tileCache;
	private final AdjacentInventoryCache inventoryCache;

	public TileFarmHatch() {
		this.tileCache = new AdjacentTileCache(this);
		this.inventoryCache = new AdjacentInventoryCache(this, tileCache, new AdjacentInventoryCache.ITileFilter() {
			@Override
			public boolean matches(TileEntity tile) {
				return !(tile instanceof TileFarm) && tile.yCoord < yCoord;
			}
		});
	}

	@Override
	public boolean allowsAutomation() {
		return true;
	}

	@Override
	public void updateServer(int tickCount) {
		if (tickCount % 40 == 0) {
			IInventory productInventory = getMultiblockLogic().getController().getFarmInventory().getProductInventory();

			if (!InventoryUtil.moveOneItemToPipe(productInventory, tileCache, dumpDirections)) {
				InventoryUtil.moveItemStack(productInventory, inventoryCache.getAdjacentInventories());
			}
		}
	}

	@Override
	public void updateClient(int tickCount) {

	}

	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
		return Collections.emptyList();
	}

	/* ITRIGGERPROVIDER */
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		if (!getMultiblockLogic().isConnected()) {
			return Collections.emptyList();
		}

		return FarmingTriggers.allExternalTriggers;
	}

}

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
package forestry.farming.gadgets;

import java.util.Collection;
import java.util.LinkedList;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;

import forestry.api.core.ITileStructure;
import forestry.core.inventory.AdjacentInventoryCache;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.ITileFilter;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.farming.triggers.FarmingTriggers;

import buildcraft.api.statements.ITriggerExternal;

public class TileHatch extends TileFarm implements ISidedInventory {

	private static final ForgeDirection[] dumpDirections = new ForgeDirection[]{ForgeDirection.DOWN};
	
	private final AdjacentInventoryCache inventoryCache = new AdjacentInventoryCache(this, getTileCache(), new ITileFilter() {

		@Override
		public boolean matches(TileEntity tile) {
			return !(tile instanceof TileFarm) && tile.yCoord < yCoord;
		}
	}, null);

	public TileHatch() {
		fixedType = TYPE_HATCH;
	}

	@Override
	public boolean hasFunction() {
		return true;
	}

	@Override
	protected void updateServerSide() {
		if (updateOnInterval(40)) {
			dumpStash();
		}
	}

	/* AUTO-EJECTING */
	private IInventory getProductInventory() {
		IInventoryAdapter inventory = getStructureInventory();
		return new InventoryMapper(inventory, TileFarmPlain.SLOT_PRODUCTION_1, TileFarmPlain.SLOT_PRODUCTION_COUNT);
	}

	protected void dumpStash() {
		IInventory productInventory = getProductInventory();
		if (productInventory == null) {
			return;
		}

		if (!InvTools.moveOneItemToPipe(productInventory, tileCache, dumpDirections)) {
			InvTools.moveItemStack(productInventory, inventoryCache.getAdjacentInventories());
		}
	}

	/* IINVENTORY */
	@Override
	public IInventoryAdapter getInternalInventory() {
		ITileStructure central = getCentralTE();
		if (central instanceof TileFarmPlain) {
			return ((TileFarmPlain) central).getInternalInventory();
		}

		return super.getInternalInventory();
	}

	/* ITRIGGERPROVIDER */
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		if (!hasMaster()) {
			return null;
		}

		LinkedList<ITriggerExternal> list = new LinkedList<ITriggerExternal>();
		list.add(FarmingTriggers.lowResourceLiquid50);
		list.add(FarmingTriggers.lowResourceLiquid25);
		list.add(FarmingTriggers.lowSoil128);
		list.add(FarmingTriggers.lowSoil64);
		list.add(FarmingTriggers.lowSoil32);
		list.add(FarmingTriggers.lowFertilizer50);
		list.add(FarmingTriggers.lowFertilizer25);
		list.add(FarmingTriggers.lowGermlings25);
		list.add(FarmingTriggers.lowGermlings10);
		return list;
	}

}

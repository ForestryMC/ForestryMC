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
package forestry.farming.multiblock;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitSocketType;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmInventory;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmable;
import forestry.core.fluids.FakeTankManager;
import forestry.core.fluids.ITankManager;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.FakeMultiblockController;
import forestry.farming.gui.IFarmLedgerDelegate;

import io.netty.util.internal.EmptyArrays;

public class FakeFarmController extends FakeMultiblockController implements IFarmControllerInternal {
	public static final FakeFarmController instance = new FakeFarmController();

	private FakeFarmController() {

	}

	@Override
	public int[] getCoords() {
		return EmptyArrays.EMPTY_INTS;
	}

	@Override
	public int[] getArea() {
		return EmptyArrays.EMPTY_INTS;
	}

	@Override
	public int[] getOffset() {
		return EmptyArrays.EMPTY_INTS;
	}

	@Override
	public boolean doWork() {
		return false;
	}

	@Override
	public boolean hasLiquid(FluidStack liquid) {
		return false;
	}

	@Override
	public void removeLiquid(FluidStack liquid) {

	}

	@Override
	public boolean plantGermling(IFarmable farmable, World world, int x, int y, int z) {
		return false;
	}

	@Override
	public IFarmInventory getFarmInventory() {
		return FakeFarmInventory.instance;
	}

	@Override
	public void setFarmLogic(FarmDirection direction, IFarmLogic logic) {

	}

	@Override
	public IFarmLogic getFarmLogic(FarmDirection direction) {
		return null;
	}

	@Override
	public void resetFarmLogic(FarmDirection direction) {

	}

	@Override
	public int getStoredFertilizerScaled(int scale) {
		return 0;
	}

	@Override
	public int getSocketCount() {
		return 0;
	}

	@Override
	public ItemStack getSocket(int slot) {
		return null;
	}

	@Override
	public void setSocket(int slot, ItemStack stack) {

	}

	@Override
	public ICircuitSocketType getSocketType() {
		return CircuitSocketType.NONE;
	}

	@Override
	public IFarmLedgerDelegate getFarmLedgerDelegate() {
		return FakeFarmLedgerDelegate.instance;
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return FakeInventoryAdapter.instance();
	}

	@Override
	public ITankManager getTankManager() {
		return FakeTankManager.instance;
	}

	private static class FakeFarmInventory implements IFarmInventory {
		public static final FakeFarmInventory instance = new FakeFarmInventory();

		private FakeFarmInventory() {

		}

		@Override
		public boolean hasResources(ItemStack[] resources) {
			return false;
		}

		@Override
		public void removeResources(ItemStack[] resources) {

		}

		@Override
		public boolean acceptsAsGermling(ItemStack itemstack) {
			return false;
		}

		@Override
		public boolean acceptsAsResource(ItemStack itemstack) {
			return false;
		}

		@Override
		public boolean acceptsAsFertilizer(ItemStack itemstack) {
			return false;
		}

		@Override
		public IInventory getProductInventory() {
			return FakeInventoryAdapter.instance();
		}

		@Override
		public IInventory getGermlingsInventory() {
			return FakeInventoryAdapter.instance();
		}

		@Override
		public IInventory getResourcesInventory() {
			return FakeInventoryAdapter.instance();
		}

		@Override
		public IInventory getFertilizerInventory() {
			return FakeInventoryAdapter.instance();
		}
	}

	private static class FakeFarmLedgerDelegate implements IFarmLedgerDelegate {
		public static final FakeFarmLedgerDelegate instance = new FakeFarmLedgerDelegate();

		private FakeFarmLedgerDelegate() {

		}

		@Override
		public float getHydrationModifier() {
			return 0;
		}

		@Override
		public float getHydrationTempModifier() {
			return 0;
		}

		@Override
		public float getHydrationHumidModifier() {
			return 0;
		}

		@Override
		public float getHydrationRainfallModifier() {
			return 0;
		}

		@Override
		public double getDrought() {
			return 0;
		}
	}
}

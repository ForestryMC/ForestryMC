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
package forestry.factory.multiblock;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;

import forestry.core.fluids.FakeTankManager;
import forestry.core.fluids.ITankManager;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.FakeMultiblockController;
import forestry.energy.EnergyManager;

public class FakeDistillVatController extends FakeMultiblockController implements IDistillVatControllerInternal {
	public static final FakeDistillVatController instance = new FakeDistillVatController();

	private FakeDistillVatController() {

	}

	@Override
	public BlockPos getCoordinates() {
		return null;
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return FakeInventoryAdapter.instance();
	}

	@Override
	public int getProgressScaled(int i) {
		return 0;
	}

	@Override
	public int getWorkCounter() {
		return 0;
	}


	@Nonnull
	@Override
	public ITankManager getTankManager() {
		return FakeTankManager.instance;
	}

	@Nonnull
	@Override
	public EnergyManager getEnergyManager() { return new EnergyManager(0,0); }

	@Override
	public String getUnlocalizedType() {
		return "for.multiblock.distillvat.type";
	}
}

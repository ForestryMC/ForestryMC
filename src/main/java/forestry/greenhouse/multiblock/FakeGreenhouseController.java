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
package forestry.greenhouse.multiblock;

import forestry.api.core.EnumCamouflageType;
import forestry.core.fluids.FakeTankManager;
import forestry.core.fluids.ITankManager;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.FakeMultiblockController;
import forestry.energy.EnergyManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

public class FakeGreenhouseController extends FakeMultiblockController implements IGreenhouseControllerInternal {
	public static final FakeGreenhouseController instance = new FakeGreenhouseController();
	
	private FakeGreenhouseController() {

	}
	
	@Override
	public IInventoryAdapter getInternalInventory() {
		return FakeInventoryAdapter.instance();
	}

	@Override
	public ITankManager getTankManager() {
		return FakeTankManager.instance;
	}
	
	@Override
	public EnergyManager getEnergyManager() {
		return null;
	}
	
	@Override
	public boolean isInGreenhouse(BlockPos pos) {
		return false;
	}

	@Override
	public ItemStack getCamouflageBlock(EnumCamouflageType type) {
		return null;
	}

	@Override
	public void setCamouflageBlock(EnumCamouflageType type, ItemStack camouflageBlock) {
		
	}

	@Override
	public BlockPos getCoordinates() {
		return null;
	}
	
	

}

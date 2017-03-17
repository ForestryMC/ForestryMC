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

import java.util.Collections;
import java.util.Set;

import forestry.api.climate.IClimateInfo;
import forestry.api.climate.IClimateRegion;
import forestry.api.greenhouse.IInternalBlock;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.multiblock.IGreenhouseComponent.Nursery;
import forestry.api.multiblock.IGreenhouseComponent.Listener;
import forestry.core.climate.ClimateInfo;
import forestry.core.fluids.FakeTankManager;
import forestry.core.fluids.ITankManager;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.FakeMultiblockController;
import forestry.energy.EnergyManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

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
	public ItemStack getCamouflageBlock(String type) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getDefaultCamouflageBlock(String type) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean setCamouflageBlock(String type, ItemStack camouflageBlock, boolean sendClientUpdate) {
		return false;
	}

	@Override
	public boolean canHandleType(String type) {
		return false;
	}

	@Override
	public BlockPos getCoordinates() {
		return BlockPos.ORIGIN;
	}

	@Override
	public Set<IInternalBlock> getInternalBlocks() {
		return Collections.emptySet();
	}

	@Override
	public String getUnlocalizedType() {
		return "for.multiblock.greenhouse.type";
	}

	@Override
	public IClimateRegion getRegion() {
		return null;
	}

	@Override
	public Set<Listener> getListenerComponents() {
		return Collections.emptySet();
	}
	
	@Override
	public Set<Nursery> getButterflyNurserys() {
		return Collections.emptySet();
	}

	@Override
	public boolean canWork() {
		return false;
	}

	@Override
	public IClimateInfo getControlClimate() {
		return ClimateInfo.MAX;
	}
	
	@Override
	public void setControlClimate(IClimateInfo climateControl) {
		
	}

	@Override
	public boolean spawnButterfly(IButterfly butterfly) {
		return false;
	}
}

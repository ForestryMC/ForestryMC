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

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import forestry.api.core.EnumCamouflageType;
import forestry.api.greenhouse.EnumGreenhouseEventType;
import forestry.api.greenhouse.IGreenhouseLogic;
import forestry.api.greenhouse.IGreenhouseState;
import forestry.api.greenhouse.IInternalBlock;
import forestry.core.fluids.FakeTankManager;
import forestry.core.fluids.ITankManager;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.FakeMultiblockController;
import forestry.energy.EnergyManager;
import forestry.greenhouse.FakeGreenhouseState;

public class FakeGreenhouseController extends FakeMultiblockController implements IGreenhouseControllerInternal {
	public static final FakeGreenhouseController instance = new FakeGreenhouseController();
	
	private FakeGreenhouseController() {
	}
	
	@Nonnull
	@Override
	public IInventoryAdapter getInternalInventory() {
		return FakeInventoryAdapter.instance();
	}

	@Nonnull
	@Override
	public ITankManager getTankManager() {
		return FakeTankManager.instance;
	}
	
	@Override
	public EnergyManager getEnergyManager() {
		return null;
	}

	@Override
	public ItemStack getCamouflageBlock(EnumCamouflageType type) {
		return null;
	}
	
	@Override
	public ItemStack getDefaultCamouflageBlock(EnumCamouflageType type) {
		return null;
	}

	@Override
	public void setCamouflageBlock(EnumCamouflageType type, ItemStack camouflageBlock) {
		
	}

	@Override
	public BlockPos getCoordinates() {
		return null;
	}
	
	@Override
	public List<IGreenhouseLogic> getLogics() {
		return Collections.emptyList();
	}

	@Override
	public void onChange(EnumGreenhouseEventType type, Object event) {
	}

	@Override
	public void addTemperatureChange(float change, float boundaryDown, float boundaryUp) {
	}

	@Override
	public void addHumidityChange(float change, float boundaryDown, float boundaryUp) {
	}

	@Nonnull
	@Override
	public IGreenhouseState createState() {
		return FakeGreenhouseState.instance;
	}
	
	@Override
	public Set<IInternalBlock> getInternalBlocks() {
		return Collections.emptySet();
	}

	@Override
	public String getUnlocalizedType() {
		return "for.multiblock.greenhouse.type";
	}
}

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

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import forestry.api.climate.IClimateContainer;
import forestry.api.climate.ImmutableClimateState;
import forestry.api.greenhouse.IGreenhouseLimits;
import forestry.api.greenhouse.IGreenhouseProvider;
import forestry.api.multiblock.IGreenhouseComponent.Listener;
import forestry.core.multiblock.FakeMultiblockController;
import forestry.energy.EnergyManager;

public class FakeGreenhouseController extends FakeMultiblockController implements IGreenhouseControllerInternal {
	public static final FakeGreenhouseController instance = new FakeGreenhouseController();

	private FakeGreenhouseController() {
	}

	@Override
	public EnergyManager getEnergyManager() {
		return null;
	}

	@Override
	public boolean canWork() {
		return false;
	}

	@Override
	public Set<Listener> getListenerComponents() {
		return Collections.emptySet();
	}

	@Override
	public ItemStack getCamouflageBlock() {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getDefaultCamouflageBlock() {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean setCamouflageBlock(ItemStack camouflageBlock, boolean sendClientUpdate) {
		return false;
	}

	@Override
	public String getUnlocalizedType() {
		return GreenhouseController.TYPE;
	}

	@Override
	public IClimateContainer getClimateContainer() {
		return null;
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public void onUpdateClimate() {
	}

	@Override
	public ImmutableClimateState getDefaultClimate() {
		return null;
	}

	@Override
	public BlockPos getCoordinates() {
		return BlockPos.ORIGIN;
	}

	@Override
	public IGreenhouseProvider getProvider() {
		return null;
	}

	@Nullable
	@Override
	public IGreenhouseLimits getLimits() {
		return null;
	}

	@Override
	public BlockPos getCenterCoordinates() {
		return null;
	}

	@Override
	public void setCenterCoordinates(BlockPos cordinates) {

	}
}

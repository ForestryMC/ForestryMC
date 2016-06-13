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
package forestry.greenhouse.tiles;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.greenhouse.DefaultGreenhouseListener;
import forestry.api.greenhouse.IGreenhouseHousing;
import forestry.api.greenhouse.IGreenhouseListener;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IGreenhouseController;

public class TileGreenhouseControl extends TileGreenhouse implements IGreenhouseComponent.Listener {

	private final IGreenhouseListener greenhouseListener;

	public TileGreenhouseControl() {
		this.greenhouseListener = new ControlGreenhouseListener(this);
	}
	
	@Override
	public IGreenhouseListener getGreenhouseListener() {
		return greenhouseListener;
	}

	private static class ControlGreenhouseListener extends DefaultGreenhouseListener {
		private final TileGreenhouseControl tile;

		public ControlGreenhouseListener(TileGreenhouseControl tile) {
			this.tile = tile;
		}
		
		@Override
		public boolean canWork(IGreenhouseController greenhouse, boolean canWork) {
			return canWork && !hasRedstoneSignal();
		}

		private boolean hasRedstoneSignal() {
			for (EnumFacing direction : EnumFacing.VALUES) {
				BlockPos side = tile.getPos().offset(direction);
				EnumFacing dir = direction.getOpposite();
				World world = tile.getWorld();
				if (world.getRedstonePower(side, dir) > 0 || world.getStrongPower(side, dir) > 0) {
					return true;
				}
			}
			return false;
		}
	}

}

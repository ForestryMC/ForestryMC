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

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.api.farming.DefaultFarmListener;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmListener;
import forestry.api.farming.IFarmLogic;
import forestry.api.multiblock.IFarmComponent;

public class TileFarmControl extends TileFarm implements IFarmComponent.Listener {

	private final IFarmListener farmListener;

	public TileFarmControl() {
		this.farmListener = new ControlFarmListener(this);
	}

	@Override
	public IFarmListener getFarmListener() {
		return farmListener;
	}

	private static class ControlFarmListener extends DefaultFarmListener {
		private final TileFarmControl tile;

		public ControlFarmListener(TileFarmControl tile) {
			this.tile = tile;
		}

		@Override
		public boolean cancelTask(IFarmLogic logic, FarmDirection direction) {
			return hasRedstoneSignal(direction.getFacing()) || hasRedstoneSignal(EnumFacing.UP) || hasRedstoneSignal(EnumFacing.DOWN);
		}

		private boolean hasRedstoneSignal(EnumFacing direction) {
			BlockPos side = new BlockPos(tile.getPos()).offset(direction);
			EnumFacing dir = direction.getOpposite();
			World world = tile.getWorld();

			return world.isBlockIndirectlyGettingPowered(side.offset(dir)) > 0 || world.getRedstonePower(side, dir) > 0;
		}
	}

}

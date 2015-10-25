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

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.api.farming.DefaultFarmListener;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmComponent;
import forestry.api.farming.IFarmListener;
import forestry.api.farming.IFarmLogic;
import forestry.core.vect.Vect;

public class TileControl extends TileFarm implements IFarmComponent.Listener {

	private final IFarmListener farmListener;

	public TileControl() {
		this.farmListener = new ControlFarmListener(this);
	}

	@Override
	public IFarmListener getFarmListener() {
		return farmListener;
	}

	private static class ControlFarmListener extends DefaultFarmListener {
		private final TileControl tile;

		public ControlFarmListener(TileControl tile) {
			this.tile = tile;
		}

		@Override
		public boolean cancelTask(IFarmLogic logic, FarmDirection direction) {
			return hasRedstoneSignal(direction.getDirection()) || hasRedstoneSignal(EnumFacing.UP)
					|| hasRedstoneSignal(EnumFacing.DOWN);
		}

		private boolean hasRedstoneSignal(EnumFacing direction) {
			Vect side = new Vect(tile).add(direction);
			int dir = direction.getOpposite().ordinal();
			World world = tile.getWorld();

			return world.isBlockIndirectlyGettingPowered(side.pos) > 0
					|| world.getRedstonePower(tile.pos, direction) > 0;
		}
	}

}

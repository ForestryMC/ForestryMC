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

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.farming.DefaultFarmListener;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmComponent;
import forestry.api.farming.IFarmListener;
import forestry.api.farming.IFarmLogic;
import forestry.core.vect.Vect;

public class TileControl extends TileFarm implements IFarmComponent.Listener {

	private final IFarmListener farmListener;

	public TileControl() {
		this.fixedType = TYPE_CONTROL;
		this.farmListener = new ControlFarmListener(this);
	}

	@Override
	public IFarmListener getFarmListener() {
		return farmListener;
	}

	private static class ControlFarmListener extends DefaultFarmListener {
		private final TileControl tile;
		private final Vect position;

		public ControlFarmListener(TileControl tile) {
			this.tile = tile;
			this.position = new Vect(tile.xCoord, tile.yCoord, tile.zCoord);
		}

		@Override
		public boolean cancelTask(IFarmLogic logic, FarmDirection direction) {
			return hasRedstoneSignal(direction.getForgeDirection()) || hasRedstoneSignal(ForgeDirection.UP) || hasRedstoneSignal(ForgeDirection.DOWN);
		}

		private boolean hasRedstoneSignal(ForgeDirection direction) {
			Vect side = position.add(direction);

			ForgeDirection opp = direction.getOpposite();
			int dir;
			if (opp.offsetZ < 0) {
				dir = 2;
			} else if (opp.offsetZ > 0) {
				dir = 3;
			} else if (opp.offsetX < 0) {
				dir = 4;
			} else if (opp.offsetX > 0) {
				dir = 5;
			} else {
				dir = 0;
			}

			return tile.getWorldObj().getIndirectPowerLevelTo(side.x, side.y, side.z, dir) > 0 || tile.getWorldObj().isBlockProvidingPowerTo(side.x, side.y, side.z, dir) > 0;
		}
	}

}

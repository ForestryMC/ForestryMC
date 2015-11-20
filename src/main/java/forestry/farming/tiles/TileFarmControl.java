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

import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.farming.DefaultFarmListener;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmListener;
import forestry.api.farming.IFarmLogic;
import forestry.api.multiblock.IFarmComponent;
import forestry.core.utils.vect.Vect;

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
			return hasRedstoneSignal(direction.getForgeDirection()) || hasRedstoneSignal(ForgeDirection.UP) || hasRedstoneSignal(ForgeDirection.DOWN);
		}

		private boolean hasRedstoneSignal(ForgeDirection direction) {
			Vect side = new Vect(tile).add(direction);
			int dir = direction.getOpposite().ordinal();
			World world = tile.getWorldObj();

			return world.getIndirectPowerLevelTo(side.x, side.y, side.z, dir) > 0 || world.isBlockProvidingPowerTo(side.x, side.y, side.z, dir) > 0;
		}
	}

}

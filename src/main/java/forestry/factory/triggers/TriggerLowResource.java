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
package forestry.factory.triggers;

import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.core.tiles.TilePowered;
import forestry.core.triggers.Trigger;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;

public class TriggerLowResource extends Trigger {

	private float threshold = 0.25F;

	public TriggerLowResource(String tag, float threshold) {
		super(tag, "lowResources");
		this.threshold = threshold;
	}

	@Override
	public String getDescription() {
		return super.getDescription() + " < " + threshold * 100 + "%";
	}

	/**
	 * Return true if the tile given in parameter activates the trigger, given the parameters.
	 */
	@Override
	public boolean isTriggerActive(TileEntity tile, ForgeDirection side, IStatementContainer source, IStatementParameter[] parameters) {
		if (!(tile instanceof TilePowered)) {
			return false;
		}

		return !((TilePowered) tile).hasResourcesMin(threshold);
	}

}

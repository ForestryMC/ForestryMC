/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.triggers;

import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.api.gates.ITriggerParameter;

import forestry.core.gadgets.TilePowered;

public class TriggerHasWork extends Trigger {

	public TriggerHasWork() {
		super("hasWork");
	}

	/**
	 * Return true if the tile given in parameter activates the trigger, given
	 * the parameters.
	 */
	@Override
	public boolean isTriggerActive(ForgeDirection direction, TileEntity tile, ITriggerParameter parameter) {

		if (!(tile instanceof TilePowered))
			return false;

		return ((TilePowered) tile).hasWork();
	}
}

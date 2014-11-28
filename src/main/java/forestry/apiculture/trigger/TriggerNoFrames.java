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
package forestry.apiculture.trigger;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import forestry.apiculture.gadgets.TileApiary;
import forestry.core.triggers.Trigger;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TriggerNoFrames extends Trigger {

	public TriggerNoFrames() {
		super("noFrames");
	}

	/**
	 * Return true if the tile given in parameter activates the trigger, given the parameters.
	 */
	@Override
	public boolean isTriggerActive(TileEntity tile, ForgeDirection side, IStatementContainer source, IStatementParameter[] parameters) {

		if (!(tile instanceof TileApiary))
			return false;

		for (int i = TileApiary.SLOT_FRAMES_1; i < TileApiary.SLOT_FRAMES_1 + TileApiary.SLOT_FRAMES_COUNT; i++) {
			if (((TileApiary) tile).getStackInSlot(i) != null) {
				return false;
			}
		}

		return true;

	}

}

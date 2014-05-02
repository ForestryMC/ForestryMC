/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.trigger;

import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.api.gates.ITriggerParameter;

import forestry.apiculture.gadgets.TileApiary;
import forestry.core.triggers.Trigger;

public class TriggerNoFrames extends Trigger {

	public TriggerNoFrames() {
		super("noFrames");
	}

	/**
	 * Return true if the tile given in parameter activates the trigger, given the parameters.
	 */
	@Override
	public boolean isTriggerActive(ForgeDirection direction, TileEntity tile, ITriggerParameter parameter) {

		if (!(tile instanceof TileApiary))
			return false;

		for (int i = TileApiary.SLOT_FRAMES_1; i < TileApiary.SLOT_FRAMES_1 + TileApiary.SLOT_FRAMES_COUNT; i++)
			if (((TileApiary) tile).getStackInSlot(i) == null)
				continue;
			else
				return false;

		return true;

	}

}

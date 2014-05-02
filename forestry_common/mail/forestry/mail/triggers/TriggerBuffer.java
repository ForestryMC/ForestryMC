/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail.triggers;

import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.api.gates.ITriggerParameter;

import forestry.core.triggers.Trigger;
import forestry.mail.gadgets.MachineTrader;

public class TriggerBuffer extends Trigger {

	float threshold;

	public TriggerBuffer(String tag, float threshold) {
		super(tag, "mailBuffer");
		this.threshold = threshold;
	}

	@Override
	public String getDescription() {
		return super.getDescription() + " > " + threshold * 100 + "%";
	}

	@Override
	public boolean isTriggerActive(ForgeDirection direction, TileEntity tile, ITriggerParameter parameter) {

		if (!(tile instanceof MachineTrader))
			return false;

		return ((MachineTrader) tile).hasOutputBufMin(threshold);
	}

}

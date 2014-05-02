/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.triggers;

import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.api.gates.ITriggerParameter;

import forestry.core.gadgets.Engine;
import forestry.core.gadgets.TilePowered;

public class TriggerLowFuel extends Trigger {

	private float threshold = 0.25F;

	public TriggerLowFuel(String tag, float threshold) {
		super(tag, "lowFuel");
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
	public boolean isTriggerActive(ForgeDirection direction, TileEntity tile, ITriggerParameter parameter) {

		if (tile instanceof TilePowered)
			return !((TilePowered) tile).hasFuelMin(threshold);

		if (tile instanceof Engine) {
			Engine engine = (Engine) tile;
			return !engine.hasFuelMin(threshold);
		}

		return false;
	}

}

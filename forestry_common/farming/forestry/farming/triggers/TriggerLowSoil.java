/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.farming.triggers;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.api.gates.ITriggerParameter;

import forestry.api.core.ITileStructure;
import forestry.core.triggers.Trigger;
import forestry.farming.gadgets.TileFarmPlain;
import forestry.farming.gadgets.TileHatch;

public class TriggerLowSoil extends Trigger {

	private int threshold = 64;

	public TriggerLowSoil(int threshold) {
		super("lowSoil." + threshold, "lowSoil");
		this.threshold = threshold;
	}

	@Override
	public String getDescription() {
		return super.getDescription() + " < " + threshold;
	}

	@Override
	public boolean hasParameter() {
		return true;
	}

	/**
	 * Return true if the tile given in parameter activates the trigger, given
	 * the parameters.
	 */
	@Override
	public boolean isTriggerActive(ForgeDirection direction, TileEntity tile, ITriggerParameter parameter) {
		if (!(tile instanceof TileHatch))
			return false;

		ITileStructure central = ((TileHatch) tile).getCentralTE();
		if (central == null || !(central instanceof TileFarmPlain))
			return false;

		ItemStack filter;
		if (parameter == null || parameter.getItemStack() == null)
			filter = new ItemStack(Blocks.air, threshold, -1);
		else {
			filter = parameter.getItemStack().copy();
			filter.stackSize = threshold;
		}

		return !((TileFarmPlain) central).hasResources(new ItemStack[] { filter });
	}
}

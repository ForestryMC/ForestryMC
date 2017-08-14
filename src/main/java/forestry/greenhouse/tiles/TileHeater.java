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
package forestry.greenhouse.tiles;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.ClimateType;
import forestry.greenhouse.PluginGreenhouse;
import forestry.greenhouse.blocks.BlockClimatiserType;

public class TileHeater extends TileClimatiser {

	public TileHeater() {
		super(0.075F, 5F, ClimateType.TEMPERATURE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getItemStack() {
		return new ItemStack(PluginGreenhouse.getBlocks().climatiserBlock, 1, BlockClimatiserType.HEATER.ordinal());
	}

}

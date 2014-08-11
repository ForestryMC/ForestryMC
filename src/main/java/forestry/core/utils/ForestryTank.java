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
package forestry.core.utils;

import java.util.Locale;

import net.minecraft.item.EnumRarity;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;

import forestry.core.config.Config;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.tooltips.ToolTipLine;

public class ForestryTank extends FluidTank {

	public static final ForestryTank FAKETANK = new ForestryTank(0);
	public static final ForestryTank[] FAKETANK_ARRAY = new ForestryTank[] { FAKETANK };
	public static final FluidTankInfo DUMMY_TANK_INFO = new FluidTankInfo(FAKETANK);
	public static final FluidTankInfo[] DUMMY_TANKINFO_ARRAY = new FluidTankInfo[] { DUMMY_TANK_INFO };

	public ForestryTank(FluidStack stack, int capacity) {
		super(stack, capacity);
	}

	public ForestryTank(int capacity) {
		super(capacity);
	}

	public ForestryTank(Fluid fluid, int amount, int capacity) {
		super(fluid, amount, capacity);
	}

	public boolean isEmpty() {
		return getFluid() == null || getFluid().amount <= 0;
	}

	public boolean isFull() {
		return getFluid() != null && getFluid().amount == getCapacity();
	}

	public Fluid getFluidType() {
		return getFluid() != null ? getFluid().getFluid() : null;
	}

	@Override
	public String toString() {
		return String.format("Tank: %s, %d/%d", fluid != null && fluid.getFluid() != null ? fluid.getFluid().getName() : "Empty", getFluidAmount(), getCapacity());
	}

	public ToolTip getToolTip() {
		return toolTip;
	}
	protected final ToolTip toolTip = new ToolTip() {
		@Override
		public void refresh() {
			toolTip.clear();
			int amount = 0;
			if (getFluid() != null && getFluid().amount > 0 && getFluid().getFluid() != null) {
				Fluid fluidType = getFluidType();
				EnumRarity rarity = fluidType.getRarity();
				if (rarity == null)
					rarity = EnumRarity.common;
				ToolTipLine fluidName = new ToolTipLine(fluidType.getLocalizedName(getFluid()), rarity.rarityColor);
				fluidName.setSpacing(2);
				toolTip.add(fluidName);
				amount = getFluid().amount;
			} else {
				toolTip.add(new ToolTipLine(StringUtil.localize("gui.empty")));
			}
			if (Config.tooltipLiquidAmount)
				toolTip.add(new ToolTipLine(String.format(Locale.ENGLISH, "%,d / %,d", amount, getCapacity())));
		}
	};
}

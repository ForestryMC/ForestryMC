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
package forestry.core.fluids.tanks;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import net.minecraft.item.EnumRarity;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.core.gui.tooltips.ToolTipLine;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FilteredTank extends StandardTank {

	private final HashSet<Fluid> filters;

	public FilteredTank(int capacity, Fluid... filters) {
		this(capacity, Arrays.asList(filters), null);
	}

	public FilteredTank(int capacity, Collection<Fluid> filters) {
		this(capacity, filters, null);
	}

	public FilteredTank(int capacity, Collection<Fluid> filters, TileEntity tile) {
		super(capacity, tile);
		this.filters = new HashSet<Fluid>(filters);
	}

	public void addFilter(Fluid filter) {
		if (!accepts(filter))
			filters.add(filter);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (liquidMatchesFilter(resource))
			return super.fill(resource, doFill);
		return 0;
	}

	@Override
	public boolean accepts(Fluid fluid) {
		return filters.contains(fluid);
	}

	public boolean liquidMatchesFilter(FluidStack resource) {
		if (resource == null || filters == null)
			return false;
		return filters.contains(resource.getFluid());
	}

	@Override
	protected void refreshTooltip() {
		if (hasFluid()) {
			super.refreshTooltip();
			return;
		}

		toolTip.clear();
		if(Proxies.common.isShiftDown() || filters.size() < 5) {
			for (Fluid filter : filters) {
				EnumRarity rarity = filter.getRarity();
				if (rarity == null)
					rarity = EnumRarity.common;
				FluidStack filterFluidStack = FluidRegistry.getFluidStack(filter.getName(), 0);
				ToolTipLine name = new ToolTipLine(filter.getLocalizedName(filterFluidStack), rarity.rarityColor, 2);
				toolTip.add(name);
			}
		} else {
			toolTip.add("\u00A7o<" + StringUtil.localize("gui.tooltip.tmi") + ">");
		}

		toolTip.add(String.format("%,d", getFluidAmount()) + " / " + String.format("%,d", getCapacity()));
	}

}

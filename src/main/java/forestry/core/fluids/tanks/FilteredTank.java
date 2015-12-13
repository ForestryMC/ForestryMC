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
import java.util.Set;

import net.minecraft.item.EnumRarity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.core.gui.tooltips.ToolTipLine;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FilteredTank extends StandardTank {

	private final Set<String> filters = new HashSet<>(); // FluidNames

	public FilteredTank(int capacity, Fluid... filters) {
		this(capacity, Arrays.asList(filters), null);
	}

	public FilteredTank(int capacity, Collection<Fluid> filters) {
		this(capacity, filters, null);
	}

	public FilteredTank(int capacity, Collection<Fluid> filters, TileEntity tile) {
		super(capacity, tile);
		setFilters(filters);
	}

	public void setFilters(Collection<Fluid> filters) {
		this.filters.clear();
		for (Fluid fluid : filters) {
			this.filters.add(fluid.getName());
		}
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (liquidMatchesFilter(resource)) {
			return super.fill(resource, doFill);
		}
		return 0;
	}

	@Override
	public boolean accepts(Fluid fluid) {
		return filters.contains(fluid.getName());
	}

	private boolean liquidMatchesFilter(FluidStack resource) {
		if (resource == null || filters == null) {
			return false;
		}
		return filters.contains(resource.getFluid().getName());
	}

	@Override
	protected void refreshTooltip() {
		if (hasFluid()) {
			super.refreshTooltip();
			return;
		}

		toolTip.clear();
		if (Proxies.common.isShiftDown() || filters.size() < 5) {
			for (String filterName : filters) {
				Fluid fluidFilter = FluidRegistry.getFluid(filterName);
				EnumRarity rarity = fluidFilter.getRarity();
				if (rarity == null) {
					rarity = EnumRarity.common;
				}
				FluidStack filterFluidStack = FluidRegistry.getFluidStack(fluidFilter.getName(), 0);
				ToolTipLine name = new ToolTipLine(fluidFilter.getLocalizedName(filterFluidStack), rarity.rarityColor, 2);
				toolTip.add(name);
			}
		} else {
			toolTip.add(EnumChatFormatting.ITALIC + "<" + StringUtil.localize("gui.tooltip.tmi") + ">");
		}

		toolTip.add(String.format("%,d", getFluidAmount()) + " / " + String.format("%,d", getCapacity()));
	}

}

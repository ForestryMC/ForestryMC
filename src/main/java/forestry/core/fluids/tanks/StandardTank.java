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

import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.tooltips.ToolTipLine;
import java.util.Locale;
import net.minecraft.item.EnumRarity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StandardTank extends FluidTank {

	// defines how the tank responds to IFluidHandler requests
	public enum TankMode {
		DEFAULT, OUTPUT, INPUT, INTERNAL
	}

	public TankMode tankMode = TankMode.DEFAULT;
	public static final int DEFAULT_COLOR = 0xFFFFFF;
	public int colorCache = DEFAULT_COLOR;
	private int tankIndex;

	public StandardTank(int capacity) {
		super(capacity);
	}

	public StandardTank(FluidStack fluid, int capacity) {
		this(capacity);
		setFluid(fluid);
	}

	public StandardTank(int capacity, TileEntity tile) {
		this(capacity);
		this.tile = tile;
	}

	public StandardTank(FluidStack fluid, int capacity, TileEntity tile) {
		this(capacity);
		this.tile = tile;
		setFluid(fluid);
	}

	public void setTankIndex(int index) {
		this.tankIndex = index;
	}

	public int getTankIndex() {
		return tankIndex;
	}

	@Override
	public void setFluid(FluidStack fluid) {
		super.setFluid(fluid);
		colorCache = StandardTank.DEFAULT_COLOR;
	}

	public int getColor() {
		Fluid f = getFluidType();
		if (f == null)
			return DEFAULT_COLOR;
		return f.getColor(getFluid());
	}

	public boolean isEmpty() {
		return getFluid() == null || getFluid().amount <= 0;
	}

	public boolean isFull() {
		return getFluid() != null && getFluid().amount == getCapacity();
	}

	public int getRemainingSpace() {
		return capacity - getFluidAmount();
	}

	public Fluid getFluidType() {
		return getFluid() != null ? getFluid().getFluid() : null;
	}

	@Override
	public int fill(final FluidStack resource, final boolean doFill) {
		if (resource == null)
			return 0;
		if (resource.amount <= 0)
			return 0;
		if (!accepts(resource.getFluid()))
			return 0;
		return super.fill(resource, doFill);
	}

	public boolean accepts(Fluid fluid) {
		return true;
	}

	public boolean canBeFilledExternally() {
		switch (tankMode) {
			case DEFAULT:
			case INPUT:
				return true;
		}
		return false;
	}

	public boolean canBeDrainedExternally() {
		switch (tankMode) {
			case DEFAULT:
			case OUTPUT:
				return true;
		}
		return false;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (maxDrain <= 0)
			return null;
		return super.drain(maxDrain, doDrain);
	}

	@Override
	public String toString() {
		return String.format("Tank: %s, %d/%d", fluid != null && fluid.getFluid() != null ? fluid.getFluid().getName() : "Empty", getFluidAmount(), getCapacity());
	}

	public ToolTip getToolTip() {
		return toolTip;
	}

	protected boolean hasFluid() {
		FluidStack fluid = getFluid();
		return fluid != null && fluid.amount > 0 && fluid.getFluid() != null;
	}

	protected void refreshTooltip() {
		toolTip.clear();
		int amount = 0;
		if (hasFluid()) {
			Fluid fluidType = getFluidType();
			EnumRarity rarity = fluidType.getRarity();
			if (rarity == null)
				rarity = EnumRarity.common;
			ToolTipLine fluidName = new ToolTipLine(fluidType.getLocalizedName(getFluid()), rarity.rarityColor, 2);
			toolTip.add(fluidName);
			amount = getFluid().amount;
		}
		toolTip.add(new ToolTipLine(String.format(Locale.ENGLISH, "%,d / %,d", amount, getCapacity())));
	}

	protected final ToolTip toolTip = new ToolTip() {
		@Override
		public void refresh() {
			refreshTooltip();
		}

	};
}

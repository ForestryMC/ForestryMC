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

import javax.annotation.Nonnull;
import java.io.IOException;

import net.minecraft.item.EnumRarity;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import forestry.core.fluids.FakeTankUpdateHandler;
import forestry.core.fluids.ITankUpdateHandler;
import forestry.core.fluids.TankManager;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.utils.Translator;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StandardTank extends FluidTank implements IStreamable {
	private static final int DEFAULT_COLOR = 0xFFFFFF;
	@Nonnull
	private ITankUpdateHandler tankUpdateHandler = FakeTankUpdateHandler.instance;
	private int tankIndex;

	public StandardTank(int capacity, boolean canFill, boolean canDrain) {
		super(capacity);
		setCanFill(canFill);
		setCanDrain(canDrain);
	}

	public StandardTank(int capacity) {
		super(capacity);
	}

	public void setTankIndex(int index) {
		this.tankIndex = index;
	}

	public void setTankUpdateHandler(@Nonnull TankManager tankUpdateHandler) {
		this.tankUpdateHandler = tankUpdateHandler;
	}

	public int getTankIndex() {
		return tankIndex;
	}

	public int getColor() {
		Fluid f = getFluidType();
		if (f == null) {
			return DEFAULT_COLOR;
		}
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
	public int fillInternal(FluidStack resource, boolean doFill) {
		int filled = super.fillInternal(resource, doFill);
		if (doFill && filled > 0) {
			tankUpdateHandler.updateTankLevels(this);
		}
		return filled;
	}

	@Override
	public FluidStack drainInternal(int maxDrain, boolean doDrain) {
		FluidStack drained = super.drainInternal(maxDrain, doDrain);
		if (doDrain && drained != null && drained.amount > 0) {
			tankUpdateHandler.updateTankLevels(this);
		}
		return drained;
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
			if (rarity == null) {
				rarity = EnumRarity.COMMON;
			}
			toolTip.add(fluidType.getLocalizedName(getFluid()), rarity.rarityColor);
			amount = getFluid().amount;
		}
		String liquidAmount = Translator.translateToLocalFormatted("for.gui.tooltip.liquid.amount", amount, getCapacity());
		toolTip.add(liquidAmount);
	}

	protected final ToolTip toolTip = new ToolTip() {
		@Override
		public void refresh() {
			refreshTooltip();
		}

	};


	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeFluidStack(fluid);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		fluid = data.readFluidStack();
	}
}

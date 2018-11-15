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
package forestry.core.fluids;

import javax.annotation.Nullable;

import net.minecraft.item.EnumRarity;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.gui.tooltips.ToolTip;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.core.utils.Translator;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StandardTank extends FluidTank implements IStreamable {
	private static final int DEFAULT_COLOR = 0xFFFFFF;

	private ITankUpdateHandler tankUpdateHandler = FakeTankUpdateHandler.instance;
	private int tankIndex;

	@SideOnly(Side.CLIENT)
	@Nullable
	protected ToolTip toolTip;

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

	public void setTankUpdateHandler(TankManager tankUpdateHandler) {
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

	@Nullable
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
	@Nullable
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

	protected boolean hasFluid() {
		FluidStack fluid = getFluid();
		return fluid != null && fluid.amount > 0 && fluid.getFluid() != null;
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeFluidStack(fluid);
	}

	@Override
	public void readData(PacketBufferForestry data) {
		fluid = data.readFluidStack();
	}

	@SideOnly(Side.CLIENT)
	public ToolTip getToolTip() {
		if (toolTip == null) {
			toolTip = new TankToolTip(this);
		}
		return toolTip;
	}

	@SideOnly(Side.CLIENT)
	protected void refreshTooltip() {
		ToolTip toolTip = getToolTip();
		toolTip.clear();
		int amount = 0;
		FluidStack fluidStack = getFluid();
		if (fluidStack != null) {
			Fluid fluidType = fluidStack.getFluid();
			EnumRarity rarity = fluidType.getRarity();
			if (rarity == null) {
				rarity = EnumRarity.COMMON;
			}
			toolTip.add(fluidType.getLocalizedName(getFluid()), rarity.color);
			amount = getFluid().amount;
		}
		String liquidAmount = Translator.translateToLocalFormatted("for.gui.tooltip.liquid.amount", amount, getCapacity());
		toolTip.add(liquidAmount);
	}

	@SideOnly(Side.CLIENT)
	private static class TankToolTip extends ToolTip {
		private final StandardTank standardTank;

		public TankToolTip(StandardTank standardTank) {
			this.standardTank = standardTank;
		}

		@Override
		public void refresh() {
			standardTank.refreshTooltip();
		}
	}
}

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.TranslationTextComponent;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import forestry.core.gui.tooltips.ToolTip;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StandardTank extends FluidTank implements IStreamable {
	private static final int DEFAULT_COLOR = 0xFFFFFF;

	private ITankUpdateHandler tankUpdateHandler = FakeTankUpdateHandler.instance;
	private int tankIndex;
	private final boolean canFill;
	private final boolean canDrain;
	private boolean internalTest;

	@OnlyIn(Dist.CLIENT)
	@Nullable
	protected ToolTip toolTip;

	public StandardTank(int capacity, boolean canFill, boolean canDrain) {
		super(capacity);
		this.canDrain = canDrain;
		this.canFill = canFill;
	}

	public StandardTank(int capacity) {
		super(capacity);
		this.canFill = true;
		this.canDrain = true;
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
		return f.getAttributes().getColor(getFluid());
	}

	public boolean isEmpty() {
		return getFluid().isEmpty() || getFluid().getAmount() <= 0;
	}

	public boolean isFull() {
		return !getFluid().isEmpty() && getFluid().getAmount() == getCapacity();
	}

	public int getRemainingSpace() {
		return capacity - getFluidAmount();
	}

	@Nullable
	public Fluid getFluidType() {
		return !getFluid().isEmpty() ? getFluid().getFluid() : null;
	}

	@Override
	public boolean isFluidValid(FluidStack stack) {
		return !internalTest && validator.test(stack);
	}

	public boolean canFill() {
		return canFill;
	}

	public boolean canDrain() {
		return canDrain;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (!canFill() || !isFluidValid(resource)) {
			return 0;
		}
		return fillInternal(resource, action);
	}

	public int fillInternal(FluidStack resource, FluidAction action) {
		internalTest = true;
		int filled = super.fill(resource, action);
		if (action == FluidAction.EXECUTE && filled > 0) {
			tankUpdateHandler.updateTankLevels(this);
		}
		internalTest = false;
		return filled;
	}

	@Nonnull
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		if (!canDrain) {
			return FluidStack.EMPTY;
		}
		return drainInternal(maxDrain, action);
	}

	@Nonnull
	public FluidStack drainInternal(int maxDrain, FluidAction action) {
		FluidStack drained = super.drain(maxDrain, action);
		if (action == FluidAction.EXECUTE && !drained.isEmpty() && drained.getAmount() > 0) {
			tankUpdateHandler.updateTankLevels(this);
		}
		return drained;
	}

	@Nonnull
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (!canDrain) {
			return FluidStack.EMPTY;
		}
		return drainInternal(resource, action);
	}

	@Nonnull
	public FluidStack drainInternal(FluidStack resource, FluidAction action) {
		FluidStack drained = super.drain(resource, action);
		if (action == FluidAction.EXECUTE && !drained.isEmpty() && drained.getAmount() > 0) {
			tankUpdateHandler.updateTankLevels(this);
		}
		return drained;
	}

	@Override
	public String toString() {
		return String.format("Tank: %s, %d/%d", !fluid.isEmpty() ? fluid.getFluid().getRegistryName() : "Empty", getFluidAmount(), getCapacity());
	}

	protected boolean hasFluid() {
		FluidStack fluid = getFluid();
		return !fluid.isEmpty() && fluid.getAmount() > 0 && fluid.getFluid() != null;
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeFluidStack(fluid);
	}

	@Override
	public void readData(PacketBufferForestry data) {
		fluid = data.readFluidStack();
	}

	@OnlyIn(Dist.CLIENT)
	public ToolTip getToolTip() {
		if (toolTip == null) {
			toolTip = new TankToolTip(this);
		}
		return toolTip;
	}

	@OnlyIn(Dist.CLIENT)
	protected void refreshTooltip() {
		ToolTip toolTip = getToolTip();
		toolTip.clear();
		int amount = 0;
		FluidStack fluidStack = getFluid();
		if (!fluidStack.isEmpty()) {
			Fluid fluidType = fluidStack.getFluid();
			FluidAttributes attributes = fluidType.getAttributes();
			Rarity rarity = attributes.getRarity();
			if (rarity == null) {
				rarity = Rarity.COMMON;
			}
			toolTip.add(new TranslationTextComponent(attributes.getTranslationKey(fluidStack)), rarity.color);
			amount = getFluid().getAmount();
		}
		TranslationTextComponent liquidAmount = new TranslationTextComponent("for.gui.tooltip.liquid.amount", amount, getCapacity());
		toolTip.add(liquidAmount);
	}

	@OnlyIn(Dist.CLIENT)
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

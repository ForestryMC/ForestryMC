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

import com.google.common.collect.ImmutableMap;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.common.animation.TimeValues;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import forestry.api.climate.EnumClimatiserModes;
import forestry.api.climate.EnumClimatiserTypes;
import forestry.api.climate.IClimatiserDefinition;
import forestry.api.multiblock.IMultiblockLogic;
import forestry.core.climate.ClimatiserDefinition;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;

public class TileGreenhouseSprinkler extends TileGreenhouseClimatiser {
	
	private final IAnimationStateMachine asm;
	private final TimeValues.VariableValue cycleLength = new TimeValues.VariableValue(20);
	private final TimeValues.VariableValue clickTime = new TimeValues.VariableValue(Float.NEGATIVE_INFINITY);
	
	protected static final int WATER_PER_OPERATION = 25;
	private static final IClimatiserDefinition DEFINITION = new ClimatiserDefinition(0.005F, EnumClimatiserModes.POSITIVE, 9, EnumClimatiserTypes.HUMIDITY);

	public TileGreenhouseSprinkler() {
		super(DEFINITION, 20 + WATER_PER_OPERATION / 10);
		asm = Proxies.render.loadAnimationState(new ResourceLocation(Constants.MOD_ID, "asms/block/sprinkler.json"), ImmutableMap.of(
				"cycle_length", cycleLength,
				"click_time", clickTime
		));
	}
	
	@Override
	public boolean hasFastRenderer() {
		return true;
	}
	
	@Override
	public boolean canWork() {
		IMultiblockLogic logic = getMultiblockLogic();
		if(logic == null || !logic.isConnected() || getMultiblockLogic().getController().getTankManager() == null || getMultiblockLogic().getController().getTankManager().getTank(0) == null){
			return false;
		}
		return consumeWaterToDoWork(WORK_CYCLES, WATER_PER_OPERATION, getMultiblockLogic().getController().getTankManager().getTank(0));
	}
	
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		if (worldObj != null && worldObj.isRemote) {
			if (asm.currentState().equals("moving") && !isActive()) {
				clickTime.setValue(Animation.getWorldTime(getWorld(), Animation.getPartialTickTime()));
				asm.transition("stopping");
			} else if (asm.currentState().equals("default") && isActive()) {
				float time = Animation.getWorldTime(getWorld(), Animation.getPartialTickTime());
				clickTime.setValue(time);
				asm.transition("starting");
			}
		}
	}
	
	public boolean consumeWaterToDoWork(int ticksPerWorkCycle, int fluidPerWorkCycle, IFluidTank tank) {
		int fluidPerCycle = (int) Math.ceil(fluidPerWorkCycle / (float) ticksPerWorkCycle);
		if (tank.getFluid() == null || tank.getFluid().amount < fluidPerCycle) {
			return false;
		}
		FluidStack drained = tank.drain(fluidPerCycle, true);
		
		return drained != null && drained.amount > 0;
	}
	
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing side){
        if(capability == CapabilityAnimation.ANIMATION_CAPABILITY)
        {
            return true;
        }
        return super.hasCapability(capability, side);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side){
        if(capability == CapabilityAnimation.ANIMATION_CAPABILITY)
        {
            return CapabilityAnimation.ANIMATION_CAPABILITY.cast(asm);
        }
        return super.getCapability(capability, side);
    }

}

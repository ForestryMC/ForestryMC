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
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.common.animation.TimeValues;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import forestry.api.core.climate.IClimatePosition;
import forestry.api.core.climate.IClimateRegion;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.config.Constants;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.proxy.Proxies;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;

public class TileGreenhouseSprinkler extends TileGreenhouseClimatiser {
	
	private final IAnimationStateMachine asm;
	private final TimeValues.VariableValue cycleLength = new TimeValues.VariableValue(4);
	private final TimeValues.VariableValue clickTime = new TimeValues.VariableValue(Float.NEGATIVE_INFINITY);
	
	protected static final int WATER_PER_OPERATION = 2;
	private static final SprinklerDefinition definition = new SprinklerDefinition();

	public TileGreenhouseSprinkler() {
		super(definition);
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
	public void changeClimate(int tick, IClimateRegion region) {
		IMultiblockController controller = getMultiblockLogic().getController();
		if(getMultiblockLogic().isConnected() && controller != null && controller.isAssembled() && minPos != null && maxPos != null && region != null){
			IGreenhouseControllerInternal greenhouseInternal = (IGreenhouseControllerInternal) controller;
			boolean canWork = true;
			for (IGreenhouseComponent.Listener listenerComponent : greenhouseInternal.getListenerComponents()) {
				if(canWork){
					canWork = listenerComponent.getGreenhouseListener().canWork(greenhouseInternal, canWork);
				}
			}
			if (canWork && workingTime == 0 && consumeWaterToDoWork(WORK_CYCLES, WATER_PER_OPERATION, (StandardTank) greenhouseInternal.getTankManager().getTank(0))) {
				int dimensionID = worldObj.provider.getDimension();
				
				for(BlockPos pos : BlockPos.getAllInBox(maxPos, minPos)){
					IClimatePosition position = region.getPositions().get(pos);
					if(position != null){
						if(position.getHumidity() >= 2.0F){
							if(position.getHumidity() > 2.0F){
								position.setHumidity(2.0F);
							}
							continue;
						}else if(position.getHumidity() <= 0.0F){
							if(position.getHumidity() < 0.0F){
								position.setHumidity(0.0F);
							}
							continue;
						}
						
						double distance = pos.distanceSq(pos);
						int maxDistance = definition.getClimitiseRange();
						if(distance <= maxDistance){
							position.setHumidity(position.getHumidity() + (float) (definition.getChange() / distance));
						}
					}
				}
				
				// one tick of work for every 10 RF
				workingTime +=  WATER_PER_OPERATION * 2;
			}
	
			if (workingTime > 0) {
				workingTime--;
			}
	
			setActive(workingTime > 0);
		}else if(isActive()){
			setActive(false);
		}
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
	
	public boolean consumeWaterToDoWork(int ticksPerWorkCycle, int fluidPerWorkCycle, StandardTank tank) {
		int fluidPerCycle = (int) Math.ceil(fluidPerWorkCycle / (float) ticksPerWorkCycle);
		if (tank.getFluid() == null || tank.getFluid().amount < fluidPerCycle) {
			return false;
		}

		modifyWaterStored(-fluidPerCycle, tank);
		return true;
	}
	
	public void modifyWaterStored(int fluid, StandardTank tank) {

		tank.getFluid().amount += fluid;

		if (tank.getFluid().amount > tank.getCapacity()) {
			tank.getFluid().amount = tank.getCapacity();
		} else if (tank.getFluid().amount < 0) {
			tank.getFluid().amount = 0;
		}
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

	private static class SprinklerDefinition implements IClimitiserDefinition {
		
		@Override
		public ClimitiserType getType() {
			return ClimitiserType.HUMIDITY;
		}

		@Override
		public float getChange() {
			return 0.5F;
		}

		@Override
		public int getClimitiseRange() {
			return 9;
		}
	}

}

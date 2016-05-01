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

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.client.model.animation.IAnimationProvider;
import net.minecraftforge.client.model.animation.TimeValues.VariableValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;

import forestry.api.core.IClimateControlled;
import forestry.api.greenhouse.IGreenhouseHousing;
import forestry.api.multiblock.IGreenhouseController;
import forestry.core.config.Constants;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.proxy.Proxies;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;

public class TileGreenhouseSprinkler extends TileGreenhouseClimatiser implements IAnimationProvider {
	
	private final IAnimationStateMachine asm;
	private final VariableValue cycleLength = new VariableValue(4);
	private final VariableValue clickTime = new VariableValue(Float.NEGATIVE_INFINITY);
	
	protected static final int WATER_PER_OPERATION = 2;
	private static final SprinklerDefinition definition = new SprinklerDefinition();

	public TileGreenhouseSprinkler() {
		super(definition);
		asm = Proxies.render.loadAnimationState(new ResourceLocation(Constants.RESOURCE_ID, "asms/block/sprinkler.json"), ImmutableMap.of(
				"cycle_length", cycleLength,
				"click_time", clickTime
		));
	}
	
	@Override
	public boolean hasFastRenderer() {
		return true;
	}
	
	@Override
	public <G extends IGreenhouseController & IGreenhouseHousing & IClimateControlled> void changeClimate(int tick, G greenhouse) {
		IGreenhouseControllerInternal greenhouseInternal = (IGreenhouseControllerInternal) greenhouse;
		if (workingTime < 20 && consumeWaterToDoWork(WORK_CYCLES, WATER_PER_OPERATION, (StandardTank) greenhouseInternal.getTankManager().getTank(0))) {
			// one tick of work for every 10 RF
			workingTime += WATER_PER_OPERATION * 2;
		}

		if (workingTime > 0) {
			workingTime--;
			greenhouse.addHumidityChange(definition.getChangePerTransfer(), definition.getBoundaryDown(), definition.getBoundaryUp());
		}

		setActive(workingTime > 0);
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
	public IAnimationStateMachine asm() {
		return asm;
	}

	private static class SprinklerDefinition implements IClimitiserDefinition {

		@Override
		public float getChangePerTransfer() {
			return 0.02f;
		}

		@Override
		public float getBoundaryUp() {
			return 2.5f;
		}

		@Override
		public float getBoundaryDown() {
			return 0.05f;
		}
		
		@Override
		public ClimitiserType getType() {
			return ClimitiserType.HUMIDITY;
		}
	}

}

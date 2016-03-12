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

import forestry.api.core.IClimateControlled;
import forestry.api.greenhouse.IGreenhouseHousing;
import forestry.api.multiblock.IGreenhouseController;
import forestry.core.fluids.tanks.StandardTank;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;

public class TileGreenhouseSprinkler extends TileGreenhouseClimatiser {
	
	protected static final int WATER_PER_OPERATION = 75;
	private static final SprinklerDefinition definition = new SprinklerDefinition();

	public TileGreenhouseSprinkler() {
		super(definition);
	}
	
	@Override
	public <G extends IGreenhouseController & IGreenhouseHousing & IClimateControlled> void changeClimate(int tick, G greenhouse) {
		IGreenhouseControllerInternal greenhouseInternal = (IGreenhouseControllerInternal) greenhouse;
		if (workingTime < 20 && consumeWaterToDoWork(WORK_CYCLES, WATER_PER_OPERATION, (StandardTank) greenhouseInternal.getTankManager().getTank(0))) {
			// one tick of work for every 10 RF
			workingTime += ENERGY_PER_OPERATION / 10;
		}

		if (workingTime > 0) {
			workingTime--;
			greenhouse.addHumidityChange(definition.getChangePerTransfer(), definition.getBoundaryDown(), definition.getBoundaryUp());
		}

		setActive(workingTime > 0);
	}
	
	public boolean consumeWaterToDoWork(int ticksPerWorkCycle, int fluidPerWorkCycle, StandardTank tank) {
		int fluidPerCycle = (int) Math.ceil(fluidPerWorkCycle / (float) ticksPerWorkCycle);
		if (tank.getFluid().amount < fluidPerCycle) {
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

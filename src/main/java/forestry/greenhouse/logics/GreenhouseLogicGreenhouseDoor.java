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
package forestry.greenhouse.logics;

import forestry.api.greenhouse.DefaultGreenhouseLogic;
import forestry.api.greenhouse.IGreenhouseClimaLogic;
import forestry.api.multiblock.IGreenhouseController;
import net.minecraft.nbt.NBTTagCompound;

public class GreenhouseLogicGreenhouseDoor extends DefaultGreenhouseLogic implements IGreenhouseClimaLogic{

	private int workTimer;
	
	public GreenhouseLogicGreenhouseDoor(IGreenhouseController controller) {
		super(controller, "GreenhouseDoor");
	}
	
	@Override
	public void work() {
		if(controller == null || !controller.isAssembled()){
			return;
		}
		if(controller.getWorld().isDaytime()){
			if(workTimer++>20){
				controller.addTemperatureChange(-0.0001F, 0.05F, 2.5F);
				controller.addHumidityChange(-0.0001F, 0.05F, 2.5F);
				workTimer = 0;
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("workTimer", workTimer);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		workTimer = nbt.getInteger("workTimer");
	}

}

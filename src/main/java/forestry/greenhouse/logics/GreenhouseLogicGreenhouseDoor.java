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

import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.greenhouse.DefaultGreenhouseLogic;
import forestry.api.greenhouse.IGreenhouseClimaLogic;
import forestry.api.multiblock.IGreenhouseController;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.greenhouse.tiles.TileGreenhouseDoor;

public class GreenhouseLogicGreenhouseDoor extends DefaultGreenhouseLogic implements IGreenhouseClimaLogic {

	private int workTimer;
	
	public GreenhouseLogicGreenhouseDoor(IGreenhouseController controller) {
		super(controller, "GreenhouseDoor");
	}
	
	@Override
	public void work() {
		if (controller == null || !controller.isAssembled()) {
			return;
		}
		if (workTimer++ > 20) {
			int openDoors = 0;
			for (IMultiblockComponent component : controller.getComponents()) {
				if (component instanceof TileGreenhouseDoor) {
					IBlockState state = ((TileGreenhouseDoor) component).getWorld().getBlockState(component.getCoordinates());
					if (state.getValue(BlockDoor.OPEN)) {
						openDoors++;
					}
				}
			}
			controller.addTemperatureChange(-0.0001F * openDoors, 0.05F, 2.5F);
			controller.addHumidityChange(-0.0001F * openDoors, 0.05F, 2.5F);
			workTimer = 0;
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("workTimer", workTimer);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		workTimer = nbt.getInteger("workTimer");
	}

}

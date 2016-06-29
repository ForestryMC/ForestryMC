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
package forestry.greenhouse;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import forestry.api.greenhouse.IGreenhouseHelper;
import forestry.api.greenhouse.IGreenhouseLogic;
import forestry.api.greenhouse.IInternalBlock;
import forestry.api.multiblock.IGreenhouseController;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.multiblock.MultiblockRegistry;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.greenhouse.multiblock.InternalBlockCheck;

public class GreenhouseHelper implements IGreenhouseHelper {

	private final List<Class<? extends IGreenhouseLogic>> greenhouseLogics = new ArrayList<>();
	
	@Override
	public IGreenhouseController getGreenhouseController(World world, BlockPos pos) {
		if (MultiblockRegistry.getControllersFromWorld(world) != null) {
			for (IMultiblockControllerInternal controllerInternal : MultiblockRegistry.getControllersFromWorld(world)) {
				if (controllerInternal instanceof IGreenhouseControllerInternal) {
					if (controllerInternal.isAssembled()) {
						if (isPositionInGreenhouse((IGreenhouseControllerInternal) controllerInternal, pos)) {
							return (IGreenhouseController) controllerInternal;
						}
					}
				}
			}
		}
		return null;
	}
	
	private static boolean isPositionInGreenhouse(IGreenhouseControllerInternal controller, BlockPos pos) {
		IInternalBlock checkBlock = new InternalBlockCheck(pos);
		return controller.getInternalBlocks().contains(checkBlock);
	}
	
	@Override
	public void addGreenhouseLogic(Class<? extends IGreenhouseLogic> logic) {
		if(!greenhouseLogics.contains(logic)){
			greenhouseLogics.add(logic);
		}
	}
	
	@Override
	public List<Class<? extends IGreenhouseLogic>> getGreenhouseLogics() {
		return greenhouseLogics;
	}

}

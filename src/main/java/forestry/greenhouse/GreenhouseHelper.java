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

import forestry.api.greenhouse.IGreenhouseHelper;
import forestry.api.greenhouse.IGreenhouseState;
import forestry.api.greenhouse.IInternalBlock;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.multiblock.MultiblockRegistry;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.greenhouse.multiblock.InternalBlockCheck;

public class GreenhouseHelper implements IGreenhouseHelper {

	@Override
	public IGreenhouseState getGreenhouseState(World world, BlockPos pos) {
		if (MultiblockRegistry.getControllersFromWorld(world) != null) {
			for (IMultiblockControllerInternal controllerInternal : MultiblockRegistry.getControllersFromWorld(world)) {
				if (controllerInternal instanceof IGreenhouseControllerInternal) {
					if (controllerInternal.isAssembled()) {
						if (isPositionInGreenhouse((IGreenhouseControllerInternal) controllerInternal, pos)) {
							return ((IGreenhouseControllerInternal) controllerInternal).createState();
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

}

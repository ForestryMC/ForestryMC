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
package forestry.farming.multiblock;

import net.minecraft.world.World;

import forestry.api.multiblock.IMultiblockLogicFarm;
import forestry.core.multiblock.MultiblockLogic;

public class MultiblockLogicFarm extends MultiblockLogic<IFarmControllerInternal> implements IMultiblockLogicFarm {
	public MultiblockLogicFarm() {
		super(IFarmControllerInternal.class);
	}

	@Override
	public IFarmControllerInternal getController() {
		if (super.isConnected()) {
			return controller;
		} else {
			return FakeFarmController.instance;
		}
	}

	public IFarmControllerInternal createNewController(World world) {
		return new FarmController(world);
	}
}

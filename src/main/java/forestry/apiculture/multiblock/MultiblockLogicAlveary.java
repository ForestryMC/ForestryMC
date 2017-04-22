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
package forestry.apiculture.multiblock;

import forestry.api.multiblock.IMultiblockLogicAlveary;
import forestry.core.multiblock.MultiblockLogic;
import net.minecraft.world.World;

public class MultiblockLogicAlveary extends MultiblockLogic<IAlvearyControllerInternal> implements IMultiblockLogicAlveary {
	public MultiblockLogicAlveary() {
		super(IAlvearyControllerInternal.class);
	}

	@Override
	public IAlvearyControllerInternal getController() {
		if (super.isConnected()) {
			return controller;
		} else {
			return FakeAlvearyController.instance;
		}
	}

	@Override
	public IAlvearyControllerInternal createNewController(World world) {
		return new AlvearyController(world);
	}
}

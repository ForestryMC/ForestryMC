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
package forestry.factory.multiblock;

import net.minecraft.world.World;

import forestry.api.multiblock.IMultiblockLogicDistillVat;
import forestry.core.multiblock.MultiblockLogic;

public class MultiblockLogicDistillVat extends MultiblockLogic<IDistillVatControllerInternal> implements IMultiblockLogicDistillVat {
	public MultiblockLogicDistillVat() {
		super(IDistillVatControllerInternal.class);
	}

	@Override
	public IDistillVatControllerInternal getController() {
		if (super.isConnected()) {
			return controller;
		} else {
			return FakeDistillVatController.instance;
		}
	}

	@Override
	public IDistillVatControllerInternal createNewController(World world) {
		return new DistillVatController(world);
	}
}

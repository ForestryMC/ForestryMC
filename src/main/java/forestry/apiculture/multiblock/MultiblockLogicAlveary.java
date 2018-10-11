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

import net.minecraft.world.World;

import forestry.api.climate.IClimateListener;
import forestry.api.multiblock.IMultiblockLogicAlveary;
import forestry.core.multiblock.MultiblockLogic;

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

	@Override
	public void becomeMultiblockSaveDelegate() {
		super.becomeMultiblockSaveDelegate();
		IClimateListener listener = getController().getClimateListener();
		listener.markLocatableDirty();
	}

	@Override
	public void forfeitMultiblockSaveDelegate() {
		super.forfeitMultiblockSaveDelegate();
		IClimateListener listener = getController().getClimateListener();
		listener.markLocatableDirty();
	}
}

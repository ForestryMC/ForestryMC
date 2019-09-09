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
package forestry.apiculture;

import net.minecraft.world.IWorld;

import forestry.api.apiculture.BeeManager;
import forestry.core.ISaveEventHandler;

public class SaveEventHandlerApiculture implements ISaveEventHandler {

	@Override
	public void onWorldLoad(IWorld world) {
		BeeManager.beeRoot.resetBeekeepingMode();
	}

	@Override
	public void onWorldSave(IWorld world) {
	}

	@Override
	public void onWorldUnload(IWorld world) {
	}

}

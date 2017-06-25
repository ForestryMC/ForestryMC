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
package forestry.mail;

import net.minecraft.world.World;

import forestry.core.ISaveEventHandler;

public class SaveEventHandlerMail implements ISaveEventHandler {

	@Override
	public void onWorldLoad(World world) {
		PostRegistry.cachedPostOffice = null;
		PostRegistry.cachedPOBoxes.clear();
		PostRegistry.cachedTradeStations.clear();
	}

	@Override
	public void onWorldSave(World world) {
	}

	@Override
	public void onWorldUnload(World world) {
	}

}

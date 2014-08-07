/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.apiculture;

import net.minecraft.world.World;

import forestry.core.interfaces.ISaveEventHandler;
import forestry.plugins.PluginApiculture;

public class SaveEventHandlerApiculture implements ISaveEventHandler {

	@Override
	public void onWorldLoad(World world) {
		PluginApiculture.beeInterface.resetBeekeepingMode();
	}

	@Override
	public void onWorldSave(World world) {
	}

	@Override
	public void onWorldUnload(World world) {
	}

}

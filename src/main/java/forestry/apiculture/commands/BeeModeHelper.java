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
package forestry.apiculture.commands;

import forestry.api.apiculture.IBeekeepingMode;
import forestry.core.commands.ICommandModeHelper;
import forestry.plugins.PluginApiculture;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.World;

public class BeeModeHelper implements ICommandModeHelper {

	@Override
	public String[] getModeNames() {
		ArrayList<IBeekeepingMode> beekeepingModes = PluginApiculture.beeInterface.getBeekeepingModes();
		int modeStringCount = beekeepingModes.size();
		List<String> modeStrings = new ArrayList<String>(modeStringCount);
		for (IBeekeepingMode mode : beekeepingModes)
			modeStrings.add(mode.getName());

		return modeStrings.toArray(new String[modeStringCount]);
	}

	@Override
	public String getModeNameMatching(String desired) {
		IBeekeepingMode mode = PluginApiculture.beeInterface.getBeekeepingMode(desired);
		if (mode == null)
			return null;
		return mode.getName();
	}

	@Override
	public String getModeName(World world) {
		return PluginApiculture.beeInterface.getBeekeepingMode(world).getName();
	}

	@Override
	public void setMode(World world, String modeName) {
		PluginApiculture.beeInterface.setBeekeepingMode(world, modeName);
	}

	@Override
	public Iterable<String> getDescription(String modeName) {
		IBeekeepingMode mode = PluginApiculture.beeInterface.getBeekeepingMode(modeName);
		return mode.getDescription();
	}

}

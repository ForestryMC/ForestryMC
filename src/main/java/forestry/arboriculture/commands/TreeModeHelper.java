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
package forestry.arboriculture.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.World;

import forestry.api.arboriculture.ITreekeepingMode;
import forestry.core.commands.ICommandModeHelper;
import forestry.plugins.PluginArboriculture;

public class TreeModeHelper implements ICommandModeHelper {

	@Override
	public String[] getModeNames() {
		ArrayList<ITreekeepingMode> treekeepingModes = PluginArboriculture.treeInterface.getTreekeepingModes();
		int modeStringCount = treekeepingModes.size();
		List<String> modeStrings = new ArrayList<String>(modeStringCount);
		for (ITreekeepingMode mode : treekeepingModes)
			modeStrings.add(mode.getName());

		return modeStrings.toArray(new String[modeStringCount]);
	}

	@Override
	public String getModeNameMatching(String desired) {
		ITreekeepingMode mode = PluginArboriculture.treeInterface.getTreekeepingMode(desired);
		if (mode == null)
			return null;
		return mode.getName();
	}

	@Override
	public String getModeName(World world) {
		return PluginArboriculture.treeInterface.getTreekeepingMode(world).getName();
	}

	@Override
	public void setMode(World world, String modeName) {
		PluginArboriculture.treeInterface.setTreekeepingMode(world, modeName);
	}

	@Override
	public ArrayList<String> getDescription(String modeName) {
		ITreekeepingMode mode = PluginArboriculture.treeInterface.getTreekeepingMode(modeName);
		return mode.getDescription();
	}

}

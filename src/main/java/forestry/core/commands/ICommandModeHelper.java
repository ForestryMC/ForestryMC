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
package forestry.core.commands;

import net.minecraft.world.World;

public interface ICommandModeHelper {
	String[] getModeNames();

	String getModeNameMatching(String desired);

	String getModeName(World world);

	void setMode(World world, String modeName);

	Iterable<String> getDescription(String modeName);
}

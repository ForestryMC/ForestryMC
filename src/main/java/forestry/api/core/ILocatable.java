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
package forestry.api.core;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ILocatable {
	BlockPos getCoordinates();

	/**
	 * Must not be named "getWorld" to avoid SpecialSource issue https://github.com/md-5/SpecialSource/issues/12
	 */
	World getWorldObj();
}

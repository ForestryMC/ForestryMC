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
package forestry.apiculture.gui;

import forestry.api.core.IErrorLogicSource;
import forestry.core.owner.IOwnedTile;
import forestry.core.tiles.IClimatised;
import forestry.core.tiles.ITitled;

public interface IGuiBeeHousingDelegate extends ITitled, IErrorLogicSource, IOwnedTile, IClimatised {
	/**
	 * Returns scaled queen health or breeding progress
	 */
	int getHealthScaled(int i);

	String getHintKey();
}

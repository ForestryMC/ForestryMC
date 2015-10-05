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

import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.apiculture.IBeeHousing;
import forestry.core.interfaces.IClimatised;
import forestry.core.interfaces.IRestrictedAccessTile;
import forestry.core.inventory.IInventoryAdapter;

public interface IAlvearyController extends IBeeHousing, IClimatised, IRestrictedAccessTile {
	IInventoryAdapter getInternalInventory();

	Iterable<IAlvearyComponent> getComponents();

	int getHealthScaled(int i);
}

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
package forestry.climatology.items;

import forestry.core.items.ItemRegistry;

public class ItemRegistryClimatology extends ItemRegistry {

	public final ItemHabitatScreen habitatScreen;

	public ItemRegistryClimatology() {
		habitatScreen = registerItem(new ItemHabitatScreen(), "habitat_screen");
	}
}

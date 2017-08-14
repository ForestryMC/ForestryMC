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
package forestry.greenhouse.items;

import forestry.core.items.ItemRegistry;

public class ItemRegistryGreenhouse extends ItemRegistry {

	/* MISC */
	public final ItemGreenhouseScreen greenhouseScreen;

	/* Camouflage */
	public final ItemCamouflageSprayCan camouflageSprayCan;

	public ItemRegistryGreenhouse() {
		// SCREEN
		greenhouseScreen = registerItem(new ItemGreenhouseScreen(), "greenhouse_screen");
		// CAMOUFLAGE
		camouflageSprayCan = registerItem(new ItemCamouflageSprayCan(), "camouflage_spray_can");
	}
}

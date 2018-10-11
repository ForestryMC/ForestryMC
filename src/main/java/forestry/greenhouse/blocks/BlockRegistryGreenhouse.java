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
package forestry.greenhouse.blocks;

import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;

public class BlockRegistryGreenhouse extends BlockRegistry {

	public final BlockGreenhouse greenhouseBlock;
	public final BlockClimatiser climatiserBlock;
	public final BlockGreenhouseWindow window;
	public final BlockGreenhouseWindow roofWindow;

	public BlockRegistryGreenhouse() {
		greenhouseBlock = new BlockGreenhouse();
		registerBlock(greenhouseBlock, new ItemBlockForestry<>(greenhouseBlock), "greenhouse");
		climatiserBlock = new BlockClimatiser();
		registerBlock(climatiserBlock, new ItemBlockForestry<>(climatiserBlock), "climatiser");

		window = new BlockGreenhouseWindow();
		registerBlock(window, new ItemBlockForestry<>(window), "greenhouse.window");

		roofWindow = new BlockGreenhouseWindow();
		registerBlock(roofWindow, new ItemBlockForestry<>(roofWindow), "greenhouse.window_up");
	}

}

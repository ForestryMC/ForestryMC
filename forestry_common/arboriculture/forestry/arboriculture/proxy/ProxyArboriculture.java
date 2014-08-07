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
package forestry.arboriculture.proxy;

import net.minecraft.world.World;

public class ProxyArboriculture {

	public void initializeRendering() {
	}

	public int getFoliageColorBasic() {
		return 4764952;
	}

	public int getFoliageColorBirch() {
		return 8431445;
	}

	public int getFoliageColorPine() {
		return 6396257;
	}

	public int getBiomeFoliageColour(World world, int x, int z) {
		return getFoliageColorBasic();
	}

	public void addLocalizations() {
	}

}

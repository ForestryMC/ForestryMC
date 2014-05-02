/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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

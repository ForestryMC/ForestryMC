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
package forestry.arboriculture.proxy;

import forestry.arboriculture.IWoodTyped;
import net.minecraft.block.Block;

public class ProxyArboriculture {

	public void initializeModels() {
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

	public <T extends Block & IWoodTyped> void registerWoodModel(T woodTyped, boolean withVariants) {
	}
}

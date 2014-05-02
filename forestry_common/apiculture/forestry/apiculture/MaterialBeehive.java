/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MaterialBeehive extends Material {

	public MaterialBeehive(boolean noHarvest) {
		super(MapColor.stoneColor);
		if (noHarvest)
			this.setRequiresTool();
		setImmovableMobility();
	}

}

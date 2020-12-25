/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.apiculture;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class MaterialBeehive {

    //TODO - need AT for several material builder methods
    public static final Material BEEHIVE_WORLD = (new Material.Builder(MaterialColor.STONE)).build();
    public static final Material BEEHIVE_ALVEARY = (new Material.Builder(MaterialColor.STONE)).build();

    private MaterialBeehive(boolean noHarvest) {
        //		super(MaterialColor.STONE);
        //		if (noHarvest) {
        //			this.setRequiresTool();
        //		}
        //		setImmovableMobility();
    }

}

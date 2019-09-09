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
package forestry.lepidopterology.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;

public class MaterialCocoon {

	//TODO constructor ugly, AT builder methods?
	public static final Material INSTANCE = new Material(MaterialColor.WOOL, false, true, true, true, false, true, false, PushReaction.NORMAL);

	//new Material.Builder(MaterialColor.WOOL).flammable().requiresTool();

}
